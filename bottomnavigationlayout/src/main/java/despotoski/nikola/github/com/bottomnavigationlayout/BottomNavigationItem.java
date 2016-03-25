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

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Nikola D. on 3/18/2016.
 */
public class BottomNavigationItem {


    private int parentColorBackgroundColor;
    private int mIcon;
    private final String mText;
    private Drawable mIconDrawable;
    private int parentColorBackgroundResId = View.NO_ID;
    private int position;

    public BottomNavigationItem(@DrawableRes int mIcon, @NonNull String mText, @ColorRes int parentColorBackground) {
        this.mIcon = mIcon;
        this.mText = mText;
        this.parentColorBackgroundResId = parentColorBackground;
    }

    public BottomNavigationItem(@NonNull Drawable icon, @NonNull String text, @ColorInt int parentColorBackground, boolean useless) {
        mIconDrawable = icon;
        mText = text;
        this.parentColorBackgroundColor = parentColorBackground;
    }


    public int getParentBackgroundColorResource() {
        return parentColorBackgroundResId;
    }

    public int getParentColorBackgroundColor() {
        return parentColorBackgroundColor;
    }

    public int getIcon() {
        return mIcon;
    }

    public String getText() {
        return mText;
    }

    public Drawable getIconDrawable() {
        return mIconDrawable;
    }

    public int getPosition() {
        return position;
    }

    void setPosition(int p) {
        position = p;
    }
}
