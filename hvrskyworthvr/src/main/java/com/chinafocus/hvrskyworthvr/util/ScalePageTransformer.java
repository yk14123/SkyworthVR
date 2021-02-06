package com.chinafocus.hvrskyworthvr.util;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

/**
 * 现在存在 min 到 max 之间任意一点，计算该点在 min 到 max 距离的100%位置
 * 比如 1 - 5 。如 pos = 1 则距离比值 = 0 如 pos = 3 则距离比值 = 50% 如 pos = 5 则距离比值 = 100%
 * 距离比值公式 ：
 * （pos - min）/（max - min） = 距离比值
 */
public class ScalePageTransformer implements ViewPager2.PageTransformer {

    //当前显示页面缩放比例
    private static final float MAX_SCALE = 1.0f;
    //两侧页面的缩放比例
    private static final float MIN_SCALE = 0.75f;

    // 左侧页面往右缩进范围
    private float MIN_TranslationX;
    // 当前页面保持中心位置不缩进
    private static final float TranslationX = 0f;
    // 右侧页面往左缩进范围
    private float MAX_TranslationX;


    // ViewPager设置padding后，MIN 和 MAX 从原来的 -1 0 1 变成了 padding后的 MIN 和 MAX
    private float MIN = -0.541f;
    private float MAX = 1.459f;

    public ScalePageTransformer(float MIN, float MAX, float translationX) {
        this.MIN = MIN;
        this.MAX = MAX;
        this.MIN_TranslationX = translationX;
        this.MAX_TranslationX = -translationX;
    }

    /**
     * 通过转换公式把 ViewPager 两边 padding 后的 position 转换为 相对于原来 -1 0 1 之间的position！
     *
     * @param position
     * @return
     */
    private float transformPosition(float position) {
        // TODO srcPos 为 position 在 MIN-MAX 段中的距离比值
        float fraction = (position - MIN) / (MAX - MIN);
        // TODO 返回在，MIN = -1 MAX = 1 段中的具体pos
//        return (1 - (-1)) * fraction + (-1);
        return (2 - (-2)) * fraction + (-2);
//        return ((position - MIN) / (MAX - MIN)) * 2 - 1;
    }

    @Override
    public void transformPage(View view, float position) {

//        position = ((position - MIN) / (MAX - MIN)) * 2 - 1;
        position = transformPosition(position);

//        int tag = (int) view.getTag();
//        Log.i("tag", "tag" + tag + "===============" + "position" + position);
        // TODO 完美补偿了在 -2 到 -1 之间的图片追加位置
        float v = view.getMeasuredWidth() * (1f - MIN_SCALE) / 2;

        //setScaleY只支持api11以上
        if (position < -2) {
            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);
            view.setTranslationX(2 * MIN_TranslationX + v);

        } else if (position <= -1) {
            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);

            float translationXFactor = (2 * MIN_TranslationX + v) + (2 + position) * (MIN_TranslationX - (2 * MIN_TranslationX + v));
            view.setTranslationX(translationXFactor);

        } else if (position <= 0) {
            // -1 - 0 之间
//            float scaleFactor = MIN_SCALE + (1 - Math.abs(position)) * (MAX_SCALE - MIN_SCALE);
            float scaleFactor = MIN_SCALE + (1 + position) * (MAX_SCALE - MIN_SCALE);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            float translationXFactor = MIN_TranslationX + (1 + position) * (TranslationX - MIN_TranslationX);
            view.setTranslationX(translationXFactor);

        } else if (position < 1) {
            // 在 0 - 1 之间
//            float scaleFactor = MIN_SCALE + (1 - Math.abs(position)) * (MAX_SCALE - MIN_SCALE);
            float scaleFactor = MAX_SCALE + position * (MIN_SCALE - MAX_SCALE);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            float translationXFactor = TranslationX + position * (MAX_TranslationX - TranslationX);
            view.setTranslationX(translationXFactor);

        } else if (position < 2) {

            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);

            float translationXFactor = MAX_TranslationX + (position - 1) * (2 * MAX_TranslationX - v - MAX_TranslationX);
            view.setTranslationX(translationXFactor);

        } else {  // (2,+Infinity]
            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);

            view.setTranslationX(2 * MAX_TranslationX - v);
        }

    }
}