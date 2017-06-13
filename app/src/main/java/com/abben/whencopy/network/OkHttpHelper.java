package com.abben.whencopy.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by abben on 2017/5/8.
 */

public class OkHttpHelper {
    private static OkHttpClient okHttpClient;

    /**
     * 连接超时
     */
    private static final int CONNECT_TIMEOUT = 15;
    /**
     * 读取超时
     */
    private static final int READ_TIMEOUT = 25;
    /**
     * 写入超时
     */
    private static final int WRITE_TIMEOUT = 25;

    public static OkHttpClient getOkHttpClient(){
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
//                .addInterceptor(new HeaderInterceptor()) //添加 header
//                .sslSocketFactory(customHttpsTrust.sSLSocketFactory, customHttpsTrust.x509TrustManager)// https 配置
                    .build();
        }

        return okHttpClient;
    }
}
