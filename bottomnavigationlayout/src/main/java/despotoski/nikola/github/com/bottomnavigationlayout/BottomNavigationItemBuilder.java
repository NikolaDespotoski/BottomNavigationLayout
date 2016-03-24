package despotoski.nikola.github.com.bottomnavigationlayout;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

public class BottomNavigationItemBuilder {
    private int mIcon;
    private String mText;
    private int parentColorBackground;

    public BottomNavigationItemBuilder setIcon(@DrawableRes  int mIcon) {
        this.mIcon = mIcon;
        return this;
    }

    public BottomNavigationItemBuilder setText(@NonNull  String mText) {
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

    public static BottomNavigationItem create(@NonNull Drawable icon, @NonNull String text, int parentColorBackground) {
        return new BottomNavigationItem(icon, text, parentColorBackground);
    }
}