package net.xpece.material.floatinglabel;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

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
}
