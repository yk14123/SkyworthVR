package com.chinafocus.hvrskyworthvr.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.blankj.utilcode.util.ScreenUtils;
import com.chinafocus.hvrskyworthvr.R;

public class VrModeMainDialog extends AppCompatDialog {

    public VrModeMainDialog(@NonNull Context context) {
        super(context, R.style.VrModeMainDialog);
        init(context);
    }

    private void init(Context context) {
        View mContentView = LayoutInflater.from(context).inflate(R.layout.dialog_vr_mode_selected_video, null);
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        // 全新的方式设定宽高！
        setContentView(mContentView, new ViewGroup.LayoutParams(screenWidth, screenHeight));

        // 设置外部可以取消
        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }


}
