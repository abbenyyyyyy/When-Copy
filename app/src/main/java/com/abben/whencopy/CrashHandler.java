package com.abben.whencopy;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by abben on 2018/4/18.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static CrashHandler sInstance = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private static String PATH;
    private static final String FILE_NAME = "crash";

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + "whencopy" + File.separatorChar + "log" + File.separatorChar;
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            dumpExceptionToSDCard(throwable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        throwable.printStackTrace();
        //如果系统提供了默认异常处理器，则交给系统去结束程序，否则就自己结束程序
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, throwable);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * 将异常信息写入SD卡中
     *
     * @param throwable
     * @throws IOException
     */
    private void dumpExceptionToSDCard(Throwable throwable) throws IOException {
        //如果 SD 卡不存在或无法使用，则无法把异常信息写入SD卡中
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.w(TAG, "sdcard unmounted,skip dump exception");
            return;
        }

        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(current));
        File file = new File(PATH + File.separatorChar + FILE_NAME + time);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            throwable.printStackTrace(pw);
            pw.close();
        } catch (IOException e) {
            Log.e(TAG, "dump crash info failed");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将手机信息写入文件中
     *
     * @param pw
     * @throws PackageManager.NameNotFoundException
     */
    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.write("App Version：");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        //Android 版本号
        pw.print("OS Version： ");
        pw.print(Build.VERSION.RELEASE);
        pw.print('_');
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.print("Model： ");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model： ");
        pw.println(Build.MODEL);

        //CPU 架构
        pw.print("CPU ABI：");
        pw.println(Build.CPU_ABI);

    }
}
