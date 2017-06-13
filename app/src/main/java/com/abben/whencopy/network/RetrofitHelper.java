package com.abben.whencopy.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by abben on 2017/5/8.
 */

public class RetrofitHelper {
    private static final String baseUrl = "http://api.fir.im/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofit(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(OkHttpHelper.getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static String getBaseUrl(){
        return baseUrl;
    }
}
