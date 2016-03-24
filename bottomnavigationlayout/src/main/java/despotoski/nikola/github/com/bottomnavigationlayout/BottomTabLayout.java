package despotoski.nikola.github.com.bottomnavigationlayout;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikola on 3/15/2016.
 */
@CoordinatorLayout.DefaultBehavior(BottomNavigationBehavior.class)
public class BottomTabLayout extends DrawShadowFrameLayout {
    private static final float MAX_ITEM_WIDTH = 168f;
    private static final int MAX_BOTTOM_NAVIGATION_ITEMS = 5;
    private static final int MIN_BOTTOM_NAVIGATION_ITEMS = 3;
    private View mRevealOverlayView;
    private LinearLayoutCompat mContainer;
    private int mBottomTabMenuResId;
    private int mActiveColorFilter;
    private int mSelectedItemPosition = View.NO_ID;
    private View mCurrentNavigationItem;
    private boolean mAlwaysShowText = false;
    private BottomTabLayout.OnNavigationItemSelectionListener onNavigationItemSelectionListener;
    private OnClickListener mBottomTabSelectionClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!v.isSelected()) {
                v.setSelected(true);
                mCurrentNavigationItem.setSelected(false);
                if (onNavigationItemSelectionListener != null) {
                    onNavigationItemSelectionListener.onBottomNavigationItemSelected((BottomNavigationItem) v.getTag());
                    onNavigationItemSelectionListener.onBottomNavigationItemUnselected((BottomNavigationItem) mCurrentNavigationItem.getTag());
                }
            }
            mCurrentNavigationItem = v;
        }
    };
    private int mMinBottomItemWidth;
    private boolean mShiftingMode;
    private List<BottomNavigationTextView> mBottomTabViews = new ArrayList<>(MAX_BOTTOM_NAVIGATION_ITEMS);
    private int mMaxItemWidth;

    public BottomTabLayout(Context context) {
        super(context);
        removeAllViews();
    }

    public BottomTabLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        removeAllViews();
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setupOverlayView();
        setupContainer();
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BottomNavigationTabLayout);
        mActiveColorFilter = a.getResourceId(R.styleable.BottomNavigationTabLayout_activeColorFilter, View.NO_ID);
        mBottomTabMenuResId = a.getResourceId(R.styleable.BottomNavigationTabLayout_bottomTabsMenu, View.NO_ID);
        mMaxItemWidth = (int) getResources().getDimension(R.dimen.bottom_navigation_max_width);
        mMinBottomItemWidth = (int) getResources().getDimension(R.dimen.bottom_navigation_min_width);
        mContainer.setOrientation(LinearLayoutCompat.HORIZONTAL);
        mContainer.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
        setBottomTabs(mBottomTabMenuResId);
        Util.runOnAttachedToLayout(this, new Runnable() {
            @Override
            public void run() {
                updateBottomNavViews();
            }
        });
        a.recycle();
    }

    private static void checkBottomItemGuidelines(int count) {
        if (count < MIN_BOTTOM_NAVIGATION_ITEMS || count > MAX_BOTTOM_NAVIGATION_ITEMS) {
            throw new IllegalArgumentException("Number of bottom navigation items should between 3 and 5, count: " + count + " if you are using with ViewPager, Dont! ");
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        LayoutParams layoutParams = super.generateDefaultLayoutParams();
        layoutParams.gravity = Gravity.BOTTOM;
        return layoutParams;
    }

    private void setupContainer() {
        mContainer = new LinearLayoutCompat(getContext());
        mContainer.setFocusable(false);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.bottom_navigation_height));
        layoutParams.gravity = Gravity.TOP;
        layoutParams.bottomMargin = Util.isNavigationBarTranslucent(getContext()) && !isLandscape() ? Util.getNavigationBarHeight(getContext()) : 0;
        addView(mContainer, layoutParams);
    }

    private void setupOverlayView() {
        int height = (int) getResources().getDimension(R.dimen.bottom_navigation_height);
        height += Util.isNavigationBarTranslucent(getContext()) ? Util.getNavigationBarHeight(getContext()) : 0;
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        mRevealOverlayView = new View(getContext());
        mRevealOverlayView.setFocusable(false);
        mRevealOverlayView.setFocusableInTouchMode(false);
        mRevealOverlayView.setClickable(false);
        addView(mRevealOverlayView, layoutParams);
    }

    private void updateBottomNavViews() {
        if (mContainer.getChildCount() == 0) return;
        mAlwaysShowText = mContainer.getChildCount() == 3;
        mMinBottomItemWidth = findMinItemWidth();
        for (int i = mContainer.getChildCount() - 1; i >= 0; i--) {
            View childAt = mContainer.getChildAt(i);
            childAt.setLayoutParams(generateBottomItemLayoutParams());
            childAt.requestLayout();
            childAt.setOnClickListener(mBottomTabSelectionClickListener);
            if (childAt instanceof BottomNavigationTextView) {
                BottomNavigationTextView bottomView = (BottomNavigationTextView) childAt;
                bottomView.setShiftingModeEnabled(mShiftingMode);
                bottomView.setActiveColorResource(mActiveColorFilter);

            }
            if (i == mSelectedItemPosition && !childAt.isSelected()) {
                childAt.setSelected(true);
            }
        }
    }

    private LinearLayoutCompat.LayoutParams generateBottomItemLayoutParams() {
        if (isLandscape()) {
            mMinBottomItemWidth = Math.min(mMinBottomItemWidth, mMaxItemWidth);
        }
        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(mMinBottomItemWidth, (int) getResources().getDimension(R.dimen.bottom_navigation_height));
        return layoutParams;
    }

    private boolean isLandscape() {
        return getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public boolean isShiftingMode() {
        return mShiftingMode;
    }

    public void setShiftingMode(boolean mShiftingMode) {
        this.mShiftingMode = mShiftingMode;
        updateBottomNavViews();
    }

    private int findMinItemWidth() {
        return mContainer.getWidth() / mContainer.getChildCount();
    }

    public void setBottomTabs(@MenuRes int menuResId) {
        if (menuResId != View.NO_ID) {
            MenuBuilder menuBuilder = new MenuBuilder(getContext());
            ((Activity) getContext()).getMenuInflater().inflate(menuResId, menuBuilder);
            checkBottomItemGuidelines(menuBuilder.size());
            populateFromMenuResource(menuBuilder);
        }
    }

    public void populateFromMenuResource(@NonNull MenuBuilder menuBuilder) {
        removeAllTabs();
        int size = menuBuilder.size();
        checkBottomItemGuidelines(size);
        for (int i = 0; i < size; i++) {
            MenuItem item = menuBuilder.getItem(i);
            newBottomTab(item);
        }
    }

    private void newBottomTab(MenuItem item) {
        //addBottomNavigationItem(BottomNavigationItemBuilder.create(item.getIcon() , item.getTitle(), mParent));
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        mSelectedItemPosition = selectedItemPosition;
    }

    private void removeAllTabs() {
        for (int i = mContainer.getChildCount() - 1; i >= 0; i--) {
            mContainer.removeViewAt(i);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateBottomNavViews();
    }

    public void populateBottomTabItems(@NonNull BottomTabsBuilder builder) {
        if (getChildCount() >= MIN_BOTTOM_NAVIGATION_ITEMS) {
            checkBottomItemGuidelines(getChildCount());
        }
        List<BottomNavigationItem> build = builder.build();
        for (int i = 0; i < build.size(); i++) {
            BottomNavigationItem item = build.get(i);
            item.setPosition(i);
            BottomNavigationTextView bottomNavigationTextView = new BottomNavigationTextView(getContext(), item);
            bottomNavigationTextView.setActiveColor(mActiveColorFilter);
            bottomNavigationTextView.setTag(item);
            mContainer.addView(bottomNavigationTextView, generateBottomItemLayoutParams());
            mBottomTabViews.add(bottomNavigationTextView);
        }
        updateBottomNavViews();
        selectTabView();
    }

    private void selectTabView() {
        if (mSelectedItemPosition == View.NO_ID) {
            mSelectedItemPosition = 1;
        }
        BottomNavigationTextView bottomNavigationTextView = mBottomTabViews.get(mSelectedItemPosition);
        bottomNavigationTextView.requestFocus();
        bottomNavigationTextView.setSelected(true);
        mCurrentNavigationItem = bottomNavigationTextView;

    }

    @NonNull
    final View getRevealOverlayView() {
        return mRevealOverlayView;
    }


    public void setOnNavigationItemSelectionListener(OnNavigationItemSelectionListener onNavigationItemSelectionListener) {
        this.onNavigationItemSelectionListener = onNavigationItemSelectionListener;
    }

    public void setActiveItemColorResource(@ColorRes int activeColor) {
        mActiveColorFilter = activeColor;
    }

    public interface OnNavigationItemSelectionListener {
        void onBottomNavigationItemSelected(BottomNavigationItem item);

        void onBottomNavigationItemUnselected(BottomNavigationItem item);
    }

    public static class BottomTabsBuilder {


        private ArrayList<BottomNavigationItem> mNavItems;

        public BottomTabsBuilder addBottomNavigationItem(BottomNavigationItem bottomNavigationItem) {
            ensureList();
            mNavItems.add(bottomNavigationItem);
            return this;
        }

        private void ensureList() {
            if (mNavItems == null) {
                mNavItems = new ArrayList<>();
            }
        }

        public void validate() {
            checkBottomItemGuidelines(mNavItems != null ? mNavItems.size() : 0);
        }

        List<BottomNavigationItem> build() {
            validate();
            return mNavItems;
        }
    }
}

