package com.abben.whencopy;


import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

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
    private WindowManager.LayoutParams layoutParams;
    private final String start_url = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=TRsKBvAptS60HNG8kkheuFwB&q=";
    private final String end_url = "&from=auto&to=auto";
    private String text;
    private TranslationThred t;
    public boolean flag = true;//控制是否显示悬浮窗口
    private String result = null;
    private TextView textView;
    private ImageButton bt1;
    private ImageButton bt2;
    private ImageButton bt3;

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
        setupView(WhenCopyService.this);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", ""));
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = clipboardManager.getPrimaryClip();
                if ((clipData.getItemCount()==1) && flag){
                    text = clipData.getItemAt(0).getText().toString();
                    windowManager.addView(view, layoutParams);
                    t = new TranslationThred();
                    t.start();
                }
            }
        });

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

    /**生成选择悬浮窗*/
    public void setupView(Context context){
        view = LayoutInflater.from(context).inflate(R.layout.window_main,null);
        bt1 = (ImageButton) view.findViewById(R.id.imageButton);
        bt2 = (ImageButton) view.findViewById(R.id.imageButton2);
        bt3 = (ImageButton) view.findViewById(R.id.imageButton3);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode){
                    case KeyEvent.KEYCODE_BACK:
                        hideWindow();
                        return true;
                    default:
                        return false;
                }
            }
        });

        layoutParams = new WindowManager.LayoutParams();

        //设置type为最顶窗口
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //允许窗口进行交互，以及BACK键得使用
        layoutParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;
    }

    /**关闭选择悬浮窗口*/
    public void hideWindow(){
        if(view!=null) {
            windowManager.removeViewImmediate(view);
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
        windowManager.addView(translationView,layoutParams);
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

    /**得到百度翻译的结果*/
    public String translation(String value){
        String result = null;
        HttpURLConnection connection = null;
        InputStream is =null;
        String encode = null;
        try {
            encode = URLEncoder.encode(value,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String all_url = start_url + encode +end_url;
        try {
            URL url = new URL(all_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            String jsonString = readStream(connection.getInputStream());
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("trans_result");
            jsonObject = jsonArray.getJSONObject(0);
            result = jsonObject.getString("dst");
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
        t.stopThere();
    }
}
