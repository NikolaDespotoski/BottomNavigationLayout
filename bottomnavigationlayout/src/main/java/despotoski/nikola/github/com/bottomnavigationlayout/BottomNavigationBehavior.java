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

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

/**
 * Created by Nikola D. on 3/15/2016.
 */
public final class BottomNavigationBehavior<V extends View> extends VerticalScrollingBehavior<V> {
    private static final Interpolator INTERPOLATOR = new LinearOutSlowInInterpolator();
    private final BottomNavigationWithSnackbar mWithSnackBarImpl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? new LollipopBottomNavWithSnackBarImpl() : new PreLollipopBottomNavWithSnackBarImpl();
    private boolean isTablet;
    private int mTabLayoutId;
    private boolean hidden = false;
    private ViewPropertyAnimatorCompat mOffsetValueAnimator;
    private ViewGroup mTabLayout;
    private View mTabsHolder;
    private int mSnackbarHeight = -1;
    private boolean scrollingEnabled = true;
    private boolean hideAlongSnackbar = false;

    public BottomNavigationBehavior() {
        super();
    }

    public BottomNavigationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        isTablet = context.getResources().getBoolean(R.bool.isTablet);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BottomNavigationBehavior_Params);
        mTabLayoutId = a.getResourceId(R.styleable.BottomNavigationBehavior_Params_tabLayoutId, View.NO_ID);
        a.recycle();
    }

    public static <V extends View> BottomNavigationBehavior<V> from(@NonNull V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof BottomNavigationBehavior)) {
            throw new IllegalArgumentException(
                    "The view is not associated with BottomNavigationBehavior");
        }
        return (BottomNavigationBehavior<V>) behavior;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        mWithSnackBarImpl.updateSnackbar(parent, dependency, child);
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, V child, View dependency) {
        updateScrollingForSnackbar(dependency, child, true);
        super.onDependentViewRemoved(parent, child, dependency);
    }

    private void updateScrollingForSnackbar(View dependency, V child, boolean enabled) {
        if (!isTablet && dependency instanceof Snackbar.SnackbarLayout) {
            scrollingEnabled = enabled;
            if (!hideAlongSnackbar && ViewCompat.getTranslationY(child) != 0) {
                ViewCompat.setTranslationY(child, 0);
                hidden = false;
                hideAlongSnackbar = true;
            }else if(hideAlongSnackbar){
                hidden = true;
                animateOffset(child, -child.getHeight());
            }
        }
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) {
        updateScrollingForSnackbar(dependency, child, false);
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        boolean layoutChild = super.onLayoutChild(parent, child, layoutDirection);
        if (mTabLayout == null && mTabLayoutId != View.NO_ID) {
            mTabLayout = findTabLayout(child);
            getTabsHolder();
        }

        return layoutChild;
    }

    private ViewGroup findTabLayout(View child) {
        if (mTabLayoutId == 0) return null;
        return (ViewGroup) child.findViewById(mTabLayoutId);
    }

    @Override
    public void onNestedVerticalOverScroll(CoordinatorLayout coordinatorLayout, V child, @ScrollDirection int direction, int currentOverScroll, int totalOverScroll) {
    }

    @Override
    public void onDirectionNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed, @ScrollDirection int scrollDirection) {
        handleDirection(child, scrollDirection);
    }

    private void handleDirection(V child, @ScrollDirection int scrollDirection) {
        if (!scrollingEnabled) return;
        if (scrollDirection == ScrollDirection.SCROLL_DIRECTION_DOWN && hidden) {
            hidden = false;
            animateOffset(child, 0);
        } else if (scrollDirection == ScrollDirection.SCROLL_DIRECTION_UP && !hidden) {
            hidden = true;
            animateOffset(child, child.getHeight());
        }
    }

    @Override
    protected boolean onNestedDirectionFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY, @ScrollDirection int scrollDirection) {
        handleDirection(child, scrollDirection);
        return true;
    }

    private void animateOffset(final V child, final int offset) {
        ensureOrCancelAnimator(child);
        mOffsetValueAnimator.translationY(offset).start();
        animateTabsHolder(offset);
    }

    private void animateTabsHolder(int offset) {
        if (mTabsHolder != null) {
            offset = offset > 0 ? 0 : 1;
            ViewCompat.animate(mTabsHolder).alpha(offset).setDuration(200).start();
        }
    }

    private void ensureOrCancelAnimator(V child) {
        if (mOffsetValueAnimator == null) {
            mOffsetValueAnimator = ViewCompat.animate(child);
            mOffsetValueAnimator.setDuration(100);
            mOffsetValueAnimator.setInterpolator(INTERPOLATOR);
        } else {
            mOffsetValueAnimator.cancel();
        }
    }

    private void getTabsHolder() {
        if (mTabLayout != null) {
            mTabsHolder = mTabLayout.getChildAt(0);
        }
    }

    public boolean isScrollingEnabled() {
        return scrollingEnabled;
    }

    public void setScrollingEnabled(boolean scrollingEnabled) {
        this.scrollingEnabled = scrollingEnabled;
    }

    public void setHidden(V view, boolean bottomLayoutHidden) {
        if (!bottomLayoutHidden && hidden) {
            animateOffset(view, 0);
        } else if (bottomLayoutHidden && !hidden) {
            animateOffset(view, -view.getHeight());
        }
        hidden = bottomLayoutHidden;
    }

    private interface BottomNavigationWithSnackbar {
        void updateSnackbar(CoordinatorLayout parent, View dependency, View child);
    }

    private class PreLollipopBottomNavWithSnackBarImpl implements BottomNavigationWithSnackbar {

        @Override
        public void updateSnackbar(CoordinatorLayout parent, View dependency, View child) {
            if (!isTablet && dependency instanceof Snackbar.SnackbarLayout) {
                if (mSnackbarHeight == -1) {
                    mSnackbarHeight = dependency.getHeight();
                }

                int targetPadding = child.getMeasuredHeight();

                int shadow = ((BottomTabLayout) child).getShadowElevation();
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) dependency.getLayoutParams();
                layoutParams.bottomMargin = targetPadding - shadow;
                child.bringToFront();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    child.getParent().requestLayout();
                    ((View) child.getParent()).invalidate();
                }

            }
        }
    }

    private class LollipopBottomNavWithSnackBarImpl implements BottomNavigationWithSnackbar {

        @Override
        public void updateSnackbar(CoordinatorLayout parent, View dependency, View child) {
            if (!isTablet && dependency instanceof Snackbar.SnackbarLayout) {
                if (mSnackbarHeight == -1) {
                    mSnackbarHeight = dependency.getHeight();
                }
                int targetPadding = (mSnackbarHeight +
                        child.getMeasuredHeight());
                dependency.setPadding(dependency.getPaddingLeft(),
                        dependency.getPaddingTop(), dependency.getPaddingRight(), targetPadding
                );
            }
        }
    }
}
