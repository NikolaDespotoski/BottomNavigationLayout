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


import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.nineoldandroids.util.Property;

/**
 * Created by nikola on 3/25/16.
 */
public class PrehistoricProperties {
    private static final String PADDING_TOP_NAME = "paddingTop";
    public static final Property<View, Integer> PADDING_TOP = new IntProperty<View>(Integer.class, PADDING_TOP_NAME) {
        @Override
        public Integer get(View object) {
            return object.getPaddingTop();
        }

        @Override
        public void set(View object, Integer value) {
            int left = object.getPaddingLeft();
            int right = object.getPaddingRight();
            int top = value;
            int bottom = object.getPaddingBottom();
            object.setPadding(left, top, right, bottom);
        }
    };


    private static final String TEXT_VIEW_ALPHA_NAME = "painAlpha";
    public static final Property<TextView, Integer> TEXT_PAINT_ALPHA = new IntProperty<TextView>(Integer.class, TEXT_VIEW_ALPHA_NAME) {

        @Override
        public Integer get(TextView object) {
            return object.getPaint().getAlpha();
        }

        @Override
        public void set(TextView object, Integer value) {
            object.getPaint().setAlpha(value);
            ViewCompat.postInvalidateOnAnimation(object);
        }
    };
    private static final String VIEW_WIDTH_NAME = "view_width";
    public static final Property<View, Integer> VIEW_WIDTH = new IntProperty<View>(Integer.class, VIEW_WIDTH_NAME) {

        @Override
        public Integer get(View object) {
            return object.getLayoutParams().width;
        }

        @Override
        public void set(View object, Integer value) {
            object.getLayoutParams().width = value;
            object.requestLayout();
        }
    };

    private static final String TEXT_SIZE_NAME = "textSize";
    public static final Property<TextView, Float> TEXT_SIZE = new FloatProperty<TextView>(Float.class, TEXT_SIZE_NAME) {

        @Override
        public Float get(TextView object) {
            return object.getTextSize();
        }

        @Override
        public void set(TextView object, Float value) {
            object.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
        }
    };


    private static abstract class IntProperty<T extends View> extends Property<T, Integer> {
        public IntProperty(Class<Integer> type, String name) {
            super(type, name);
        }
    }

    private abstract static class FloatProperty<T extends View> extends Property<T, Float> {

        public FloatProperty(Class<Float> type, String name) {
            super(type, name);
        }
    }
}
