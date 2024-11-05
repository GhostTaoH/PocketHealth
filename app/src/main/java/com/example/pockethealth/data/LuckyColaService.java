package com.example.pockethealth.data;

import com.example.pockethealth.business.EmailRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LuckyColaService {
    @POST("https://luckycola.com.cn/tools/customMail")
    Call<ResponseBody> sendEmail(@Body EmailRequest emailRequest);
}
