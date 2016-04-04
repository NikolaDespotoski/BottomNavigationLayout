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
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import java.util.List;

/**
 * Created by Nikola D. on 3/31/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BottomNavigationFloatingActionButtonBehavior extends FloatingActionButton.Behavior {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private int mFabBottomMargin;
    private float mTargetFabTranslationY;
    private int mInitialTranslationY;
    private boolean isSnackBarShown = false;
    private ValueAnimator mFabTranslationYAnimator;
    private int mNavBarSize = 0;
    private float mTargetSnackTranslationY;
    private ViewPropertyAnimatorCompat mAnimatorForSnackbar;
    private final AnimatorListenerAdapter mAnimatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            mAnimatorForSnackbar = null;
        }
    };
    private ValueAnimator mReturnAnimator;
    private boolean mIgnoreNestedScrollingEvents = false;

    public BottomNavigationFloatingActionButtonBehavior() {
    }

    public BottomNavigationFloatingActionButtonBehavior(Context context, AttributeSet attributeSet) {

    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency) || dependency instanceof BottomTabLayout;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, final FloatingActionButton child, View dependency) {
        if (!mIgnoreNestedScrollingEvents && dependency instanceof Snackbar.SnackbarLayout) {
            mReturnAnimator = ValueAnimator.ofFloat(ViewCompat.getTranslationY(child) + child.getHeight(), ViewCompat.getTranslationY(child) - mInitialTranslationY - child.getHeight());
            mReturnAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewCompat.setTranslationY(child, (Float) animation.getAnimatedValue());
                }
            });
            mReturnAnimator.setInterpolator(INTERPOLATOR);
            mReturnAnimator.setDuration(200);
            mReturnAnimator.addListener(mAnimatorListener);
            mReturnAnimator.start();
        }
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (!mIgnoreNestedScrollingEvents) {
            if (dependency instanceof BottomTabLayout) {
                updateFabForBottomBar(parent, dependency, child);
                return true;
            } else if (dependency instanceof Snackbar.SnackbarLayout) {
                updateFabTranslationForSnackbar(parent, child, dependency);
                return true;
            }
        }
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void updateFabTranslationForSnackbar(CoordinatorLayout parent,
                                                 final FloatingActionButton child, View snackbar) {
        if (mAnimatorForSnackbar == null) {
            if (mReturnAnimator != null && mReturnAnimator.isRunning()) {
                mReturnAnimator.cancel();
            }
            mAnimatorForSnackbar = ViewCompat.animate(child).translationY(-snackbar.getHeight());
            mAnimatorForSnackbar.setDuration(100);
            mAnimatorForSnackbar.setInterpolator(INTERPOLATOR);
            mAnimatorForSnackbar.start();
        }

    }

    private void updateFabForBottomBar(CoordinatorLayout parent, View dependency, FloatingActionButton child) {
        float dependencyTranslationY = ViewCompat.getTranslationY(dependency);
        mTargetFabTranslationY = (dependencyTranslationY - dependency.getHeight()) - mNavBarSize;
        if (mTargetFabTranslationY <= mInitialTranslationY) {
            ViewCompat.setTranslationY(child, mInitialTranslationY);
        } else if (mTargetFabTranslationY >= mTargetFabTranslationY - mNavBarSize) {
            ViewCompat.setTranslationY(child, mTargetFabTranslationY);
        } else if (mTargetFabTranslationY > mInitialTranslationY && mTargetFabTranslationY < mTargetFabTranslationY - mNavBarSize) {
            ViewCompat.setTranslationY(child, mTargetFabTranslationY);
        }

    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
        boolean onLayoutChild = super.onLayoutChild(parent, child, layoutDirection);
        if (child.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            if (layoutParams.getAnchorId() != View.NO_ID) {
                mIgnoreNestedScrollingEvents = true;
                return onLayoutChild;
            }
            mFabBottomMargin = layoutParams.bottomMargin;
        }
        mNavBarSize = Util.getNavigationBarHeight(parent.getContext());
        List<View> dependencies = parent.getDependencies(child);
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            View view = dependencies.get(i);
            if (view instanceof BottomTabLayout) {
                mInitialTranslationY = -view.getHeight();
                ViewCompat.setTranslationY(child, mInitialTranslationY);
            }
        }
        return true;
    }
}
