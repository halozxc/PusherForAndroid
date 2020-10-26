package com.example.pusher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.stream.HttpUriLoader;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        spfile = getSharedPreferences(getResources().getString(R.string.share_preference_file),MODE_PRIVATE);
        token = spfile.getString( getString(R.string.login_token),null);
        intentLogin();
loadmenubackground();

    }
    void loadmenubackground(){
        final ImageView bgImageView = findViewById(R.id.menu_background);
        String requestBingpic ="http:guolin.tech/api/bing_pic";
        OkHttpClient client =new OkHttpClient();





        final Request request = new Request.Builder().url(requestBingpic).addHeader("token",token).build();
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

    private void getImageList(){
        if(token!=null){
            OkHttpClient client =new OkHttpClient();
            MediaType mediaType = MediaType.get("application/json; charset=utf-8");
            JSONObject requestContent =new JSONObject();
            try {
                requestContent.put("pageNum",1);
                requestContent.put("pageSize",15);

            }catch (Exception e){
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(mediaType,requestContent.toString());


            final Request request = new Request.Builder().url("http://101.37.172.244:8080/pic/images?pageNum=1&pageSize=10").addHeader("token",token).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String res = Objects.requireNonNull(response.body()).string();

                    Gson gson = new Gson();
                   final  com.example.pusher.ImagePage imagePage = gson.fromJson(res,com.example.pusher.ImagePage.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.d("requestresult",res);
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
    void registeUserInfo(){

        OkHttpClient client =new OkHttpClient();
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        JSONObject requestContent =new JSONObject();
        try {
            requestContent.put("username","824927872@qq.com");
            requestContent.put("password","hjh123456");
            requestContent.put("nickname","haeye");
        }catch (Exception e){
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(mediaType,requestContent.toString());
        Request request = new Request.Builder().url("http://101.37.172.244:8080/pic/register").post(requestBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("failure","failure");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String res = Objects.requireNonNull(response.body()).string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("requestresult",res);
                    }
                });
            }
        });

    }
}
