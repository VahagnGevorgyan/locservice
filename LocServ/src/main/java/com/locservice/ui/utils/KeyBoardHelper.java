package com.locservice.ui.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyBoardHelper {

	public static void Hide(EditText editText, Context context) {	
		String text = editText.getText().toString();
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		editText.setText(text);
		int textLength = editText.getText().length();
		editText.setSelection(textLength, textLength);		
	}
	
	public static void Show(EditText editText, Context context) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.showSoftInputFromInputMethod(editText.getWindowToken(), 0);
		imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
		//imm.toggleSoftInput(0, 0);
	}	
	
}
