package com.abben.whencopy;


import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.abben.whencopy.view.TopViewController;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by abbenyyyyyy on 2015/10/13.
 */
public class WhenCopyService extends Service implements View.OnClickListener{
    private View view;
    private View translationView;
    private WindowManager windowManager;
    private String text;
    private TranslationThred t;
    public boolean flag = true;//控制是否显示悬浮窗口
    private String result = null;
    private TextView textView;
    private ImageButton bt1;
    private ImageButton bt2;
    private ImageButton bt3;
    private TopViewController topViewController;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder{
        public WhenCopyService getService(){
            return WhenCopyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        flag = true;
        topViewController = new TopViewController(WhenCopyService.this);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", ""));
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = clipboardManager.getPrimaryClip();
                if ((clipData.getItemCount()==1) && flag){
                    text = clipData.getItemAt(0).getText().toString();
                    topViewController.showSelect();
                    t = new TranslationThred();
                    t.start();
                }
            }
        });

    }

    public void notifySelectViewVisbility(){

    }


    /**翻译的线程*/
    class TranslationThred extends Thread {
        private boolean stopFlag = false;

        @Override
        public void run() {
            while (!stopFlag){
                result = translation(text);
                if(result!=null){
                    break;
                }
            }
        }

        public void stopThere(){
            stopFlag = true;
        }
    }



    private WindowManager.LayoutParams getPopViewParams() {
        int w = WindowManager.LayoutParams.MATCH_PARENT;
        int h = WindowManager.LayoutParams.MATCH_PARENT;

        int flags = 0;
        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        layoutParams.gravity = Gravity.CENTER | Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = 0;
        return layoutParams;
    }

    /**关闭选择悬浮窗口*/
    public void hideWindow(){
        if(view!=null) {
            windowManager.removeViewImmediate(view);
            view.setOnKeyListener(null);
            view.setOnClickListener(null);
        }

    }

    /**生成翻译窗口并显示*/
    public void setupTranslationView(Context context,String text){
        translationView = LayoutInflater.from(context).inflate(R.layout.window_translation,null);
        textView = (TextView) translationView.findViewById(R.id.translation);
        textView.setOnClickListener(this);
        textView.setText(result);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode){
                    case KeyEvent.KEYCODE_BACK:
                        windowManager.removeViewImmediate(translationView);
                        return true;
                    default:
                        return false;
                }
            }
        });
        hideWindow();
        windowManager.addView(translationView,getPopViewParams());
    }

    /**改变窗口图标为不可见*/
    public void changeWindowGone(int num){
        switch (num){
            case 1:
                bt1.setVisibility(View.GONE);
                break;
            case 2:
                bt2.setVisibility(View.GONE);
                break;

            case 3:
                bt2.setVisibility(View.GONE);
                break;
        }
    }

    /**改变窗口图标为可见*/
    public void changeWindowVis(int num){
        switch (num){
            case 1:
                bt1.setVisibility(View.VISIBLE);
                break;
            case 2:
                bt2.setVisibility(View.VISIBLE);
                break;

            case 3:
                bt2.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**启动百度搜索*/
    public void searchByBaidu(String text){
        Uri uri = Uri.parse("http://www.baidu.com/s?wd=" + text);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**插入日历事件*/
    public void insertEvent(String text){
        Intent intent = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE,text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**得到有道翻译的结果*/
    public String translation(String value){
        //范例:http://fanyi.youdao.com/openapi.do?keyfrom=When-Copy&key=870362664&type=data&doctype=<doctype>&version=1.1&q=要翻译的文本
        String result = "";
        HttpURLConnection connection ;
        String encode = null;
        try {
            encode = URLEncoder.encode(value,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String all_url = "http://fanyi.youdao.com/openapi.do?keyfrom=When-Copy&key=870362664&type=data&doctype=<doctype>&version=1.1&q="
                + encode;
        try {
            URL url = new URL(all_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            String jsonString = readStream(connection.getInputStream());
            JSONObject jsonObject = new JSONObject(jsonString);
            Gson gson = new Gson();
            TranslationBean translationBean = gson.fromJson(jsonString,TranslationBean.class);
            Log.i("wwwww","标志解析:"+translationBean.getTranslation().toString());
//            JSONArray jsonArray = jsonObject.getJSONArray("trans_result");
//            jsonObject = jsonArray.getJSONObject(0);
//            result = jsonObject.getString("dst");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
            case R.id.imageButton:
                searchByBaidu(text);
                hideWindow();
                break;

            case R.id.imageButton2:
                setupTranslationView(WhenCopyService.this,text);
                break;

            case R.id.imageButton3:
                insertEvent(text);
                hideWindow();
                break;

            case R.id.translation:
                windowManager.removeViewImmediate(translationView);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
