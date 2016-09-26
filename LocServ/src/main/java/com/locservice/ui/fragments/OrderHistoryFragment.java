package com.locservice.ui.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.adapters.OrderHistoryAdapter;
import com.locservice.api.entities.Order;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.manager.OrderManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.OrderInfoActivity;
import com.locservice.ui.RegisterActivity;
import com.locservice.ui.helpers.OrderState;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.Logger;
import com.locservice.utils.OrderHistoryComparator;
import com.locservice.utils.Utils;
import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderHistoryFragment extends Fragment {
	
	private static final String TAG = OrderHistoryFragment.class.getSimpleName();
	
	private View rootView;

	private LinearLayout layoutBack;
	private OrderHistoryAdapter recyclerAdapter;

	private RecyclerView recyclerViewOrders;

	private FragmentActivity mContext;
	private List<Order> orderList = new ArrayList<Order>();
	private RelativeLayout layoutHeader;
	private ProgressBar progressBar;
	private String currentOrderId;
	private int currentOrderPosition;

	private OrderHistoryAdapter.OnItemClickListener mItemClickListener = new OrderHistoryAdapter.OnItemClickListener() {
		@Override
		public void activeOrdersOnItemClick(View view, int position, Order order) {
			Order orderItem = orderList.get(position);

			int activeOrderPosition = recyclerAdapter.getOrderPositionIfContainsOrderID(orderItem.getIdOrder());
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryFragment.activeOrdersOnItemClick : status : " + order.getStatus());

			if (order.getStatus().equals(OrderState.getStatusStringByState(OrderState.CP))
					|| order.getStatus().equals(OrderState.getStatusStringByState(OrderState.CC))
					|| order.getStatus().equals(OrderState.getStatusStringByState(OrderState.NC))) {
				currentOrderId = order.getIdOrder();
				currentOrderPosition = position;

				// start Order Info Activity
				Intent intent = new Intent(mContext, OrderInfoActivity.class);
				intent.putExtra(CMAppGlobals.EXTRA_ORDER_ID, orderItem.getIdOrder());
				intent.putExtra(CMAppGlobals.EXTRA_ORDER_RATE, orderItem.getRating());
				intent.putExtra(CMAppGlobals.EXTRA_ORDER, orderItem);
				mContext.startActivityForResult(intent, CMAppGlobals.REQUEST_ORDER_INFO);
			} else {
//				Intent intent  = new Intent();
//				intent.putExtra(CMAppGlobals.EXTRA_ACTIVE_ORDER_ID, order.getIdOrder());
//				mContext.setResult(Activity.RESULT_OK, intent);
//				mContext.finish();
				orderManager.GetOrderStatusList(order.getIdOrder());
			}

		}
	};

	/**
	 * Method for sending order status data
	 * @param orderStatusData
     */
	public void sendOrderStatusData(OrderStatusData orderStatusData) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryFragment.sendOrderStatusData : orderStatusData : " + orderStatusData);

		Intent intent  = new Intent();
		intent.putExtra(CMAppGlobals.EXTRA_ACTIVE_ORDER_STATUS_DATA, orderStatusData);
		mContext.setResult(Activity.RESULT_OK, intent);
		mContext.finish();

	} // end method sendOrderStatusData

	private OrderManager orderManager;
	private RelativeLayout layoutMainContent;
	private RelativeLayout layoutRegisterContent;

	@Override @TargetApi(11)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView != null)
            return rootView;
		rootView = inflater.inflate(R.layout.fragment_order_history, container, false);
		mContext = getActivity();
	
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderHistoryFragment.onCreateView ");

		// Checking is user registered
		layoutMainContent = (RelativeLayout) rootView.findViewById(R.id.layoutMainContent);
		layoutRegisterContent = (RelativeLayout) rootView.findViewById(R.id.layoutRegisterContent);
		String auth_token = (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), ""));
		if (!auth_token.equals("")) {
			layoutMainContent.setVisibility(View.VISIBLE);
			layoutRegisterContent.setVisibility(View.GONE);
		} else {
			layoutMainContent.setVisibility(View.GONE);
			layoutRegisterContent.setVisibility(View.VISIBLE);
			if (CMApplication.hasNavigationBar(mContext)) {
				layoutRegisterContent.setPadding(0, 0, 0, CMApplication.dpToPx(48));
			}
			RelativeLayout layoutGoToRegister = (RelativeLayout) rootView.findViewById(R.id.layoutGoToRegister);
			layoutGoToRegister.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivityForResult(new Intent(mContext, RegisterActivity.class), CMAppGlobals.REQUEST_REGISTER);
				}
			});
		}

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		// set events
		setEventListeners();
		layoutHeader = (RelativeLayout) rootView.findViewById(R.id.layoutHeader);
		recyclerViewOrders = (RecyclerView) rootView.findViewById(R.id.recycler_view_orders);
		recyclerViewOrders.setLayoutManager(new LinearLayoutManager(mContext));

		if (!auth_token.isEmpty()) {
			orderManager = new OrderManager((ICallBack) mContext);
			orderManager.GetOrders(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PHONE_NUMBER.key(), ""), 0);
		}



		if(CMApplication.hasNavigationBar(mContext)) {
			layoutMainContent.setPadding(0, 0, 0, CMApplication.dpToPx(48));
		}

		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryFragment.onActivityResult : resultCode : " + resultCode + " : resultCode : " + resultCode + " : data : " + data);

		if (requestCode == CMAppGlobals.REQUEST_REGISTER && resultCode == Activity.RESULT_OK) {
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryFragment.onActivityResult : REQUEST_REGISTER");
			mContext.setResult(Activity.RESULT_OK);
			mContext.onBackPressed();
		}
	}

	/**
	 * Method for setting all event listeners
	 */
	private void setEventListeners() {
		layoutBack = (LinearLayout) rootView.findViewById(R.id.layoutBack);
		layoutBack.setOnTouchListener(new OnTouchListener() {
			ImageView imageViewBack = (ImageView) layoutBack.findViewById(R.id.imageViewBack);
			@SuppressLint("ClickableViewAccessibility") 
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return CommonHelper.setOnTouchImage(imageViewBack, event);
			}
		});
		layoutBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mContext != null) {
					// check network status
					if (Utils.checkNetworkStatus(mContext)) {
						mContext.onBackPressed();
					}
				}
			}
		});
		
	} // end method setEventListeners

	/**
	 * Method for showing orders
	 * @param orders
	 */
	public void showOrders(List<Order> orders) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderHistoryFragment.showOrders : orders : " + orders);
		progressBar.setVisibility(View.GONE);
		orderList = orders;

		if (recyclerAdapter == null) {
			Collections.sort(orderList, new OrderHistoryComparator());
			recyclerAdapter = new OrderHistoryAdapter(mContext, orderList);
			recyclerAdapter.setOnItemClickListener(mItemClickListener);
			recyclerAdapter.setMode(Attributes.Mode.Single);
			recyclerViewOrders.setAdapter(recyclerAdapter);
		} else {
			recyclerAdapter.notifyDataSetChanged();
		}

	} // end method showOrders]

	/**
	 * Method for removing order info from list
	 */
	public void removeOrderInfo() {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryFragment.removeOrderInfo ");

		recyclerAdapter.removeOrderFromList();

	} // end method removeOrderInfo

	/**
	 * Method for updating order list item
	 * @param orderItem
	 */
	public void updateOrderListItem(Order orderItem) {
		if (orderList != null && !orderList.isEmpty()) {
			orderList.set(currentOrderPosition, orderItem);
			recyclerAdapter.notifyItemChanged(currentOrderPosition);
		}
	}

	/**
	 * Method for getting Current Order Id
	 * @return
	 */
	public String getCurrentOrderId() {
		return currentOrderId;

	} // end method getCurrentOrderId
}
