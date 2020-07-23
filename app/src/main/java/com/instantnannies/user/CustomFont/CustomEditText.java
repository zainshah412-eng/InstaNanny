package com.instantnannies.user.CustomFont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class CustomEditText extends AppCompatEditText {

    public CustomEditText(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("Font/CircularStd-Book.ttf", context);
        setTypeface(customFont);
    }
}
