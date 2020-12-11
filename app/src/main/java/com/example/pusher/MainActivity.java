    package com.example.pusher;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.Toolbar;
    import androidx.appcompat.widget.ViewUtils;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.MergeAdapter;
    import androidx.recyclerview.widget.RecyclerView;

    import android.animation.Animator;
    import android.animation.ValueAnimator;
    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Debug;
    import android.util.Log;
    import android.view.View;
    import android.view.animation.AccelerateDecelerateInterpolator;
    import android.view.animation.LinearInterpolator;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.bumptech.glide.Glide;
    import com.bumptech.glide.load.model.stream.HttpUriLoader;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.gson.Gson;

    import org.jetbrains.annotations.NotNull;
    import org.json.JSONObject;
    import org.w3c.dom.Text;

    import java.io.IOException;
    import java.util.Objects;


    import okhttp3.Call;
    import okhttp3.Callback;
    import okhttp3.FormBody;
    import okhttp3.MediaType;
    import okhttp3.OkHttp;
    import okhttp3.OkHttpClient;
    import okhttp3.Request;
    import okhttp3.RequestBody;
    import okhttp3.Response;
    import okio.BufferedSink;

    public class  MainActivity extends AppCompatActivity {
        class RecycleViewContentNavigation{
            int pageSize =0;
            int pageTotalCount=0;//总页数
            int lastPosition = 0;//位置
            int lastOffset = 0;//偏移量
            int nextPageNum=1;
            MergeAdapter adapter;
            String url;
            RecycleViewContentNavigation(int pagesize,int pageTotalCount,int nextPageNum,String url){

                this.pageSize =pagesize;
                this.pageTotalCount =pageTotalCount;
                this.nextPageNum =nextPageNum;
                this.url =url;
            }
            private void clear(){

                pageTotalCount=0;//总页数
                 lastPosition = 0;//位置
                 lastOffset = 0;//偏移量
                 nextPageNum=1;
                adapter=null;
            }
        }

   RecycleViewContentNavigation selectedNavigation;
        RecycleViewContentNavigation exploreNavigation;
        RecycleViewContentNavigation collectionNavigation;
        private int selectedPage[] ={1,0,0};
        String token;
        SharedPreferences spfile;
        RecyclerView rvImageGallery ;
        FloatingActionButton publicationNewImage ;
        FloatingActionButton fabLogOut;
        View llExplore;
        View llPublish;
        View llCollection;
        View llVipCenter;
        View llmall;
        TextView tvExplore;
        TextView tvPublish;
        TextView tvaccountTitle;
        TextView tvnickTitle;
        TextView tvColection;
        ImageView iveditnick;
        Toolbar tbTitle;
        private float selectedPos=0.25f;//别问我为什么是0.25，我不想解释
        private float targetPos = 0.25f;
        private tabBarShape tbsNavigationItem;

        @SuppressLint("ResourceType")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
            rvImageGallery = findViewById(R.id.rvImageGallery);
            llExplore =findViewById(R.id.llExplore);
            llPublish =findViewById(R.id.llPublication);
            llCollection =findViewById(R.id.llCollection);
            llVipCenter = findViewById(R.id.llvipcenter);
            llmall = findViewById(R.id.llmall);
            tvExplore = findViewById(R.id.tvExplore);
            tvPublish =findViewById(R.id.tvPublication);
            tvColection =findViewById(R.id.tvCollection);
            fabLogOut = findViewById(R.id.fabLogOut);
            tbTitle =findViewById(R.id.tbTitle);
            tvaccountTitle =findViewById(R.id.tvaccounttitle);
            tvnickTitle =findViewById(R.id.tvnicktitle);
            tbsNavigationItem =findViewById(R.id.tbsNavigationItem);
            iveditnick =findViewById(R.id.iveditnick);
            spfile = getSharedPreferences(getResources().getString(R.string.share_preference_file),MODE_PRIVATE);
            token = spfile.getString( getString(R.string.login_token),null);
            selectedNavigation = exploreNavigation = new RecycleViewContentNavigation(Integer.parseInt(getString(R.integer.gallery_size_per_page)),0,1,getString(R.string.api_getpic));
            //collectionNavigation =new RecycleViewContentNavigation(Integer.parseInt(getString(R.integer.gallery_size_per_page)),0,1,getString(R.string.api_getimageLiked)+spfile.getString("user_id","")+"?");

            //pageSize = Integer.parseInt(getString(R.integer.gallery_size_per_page));//每一页5张图
            tvnickTitle.setText(spfile.getString("user_nickname",""));
            tvaccountTitle.setText(spfile.getString("user_account",""));
            publicationNewImage = findViewById(R.id.fabPublicNewImage);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            initNavigationBar();
            if(token==null)
            {
                intentLogin();
            }
            else{
                Log.d("user id is:",spfile.getString("user_id",""));
                collectionNavigation =new RecycleViewContentNavigation(Integer.parseInt(getString(R.integer.gallery_size_per_page)),0,1,getString(R.string.api_getimageLiked)+spfile.getString("user_id","")+"?");
                getImageList(selectedNavigation);

            }

             loadmenubackground();//bing image everyday
              publicationNewImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent =new Intent(MainActivity.this,PublicationImageActivity.class);
            startActivityForResult(intent,2);
        }
    });
            rvImageGallery.addOnScrollListener(new RecyclerView.OnScrollListener() {
                boolean isSlidingToLast = false;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    //设置什么布局管理器,就获取什么的布局管理器

                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    try{ View topView = manager.getChildAt(0); //获取可视的第一个view
                       selectedNavigation.lastOffset = topView.getTop(); //获取与该view的顶部的偏移量
                        selectedNavigation.lastPosition = manager.getPosition(topView);  //得到该View的数组位置
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    // 当停止滑动时
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        //获取最后一个完全显示的ItemPosition ,角标值
                        int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                        //所有条目,数量值
                        int totalItemCount = manager.getItemCount();

                        // 判断是否滚动到底部，并且是向右滚动
                        if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                            //加载更多功能的代码
                           if(selectedNavigation.nextPageNum!=0){Toast.makeText(MainActivity.this,"正在拼命加载",Toast.LENGTH_SHORT).show();
                               getImageList(selectedNavigation);}
                           else if(selectedNavigation.nextPageNum==0){
                               Toast.makeText(MainActivity.this,"精彩见底了，返回刷新试试吧",Toast.LENGTH_SHORT).show();
                           }

                        }
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Log.d("scrolling","dy");
                    if (dy > 0) {
                        isSlidingToLast = true;
                    } else {
                        isSlidingToLast = false;
                    }
                }
            });
            iveditnick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInputDialog(tvnickTitle);

                }
            });
        fabLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentLogin();
            }
        });
        llExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNavigation = exploreNavigation;
                if(selectedNavigation.pageTotalCount==0){
                    getImageList(selectedNavigation);
                }
                else
                {
                    showlayout(selectedNavigation);
                }

                highlightNavigationBarItem(0,llExplore);
            }
        });
        llCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedNavigation =collectionNavigation;
                if(selectedNavigation.pageTotalCount==0){
                    getImageList(collectionNavigation);
                }
                else {
                    showlayout(selectedNavigation);
                }

                highlightNavigationBarItem(2,llCollection);
            }
        });
        llPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                highlightNavigationBarItem(1,llPublish);
            }
        });
        tbTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rvImageGallery.smoothScrollToPosition(0);
                selectedNavigation.clear();
                getImageList(selectedNavigation);
            }
        });
        llmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,MallActivity.class);
                startActivity(intent);
            }
        });
        llVipCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,vipactivity.class);
                startActivity(intent);
            }
        });

        }

        void loadmenubackground(){
            final ImageView bgImageView = findViewById(R.id.menu_background);
            String requestBingpic ="http:guolin.tech/api/bing_pic";
            OkHttpClient client =new OkHttpClient();





            final Request request = new Request.Builder().url(requestBingpic).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String res = Objects.requireNonNull(response.body()).string();



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(MainActivity.this).load(res.toString()).into(bgImageView);
                            Log.d("BingImageURL",res);
                        }
                    });
                }
            });

        }
private void showlayout(RecycleViewContentNavigation recycleViewContentNavigation){
           if(!rvImageGallery.getAdapter().equals(recycleViewContentNavigation))
           {
               rvImageGallery.setAdapter(recycleViewContentNavigation.adapter);
               ((LinearLayoutManager)rvImageGallery.getLayoutManager()) .scrollToPositionWithOffset(recycleViewContentNavigation.lastPosition, recycleViewContentNavigation.lastOffset);
           }


}
        private void showlayout(java.util.List imageList,RecycleViewContentNavigation recycleViewContentNavigation ){


            if(recycleViewContentNavigation.pageTotalCount<2)
            {
                rvImageGallery.setLayoutManager(new LinearLayoutManager(this));
                recycleViewContentNavigation.adapter = new MergeAdapter(new ImageGalleryAdapter(imageList,MainActivity.this));
                rvImageGallery.setAdapter(recycleViewContentNavigation.adapter);
            }
            else{
                MergeAdapter  mergeAdapter = new MergeAdapter(recycleViewContentNavigation.adapter,new ImageGalleryAdapter(imageList,MainActivity.this));
                recycleViewContentNavigation.adapter = mergeAdapter;
                rvImageGallery.setAdapter(mergeAdapter);
                ((LinearLayoutManager)rvImageGallery.getLayoutManager()) .scrollToPositionWithOffset(recycleViewContentNavigation.lastPosition, recycleViewContentNavigation.lastOffset);

            }


        }

        private void getImageList(final RecycleViewContentNavigation recycleViewContentNavigation){
            if(token!=null){
                OkHttpClient client =new OkHttpClient();
               // MediaType mediaType = MediaType.get("application/json; charset=utf-8");
                final Request request = new Request.Builder().url(recycleViewContentNavigation.url+"pageNum="+recycleViewContentNavigation.nextPageNum+"&pageSize="+recycleViewContentNavigation.pageSize).addHeader("token",token).build();
                Log.d("url is",recycleViewContentNavigation.url+"pageNum="+recycleViewContentNavigation.nextPageNum+"&pageSize="+recycleViewContentNavigation.pageSize);
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        final String res = Objects.requireNonNull(response.body()).string();
                        Log.d("image list is:",res);
                        final Gson gson = new Gson();

                        final  com.example.pusher.ImagePage imagePage = gson.fromJson(res,com.example.pusher.ImagePage.class);
                        final com.example.pusher.Data pageinfo = imagePage.getData();
                        Log.d("nextPage:",String.valueOf(recycleViewContentNavigation.nextPageNum));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Log.d("requestresult",imagePage.getData().getList().toString());
                                Log.d("requestresult",res);
                                if(imagePage.getMsg().equals("token失效，请重新登录"))
                                {
                                    intentLogin();
                                    return;
                                }
                                else if(imagePage.getMsg().equals("显示成功"))
                                {
                                    recycleViewContentNavigation.nextPageNum = pageinfo.getNextPage();
                                    try{

                                            recycleViewContentNavigation.pageTotalCount++;
                                            showlayout(imagePage.getData().getList(),recycleViewContentNavigation);}


                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }}



                            }
                        });
                    }
                });

            }

        }
        private void intentLogin(){

            Intent intent =new Intent(MainActivity.this,login.class);

            startActivityForResult(intent,1);


        }

        @SuppressLint("ResourceType")
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode){
                case 1://login sucess
                    if(resultCode  ==RESULT_OK){
                        Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        spfile = getSharedPreferences(getResources().getString(R.string.share_preference_file),MODE_PRIVATE);
                        token = spfile.getString(getString(R.string.login_token),null);//refresh token
                        collectionNavigation =new RecycleViewContentNavigation(Integer.parseInt(getString(R.integer.gallery_size_per_page)),0,1,getString(R.string.api_getimageLiked)+spfile.getString("user_id","")+"?");
                        Log.d("now user_id is",spfile.getString("user_id",""));
                        getImageList(selectedNavigation);

                    }
                    break;
                case 2://publicimage
                    if(resultCode == RESULT_OK){
                        Toast.makeText(MainActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                        exploreNavigation.clear();
                        selectedNavigation=exploreNavigation;
                        getImageList(selectedNavigation);
                    }
            }
        }
        public void highlightNavigationBarItem(int item,View selectedView){
       View animatorview =null;
       TextView text=null;
    targetPos = (item+1)*0.25f;
            if(selectedPage[item]==1){
                navigationBounceAnimation(selectedView);
                return;
            }
            else{
                for(int i=0;i<selectedPage.length;i++){
                if(selectedPage[i]==1){
                    selectedPage[i]=0;
                    switch (i){
                        case 0:
                            animatorview =  llExplore;
                           text=tvExplore;
                          tvExplore.setText("发现");
                            break;


                        case 1:

                            animatorview = llPublish;

                            tvPublish.setText("发布");

                            break;


                        case 2:
                            animatorview = llCollection;

                            tvColection.setText("收藏");
                            break;

                    }
                }

                }

                selectedPage[item]=1;
            unHighlightNavigetionAnimation(animatorview);
                if (llExplore.equals(selectedView)) {
                    text = tvExplore;
                } else if (llPublish.equals(selectedView)) {
                    text = tvPublish;
                } else if (llCollection.equals(selectedView)) {
                    text = tvColection;
                }
            highlightNavigationItemAnimation(selectedView,text);
            }

    }
        public void highlightNavigationItemAnimation(View view, TextView text){
         text.setText(null);
        final View animationView = view;
        ValueAnimator animator = ValueAnimator.ofFloat(0,-50);
        final View finalAnimatorview = view;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationView.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
        if(view==llPublish){
            showPublicationButton(publicationNewImage);
        }
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        ValueAnimator animator1 = ValueAnimator.ofFloat(selectedPos,targetPos);


        animator1.setInterpolator(new AccelerateDecelerateInterpolator());
        animator1.setDuration(200);//播放时长
        animator1.setCurrentFraction(0.01f);
        animator1.setRepeatCount(0);//重放次数
        animator1.setRepeatMode(ValueAnimator.REVERSE);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取改变后的值

                tbsNavigationItem.rpos  =  (float) animation.getAnimatedValue();
                tbsNavigationItem.postInvalidate();
            }
        });
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
    selectedPos =targetPos;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator1.start();


    }
        public void unHighlightNavigetionAnimation(View view){
        final View animationView = view;
        ValueAnimator animator = ValueAnimator.ofFloat(-50,0);
        final View finalAnimatorview = view;
            if(view==llPublish){
                hidePublicationButton(publicationNewImage);
            }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationView.setTranslationY((Float) animation.getAnimatedValue());
            }
        });

        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }
    public void navigationBounceAnimation(View view){
        final View animationView = view;

        ValueAnimator animator = ValueAnimator.ofFloat(-50,-150,-50);
        final View finalAnimatorview = view;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationView.setTranslationY((Float) animation.getAnimatedValue());
               // animationView.setRotation(360*Math.abs((Float) animation.getAnimatedValue())/50);
            }
        });

        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }
        public void initNavigationBar(){
         highlightNavigationItemAnimation(llExplore,tvExplore);//默认选中左边第一个
            hidePublicationButton(publicationNewImage );
        }
        public void  showPublicationButton(View view){
            final View animationView = view;
            ValueAnimator animator = ValueAnimator.ofFloat(0,1);
            final View finalAnimatorview = view;
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    animationView.setScaleX((Float) animation.getAnimatedValue());
                    animationView.setScaleY((Float) animation.getAnimatedValue());
                }
            });

            animator.setDuration(200);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        }
        public void  hidePublicationButton(View view){
            final View animationView = view;
            ValueAnimator animator = ValueAnimator.ofFloat(1,0);
            final View finalAnimatorview = view;
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    animationView.setScaleX((Float) animation.getAnimatedValue());
                    animationView.setScaleY((Float) animation.getAnimatedValue());
                }
            });

            animator.setDuration(200);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        }
        private void showInputDialog(final View bindingView) {
            /*@setView 装入一个EditView
             */
            final EditText editText = new EditText(MainActivity.this);
            try{
                editText.setText(((TextView)bindingView).getText());
            }
            catch (Exception e){
                editText.setText("");
            }
            AlertDialog.Builder inputDialog =
                    new AlertDialog.Builder(MainActivity.this);
            inputDialog.setTitle("你的昵称" ).setView(editText);
            inputDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this,
                                    editText.getText().toString(),
                                    Toast.LENGTH_SHORT).show();
                        if(!editText.getText().equals(((TextView)bindingView).getText())){
                            ((TextView)bindingView).setText(editText.getText());
                            SharedPreferences.Editor edit = spfile.edit();
                            edit.putString("user_nickname",editText.getText().toString());
                            edit.apply();
                            edit.commit();
                        }
                        }
                    }).show();
        }

    }
