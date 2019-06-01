package com.example.map.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.example.map.R;


public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText mEmail;
    private Button button;
    private ImageView mBackImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();
    }

    private void init(){
        mBackImage = findViewById(R.id.back_image);

        mBackImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mEmail = findViewById(R.id.forgot_password_edittext);
        button = findViewById(R.id.send_email);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword(mEmail.getText().toString());
            }
        });
    }

    private void resetPassword(String email){
        if (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")){
            Toast.makeText(getApplicationContext(),"邮箱格式有误！",Toast.LENGTH_SHORT).show();
            return;
        }

        AVUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(),"验证成功，请前往邮箱修改密码",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"网络异常，请重试",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
