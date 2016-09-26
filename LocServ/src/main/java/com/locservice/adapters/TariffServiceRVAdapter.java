package com.locservice.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.entities.TariffService;
import com.locservice.protocol.IOptionAdapter;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.utils.Logger;
import com.locservice.utils.Typefaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 5 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class TariffServiceRVAdapter extends RecyclerView.Adapter<TariffServiceRVAdapter.ViewHolder> implements IOptionAdapter {

    private static final String TAG = DateRVAdapter.class.getSimpleName();
    private final Typeface robotoLight;
    private final Typeface robotoRegular;
    private Context mContext;
    private List<TariffService> mTariffServiceList;
    private TariffServiceOnItemClickListener mOnItemClickListener;

    private List<TariffService> selectedTariffServices = new ArrayList<>();

    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE/2;
    public final int MIDDLE;

    private boolean isClickEnable = true;
    private boolean isChildSeatAdded = false;
    private boolean isInfinite = true;

    public TariffServiceRVAdapter(Context mContext, List<TariffService> mTariffServiceList, boolean isInfinite) {
        this.mContext = mContext;
        this.mTariffServiceList = mTariffServiceList;
        if (mTariffServiceList.size() > 0)
            MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % mTariffServiceList.size();
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
        if(mTariffServiceList.size() > 0) {
            int realPosition;
            if (isInfinite)  {
                realPosition = position % mTariffServiceList.size();
            } else {
                realPosition = position;
            }

            TariffService item = mTariffServiceList.get(realPosition);

            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateRVAdapter.onBindViewHolder : position : " + position + " : realPosition : " + realPosition);
            float fontScale = mContext.getResources().getConfiguration().fontScale;
            holder.textViewItem.setText(item.getTitle());
            holder.textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
            holder.textViewItem.setTypeface(robotoLight);
            holder.textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    mContext.getResources().getInteger(R.integer.int_slide_panel_items_unselected_text_size) / fontScale);
            holder.textViewItem.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.carousel_item_top_padding), 0, 0);

            if (selectedTariffServices.contains(item)) {
                holder.textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                holder.textViewItem.setTypeface(robotoRegular);
                holder.textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                        mContext.getResources().getInteger(R.integer.int_slide_panel_items_selected_text_size) / fontScale);
                holder.textViewItem.setPadding(0, 0, 0, 0);

            }
        }
    }

    @Override
    public int getItemCount() {
        if (isInfinite)
            return Integer.MAX_VALUE;
        else if (mTariffServiceList != null)
            return mTariffServiceList.size();

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
            if(mTariffServiceList.size() > 0) {
                TariffService tariffServiceItem;
                if (isInfinite) {
                    if (getAdapterPosition() % mTariffServiceList.size() >= 0
                            && getAdapterPosition() % mTariffServiceList.size() < mTariffServiceList.size()
                            && getAdapterPosition() >= 0) {
                        tariffServiceItem = mTariffServiceList.get(getAdapterPosition() % mTariffServiceList.size());
                        mOnItemClickListener.tariffServiceOnItemClick(itemView, getAdapterPosition(), tariffServiceItem);
                    }
                } else {
                    if (getAdapterPosition() >= 0
                            && getAdapterPosition() < mTariffServiceList.size()) {
                        tariffServiceItem = mTariffServiceList.get(getAdapterPosition());
                        mOnItemClickListener.tariffServiceOnItemClick(itemView, getAdapterPosition(), tariffServiceItem);
                    }
                }
            }
        }
    }

    public interface TariffServiceOnItemClickListener {
        void tariffServiceOnItemClick(View itemView, int position, TariffService item);
    }

    /**
     * Method for setting OnItemClickListener
     * @param onItemClickListener - On item click listener
     */
    public void setOnItemClickListener (TariffServiceOnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;

    } // end method setOnItemClickListener


    /**
     * Method for getting isClickEnable
     * @return - Is click enable
     */
    public boolean isClickEnable() {
        return isClickEnable;

    } // end method isClickEnable

    /**
     * Method for getting selected tariff services
     * @return - Selected tariff service list
     */
    public List<TariffService> getSelectedTariffServices() {
        return selectedTariffServices;

    } // end method getSelectedTariffServices

    /**
     * Method fro setting selected tariff services
     * @param selectedTariffServices - Selected tariff service list
     */
    public void setSelectedTariffServices(List<TariffService> selectedTariffServices) {
        this.selectedTariffServices = selectedTariffServices;

    } // end method setSelectedTariffServices

    /**
     * Method for getting isChildSeatAdded
     * @return IsChildSeatAdded
     */
    public boolean isChildSeatAdded() {
        return isChildSeatAdded;

    } // end method isChildSeatAdded

    /**
     * Method for setting isChildSeatAdded
     * @param isChildSeatAdded - Is child seat Added
     */
    public void setIsChildSeatAdded(boolean isChildSeatAdded) {
        this.isChildSeatAdded = isChildSeatAdded;

    } // end method setIsChildSeatAdded

    /**
     * Method for getting isInfinite
     * @return Is Infinite
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
        if(mTariffServiceList.size() > 0)
            middle = HALF_MAX_VALUE - HALF_MAX_VALUE % mTariffServiceList.size();

        return middle;

    } // end method getMiddlePosition
}