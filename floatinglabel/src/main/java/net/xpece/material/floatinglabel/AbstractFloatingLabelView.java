package net.xpece.material.floatinglabel;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import net.xpece.material.floatinglabel.internal.OnFocusChangeListenerWrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import hugo.weaving.DebugLog;

/**
 * Created by Eugen on 18. 3. 2015.
 */
abstract class AbstractFloatingLabelView extends TextView {

    protected static final long ANIMATION_DURATION = 150; //short

    private static final int[] ATTRS_TEXT_APPEARANCE = {
        android.R.attr.textAppearance
    };

    private static final String SAVED_SUPER_STATE = "SAVED_SUPER_STATE";
    private static final String SAVED_TRIGGER = "SAVED_TRIGGER";
    private static final String SAVED_OWNER_VIEW_POSITION = "SAVED_OWNER_VIEW_POSITION";
    private static final String SAVED_OWNER_VIEW_ID = "SAVED_OWNER_VIEW_ID";
    private static final String SAVED_TEXT_DEFAULT_ID = "SAVED_TEXT_DEFAULT_ID";
    private static final String SAVED_TEXT_DEFAULT = "SAVED_TEXT_DEFAULT";
    private static final String SAVED_COLOR_DEFAULT = "SAVED_COLOR_DEFAULT";

    private int mOwnerViewId;
    private View mOwnerView;
    @Position
    private int mOwnerViewPosition;
    @Trigger
    private int mTrigger;
    private int mTextDefaultId;
    private CharSequence mTextDefault;
    private int mColorDefault;

    private boolean mWasSupposedToBeVisible;

    private OnClickListener mOnClickRequestFocus = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOwnerView != null) {
                mOwnerView.requestFocus();
            }
        }
    };

    private final OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

        @Override
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public void onFocusChange(View view, boolean focused) {
            if (mTrigger == Trigger.FOCUS) {
                onOwnerViewFocusChanged(focused);
            }
        }
    };

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onOwnerViewTextChanged(start + before, s);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    protected int getDefaultStyle() {
        return R.attr.floatingLabelViewStyle;
    }

    protected int getDefaultTheme() {
        return R.style.Widget_FloatingLabelView;
    }

    public AbstractFloatingLabelView(Context context) {
        super(context);
        init(context, null, getDefaultStyle(), getDefaultTheme());
    }

    public AbstractFloatingLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, getDefaultStyle(), getDefaultTheme());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AbstractFloatingLabelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle, getDefaultTheme());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractFloatingLabelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    @DebugLog
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        mWasSupposedToBeVisible = getVisibility() == VISIBLE;
        setVisibility(INVISIBLE);

        TypedArray a;

        a = context.obtainStyledAttributes(attrs, ATTRS_TEXT_APPEARANCE, defStyleAttr, defStyleRes);
        int textAppearance = a.getResourceId(0, android.R.style.TextAppearance_Small);
        a.recycle();
        setTextAppearance(context, textAppearance);

        a = context.obtainStyledAttributes(attrs, R.styleable.FloatingLabelView, defStyleAttr, defStyleRes);

        mOwnerViewId = a.getResourceId(R.styleable.FloatingLabelView_flv_ownerView, 0);
        mOwnerViewPosition = a.getInteger(R.styleable.FloatingLabelView_flv_ownerViewPosition, 0);
        mTrigger = a.getInteger(R.styleable.FloatingLabelView_flv_trigger, 0);

        mTextDefaultId = a.getResourceId(R.styleable.FloatingLabelView_flv_textDefault, 0);
        if (mTextDefaultId == 0) {
            mTextDefault = a.getText(R.styleable.FloatingLabelView_flv_textDefault);
        }
        onTextDefaultChanged();

        mColorDefault = a.getColor(R.styleable.FloatingLabelView_flv_colorDefault, 0);
        onColorDefaultChanged();

        a.recycle();

        setOnClickListener(mOnClickRequestFocus);

        if (isInEditMode()) {
            setText(getTextDefault());
            setVisibility(VISIBLE);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVED_SUPER_STATE, super.onSaveInstanceState());
        bundle.putInt(SAVED_TRIGGER, mTrigger);
        bundle.putInt(SAVED_OWNER_VIEW_POSITION, mOwnerViewPosition);
        bundle.putInt(SAVED_OWNER_VIEW_ID, mOwnerViewId);
        bundle.putInt(SAVED_TEXT_DEFAULT_ID, mTextDefaultId);
        bundle.putCharSequence(SAVED_TEXT_DEFAULT, mTextDefault);
        bundle.putInt(SAVED_COLOR_DEFAULT, mColorDefault);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            mTrigger = bundle.getInt(SAVED_TRIGGER);
            mOwnerViewId = bundle.getInt(SAVED_OWNER_VIEW_ID);
            mOwnerViewPosition = bundle.getInt(SAVED_OWNER_VIEW_POSITION);

            mTextDefaultId = bundle.getInt(SAVED_TEXT_DEFAULT_ID);
            mTextDefault = bundle.getCharSequence(SAVED_TEXT_DEFAULT);
            onTextDefaultChanged();

            mColorDefault = bundle.getInt(SAVED_COLOR_DEFAULT);
            onColorDefaultChanged();

            if (mTrigger == Trigger.FOCUS) {
                View v = getOwnerView();
                if (v != null) {
                    onOwnerViewFocusChanged(v.isFocused());
                }
            } else if (mTrigger == Trigger.TEXT) {
                TextView tv = getTextView();
                if (tv != null) {
                    onOwnerViewTextChanged(0, tv.getText());
                }
            }

            state = bundle.getParcelable(SAVED_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    @DebugLog
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            if (mOwnerViewId != 0) {
                View v = ((ViewGroup) getParent()).findViewById(mOwnerViewId);
                setOwnerView(v);
                mOwnerViewId = 0;

                if (getTrigger() == Trigger.MANUAL) {
                    show();
                }
            }
        }
    }

    public void setOwnerView(View v) {
        if (v == null) {
            setVisibility(INVISIBLE);
        }
        if (v != mOwnerView) {
            releaseOwnerView();
            mOwnerView = v;
            setupOwnerView();
        }
    }

    public View getOwnerView() {
        return mOwnerView;
    }

    protected TextView getTextView() {
        return mOwnerView instanceof TextView ? (TextView) mOwnerView : null;
    }

    public void setTextDefault(CharSequence cs) {
        mTextDefaultId = 0;
        mTextDefault = cs;
        onTextDefaultChanged();
    }

    public void setTextDefault(@StringRes int resId) {
        mTextDefaultId = resId;
        mTextDefault = null;
        onTextDefaultChanged();
    }

    public CharSequence getTextDefault() {
        if (mTextDefaultId > 0) {
            return getResources().getText(mTextDefaultId);
        } else {
            return mTextDefault;
        }
    }

    public int getTrigger() {
        return mTrigger;
    }

    public void setTrigger(int trigger) {
        if (trigger != mTrigger) {
            mTrigger = trigger;

            if (mOwnerView != null) {
                onTriggerChanged();
            }
        }
    }

    public void setColorDefault(int color) {
        if (mColorDefault != color) {
            mColorDefault = color;
            onColorDefaultChanged();
        }
    }

    public int getColorDefault() {
        return mColorDefault;
    }

    @DebugLog
    public void show() {
        if (getVisibility() == VISIBLE) return;

        setVisibility(View.VISIBLE);
        ViewPropertyAnimator.animate(this).cancel();
        ViewHelper.setAlpha(this, 0f);
        switch (mOwnerViewPosition) {
            case Position.BOTTOM:
                ViewHelper.setTranslationY(this, getHeight() / 2);
                ViewHelper.setTranslationX(this, 0f);
                break;
            case Position.TOP:
                ViewHelper.setTranslationY(this, -getHeight() / 2);
                ViewHelper.setTranslationX(this, 0f);
                break;
            case Position.LEFT:
                ViewHelper.setTranslationX(this, -getWidth() / 2);
                ViewHelper.setTranslationY(this, 0f);
                break;
            case Position.RIGHT:
                ViewHelper.setTranslationX(this, getWidth() / 2);
                ViewHelper.setTranslationY(this, 0f);
                break;
        }
        ViewPropertyAnimator.animate(this)
            .alpha(1f)
            .translationY(0f)
            .translationX(0f)
            .setDuration(ANIMATION_DURATION)
            .setListener(null).start();
    }

    @DebugLog
    public void hide() {
        if (getVisibility() != VISIBLE) return;

        float translationX = 0, translationY = 0;
        switch (mOwnerViewPosition) {
            case Position.BOTTOM:
                translationY = getHeight();
                break;
            case Position.TOP:
                translationY = -getHeight();
                break;
            case Position.LEFT:
                translationX = -getWidth();
                break;
            case Position.RIGHT:
                translationX = getWidth();
                break;
        }
        translationX /= 2;
        translationY /= 2;

        ViewPropertyAnimator.animate(this).cancel();
        ViewHelper.setAlpha(this, 1f);
        ViewHelper.setTranslationY(this, 0f);
        ViewHelper.setTranslationX(this, 0f);
        ViewPropertyAnimator.animate(this)
            .alpha(0f)
            .translationY(translationY)
            .translationX(translationX)
            .setDuration(ANIMATION_DURATION)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(View.INVISIBLE);
                    onHide();
                }
            }).start();
    }

    @DebugLog
    protected void releaseOwnerView() {
        if (mOwnerView != null) {
            OnFocusChangeListenerWrapper.remove(mOwnerView, mOnFocusChangeListener);
            if (mOwnerView instanceof TextView) {
                ((TextView) mOwnerView).removeTextChangedListener(mTextWatcher);
            }
            onReleaseOwnerView();
        }
    }

    @DebugLog
    protected void setupOwnerView() {
        if (mOwnerView != null) {
            OnFocusChangeListenerWrapper.add(mOwnerView, mOnFocusChangeListener);
            if (mOwnerView instanceof TextView) {
                ((TextView) mOwnerView).addTextChangedListener(mTextWatcher);
            }

            onTriggerChanged();

            onSetupOwnerView();
        }
    }

    protected void onOwnerViewFocusChanged(boolean focused) {}

    /**
     * This gets called only if owner view is a {@link android.widget.TextView}. No handling of hint
     * is needed as the hint goes away when anything is typed in.
     *
     * @param oldLen
     * @param s
     */
    protected void onOwnerViewTextChanged(int oldLen, CharSequence s) {}

    protected void onTriggerChanged() {
        if (getTrigger() == Trigger.FOCUS) {
            onOwnerViewFocusChanged(getOwnerView().isFocused());
        } else {
            TextView tv = getTextView();
            if (tv != null) {
                if (getTrigger() == Trigger.TEXT) {
                    CharSequence text = tv.getText();
                    onOwnerViewTextChanged(0, text);
                }
            }
        }
    }

    protected void onReleaseOwnerView() {}

    protected void onSetupOwnerView() {}

    protected void onTextDefaultChanged() {
        setText(getTextDefault());
    }

    protected void onColorDefaultChanged() {
        setTextColor(getColorDefault());
    }

    protected void onHide() {}

    /**
     * @param target
     * @see {@code http://stackoverflow.com/questions/18216285/android-animate-color-change-from-color-to-color}
     */
    protected final void setTextColorSmooth(final int target) {
        if (getVisibility() != VISIBLE) {
            setTextColor(target);
            return;
        }

        final int source = getCurrentTextColor();

        final float[] from = new float[3];
        final float[] to = new float[3];
        final int alphaSource = Color.alpha(source);
        final int alphaTarget = Color.alpha(target);

        Color.colorToHSV(source, from);
        Color.colorToHSV(target, to);
        final int alphaDiff = alphaTarget - alphaSource;

        final float[] hsv = new float[3];
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1).setDuration(ANIMATION_DURATION);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();
//                setTextColor(Color.HSVToColor(hsv));
                final int alpha = alphaSource + (int) (alphaDiff * animation.getAnimatedFraction());
                setTextColor(Color.HSVToColor(hsv) & ((alpha << 24) | 0xffffff));
            }
        });
        anim.start();
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Trigger {
        @Trigger
        public static final int MANUAL = 0;
        @Trigger
        public static final int FOCUS = 1;
        @Trigger
        public static final int TEXT = 2;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Position {
        @Position
        public static final int UNDEFINED = 0;
        @Position
        public static final int LEFT = 1;
        @Position
        public static final int TOP = 2;
        @Position
        public static final int RIGHT = 3;
        @Position
        public static final int BOTTOM = 4;
    }
}
