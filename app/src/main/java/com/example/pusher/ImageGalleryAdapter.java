package com.example.pusher;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     com.example.pusher.List galleryItem = galleryItemList.get(position);
     holder.accountText.setText( galleryItem.getDescription());

     Glide.with(activity).load("http://101.37.172.244:8089/test/"+galleryItem.getPicUri()).into(holder.contentImage);
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            potraitImage =(ImageView) itemView.findViewById(R.id.imPortrait);
            contentImage  =(ImageView)itemView.findViewById(R.id.imContent);
            accountText=  (TextView)itemView.findViewById(R.id.tvUsername);
            favoriteImage =(ImageView) itemView.findViewById(R.id.imFavorite);

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