package com.chinafocus.hvrskyworthvr.util.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.util.VerticalImageSpan;

import java.util.List;

/**
 * 自定义标签View
 */
public class TagTextView extends AppCompatTextView {

    public TagTextView(Context context) {
        super(context);
    }

    public TagTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setContentAndTag(String content, List<String> tags, String color) {
        StringBuffer content_buffer = new StringBuffer();
        for (String item : tags) {
            // 将每个tag的内容添加到content后边，之后将用drawable替代这些tag所占的位置
            content_buffer.append(item);
        }
        content_buffer.append(content);
        SpannableString spannableString = new SpannableString(content_buffer);
        for (int i = 0; i < tags.size(); i++) {
            String item = tags.get(i);
            // R.layout.tag是每个标签的布局
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(getContext()).inflate(R.layout.tag, null);
            // 部分机型会因为没有LayoutParams，而报NPE异常,所以这里设置一下
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView tv_tag = view.findViewById(R.id.tv_tag);
            tv_tag.setText(item);
            // 自定义圆角颜色
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.parseColor(color));
            gradientDrawable.setCornerRadius(10.f);
            tv_tag.setBackground(gradientDrawable);

            Bitmap bitmap = convertViewToBitmap(view);
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            d.setBounds(0, 0, tv_tag.getWidth(), tv_tag.getHeight());//缺少这句的话，不会报错，但是图片不回显示
            VerticalImageSpan span = new VerticalImageSpan(d);//图片将对齐底部边线
            int startIndex;
            int endIndex;
            startIndex = getLastLength(tags, i);
            endIndex = startIndex + item.length();
            spannableString.setSpan(span, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(spannableString);
    }

    /**
     * buildDrawingCache 废弃了。使用Bitmap.createBitmap代替
     *
     * @param view view
     * @return bitmap
     */
    private static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);
        view.draw(canvas);
        return bitmap;
    }

    private int getLastLength(List<String> list, int maxLength) {
        int length = 0;
        for (int i = 0; i < maxLength; i++) {
            length += list.get(i).length();
        }
        return length;
    }
}