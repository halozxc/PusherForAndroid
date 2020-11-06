package com.example.pusher;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>{
private List<com.example.pusher.List> galleryItemList;
Activity activity;
    SharedPreferences spfile;
ImageGalleryAdapter(List<com.example.pusher.List> items, Activity activity){
    galleryItemList = items;
    this.activity =activity;
    spfile = activity.getSharedPreferences(activity.getResources().getString(R.string.share_preference_file),MODE_PRIVATE);
}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_gallery_item,parent,false);
        ViewHolder holder = new ViewHolder(view);



       return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
     com.example.pusher.List galleryItem = galleryItemList.get(position);

        holder.favoriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.isfavorite = holder.isfavorite==true ? false:true;
                Glide.with(activity).load(holder.isfavorite? R.mipmap.favorite_fill  : R.mipmap.favorite).into(holder.favoriteImage);
                   changeFavoriteState(holder,position);
            }
        });
        holder.accountText.setText( galleryItem.getNickName());
        holder.descriptionText.setText(galleryItem.getDescription());
        holder.goodcountText.setText(galleryItem.getGoodCount()>0 ?  galleryItem.getGoodCount()+"人感觉很赞" : "");
        Glide.with(activity).load(activity.getString(R.string.api_image_address).toString()+galleryItem.getPicUri()).error(Glide.with(activity).load(R.drawable.ic_default)).into(holder.contentImage);
        Glide.with(activity).load(R.mipmap.favorite).into(holder.favoriteImage);
        syncUserInfo(holder,position);



    }

    @Override
    public int getItemCount() {
        return galleryItemList.size();
    }

    static  class ViewHolder extends RecyclerView.ViewHolder{
         ImageView potraitImage;
         ImageView contentImage;
         TextView accountText;
         ImageView favoriteImage;
         TextView descriptionText;
         TextView goodcountText;
         Boolean isfavorite;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            isfavorite =false;
            potraitImage =(ImageView) itemView.findViewById(R.id.imPortrait);
            contentImage  =(ImageView)itemView.findViewById(R.id.imContent);
            accountText=  (TextView)itemView.findViewById(R.id.tvUsername);
            favoriteImage =(ImageView) itemView.findViewById(R.id.imFavorite);
            descriptionText =(TextView)itemView.findViewById(R.id.tvDesription);
            goodcountText =(TextView)itemView.findViewById(R.id.tvGoodCount);
        }
    }
void syncUserInfo(final ViewHolder holder, int position){
    com.example.pusher.List galleryItem = galleryItemList.get(position);
    final String[] nickname = new String[1];
    OkHttpClient client =new OkHttpClient();
    OkHttpClient client1 =new OkHttpClient();
    MediaType mediaType = MediaType.get("application/json; charset=utf-8");
    Request request = new Request.Builder().url(activity.getString(R.string.api_get_user_info) +galleryItem.getUid()).build();
    Call call = client.newCall(request);
    call.enqueue(new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            final String res = Objects.requireNonNull(response.body()).string();
            String nick = "";
            try{

                JSONObject reponse  = new JSONObject(res);
                if(reponse.getString("msg").equals("个人信息")){
                    JSONObject uinfo = new JSONObject(reponse.getString("data"));
                    nick = uinfo.getString("nickname");
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            final String finalNick = nick;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    holder.accountText.setText(finalNick);
                }
            });

        }
    });

    Request request1 = new Request.Builder().url(activity.getString(R.string.api_getisfavorite) + galleryItem.getPicId() + "/" + spfile.getString("user_id", null)).build();
    Call call1 = client1.newCall(request1);
    call1.enqueue(new Callback() {
    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {

    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        final String res = Objects.requireNonNull(response.body()).string();

        try {
            Log.d("get fav status:",res);
            JSONObject favorite = new JSONObject(res);
            final String is = favorite.getString("data");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    holder.isfavorite = (is.equals(1)) ? true:false;
                    Glide.with(activity).load(is.equals(1) ? R.mipmap.favorite_fill :R.mipmap.favorite).into(holder.favoriteImage);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
});







}
void changeFavoriteState(final ViewHolder holder, final int position){
    com.example.pusher.List galleryItem = galleryItemList.get(position);
    OkHttpClient client =new OkHttpClient();
    MediaType mediaType = MediaType.get("application/json; charset=utf-8");
    JSONObject body =new JSONObject();
    try {
        body.put("picId", galleryItem.getPicId());
        body.put("userId",spfile.getString("user_id","") );
        body.put("status",Byte.valueOf((byte) (holder.isfavorite ? 1:0)));
        Log.d("post body is",body.toString());
    } catch (JSONException e) {
        e.printStackTrace();
    }
    RequestBody requestBody = RequestBody.create(mediaType,body.toString());
    Request request = new Request.Builder().url(activity.getString(R.string.api_setisfavorite)).post(requestBody).build();
    Call call =client.newCall(request);
    call.enqueue(new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            final String res = Objects.requireNonNull(response.body()).string();
            Log.d("tapgood result",res);
            try {
                final JSONObject body = new JSONObject(res);
               final String msg = body.getString("msg");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
             Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
syncUserInfo(holder,position);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    });
}
}
