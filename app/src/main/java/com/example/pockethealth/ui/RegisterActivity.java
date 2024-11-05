package com.example.pockethealth.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.pockethealth.R;
import com.example.pockethealth.business.EmailRequest;
import com.example.pockethealth.business.User;
import com.example.pockethealth.business.UserManager;
import com.example.pockethealth.data.LuckyColaService;
import com.example.pockethealth.data.RetrofitClient;
import com.example.pockethealth.security.PasswordEncryptor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ImageView exitBtn;
    private EditText ed_userName,ed_email,ed_password,ed_code;
    private TextView signInTex;
    private Button RegisterBtn,GetCodeBtn;
    private List<User> userList;
    private EncryptedSharedPreferences sharedPreferences;
    private int getcode;
    private ImageView eyes;
    private boolean isPasswordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //初始化
        initView();

        //设置登录文本的点击
        setClickableText(signInTex);

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName=ed_userName.getText().toString();
                String email = ed_email.getText().toString();
                String code=ed_code.getText().toString();
                String hashPassword= PasswordEncryptor.hashPassword(ed_password.getText().toString());//进行加密操作后存储
                userList=new ArrayList<>();

                // 检查用户输入是否为空
                if(userName.isEmpty() || email.isEmpty() || hashPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请完整填写所有字段", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!code.equals(String.valueOf(getcode))) {
                    Toast.makeText(RegisterActivity.this, "验证码错误，请重新输入！", Toast.LENGTH_SHORT).show();
                    return;
                }


                User user=new User(userName,email,hashPassword);
                userList.add(user);
                UserManager userManager=new UserManager(getApplicationContext());
                userManager.addUsersUsingPreparedStatements(userList);

                // 显示注册成功提示
                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();

                //使用EncryptedSharedPreferences加密存储用户数据
                Encrypted();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username",userName);
                editor.putString("email",email);
                editor.putString("password",ed_password.getText().toString());
                editor.apply();

                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        GetCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random=new Random();
                LuckyColaService service= RetrofitClient.getInstance().create(LuckyColaService.class);
                EmailRequest emailRequest=new EmailRequest();
                emailRequest.setColaKey("OZkxfNT9kjw20t1729680656280XECPXpXRyR");//唯一认证ColaKey
                emailRequest.setTomail(ed_email.getText().toString());//目标邮箱地址
                emailRequest.setFromTitle("口袋健康|验证码");//邮件标题
                emailRequest.setSubject("口袋健康|验证码");//邮件主题
                emailRequest.setSmtpCode("rnmpjwrvindgcbej");//邮箱系统授权码
                emailRequest.setSmtpCodeType("qq");//开启授权码对应的授权邮箱
                emailRequest.setSmtpEmail("1839947283@qq.com");
                emailRequest.setTextContent(true);//邮箱内容是否是文本形式
                emailRequest.setContent(String.valueOf(randomCode()));//邮件内容

                service.sendEmail(emailRequest).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            //发送成功
                            try {
                                Log.d("API Response", response.body().string());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            Toast.makeText(RegisterActivity.this, "验证码发送成功！", Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(RegisterActivity.this, "验证码发送失败", Toast.LENGTH_SHORT).show();
                            if (response.errorBody() != null) {
                                try {
                                    Log.e("TAG", "验证码发送失败: " + response.errorBody().string());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        Toast.makeText(RegisterActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

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
    }

    private int randomCode() {
        String str = "0123456789";
        StringBuilder code = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            char c = str.charAt(new Random().nextInt(str.length()));
            code.append(c);
        }
        getcode=Integer.parseInt(code.toString());
        return Integer.parseInt(code.toString());
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
    }

    private void setClickableText(TextView signInTex) {
        String word = "已经注册？请登录";
        SpannableString spannableString=new SpannableString(word);

        ClickableSpan clickableSpan=new ClickableSpan() {
            //设置可点击的文字部分
            @Override
            public void onClick(@NonNull View widget) {
                //Toast.makeText(LoginActivity.this, "d=====(￣▽￣*)b", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.Primary600));
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan,5,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signInTex.setText(spannableString);
        signInTex.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initView() {
        exitBtn=findViewById(R.id.register_exit_btn);
        ed_userName=findViewById(R.id.register_edUserName);
        ed_email=findViewById(R.id.register_edEmail);
        ed_password=findViewById(R.id.register_edPassword);
        ed_code=findViewById(R.id.ed_Code);
        signInTex=findViewById(R.id.register_sign_in);
        RegisterBtn=findViewById(R.id.signUpBtn);
        GetCodeBtn=findViewById(R.id.Get_codeBtn);
        eyes=findViewById(R.id.register_imageViewEye);
    }
}