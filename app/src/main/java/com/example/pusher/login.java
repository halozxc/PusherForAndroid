package com.example.pusher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import javax.xml.transform.Result;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class login extends AppCompatActivity {
    private static String servicesURL = "http://101.37.172.244:8080";
    Boolean islogin = false;
    String account ;
    String password;
    String token;
    SharedPreferences spfile;
    SharedPreferences.Editor editor;
    Button btLogin;
    EditText etAccount;
    EditText etPassword;
    TextView tvRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences.Editor editor;
        setContentView(R.layout.activity_welcome);
         spfile = getSharedPreferences(getResources().getString(R.string.share_preference_file),MODE_PRIVATE);
         etAccount = findViewById(R.id.etAccount);
         btLogin = findViewById(R.id.btLogin);
         etPassword = findViewById(R.id.etPassword);
         tvRegister = findViewById(R.id.tvRegister);
        account = spfile.getString( getString(R.string.user_account),null);
        password = spfile.getString( getString(R.string.user_password),null);
        token = spfile.getString( getString(R.string.login_token),null);
        etAccount.setText(account);
        etPassword.setText(password);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btLogin.setText("注册");
            }
        });
        btLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
//                editor.putString(getString(R.string.user_account), String.valueOf(etAccount.getText()));
//                editor.putString(getString(R.string.user_password), String.valueOf(etPassword.getText()));
//                editor.apply();
//                editor.commit();
               String actionstate = String.valueOf(btLogin.getText()) ;
                if(actionstate.equals("登录")){


                    loginAction();
                }
                else if (actionstate.equals("注册")){
                 registAction();
                }

            }
        });
    }
    private  void registAction(){

        OkHttpClient client =new OkHttpClient();
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        JSONObject requestContent =new JSONObject();
        try {
            requestContent.put("username",etAccount.getText());
            requestContent.put("password",etPassword.getText());
            requestContent.put("nickname","new_pusher");
        }catch (Exception e){
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(mediaType,requestContent.toString());
        Request request = new Request.Builder().url("http://101.37.172.244:8080/pic/register").build();
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
private void loginAction(){
    Toast.makeText(login.this,"logining",Toast.LENGTH_SHORT).show();//tips
        btLogin.setClickable(false);//避免重复请求
    OkHttpClient client =new OkHttpClient();
    MediaType mediaType = MediaType.get("application/json; charset=utf-8");

    JSONObject requestContent =new JSONObject();
    try {
//        requestContent.put("username","824927872@qq.com");
//        requestContent.put("password","hjh123456");
//        requestContent.put("nickname","haeye");
          requestContent.put("username",account);
          requestContent.put("password",password);

    }catch (Exception e){
        e.printStackTrace();
    }
    RequestBody requestBody = RequestBody.create(mediaType,requestContent.toString());
    final Request request = new Request.Builder().url(servicesURL+"/pic/login").post(requestBody).build();

    Call call = client.newCall(request);
    call.enqueue(new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Log.d("failure","failure");
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
            final String res = Objects.requireNonNull(response.body()).string();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btLogin.setClickable(true);
                    Log.d("requestresult",res);
                   editor = spfile.edit();
                    editor.putString(getString(R.string.user_account), String.valueOf(etAccount.getText()));
                    editor.putString(getString(R.string.user_password), String.valueOf(etPassword.getText()));
                    editor.apply();
                    editor.commit();
                    try {
                        JSONObject reponse  = new JSONObject(res);
                        Log.d("msg is:",reponse.getString("msg"));
                       if(reponse.getString("msg").equals("登录成功") )
                       {  islogin = true;

                           String data  = reponse.getString("data");
                           Log.d("data is",data);
                           JSONObject token  =new JSONObject(data);
                           editor.putString("login_token",token.getString("token"));
                           editor.apply();
                           editor.commit();
                           Log.d("token is:",token.getString("token"));
                           Intent intent =new Intent();
                           setResult(RESULT_OK);
                           finish();
                       }
                       else{
                          Toast.makeText(login.this,reponse.getString("msg"),Toast.LENGTH_SHORT).show();
                       }

                    } catch (JSONException e) {
                        Toast.makeText(login.this,"oops,登录错误了",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            });
        }
    });
}

}
