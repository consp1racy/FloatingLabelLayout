package net.xpece.material.floatinglabel;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;

import net.xpece.material.floatinglabel.internal.Utils;

/**
 * Created by Eugen on 25. 3. 2015.
 */
public class FloatingLabelUtils {
    private FloatingLabelUtils() {
    }

    public static Drawable getCompositeErrorDrawable(Drawable commonDrawable, Drawable errorDrawable, int errorColor) {
        errorDrawable = getSimpleErrorDrawable(errorDrawable, errorColor);
        return new LayerDrawable(new Drawable[]{commonDrawable, errorDrawable});
    }

    public static Drawable getSimpleErrorDrawable(Drawable drawable, int color) {
        return Utils.colorizeDrawable(drawable, color);
    }

    @TargetApi(21)
    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context context, @DrawableRes int resId) {
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getDrawable(resId);
        } else {
            return context.getResources().getDrawable(resId);
        }
    }
}
