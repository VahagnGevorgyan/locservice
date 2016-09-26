package com.locservice.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.adapters.LanguageListAdapter;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 22 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class LanguageListActivity extends Activity implements View.OnClickListener, LanguageListAdapter.OnItemClickListener {

    private static final String TAG = LanguageListActivity.class.getSimpleName();
    private LinearLayout layoutBack;
    private RecyclerView recyclerViewLanguages;
    private LanguageListAdapter languageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);

        // check network status
        if (Utils.checkNetworkStatus(this)) {

            layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
            layoutBack.setOnTouchListener(new View.OnTouchListener() {
                ImageView imageViewBack = (ImageView) layoutBack.findViewById(R.id.imageViewBack);

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return CommonHelper.setOnTouchImage(imageViewBack, event);
                }
            });
            layoutBack.setOnClickListener(this);

            recyclerViewLanguages = (RecyclerView) findViewById(R.id.recyclerViewLanguages);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerViewLanguages.setLayoutManager(layoutManager);
            List<String> languageList = new ArrayList<String >(Arrays.asList(getResources().getStringArray(R.array.languages)));
            languageListAdapter = new LanguageListAdapter(this, languageList);
            languageListAdapter.setOnItemClickListener(this);
            recyclerViewLanguages.setAdapter(languageListAdapter);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position, String language) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: LanguageListActivity : dateOnItemClick"
                + " : view : " + view
                + " : position : " + position
                + " : language : " + language);

        Toast.makeText(LanguageListActivity.this, language, Toast.LENGTH_SHORT).show();
    }
}
