package despotoski.nikola.github.com.bottomnavigationlayout;

import android.graphics.drawable.Drawable;

/**
 * Created by Nikola D. on 3/18/2016.
 */
public class BottomNavigationItem {
    private int mIcon;
    private final String mText;
    private Drawable mIconDrawable;
    private int parentColorBackground;
    private int position;

    public BottomNavigationItem(int mIcon, String mText, int parentColorBackground) {
        this.mIcon = mIcon;
        this.mText = mText;
        this.parentColorBackground = parentColorBackground;
    }

    public BottomNavigationItem(Drawable icon, String text, int parentColorBackground) {
        mIconDrawable = icon;
        mText = text;
        this.parentColorBackground = parentColorBackground;
    }


    public int getParentBackgroundColor() {
        return parentColorBackground;
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
