/*
 * Copyright (C) 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

public class FloatingLabelView extends AbstractFloatingLabelView {

    private static final String SAVED_SUPER_STATE = "SAVED_SUPER_STATE";
    private static final String SAVED_ORIGINAL_HINT = "SAVED_ORIGINAL_HINT";
    private static final String SAVED_COLOR_ACTIVATED = "SAVED_COLOR_ACTIVATED";

    private CharSequence mOriginalHint;
    private int mColorActivated;

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
        bundle.putInt(SAVED_COLOR_ACTIVATED, mColorActivated);
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            mOriginalHint = bundle.getCharSequence(SAVED_ORIGINAL_HINT);

            mColorActivated = bundle.getInt(SAVED_COLOR_ACTIVATED);
            onColorActivatedChanged();

            state = bundle.getParcelable(SAVED_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
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
        if (getTrigger() != Trigger.FOCUS) return;

        if (focused) {
            setTextColorSmooth(getColorActivated());
        } else {
            setTextColorSmooth(getColorDefault());
        }

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
                    // keep showing label
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

    @Override
    protected void onColorDefaultChanged() {
        View v = getOwnerView();
        if (v == null || !v.isFocused()) {
            setTextColor(getColorDefault());
        }
    }

    protected void onColorActivatedChanged() {
        View v = getOwnerView();
        if (v != null && v.isFocused()) {
            setTextColor(mColorActivated);
        }
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
}
