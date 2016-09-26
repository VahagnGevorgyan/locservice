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
import com.locservice.api.entities.ChildSeat;
import com.locservice.protocol.IOptionAdapter;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.utils.Logger;
import com.locservice.utils.Typefaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 15 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ChildSeatRVAdapter extends RecyclerView.Adapter<ChildSeatRVAdapter.ViewHolder> implements IOptionAdapter {

    private static final String TAG = DateRVAdapter.class.getSimpleName();
    private final Typeface robotoLight;
    private final Typeface robotoRegular;
    private Context mContext;
    private List<ChildSeat> mChildList;
    private ChildSeatOnItemClickListener mOnItemClickListener;
    private List<ChildSeat> selectedChildSeats = new ArrayList<>();


    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE/2;
    public final int MIDDLE;

    private boolean isClickEnable = true;

    public ChildSeatRVAdapter(Context mContext, List<ChildSeat> mChildList) {
        this.mContext = mContext;
        this.mChildList = mChildList;
        MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % mChildList.size();
        robotoLight = Typefaces.get(mContext, "fonts/RobotoLight.ttf");
        robotoRegular = Typefaces.get(mContext, "fonts/RobotoRegular.ttf");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_so_options, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int realPosition = position % mChildList.size();
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateRVAdapter.onBindViewHolder : position : " + position + " : realPosition : " + realPosition);

        ChildSeat item = mChildList.get(realPosition);
        float fontScale = mContext.getResources().getConfiguration().fontScale;
//        holder.textViewItem.setText(item.getAge());
        holder.textViewItem.setText(item.getWeight());
        holder.textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
        holder.textViewItem.setTypeface(robotoLight);
        holder.textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                mContext.getResources().getInteger(R.integer.int_slide_panel_items_unselected_text_size) / fontScale);
        holder.textViewItem.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.carousel_item_top_padding), 0, 0);

        if (selectedChildSeats.contains(item)) {
            holder.textViewItem.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.textViewItem.setTypeface(robotoRegular);
            holder.textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    mContext.getResources().getInteger(R.integer.int_slide_panel_items_selected_text_size) / fontScale);
            holder.textViewItem.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
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
            if (mChildList.size() > 0) {
                if (getAdapterPosition() % mChildList.size() >= 0
                        && getAdapterPosition() % mChildList.size() < mChildList.size()
                        && getAdapterPosition() >= 0) {
                    mOnItemClickListener.childSeatOnItemClick(itemView, getAdapterPosition(), mChildList.get(getAdapterPosition() % mChildList.size()));
                }
            }
        }
    }

    public interface ChildSeatOnItemClickListener {
        public void childSeatOnItemClick(View itemView, int infinitePosition, ChildSeat item);
    }

    /**
     * Method for setting OnItemClickListener
     * @param onItemClickListener
     */
    public void setOnItemClickListener (ChildSeatOnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;

    } // end method setOnItemClickListener

    /**
     * Method for getting isClickEnable
     * @return
     */
    public boolean isClickEnable() {
        return isClickEnable;

    } // end method isClickEnable

    /**
     * Method for getting selectedChildSeats
     * @return
     */
    public List<ChildSeat> getSelectedChildSeats() {
        return selectedChildSeats;

    } // end method selectedChildSeats

    /**
     * Method for setting selectedChildSeats
     * @param selectedChildSeats
     */
    public void setSelectedChildSeats(List<ChildSeat> selectedChildSeats) {
        this.selectedChildSeats = selectedChildSeats;

    } // end method setSelectedChildSeats

    /**
     * Method for getting child sets list
     * @param context
     * @return
     */
    public static List<ChildSeat> getChildSeatList(Context context) {
        List<ChildSeat> childSeatList = new ArrayList<>();
//        String[] ages = context.getResources().getStringArray(R.array.so_child_seat_items);
        String[] weights = context.getResources().getStringArray(R.array.so_child_seat_items);
        for (int i = 0; i < weights.length; i++) {
//            ChildSeat childSeat = new ChildSeat(ages[i], "", i);
            ChildSeat childSeat = new ChildSeat("", weights[i], i + 1);
            childSeatList.add(childSeat);
        }

        return childSeatList;

    } // end method getChildSeatList
}
