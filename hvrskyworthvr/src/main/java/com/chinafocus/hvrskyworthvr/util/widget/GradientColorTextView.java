package com.chinafocus.hvrskyworthvr.util.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class GradientColorTextView extends AppCompatTextView {

    private LinearGradient mLinearGradient;
    private Paint mPaint;
    private Rect mTextBound = new Rect();
    boolean isLinearGradientCreate;

    public GradientColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isLinearGradientCreate) {
            mLinearGradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,
                    new int[]{0xFF14C27B, 0xFFB6FFE2},
                    null, Shader.TileMode.REPEAT);
            isLinearGradientCreate = true;
        }
        mPaint = getPaint();
        String mTipText = getText().toString();
        mPaint.getTextBounds(mTipText, 0, mTipText.length(), mTextBound);
        mPaint.setShader(mLinearGradient);
        canvas.drawText(mTipText, (getMeasuredWidth() >> 1) - (mTextBound.width() >> 1), (getMeasuredHeight() >> 1) + (mTextBound.height() >> 1), mPaint);
    }
}
