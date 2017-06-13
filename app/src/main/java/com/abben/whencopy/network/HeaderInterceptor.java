package com.abben.whencopy.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by abben on 2017/5/8.
 */
public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request compressedRequest = request.newBuilder()
                .header("cookie", "")
                .header("User-Agent", "")
                .build();
        return chain.proceed(compressedRequest);
    }
}
