package com.locservice.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.entities.Order;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.manager.OrderManager;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.controls.DynamicHeightImageView;
import com.locservice.ui.helpers.DateHelper;
import com.locservice.ui.helpers.MenuHelper;
import com.locservice.utils.Logger;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.Date;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 08 December 2015
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderHistoryAdapter extends RecyclerSwipeAdapter<OrderHistoryAdapter.ViewHolder> {
    private static final String TAG = OrderHistoryAdapter.class.getSimpleName();
	private final List<OrderStatusData> activeOrderStatusList;
	private Context context;
    private List<Order> orders;
    private OnItemClickListener mItemClickListener;
	private int removedPosition = -1;
	

	private DateHelper mDateHelper;

    private SwipeLayout currentSwipeLayout;
	
	public OrderHistoryAdapter(Context context, List<Order> orders) {
		this.context = context;
		this.orders = orders;

		mDateHelper = new DateHelper(this.context);

		activeOrderStatusList = MenuHelper.getActiveOrdersList();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		SwipeLayout swipeLayout;
		DynamicHeightImageView imageViewThumb;
		TextView textViewName;
		TextView textViewDescription;
		TextView textViewPrice;
		LinearLayout layoutItem;
		View dividerLine;
		RatingBar ratingBarOrder;
		LinearLayout layoutPriceSize;
		CustomTextView textViewDelete;
		CustomTextView textViewSave;

		public ViewHolder(View itemView) {
			super(itemView);
			swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
			imageViewThumb = (DynamicHeightImageView) itemView.findViewById(R.id.imageViewThumb);
			textViewName = (TextView) itemView.findViewById(R.id.textViewName);
			textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
			textViewPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
			layoutItem = (LinearLayout) itemView.findViewById(R.id.layoutItem);
			dividerLine = (View) itemView.findViewById(R.id.dividerLine);
			ratingBarOrder = (RatingBar) itemView.findViewById(R.id.ratingBarOrder);
			layoutPriceSize = (LinearLayout) itemView.findViewById(R.id.layoutPriceSize);
			textViewDelete = (CustomTextView) itemView.findViewById(R.id.textViewDelete);
			textViewSave = (CustomTextView) itemView.findViewById(R.id.textViewSave);
			layoutItem.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (mItemClickListener != null) {
				if(orders != null && orders.size() > 0) {
					mItemClickListener.activeOrdersOnItemClick(itemView, getAdapterPosition(), orders.get(getAdapterPosition()));
				}
			}
		}
	}

	public interface OnItemClickListener {
		void activeOrdersOnItemClick(View view, int position, Order order);
	}

	public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
		this.mItemClickListener = mItemClickListener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		final Order order = orders.get(position);
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryAdapter : CollAddressText : " + order.getCollAddressText());
		holder.textViewName.setText(order.getCollAddressText());
		Date itemDate = mDateHelper.getDateByStringDateFormat(order.getCollDate() + " " + order.getCollTime(), "dd-MM-yyyy HHmm");
//		String description = mDateHelper.getStringDateByUI(itemDate) + " " + order.getTariffName();
		String description = mDateHelper.getDateString(itemDate) + " " + order.getTariffName();
		holder.textViewDescription.setText(description);



		if(order.getStatus().equals("CP") || order.getStatus().equals("CC")|| order.getStatus().equals("NC")) {
			holder.layoutPriceSize.setVisibility(View.VISIBLE);
			holder.textViewPrice.setText(orders.get(position).getPrice() + "");
			holder.layoutItem.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_white_selector));
			holder.dividerLine.setVisibility(View.VISIBLE);
			holder.ratingBarOrder.setVisibility(View.VISIBLE);
			holder.ratingBarOrder.setRating(order.getRating());
			holder.textViewName.setTextColor(Color.BLACK);
			holder.textViewDescription.setTextColor(Color.parseColor("#999999"));
		} else {
			holder.ratingBarOrder.setVisibility(View.GONE);
			holder.dividerLine.setVisibility(View.GONE);
			holder.layoutPriceSize.setVisibility(View.GONE);


			int activeOrderPosition = getOrderPositionIfContainsOrderID(order.getIdOrder());

			switch (order.getStatus()) {
				case "R":
				case "ASAP_R":
				case "LATER_R":
				case "AR":
					holder.layoutItem.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_tariff_sec));
					holder.textViewName.setTextColor(Color.WHITE);
					holder.textViewDescription.setTextColor(Color.WHITE);
					if (activeOrderPosition != -1) {
						holder.textViewDescription.setText(Html.fromHtml(activeOrderStatusList.get(activeOrderPosition).getSubTitle()));
					}
					break;
				case "RC":
					holder.layoutItem.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_list_item_yellow_click));
					holder.textViewName.setTextColor(Color.BLACK);
					holder.textViewDescription.setTextColor(Color.BLACK);
					if (activeOrderPosition != -1) {
						holder.textViewDescription.setText(Html.fromHtml(activeOrderStatusList.get(activeOrderPosition).getSubTitle()));
					}
					break;
				case "A":
				case "ASAP_A_THIS":
				case "ASAP_A_BUSY":
				case "LATER_A":
					holder.layoutItem.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_driver_found_list_click));
					holder.textViewName.setTextColor(Color.WHITE);
					holder.textViewDescription.setTextColor(Color.WHITE);
					if (activeOrderPosition != -1) {
						holder.textViewDescription.setText(Html.fromHtml(activeOrderStatusList.get(activeOrderPosition).getSubTitle()));
					}
					break;
//				case DRIVER_ONWAY:
//					holder.layoutItem.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_driver_onway_list_click));
//					holder.textViewName.setTextColor(Color.WHITE);
//					holder.textViewDescription.setTextColor(Color.WHITE);
//					break;
				case "OW":
					holder.layoutItem.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_driver_trip_list_click));
					holder.textViewName.setTextColor(Color.WHITE);
					holder.textViewDescription.setTextColor(Color.WHITE);
					if (activeOrderPosition != -1) {
						holder.textViewDescription.setText(Html.fromHtml(activeOrderStatusList.get(activeOrderPosition).getSubTitle()));
					}
					break;
				default:
					holder.textViewName.setTextColor(Color.BLACK);
					holder.textViewDescription.setTextColor(Color.parseColor("#999999"));
					break;
			}
		}

		if (order.getStatus().equals("CC") || order.getStatus().equals("NC")) {
			holder.layoutItem.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_gray_selector));
		}

			holder.textViewDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ADAPTER : position : " + position);
				// REMOVE ORDER
				OrderManager orderManager = new OrderManager((ICallBack) context);
				orderManager.RemoveOrder(order.getIdOrder());
				// set position
				removedPosition = position;
			}
		});
		holder.textViewSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show();
			}
		});

		holder.layoutItem.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
//				builder.setItems(context.getResources().getStringArray(R.array.list_action_items), new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						switch (which) {
//							case 0:
//								if (CMAppGlobals.DEBUG)
//									Logger.i(TAG, ":: ADAPTER : position : " + position);
//								// REMOVE ORDER
//								OrderManager orderManager = new OrderManager((ICallBack) context);
//								orderManager.RemoveOrder(order.getIdOrder());
//								// set position
//								removedPosition = position;
//								break;
//							default:
//								break;
//						}
//					}
//				});
				builder.setMessage(R.string.alert_order_delete);
				builder.setNegativeButton(R.string.str_no, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (CMAppGlobals.DEBUG)Logger.i(TAG, ":: ADAPTER : position : " + position);
						// REMOVE ORDER
						OrderManager orderManager = new OrderManager((ICallBack) context);
						orderManager.RemoveOrder(order.getIdOrder());
						// set position
						removedPosition = position;
						dialog.dismiss();
					}
				});
				builder.create().show();
				return false;
			}
		});
		// TODO swipe functional
//		holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
//		holder.swipeLayout.addSwipeListener(new SimpleSwipeListener(){
//			@Override
//			public void onStartOpen(SwipeLayout layout) {
//				super.onStartOpen(layout);
//				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryAdapter.addSwipeListener.onStartOpen layout : " + layout);
//				if (currentSwipeLayout != null)
//					currentSwipeLayout.close(true);
//			}
//
//			@Override
//			public void onOpen(SwipeLayout layout) {
//				super.onOpen(layout);
//				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryAdapter.addSwipeListener.onOpen layout : " + layout);
//				currentSwipeLayout = holder.swipeLayout;
//			}
//		});
	}
	@Override
	public int getItemCount() {
		if (orders != null) {
			return orders.size();
		}
		return 0;
	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return 0;
	}

	/**
	 * Method for getting Order Position If Contains Order ID
	 * @param orderId
	 * @return
	 */
	public int getOrderPositionIfContainsOrderID(String orderId) {
		if (activeOrderStatusList != null) {
			for (int i = 0; i < activeOrderStatusList.size(); i++) {
				if (orderId.equals(activeOrderStatusList.get(i).getIdOrder())) {
					return i;
				}
			}
		}
		return -1;

	} // getOrderPositionIfContainsOrderID

	/**
	 * Method for getting removedPosition
	 * @return removedPosition
	 */
	public int getRemovedPosition() {
		return removedPosition;

	} // end method getRemovedPosition


	/**
	 * Method for removing order from list
	 */
	public void removeOrderFromList() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderHistoryAdapter.removeOrderFromList ");

		int position = getRemovedPosition();
		if (position >= 0 && position < orders.size()) {
			orders.remove(position);
			notifyItemRemoved(position);
			notifyItemRangeChanged(position, orders.size());
		}

	} // end method removeOrderFromList


 }
