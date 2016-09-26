package com.locservice.ui.controls;

import com.locservice.CMAppGlobals;
import com.locservice.utils.Typefaces;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class CustomEditTextView extends EditText {
	
	private Context context;
	private String fontName = null;
	
	public CustomEditTextView(Context context) {
		super(context);
		this.context = context;
		initilaize();
	}
	
	public CustomEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            this.fontName = attrs.getAttributeValue(CMAppGlobals.CUSTOM_ATTR_SCHEMAS, "font");
            initilaize();
        }
    }
	
	private void initilaize() {
        if (null == this.fontName) {
            this.fontName = CMAppGlobals.DEFAULT_FONT;
        }
        if(!isInEditMode()){
        	Typeface font = Typefaces.get(this.context, this.fontName);
        	setTypeface(font);
        }
    }

	@Override
	public void setTypeface(Typeface tf) {
		super.setTypeface(tf);
	}

	@Override
	public void setTextColor(int color) {
		super.setTextColor(color);
	}

	private BackPressedListener mOnImeBack;

    /* constructors */

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
			if (mOnImeBack != null) mOnImeBack.onImeBack(this);
		}
		return super.dispatchKeyEvent(event);
	}

	public void setBackPressedListener(BackPressedListener listener) {
		mOnImeBack = listener;
	}

	public interface BackPressedListener {
		void onImeBack(CustomEditTextView editText);
	}

}
