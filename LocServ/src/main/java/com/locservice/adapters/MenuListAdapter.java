package com.locservice.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.ui.AboutActivity;
import com.locservice.ui.ChatActivity;
import com.locservice.ui.CreditCardsActivity;
import com.locservice.ui.MainActivity;
import com.locservice.ui.OrderHistoryActivity;
import com.locservice.ui.RewardsActivity;
import com.locservice.ui.TariffActivity;
import com.locservice.ui.helpers.MenuHelper;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 02 November 2015
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class MenuListAdapter extends ArrayAdapter<String> {
	
	private static final String TAG = MenuListAdapter.class.getSimpleName();
	
	private static MenuListAdapter mInstance = null;
	
	private static FragmentActivity mContext;
	
	public static MenuListAdapter getInstance(FragmentActivity context, int resource, String[] objects) {
		if(mInstance == null)
			mInstance = new MenuListAdapter(context, resource, objects);
		mContext = context;
		return mInstance;
	}
	
	public MenuListAdapter(Activity context, int resource, String[] objects) {
		super(context, resource, objects);
	}
	
	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				TextView textViewItem = (TextView) v.findViewById(android.R.id.text1);
				switch(event.getAction()) {
		            case MotionEvent.ACTION_DOWN:
		            	textViewItem.setTextColor(Color.parseColor("#A9A9A9"));
		            	break;
		            case MotionEvent.ACTION_MOVE:	
		            	textViewItem.setTextColor(Color.parseColor("#A9A9A9"));
		            	break;
		            case MotionEvent.ACTION_UP:
		                v.performClick();
		                textViewItem.setTextColor(Color.parseColor("#ffffff"));
		                break;
		            default:
		            	textViewItem.setTextColor(Color.parseColor("#ffffff"));
		            	break;
		        }
				return true;
			}
		});
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mContext != null) {
					OpenSelectedActivity(mContext, position);
				}
			}
		});
		return view;
	}
	
	/**
	 * Open selected fragment
	 * @param position - current position
	 */
	public void OpenSelectedActivity(Activity context, int position) {
		Intent intent = null;
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuListAdapter.OpenSelectedActivity : position : " + position);
		switch (position) {
		case 0:
			// MAP
			if(mContext instanceof MainActivity && CMApplication.getTrackingOrderId().isEmpty()) {
				MenuHelper.getInstance().closeDrawer(mContext, Gravity.LEFT);
			} else {
				// Removing order tracking id
				CMApplication.setTrackingOrderId("");
				// Starting MainActivity
				MenuHelper.getInstance().setDrawerOpen(false);
				Intent intent1 = new Intent(mContext, MainActivity.class);
				intent1.putExtra(CMAppGlobals.EXTRA_FROM_MENU, true);
				mContext.finish();
				mContext.startActivity(intent1);
			}
			break;
		case 1:
			// CREDIT CARDS
			intent = new Intent(context, CreditCardsActivity.class);
			context.startActivityForResult(intent, CMAppGlobals.REQUEST_CHANGE_PROFILE_INFO);
			break;
		case 2:
			// ORDER_HISTORY
			intent = new Intent(context, OrderHistoryActivity.class);
			context.startActivityForResult(intent, CMAppGlobals.REQUEST_ORDER_LIST);
			break;
		case 3:
			// REWARDS
			intent = new Intent(context, RewardsActivity.class);
			context.startActivityForResult(intent, CMAppGlobals.REQUEST_CHANGE_PROFILE_INFO);
			break;
		case 4:
			// TARIFF
			intent = new Intent(context, TariffActivity.class);
			context.startActivity(intent);
			break;
		case 5:
			// CHAT
			intent = new Intent(context, ChatActivity.class);
			context.startActivityForResult(intent, CMAppGlobals.REQUEST_CHANGE_PROFILE_INFO);
			break;
		case 6:
			// ABOUT
			intent = new Intent(context, AboutActivity.class);
			context.startActivity(intent);
			break;
		}
	} // end method OpenSelectedActivity
	
}
