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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;

/**
 * Created by Nikola on 3/23/2016.
 */
public final class BottomTabletNavigationTextView extends ImageView implements BottomNavigation{


    private static final long ANIMATION_DURATION = 200;
    private int mIcon;
    private int mParentBackgroundColor;
    private int mTextActiveColorFilter = Color.WHITE;
    private boolean previouslySelected = false;
    private int mViewTopPaddingInactive;
    private int mViewTopPaddingActive;
    private Drawable mTopDrawable;
    private boolean mShiftingMode = false;
    private int mActiveViewWidth;
    private int mInactiveWidth;
    private int mInactiveTextColor;
    private final RevealViewAnimator mRevealViewImpl =
            Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT ?
                    new LollipopRevealViewAnimator() :
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                            new PreKitkatRevealViewImpl() :
                            new PrehistoricRevealViewImpl();
    private final AnimatorCompat mSelectionAnimator =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    new NewEraAnimator() : new PreHistoricAnimator();
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
        mParentBackgroundColor = bottomNavigationItem.getParentBackgroundColorResource() != View.NO_ID ?
                ContextCompat.getColor(getContext(), bottomNavigationItem.getParentBackgroundColorResource()) : bottomNavigationItem.getParentColorBackgroundColor();
        mIcon = bottomNavigationItem.getIcon();
        mTopDrawable = DrawableCompat.wrap(bottomNavigationItem.getIconDrawable());
        initialize();
    }


    private void initialize() {
        setRipple();
        mTabletLeftPadding = (int)getResources().getDimension(R.dimen.bottom_navigation_left_padding);
        mViewTopPaddingInactive = (int) getResources().getDimension(R.dimen.bottom_navigation_icon_padding_inactive);
        mViewTopPaddingActive = (int) getResources().getDimension(R.dimen.bottom_navigation_icon_padding_active);
        mActiveViewWidth = (int) getResources().getDimension(R.dimen.bottom_navigation_width_active);
        setScaleType(ScaleType.CENTER);
        if (mTopDrawable == null) {
            mTopDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), mIcon));
        }
        setImageDrawable(mTopDrawable);
        Util.runOnAttachedToLayout(this, new Runnable() {
            @Override
            public void run() {
                int paddingStart = getInactivePadding();
                mInactiveWidth = getWidth();
                setPadding(mTabletLeftPadding, paddingStart, mViewTopPaddingActive * 2, mTabletLeftPadding);
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


    private int getInactivePadding() {
        boolean isAlwaysTextShown = ((ViewGroup) getParent()).getChildCount() == 3;
        return !isAlwaysTextShown ?
                (int) ((mViewTopPaddingInactive))
                : mViewTopPaddingInactive;
    }


    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected && !previouslySelected) {
            applyColorFilters();
        } else if (!selected && previouslySelected) {
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
        mShiftingMode = shiftingModeEnabled;
    }

    private void animateSelection(float textSize, float targetTextSize) {
        mSelectionAnimator.animateSelection(textSize, targetTextSize);
    }

    private void ensureInactiveViewWidth() {
        if (mInactiveWidth == 0) {
            mInactiveWidth = Math.max(getWidth(), getLayoutParams().width);
        }
    }



    private void startParentBackgroundColorAnimator() {
        if (!isSelected()) return;
        Util.runOnAttachedToLayout(this, new Runnable() {
            @Override
            public void run() {
                mRevealViewImpl.animateBackground();
            }
        });
    }

    private ColorDrawable getColorDrawable(View view) {
        return view.getBackground() != null && view.getBackground() instanceof ColorDrawable ? ((ColorDrawable) view.getBackground()) : new ColorDrawable(Color.WHITE);
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


    private interface RevealViewAnimator {
        void animateBackground();
    }

    private interface AnimatorCompat {
        void animateSelection(float textSize, float targetSize);
    }

    private class NewEraAnimator implements AnimatorCompat {

        private final TimeInterpolator INTERPOLATOR = new FastOutLinearInInterpolator();

        @Override
        public void animateSelection(float textSize, float targetTextSize) {
            boolean isAlwaysTextShown = ((ViewGroup) getParent()).getChildCount() == 3;
            int paddingStart = getInactivePadding();
            int paddingEnd = mViewTopPaddingActive;
            if (!isSelected()) {
                int temp = paddingEnd;
                paddingEnd = paddingStart;
                paddingStart = temp;
            }
            startParentBackgroundColorAnimator();
            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(BottomTabletNavigationTextView.this,
                    PropertyValuesHolder.ofInt(Properties.PADDING_TOP, paddingStart, paddingEnd),
                    PropertyValuesHolder.ofFloat(Properties.TEXT_SIZE, textSize, targetTextSize));
            mShiftingMode = mShiftingMode && !isAlwaysTextShown;
            if (isAlwaysTextShown && !mShiftingMode) {
                startObjectAnimator(objectAnimator);
                return;
            }
            int alphaStart = 0;
            int alphaEnd = 255;
            if (!isSelected()) {
                int a = alphaEnd;
                alphaEnd = alphaStart;
                alphaStart = a;
            }
            objectAnimator = ObjectAnimator.ofPropertyValuesHolder(BottomTabletNavigationTextView.this,
                    PropertyValuesHolder.ofInt(Properties.PADDING_TOP, paddingStart, paddingEnd),
                    PropertyValuesHolder.ofFloat(Properties.TEXT_SIZE, textSize, targetTextSize),
                    PropertyValuesHolder.ofInt(Properties.TEXT_PAINT_ALPHA, alphaStart, alphaEnd));
            if (!mShiftingMode) {
                startObjectAnimator(objectAnimator);
                return;
            }
            ensureInactiveViewWidth();
            int widthStart = mInactiveWidth;
            int widthEnd = mInactiveWidth + mActiveViewWidth;
            if (!isSelected()) {
                int a = widthEnd;
                widthEnd = widthStart;
                widthStart = a;
            }
            objectAnimator = ObjectAnimator.ofPropertyValuesHolder(BottomTabletNavigationTextView.this,
                    PropertyValuesHolder.ofInt(Properties.PADDING_TOP, paddingStart, paddingEnd),
                    PropertyValuesHolder.ofFloat(Properties.TEXT_SIZE, textSize, targetTextSize),
                    PropertyValuesHolder.ofInt(Properties.TEXT_PAINT_ALPHA, alphaStart, alphaEnd),
                    PropertyValuesHolder.ofInt(Properties.VIEW_HEIGHT, widthStart, widthEnd));
            startObjectAnimator(objectAnimator);
        }

        private void startObjectAnimator(ObjectAnimator objectAnimator) {
            objectAnimator.setInterpolator(INTERPOLATOR);
            objectAnimator.setDuration(ANIMATION_DURATION);
            objectAnimator.start();
        }
    }

    private class PreHistoricAnimator implements AnimatorCompat {
        private final Interpolator INTERPOLATOR = new FastOutLinearInInterpolator();

        @Override
        public void animateSelection(float textSize, float targetTextSize) {
            boolean isAlwaysTextShown = ((ViewGroup) getParent()).getChildCount() == 3;
            int paddingStart = getInactivePadding();
            int paddingEnd = mViewTopPaddingActive;
            if (!isSelected()) {
                int temp = paddingEnd;
                paddingEnd = paddingStart;
                paddingStart = temp;
            }
            startParentBackgroundColorAnimator();
            com.nineoldandroids.animation.ObjectAnimator objectAnimator = com.nineoldandroids.animation.ObjectAnimator.ofPropertyValuesHolder(BottomTabletNavigationTextView.this,
                    com.nineoldandroids.animation.PropertyValuesHolder.ofInt(PrehistoricProperties.PADDING_TOP, paddingStart, paddingEnd),
                    com.nineoldandroids.animation.PropertyValuesHolder.ofFloat(PrehistoricProperties.TEXT_SIZE, textSize, targetTextSize));
            mShiftingMode = mShiftingMode && !isAlwaysTextShown;
            if (isAlwaysTextShown && !mShiftingMode) {
                objectAnimator.setDuration(ANIMATION_DURATION);
                objectAnimator.start();
                return;
            }
            int alphaStart = 0;
            int alphaEnd = 255;
            if (!isSelected()) {
                int a = alphaEnd;
                alphaEnd = alphaStart;
                alphaStart = a;
            }
            objectAnimator = com.nineoldandroids.animation.ObjectAnimator.ofPropertyValuesHolder(BottomTabletNavigationTextView.this,
                    com.nineoldandroids.animation.PropertyValuesHolder.ofInt(PrehistoricProperties.PADDING_TOP, paddingStart, paddingEnd),
                    com.nineoldandroids.animation.PropertyValuesHolder.ofFloat(PrehistoricProperties.TEXT_SIZE, textSize, targetTextSize),
                    com.nineoldandroids.animation.PropertyValuesHolder.ofInt(PrehistoricProperties.TEXT_PAINT_ALPHA, alphaStart, alphaEnd));
            if (!mShiftingMode) {
                startObjectAnimator(objectAnimator);
                return;
            }
            ensureInactiveViewWidth();
            int widthStart = mInactiveWidth;
            int widthEnd = mInactiveWidth + mActiveViewWidth;
            if (!isSelected()) {
                int a = widthEnd;
                widthEnd = widthStart;
                widthStart = a;
            }
            objectAnimator = com.nineoldandroids.animation.ObjectAnimator.ofPropertyValuesHolder(BottomTabletNavigationTextView.this,
                    com.nineoldandroids.animation.PropertyValuesHolder.ofInt(PrehistoricProperties.PADDING_TOP, paddingStart, paddingEnd),
                    com.nineoldandroids.animation.PropertyValuesHolder.ofFloat(PrehistoricProperties.TEXT_SIZE, textSize, targetTextSize),
                    com.nineoldandroids.animation.PropertyValuesHolder.ofInt(PrehistoricProperties.TEXT_PAINT_ALPHA, alphaStart, alphaEnd),
                    com.nineoldandroids.animation.PropertyValuesHolder.ofInt(PrehistoricProperties.VIEW_WIDTH, widthStart, widthEnd));
            objectAnimator.setDuration(ANIMATION_DURATION);
            objectAnimator.setInterpolator(new FastOutLinearInInterpolator());
            objectAnimator.start();
        }

        private void startObjectAnimator(com.nineoldandroids.animation.ObjectAnimator objectAnimator) {
            objectAnimator.setInterpolator(INTERPOLATOR);
            objectAnimator.setDuration(ANIMATION_DURATION);
            objectAnimator.start();
        }
    }

    private class LollipopRevealViewAnimator implements RevealViewAnimator {
        private final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void animateBackground() {
            final BottomTabLayout topParent = (BottomTabLayout) getParent().getParent();
            final View revealView = topParent.getRevealOverlayView();
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(revealView, (int) getX() + getWidth() / 2, (int) getY() + getHeight() / 2, 0, revealView.getWidth());
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    revealView.setVisibility(View.VISIBLE);
                    revealView.setBackgroundColor(mParentBackgroundColor);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    topParent.setBackgroundColor(mParentBackgroundColor);
                    revealView.setBackgroundColor(mParentBackgroundColor);
                    //revealView.setVisibility(GONE);
                }
            });
            final ColorDrawable color = getColorDrawable(revealView);
            ValueAnimator rgb = ObjectAnimator.ofInt(color.getColor(), mParentBackgroundColor);
            rgb.setEvaluator(ARGB_EVALUATOR);
            rgb.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator a) {
                    DrawableCompat.setTint(color, (Integer) a.getAnimatedValue());
                }
            });
            Util.playTogether(circularReveal, rgb);
        }
    }

    private class PrehistoricRevealViewImpl implements RevealViewAnimator {

        private final com.nineoldandroids.animation.TypeEvaluator ARGB_EVALUATOR_COMPAT = new com.nineoldandroids.animation.ArgbEvaluator();

        @Override
        public void animateBackground() {
            final BottomTabLayout topParent = (BottomTabLayout) getParent().getParent();
            final View revealView = topParent.getRevealOverlayView();
            final ColorDrawable color = getColorDrawable(revealView);
            com.nineoldandroids.animation.ValueAnimator rgb = com.nineoldandroids.animation.ObjectAnimator.ofInt(color.getColor(), mParentBackgroundColor);
            rgb.setEvaluator(ARGB_EVALUATOR_COMPAT);
            rgb.addUpdateListener(new com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(com.nineoldandroids.animation.ValueAnimator a) {
                    color.setColor((Integer) a.getAnimatedValue());
                    Util.setBackground(topParent, color);
                }
            });
            rgb.start();
        }
    }

    private class PreKitkatRevealViewImpl implements RevealViewAnimator {
        private final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();

        @Override
        public void animateBackground() {
            final BottomTabLayout topParent = (BottomTabLayout) getParent().getParent();
            final ColorDrawable color = getColorDrawable(topParent);
            ValueAnimator rgb = ObjectAnimator.ofInt(color.getColor(), mParentBackgroundColor);
            rgb.setEvaluator(ARGB_EVALUATOR);
            rgb.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator a) {
                    color.setColor((Integer) a.getAnimatedValue());
                    Util.setBackground(topParent, color);
                }
            });
            rgb.start();
        }
    }


}
