package com.abben.whencopy.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.abben.whencopy.WhenCopyService;
import com.abben.whencopy.R;
import com.abben.whencopy.model.MainModel;

import java.util.List;

/**
 * Created by abbenyyyyyy on 2015/10/14.
 */
public class ListViewAdapter extends BaseAdapter {
    private List<MainModel> list;
    private LayoutInflater layoutInflater;
    private Resources res;
    private SharedPreferences preferences;
    private Context context;
    private Intent statServer;
    private ServiceConnection serviceConnection;
    private WhenCopyService myService;

    public ListViewAdapter(Resources res,Context context,List<MainModel> list){
        super();
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
        this.res = res;
        preferences = context.getSharedPreferences("init", 0);
        this.context = context;

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myService =  ((WhenCopyService.MyBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        statServer = new Intent(context,WhenCopyService.class);

        if(preferences.getBoolean("MAIN_SWITCH",false)){
            context.bindService(statServer,serviceConnection,Context.BIND_AUTO_CREATE);
        }


    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = layoutInflater.inflate(R.layout.item_listview,null);
            viewHolder = new ViewHolder();
            viewHolder.itemIcon = (ImageView) convertView.findViewById(R.id.item_icon);
            viewHolder.itemTittle = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.itemRemake = (TextView) convertView.findViewById(R.id.item_remake);
            viewHolder.itemSwitch = (Switch) convertView.findViewById(R.id.item_switch);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(list.get(position).isShowIcon()) {
            Bitmap bitmap = BitmapFactory.decodeResource(res, list.get(position).getIcon_id());
            viewHolder.itemIcon.setImageBitmap(bitmap);
        }else viewHolder.itemIcon.setVisibility(View.GONE);

        if(list.get(position).isShowSwitch()){
            viewHolder.itemSwitch.setTag(position);
            viewHolder.itemSwitch.setChecked(list.get(position).isBoolean_switch());
            viewHolder.itemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = preferences.edit();
                    if (isChecked) {
                        switch (buttonView.getTag().toString()) {
                            case "0":
                                editor.putBoolean("MAIN_SWITCH", true);
                                editor.commit();
                                Switch swi;
                                swi = (Switch) parent.findViewWithTag(1);
                                swi.setChecked(true);
                                swi = (Switch) parent.findViewWithTag(2);
                                swi.setChecked(true);
                                swi = (Switch) parent.findViewWithTag(3);
                                swi.setChecked(true);
                                context.bindService(statServer,serviceConnection, Context.BIND_AUTO_CREATE);
                                break;
                            //是搜索的开关
                            case "1":
                                editor.putBoolean("SEARCH_SWITCH", true);
                                editor.commit();
                                myService.changeWindowVis(1);
                                break;
                            //是翻译的开关
                            case "2":
                                editor.putBoolean("TRANSLATION_SWITCH", true);
                                editor.commit();
                                myService.changeWindowVis(2);
                                break;
                            //是日历的开关
                            case "3":
                                editor.putBoolean("INSERTEVENTS_SWITCH", true);
                                editor.commit();
                                myService.changeWindowVis(3);
                                break;
                        }
                    } else {
                        switch (buttonView.getTag().toString()) {
                            case "0":
                                editor.putBoolean("MAIN_SWITCH", false);
                                editor.commit();
                                Switch swi;
                                swi = (Switch) parent.findViewWithTag(1);
                                swi.setChecked(false);
                                swi = (Switch) parent.findViewWithTag(2);
                                swi.setChecked(false);
                                swi = (Switch) parent.findViewWithTag(3);
                                swi.setChecked(false);
                                myService.flag = false;
                                context.unbindService(serviceConnection);
                                break;
                            //是搜索的开关
                            case "1":
                                editor.putBoolean("SEARCH_SWITCH", false);
                                editor.commit();
                                myService.changeWindowGone(1);
                                break;

                            //是翻译的开关
                            case "2":
                                editor.putBoolean("TRANSLATION_SWITCH", false);
                                editor.commit();
                                myService.changeWindowGone(2);
                                break;
                            //是日历的开关
                            case "3":
                                editor.putBoolean("INSERTEVENTS_SWITCH", false);
                                editor.commit();
                                myService.changeWindowGone(3);
                                break;
                        }

                    }
                }
            });
        }else viewHolder.itemSwitch.setVisibility(View.GONE);

        viewHolder.itemTittle.setText(list.get(position).getTitle());

        viewHolder.itemRemake.setText(list.get(position).getRemake());

        return convertView;
    }

    class ViewHolder{
        ImageView itemIcon;
        TextView itemTittle,itemRemake;
        Switch itemSwitch;
    }
}
