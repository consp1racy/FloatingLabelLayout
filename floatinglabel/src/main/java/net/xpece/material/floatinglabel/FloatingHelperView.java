package net.xpece.material.floatinglabel;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import net.xpece.material.floatinglabel.internal.Utils;

import hugo.weaving.DebugLog;

/**
 * Created by Eugen on 16. 3. 2015.
 */
public class FloatingHelperView extends AbstractFloatingLabelView {
    private static final String TAG = FloatingHelperView.class.getSimpleName();

    private static final String SAVED_ERROR = "savedError";
    private static final String SAVED_SUPER_STATE = "superState";

    private CharSequence mTextError;

    private int mColorError;
    private boolean mUseColorError;
    private Drawable mBackgroundError;
    private Drawable mBackgroundOriginal;

    private boolean mError;
    private boolean mSavedError;

    protected int getDefaultStyle() {
        return R.attr.floatingHelperViewStyle;
    }

    protected int getDefaultTheme() {
        return R.style.Widget_FloatingLabelView_Helper;
    }

    public FloatingHelperView(Context context) {
        super(context);
        init(context, null, getDefaultStyle(), getDefaultTheme());
    }

    public FloatingHelperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, getDefaultStyle(), getDefaultTheme());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public FloatingHelperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, getDefaultTheme());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatingHelperView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    @DebugLog
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a;
        a = context.obtainStyledAttributes(attrs, R.styleable.FloatingLabelView, defStyleAttr, defStyleRes);

        mTextError = a.getText(R.styleable.FloatingLabelView_flv_textError);
        onTextErrorChanged();

        mColorError = a.getColor(R.styleable.FloatingLabelView_flv_colorError, 0);
        mBackgroundError = a.getDrawable(R.styleable.FloatingLabelView_flv_ownerViewBackgroundError);
        mUseColorError = a.getBoolean(R.styleable.FloatingLabelView_flv_ownerViewUseColorError, true);
        onColorErrorChanged();

        a.recycle();

        if (isInEditMode()) {
            setText(getTextDefault());
            setVisibility(VISIBLE);
        }
    }

    @Override
    @DebugLog
    public Parcelable onSaveInstanceState() {
        Bundle b = new Bundle();
        b.putParcelable(SAVED_SUPER_STATE, super.onSaveInstanceState());
        b.putBoolean(SAVED_ERROR, mError);
        return b;
    }

    @Override
    @DebugLog
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            mSavedError = ((Bundle) state).getBoolean(SAVED_ERROR);
            state = ((Bundle) state).getParcelable(SAVED_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    public void setColorError(int color) {
        if (color != mColorError) {
            mColorError = color;
            onColorErrorChanged();
        }
    }

    public int getColorError() {
        return mColorError;
    }

    public void setTextError(CharSequence cs) {
        mTextError = cs;
        onTextErrorChanged();
    }

    public void setTextError(@StringRes int resId) {
        mTextError = getContext().getText(resId);
        onTextErrorChanged();
    }

    public CharSequence getTextError() {
        return mTextError;
    }

    public void setBackgroundError(Drawable d) {
        mBackgroundError = d;
        onBackgroundErrorChanged();
    }

    public void setBackgroundError(@DrawableRes int resId) {
        mBackgroundError = FloatingLabelUtils.getDrawable(getContext(), resId);
        onBackgroundErrorChanged();
    }

    public Drawable getBackgroundError() {
        return mBackgroundError;
    }

    public void setUseColorError(boolean value) {
        if (mUseColorError != value) {
            mUseColorError = value;
            onUseColorErrorChanged();
        }
    }

    public boolean getUseColorError() {
        return mUseColorError;
    }

    public void showError(@StringRes int resId) {
        setTextError(resId);
        showError();
    }

    public void showError(String text) {
        setTextError(text);
        showError();
    }

    public void showError() {
        if (!mError) {
            View ownerView = getOwnerView();
            if (ownerView != null) {
                mBackgroundOriginal = ownerView.getBackground();
                overrideOwnerViewColors();
            }

            setTextColor(getColorError());

            mError = true;
        }

        setText(getTextError());

        show();
    }

    public void showDefault() {
        if (mError) {
            restoreOwnerViewColors();

            setTextColor(getColorDefault());

            mError = false;
        }

        setText(getTextDefault());

        show();
    }

    @Override
    protected void onHide() {
        restoreOwnerViewColors();

        mError = false;
    }

    @Override
    protected void onReleaseOwnerView() {
        restoreOwnerViewColors();
    }

    protected void onColorErrorChanged() {
        if (mError) {
            overrideOwnerViewColors();
            setTextColor(mColorError);
        }
    }

    @Override
    protected void onColorDefaultChanged() {
        if (!mError) {
            setTextColor(getColorDefault());
        }
    }

    protected void onTextErrorChanged() {
        if (mError) {
            setText(getTextError());
        }
    }

    @Override
    protected void onTextDefaultChanged() {
        if (!mError) {
            setText(getTextDefault());
        }
    }

    protected void onBackgroundErrorChanged() {
        if (mError) {
            overrideOwnerViewColors();
        }
    }

    protected void onUseColorErrorChanged() {
        if (mError) {
            overrideOwnerViewColors();
        }
    }

    private void restoreOwnerViewColors() {
        View ownerView = getOwnerView();
        if (ownerView == null) return;

        // original background may have been null in the first place
//        if (mBackgroundOriginal != null) {
        Utils.setBackground(ownerView, mBackgroundOriginal);
        mBackgroundOriginal = null;
//        }
    }

    private void overrideOwnerViewColors() {
        View ownerView = getOwnerView();
        if (ownerView == null) return;

        Drawable backgroundError = getBackgroundError();
        boolean useColorError = getUseColorError();

        Drawable d;
        if (backgroundError != null) {
            if (useColorError) {
                d = Utils.colorizeDrawable(backgroundError, getColorError());
            } else {
                d = backgroundError;
            }
        } else {
            if (useColorError && mBackgroundOriginal != null) {
                d = Utils.colorizeDrawable(mBackgroundOriginal, getColorError());
            } else {
                d = mBackgroundOriginal;
            }
        }
        if (d != mBackgroundOriginal) {
            Utils.setBackground(ownerView, d);
        }
    }

    @Override
    @DebugLog
    protected void onOwnerViewTextChanged(int oldLen, CharSequence s) {
        int trigger = getTrigger();

        if (mError) {
            // clear error
            if (trigger == Trigger.TEXT) {
                if (!TextUtils.isEmpty(s)) {
                    showDefault();
                } else {
                    hide();
                }
            } else if (trigger == Trigger.FOCUS) {
                if (getOwnerView().isFocused()) {
                    showDefault();
                } else {
                    hide();
                }
            } else {
                showDefault();
            }
        } else {
            if (trigger != Trigger.TEXT) return;

            if (!TextUtils.isEmpty(s)) {
                show();
            } else {
                hide();
            }
        }
    }

    @Override
    @DebugLog
    protected void onOwnerViewFocusChanged(boolean focused) {
        if (getTrigger() != Trigger.FOCUS) return;

        if (focused) {
            if (!mError) {
                showDefault();
            }
        } else {
            if (!mError) {
                hide();
            }
        }
    }

    @Override
    protected void onSetupOwnerView() {
        Log.d(TAG, "mSavedError=" + mSavedError);
        if (mSavedError) {
            mSavedError = false;
            showError();
        }
    }
}
