package com.chinafocus.hvrskyworthvr.rtr.playcount;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.rtr.dialog.RtrVideoPlayCountDialog;
import com.chinafocus.hvrskyworthvr.util.FileContentUtil;
import com.chinafocus.hvrskyworthvr.util.statusbar.StatusBarCompatFactory;

import java.text.MessageFormat;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.chinafocus.hvrskyworthvr.global.Constants.VIDEO_PLAY_COUNT_CLEAR_TIME;

public class VideoPlayCountActivity extends AppCompatActivity {

    private RtrVideoPlayCountDialog mRtrVideoPlayCountDialog;
    private AppCompatTextView tvVideoPlayCountSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarCompatFactory.getInstance().setStatusBarImmerse(this, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play_count);

        findViewById(R.id.iv_video_update_back).setOnClickListener(v -> finish());
        tvVideoPlayCountSize = findViewById(R.id.tv_video_play_count_size);
        initVideoCount();

        AppCompatTextView clearTime = findViewById(R.id.tv_video_play_count_clear_time);
        String time = SPUtils.getInstance().getString(VIDEO_PLAY_COUNT_CLEAR_TIME);
        if (TextUtils.isEmpty(time)) {
            clearTime.setVisibility(View.GONE);
        } else {
            clearTime.setVisibility(View.VISIBLE);
            clearTime.setText(MessageFormat.format("(上次清空时间：{0})", time));
        }

        findViewById(R.id.bt_video_play_count_clear).setOnClickListener(v -> {
            if (mRtrVideoPlayCountDialog == null) {
                mRtrVideoPlayCountDialog = new RtrVideoPlayCountDialog(VideoPlayCountActivity.this);
                mRtrVideoPlayCountDialog.setOnCheckedChangeListener(s -> {

                    clearTime.setVisibility(View.VISIBLE);
                    clearTime.setText(MessageFormat.format("(上次清空时间：{0})", s));
                    SPUtils.getInstance().put(VIDEO_PLAY_COUNT_CLEAR_TIME, s);

                    tvVideoPlayCountSize.setText(MessageFormat.format("{0}次", 0));
                    FileContentUtil.writeCount(getApplicationContext(), "VideoCount.txt", "0");
                });
            }
            if (!mRtrVideoPlayCountDialog.isShowing()) {
                mRtrVideoPlayCountDialog.show();
            }
        });

    }

    private void initVideoCount() {
        Observable.fromCallable(() -> FileContentUtil.readCount(getApplicationContext(), "VideoCount.txt"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> tvVideoPlayCountSize.setText(MessageFormat.format("{0}次", s)))
                .subscribe();
    }
}