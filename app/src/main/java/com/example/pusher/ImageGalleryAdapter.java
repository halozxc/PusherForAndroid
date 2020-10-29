package com.example.pusher;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.jetbrains.annotations.NotNull;
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

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>{
private List<com.example.pusher.List> galleryItemList;
Activity activity;
ImageGalleryAdapter(List<com.example.pusher.List> items, Activity activity){
    galleryItemList = items;
    this.activity =activity;
}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_gallery_item,parent,false);
        ViewHolder holder = new ViewHolder(view);


       return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
     com.example.pusher.List galleryItem = galleryItemList.get(position);
        final String[] nickname = new String[1];
        OkHttpClient client =new OkHttpClient();
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        Request request = new Request.Builder().url("http://101.37.172.244:8080/pic/admin/"+galleryItem.getUid()).build();
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

     holder.accountText.setText( galleryItem.getNickName());
     holder.descriptionText.setText(galleryItem.getDescription());

     Glide.with(activity).load("http://101.37.172.244:8099/zxm/"+galleryItem.getPicUri()).error(Glide.with(activity).load(R.drawable.ic_default)).into(holder.contentImage);
     Glide.with(activity).load(galleryItem.getGoodCount() >0 ? R.mipmap.favorite_fill :R.mipmap.favorite).into(holder.favoriteImage);

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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            potraitImage =(ImageView) itemView.findViewById(R.id.imPortrait);
            contentImage  =(ImageView)itemView.findViewById(R.id.imContent);
            accountText=  (TextView)itemView.findViewById(R.id.tvUsername);
            favoriteImage =(ImageView) itemView.findViewById(R.id.imFavorite);
            descriptionText =(TextView)itemView.findViewById(R.id.tvDesription);

        }
    }
}
//class GalleryItem{
//    int imageItemNUm;
//    boolean isfavorite;
//    String userName;
//    String imageURl;
//    String userPortraitURL;
//
//
//}