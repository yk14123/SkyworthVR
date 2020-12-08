package com.chinafocus.hvrskyworthvr.exo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.blankj.utilcode.util.BarUtils;
import com.chinafocus.hvrskyworthvr.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @date 2020/1/8
 * description：
 */
public class VideoRatioLangView extends FrameLayout implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup mRatioGroup;
    private RadioGroup mLangGroup;

    private IVideoInfoChangeListener mVideoInfoChangeListener;
    private List<VideoInfoHolder> mRatioHolders;
    private List<VideoInfoHolder> mLangHolders;
    private ConstraintLayout mConstraintLayout;
    private ConstraintSet mLandConstraintSet;
    private ConstraintSet mProConstraintSet;

    public VideoRatioLangView(@NonNull Context context) {
        this(context, null);
    }

    public VideoRatioLangView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoRatioLangView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
//        LayoutInflater.from(context).inflate(R.layout.exo_video_ratio_lang, this);
//
//        setOnClickListener(v -> setVisibility(View.GONE));
//
//        mConstraintLayout = findViewById(R.id.csl_root_video_info);
//        mConstraintLayout.setPadding(0, 0, 0, BarUtils.getStatusBarHeight() / 2);
//
//        mRatioGroup = findViewById(R.id.rg_video_info_ratio);
//        mRatioGroup.setOnCheckedChangeListener(this);
//
//        mLangGroup = findViewById(R.id.rg_video_info_lang);
//        mLangGroup.setOnCheckedChangeListener(this);

    }

    /**
     * 横屏模式下，靠边展示
     */
    public void setLandConstraintLayout() {
        if (mLandConstraintSet == null) {
            mLandConstraintSet = new ConstraintSet();
        }
        LayoutParams layoutParams = (LayoutParams) mConstraintLayout.getLayoutParams();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.MATCH_PARENT;
        mConstraintLayout.setLayoutParams(layoutParams);

        mLandConstraintSet.clone(mConstraintLayout);
//        mLandConstraintSet.setHorizontalBias(R.id.rg_video_info_ratio, 0.9f);
//        mLandConstraintSet.setHorizontalBias(R.id.rg_video_info_lang, 0.9f);
        mLandConstraintSet.applyTo(mConstraintLayout);
    }

    /**
     * 竖屏模式下，居中展示
     */
    public void setProConstraintLayout() {
        if (mProConstraintSet == null) {
            mProConstraintSet = new ConstraintSet();
        }
        LayoutParams layoutParams = (LayoutParams) mConstraintLayout.getLayoutParams();
        layoutParams.width = LayoutParams.MATCH_PARENT;
//        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.dp_400);
        mConstraintLayout.setLayoutParams(layoutParams);

        mProConstraintSet.clone(mConstraintLayout);
//        mProConstraintSet.setHorizontalBias(R.id.rg_video_info_ratio, 0.5f);
//        mProConstraintSet.setHorizontalBias(R.id.rg_video_info_lang, 0.5f);
        mProConstraintSet.applyTo(mConstraintLayout);
    }


    public void setVideoInfoChangeListener(IVideoInfoChangeListener videoInfoChangeListener) {
        mVideoInfoChangeListener = videoInfoChangeListener;
    }

    public void showRatioView() {
        if (!isShowVideoRatio) {
            return;
        }

        mRatioGroup.setVisibility(View.VISIBLE);
        mLangGroup.setVisibility(View.GONE);

        controlViewDisplay();
    }

    public void showLangView() {
        if (!isShowVideoLang) {
            return;
        }

        mRatioGroup.setVisibility(View.GONE);
        mLangGroup.setVisibility(View.VISIBLE);

        controlViewDisplay();
    }


    private void controlViewDisplay() {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == currentRatioId || checkedId == currentLangId) {
            return;
        }


//        String preRatio = currentRatioName;
//        String preLang = currentLangName;

        if (group == mRatioGroup) {

            for (VideoInfoHolder ratioHolder : mRatioHolders) {
                if (ratioHolder.viewId == checkedId) {
//                    ratio = ratioHolder.value;
                    currentRatioName = ratioHolder.value;
                    currentRatioId = checkedId;
                    if (mVideoInfoChangeListener != null) {
                        mVideoInfoChangeListener.onVideoRatioChange(currentRatioName);
                    }
                    break;
                }
            }

        } else if (group == mLangGroup) {
            for (VideoInfoHolder langHolder : mLangHolders) {
                if (langHolder.viewId == checkedId) {
//                    lang = langHolder.value;
                    currentLangName = langHolder.value;
                    currentLangId = checkedId;
                    if (mVideoInfoChangeListener != null) {
                        mVideoInfoChangeListener.onVideoLangChange(currentLangName);
                    }
                    break;
                }
            }
        }

        if (currentRatioId != 0 && currentLangId != 0) {

//            for (VideoDetailInfoNew.VideoDetailListBean temp : mVideoDetailList) {
//                if (temp.getRatio().equals(currentRatioName) && temp.getLangName().equals(currentLangName)) {
//                    if (mVideoInfoChangeListener != null) {
//                        Log.e("MyLog", "onVideoInfoChange当前视频 ratio >>> " + currentRatioName
//                                + " lang >>> " + currentLangName);
//                        mVideoInfoChangeListener.onVideoInfoChange(temp.getFormat(), temp.getVideoUrl(), temp.getSubTitleUrl());
//                    }
//                    break;
//                }
//            }
        }

        setVisibility(View.GONE);

    }

    private int currentRatioId;
    private int currentLangId;

    private String currentRatioName;
    private String currentLangName;

    private boolean isShowVideoRatio;
    private boolean isShowVideoLang;

//    private List<VideoDetailInfoNew.VideoDetailListBean> mVideoDetailList;

//    public void bindRatioAndLang(List<VideoDetailInfoNew.VideoDetailListBean> videoDetailList) {
//        if (mVideoDetailList == null) {
//            mVideoDetailList = new ArrayList<>();
//        }
//
//        mVideoDetailList.clear();
//        mVideoDetailList.addAll(videoDetailList);
//
//        List<String> ratio = new ArrayList<>();
//        List<String> lang = new ArrayList<>();
//
//        if (mRatioHolders == null) {
//            mRatioHolders = new ArrayList<>();
//        }
//        mRatioHolders.clear();
//        if (mLangHolders == null) {
//            mLangHolders = new ArrayList<>();
//        }
//        mLangHolders.clear();
//
//        mRatioGroup.removeAllViews();
//        mLangGroup.removeAllViews();
//
//        for (VideoDetailInfoNew.VideoDetailListBean temp : mVideoDetailList) {
//            if (!ratio.contains(temp.getRatio())) {
//                ratio.add(temp.getRatio());
//                VideoInfoHolder videoInfoHolder = new VideoInfoHolder();
//                videoInfoHolder.value = temp.getRatio();
//                videoInfoHolder.viewId = View.generateViewId();
//                mRatioHolders.add(videoInfoHolder);
//            }
//            if (!lang.contains(temp.getLangName())) {
//                lang.add(temp.getLangName());
//                VideoInfoHolder videoInfoHolder = new VideoInfoHolder();
//                videoInfoHolder.value = temp.getLangName();
//                videoInfoHolder.viewId = View.generateViewId();
//                videoInfoHolder.tag = temp.getLang();
//                mLangHolders.add(videoInfoHolder);
//            }
//        }
//
//        if (mRatioHolders.size() == 0) {
//            isShowVideoRatio = false;
//        } else {
//            isShowVideoRatio = true;
//            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.dp_60), getResources().getDimensionPixelOffset(R.dimen.dp_50));
//            for (int i = 0; i < mRatioHolders.size(); i++) {
//                RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.exo_video_info_item, null);
//                radioButton.setText(mRatioHolders.get(i).value);
//                radioButton.setId(mRatioHolders.get(i).viewId);
//                mRatioGroup.addView(radioButton, layoutParams);
//            }
//        }
//
//        if (mLangHolders.size() == 0) {
//            isShowVideoLang = false;
//        } else {
//            isShowVideoLang = true;
//            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.dp_60), getResources().getDimensionPixelOffset(R.dimen.dp_50));
//            for (int i = 0; i < mLangHolders.size(); i++) {
//                RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.exo_video_info_item, null);
//                radioButton.setText(mLangHolders.get(i).value);
//                radioButton.setId(mLangHolders.get(i).viewId);
//                mLangGroup.addView(radioButton, layoutParams);
//            }
//        }
//    }

    public void initRatioLangCheckStatus(String ratio, String lang) {
        currentRatioId = 0;
        currentLangId = 0;

        for (VideoInfoHolder ratioHolder : mRatioHolders) {
            if (ratioHolder.value.equalsIgnoreCase(ratio)) {
                mRatioGroup.check(ratioHolder.viewId);
            }
        }

        for (VideoInfoHolder langHolder : mLangHolders) {
            if (langHolder.value.equalsIgnoreCase(lang)) {
                mLangGroup.check(langHolder.viewId);
            }
        }
    }


    public String getLangCheckStatus() {
        for (VideoInfoHolder langHolder : mLangHolders) {
            if (langHolder.viewId == mLangGroup.getCheckedRadioButtonId()) {
                return langHolder.value;
            }
        }
        return null;
    }

    public String getLangTagCheckStatus() {
        for (VideoInfoHolder langHolder : mLangHolders) {
            if (langHolder.viewId == mLangGroup.getCheckedRadioButtonId()) {
                return langHolder.tag;
            }
        }
        return null;
    }

    public String getRatioCheckStatus() {
        for (VideoInfoHolder langHolder : mRatioHolders) {
            if (langHolder.viewId == mRatioGroup.getCheckedRadioButtonId()) {
                return langHolder.value;
            }
        }
        return null;
    }

    static class VideoInfoHolder {
        private String value;
        private String tag;
        private int viewId;
    }
}
