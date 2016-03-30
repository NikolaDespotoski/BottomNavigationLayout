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

import android.support.annotation.IdRes;

import java.lang.ref.WeakReference;

/**
 * Created by nikola on 3/26/16.
 */
public abstract class FragmentCrossFaderHelper implements BottomTabLayout.OnNavigationItemSelectionListener {
    private final BottomTabLayout mBottomTabLayout;
    private final WeakReference<BottomTabLayout.OnNavigationItemSelectionListener> mListenerRef;

    public FragmentCrossFaderHelper(@IdRes int fragmentContainer, BottomTabLayout bottomTabLayout, BottomTabLayout.OnNavigationItemSelectionListener listener) {
        mListenerRef = new WeakReference<>(listener);
        mBottomTabLayout = bottomTabLayout;
        mBottomTabLayout.addOnNavigationItemSelectionListener(this);
    }

    @Override
    public void onBottomNavigationItemSelected(BottomNavigationItem item) {
        
    }

    @Override
    public void onBottomNavigationItemUnselected(BottomNavigationItem item) {

    }
}
