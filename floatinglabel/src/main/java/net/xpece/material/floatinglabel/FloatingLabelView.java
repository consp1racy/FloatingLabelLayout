package net.xpece.material.floatinglabel;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Eugen on 18. 3. 2015.
 */
public class FloatingLabelView extends AbstractFloatingLabelView {

    private static final String SAVED_SUPER_STATE = "SAVED_SUPER_STATE";
    private static final String SAVED_ORIGINAL_HINT = "SAVED_ORIGINAL_HINT";

    private CharSequence mOriginalHint;
    private int mColorActivated;

    @Override
    protected int getDefaultStyleAttr() {
        return R.attr.floatingLabelViewStyle;
    }

    @Override
    protected int getDefaultStyleRes() {
        return R.style.Widget_FloatingLabelView;
    }

    public FloatingLabelView(Context context) {
        super(context);
        init(context, null, R.attr.floatingLabelViewStyle, R.style.Widget_FloatingLabelView);
    }

    public FloatingLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.floatingLabelViewStyle, R.style.Widget_FloatingLabelView);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public FloatingLabelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle, R.style.Widget_FloatingLabelView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatingLabelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a;
        a = context.obtainStyledAttributes(attrs, R.styleable.FloatingLabelView, defStyleAttr, defStyleRes);

        mColorActivated = a.getColor(R.styleable.FloatingLabelView_flv_colorActivated, 0);
        onColorActivatedChanged();

        a.recycle();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(SAVED_ORIGINAL_HINT, mOriginalHint);
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            mOriginalHint = bundle.getCharSequence(SAVED_ORIGINAL_HINT);

            state = bundle.getParcelable(SAVED_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onLayout() {
        TextView tv = getTextView();
        if (tv != null && tv.length() > 0) {
            show();
        }
    }

    @Override
    protected void onReleaseOwnerView() {
        super.onReleaseOwnerView();
        TextView tv = getTextView();
        if (tv != null && mOriginalHint != null) {
            tv.setHint(mOriginalHint);
            mOriginalHint = null;
        }
    }

    @Override
    protected void onOwnerViewFocusChanged(boolean focused) {
        if (hasErrorState()) {
            setTextColor(getColorError());
        } else if (focused) {
            setTextColorSmooth(getColorActivated());
        } else {
            setTextColorSmooth(getColorDefault());
        }

        if (getTrigger() != Trigger.FOCUS) return;

        TextView tv = getTextView();
        if (focused) {
            if (tv != null) {
                CharSequence hint = tv.getHint();
                if (!TextUtils.isEmpty(hint)) {
                    mOriginalHint = hint;
                    tv.setHint(null);
                }
            }
            show();
        } else {
            if (tv != null) {
                if (TextUtils.isEmpty(tv.getText())) {
                    if (mOriginalHint != null) {
                        tv.setHint(mOriginalHint);
                        mOriginalHint = null;
                    }
                } else {
                    show(); // ensure non-empty TextViews have a label
                    return;
                }
            }
            hide();
        }
    }

    @Override
    protected void onOwnerViewTextChanged(int oldLen, CharSequence s) {
        if (getTrigger() != Trigger.TEXT) return;

        if (!TextUtils.isEmpty(s)) {
            show();
        } else {
            hide();
        }
    }

    protected void onColorActivatedChanged() {
        setTextColor(getPreferredTextColor());
    }

    public void setColorActivated(int color) {
        if (mColorActivated != color) {
            mColorActivated = color;
            onColorActivatedChanged();
        }
    }

    public int getColorActivated() {
        return mColorActivated;
    }

    @Override
    int getPreferredTextColor() {
        View v = getOwnerView();
        if (v != null && v.isFocused()) {
            return getColorActivated();
        } else {
            return getColorDefault();
        }
    }
}
