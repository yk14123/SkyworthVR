package com.chinafocus.hvrskyworthvr.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.chinafocus.hvrskyworthvr.R;

import java.util.Objects;

/**
 * 用户权限确认框
 */
public class VideoDetailDialog extends Dialog {
    // 标题
    private TextView mTvTitle;
    // 提示文本
    private TextView mTvMessage;

    public VideoDetailDialog(Context context) {
        super(context, R.style.VideoDetail_TranslucentGreyDialog);
        init(context);
    }

    private void init(Context context) {
        View mContentView = LayoutInflater.from(context).inflate(R.layout.dialog_video_detail, null);
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        // 全新的方式设定宽高！
        setContentView(mContentView, new ViewGroup.LayoutParams(screenWidth, screenHeight));

        mContentView.findViewById(R.id.iv_dialog_info_close).setOnClickListener(v -> dismiss());

        mTvTitle = mContentView.findViewById(R.id.tv_dialog_info_title);
        mTvMessage = mContentView.findViewById(R.id.tv_dialog_info_message);
        mTvMessage.setMovementMethod(ScrollingMovementMethod.getInstance());

        // 设置外部可以取消
        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setMessage(String message) {
        mTvMessage.setText(message);
    }

}
