package com.example.piramidnull;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        page.setTranslationX(-position * page.getWidth() * 0.25f);

        if (position < -1 || position > 1) {
            page.setAlpha(0f);
        } else {
            float scale = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float alpha = Math.max(MIN_ALPHA, 1 - Math.abs(position));
            page.setScaleX(scale);
            page.setScaleY(scale);
            page.setAlpha(alpha);
        }
    }
}