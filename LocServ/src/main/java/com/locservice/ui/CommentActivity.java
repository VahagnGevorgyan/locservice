package com.locservice.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.ui.controls.CustomEditTextView;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;

/**
 * Created by Vahagn Gevorgyan
 * 25 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CommentActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = CommentActivity.class.getSimpleName();

    private FrameLayout layoutClear;
    private CustomEditTextView editTextEddFieldHint;
    private TextView textViewOk;
    private FrameLayout layoutBack;
    private EditText editTextEddFieldEntrance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // check network status
        if (Utils.checkNetworkStatus(this)) {
            layoutClear = (FrameLayout) findViewById(R.id.layoutClear);
            layoutClear.setOnClickListener(this);
            textViewOk = (TextView) findViewById(R.id.textViewOk);
            textViewOk.setOnClickListener(this);
            editTextEddFieldHint = (CustomEditTextView) findViewById(R.id.editTextEddFieldHint);
            layoutBack = (FrameLayout) findViewById(R.id.layoutBack);
            layoutBack.setOnClickListener(this);
            editTextEddFieldEntrance = (EditText) findViewById(R.id.editTextEddFieldEntrance);
            // Set old comment
            if (getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_COMMENT) != null
                    && !getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_COMMENT).equals(getResources().getString(R.string.str_comment_title))) {
                editTextEddFieldHint.setText(getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_COMMENT));
                editTextEddFieldHint.setSelection(getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_COMMENT).length());
            }
            // Set ond entrance
            if (getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_ENTRANCE) != null
                    && !getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_ENTRANCE).isEmpty()) {
                editTextEddFieldEntrance.setText(getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_ENTRANCE));
                editTextEddFieldEntrance.setSelection(getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_ENTRANCE).length());
            }
        }

    }

    @Override
    public void onClick(View v) {
        // check network status
        if (Utils.checkNetworkStatus(CommentActivity.this)) {

            switch (v.getId()) {
                case R.id.layoutClear:
                    View view_for_back = CommentActivity.this.getCurrentFocus();
                    if (view_for_back != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view_for_back.getWindowToken(), 0);
                    }
                    onBackPressed();
                    break;
                case R.id.layoutBack:
                    break;
                case R.id.textViewOk:
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommentActivity.onClick ");
                    View view = CommentActivity.this.getCurrentFocus();
                    if (view != null) {
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CommentActivity.onClick : view != null");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    // start search
                    Intent intent = new Intent();
                    intent.putExtra(CMAppGlobals.EXTRA_ORDER_COMMENT, editTextEddFieldHint.getText().toString());
                    intent.putExtra(CMAppGlobals.EXTRA_ORDER_ENTRANCE, editTextEddFieldEntrance.getText().toString());
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommentActivity.onClick : comment : " + editTextEddFieldHint.getText().toString());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    }
}
