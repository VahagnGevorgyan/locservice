package com.locservice.ui.emojicon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.DynamicDrawableSpan;

class EmojiconSpan extends DynamicDrawableSpan {
    private final Context mContext;
    private final int mResourceId;
    private final int mSize;
    private Drawable mDrawable;

    public EmojiconSpan(Context context, int resourceId, int size) {
        super();
        mContext = context;
        mResourceId = resourceId;
        mSize = size;
    }

    public Drawable getDrawable() {
        if (mDrawable == null) {
            try {
            	mDrawable = ContextCompat.getDrawable(mContext, mResourceId);
                int size = mSize;
                mDrawable.setBounds(0, 0, size, size);
            } catch (Exception e) {
                // swallow
            }
        }
        return mDrawable;
    }
}