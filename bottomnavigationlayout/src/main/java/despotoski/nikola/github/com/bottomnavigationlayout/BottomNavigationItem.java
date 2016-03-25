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
