package com.abben.whencopy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.abben.whencopy.controller.PreferencesController;
import com.abben.whencopy.model.MainModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private PreferencesController preferencesController;
    private List<MainModel> modelList;
    private ListViewAdapter listViewAdapter;
    private ListView listView;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view);

        preferences = getSharedPreferences("init", MODE_PRIVATE);

        preferencesController = new PreferencesController(preferences);

        isFirstRun();

        modelList = preferencesController.getList();
        Resources res=getResources();

        listViewAdapter = new ListViewAdapter(res,this,modelList);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(modelList.get(position).getTitle().equals("关于")){
                    dialog();
                }
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

        MainModel mainModel = new MainModel();
        preferencesController = new PreferencesController(preferences);
        //第一次启动APP,初始化preferences
        if(lastVerson!=currentVersion){
            preferencesController.initPreferces(currentVersion);
            return true;
        }else return false;
    }

    /**弹出关于的窗口*/
    private void dialog(){
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
