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

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikola on 3/15/2016.
 */
@CoordinatorLayout.DefaultBehavior(BottomNavigationBehavior.class)
public class BottomTabLayout extends DrawShadowFrameLayout {
    static final float MAX_ITEM_WIDTH = 168f;
    private static final int MAX_BOTTOM_NAVIGATION_ITEMS = 5;
    private static final int MIN_BOTTOM_NAVIGATION_ITEMS = 3;
    private int[] mParentBackgroundColors;
    private View mRevealOverlayView;
    private LinearLayoutCompat mContainer;
    private int mActiveColorFilter;
    private int mSelectedItemPosition = View.NO_ID;
    private View mCurrentNavigationItem;
    private boolean mAlwaysShowText = false;
    private BottomTabLayout.OnNavigationItemSelectionListener onNavigationItemSelectionListener;
    private final OnClickListener mBottomTabSelectionClickListener = new OnClickListener() {
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
    private final List<View> mBottomTabViews = new ArrayList<>(MAX_BOTTOM_NAVIGATION_ITEMS);
    private int mMaxItemWidth;
    private int mInactiveTextColor;
    private boolean isTablet;
    private int mMaxContainerHeight;

    public BottomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initalize(context, attrs);

    }

    public BottomTabLayout(Context context) {
        super(context);
        removeAllViews();

    }

    public BottomTabLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        isTablet = getResources().getBoolean(R.bool.isTablet);
        initalize(context, attrs);
    }

    private void initalize(Context context, AttributeSet attrs) {
        mMaxContainerHeight = (int) getResources().getDimension(R.dimen.bottom_navigation_height);
        if (isTablet) {
            setShadowVisible(false);
            Util.runOnAttachedToLayout(this, new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams params = getLayoutParams();
                    if (params instanceof FrameLayout.LayoutParams) {
                        ((LayoutParams) params).gravity = GravityCompat.START;
                    } else if (params instanceof CoordinatorLayout.LayoutParams) {
                        ((CoordinatorLayout.LayoutParams) params).gravity = GravityCompat.START;
                    }
                    params.width = mMaxContainerHeight;
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    requestLayout();
                    if (mContainer != null) mContainer.requestLayout();
                    disableBehavior();
                }
            });
        }
        removeAllViews();
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        if (!isTablet)
            setupOverlayView();
        setupContainer();
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BottomNavigationTabLayout);
        mShiftingMode = a.getBoolean(R.styleable.BottomNavigationTabLayout_shift_mode, false);
        mInactiveTextColor = a.getColor(R.styleable.BottomNavigationTabLayout_inactive_item_text_color, Color.WHITE);
        mActiveColorFilter = a.getResourceId(R.styleable.BottomNavigationTabLayout_active_item_color_filter, View.NO_ID);
        int bottomTabMenuResId = a.getResourceId(R.styleable.BottomNavigationTabLayout_bottom_tabs_menu, View.NO_ID);
        if (bottomTabMenuResId != View.NO_ID) {
            int parentBackgroundColorsResId = a.getResourceId(R.styleable.BottomNavigationTabLayout_bottom_tabs_menu_parent_background_colors, View.NO_ID);
            mParentBackgroundColors = getResources().getIntArray(parentBackgroundColorsResId);
        }
        mMaxItemWidth = (int) getResources().getDimension(R.dimen.bottom_navigation_max_width);
        mMinBottomItemWidth = (int) getResources().getDimension(R.dimen.bottom_navigation_min_width);

        setBottomTabs(bottomTabMenuResId, mParentBackgroundColors);
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
            throw new IllegalArgumentException("Number of bottom navigation items should between 3 and 5, count: " + count);
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {

        LayoutParams layoutParams = super.generateDefaultLayoutParams();
        if (!isTablet)
            layoutParams.gravity = Gravity.BOTTOM;
        else {
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = GravityCompat.START;
        }
        return layoutParams;
    }

    private void setupContainer() {
        mContainer = new LinearLayoutCompat(getContext());
        mContainer.setFocusable(false);
        LayoutParams layoutParams;
        if (isTablet) {
            layoutParams = new LayoutParams(mMaxContainerHeight, LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            disableBehavior();
            mContainer.setOrientation(LinearLayoutCompat.VERTICAL);
            mContainer.setGravity(Gravity.TOP | Gravity.CENTER_VERTICAL);
            addView(mContainer, layoutParams);
        } else {
            mContainer.setOrientation(LinearLayoutCompat.HORIZONTAL);
            mContainer.setPadding(0, 0, (int) getResources().getDimension(R.dimen.bottom_navigation_item_padding_bottom), 0);
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.bottom_navigation_height));
            layoutParams.gravity = Gravity.TOP;
            layoutParams.bottomMargin = Util.isNavigationBarTranslucent(getContext()) && !isLandscape() ? Util.getNavigationBarHeight(getContext()) : 0;
            mContainer.setOrientation(LinearLayoutCompat.HORIZONTAL);
            mContainer.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
            addView(mContainer, layoutParams);
        }


    }

    private void disableBehavior() {
        ViewGroup.LayoutParams params = super.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            ((CoordinatorLayout.LayoutParams) params).setBehavior(null);
        }
    }

    private void setupOverlayView() {
        int height = (int) getResources().getDimension(R.dimen.bottom_navigation_height);
        height += Util.isNavigationBarTranslucent(getContext()) ? Util.getNavigationBarHeight(getContext()) : 0;
        LayoutParams layoutParams;
        if (!isTablet) {
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, height);
            layoutParams.topMargin = getShadowElevation();
        } else {
            layoutParams = new LayoutParams(height, LayoutParams.MATCH_PARENT);
        }
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
            childAt.setOnClickListener(mBottomTabSelectionClickListener);
            if (childAt instanceof BottomNavigation) {
                BottomNavigation bottomView = (BottomNavigation) childAt;
                bottomView.setShiftingModeEnabled(mShiftingMode);
                bottomView.setActiveColorResource(mActiveColorFilter);
                bottomView.setInactiveTextColor(mInactiveTextColor);

            }
        }
        selectTabView();
    }

    private LinearLayoutCompat.LayoutParams generateBottomItemLayoutParams() {
        if (isLandscape()) {
            mMinBottomItemWidth = Math.min(mMinBottomItemWidth, mMaxItemWidth);
        }
        if (isTablet) {
            return new LinearLayoutCompat.LayoutParams(mMaxContainerHeight, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return new LinearLayoutCompat.LayoutParams(mMinBottomItemWidth, mMaxContainerHeight);
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
        int longest = isTablet ? mContainer.getMeasuredHeight() : mContainer.getMeasuredWidth();
        return longest / mContainer.getChildCount();
    }

    public void setBottomTabs(@MenuRes int menuResId, int[] parentBackgroundColors) {
        if (menuResId != View.NO_ID) {
            MenuBuilder menuBuilder = new MenuBuilder(getContext());
            ((Activity) getContext()).getMenuInflater().inflate(menuResId, menuBuilder);
            if (menuBuilder.size() != parentBackgroundColors.length) {
                throw new IllegalArgumentException("The number of menu items should be equal to the number of parent backgrounds. Make sure you are using both attributes.");
            }
            checkBottomItemGuidelines(menuBuilder.size());
            populateFromMenuResource(menuBuilder, parentBackgroundColors);
        }
    }

    private void populateFromMenuResource(@NonNull MenuBuilder menuBuilder, @NonNull int[] parentBackgroundColors) {
        removeAllTabs();
        int size = menuBuilder.size();
        checkBottomItemGuidelines(size);
        for (int i = 0; i < size; i++) {
            MenuItem item = menuBuilder.getItem(i);
            BottomNavigationItem bottomNavigationItem = BottomNavigationItemBuilder.create(item.getIcon(), String.valueOf(item.getTitle()), parentBackgroundColors[i]);
            bottomNavigationItem.setPosition(i);
            addBottomNavigationItem(bottomNavigationItem);
        }
        updateBottomNavViews();
    }


    private void addBottomNavigationItem(BottomNavigationItem item) {
        View tabView;
        if (!isTablet) {
            BottomNavigationTextView bottomNavigationTextView = new BottomNavigationTextView(getContext(), item);
            bottomNavigationTextView.setActiveColor(mActiveColorFilter);
            bottomNavigationTextView.setInactiveTextColor(mInactiveTextColor);
            bottomNavigationTextView.setTag(item);
            tabView = bottomNavigationTextView;
        } else {
            BottomTabletNavigationTextView tabletNavigationView = new BottomTabletNavigationTextView(getContext(), item);
            tabletNavigationView.setActiveColor(mActiveColorFilter);
            tabletNavigationView.setInactiveTextColor(mInactiveTextColor);
            tabletNavigationView.setTag(item);
            tabView = tabletNavigationView;
        }
        mContainer.addView(tabView, generateBottomItemLayoutParams());
        mBottomTabViews.add(tabView);
    }
    /* Method for manually selecting bottom navigation item
     * @param selectedItemPosition index of item (zero based)
     *
     */

    public void setSelectedItemPosition(int selectedItemPosition) {
        mSelectedItemPosition = selectedItemPosition;
        updateBottomNavViews();
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

    /*  Convinient way to populate bottom navigation layout using MenuBulder
     *
     *  @param BottomTabsBuilder builder,
     *
     */

    public void populateBottomTabItems(@NonNull BottomTabsBuilder builder) {
        if (mContainer.getChildCount() >= MIN_BOTTOM_NAVIGATION_ITEMS) {
            checkBottomItemGuidelines(mContainer.getChildCount());
        }
        List<BottomNavigationItem> build = builder.build();
        for (int i = 0; i < build.size(); i++) {
            BottomNavigationItem item = build.get(i);
            item.setPosition(i);
            addBottomNavigationItem(item);
        }
        updateBottomNavViews();
        selectTabView();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState savedState = new SavedState(parcelable);
        savedState.selectedPosition = mSelectedItemPosition;
        return parcelable;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (state instanceof SavedState) {
            mSelectedItemPosition = ((SavedState) state).selectedPosition;
        }
    }

    private void selectTabView() {
        boolean callListener = false;
        if (mSelectedItemPosition == View.NO_ID) {
            mSelectedItemPosition = 0;
            callListener = true;
        }
        View bottomNavigationTextView = mBottomTabViews.get(mSelectedItemPosition);
        bottomNavigationTextView.requestFocus();
        bottomNavigationTextView.setSelected(true);
        mCurrentNavigationItem = bottomNavigationTextView;
        if (callListener) {
            callListener = false;
            mBottomTabSelectionClickListener.onClick(mCurrentNavigationItem);
        }

    }

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


    static class SavedState extends BaseSavedState {
        int selectedPosition;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.selectedPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.selectedPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}

