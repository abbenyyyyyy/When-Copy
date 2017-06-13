package com.abben.whencopy;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.abben.whencopy.bean.UpdateBean;
import com.abben.whencopy.network.RetrofitHelper;
import com.abben.whencopy.network.RetryWithDelay;
import com.abben.whencopy.view.customview.CustomDialog;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.Util;

public class MainActivity extends AppCompatActivity {
    private ImageView search_icon, translation_icon, insertevents_icon;
    private final String searchPreferenceKey = "searchPreferenceKey", translationPreferenceKey = "translationPreferenceKey",
            inserteventsPreferenceKey = "inserteventsPreferenceKey";

    private CompositeDisposable mCompositeDisposable;
    private SharedPreferences preferences;
    private CustomAidlInterface customAidlInterface;
    private AdditionServiceConnection additionServiceConnection;

    private long appDownloadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCompositeDisposable = new CompositeDisposable();
        initService();
        initView();
        checkUpdate(3,15);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(additionServiceConnection);
        mCompositeDisposable.clear();
    }

    private class AdditionServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            customAidlInterface = CustomAidlInterface.Stub.asInterface(service);
            initServiceVisibilityFlag();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            customAidlInterface = null;
        }
    }


    private void initService() {
        additionServiceConnection = new AdditionServiceConnection();
        Intent intent = new Intent(MainActivity.this,WhenCopyService.class);
        bindService(intent, additionServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        RelativeLayout aboutLayout = (RelativeLayout) findViewById(R.id.about_layout);
        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
            }
        });

        checkUpdateSearchSwitch();
        checkUpdateTranslationSwitch();
        checkUpdateInserteventsSwitch();

    }

    private void setSharedPreferences(String key, boolean aBoolean){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,aBoolean);
        editor.apply();
    }

    /**通知另一进程的服务初始化VisibilityFlag*/
    private void initServiceVisibilityFlag(){
        try {
            customAidlInterface.initServiceVisibilityFlag(preferences.getBoolean(searchPreferenceKey,true) ,
                    preferences.getBoolean(translationPreferenceKey,true), preferences.getBoolean(inserteventsPreferenceKey,true));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**通知另一进程的服务改变VisibilityFlag*/
    private void notifyVisibity(int changeVisibityIndex , boolean visibility){
        try {
            customAidlInterface.changeView(changeVisibityIndex,visibility);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定搜索searchSwitch的变化
     */
    private void checkUpdateSearchSwitch() {
        Switch search_switch = (Switch) findViewById(R.id.search_switch);
        search_icon = (ImageView) findViewById(R.id.search_icon);

        boolean isChecked = preferences.getBoolean(searchPreferenceKey,true);
        search_icon.setImageResource(isChecked ? R.mipmap.search_true : R.mipmap.search_false);
        search_switch.setChecked(isChecked);
        search_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSharedPreferences(searchPreferenceKey,isChecked);
                search_icon.setImageResource(isChecked ? R.mipmap.search_true : R.mipmap.search_false);
                notifyVisibity(WhenCopyService.SELECT_SEARCH_INDEX,isChecked);
            }
        });
    }

    /**
     * 绑定翻译translationSwitch的变化
     */
    private void checkUpdateTranslationSwitch() {
        Switch translation_switch = (Switch) findViewById(R.id.translation_switch);
        translation_icon = (ImageView) findViewById(R.id.translation_icon);

        boolean isChecked = preferences.getBoolean(translationPreferenceKey,true);
        translation_icon.setImageResource(isChecked ? R.mipmap.translation_true : R.mipmap.translation_false);
        translation_switch.setChecked(isChecked);
        translation_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSharedPreferences(translationPreferenceKey,isChecked);
                translation_icon.setImageResource(isChecked ? R.mipmap.translation_true : R.mipmap.translation_false);
                notifyVisibity(WhenCopyService.SELECT_TRANSLATION_INDEX,isChecked);
            }
        });
    }

    /**
     * 绑定插入日历inserteventsSwitch的变化
     */
    private void checkUpdateInserteventsSwitch() {
        Switch insertevents_switch = (Switch) findViewById(R.id.insertevents_switch);
        insertevents_icon = (ImageView) findViewById(R.id.insertevents_icon);

        boolean isChecked = preferences.getBoolean(inserteventsPreferenceKey,true);
        insertevents_icon.setImageResource(isChecked ? R.mipmap.insertevents_true : R.mipmap.insertevents_false);
        insertevents_switch.setChecked(isChecked);
        insertevents_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSharedPreferences(inserteventsPreferenceKey,isChecked);
                insertevents_icon.setImageResource(isChecked ? R.mipmap.insertevents_true : R.mipmap.insertevents_false);
                notifyVisibity(WhenCopyService.SELECT_INSERTEVENTS_INDEX,isChecked);
            }
        });
    }



    /**
     * 弹出关于的窗口
     */
    private void showAboutDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(R.string.about);
        builder.setMessage(getString(R.string.detail_about));
        builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void checkUpdate(int maxRetries, int retryDelaySeconds) {

        final String firImUpdateUrl = "http://api.fir.im/apps/latest/57abe8de959d6921cc000665?api_token=f5c0e4f1f9f642e9a5ab657903825660";
        Api api = RetrofitHelper.getRetrofit().create(Api.class);
        api.fetchStringFromUrl(firImUpdateUrl)
                .retryWhen(new RetryWithDelay(maxRetries, retryDelaySeconds))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull UpdateBean updateBean) {
                        compareLocalAndServer(updateBean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 比较本地应用的版本号和服务器的版本号，是否需要更新
     */
    private boolean compareLocalAndServer(UpdateBean updateBean){
        try {
            int compareResult = getLocalAPPVersion(this).compareTo(updateBean.getVersionShort());
            if(compareResult < 0){
                showDialog(updateBean);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void showDialog(final UpdateBean updateBean) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        if(updateBean.getChangelog() != null && !updateBean.getChangelog().equals("")){
            builder.setMessage(updateBean.getChangelog() + "\n" + getString(R.string.update_message));
        }else{
            builder.setMessage(getString(R.string.update_message));
        }
        builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startDownload(updateBean.getInstallUrl());
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 获取当前应用的版本号
     */
    private String getLocalAPPVersion(Context context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionName;
    }

    private void startDownload(String downloadUrl){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setTitle("WhenCopy下载");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"WhenCopy.apk");
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        appDownloadId = downloadManager.enqueue(request);
        //注册广播接收器
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, filter);
    }

    /**
     * 广播接受器, 下载完成监听器
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction() ;
            if( action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
                //获取当前完成任务的ID
                long  referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if(appDownloadId == referenceId){
                    String appFilePath = Environment.getExternalStorageDirectory() +File.separator
                            + Environment.DIRECTORY_DOWNLOADS + File.separator + "WhenCopy.apk";
                    installApk(appFilePath);
                }
            }
        }
    };

    /**发出安装APK的意图给用户*/
    private void installApk(String appFilePath){
        File appFile = new File(appFilePath);
        // 创建URI
        Uri uri = Uri.fromFile(appFile);
        // 创建Intent意图
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 设置Uri和类型
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 执行意图进行安装
        startActivity(intent);
    }

}
