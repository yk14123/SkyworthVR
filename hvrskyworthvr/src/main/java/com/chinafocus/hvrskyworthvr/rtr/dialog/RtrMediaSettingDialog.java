package com.chinafocus.hvrskyworthvr.rtr.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.VolumeUtils;
import com.chinafocus.hvrskyworthvr.R;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static com.chinafocus.hvrskyworthvr.global.Constants.BRIGHTNESS_CURRENT_STATUS;
import static com.chinafocus.hvrskyworthvr.global.Constants.VOLUME_CURRENT_STATUS;

public class RtrMediaSettingDialog extends AppCompatDialog implements View.OnClickListener {

    private AppCompatSeekBar volumeDrag;
    private AppCompatSeekBar brightnessDrag;

    public RtrMediaSettingDialog(@NonNull Context context) {
        super(context, R.style.VrMediaSettingDialog);
        init(context);
    }

    private void init(Context context) {
        @SuppressLint("InflateParams")
        View mContentView = LayoutInflater.from(context).inflate(R.layout.rtr_dialog_media_setting, null);
        setContentView(mContentView);

        mContentView.findViewById(R.id.ib_click_volume_down).setOnClickListener(this);
        mContentView.findViewById(R.id.ib_click_volume_up).setOnClickListener(this);

        volumeDrag = mContentView.findViewById(R.id.sb_volume);
        volumeDrag.setMax(15);
        int volume = SPUtils.getInstance().getInt(VOLUME_CURRENT_STATUS, 0);
        volumeDrag.setProgress(volume);
        volumeDrag.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                VolumeUtils.setVolume(AudioManager.STREAM_MUSIC, progress, FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                SPUtils.getInstance().put(VOLUME_CURRENT_STATUS, progress);
            }
        });

        mContentView.findViewById(R.id.ib_click_brightness_down).setOnClickListener(this);
        mContentView.findViewById(R.id.ib_click_brightness_up).setOnClickListener(this);

        brightnessDrag = mContentView.findViewById(R.id.sb_brightness);
        brightnessDrag.setMax(255);
        int brightness = SPUtils.getInstance().getInt(BRIGHTNESS_CURRENT_STATUS, 0);
        brightnessDrag.setProgress(brightness);
        brightnessDrag.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BrightnessUtils.setBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                SPUtils.getInstance().put(BRIGHTNESS_CURRENT_STATUS, progress);
            }
        });

        // 设置外部可以取消
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_click_volume_up) {
            changeVolume(2);
        } else if (id == R.id.ib_click_volume_down) {
            changeVolume(-2);
        } else if (id == R.id.ib_click_brightness_up) {
            changeBrightness(20);
        } else if (id == R.id.ib_click_brightness_down) {
            changeBrightness(-20);
        }
    }

    private void changeVolume(int temp) {
        int volume = SPUtils.getInstance().getInt(VOLUME_CURRENT_STATUS, 0);
        volume += temp;
        if (volume <= 0) {
            volume = 0;
        } else if (volume >= 15) {
            volume = 15;
        }
        VolumeUtils.setVolume(AudioManager.STREAM_MUSIC, volume, FLAG_PLAY_SOUND);
        SPUtils.getInstance().put(VOLUME_CURRENT_STATUS, volume);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            volumeDrag.setProgress(volume, true);
        } else {
            volumeDrag.setProgress(volume);
        }
    }

    private void changeBrightness(int temp) {
        int brightness = SPUtils.getInstance().getInt(BRIGHTNESS_CURRENT_STATUS, 0);
        brightness += temp;
        if (brightness <= 0) {
            brightness = 0;
        } else if (brightness >= 255) {
            brightness = 255;
        }
        BrightnessUtils.setBrightness(brightness);
        SPUtils.getInstance().put(BRIGHTNESS_CURRENT_STATUS, brightness);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            brightnessDrag.setProgress(brightness, true);
        } else {
            brightnessDrag.setProgress(brightness);
        }
    }

}
