package com.example.map.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.example.map.R;

public class RegisterActivity extends BaseActivity {
    EditText username;
    EditText password;
    EditText checkPassword;
    EditText email;
    Button regisetButton;
    TextInputLayout passwordLayout;
    TextInputLayout checkPasswordLayout;
    ImageView mBackImage;
    String usernameText = "";
    String phoneNumberText = "";
    String passwordText = "";
    String checkPasswordText = "";
    String emailText = "";
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }


    private void init(){
        username = findViewById(R.id.register_username);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        checkPassword = findViewById(R.id.register_checkPassword);
        regisetButton = findViewById(R.id.register_register_button);
        passwordLayout = findViewById(R.id.register_passwordLayout);
        checkPasswordLayout = findViewById( R.id.register_checkPasswordLayout);
        mBackImage = findViewById(R.id.back_image);

        mBackImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameText = String.valueOf(s);
                    if (s.length() < 4)
                        username.setError("用户名不能小于4位字符");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailText = String.valueOf(s);
                if (!emailText.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"))
                    email.setError("邮箱有误");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordText = String.valueOf(s);
                if (passwordText.length() < 6){
                    passwordLayout.setErrorEnabled(true);
                    passwordLayout.setError("密码不能小于6位");
                }
                else
                    passwordLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        checkPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordText = String.valueOf(s);
                if (!checkPasswordText.equals(passwordText)){
                    checkPasswordLayout.setErrorEnabled(true);
                    checkPasswordLayout.setError("两次密码不一样");
                }else
                    checkPasswordLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        regisetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameText = username.getText().toString();
                if (usernameText.length() < 4){
                    Toast.makeText(RegisterActivity.this,"账号名不能小于4位字符",Toast.LENGTH_SHORT).show();
                }else if (!emailText.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")){
                    Toast.makeText(RegisterActivity.this,"验证邮箱格式错误",Toast.LENGTH_SHORT).show();
                }
                else if(passwordText.length() >= 6 && passwordText.equals(checkPasswordText)){
                    register(usernameText,passwordText,emailText);
                }else {
                    Toast.makeText(RegisterActivity.this,"密码有误",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public static Intent newIntent(Context packageContext){
        Intent intent = new Intent(packageContext,RegisterActivity.class);

        return intent;
    }

    private void register(String username,String password,String emailText){
        final AVUser user = new AVUser();// 新建 AVUser 对象实例
        user.setUsername(username);// 设置用户名
        user.setPassword(password);// 设置密码
        user.setEmail(emailText);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    editor = getSharedPreferences("user_info", MODE_PRIVATE).edit();
                    editor.putString("user_id",user.getObjectId());
                    editor.apply();
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else {
                    e.printStackTrace();
                    // 失败的原因可能有多种，常见的是用户名已经存在。
                    Toast.makeText(RegisterActivity.this, "注册失败（用户名已存在或者其他原因）", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
