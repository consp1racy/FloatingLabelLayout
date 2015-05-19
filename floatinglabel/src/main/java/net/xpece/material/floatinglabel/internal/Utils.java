package net.xpece.material.floatinglabel.internal;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;

/**
 * Created by Eugen on 9. 3. 2015.
 */
public class Utils {
    private static final String TAG = Utils.class.getName();

    private Utils() {}

    @TargetApi(16)
    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable background) {
        if (Build.VERSION.SDK_INT < 16) {
            view.setBackgroundDrawable(background);
        } else {
            view.setBackground(background);
        }
    }

    @TargetApi(21)
    public static Drawable colorizeDrawable(Drawable d, int color) {
        Drawable.ConstantState cs = d.getConstantState();
        if (cs == null && DrawableWrapperCompat.isWrapper(d)) {
            // if the drawable is wrapped, unwrap it and use it
            cs = DrawableWrapperCompat.unwrap(d).getConstantState();
        }
        if (cs == null) {
            Log.d(TAG, "Could not colorize drawable. " + d + " has no ConstantState.");
            return d;
        }
        d = cs.newDrawable();//.mutate();
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable d2 = d;
            while (d2 instanceof InsetDrawable) {
                // skip any InsetDrawables
                InsetDrawable i = (InsetDrawable) d;
                d2 = i.getDrawable();
            }
            if (d2 instanceof RippleDrawable) {
                // RippleDrawable's ripple has to be colored separately
                RippleDrawable r = (RippleDrawable) d2;
                r.setColor(ColorStateList.valueOf(color));
            }
        }
        // color the overall Drawable
        d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return d;
    }

    public static int blendColorsWithAlpha(int color1, int color2, float ratio) {
        final float inverseRatio = 1f - ratio;
        float a = (Color.alpha(color1) * ratio) + (Color.alpha(color2) * inverseRatio);
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRatio);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRatio);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRatio);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

}
