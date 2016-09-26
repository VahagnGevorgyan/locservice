package com.locservice.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.ui.controls.CustomHorizontalScrollView;
import com.locservice.ui.fragments.DriverFragment.DriverRate;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 12 November 2015
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DriverRateAdapter extends ArrayAdapter<DriverRate> {

	private static final String TAG = DriverRateAdapter.class.getSimpleName();

	CustomHorizontalScrollView mScrollView;
	Context context;
	private ArrayList<DriverRate> list;
	int layoutId;
	public Holder holder;
	public View viewItem;
	public int currPosition = 0;
	public View view;


	public DriverRateAdapter(Context context, int textViewResourceId,
			ArrayList<DriverRate> list, CustomHorizontalScrollView scrollView) {
		super(context, textViewResourceId, list);
		this.list = list;
		this.context = context;
		this.mScrollView = scrollView;
		layoutId = textViewResourceId;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public DriverRate getItem(int position) {
		return list.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final LinearLayout mLayout;

		if (convertView == null) {
			mLayout = (LinearLayout) View.inflate(context, layoutId, null);
			holder = new Holder();
			float fontScale = context.getResources().getConfiguration().fontScale;
			holder.textViewName = (TextView) mLayout.findViewById(R.id.textViewName);
			holder.textViewName.setTextSize(TypedValue.COMPLEX_UNIT_SP,
					context.getResources().getInteger(R.integer.int_driver_rate_item_name_text_size) / fontScale);
			holder.textViewValue = (TextView) mLayout.findViewById(R.id.textViewValue);
			holder.textViewValue.setTextSize(TypedValue.COMPLEX_UNIT_SP,
					context.getResources().getInteger(R.integer.int_driver_rate_item_value_text_size) / fontScale);
			holder.textViewValueNumber = (TextView) mLayout.findViewById(R.id.textViewValueNumber);
			holder.textViewValueNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP,
					context.getResources().getInteger(R.integer.int_driver_rate_item_number_text_size) / fontScale);
			mLayout.setTag(holder);
		} else {
			mLayout = (LinearLayout) convertView;
			view = mLayout;
			holder = (Holder) mLayout.getTag();
		}
		DriverRate listSource = getItem(position);
		holder.textViewName.setText(listSource.getName());
		holder.textViewValue.setText(listSource.getValue());
		if (listSource.isNumberVisible()) {
			holder.textViewValueNumber.setVisibility(View.VISIBLE);
		} else {
			holder.textViewValueNumber.setVisibility(View.GONE);
		}

		switch (position) {
		case 0:
			holder.textViewValue.setTextColor(ContextCompat.getColor(getContext(), R.color.green_rate));
			break;
//		case 1:
//			holder.textViewValue.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow_rate));
//			break;
		default:
			break;
		}

		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DriverRateFragment.getView : name : " + listSource.getName() + " : value : " + listSource.getValue());

		return mLayout;
	}


	private class Holder {
		public TextView textViewName;
		public TextView textViewValue;
		public TextView textViewValueNumber;
	}

	public int getCurrentPosition(){
		return currPosition;
	}




}
