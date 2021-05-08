package com.chinafocus.hvrskyworthvr.util.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.chinafocus.hvrskyworthvr.R;

import java.text.DecimalFormat;

/**
 * 带百分比的圆环进度条
 */
@SuppressWarnings("all")
public class ProgressChart extends View {

    private Context context;

    //圆环背景画笔
    private Paint paintBg;

    //圆环进度画笔
    private Paint paintProgress;

    //文字画笔1
    private Paint paintText1;

    //文字画笔2
    private Paint paintText2;

    // 圆环的宽度
    private float progressWidth;

    //圆环的区域
    private RectF roundRect;

    //文字的区域
    private Rect textRect;

    //单个字符的区域
    private Rect charRect;

    //绘制时每次增加的度数
    private float rotateDegree = 1.0F;

    //绘制开始的度数
    private float startDegree = 0.0F;

    //结束的度数
    private float endDegree;

    //背景颜色
    private int bgColor;

    //进度颜色
    private int progressColor;

    // 中间进度百分比的字符串的颜色
    private int textColor;

    //字符串的文字大小
    private float text1Size;

    //字符串的文字大小
    private float text2Size;

    //绘制的字符串
    private String text = "0%";


    public ProgressChart(Context context) {
        this(context, null);
    }

    public ProgressChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressChart);

        this.progressWidth = mTypedArray.getDimension(R.styleable.ProgressChart_progressWidth, 8);
        this.text1Size = mTypedArray.getDimension(R.styleable.ProgressChart_textSize1, 32);
        this.text2Size = mTypedArray.getDimension(R.styleable.ProgressChart_textSize2, 20);
        this.bgColor = mTypedArray.getColor(R.styleable.ProgressChart_bgColor, Color.parseColor("#fff2f2f2"));
        this.progressColor = mTypedArray.getColor(R.styleable.ProgressChart_progressColor, Color.parseColor("#fffd0000"));
        this.textColor = mTypedArray.getColor(R.styleable.ProgressChart_txtColor, Color.parseColor("#fffd0000"));

        mTypedArray.recycle();

        this.textRect = new Rect();
        this.charRect = new Rect();

        this.paintBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintBg.setStyle(Paint.Style.STROKE);
        this.paintBg.setStrokeWidth(this.progressWidth);
        this.paintBg.setColor(this.bgColor);

        this.paintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintProgress.setStyle(Paint.Style.STROKE);
        this.paintProgress.setStrokeWidth(this.progressWidth);
        this.paintProgress.setColor(this.progressColor);


        this.paintText1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintText1.setTextSize(this.text1Size);
        this.paintText1.setTextAlign(Paint.Align.CENTER);
        this.paintText1.setColor(this.textColor);

        this.paintText2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintText2.setTextSize(this.text2Size);
        this.paintText2.setTextAlign(Paint.Align.CENTER);
        this.paintText2.setColor(this.textColor);
    }

    public void setProgress(String progress) {
        this.text = DecimalFormat.getPercentInstance().format(Double.valueOf(progress));
        this.startDegree = 0.0F;
        this.endDegree = (360.0F * Float.parseFloat(progress));
        this.rotateDegree = (this.endDegree / 40.0F);

        invalidate();
    }

    public void setProgress(float progress) {
        this.text = DecimalFormat.getPercentInstance().format(Double.valueOf(progress));
        this.startDegree = 0.0F;
        this.endDegree = (360.0F * progress);
        this.rotateDegree = (this.endDegree / 40.0F);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制圆环背景
        canvas.drawArc(this.roundRect, 0.0F, 360.0F, false, this.paintBg);

        //绘制进度
        if (this.startDegree < this.endDegree) {
            canvas.drawArc(this.roundRect, -90.0F, this.startDegree, false, this.paintProgress);
            this.startDegree += this.rotateDegree;
            invalidate();
        } else {
            canvas.drawArc(this.roundRect, -90.0F, this.endDegree, false, this.paintProgress);
        }

        if (!TextUtils.isEmpty(this.text)) {
            //绘制文字
            this.paintText1.getTextBounds(this.text, 0, this.text.length(), this.textRect);
            this.paintText2.getTextBounds("%", 0, 1, this.charRect);

            FontMetricsInt fontMetricsInt = this.paintText1.getFontMetricsInt();
            float y = this.roundRect.top + (this.roundRect.bottom - this.roundRect.top - fontMetricsInt.bottom + fontMetricsInt.top) / 2.0F - fontMetricsInt.top;
            canvas.drawText(this.text.replace("%", ""), this.roundRect.centerX() - (this.charRect.width() >> 1), y, this.paintText1);
            canvas.drawText("%", this.roundRect.centerX() + (this.textRect.width() >> 1) - (this.charRect.width() >> 1), y, this.paintText2);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.roundRect = new RectF(this.progressWidth, this.progressWidth, w - this.progressWidth, h - this.progressWidth);
    }


}

