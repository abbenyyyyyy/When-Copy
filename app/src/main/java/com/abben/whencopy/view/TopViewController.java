package com.abben.whencopy.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abben.whencopy.R;
import com.abben.whencopy.TranslationBean;
import com.abben.whencopy.WhenCopyService;


public class TopViewController implements View.OnTouchListener, View.OnKeyListener {
    private WindowManager windowManager;
    private Context context;
    private View windowView;
    private LinearLayout displayLayout , searchSelect , translationSelect,inserteventsSelect;
    private View.OnClickListener onClickListener;

    public TopViewController(Context context) {
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**显示选择窗口*/
    public void showSelect(boolean[] visibilityFlag) {
        windowView = LayoutInflater.from(context).inflate(R.layout.window_main, null);
        displayLayout = (LinearLayout) windowView.findViewById(R.id.selectLayout);

        searchSelect = (LinearLayout) windowView.findViewById(R.id.searchSelect);
        translationSelect = (LinearLayout) windowView.findViewById(R.id.translationSelect);
        inserteventsSelect = (LinearLayout) windowView.findViewById(R.id.inserteventsSelect);
        for(int i =0;i<visibilityFlag.length;i++){
            if(!visibilityFlag[i]){
                switch (i){
                    case WhenCopyService.SELECT_SEARCH_INDEX:
                        searchSelect.setVisibility(View.GONE);
                        break;
                    case WhenCopyService.SELECT_TRANSLATION_INDEX:
                        translationSelect.setVisibility(View.GONE);
                        break;
                    case WhenCopyService.SELECT_INSERTEVENTS_INDEX:
                        inserteventsSelect.setVisibility(View.GONE);
                        break;
                }
            }
        }

        if(onClickListener != null){
            searchSelect.setOnClickListener(onClickListener);
            translationSelect.setOnClickListener(onClickListener);
            inserteventsSelect.setOnClickListener(onClickListener);
        }

        windowView.setOnTouchListener(this);
        windowView.setOnKeyListener(this);

        windowManager.addView(windowView, getWindowLayoutParams());
    }

    /**显示翻译结果*/
    public void showTranslation(TranslationBean translationBean){
        removeView();

        windowView = LayoutInflater.from(context).inflate(R.layout.window_translation,null);
        displayLayout = (LinearLayout) windowView.findViewById(R.id.translationLayout);

        setTranslationView(windowView , translationBean);

        windowView.setOnTouchListener(this);
        windowView.setOnKeyListener(this);

        windowManager.addView(windowView, getWindowLayoutParams());
    }

    public void updateOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public void removeView() {
        if (windowView != null) {
            windowView.setOnKeyListener(null);
            windowView.setOnTouchListener(null);
            if(searchSelect != null){
                searchSelect.setOnClickListener(null);
                translationSelect.setOnClickListener(null);
                inserteventsSelect.setOnClickListener(null);
                onClickListener = null;
            }
            windowManager.removeView(windowView);
        }
    }


    private void setTranslationView(View view , TranslationBean translationBean){
        TextView query = (TextView) view.findViewById(R.id.queryText);
        //errorCode：
        //0 - 正常
        //20 - 要翻译的文本过长
        //30 - 无法进行有效的翻译
        //40 - 不支持的语言类型
        //50 - 无效的key
        //60 - 无词典结果，仅在获取词典结果生效
        switch (translationBean.getErrorCode()){
            case 20:
                query.setText("要翻译的文本过长");
                break;
            case 30:
                query.setText("无法进行有效的翻译");
                break;
            case 40:
                query.setText("不支持的语言类型");
                break;
            case 50:
                query.setText("无效的key");
                break;
            case 60:
                query.setText("无词典结果");
                break;
            case 0:
                query.setText(translationBean.getQuery());
                if(translationBean.getBasic().getUkphonetic() != null){
                    view.findViewById(R.id.phoneticLayout).setVisibility(View.VISIBLE);
                    String ukPhonetic = "英 [ " + translationBean.getBasic().getUkphonetic() + "]";
                    ((TextView) view.findViewById(R.id.ukPhonetic)).setText(ukPhonetic);
                    if(translationBean.getBasic().getUsphonetic() != null){
                        String usPhonetic = "美 [ " + translationBean.getBasic().getUsphonetic() + "]";
                        ((TextView) view.findViewById(R.id.usPhonetic)).setText(usPhonetic);
                    }
                }
                LinearLayout explainsLayout = (LinearLayout) view.findViewById(R.id.explainsLayout);
                for(String x : translationBean.getBasic().getExplains()){
                    TextView explainsTextView = new TextView(context);
                    explainsTextView.setText(x);
                    explainsTextView.setTextAppearance(context, R.style.bottomTextviewTheme);
                    explainsLayout.addView(explainsTextView);
                }
                break;
            default:
                break;
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
        displayLayout.getGlobalVisibleRect(rect);
        if (!rect.contains(x, y)) {
            removeView();
        }
        return false;
    }
}
