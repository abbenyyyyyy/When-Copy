package com.abben.whencopy;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.abben.whencopy.view.TopViewController;
import com.google.gson.Gson;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WhenCopyService extends Service implements View.OnClickListener{
    private String text;
    private TopViewController topViewController;
    public final static int SELECT_SEARCH_INDEX = 0;
    public final static int SELECT_TRANSLATION_INDEX = 1;
    public final static int SELECT_INSERTEVENTS_INDEX = 2;

    private Api api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private boolean[] visibilityFlag = {false ,false, false};
    private int visibilityNumble = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new CustomAidlInterface.Stub(){

            @Override
            public void initServiceVisibilityFlag(boolean visibilitySearch, boolean visibilityTranslation, boolean visibilityInsertevents) throws RemoteException {
                visibilityFlag[0] = visibilitySearch;
                visibilityFlag[1] = visibilityTranslation;
                visibilityFlag[2] = visibilityInsertevents;
                int numble = 0;
                for(boolean x : visibilityFlag){
                    if(x){
                        numble ++;
                    }
                }
                visibilityNumble = numble;
            }

            @Override
            public void changeView(int changeVisibityIndex, boolean visibility) throws RemoteException {
                visibilityFlag[changeVisibityIndex] = visibility;
                int numble = 0;
                for(boolean x : visibilityFlag){
                    if(x){
                        numble ++;
                    }
                }
                visibilityNumble = numble;
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        api = new Retrofit.Builder().baseUrl("http://fanyi.youdao.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(Api.class);
        topViewController = new TopViewController(WhenCopyService.this);
        topViewController.updateOnClickListener(this);
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", ""));
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = clipboardManager.getPrimaryClip();
                if ( clipData.getItemCount()==1 && !clipData.getItemAt(0).getText().toString().equals("")
                        && !topViewController.getIsShowing()){
                    text = clipData.getItemAt(0).getText().toString();
                    howToShowSleect(visibilityNumble,visibilityFlag);
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }



    private void howToShowSleect(int visibilityNumble,boolean[] visibilityFlag ){
        switch (visibilityNumble){
            case 1:
                for(int i =0;i<visibilityFlag.length;i++){
                    if(visibilityFlag[i]){
                        switch (i){
                            case SELECT_SEARCH_INDEX:
                                searchByBaidu(text);
                                break;
                            case SELECT_TRANSLATION_INDEX:
                                translation(text);
                                break;
                            case SELECT_INSERTEVENTS_INDEX:
                                insertEvent(text);
                                break;
                        }
                        break;
                    }
                }
                break;
            case 2:
                topViewController.showSelect(visibilityFlag);
                break;
            case 3:
                topViewController.showSelect(visibilityFlag);
                break;
            default:
                break;
        }
    }


    /**启动百度搜索*/
    private void searchByBaidu(String text){
        Uri uri = Uri.parse("http://www.baidu.com/s?wd=" + text);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**插入日历事件*/
    private void insertEvent(String text){
        Intent intent = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE,text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**得到有道翻译的结果并显示*/
    private void translation(String value){
        //范例:http://fanyi.youdao.com/openapi.do?keyfrom=When-Copy&key=870362664&type=data&doctype=<doctype>&version=1.1&q=要翻译的文本
        api.translation("When-Copy","870362664","data","json","1.1",value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try {
                            String result = responseBody.string();
                            Gson gson = new Gson();
                            topViewController.showTranslation(gson.fromJson(result.replace("-",""),TranslationBean.class));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        //请求错误
                        topViewController.removeView();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchSelect:
                searchByBaidu(text);
                topViewController.removeView();
                break;
            case R.id.translationSelect:
                translation(text);
                break;
            case R.id.inserteventsSelect:
                insertEvent(text);
                topViewController.removeView();
                break;
        }
    }

}
