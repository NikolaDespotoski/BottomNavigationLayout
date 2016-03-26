/*
 * BottomNavigationLayout library for Android
 * Copyright (c) 2016. Nikola Despotoski (http://github.com/NikolaDespotoski).
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package despotoski.nikola.github.com.bottomnavigationlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.widget.ImageView;

/**
 * Created by Nikola on 3/23/2016.
 */
public final class BottomTabletNavigationTextView extends ImageView implements BottomNavigation {


    private static final long ANIMATION_DURATION = 200;
    private int mIcon;
    private int mTextActiveColorFilter = Color.WHITE;
    private boolean previouslySelected = false;
    private int mViewTopPaddingInactive;
    private int mViewTopPaddingActive;
    private Drawable mTopDrawable;
    private int mInactiveTextColor;
    private final AnimatorCompat mSelectionAnimator = new ScaleAnimator();
    private boolean isTablet;
    private int mTabletLeftPadding;

    public BottomTabletNavigationTextView(Context context) {
        super(context);
        initialize();
    }

    public BottomTabletNavigationTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();

    }

    public BottomTabletNavigationTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomTabletNavigationTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        invalidate();
    }

    public BottomTabletNavigationTextView(Context context, BottomNavigationItem bottomNavigationItem) {
        super(context);
        mIcon = bottomNavigationItem.getIcon();
        mTopDrawable = DrawableCompat.wrap(bottomNavigationItem.getIconDrawable());
        initialize();
    }


    private void initialize() {
        setRipple();
        mTabletLeftPadding = (int) getResources().getDimension(R.dimen.bottom_navigation_left_padding);
        mViewTopPaddingInactive = (int) getResources().getDimension(R.dimen.bottom_navigation_icon_padding_inactive);
        mViewTopPaddingActive = (int) getResources().getDimension(R.dimen.bottom_navigation_icon_padding_active);
        setScaleType(ScaleType.CENTER);
        if (mTopDrawable == null) {
            mTopDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), mIcon));
        }
        setImageDrawable(mTopDrawable);
        Util.runOnAttachedToLayout(this, new Runnable() {
            @Override
            public void run() {
                setPadding(mTabletLeftPadding, mViewTopPaddingActive * 2, mViewTopPaddingActive * 2, mTabletLeftPadding);
                previouslySelected = false;
                setSelected(isSelected());
            }
        });
        postInvalidate();
    }

    private void setRipple() {
        int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
        TypedArray ta = getContext().obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0);
        ta.recycle();
        if (drawableFromTheme != null) {
            Util.setBackground(this, drawableFromTheme);
        }
    }


    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected && !previouslySelected) {
            applyColorFilters();
            mSelectionAnimator.animateSelection(0, 0);
        } else if (!selected && previouslySelected) {
            mSelectionAnimator.animateSelection(0, 0);
            applyColorFilters();
        }
        previouslySelected = selected;
    }

    private void applyColorFilters() {
        if (isSelected()) {
            DrawableCompat.setTint(mTopDrawable, mTextActiveColorFilter);
        } else {
            DrawableCompat.setTintList(mTopDrawable, null);
        }
    }

    public void setActiveColorResource(@ColorRes int colorRes) {
        setActiveColor(ContextCompat.getColor(getContext(), colorRes));
    }

    public void setActiveColor(@ColorInt int colorInt) {
        mTextActiveColorFilter = colorInt;
    }

    @Override
    public void setInactiveTextColor(@ColorInt int inactiveTextColor) {

    }

    @Override
    public void setShiftingModeEnabled(boolean shiftingModeEnabled) {
    }

    public void setInactiveTextColorResource(@ColorRes int inactiveTextColor) {
        this.mInactiveTextColor = ContextCompat.getColor(getContext(), inactiveTextColor);
    }


    @ColorInt
    public int getInactiveTextColor() {
        return mInactiveTextColor;
    }

    void setIsTablet(boolean isTablet) {
        this.isTablet = isTablet;
    }

    boolean isTablet() {
        return isTablet;
    }

    public void setTablet(boolean isTablet) {
        this.isTablet = isTablet;
    }


    private interface AnimatorCompat {
        void animateSelection(float textSize, float targetSize);
    }

    private class ScaleAnimator implements AnimatorCompat {

        private final Interpolator INTERPOLATOR = new FastOutLinearInInterpolator();

        @Override
        public void animateSelection(float textSize, float targetTextSize) {
            float scale = isSelected() ? 1.2f : 1.0f;
            ViewCompat.animate(BottomTabletNavigationTextView.this)
                    .scaleX(scale).scaleY(scale)
                    .setDuration(ANIMATION_DURATION).setInterpolator(INTERPOLATOR).start();
        }
    }


}
