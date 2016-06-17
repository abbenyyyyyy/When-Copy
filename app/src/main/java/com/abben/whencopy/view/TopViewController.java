package com.abben.whencopy.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.abben.whencopy.R;


public class TopViewController implements View.OnClickListener, View.OnTouchListener, View.OnKeyListener {
    private WindowManager windowManager;
    private Context context;
    private View selectView;
    private LinearLayout selectLayout;

    public TopViewController(Context context) {
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void showSelect() {
        selectView = LayoutInflater.from(context).inflate(R.layout.window_main, null);

        selectLayout = (LinearLayout) selectView.findViewById(R.id.selectLayout);

        selectView.setOnTouchListener(this);
        selectView.setOnKeyListener(this);

        windowManager.addView(selectView, getWindowLayoutParams());
    }

    private void removeView() {
        if (selectView != null) {
            selectView.setOnKeyListener(null);
            selectView.setOnTouchListener(null);
            windowManager.removeView(selectView);
        }
    }

    private WindowManager.LayoutParams getWindowLayoutParams() {
        int w = WindowManager.LayoutParams.MATCH_PARENT;
        int h = WindowManager.LayoutParams.MATCH_PARENT;

        int flags = 0;
        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP;

        return layoutParams;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            removeView();
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        Rect rect = new Rect();
        selectLayout.getGlobalVisibleRect(rect);
        if (!rect.contains(x, y)) {
            removeView();
        }
        return false;
    }
}
