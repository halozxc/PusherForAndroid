package com.example.pusher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


    }
     void CheckLoginState() {

    }

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
