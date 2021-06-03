package com.chinafocus.hvrskyworthvr.rtr.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.chinafocus.hvrskyworthvr.R;

import static com.chinafocus.hvrskyworthvr.global.Constants.VIDEO_UPDATE_STATUS;

/**
 * Video提示更新Dialog
 */
public class RtrVideoUpdateDialog extends Dialog {

    private AppCompatTextView mVideoUpdateText;
    private AppCompatButton mVideoUpdateOk;

    public RtrVideoUpdateDialog(Context context) {
        super(context, R.style.VrModeMainDialog);
        init(context);
    }

    private void init(Context context) {
        @SuppressLint("InflateParams") View mContentView = LayoutInflater.from(context).inflate(R.layout.rtr_dialog_video_update, null);
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        setContentView(mContentView, new ViewGroup.LayoutParams(screenWidth, screenHeight));

        mVideoUpdateText = mContentView.findViewById(R.id.tv_video_update_des);
        mContentView.findViewById(R.id.bt_video_update_cancel).setOnClickListener(v -> {
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(false, false);
            }
            dismiss();
        });
        mVideoUpdateOk = mContentView.findViewById(R.id.bt_video_update_ok);
        mVideoUpdateOk.setOnClickListener(v -> {
            boolean open = SPUtils.getInstance().getBoolean(VIDEO_UPDATE_STATUS);
            SPUtils.getInstance().put(VIDEO_UPDATE_STATUS, !open);
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(true, !open);
            }
            dismiss();
        });

        // 设置外部可以取消
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    private void showOpenVideoUpdateUI() {
        mVideoUpdateText.setText(getContext().getString(R.string.video_update_text_open));
        mVideoUpdateOk.setText(getContext().getString(R.string.video_update_button_open));
        mVideoUpdateOk.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shape_check_version_confirm_bg));
    }

    private void showCloseVideoUpdateUI() {
        mVideoUpdateText.setText(getContext().getString(R.string.video_update_text_close));
        mVideoUpdateOk.setText(getContext().getString(R.string.video_update_button_close));
        mVideoUpdateOk.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shape_check_version_pause_bg));
    }

    @Override
    public void show() {
        boolean open = SPUtils.getInstance().getBoolean(VIDEO_UPDATE_STATUS);
        if (open) {
            showCloseVideoUpdateUI();
        } else {
            showOpenVideoUpdateUI();
        }
        super.show();
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(boolean isChange, boolean switchStatus);
    }

    private OnCheckedChangeListener mOnCheckedChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener OnCheckedChangeListener) {
        mOnCheckedChangeListener = OnCheckedChangeListener;
    }

}
