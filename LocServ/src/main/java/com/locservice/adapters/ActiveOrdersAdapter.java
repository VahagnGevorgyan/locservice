package com.locservice.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.helpers.OrderState;
import com.locservice.utils.Logger;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 15 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ActiveOrdersAdapter extends RecyclerSwipeAdapter<ActiveOrdersAdapter.ActiveOrdersViewHolder> {

    private static final String TAG = ActiveOrdersAdapter.class.getSimpleName();
    private Activity mContext;
    private List<OrderStatusData> orderList;
    private OnItemClickListener mItemClickListener;

    public ActiveOrdersAdapter(Activity context, List<OrderStatusData> orderList) {
		this.mContext = context;
		this.orderList = orderList;
	}

    @Override
    public ActiveOrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_active_order, parent, false);
		return new ActiveOrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ActiveOrdersViewHolder viewHolder, int position) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ActiveOrdersAdapter.onBindViewHolder : position : " + position);

        final OrderStatusData orderStatusData = orderList.get(position);

        if (position == 0) {
            viewHolder.layoutTopActiveOrderItem.setVisibility(View.VISIBLE);
        } else  {
            viewHolder.layoutTopActiveOrderItem.setVisibility(View.GONE);
        }
        viewHolder.textViewTitle.setText(orderStatusData.getTitle());
        viewHolder.textViewSubTitle.setText(Html.fromHtml(orderStatusData.getSubTitle()));
        viewHolder.textViewTitle.setTextColor(Color.WHITE);
        viewHolder.textViewSubTitle.setTextColor(Color.WHITE);
        switch (OrderState.getStateByStatus(orderStatusData.getStatus())) {
            case R:
                viewHolder.layoutActiveOrders.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_tariff_sec));
                break;
            case LATER_R: // ORDER REGISTERED LATER
                viewHolder.layoutActiveOrders.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_tariff_sec));
                break;
            case ASAP_R: // DRIVER SEARCH
                viewHolder.layoutActiveOrders.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_tariff_sec));
                break;
            case ASAP_A_BUSY:
                viewHolder.layoutActiveOrders.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_found_list));
                break;
            case LATER_A:
            case ASAP_A_THIS: // DRIVER ON WAY
                viewHolder.layoutActiveOrders.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_onway_list));
                break;
            case A:
                viewHolder.layoutActiveOrders.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_found_list_click));
                break;
            case RC:
                viewHolder.layoutActiveOrders.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_list_item_yellow_click));
                viewHolder.textViewTitle.setTextColor(Color.BLACK);
                viewHolder.textViewSubTitle.setTextColor(Color.BLACK);
                break;
            case OW:
                viewHolder.layoutActiveOrders.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_trip_list_click));
                break;
            case CP:
                viewHolder.layoutActiveOrders.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_tariff_sec));
                break;
            case AR:
                viewHolder.layoutActiveOrders.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_tariff_sec));
                break;
        }


    }

    @Override
    public int getItemCount() {
        if (orderList != null) {
            return orderList.size();
        }
        return 0;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return 0;
    }


    public class ActiveOrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CustomTextView textViewTitle;
        CustomTextView textViewSubTitle;
        LinearLayout layoutActiveOrders;
        LinearLayout layoutTopActiveOrderItem;

        public ActiveOrdersViewHolder(View itemView) {
            super(itemView);
            layoutActiveOrders = (LinearLayout) itemView;
            textViewTitle = (CustomTextView) itemView.findViewById(R.id.textViewTitle);
            textViewSubTitle = (CustomTextView) itemView.findViewById(R.id.textViewSubTitle);
            layoutTopActiveOrderItem = (LinearLayout) itemView.findViewById(R.id.layoutTopActiveOrderItem);
            layoutActiveOrders.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (orderList != null && !orderList.isEmpty() && getAdapterPosition() >= 0) {
                mItemClickListener.activeOrdersOnItemClick(mContext, itemView, getAdapterPosition(), orderList.get(getAdapterPosition()));
            }
        }
    }

    /**
     * interface OnItemClickListener
     */
    public interface OnItemClickListener {
		void activeOrdersOnItemClick(Activity context, View view, int position, OrderStatusData orderStatusData);

	} // end interface OnItemClickListener

    /**
     * Mthod for  setting Item Click Listener
     *
     * @param mItemClickListener
     */
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
		this.mItemClickListener = mItemClickListener;

	} // end method setOnItemClickListener


}
