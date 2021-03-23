package com.chinafocus.hvrskyworthvr.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.chinafocus.hvrskyworthvr.R;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

import java.util.Objects;

public class GradientScaleTransitionPagerTitleView extends AppCompatTextView implements IPagerTitleView {

    private float mMinScale = 0.85f;

    public GradientScaleTransitionPagerTitleView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER);
        int padding = UIUtil.dip2px(context, 5);
        setPadding(padding, 0, padding, 0);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.END);
        setIncludeFontPadding(false);
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
        getPaint().setShader(linearGradient);

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.rtr_main_category_logo);
        Objects.requireNonNull(drawable).setBounds(0, 0, 30, 30);
        setCompoundDrawables(null, null, drawable, null);

        invalidate();
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        getPaint().setShader(null);
        setTextColor(ContextCompat.getColor(getContext(), R.color.color_white_a60));
        setCompoundDrawables(null, null, null, null);
        invalidate();
    }

}
