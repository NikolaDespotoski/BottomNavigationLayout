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

public class BottomNavigationItemBuilder {
    private int mIcon;
    private String mText;
    private int parentColorBackground;

    public BottomNavigationItemBuilder setIcon(@DrawableRes int mIcon) {
        this.mIcon = mIcon;
        return this;
    }

    public BottomNavigationItemBuilder setText(@NonNull String mText) {
        this.mText = mText;
        return this;
    }

    public BottomNavigationItemBuilder setParentColorBackground(@ColorRes int parentColorBackground) {
        this.parentColorBackground = parentColorBackground;
        return this;
    }

    public BottomNavigationItem build() {
        return new BottomNavigationItem(mIcon, mText, parentColorBackground);
    }

    public static BottomNavigationItem create(@NonNull Drawable icon, @NonNull String text, @ColorInt int parentColorBackground) {
        return new BottomNavigationItem(icon, text, parentColorBackground, false);
    }
}