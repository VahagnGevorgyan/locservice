package com.locservice.ui.controls;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.widget.TextView;

import com.locservice.utils.Typefaces;

public class CustomTypefaceSpan extends TypefaceSpan {

	private final Typeface newType;

	public CustomTypefaceSpan(String family, Typeface type) {
	    super(family);
	    newType = type;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		applyCustomTypeFace(ds, newType);
	}

	@Override
	public void updateMeasureState(TextPaint paint) {
	    applyCustomTypeFace(paint, newType);
	}
	
	private static void applyCustomTypeFace(Paint paint, Typeface tf) {
	    int oldStyle;
	    Typeface old = paint.getTypeface();
	    if (old == null) {
	        oldStyle = 0;
	    } else {
	        oldStyle = old.getStyle();
	    }
	
	    int fake = oldStyle & ~tf.getStyle();
	    if ((fake & Typeface.BOLD) != 0) {
	        paint.setFakeBoldText(true);
	    }
	
	    if ((fake & Typeface.ITALIC) != 0) {
	        paint.setTextSkewX(-0.25f);
	    }
	
	    paint.setTypeface(tf);
	}
	
	public class CustomTextItem {
		private String font;
		private String text;
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getFont() {
			return font;
		}
		public void setFont(String font) {
			this.font = font;
		}
		
	}
	
	/**
	 * Method for setting text fonts 
	 */
	public static void setTextFonts(Context mContext, String[] fonts, String[] texts, TextView textViewItem) {
		
		String content = texts[0];
		Typeface content_font = Typefaces.get(mContext, fonts[0]);
		// set texts
		if(texts.length > 1) {
			int i = 0;
			while(i < texts.length) {
				String text = texts[i];
				if(content.contains("{" + (i-1) + "}")) {
					content = content.replace("{" + (i-1) + "}", text);
				}
				i++;
			}
		}
		// set fonts
		SpannableStringBuilder SS = new SpannableStringBuilder(content);
		int last_index = 0;
		char[] last_array = null;
		if(texts.length > 1) {
			int index = 1;
			while(index < texts.length) {
				String text = texts[index];
				
				int current_index = content.indexOf(text);
				
				char[] charArrayCurrent = text.toCharArray();
				Typeface current_font = Typefaces.get(mContext, fonts[index]);
				
				if(last_index <= 0) {
					SS.setSpan (new CustomTypefaceSpan("", content_font), 0, current_index, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
					SS.setSpan (new CustomTypefaceSpan("", current_font), current_index, current_index + charArrayCurrent.length,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
				} else {
					SS.setSpan (new CustomTypefaceSpan("", content_font),  last_index + last_array.length + 1, current_index,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
			        SS.setSpan (new CustomTypefaceSpan("", current_font), current_index, current_index + charArrayCurrent.length,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
				}
				
		        last_index = current_index;
		        last_array = charArrayCurrent;
				index++;
			}
			if(last_index + last_array.length < content.length()) {
				SS.setSpan (new CustomTypefaceSpan("", content_font),  last_index + last_array.length + 1, content.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
			}
		}
        // set text
        textViewItem.setText(SS);
		
	} // end method setTextFonts


	/**
	 * Method for setting text fonts
	 */
	public static void setTextFonts2(Context mContext, String[] fonts, String[] texts, TextView textViewItem) {

		String content = texts[0];
		Typeface content_font = Typefaces.get(mContext, fonts[0]);
		// set texts
		if(texts.length > 1) {
			int i = 0;
			while(i < texts.length) {
				String text = texts[i];
				if(content.contains("{" + (i-1) + "}")) {
					content = content.replace("{" + (i-1) + "}", text);
				}
				i++;
			}
		}
		// set fonts
		SpannableStringBuilder SS = new SpannableStringBuilder(content);
		int last_index = 0;
		char[] last_array = null;
		if(texts.length > 1) {
			int index = 1;
			while(index < texts.length) {
				String text = texts[index];

				int current_index = content.indexOf(text);

				char[] charArrayCurrent = text.toCharArray();
				Typeface current_font = Typefaces.get(mContext, fonts[index]);

				if(last_index <= 0) {
					SS.setSpan (new CustomTypefaceSpan("", content_font), 0, current_index, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
					SS.setSpan (new CustomTypefaceSpan("", current_font), current_index, current_index + charArrayCurrent.length,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
				} else {
					SS.setSpan (new CustomTypefaceSpan("", content_font),  last_index + last_array.length + 1, current_index,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
					SS.setSpan (new CustomTypefaceSpan("", current_font), current_index, current_index + charArrayCurrent.length,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
				}

				last_index = current_index;
				last_array = charArrayCurrent;
				index++;
			}
			if(last_index + last_array.length < content.length()) {
				SS.setSpan (new CustomTypefaceSpan("", content_font),  last_index + last_array.length + 1, content.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
			}
		}
		// set text
		textViewItem.setText(SS);

	} // end method setTextFonts
}