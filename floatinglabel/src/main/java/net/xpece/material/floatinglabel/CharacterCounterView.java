package net.xpece.material.floatinglabel;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Eugen on 16. 3. 2015.
 */
public class CharacterCounterView extends FloatingHelperView {

    private int mCharacterLimit;

    protected int getDefaultStyle() {
        return R.attr.characterCounterViewStyle;
    }

    protected int getDefaultTheme() {
        return R.style.Widget_FloatingLabelView_CharacterCounter;
    }

    public CharacterCounterView(Context context) {
        super(context);
        init(context, null, getDefaultStyle(), getDefaultTheme());
    }

    public CharacterCounterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, getDefaultStyle(), getDefaultTheme());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CharacterCounterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, getDefaultTheme());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CharacterCounterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a;
        a = context.obtainStyledAttributes(attrs, R.styleable.FloatingLabelView, defStyleAttr, defStyleRes);

        mCharacterLimit = a.getInteger(R.styleable.FloatingLabelView_flv_characterLimit, -1);
        onCharacterLimitChanged(-1);

        a.recycle();

        if (isInEditMode()) {
            if (TextUtils.isEmpty(getText())) {
                setText("0 / " + mCharacterLimit);
            }
        }
    }

    public void setCharacterLimit(int limit) {
        if (limit != mCharacterLimit) {
            int oldLimit = mCharacterLimit;
            mCharacterLimit = limit;
            onCharacterLimitChanged(oldLimit);
        }
    }

    public int getCharacterLimit() {
        return mCharacterLimit;
    }

    protected void onCharacterLimitChanged(int oldLimit) {
        TextView tv = getTextView();
        if (tv == null) return;

        int limit = getCharacterLimit();
        if (limit < 0) {
            hide();
        } else {
            int len = tv.length();
            if ((limit > len && oldLimit > len) || (limit <= len && oldLimit <= len)) {
                // no-op
            } else {
                if (limit <= len) {
                    showDefault();
                } else {
                    showError();
                }
            }
        }
    }

    @Override
    public CharSequence getTextDefault() {
        TextView tv = getTextView();
        if (tv == null) return null;

        return tv.length() + " / " + getCharacterLimit();
    }

    @Override
    public CharSequence getTextError() {
        return getTextDefault();
    }

    @Override
    protected void onOwnerViewTextChanged(int oldLen, CharSequence s) {
        int len = s.length();
        int limit = getCharacterLimit();
        int trigger = getTrigger();
        if ((trigger == Trigger.TEXT && len == 0) || limit < 0) {
            hide();
        } else if (oldLen != len) {
            if (len <= limit) {
                showDefault();
            } else {
                showError();
            }

        }
    }

    @Override
    protected void onOwnerViewFocusChanged(boolean focused) {
        if (getTrigger() != Trigger.FOCUS) return;

        TextView tv = getTextView();
        int len = tv.length();
        int limit = getCharacterLimit();
        if (limit < 0 || (len == 0 && !tv.isFocused())) {
            hide();
        } else if (len > limit) {
            showError();
        } else {
            showDefault();
        }
    }

    public void showError(@StringRes int resId) {
        throw new UnsupportedOperationException();
    }

    public void showError(String text) {
        throw new UnsupportedOperationException();
    }

}
