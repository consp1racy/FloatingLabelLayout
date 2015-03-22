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
import android.view.View;

import net.xpece.material.floatinglabel.internal.Utils;

/**
 * Created by Eugen on 16. 3. 2015.
 */
public class FloatingHelperView extends AbstractFloatingLabelView {

    private static final String SAVED_SUPER_STATE = "SAVED_SUPER_STATE";
    private static final String SAVED_TEXT_ERROR_ID = "SAVED_TEXT_ERROR_ID";
    private static final String SAVED_TEXT_ERROR = "SAVED_TEXT_ERROR";
    private static final String SAVED_COLOR_ERROR = "SAVED_COLOR_ERROR";
    private static final String SAVED_USE_COLOR_ERROR = "SAVED_USE_COLOR_ERROR";
    private static final String SAVED_BACKGROUND_ERROR_ID = "SAVED_BACKGROUND_ERROR_ID";
//    private static final String SAVED_BACKGROUND_ERROR = "SAVED_BACKGROUND_ERROR";

    private int mTextErrorId;
    private CharSequence mTextError;

    private int mColorError;
    private boolean mUseColorError;

    private int mBackgroundErrorId;
    private Drawable mBackgroundError;

    private Drawable mBackgroundOriginal;
    private boolean mError;

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

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a;
        a = context.obtainStyledAttributes(attrs, R.styleable.FloatingLabelView, defStyleAttr, defStyleRes);

        mTextErrorId = a.getResourceId(R.styleable.FloatingLabelView_flv_textError, 0);
        if (mTextErrorId == 0) {
            mTextError = a.getText(R.styleable.FloatingLabelView_flv_textError);
        }
        onTextErrorChanged();

        mColorError = a.getColor(R.styleable.FloatingLabelView_flv_colorError, 0);
        mBackgroundErrorId = a.getResourceId(R.styleable.FloatingLabelView_flv_ownerViewBackgroundError, 0);
        mUseColorError = a.getBoolean(R.styleable.FloatingLabelView_flv_ownerViewUseColorError, true);
        onColorErrorChanged();

        a.recycle();

        if (isInEditMode()) {
            setText(getTextDefault());
            setVisibility(VISIBLE);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVED_SUPER_STATE, super.onSaveInstanceState());
        bundle.putInt(SAVED_TEXT_ERROR_ID, mTextErrorId);
        bundle.putCharSequence(SAVED_TEXT_ERROR, mTextError);
        bundle.putInt(SAVED_COLOR_ERROR, mColorError);
        bundle.putBoolean(SAVED_USE_COLOR_ERROR, mUseColorError);
        bundle.putInt(SAVED_BACKGROUND_ERROR_ID, mBackgroundErrorId);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            mTextErrorId = bundle.getInt(SAVED_TEXT_ERROR_ID);
            mTextError = bundle.getCharSequence(SAVED_TEXT_ERROR);
            onTextErrorChanged();

            mColorError = bundle.getInt(SAVED_COLOR_ERROR);
            mUseColorError = bundle.getBoolean(SAVED_USE_COLOR_ERROR);
            mBackgroundErrorId = bundle.getInt(SAVED_BACKGROUND_ERROR_ID);
            onColorErrorChanged();

            state = bundle.getParcelable(SAVED_SUPER_STATE);
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
        mTextErrorId = 0;
        mTextError = cs;
        onTextErrorChanged();
    }

    public void setTextError(@StringRes int resId) {
        mTextErrorId = resId;
        mTextError = null;
        onTextErrorChanged();
    }

    public CharSequence getTextError() {
        if (mTextErrorId > 0) {
            return getResources().getText(mTextErrorId);
        } else {
            return mTextError;
        }
    }

    public void setBackgroundError(Drawable d) {
        mBackgroundErrorId = 0;
        mBackgroundError = d;
        onBackgroundErrorChanged();
    }

    public void setBackgroundError(@DrawableRes int resId) {
        mBackgroundErrorId = resId;
        mBackgroundError = null;
        onBackgroundErrorChanged();
    }

    public Drawable getBackgroundError() {
        if (mBackgroundErrorId > 0) {
            return getResources().getDrawable(mBackgroundErrorId);
        } else {
            return mBackgroundError;
        }
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

        if (mBackgroundOriginal != null) {
            Utils.setBackground(ownerView, mBackgroundOriginal);
            mBackgroundOriginal = null;
        }
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
}
