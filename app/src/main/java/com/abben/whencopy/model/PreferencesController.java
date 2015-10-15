package com.abben.whencopy.model;

import android.content.SharedPreferences;

import com.abben.whencopy.R;
import com.abben.whencopy.model.MainModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abbenyyyyyy on 2015/10/14.
 */
public class PreferencesController {
    private static SharedPreferences preferences;

    public PreferencesController(SharedPreferences preferences){
        super();
        this.preferences = preferences;
    }


    /**初始化Preferces*/
    public void initPreferces(int currentVersion){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("VERSION_KEY", currentVersion);

        editor.putString("MAIN_TITLE", "启用When Copy");
        editor.putBoolean("MAIN_SWITCH", true);

        editor.putInt("SEARCH_ICONID", R.mipmap.ic_search);
        editor.putString("SEARCH_TITLE", "搜索");
        editor.putString("SEARCH_REMAKE", "通过百度搜索复制内容");
        editor.putBoolean("SEARCH_SWITCH", true);

        editor.putInt("TRANSLATION_ICONID", R.mipmap.ic_translation);
        editor.putString("TRANSLATION_TITLE", "翻译");
        editor.putString("TRANSLATION_REMAKE", "通过百度翻译翻译复制内容");
        editor.putBoolean("TRANSLATION_SWITCH", true);

        editor.putInt("INSERTEVENTS_ICONID", R.mipmap.ic_insertevents);
        editor.putString("INSERTEVENTS_TITLE", "日历事务");
        editor.putString("INSERTEVENTS_REMAKE", "在日历创建复制内容的事务");
        editor.putBoolean("INSERTEVENTS_SWITCH", true);


        editor.commit();



    }

    public List<MainModel> getList(){
        List<MainModel> modelList = new ArrayList<MainModel>();

        MainModel mainModel = new MainModel();
        mainModel.setIsShow_icon(false);
        mainModel.setIsShow_switch(true);
        mainModel.setTitle(preferences.getString("MAIN_TITLE", ""));
        mainModel.setRemake("");
        mainModel.setBoolean_switch(preferences.getBoolean("MAIN_SWITCH", true));
        mainModel.setIcon_id(0);
        modelList.add(mainModel);

        MainModel secondModel = new MainModel();
        secondModel.setIsShow_icon(true);
        secondModel.setIsShow_switch(true);
        secondModel.setTitle(preferences.getString("SEARCH_TITLE", ""));
        secondModel.setRemake(preferences.getString("SEARCH_REMAKE", ""));
        secondModel.setBoolean_switch(preferences.getBoolean("SEARCH_SWITCH", true));
        secondModel.setIcon_id(preferences.getInt("SEARCH_ICONID", 0));
        modelList.add(secondModel);

        MainModel thirdModel = new MainModel();
        thirdModel.setIsShow_icon(true);
        thirdModel.setIsShow_switch(true);
        thirdModel.setTitle(preferences.getString("TRANSLATION_TITLE", ""));
        thirdModel.setRemake(preferences.getString("TRANSLATION_REMAKE", ""));
        thirdModel.setBoolean_switch(preferences.getBoolean("TRANSLATION_SWITCH", true));
        thirdModel.setIcon_id(preferences.getInt("TRANSLATION_ICONID", 0));
        modelList.add(thirdModel);

        MainModel fourthModel = new MainModel();
        fourthModel.setIsShow_icon(true);
        fourthModel.setIsShow_switch(true);
        fourthModel.setTitle(preferences.getString("INSERTEVENTS_TITLE", ""));
        fourthModel.setRemake(preferences.getString("INSERTEVENTS_REMAKE", ""));
        fourthModel.setBoolean_switch(preferences.getBoolean("INSERTEVENTS_SWITCH", true));
        fourthModel.setIcon_id(preferences.getInt("INSERTEVENTS_ICONID", 0));
        modelList.add(fourthModel);

        MainModel fifthModel = new MainModel();
        fifthModel.setIsShow_icon(false);
        fifthModel.setIsShow_switch(false);
        fifthModel.setTitle("关于");
        fifthModel.setRemake("");
        fifthModel.setBoolean_switch(true);
        fifthModel.setIcon_id(0);
        modelList.add(fifthModel);

        return modelList;
    }


}
