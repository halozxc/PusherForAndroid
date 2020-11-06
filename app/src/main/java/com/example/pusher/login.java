package com.example.pusher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import javax.xml.transform.Result;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class login extends AppCompatActivity {
    public  static String USER_ACCOUNT;
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
        account = spfile.getString( getString(R.string.user_account),"null");
        password = spfile.getString( getString(R.string.user_password),"null");
        token = spfile.getString( getString(R.string.login_token),null);

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

              if(account.equals("null")){
     account =String.valueOf( etAccount.getText());
 }
             if(password.equals("null")){
     password = String.valueOf(etPassword.getText());
 }
                    loginAction();
                }
                else if (actionstate.equals("注册")){
                 registAction();
                }

            }
        });
    }
    private  void registAction(){
        etPassword.setInputType(InputType.TYPE_NULL);
        OkHttpClient client =new OkHttpClient();
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        Random r =new Random(System.currentTimeMillis());
        JSONObject requestContent =new JSONObject();
        try {
            requestContent.put("username",String.valueOf(etAccount.getText()) );
            requestContent.put("password",String.valueOf(etPassword.getText()));
            requestContent.put("nickname",String.valueOf("new_pusher"+r.nextInt()%100));


        }catch (Exception e){
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(mediaType,requestContent.toString());
        Request request = new Request.Builder().url(getString(R.string.api_register)).post(requestBody).build();
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
                        try {
                            JSONObject reponse = new  JSONObject(res);
                            if(reponse.getString("msg").equals("注册成功")){
                                JSONObject newUser = new JSONObject(reponse.getString("data"));
                                editor.putString("user_account",newUser.getString("username"));
                                editor.putString("user_nickname",newUser.getString("nickname"));
                                editor.putString("user_password",newUser.getString("password"));
                                editor.putString("user_id",newUser.getString("uid"));
                            }

                            Toast.makeText(login.this,reponse.getString("msg"),Toast.LENGTH_SHORT).show();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        btLogin.setText("登录");
                        Log.d("requestresult",res);
                    }
                });
            }
        });
    }
private void loginAction(){
        
    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
          USER_ACCOUNT = account;
          requestContent.put("password",password);

    }catch (Exception e){
        e.printStackTrace();
    }
    RequestBody requestBody = RequestBody.create(mediaType,requestContent.toString());
    final Request request = new Request.Builder().url(getString(R.string.api_login)).post(requestBody).build();

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
                        Log.d("login msg is:",reponse.getString("msg"));
                       if(reponse.getString("msg").equals("登录成功") )
                       {  islogin = true;

                           String data  = reponse.getString("data");
                           Log.d("data is",data);
                           JSONObject token  =new JSONObject(data);
                           editor.putString("login_token",token.getString("token"));
                           editor.putString("user_id", token.getString("uid"));

                           editor.apply();
                           editor.commit();

Log.d("user_id is:",spfile.getString("user_id",""));
                           Intent intent =new Intent();
                           setResult(RESULT_OK);
                           finish();
                       }
                       else{
                          Toast.makeText(login.this,reponse.getString("msg"),Toast.LENGTH_SHORT).show();
                       }
                      btLogin.setClickable(true);
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
