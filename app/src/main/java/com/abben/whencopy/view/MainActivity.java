package com.abben.whencopy.view;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

import com.abben.whencopy.R;
import com.abben.whencopy.model.PreferencesController;
import com.abben.whencopy.model.MainModel;
import com.abben.whencopy.presenter.ListViewAdapter;
import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import java.util.List;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private PreferencesController preferencesController;
    private List<MainModel> modelList;
    private Switch search_switch;
    ImageView icon_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView  listView = (ListView) findViewById(R.id.list_view);
        search_switch = (Switch) findViewById(R.id.search_switch);
        icon_search = (ImageView) findViewById(R.id.icon_search);

//        SharedPreferences preferences = getSharedPreferences("WhenCopy", MODE_PRIVATE);

//        preferencesController = new PreferencesController(preferences);

//        isFirstRun();

//        modelList = preferencesController.getList();
//        Resources res=getResources();

//        ListViewAdapter listViewAdapter = new ListViewAdapter(res,this,modelList);
//        listView.setAdapter(listViewAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(modelList.get(position).getTitle().equals("关于")){
//                    showAboutDialog();
//                }
//            }
//        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(preferences);
        Preference<Boolean> searchPreference = rxSharedPreferences.getBoolean("SEARCH_SWITCH",true);
        search_switch.setChecked(searchPreference.get());

        RxCompoundButton.checkedChanges(search_switch)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        icon_search.setImageResource(aBoolean?R.mipmap.search_true:R.mipmap.search_false);
                    }
                });


    }

    /**判断是不是第一次启动APP，是的话就执行初始操作*/
    private boolean isFirstRun(){
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int currentVersion = info.versionCode;
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);

        int lastVerson = preferences.getInt("VERSION_KEY",0);

        preferencesController = new PreferencesController(preferences);
        //第一次启动APP,初始化preferences
        if(lastVerson!=currentVersion){
            preferencesController.initPreferces(currentVersion);
            return true;
        }else return false;
    }

    /**弹出关于的窗口*/
    private void showAboutDialog(){
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
