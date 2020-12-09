package com.chinafocus.hvrskyworthvr.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ScreenUtils;
import com.chinafocus.hvrskyworthvr.R;

public class VrModeVideoLinkingDialog extends Dialog {

    public VrModeVideoLinkingDialog(@NonNull Context context) {
        super(context, R.style.VrModeLinkingDialog);
        init(context);
    }

    private void init(Context context) {

        View mContentView = LayoutInflater.from(context).inflate(R.layout.dialog_vr_mode_linking_video, null);
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        // 全新的方式设定宽高！
        setContentView(mContentView, new ViewGroup.LayoutParams(screenWidth, screenHeight));

        mContentView.findViewById(R.id.tv_dialog_back).setOnClickListener(v -> dismiss());

        // 设置外部可以取消
        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }

}
