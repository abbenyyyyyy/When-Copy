package com.abben.whencopy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by abbenyyyyyy on 2015/10/13.
 */
public class SelectWindow  extends Activity{
    private Button btn_take_photo, btn_pick_photo, btn_cancel;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.window_main);

   }
}
