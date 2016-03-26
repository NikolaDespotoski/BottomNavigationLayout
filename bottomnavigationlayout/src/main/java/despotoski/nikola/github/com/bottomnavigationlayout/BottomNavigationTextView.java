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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.TextView;

/**
 * Created by Nikola on 3/23/2016.
 */
public final class BottomNavigationTextView extends TextView {


    private static final float ACTIVE_TEXT_SIZE = 14;
    private static final float INACTIVE_TEXT_SIZE = 12;
    private static final long ANIMATION_DURATION = 200;
    private String mText;
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
    private float mOriginalTextSize;

    public BottomNavigationTextView(Context context) {
        super(context);
        initialize();
    }

    public BottomNavigationTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();

    }

    public BottomNavigationTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomNavigationTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        invalidate();
    }

    public BottomNavigationTextView(Context context, BottomNavigationItem bottomNavigationItem) {
        super(context);
        mParentBackgroundColor = bottomNavigationItem.getParentBackgroundColorResource() != View.NO_ID ?
                ContextCompat.getColor(getContext(), bottomNavigationItem.getParentBackgroundColorResource()) : bottomNavigationItem.getParentColorBackgroundColor();
        mIcon = bottomNavigationItem.getIcon();
        mText = bottomNavigationItem.getText();
        mTopDrawable = DrawableCompat.wrap(bottomNavigationItem.getIconDrawable());
        initialize();
    }


    private void initialize() {
        setRipple();
        mViewTopPaddingInactive = (int) getResources().getDimension(R.dimen.bottom_navigation_icon_padding_inactive);
        mViewTopPaddingActive = (int) getResources().getDimension(R.dimen.bottom_navigation_icon_padding_active);
        mActiveViewWidth = (int) getResources().getDimension(R.dimen.bottom_navigation_width_active);
        setGravity(Gravity.CENTER);
        setTextIsSelectable(false);
        setText(mText);
        setTextColor(mInactiveTextColor);
        setSingleLine(true);
        setMaxLines(1);
        setEllipsize(TextUtils.TruncateAt.END);
        if (mTopDrawable == null) {
            mTopDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), mIcon));
        }
        mOriginalTextSize = getTextSize();
        setCompoundDrawablesWithIntrinsicBounds(null, mTopDrawable, null, null);
        setCompoundDrawablePadding(0);
        Util.runOnAttachedToLayout(this, new Runnable() {
            @Override
            public void run() {
                setTextSize(getCurrentTextSize());
                int paddingStart = getInactivePadding();
                mInactiveWidth = getWidth();
                setPadding(mViewTopPaddingActive * 2, paddingStart, mViewTopPaddingActive * 2, 0);
                previouslySelected = false;
                setSelected(isSelected());
            }
        });
        getPaint().setAlpha(255);
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

    private float getCurrentTextSize() {
        boolean isAlwaysTextShown = isTextAlwaysShown();
        return isAlwaysTextShown && isSelected() ? ACTIVE_TEXT_SIZE : isAlwaysTextShown && !isSelected() ? INACTIVE_TEXT_SIZE : 0;
    }

    private int getInactivePadding() {
        boolean isAlwaysTextShown = ((ViewGroup) getParent()).getChildCount() == 3;
        return !isAlwaysTextShown ?
                (int) ((mViewTopPaddingInactive) + (mOriginalTextSize / 2))
                : mViewTopPaddingInactive;
    }

    private boolean isTextAlwaysShown() {
        return ((ViewGroup) getParent()).getChildCount() == 3;
    }

    @Override
    public void setTextIsSelectable(boolean selectable) {
        super.setTextIsSelectable(false);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        boolean isAlwaysTextShown = ((ViewGroup) getParent()).getChildCount() == 3;
        if (selected && !previouslySelected) {
            if (!isAlwaysTextShown) {
                animateSelection(0, ACTIVE_TEXT_SIZE);
                applyColorFilters();
            } else {
                animateSelection(INACTIVE_TEXT_SIZE, ACTIVE_TEXT_SIZE);
                applyColorFilters();
            }
        } else if (!selected && previouslySelected) {
            if (!isAlwaysTextShown) {
                animateSelection(ACTIVE_TEXT_SIZE, 0);
                applyColorFilters();
            } else {
                animateSelection(ACTIVE_TEXT_SIZE, INACTIVE_TEXT_SIZE);
                applyColorFilters();
            }
        }
        previouslySelected = selected;
    }

    private void applyColorFilters() {
        if (isSelected()) {
            DrawableCompat.setTint(mTopDrawable, mTextActiveColorFilter);
            setTextColor(mTextActiveColorFilter);
        } else {
            DrawableCompat.setTintList(mTopDrawable, null);
            if (!mShiftingMode) {
                setTextColor(mInactiveTextColor);
            }
        }
    }

    public void setActiveColorResource(@ColorRes int colorRes) {
        setActiveColor(ContextCompat.getColor(getContext(), colorRes));
    }

    public void setActiveColor(@ColorInt int colorInt) {
        mTextActiveColorFilter = colorInt;
    }

    private void animateSelection(float textSize, float targetTextSize) {
        mSelectionAnimator.animateSelection(textSize, targetTextSize);
    }

    private void ensureInactiveViewWidth() {
        if (mInactiveWidth == 0) {
            mInactiveWidth = Math.max(getWidth(), getLayoutParams().width);
        }
    }


    void setShiftingModeEnabled(boolean shiftingModeEnabled) {
        mShiftingMode = shiftingModeEnabled;
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
        return view.getBackground() != null && view.getBackground() instanceof ColorDrawable? ((ColorDrawable) view.getBackground()) : new ColorDrawable(Color.WHITE);
    }

    public void setInactiveTextColorResource(@ColorRes int inactiveTextColor) {
        this.mInactiveTextColor = ContextCompat.getColor(getContext(), inactiveTextColor);
    }

    public void setInactiveTextColor(@ColorInt int inactiveTextColor) {
        this.mInactiveTextColor = inactiveTextColor;
        setTextColor(!isSelected() ? mInactiveTextColor : getCurrentTextColor());
    }

    @ColorInt
    public int getInactiveTextColor() {
        return mInactiveTextColor;
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
            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(BottomNavigationTextView.this,
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
            objectAnimator = ObjectAnimator.ofPropertyValuesHolder(BottomNavigationTextView.this,
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
            objectAnimator = ObjectAnimator.ofPropertyValuesHolder(BottomNavigationTextView.this,
                    PropertyValuesHolder.ofInt(Properties.PADDING_TOP, paddingStart, paddingEnd),
                    PropertyValuesHolder.ofFloat(Properties.TEXT_SIZE, textSize, targetTextSize),
                    PropertyValuesHolder.ofInt(Properties.TEXT_PAINT_ALPHA, alphaStart, alphaEnd),
                    PropertyValuesHolder.ofInt(Properties.VIEW_WIDTH, widthStart, widthEnd));
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
            com.nineoldandroids.animation.ObjectAnimator objectAnimator = com.nineoldandroids.animation.ObjectAnimator.ofPropertyValuesHolder(BottomNavigationTextView.this,
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
            objectAnimator = com.nineoldandroids.animation.ObjectAnimator.ofPropertyValuesHolder(BottomNavigationTextView.this,
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
            objectAnimator = com.nineoldandroids.animation.ObjectAnimator.ofPropertyValuesHolder(BottomNavigationTextView.this,
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
                    color.setColor((Integer)a.getAnimatedValue());
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
                    color.setColor((Integer)a.getAnimatedValue());
                    Util.setBackground(topParent, color);
                }
            });
            rgb.start();
        }
    }


}
