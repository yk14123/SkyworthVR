package com.chinafocus.hvrskyworthvr.rtr.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.blankj.utilcode.util.ScreenUtils;
import com.chinafocus.hvrskyworthvr.R;

public class RtrBluetoothLostDialog extends AppCompatDialog {

    private View mContentView;

    public RtrBluetoothLostDialog(@NonNull Context context) {
        super(context, R.style.VrModeMainDialog);
        init(context);
    }

    private void init(Context context) {
        mContentView = LayoutInflater.from(context).inflate(R.layout.rtr_dialog_bluetooth_lost, null);
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        // 全新的方式设定宽高！
        setContentView(mContentView, new ViewGroup.LayoutParams(screenWidth, screenHeight));

        mContentView.findViewById(R.id.bt_bluetooth_status_ok).setOnClickListener(v -> {
            dismiss();
        });

        // 设置外部可以取消
        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }
}
