package com.locservice.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.entities.PaymentInfo;
import com.locservice.protocol.IOptionAdapter;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.utils.Logger;
import com.locservice.utils.Typefaces;

import java.util.List;


/**
 * Created by Vahagn Gevorgyan
 * 29 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class PaymentRVAdapter extends RecyclerView.Adapter<PaymentRVAdapter.ViewHolder> implements IOptionAdapter {

    private static final String TAG = DateRVAdapter.class.getSimpleName();
    private final Typeface robotoRegular;
    private final Typeface robotoLight;
    private Context mContext;
    private List<PaymentInfo> mPaymentList;
    private PaymentOnItemClickListener mOnItemClickListener;
    private CustomTextView currentTextView;
    private int currentPosition = 0;

    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE/2;
    public final int MIDDLE;

    private boolean isClickEnable = true;
    private boolean isInfinite = true;

    public PaymentRVAdapter(Context mContext, List<PaymentInfo> mStringList, int currentPosition,  boolean isInfinite) {
        this.mContext = mContext;
        this.mPaymentList = mStringList;
        this.currentPosition = currentPosition;
        if(mPaymentList.size() > 0)
            MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % mPaymentList.size();
        else
            MIDDLE = 0;
        robotoLight = Typefaces.get(mContext, "fonts/RobotoLight.ttf");
        robotoRegular = Typefaces.get(mContext, "fonts/RobotoRegular.ttf");
        this.isInfinite = isInfinite;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_so_options, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(mPaymentList.size() > 0) {
            int realPosition;
            if (isInfinite)  {
                realPosition = position % mPaymentList.size();
            } else {
                realPosition = position;
            }
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: mPaymentList.onBindViewHolder : position : " + position + " : realPosition : " + realPosition);


            String item = mPaymentList.get(realPosition).getPayment_name();
            float fontScale = mContext.getResources().getConfiguration().fontScale;
            holder.textViewItem.setText(item);
            holder.textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
            holder.textViewItem.setTypeface(robotoLight);
            holder.textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    mContext.getResources().getInteger(R.integer.int_slide_panel_items_unselected_text_size) / fontScale);
            holder.textViewItem.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.carousel_item_top_padding), 0, 0);

            if (currentPosition == realPosition) {
                holder.textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                holder.textViewItem.setTypeface(robotoRegular);
                holder.textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                        mContext.getResources().getInteger(R.integer.int_slide_panel_items_selected_text_size) / fontScale);
                holder.textViewItem.setPadding(0, 0, 0, 0);
                currentTextView = holder.textViewItem;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (isInfinite)
            return Integer.MAX_VALUE;
        else if (mPaymentList != null)
            return mPaymentList.size();

        return 0;
    }

    @Override
    public void setItemClickState(boolean isEnable) {
        isClickEnable = isEnable;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextView textViewItem;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewItem = (CustomTextView) itemView.findViewById(R.id.textViewItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PaymentInfo paymentInfo;
            if (isInfinite) {
                if (getAdapterPosition() % mPaymentList.size() >= 0
                        && getAdapterPosition() % mPaymentList.size() < mPaymentList.size()
                        && getAdapterPosition() >=0) {
                    paymentInfo = mPaymentList.get(getAdapterPosition() % mPaymentList.size());
                    mOnItemClickListener.paymentOnItemClick(itemView, getAdapterPosition(), paymentInfo, currentTextView);
                }
            } else {
                if (getAdapterPosition() >= 0
                        && getAdapterPosition() < mPaymentList.size()) {
                    paymentInfo = mPaymentList.get(getAdapterPosition());
                    mOnItemClickListener.paymentOnItemClick(itemView, getAdapterPosition(), paymentInfo, currentTextView);
                }
            }
        }
    }

    public interface PaymentOnItemClickListener {
        void paymentOnItemClick(View itemView, int infinitePosition, PaymentInfo item, TextView currentTextView);
    }

    /**
     * Method for setting OnItemClickListener
     * @param onItemClickListener - Payment on item lick listener
     */
    public void setOnItemClickListener (PaymentOnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;

    } // end method setOnItemClickListener

    /**
     * Method for getting currentPosition
     * @return currentPosition
     */
    public int getCurrentPosition() {
        return currentPosition;

    } // end method currentPosition

    /**
     * Method for setting currentPosition
     * @param currentPosition - Adapter current position
     */
    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;

    } // end method setCurrentPosition

    /**
     * Method for getting currentTextView
     * @return currentTextView
     */
    public CustomTextView getCurrentTextView() {
        return currentTextView;

    } // end method getCurrentTextView

    /**
     * Method for setting currentTextView
     * @param currentTextView - Current text view
     */
    public void setCurrentTextView(CustomTextView currentTextView) {
        this.currentTextView = currentTextView;

    } // end method setCurrentTextView

    /**
     * Method for getting isClickEnable
     * @return - Is click enable
     */
    public boolean isClickEnable() {
        return isClickEnable;

    } // end method isClickEnable

    /**
     * Method for getting isInfinite
     * @return isInfinite
     */
    public boolean isInfinite() {
        return isInfinite;

    } // en method isInfinite

    /**
     * Method for setting isInfinite
     * @param isInfinite - Is infinite
     */
    public void setIsInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;

    } // end method setIsInfinite

}