package com.chinafocus.hvrskyworthvr.ui.widget.transformer;

import android.view.View;

import androidx.annotation.FloatRange;

import com.yarolegovich.discretescrollview.transform.DiscreteScrollItemTransformer;
import com.yarolegovich.discretescrollview.transform.Pivot;

/**
 * Created by yksm on 02.06.2021.
 * <p>
 * 一屏5个item。中间item放大。两边item变小。并且所有间距始终保持一致！
 * <p>
 * UI上，中心大Item宽 = W
 * UI上，其他位置的小Item宽 = w
 * UI上，小Item之间的间距 = v
 * XML中，Item 的 layout_width 宽设置为 (w+v)
 * minScale = w/(w+v) >>> 通过minScale可以确保每一个小item的间距一致
 * maxScale = W/(w+v) >>>
 * 左右两边的Item，需要往各自的方向位移 TranslationX = (W-w)/2 也等于 (w+v)(maxScale-minScale)/2
 */
public class MyCenterScaleTransformer implements DiscreteScrollItemTransformer {

    private Pivot pivotX;
    private Pivot pivotY;
    // （item的宽+间距）/ item的宽
    private float minScale;
    private float maxMinDiff;

    public MyCenterScaleTransformer() {
        pivotX = Pivot.X.CENTER.create();
        pivotY = Pivot.Y.CENTER.create();
        minScale = 0.8f;
        maxMinDiff = 0.2f;
    }

    // 左侧Item往左缩进范围为负
    private float left_TranslationX;
    // 右侧Item往右缩进范围为正
    private float right_TranslationX;

    private boolean isCalculateTranslationX;

    @Override
    public void transformItem(View item, float position) {

        if (!isCalculateTranslationX) {
            isCalculateTranslationX = true;
            right_TranslationX = item.getWidth() * maxMinDiff / 2;
            left_TranslationX = -right_TranslationX;
        }

        pivotX.setOn(item);
        pivotY.setOn(item);
        float closenessToCenter = 1f - Math.abs(position);
        float scale = minScale + maxMinDiff * closenessToCenter;
        item.setScaleX(scale);
        item.setScaleY(scale);

        if (position <= 0) {
            // -1 - 0 之间
            float translationXFactor = left_TranslationX + (1 + position) * -left_TranslationX;
            item.setTranslationX(translationXFactor);
        } else if (position <= 1) {
            // 在 0 - 1 之间
            float translationXFactor = position * right_TranslationX;
            item.setTranslationX(translationXFactor);
        }

    }

    public static class Builder {

        private MyCenterScaleTransformer transformer;
        private float maxScale;

        public Builder() {
            transformer = new MyCenterScaleTransformer();
            maxScale = 1f;
        }

        public Builder setMinScale(@FloatRange(from = 0.01) float scale) {
            transformer.minScale = scale;
            return this;
        }

        public Builder setMaxScale(@FloatRange(from = 0.01) float scale) {
            maxScale = scale;
            return this;
        }

        public Builder setPivotX(Pivot.X pivotX) {
            return setPivotX(pivotX.create());
        }

        public Builder setPivotX(Pivot pivot) {
            assertAxis(pivot, Pivot.AXIS_X);
            transformer.pivotX = pivot;
            return this;
        }

        public Builder setPivotY(Pivot.Y pivotY) {
            return setPivotY(pivotY.create());
        }

        public Builder setPivotY(Pivot pivot) {
            assertAxis(pivot, Pivot.AXIS_Y);
            transformer.pivotY = pivot;
            return this;
        }

        public MyCenterScaleTransformer build() {
            transformer.maxMinDiff = maxScale - transformer.minScale;

            return transformer;
        }

        private void assertAxis(Pivot pivot, @Pivot.Axis int axis) {
            if (pivot.getAxis() != axis) {
                throw new IllegalArgumentException("You passed a Pivot for wrong axis.");
            }
        }
    }
}
