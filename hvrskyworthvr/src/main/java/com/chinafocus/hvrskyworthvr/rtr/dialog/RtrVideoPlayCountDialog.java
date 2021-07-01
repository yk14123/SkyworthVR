package com.chinafocus.hvrskyworthvr.rtr.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;

import com.blankj.utilcode.util.ScreenUtils;
import com.chinafocus.hvrskyworthvr.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Video提示更新Dialog
 */
public class RtrVideoPlayCountDialog extends Dialog {


    public RtrVideoPlayCountDialog(Context context) {
        super(context, R.style.VrModeMainDialog);
        init(context);
    }

    private void init(Context context) {
        @SuppressLint("InflateParams") View mContentView = LayoutInflater.from(context).inflate(R.layout.rtr_dialog_video_play_count, null);
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        setContentView(mContentView, new ViewGroup.LayoutParams(screenWidth, screenHeight));

        mContentView.findViewById(R.id.bt_video_play_count_cancel).setOnClickListener(v -> dismiss());
        AppCompatButton mVideoUpdateOk = mContentView.findViewById(R.id.bt_video_play_count__ok);
        mVideoUpdateOk.setOnClickListener(v -> {
            Date date = new Date();
            // 2021.06.30 20:22:00
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.CHINA);
            String format = simpleDateFormat.format(date);
            if (mOnClearListener != null) {
                mOnClearListener.onClear(format);
            }
            dismiss();
        });

        // 设置外部可以取消
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public interface OnClearListener {
        void onClear(String time);
    }

    private OnClearListener mOnClearListener;

    public void setOnCheckedChangeListener(OnClearListener onClearListener) {
        mOnClearListener = onClearListener;
    }

}
