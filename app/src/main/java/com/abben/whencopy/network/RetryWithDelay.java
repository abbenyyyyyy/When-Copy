package com.abben.whencopy.network;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by abbenyyy on 2017/5/16.
 */

public class RetryWithDelay implements Function<Observable<?extends Throwable>,ObservableSource<?>>{

    private final int maxRetries;
    private final int retryDelaySeconds;
    private int retryCount;

    public RetryWithDelay(int maxRetries, int retryDelaySeconds){
        this.maxRetries = maxRetries;
        this.retryDelaySeconds = retryDelaySeconds;
        retryCount = 0;
    }

    @Override
    public ObservableSource<?> apply(@NonNull Observable<? extends Throwable> observable) throws Exception {
        return observable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {
                if(++retryCount < maxRetries){
                    //订阅没有触发OnNext,重新订阅
                    return Observable.interval(retryDelaySeconds, TimeUnit.SECONDS);
                }
                return Observable.error(throwable);
            }
        });
    }
}
