package com.locservice.ui.controls;

import com.locservice.CMAppGlobals;
import com.locservice.utils.Typefaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;


@SuppressLint("NewApi") public class CustomNumberPicker extends NumberPicker {

    private Context context;
    private String fontName = null;
    
    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            this.fontName = attrs.getAttributeValue(CMAppGlobals.CUSTOM_ATTR_SCHEMAS, "font");
        }
    }
    
    private void initilaize(EditText editTextView) {
        if (null == this.fontName) {
            this.fontName = CMAppGlobals.DEFAULT_FONT;
        }
        if(!isInEditMode()){
        	Typeface font = Typefaces.get(this.context, this.fontName);
        	editTextView.setTypeface(font);
        }
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    private void updateView(View view) {
        if(view instanceof EditText){
            initilaize((EditText) view);
            ((EditText) view).setTextSize(20);
            ((EditText) view).setTextColor(Color.BLACK);
        }
    }
}
