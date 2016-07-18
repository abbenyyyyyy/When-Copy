package com.abben.whencopy;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
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
    private ServiceConnection serviceConnection;
    private WhenCopyService whenCopyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                whenCopyService = ((WhenCopyService.MyBinder) service).getService();
                initView();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Intent startService = new Intent(MainActivity.this, WhenCopyService.class);
                bindService(startService, serviceConnection, BIND_AUTO_CREATE);
            }
        };

        Intent startService = new Intent(MainActivity.this, WhenCopyService.class);
        bindService(startService, serviceConnection, BIND_AUTO_CREATE);

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
    }

    /**
     * 绑定搜索searchSwitch的变化
     */
    private void checkUpdateSearchSwitch(RxSharedPreferences rxSharedPreferences) {
        Switch search_switch = (Switch) findViewById(R.id.search_switch);
        search_icon = (ImageView) findViewById(R.id.search_icon);

        searchPreference = rxSharedPreferences.getBoolean("SEARCH_SWITCH", true);
        search_switch.setChecked(searchPreference.get());
        whenCopyService.notifySelectViewVisbility(WhenCopyService.SELECT_SEARCH_INDEX,searchPreference.get());

        RxCompoundButton.checkedChanges(search_switch)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        searchPreference.set(aBoolean);
                        search_icon.setImageResource(aBoolean ? R.mipmap.search_true : R.mipmap.search_false);
                        whenCopyService.notifySelectViewVisbility(WhenCopyService.SELECT_SEARCH_INDEX,aBoolean);
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
        whenCopyService.notifySelectViewVisbility(WhenCopyService.SELECT_TRANSLATION_INDEX,translationPreference.get());

        RxCompoundButton.checkedChanges(translation_switch)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        translationPreference.set(aBoolean);
                        translation_icon.setImageResource(aBoolean ? R.mipmap.translation_true : R.mipmap.translation_false);
                        whenCopyService.notifySelectViewVisbility(WhenCopyService.SELECT_TRANSLATION_INDEX,aBoolean);
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
        whenCopyService.notifySelectViewVisbility(WhenCopyService.SELECT_INSERTEVENTS_INDEX,inserteventsPreference.get());

        RxCompoundButton.checkedChanges(insertevents_switch)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        inserteventsPreference.set(aBoolean);
                        insertevents_icon.setImageResource(aBoolean ? R.mipmap.insertevents_true : R.mipmap.insertevents_false);
                        whenCopyService.notifySelectViewVisbility(WhenCopyService.SELECT_INSERTEVENTS_INDEX,aBoolean);
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

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
