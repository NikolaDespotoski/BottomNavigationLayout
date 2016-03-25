package despotoski.nikola.github.com.bottomnavigationlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Nikola on 3/23/2016.
 */
public class BottomNavigationTextView extends TextView {


    private static final float ACTIVE_TEXT_SIZE = 14;
    private static final float INACTIVE_TEXT_SIZE = 12;
    private static final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();
    private static final TypeEvaluator FLOAT_EVALUATOR = new FloatEvaluator();
    private static final TypeEvaluator INT_EVALUATOR = new IntEvaluator();
    private String mText;
    private int mIcon;
    private int mParentBackgroundColor;
    private int mTextActiveColorFilter = Color.WHITE;
    private boolean previouslySelected = false;
    private int mViewTopPaddingInactive;
    private int mViewTopPaddingActive;
    private Drawable mTopDrawable;
    private float mOriginalTextSize;
    private int mBottomTextPadding;
    private boolean mShiftingMode = false;
    private int mActiveViewWidth;
    private int mInactiveWidth;
    private int mInactiveTextColor;
    private final RevealViewAnimator mRevealViewImpl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? new KitkatRevealViewAnimatorImpl() : new PreKitkatRevealViewImpl();

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
        mTopDrawable = bottomNavigationItem.getIconDrawable();
        initialize();
    }


    private void initialize() {
        setRipple();
        mViewTopPaddingInactive = (int) getResources().getDimension(R.dimen.bottom_navigation_icon_padding_inactive);
        mViewTopPaddingActive = (int) getResources().getDimension(R.dimen.bottom_navigation_icon_padding_active);
        mBottomTextPadding = (int) getResources().getDimension(R.dimen.bottom_navigation_item_padding_bottom);
        mActiveViewWidth = (int) getResources().getDimension(R.dimen.bottom_navigation_width_active);
        setGravity(Gravity.CENTER);
        setTextIsSelectable(false);
        setText(mText);
        setTextColor(Color.WHITE);
        setSingleLine(true);
        setMaxLines(1);
        setEllipsize(TextUtils.TruncateAt.END);
        mInactiveTextColor = getCurrentTextColor();
        if (mTopDrawable == null) {
            mTopDrawable = ContextCompat.getDrawable(getContext(), mIcon);
        }
        setCompoundDrawablesWithIntrinsicBounds(null, mTopDrawable, null, null);
        mOriginalTextSize = getTextSize();
        Util.runOnAttachedToLayout(this, new Runnable() {
            @Override
            public void run() {
                setTextSize(getCurrentTextSize());
                int paddingStart = getInactivePadding();
                mInactiveWidth = getWidth();
                setPadding(mViewTopPaddingActive * 2, paddingStart, mViewTopPaddingActive * 2, mBottomTextPadding);
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
        if(drawableFromTheme != null) {
            Util.setBackground(this, drawableFromTheme);
        }
    }

    private float getCurrentTextSize() {
        boolean isAlwaysTextShown = isTextAlwaysShown();
        return isAlwaysTextShown && isSelected() ? ACTIVE_TEXT_SIZE : isAlwaysTextShown && !isSelected() ? INACTIVE_TEXT_SIZE : 0;
    }

    private int getInactivePadding() {
        boolean isAlwaysTextShown = isTextAlwaysShown();
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
        boolean isAlwaysTextShown = ((ViewGroup) getParent()).getChildCount() == 3;
        int paddingStart = getInactivePadding();
        int paddingEnd = mViewTopPaddingActive;
        if (!isSelected()) {
            int temp = paddingEnd;
            paddingEnd = paddingStart;
            paddingStart = temp;
        }
        startParentBackgroundColorAnimator();
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofInt(Properties.PADDING_TOP, paddingStart, paddingEnd),
                PropertyValuesHolder.ofFloat(Properties.TEXT_SIZE, textSize, targetTextSize));
        mShiftingMode = mShiftingMode && !isAlwaysTextShown;
        if (isAlwaysTextShown && !mShiftingMode) {
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
        objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofInt(Properties.PADDING_TOP, paddingStart, paddingEnd),
                PropertyValuesHolder.ofFloat(Properties.TEXT_SIZE, textSize, targetTextSize),
                PropertyValuesHolder.ofInt(Properties.TEXT_PAINT_ALPHA, alphaStart, alphaEnd));
        if (!mShiftingMode) {
            objectAnimator.start();
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
        objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofInt(Properties.PADDING_TOP, paddingStart, paddingEnd),
                PropertyValuesHolder.ofFloat(Properties.TEXT_SIZE, textSize, targetTextSize),
                PropertyValuesHolder.ofInt(Properties.TEXT_PAINT_ALPHA, alphaStart, alphaEnd),
                PropertyValuesHolder.ofInt(Properties.VIEW_WIDTH, widthStart, widthEnd));
        objectAnimator.start();

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
        return view.getBackground() != null ? ((ColorDrawable) view.getBackground()) : new ColorDrawable(Color.WHITE);
    }


    private interface RevealViewAnimator {
        void animateBackground();
    }

    private class KitkatRevealViewAnimatorImpl implements RevealViewAnimator {

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


    private class PreKitkatRevealViewImpl implements RevealViewAnimator {
        @Override
        public void animateBackground() {
            final BottomTabLayout topParent = (BottomTabLayout) getParent();
            final ColorDrawable color = getColorDrawable(topParent);
            ValueAnimator rgb = ObjectAnimator.ofInt(color.getColor(), mParentBackgroundColor);
            rgb.setEvaluator(ARGB_EVALUATOR);
            rgb.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator a) {
                    DrawableCompat.setTint(color, (Integer) a.getAnimatedValue());
                }
            });
            rgb.start();
        }
    }
}
