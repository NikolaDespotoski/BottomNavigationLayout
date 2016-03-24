package despotoski.nikola.github.com.bottomnavigationlayout;

import android.support.v4.view.ViewCompat;
import android.util.Property;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Nikola on 3/24/2016.
 */
public class Properties {
    private static final String PADDING_TOP_NAME = "paddingTop";
    public static Property<View, Integer> PADDING_TOP = new IntProperty<View>(Integer.class, PADDING_TOP_NAME) {
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
    public static Property<TextView, Integer> TEXT_PAINT_ALPHA = new IntProperty<TextView>(Integer.class, TEXT_VIEW_ALPHA_NAME) {

        @Override
        public Integer get(TextView object) {
            return object.getPaint().getAlpha();
        }

        @Override
        public void set(TextView object, Integer value) {
            object.getPaint().setAlpha(value.intValue());
            ViewCompat.postInvalidateOnAnimation(object);
        }
    };
    private static final String VIEW_WIDTH_NAME = "view_width";
    public static Property<View, Integer> VIEW_WIDTH = new IntProperty<View>(Integer.class, VIEW_WIDTH_NAME) {

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
    public static Property<TextView, Float> TEXT_SIZE = new FloatProperty<TextView>(Float.class, TEXT_SIZE_NAME) {

        @Override
        public Float get(TextView object) {
            return object.getTextSize();
        }

        @Override
        public void set(TextView object, Float value) {
            object.setTextSize(TypedValue.COMPLEX_UNIT_SP, value.floatValue());
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
