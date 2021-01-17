package com.chinafocus.hvrskyworthvr.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatEditText;

import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.R;

import java.util.Objects;

import static com.chinafocus.hvrskyworthvr.global.Constants.DEVICE_ALIAS;

public class SettingAliasDialog extends AppCompatDialog {

    private AppCompatEditText mEditText;

    public SettingAliasDialog(@NonNull Context context) {
        super(context, R.style.VrModeLinkingDialog);
        init(context);
    }

    private void init(Context context) {
        @SuppressLint("InflateParams") View mContentView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_alias, null);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //去掉dialog默认的padding
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = 1000;
            lp.height = 529;
            //设置dialog的位置在底部
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);

            //解决dilaog中EditText无法弹出输入的问题
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }

        // 设置外部可以取消
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(mContentView);

        mEditText = mContentView.findViewById(R.id.et_setting_alias);

        mContentView.findViewById(R.id.bt_setting_alias_cancel).setOnClickListener(v -> dismiss());
        mContentView.findViewById(R.id.bt_setting_alias_confirm).setOnClickListener(v -> {
            String s = Objects.requireNonNull(mEditText.getText()).toString().trim();
            if (mAliasSettingListener != null) {
                mAliasSettingListener.postDeviceName(s);
            }
            dismiss();
        });


    }

    public interface AliasSettingListener {
        void postDeviceName(String name);
    }

    private AliasSettingListener mAliasSettingListener;

    public void setAliasSettingListener(AliasSettingListener aliasSettingListener) {
        mAliasSettingListener = aliasSettingListener;
    }

    @Override
    public void show() {
        super.show();
        mEditText.setText(SPUtils.getInstance().getString(DEVICE_ALIAS));
        //弹出对话框后直接弹出键盘
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        mEditText.setSelection(mEditText.getText() == null ? 0 : mEditText.getText().length());
        mEditText.postDelayed(() -> {
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(mEditText, 0);
        }, 100);
    }
}
