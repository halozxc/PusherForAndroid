package com.example.pusher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Time;
import java.util.Random;

public class PublicationImageActivity extends AppCompatActivity {
public static final int TAKE_PHOTO =1;
private final int REQUEST_EXTERNAL_STORAGE =1;
    private final String[] PERMISSONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    ImageView publicImage ;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.CAMERA}, 100);
        }
        setContentView(R.layout.activity_publication_image);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

            }

            else {
                requestPermissions(PERMISSONS_STORAGE,REQUEST_EXTERNAL_STORAGE);

            }
        }
        publicImage =findViewById(R.id.ivpublicationContent);
        publicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random r =new Random(System.currentTimeMillis());
                File outputImage = new File(getExternalCacheDir(),"output_images"+r.nextInt(1000)+".jpg");
                try{
                    if(outputImage.exists()){

                        outputImage.delete();
                        Log.d("delete ?","yes");
                    }
                    outputImage.createNewFile();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri = FileProvider.getUriForFile(PublicationImageActivity.this,"com.example.cameraalbumtest.fileprovider",outputImage);
                }else{
                    imageUri =Uri.fromFile(outputImage);
                }



                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);


            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent,TAKE_PHOTO);

                } else {
                    Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       Log.d("imageUri", String.valueOf(resultCode)+"  "+String.valueOf(imageUri));
        switch (requestCode) {
            case TAKE_PHOTO:

                if (resultCode == RESULT_OK) {
                    Glide.with(this).load(imageUri).into(publicImage);
                }
                Glide.with(this).load(imageUri).into(publicImage);
                break;
            default:
                Log.d("get camera","no image captured");
                break;

        }
    }
}