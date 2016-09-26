package com.locservice.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.EditText;

import com.locservice.R;
import com.locservice.ui.emojicon.EmojiconHandler;


public class EmojiconEditText extends EditText {
    private int mEmojiconSize;
	private Activity activity;

    public EmojiconEditText(Context context) {
        super(context);
        mEmojiconSize = (int) getTextSize();

    }
    
    public EmojiconEditText(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	init(attrs);
    }

    public EmojiconEditText(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	init(attrs);
    }

    public void setActivityForEditText(Activity act){
    	this.activity = act;
    }

    @Override
    public void setSelection(int start, int stop) {
        
        super.setSelection(start, stop);
    }


    @Override
    public void setSelection(int index) {
       
        super.setSelection(index);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        //setSelection(getText().length());
        super.onDraw(canvas);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
        mEmojiconSize = (int) a.getDimension(R.styleable.Emojicon_emojiconSize, getTextSize());
        a.recycle();
        setText(getText());    
        setMaxLines(3);
    }
     
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        EmojiconHandler.addEmojis(getContext(), getText(), mEmojiconSize);
        this.setTextColor(Color.BLACK);
        boolean toEnableSendImage = false;
        if(!text.toString().trim().isEmpty()){
        	toEnableSendImage = true;
        }

    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
    }

 }
