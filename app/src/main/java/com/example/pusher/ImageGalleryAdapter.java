package com.example.pusher;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageGalleryAdapter extends RecyclerView.ViewHolder{
private List<GalleryItem> galleryItemList;
    public ImageGalleryAdapter(@NonNull View itemView) {
        super(itemView);

    }
}
class GalleryItem{
    int imageItemNUm;
    boolean isLike;
    String userName;
    String imageURl;
    String userPortraitURL;

}