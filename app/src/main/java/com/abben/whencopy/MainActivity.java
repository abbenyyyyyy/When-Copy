package com.abben.whencopy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private ImageView search_icon, translation_icon, insertevents_icon;
    private Preference<Boolean> searchPreference, translationPreference, inserteventsPreference;
    private Intent startIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startIntent = new Intent(Constant.ACTION_MYSERVICE);
        startIntent.setPackage(getPackageName());
        startService(startIntent);
        initView();

    }

    private void initView() {
        RelativeLayout aboutLayout = (RelativeLayout) findViewById(R.id.about_layout);
        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
            }
        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(preferences);
        checkUpdateSearchSwitch(rxSharedPreferences);
        checkUpdateTranslationSwitch(rxSharedPreferences);
        checkUpdateInserteventsSwitch(rxSharedPreferences);

        //延时2秒是为了服务已被启动，能正常接收初始化信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    initServiceVisibilityFlag();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).run();
    }

    /**通知另一进程的服务初始化VisibilityFlag*/
    private void initServiceVisibilityFlag(){
        boolean[] visibilityFlag = {searchPreference.get() ,translationPreference.get(), inserteventsPreference.get()};
        startIntent.setAction(Constant.ACTION_INIT);
        startIntent.putExtra("initVisibity",visibilityFlag);
        sendBroadcast(startIntent);
    }

    /**通知另一进程的服务改变VisibilityFlag*/
    private void notifyVisibity(int changeVisibityIndex , boolean visibility){
        startIntent.setAction(Constant.ACTION_CHANGE_VISIBITY);
        startIntent.putExtra("changeVisibityIndex",changeVisibityIndex);
        startIntent.putExtra("changeVisibity",visibility);
        sendBroadcast(startIntent);
    }

    /**
     * 绑定搜索searchSwitch的变化
     */
    private void checkUpdateSearchSwitch(RxSharedPreferences rxSharedPreferences) {
        Switch search_switch = (Switch) findViewById(R.id.search_switch);
        search_icon = (ImageView) findViewById(R.id.search_icon);

        searchPreference = rxSharedPreferences.getBoolean("SEARCH_SWITCH", true);
        search_switch.setChecked(searchPreference.get());

        RxCompoundButton.checkedChanges(search_switch)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        searchPreference.set(aBoolean);
                        search_icon.setImageResource(aBoolean ? R.mipmap.search_true : R.mipmap.search_false);
                        notifyVisibity(WhenCopyService.SELECT_SEARCH_INDEX,aBoolean);

                    }
                });
    }

    /**
     * 绑定翻译translationSwitch的变化
     */
    private void checkUpdateTranslationSwitch(RxSharedPreferences rxSharedPreferences) {
        Switch translation_switch = (Switch) findViewById(R.id.translation_switch);
        translation_icon = (ImageView) findViewById(R.id.translation_icon);

        translationPreference = rxSharedPreferences.getBoolean("TRANSLATION_SWITCH", true);
        translation_switch.setChecked(translationPreference.get());

        RxCompoundButton.checkedChanges(translation_switch)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        translationPreference.set(aBoolean);
                        translation_icon.setImageResource(aBoolean ? R.mipmap.translation_true : R.mipmap.translation_false);
                        notifyVisibity(WhenCopyService.SELECT_TRANSLATION_INDEX,aBoolean);

                    }
                });
    }

    /**
     * 绑定插入日历inserteventsSwitch的变化
     */
    private void checkUpdateInserteventsSwitch(RxSharedPreferences rxSharedPreferences) {
        Switch insertevents_switch = (Switch) findViewById(R.id.insertevents_switch);
        insertevents_icon = (ImageView) findViewById(R.id.insertevents_icon);

        inserteventsPreference = rxSharedPreferences.getBoolean("INSERTEVENTS_SWITCH", true);
        insertevents_switch.setChecked(inserteventsPreference.get());

        RxCompoundButton.checkedChanges(insertevents_switch)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        inserteventsPreference.set(aBoolean);
                        insertevents_icon.setImageResource(aBoolean ? R.mipmap.insertevents_true : R.mipmap.insertevents_false);
                         notifyVisibity(WhenCopyService.SELECT_INSERTEVENTS_INDEX,aBoolean);
                    }
                });
    }

    /**
     * 弹出关于的窗口
     */
    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("关于");
        builder.setMessage(getString(R.string.detail_about));
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
