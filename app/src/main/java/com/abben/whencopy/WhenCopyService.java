package com.abben.whencopy;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.abben.whencopy.view.TopViewController;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WhenCopyService extends Service implements View.OnClickListener{
    private String text;
    private TopViewController topViewController;
    public final static int SELECT_SEARCH_INDEX = 0;
    public final static int SELECT_TRANSLATION_INDEX = 1;
    public final static int SELECT_INSERTEVENTS_INDEX = 2;

    private boolean[] visibilityFlag = {false ,false, false};
    private int visibilityNumble = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            MyReceiver myReceiver = new MyReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.ACTION_CHANGE_VISIBITY);
            filter.addAction(Constant.ACTION_INIT);
            registerReceiver(myReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Constant.ACTION_INIT.equals(intent.getAction())){
                boolean[] initVisibityFlag = intent.getBooleanArrayExtra("initVisibity");
                System.arraycopy(initVisibityFlag,0,visibilityFlag,0,initVisibityFlag.length);
                int numble = 0;
                for(boolean x : visibilityFlag){
                    if(x){
                        numble ++;
                    }
                }
                visibilityNumble = numble;
            }else if(Constant.ACTION_CHANGE_VISIBITY.equals(intent.getAction())){
                int changeVisibityIndex = intent.getIntExtra("changeVisibityIndex",-1);
                boolean changeVisibity = intent.getBooleanExtra("changeVisibity",true);
                if(changeVisibityIndex != -1) {
                    notifySelectViewVisbility(changeVisibityIndex,changeVisibity);
                }
            }

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        return super.onStartCommand(intent, flags, startId);
    }

    private void notifySelectViewVisbility(int changeVisibityIndex , boolean visibility){

        this.visibilityFlag[changeVisibityIndex] = visibility;
        int numble = 0;
        for(boolean x : visibilityFlag){
            if(x){
                numble ++;
            }
        }
        visibilityNumble = numble;

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
                                new TranslationAsy().execute(text);
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

    class TranslationAsy extends AsyncTask<String,Void,TranslationBean>{

        @Override
        protected void onPostExecute(TranslationBean translationBean2) {
            if(translationBean2!=null){
                TranslationBean translationBean = translationBean2;
                topViewController.showTranslation(translationBean);
            }
        }

        @Override
        protected TranslationBean doInBackground(String... params) {
            String needTranslationText = params[0];
            return translation(needTranslationText);
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

    /**得到有道翻译的结果*/
    private TranslationBean translation(String value){
        //范例:http://fanyi.youdao.com/openapi.do?keyfrom=When-Copy&key=870362664&type=data&doctype=<doctype>&version=1.1&q=要翻译的文本
        TranslationBean translationBean = new TranslationBean();
        HttpURLConnection connection ;
        String encode = null;
        try {
            encode = URLEncoder.encode(value,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String all_url = "http://fanyi.youdao.com/openapi.do?keyfrom=When-Copy&key=870362664&type=data&doctype=json&version=1.1&q="
                + encode;
        try {
            URL url = new URL(all_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            String jsonString = readStream(connection.getInputStream()).replace("-","");
            Gson gson = new Gson();
            translationBean = gson.fromJson(jsonString,TranslationBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return translationBean;
    }

    /**通过readStream方法将字节流变成字符流，获得完整的网络数据JSON格式数据*/
    private String readStream(InputStream in){
        InputStreamReader re = null;
        String result = "";
        try {
            String line = "";
            re = new InputStreamReader(in, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(re);
            try {
                while((line = bufferedReader.readLine())!=null){
                    result += line;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                re.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchSelect:
                searchByBaidu(text);
                topViewController.removeView();
                break;

            case R.id.translationSelect:
                new TranslationAsy().execute(text);
                break;

            case R.id.inserteventsSelect:
                insertEvent(text);
                topViewController.removeView();
                break;

        }
    }

}
