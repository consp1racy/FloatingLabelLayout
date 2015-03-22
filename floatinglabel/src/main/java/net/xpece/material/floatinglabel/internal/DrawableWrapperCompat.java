package net.xpece.material.floatinglabel.internal;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by Eugen on 8. 3. 2015.
 */
public class DrawableWrapperCompat {
    private static final String TAG = DrawableWrapperCompat.class.getSimpleName();

    private static final Class<?> CLASS_DRAWABLE_WRAPPER;
    private static final Class<?> CLASS_DRAWABLE_WRAPPER_SUPPORT;
    private static final Field FIELD_DRAWABLE;
    private static final Field FIELD_DRAWABLE_SUPPORT;

    static {
        Class<?> drawableWrapper = null;
        Class<?> drawableWrapperSupport = null;
        Field drawable = null;
        Field drawableSupport = null;
        try {
            drawableWrapper = Class.forName("com.android.settings.drawable.DrawableWrapper");
            drawable = drawableWrapper.getDeclaredField("mDrawable");
            drawable.setAccessible(true);
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage() + " not available.");
        }
        try {
            drawableWrapperSupport = Class.forName("android.support.v7.internal.widget.DrawableWrapper");
            drawableSupport = drawableWrapperSupport.getDeclaredField("mDrawable");
            drawableSupport.setAccessible(true);
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage() + " not available.");
        }
        CLASS_DRAWABLE_WRAPPER = drawableWrapper;
        CLASS_DRAWABLE_WRAPPER_SUPPORT = drawableWrapperSupport;
        FIELD_DRAWABLE = drawable;
        FIELD_DRAWABLE_SUPPORT = drawableSupport;
    }

    private DrawableWrapperCompat() {}

    /**
     * Gets the wrapped drawable from supplied {@code DrawableWrapper}.
     *
     * @param wrapper {@code DrawableWrapper} instance.
     * @return Wrapped drawable or supplied drawable on error.
     */
    public static Drawable unwrap(Drawable wrapper) {
        try {
            if (isWrapperSupport(wrapper)) {
                return (Drawable) FIELD_DRAWABLE_SUPPORT.get(wrapper);
            } else if (isWrapperNative(wrapper)) {
                return (Drawable) FIELD_DRAWABLE.get(wrapper);
            } else {
//                Log.d(TAG, wrapper + "is not an instance of DrawableWrapper.");
                return wrapper;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return wrapper;
    }

    public static boolean isWrapper(Drawable drawable) {
        return isWrapperNative(drawable) || isWrapperSupport(drawable);
    }

    private static boolean isWrapperSupport(Drawable drawable) {
        return (CLASS_DRAWABLE_WRAPPER_SUPPORT != null && CLASS_DRAWABLE_WRAPPER_SUPPORT.isInstance(drawable));
    }

    private static boolean isWrapperNative(Drawable drawable) {
        return (CLASS_DRAWABLE_WRAPPER != null && CLASS_DRAWABLE_WRAPPER.isInstance(drawable));
    }
}
