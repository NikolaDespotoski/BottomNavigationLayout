package despotoski.nikola.github.com.bottomnavigationlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
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
public class BottomNavigationBehavior<V extends View> extends VerticalScrollingBehavior<V> {
    private static final Interpolator INTERPOLATOR = new LinearOutSlowInInterpolator();
    private int mTabLayoutId;
    private boolean hidden = false;
    private ViewPropertyAnimatorCompat mOffsetValueAnimator;
    private ViewGroup mTabLayout;
    private View mTabsHolder;

    public BottomNavigationBehavior() {
        super();
    }

    public BottomNavigationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BottomNavigationBehavior_Params);
        mTabLayoutId = a.getResourceId(R.styleable.BottomNavigationBehavior_Params_tabLayoutId, View.NO_ID);
        a.recycle();
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

    private void handleDirection(V child, int scrollDirection) {
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
      //  animateTabsHolder(offset);
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

    public static <V extends View> BottomNavigationBehavior<V> from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof BottomNavigationBehavior)) {
            throw new IllegalArgumentException(
                    "The view is not associated with ottomNavigationBehavior");
        }
        return (BottomNavigationBehavior<V>) behavior;
    }

    public void setTabLayoutId(int mTabLayoutId) {
        this.mTabLayoutId = mTabLayoutId;
    }
}
