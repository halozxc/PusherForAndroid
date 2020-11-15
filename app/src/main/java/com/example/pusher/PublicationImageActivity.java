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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Time;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    String imagePath="";
    EditText etImageDescriptionl;
    Button btpublic;
    SharedPreferences spfile;
    String uid ;
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
        etImageDescriptionl =findViewById(R.id.etImageDespription);
        btpublic =findViewById(R.id.btPublic);
        publicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random r =new Random(System.currentTimeMillis());
                imagePath = "output_images"+r.nextInt(1000)+".jpg";
                        File outputImage = new File(getExternalCacheDir(),imagePath);

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
        btpublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri!=null){
                    publicationImage();
                }
                else {
                    Toast.makeText(PublicationImageActivity.this,"还没有添加图片哦",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PublicationImageActivity.this,"申请成功",Toast.LENGTH_SHORT);

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
    private void publicationImage(){
        spfile = getSharedPreferences(getResources().getString(R.string.share_preference_file),MODE_PRIVATE);
        uid = spfile.getString( getString(R.string.user_id),null);
        File outputImage = new File(getExternalCacheDir(),imagePath);

        if(outputImage.exists()){
            Toast.makeText(PublicationImageActivity.this,"正在上传",Toast.LENGTH_SHORT).show();
            OkHttpClient client =new OkHttpClient();
            RequestBody requestBody=RequestBody.create(MediaType.parse("image/jpeg"),new File(getExternalCacheDir(),imagePath));
            MultipartBody body=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title","test")
                    .addFormDataPart("description",String.valueOf(etImageDescriptionl.getText()))
                    .addFormDataPart("pic","uploadimage.jpg",requestBody)
                    .addFormDataPart("goodCount", String.valueOf(0))

                    .addFormDataPart("uid",uid)
                    .build();//图片服务器定义名字，
            OkHttpClient okHttpClient=new OkHttpClient();
            Request request=new Request.Builder().url(getString(R.string.api_uploadpic)).post(body).addHeader("token",token).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
              e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String res = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONObject reponse = new JSONObject(res);
                        Log.d("upload result",reponse.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        else{
            Toast.makeText(this,"还没有添加照片哦",Toast.LENGTH_LONG).show();
        }


    }
}