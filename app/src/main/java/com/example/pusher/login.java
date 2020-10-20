package com.example.pusher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity {
    Boolean islogin = false;
    String account ;
    String password;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        SharedPreferences spfile = getSharedPreferences(getResources().getString(R.string.share_preference_file),MODE_PRIVATE);

        final EditText etAccount = findViewById(R.id.etAccount);
        Button btLogin = findViewById(R.id.btLogin);
        final EditText etPassword = findViewById(R.id.etPassword);
       final SharedPreferences.Editor editor = spfile.edit();
        account = spfile.getString( getString(R.string.user_account),null);
        password = spfile.getString( getString(R.string.user_password),null);
        token = spfile.getString( getString(R.string.login_token),null);
        etAccount.setText(account);
        etPassword.setText(password);
        btLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                editor.putString(getString(R.string.user_account), String.valueOf(etAccount.getText()));
                editor.putString(getString(R.string.user_password), String.valueOf(etPassword.getText()));
               editor.apply();
                editor.commit();
            }
        });
    }
//    private String getSpStringValue(String string){
//
//    }


}
