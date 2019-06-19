package com.huabao.mytest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.huabao.mytest.R;

public class AirDialog extends Dialog {

    private Context mContext;

    public AirDialog(Context context, int themeResId) {
        super(context, themeResId);

        this.mContext = context;

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_air);
        setCanceledOnTouchOutside(false);
    }



}