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
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nikola on 3/26/16.
 */
public class BottomNavigationTabletBehavior extends CoordinatorLayout.Behavior<View> {
    public BottomNavigationTabletBehavior() {
    }

    public BottomNavigationTabletBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        boolean b = super.onLayoutChild(parent, child, layoutDirection);
        if (child.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            if (layoutDirection == LayoutDirection.LTR)
                ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).leftMargin = (int) child.getContext().getResources().getDimension(R.dimen.bottom_navigation_height);
            else if (layoutDirection == LayoutDirection.RTL) {
                ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).rightMargin = (int) child.getContext().getResources().getDimension(R.dimen.bottom_navigation_height);
            }
        }
        return b;
    }
}
