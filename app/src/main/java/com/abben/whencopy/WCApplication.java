package com.abben.whencopy;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Shaolin on 2017/7/4.
 */

public class WCApplication extends Application{
    public Notification notification = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // 若为创建独立进程，则不初始化成员变量
        if(getCurProcessName(getApplicationContext()).equals("com.abben.whencopy:clipboard")){
            return;
        }
        initNotification();
    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    private void initNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        Intent notificationIntent = new Intent(this, MainActivity.class);

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.icon);

        // 设置PendingIntent
        builder.setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0))
                .setLargeIcon(icon)  // 设置下拉列表中的图标(大图标)
                .setContentTitle(getString(R.string.app_name)) // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.icon) // 设置状态栏内的小图标
                .setContentText("正在监控剪切板...")// 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
    }
}
