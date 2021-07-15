package com.chinafocus.hvrskyworthvr.rtr.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.VolumeUtils;
import com.chinafocus.hvrskyworthvr.R;

public class RtrMediaSettingDialog extends AppCompatDialog {

    public RtrMediaSettingDialog(@NonNull Context context) {
        super(context, R.style.VrMediaSettingDialog);
        init(context);
    }

    private void init(Context context) {
        View mContentView = LayoutInflater.from(context).inflate(R.layout.rtr_dialog_media_setting, null);
        setContentView(mContentView);


        // 设置外部可以取消
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }
}
