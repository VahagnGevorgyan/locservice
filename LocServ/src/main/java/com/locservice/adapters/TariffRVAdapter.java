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
import com.locservice.api.entities.Tariff;
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
public class TariffRVAdapter extends RecyclerView.Adapter<TariffRVAdapter.ViewHolder> implements IOptionAdapter {

    private static final String TAG = DateRVAdapter.class.getSimpleName();
    private final Typeface robotoLight;
    private final Typeface robotoRegular;
    private Context mContext;
    private List<Tariff> mTariffList;
    private TariffOnItemClickListener mOnItemClickListener;
    private CustomTextView currentTextView;
    private int currentPosition = 0;

    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE/2;
    public final int MIDDLE;

    private boolean isClickEnable = true;

    private boolean isFirst = true;
    private boolean isInfinite = true;

    public TariffRVAdapter(Context mContext, List<Tariff> mTariffList, int currentPosition, boolean isInfinite) {
        this.mContext = mContext;
        this.mTariffList = mTariffList;
        this.currentPosition = currentPosition;
        if(mTariffList.size() > 0)
            MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % mTariffList.size();
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
        if (mTariffList.size() > 0) {

            int realPosition;
            if (isInfinite)  {
                realPosition = position % mTariffList.size();
            } else {
                realPosition = position;
            }

            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateRVAdapter.onBindViewHolder : position : " + position
                    + " : realPosition : " + realPosition
                    + " : currentPosition : " + currentPosition);

            Tariff item = mTariffList.get(realPosition);
            float fontScale = mContext.getResources().getConfiguration().fontScale;
            holder.textViewItem.setText(item.getName());
            holder.textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
            holder.textViewItem.setTypeface(robotoLight);
            holder.textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    mContext.getResources().getInteger(R.integer.int_slide_panel_items_unselected_text_size) / fontScale);
            holder.textViewItem.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.carousel_item_top_padding), 0, 0);

            if (currentPosition == 0) {
                if (item.getIsDefault().equals("1") && isFirst) {
                    isFirst = false;
                    holder.textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                    holder.textViewItem.setTypeface(robotoRegular);
                    holder.textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                            mContext.getResources().getInteger(R.integer.int_slide_panel_items_selected_text_size) / fontScale);
                    holder.textViewItem.setPadding(0, 0, 0, 0);
                    currentTextView = holder.textViewItem;
                    currentPosition = realPosition;
                }
            } else {
                isFirst = false;
            }

            if (currentPosition == realPosition && !isFirst) {
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
        else if (mTariffList != null)
            return mTariffList.size();

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
            Tariff tariffItem;
            if (isInfinite) {
                if(mTariffList != null && mTariffList.size() > 0) {
                    if (getAdapterPosition() % mTariffList.size() >= 0
                            && getAdapterPosition() % mTariffList.size() < mTariffList.size()
                            && getAdapterPosition() >= 0) {
                        tariffItem = mTariffList.get(getAdapterPosition() % mTariffList.size());
                        mOnItemClickListener.tariffOnItemClick(itemView, getAdapterPosition(), tariffItem, currentTextView);
                    }
                }
            } else {
                if(mTariffList != null && mTariffList.size() > 0) {
                    if (getAdapterPosition() >= 0
                            && getAdapterPosition() <mTariffList.size()) {
                        tariffItem = mTariffList.get(getAdapterPosition());
                        mOnItemClickListener.tariffOnItemClick(itemView, getAdapterPosition(), tariffItem, currentTextView);
                    }
                }
            }
        }
    }

    public interface TariffOnItemClickListener {
        void tariffOnItemClick(View itemView, int position, Tariff item, TextView currentTextView);
    }

    /**
     * Method for setting OnItemClickListener
     * @param onItemClickListener - Tariff on item click listener
     */
    public void setOnItemClickListener (TariffOnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;

    } // end method setOnItemClickListener

    /**
     * Method for getting currentPosition
     * @return - Current position
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
     * @return - CurrentTextView
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
     * @return - IsInfinite
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

    /**
     * Method for getting middle position
     * @return - Adapter middle position
     */
    public long getMiddlePosition() {
        long middle = 0;
        if(mTariffList.size() > 0)
            middle = HALF_MAX_VALUE - HALF_MAX_VALUE % mTariffList.size();

        return middle;

    } // end method getMiddlePosition

    /**
     * Method for setting is first
     * @param first - first time
     */
    public void setFirst(boolean first) {
        this.isFirst = first;
    } // end method setFirst

}