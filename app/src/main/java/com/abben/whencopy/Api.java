package com.abben.whencopy;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Shaolin on 2017/6/12.
 */

public interface Api {
    @GET("openapi.do")
    Observable<ResponseBody> translation(@Query("keyfrom") String keyfrom, @Query("key") String key,
                                         @Query("type") String type, @Query("doctype") String doctype,
                                         @Query("version") String version, @Query("q") String q
                                         );
}
