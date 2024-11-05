package com.example.pockethealth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.pockethealth.R;
import com.example.pockethealth.business.UserManager;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class LoginActivity extends AppCompatActivity {

    private EditText ed_email,ed_password;
    private TextView forgetPwd,signUpTex;
    private Button LoginBtn;
    private EncryptedSharedPreferences sharedPreferences;
    private ImageView eyes;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //初始化
        initView();

        //设置注册文本的点击
        setClickableText(signUpTex);

        //查看是否缓存区是否有数据，有就填充账号和密码
        Encrypted();

        eyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 切换密码可见性
                if (isPasswordVisible) {
                    ed_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyes.setImageResource(R.drawable.login_eye_pic);
                } else {
                    ed_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyes.setImageResource(R.drawable.login_eye_visibility_pic);
                }
                // 重新请求焦点以更新显示
                ed_password.setSelection(ed_password.length());
                isPasswordVisible = !isPasswordVisible;
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=ed_email.getText().toString();
                String password=ed_password.getText().toString();

                UserManager userManager=new UserManager(getApplicationContext());

                if (userManager.loginVerification(username,password)){
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void Encrypted() {
        KeyGenParameterSpec aes256GcmSpec = MasterKeys.AES256_GCM_SPEC;
        String masterKeyAlias= "";
        try {
            masterKeyAlias = MasterKeys.getOrCreate(aes256GcmSpec);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        try {
            sharedPreferences= (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    "UserPref",
                    masterKeyAlias,
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        //不自动输入密码
        String savedEmail = sharedPreferences.getString("email", "");
        ed_email.setText(savedEmail);
    }

    private void setClickableText(TextView text) {
        String word = "还没有账号？请注册";
        SpannableString spannableString=new SpannableString(word);

        ClickableSpan clickableSpan=new ClickableSpan() {
            //设置可点击的文字部分
            @Override
            public void onClick(@NonNull View widget) {
                //Toast.makeText(LoginActivity.this, "d=====(￣▽￣*)b", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.Primary600));
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan,6,9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setText(spannableString);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initView() {
        ed_email=findViewById(R.id.login_edEmail);
        ed_password=findViewById(R.id.login_edPassword);
        forgetPwd=findViewById(R.id.forgetPassword);
        signUpTex=findViewById(R.id.sign_up);
        eyes=findViewById(R.id.login_imageViewEye);
        LoginBtn=findViewById(R.id.signInBtn);
    }
}