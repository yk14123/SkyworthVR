package com.chinafocus.hvrskyworthvr.rtr.dialog;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.chinafocus.hvrskyworthvr.R;

import static com.chinafocus.hvrskyworthvr.global.Constants.VIDEO_UPDATE_TIME_TASK_END;
import static com.chinafocus.hvrskyworthvr.global.Constants.VIDEO_UPDATE_TIME_TASK_START;


public class RtrTimeTaskDialog extends AppCompatDialog {

    private View mContentView;
    private NumberPicker mPickerStart;
    private NumberPicker mPickerEnd;

    private String[] times = {
            "01: 00", "02: 00", "03: 00", "04: 00", "05: 00", "06: 00",
            "07: 00", "08: 00", "09: 00", "10: 00", "11: 00", "12: 00",
            "13: 00", "14: 00", "15: 00", "16: 00", "17: 00", "18: 00",
            "19: 00", "20: 00", "21: 00", "22: 00", "23: 00", "00: 00"};

    public RtrTimeTaskDialog(@NonNull Context context) {
        super(context, R.style.VrModeMainDialog);
        init(context);
    }

    private void init(Context context) {
        mContentView = LayoutInflater.from(context).inflate(R.layout.rtr_dialog_video_update_time_task, null);
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        // 全新的方式设定宽高！
        setContentView(mContentView, new ViewGroup.LayoutParams(screenWidth, screenHeight));

        mContentView.findViewById(R.id.bt_video_update_time_task_cancel).setOnClickListener(v -> dismiss());

        initStartTimePicker();
        initEndTimePicker();

        initTime();

        mContentView.findViewById(R.id.bt_video_update_time_task_ok).setOnClickListener(v -> {
            if (mPickerStart.getValue() != mPickerEnd.getValue()) {
                SPUtils.getInstance().put(VIDEO_UPDATE_TIME_TASK_START, mPickerStart.getValue());
                SPUtils.getInstance().put(VIDEO_UPDATE_TIME_TASK_END, mPickerEnd.getValue());
                dismiss();
            }
        });

        // 设置外部可以取消
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    private void initEndTimePicker() {
        mPickerEnd = mContentView.findViewById(R.id.time_picker_end);
        mPickerEnd.setDisplayedValues(times);
        mPickerEnd.setMinValue(1);
        mPickerEnd.setMaxValue(times.length);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mPickerEnd.setTextSize(40);
        }
        // 阻止子View获取焦点，这样内部子View就无法触发弹窗
        mPickerEnd.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    private void initStartTimePicker() {
        mPickerStart = mContentView.findViewById(R.id.time_picker_start);
        mPickerStart.setDisplayedValues(times);
        mPickerStart.setMinValue(1);
        mPickerStart.setMaxValue(times.length);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mPickerStart.setTextSize(40);
        }
        mPickerStart.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    public void show() {
        initTime();
        super.show();
    }

    private void initTime() {
        int startTime = SPUtils.getInstance().getInt(VIDEO_UPDATE_TIME_TASK_START, 1);
        int endTime = SPUtils.getInstance().getInt(VIDEO_UPDATE_TIME_TASK_END, 8);
        mPickerStart.setValue(startTime);
        mPickerEnd.setValue(endTime);
    }
}
