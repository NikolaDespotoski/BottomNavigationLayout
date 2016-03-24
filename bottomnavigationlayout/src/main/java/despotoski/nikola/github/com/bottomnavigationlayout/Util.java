package despotoski.nikola.github.com.bottomnavigationlayout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

/**
 * Created by Nikola on 3/24/2016.
 */
public class Util {

    public static void runOnAttachedToLayout(View v, final Runnable runnable) {
        if (ViewCompat.isLaidOut(v)) runnable.run();
        else {
            v.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    runnable.run();
                    v.removeOnLayoutChangeListener(this);
                }
            });
        }
    }

    public static boolean isNavigationBarTranslucent(Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        getNavigationBarHeight(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && ViewConfiguration.get(activity).hasPermanentMenuKey()) {
            return false;
        }

        /**
         * Copy-paste coding made possible by:
         * http://stackoverflow.com/a/14871974/940036
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display d = activity.getWindowManager().getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            boolean hasSoftwareKeys = (realWidth - displayWidth) > 0
                    || (realHeight - displayHeight) > 0;

            if (!hasSoftwareKeys) {
                return hasSoftwareKeys;
            }
        }
        return (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION) != 0;
    }

    public static int getNavigationBarHeight(Context context) {
        Resources res = context.getResources();
        int navBarIdentifier = res.getIdentifier("navigation_bar_height",
                "dimen", "android");
        int navBarHeight = 0;
        if (navBarIdentifier > 0) {
            navBarHeight = res.getDimensionPixelSize(navBarIdentifier);
        }
        return navBarHeight;
    }

    public static void playTogether(Animator... animators) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        set.start();
    }

    public static void setBackground(View view, @NonNull Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            //noinspection deprecation
            view.setBackgroundDrawable(drawable);
        }
    }
}
