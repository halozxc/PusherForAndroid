package com.example.pusher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ViewUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.MergeAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.stream.HttpUriLoader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

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

public class MainActivity extends AppCompatActivity {
    String token;
    SharedPreferences spfile;
    RecyclerView rvImageGallery ;
    FloatingActionButton publicationNewImage ;
    FloatingActionButton fabLogOut;
    int pageSize ;
    int pageTotalCount=0;//总页数
    int pageNavigationCount=0;//导航页
    int nextPageNum=1;
    private int lastPosition = 0;//位置
    private int lastOffset = 0;//偏移量
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        rvImageGallery = findViewById(R.id.rvImageGallery);
        fabLogOut = findViewById(R.id.fabLogOut);
        spfile = getSharedPreferences(getResources().getString(R.string.share_preference_file),MODE_PRIVATE);
        token = spfile.getString( getString(R.string.login_token),null);

        pageSize = Integer.parseInt(getString(R.integer.gallery_size_per_page));//每一页5张图

        publicationNewImage =findViewById(R.id.fabPublicNewImage);

        if(token==null)
        {
            intentLogin();
        }
        else{
            getImageList();

        }

         loadmenubackground();//bing image everyday
          publicationNewImage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        Intent intent =new Intent(MainActivity.this,PublicationImageActivity.class);
        startActivity(intent);
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
                    lastOffset = topView.getTop(); //获取与该view的顶部的偏移量
                    lastPosition = manager.getPosition(topView);  //得到该View的数组位置
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
                       if(nextPageNum!=0){Toast.makeText(MainActivity.this,"正在拼命加载",Toast.LENGTH_SHORT).show();
                           getImageList();}
                       else if(nextPageNum==0){
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
    fabLogOut.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            intentLogin();
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
private void showlayout(java.util.List imageList ){

        rvImageGallery.setLayoutManager(new LinearLayoutManager(this));
        if(pageTotalCount<2)
        {
            rvImageGallery.setAdapter(new ImageGalleryAdapter(imageList,MainActivity.this));
        }
        else{
            MergeAdapter  mergeAdapter = new MergeAdapter(rvImageGallery.getAdapter(),new ImageGalleryAdapter(imageList,MainActivity.this));
            rvImageGallery.setAdapter(mergeAdapter);
            ((LinearLayoutManager)rvImageGallery.getLayoutManager()) .scrollToPositionWithOffset(lastPosition, lastOffset);
        }


}
    private void getImageList(){
        if(token!=null){
            OkHttpClient client =new OkHttpClient();
            MediaType mediaType = MediaType.get("application/json; charset=utf-8");



            final Request request = new Request.Builder().url(getString(R.string.api_getpic)+"pageNum="+nextPageNum+"&pageSize="+pageSize).addHeader("token",token).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String res = Objects.requireNonNull(response.body()).string();

                    final Gson gson = new Gson();
                   final  com.example.pusher.ImagePage imagePage = gson.fromJson(res,com.example.pusher.ImagePage.class);
                    com.example.pusher.Data pageinfo =imagePage.getData();
                    nextPageNum = pageinfo.getNextPage();
                    Log.d("nextPage:",String.valueOf(nextPageNum));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // Log.d("requestresult",imagePage.getData().getList().toString());
                           Log.d("requestresult",res);
                           if(imagePage.getMsg().equals("token失效，请重新登录"))
                           {intentLogin();
                           return;}
                           else if(imagePage.getMsg().equals("显示成功")){try{
if(nextPageNum!=0){  pageTotalCount++;
    showlayout(imagePage.getData().getList());}

                           }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1://login sucess
                if(resultCode  ==RESULT_OK){
                    Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    spfile = getSharedPreferences(getResources().getString(R.string.share_preference_file),MODE_PRIVATE);
                    token = spfile.getString(getString(R.string.login_token),null);//refresh token
                    getImageList();
                }
                break;
        }
    }

    //

}
