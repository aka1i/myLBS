package com.example.map.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.example.map.R;
import com.example.map.SPStr;

public class LoginActivity extends AppCompatActivity {
    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;
    private Button mRegisterButton;
    private TextView mForgotPassword;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){
        mUsername = findViewById(R.id.login_user_name);
        mPassword = findViewById(R.id.login_password);
        mRegisterButton = findViewById(R.id.login_register);
        mLoginButton = findViewById(R.id.login_btn);
        mForgotPassword = findViewById(R.id.login_forgot_password);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsername.getText().toString();
                final String password = mPassword.getText().toString();
                if (username.length() < 4){
                    Toast.makeText(LoginActivity.this, "用户名不能小于4位", Toast.LENGTH_SHORT).show();
                }else if (password.length() == 0){
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();

                }else{
                    AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
                        @Override
                        public void done(AVUser avUser, AVException e) {
                            if (e == null) {
                                editor = getSharedPreferences("user_info", MODE_PRIVATE).edit();
                                editor.putString("user_id",avUser.getObjectId());
                                editor.putString("user_name",username);
                                editor.putString(SPStr.HEAD_IMG,avUser.getAVFile("pic").getUrl());
                                editor.commit();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                Toast.makeText(LoginActivity.this, "脑电波接入成功~~~", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "登入失败（可能账户名或密码错误）", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}
