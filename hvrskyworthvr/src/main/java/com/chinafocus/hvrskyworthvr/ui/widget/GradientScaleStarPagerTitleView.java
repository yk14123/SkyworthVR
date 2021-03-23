package com.chinafocus.hvrskyworthvr.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.chinafocus.hvrskyworthvr.R;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

public class GradientScaleStarPagerTitleView extends FrameLayout implements IPagerTitleView {

    private float mMinScale = 0.85f;
    private AppCompatTextView mTvPageTitle;
    private AppCompatImageView mIvStar;

    public GradientScaleStarPagerTitleView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.rtr_main_category_star, this);
        mTvPageTitle = findViewById(R.id.tv_page_title);
        mIvStar = findViewById(R.id.iv_page_star);
        int padding = UIUtil.dip2px(context, 5);
        setPadding(padding, padding, padding, padding);
    }

    public void setTextSize(int size) {
        mTvPageTitle.setTextSize(size);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        setScaleX(mMinScale + (1.0f - mMinScale) * enterPercent);
        setScaleY(mMinScale + (1.0f - mMinScale) * enterPercent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        setScaleX(1.0f + (mMinScale - 1.0f) * leavePercent);
        setScaleY(1.0f + (mMinScale - 1.0f) * leavePercent);
    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }

    @Override
    public void onSelected(int index, int totalCount) {
        LinearGradient linearGradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,
                Color.parseColor("#FFFFDC8E"),
                Color.parseColor("#FFFBCB61"),
                Shader.TileMode.CLAMP);
        mTvPageTitle.getPaint().setShader(linearGradient);
        mTvPageTitle.invalidate();

        mIvStar.setVisibility(VISIBLE);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        mTvPageTitle.getPaint().setShader(null);
        mTvPageTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.color_white_a60));
        mTvPageTitle.invalidate();

        mIvStar.setVisibility(GONE);
    }

    public void setText(String string) {
        mTvPageTitle.setText(string);
    }
}
