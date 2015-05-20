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
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import net.xpece.material.floatinglabel.internal.Utils;

/**
 * Created by Eugen on 16. 3. 2015.
 */
public class FloatingHelperView extends AbstractFloatingLabelView {
    private static final String TAG = FloatingHelperView.class.getSimpleName();

    private static final String SAVED_ERROR = "savedError";
    private static final String SAVED_SUPER_STATE = "superState";

    private boolean mSavedError;

    private CharSequence mTextError;

    private boolean mUseColorError;
    private Drawable mBackgroundError;
    private Drawable mProcessedBackgroundError;
    private Drawable mBackgroundOriginal;
    private boolean mOverriddenBackground;

    private int mTitleViewId;
    private FloatingLabelView mTitleView;

    @Override
    protected int getDefaultStyleAttr() {
        return R.attr.floatingHelperViewStyle;
    }

    @Override
    protected int getDefaultStyleRes() {
        return R.style.Widget_FloatingLabelView_Helper;
    }

    public FloatingHelperView(Context context) {
        super(context);
        init(context, null, getDefaultStyleAttr(), getDefaultStyleRes());
    }

    public FloatingHelperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, getDefaultStyleAttr(), getDefaultStyleRes());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public FloatingHelperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, getDefaultStyleRes());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatingHelperView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a;
        a = context.obtainStyledAttributes(attrs, R.styleable.FloatingLabelView, defStyleAttr, defStyleRes);

        mTextError = a.getText(R.styleable.FloatingLabelView_flv_textError);
        onTextErrorChanged();

        mBackgroundError = a.getDrawable(R.styleable.FloatingLabelView_flv_ownerViewBackgroundError);
        mUseColorError = a.getBoolean(R.styleable.FloatingLabelView_flv_ownerViewUseColorError, true);
        onBackgroundErrorChanged();

        mTitleViewId = a.getResourceId(R.styleable.FloatingLabelView_flv_titleView, 0);

        a.recycle();

        if (isInEditMode()) {
            setText(getTextDefault());
            setVisibility(VISIBLE);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle b = new Bundle();
        b.putParcelable(SAVED_SUPER_STATE, super.onSaveInstanceState());
        b.putBoolean(SAVED_ERROR, hasErrorState());
        return b;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            mSavedError = ((Bundle) state).getBoolean(SAVED_ERROR);
            state = ((Bundle) state).getParcelable(SAVED_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            FloatingLabelView v = (FloatingLabelView) ((ViewGroup) getParent()).findViewById(mTitleViewId);
            setTitleView(v);
        }
    }

    public void setTitleView(FloatingLabelView v) {
        if (v == null) {
            setVisibility(INVISIBLE);
        }
        if (v != mTitleView) {
            releaseTitleView();
            mTitleView = v;
            setupTitleView();
        }
    }

    public FloatingLabelView getTitleView() {
        return mTitleView;
    }

    protected void releaseTitleView() {
        if (mTitleView != null) {
            mTitleView.setErrorState(false);

            onReleaseTitleView();
        }
    }

    protected void setupTitleView() {
        if (mTitleView != null) {
            mTitleView.setErrorState(hasErrorState());

            onSetupTitleView();
        }
    }

    protected void onReleaseTitleView() {
    }

    protected void onSetupTitleView() {
    }

    @Override
    protected void onErrorStateChanged() {
        if (mTitleView != null) {
            mTitleView.setErrorState(hasErrorState());
        }
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
        mBackgroundError = ContextCompat.getDrawable(getContext(), resId);
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
        if (!hasErrorState()) {
            View ownerView = getOwnerView();
            if (ownerView != null) {
                overrideOwnerViewColors();
            }

            setTextColor(getColorError());

            setErrorState(true);
        }

        setText(getTextError());

        show();
    }

    public void showDefault() {
        if (hasErrorState()) {
            restoreOwnerViewColors();

            setTextColor(getColorDefault());

            setErrorState(false);
        }

        setText(getTextDefault());

        show();
    }

    @Override
    protected void onHide() {
        restoreOwnerViewColors();

        setErrorState(false);
    }

    @Override
    protected void onReleaseOwnerView() {
        restoreOwnerViewColors();
    }

    @Override
    protected void onColorErrorChanged() {
        super.onColorErrorChanged();

        mProcessedBackgroundError = null;
        if (hasErrorState()) {
            overrideOwnerViewColors();
        }
    }

    @Override
    protected void onColorDefaultChanged() {
        if (!hasErrorState()) {
            setTextColor(getColorDefault());
        }
    }

    protected void onTextErrorChanged() {
        if (hasErrorState()) {
            setText(getTextError());
        }
    }

    @Override
    protected void onTextDefaultChanged() {
        if (!hasErrorState()) {
            setText(getTextDefault());
        }
    }

    protected void onBackgroundErrorChanged() {
        mProcessedBackgroundError = null;
        if (hasErrorState()) {
            overrideOwnerViewColors();
        }
    }

    protected void onUseColorErrorChanged() {
        mProcessedBackgroundError = null;
        if (hasErrorState()) {
            overrideOwnerViewColors();
        }
    }

    private void restoreOwnerViewColors() {
        View ownerView = getOwnerView();
        if (ownerView == null) return;

        // original background may have been null in the first place
        if (mOverriddenBackground) {
            Utils.setBackground(ownerView, mBackgroundOriginal);
            mBackgroundOriginal = null;
            mOverriddenBackground = false;
        }
    }

    private void overrideOwnerViewColors() {
        View ownerView = getOwnerView();
        if (ownerView == null) return;

        if (!mOverriddenBackground) {
            mBackgroundOriginal = ownerView.getBackground();
            mOverriddenBackground = true;
        }

        if (mProcessedBackgroundError == null) {
            Drawable backgroundError = getBackgroundError();
            boolean useColorError = getUseColorError();

            Drawable d;
            if (backgroundError != null) {
                d = backgroundError;
            } else {
                d = mBackgroundOriginal;
            }
            if (d != null && useColorError) {
//                d = d.getConstantState().newDrawable();
//                d = DrawableCompat.wrap(d);
//                DrawableCompat.setTint(d, getColorError());
                d = Utils.colorizeDrawable(d, getColorError());
            }
            mProcessedBackgroundError = d;
        }
        Utils.setBackground(ownerView, mProcessedBackgroundError);
    }

    @Override
    protected void onOwnerViewTextChanged(int oldLen, CharSequence s) {
        int trigger = getTrigger();

        if (hasErrorState()) {
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
            if (!hasErrorState()) {
                showDefault();
            }
        } else {
            if (!hasErrorState()) {
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
