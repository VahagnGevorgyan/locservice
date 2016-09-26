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
import com.locservice.api.entities.DateData;
import com.locservice.protocol.IOptionAdapter;
import com.locservice.ui.MainActivity;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.controls.TimeSelectionDialog;
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
public class DateRVAdapter extends RecyclerView.Adapter<DateRVAdapter.ViewHolder> implements IOptionAdapter {

    private static final String TAG = DateRVAdapter.class.getSimpleName();
    private final Typeface robotoLight;
    private final Typeface robotoRegular;
    private Context mContext;
    private List<DateData> mDateList;
    private DateOnItemClickListener mOnItemClickListener;
    private CustomTextView currentTextView;
    private int currentPosition = 0;

    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE/2;
    public final int MIDDLE;

    private boolean isClickEnable = true;
    private boolean isAddedDate = false;
    private boolean isInfinite = false;

    public DateRVAdapter(Context mContext, List<DateData> mStringList, int currentPosition, boolean isInfinite) {
        this.mContext = mContext;
        this.mDateList = mStringList;
        this.currentPosition = currentPosition;
        MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % mDateList.size();
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

        int realPosition;
        if (isInfinite)  {
            realPosition = position % mDateList.size();
        } else {
            realPosition = position;
        }

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DateRVAdapter.onBindViewHolder : position : " + position + " : realPosition : " + realPosition);

        DateData item = mDateList.get(realPosition);
        float fontScale = mContext.getResources().getConfiguration().fontScale;
        holder.textViewItem.setText(item.getTitle());
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
            currentTextView = holder.textViewItem;
            holder.textViewItem.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        if (isInfinite)
            return Integer.MAX_VALUE;
        else if (mDateList != null)
            return mDateList.size();

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
            DateData dateItem;
            if (isInfinite) {
                if(mDateList != null && mDateList.size() > 0) {
                    if (getAdapterPosition() % mDateList.size() >= 0
                            && getAdapterPosition() % mDateList.size() < mDateList.size()
                            && getAdapterPosition() >= 0) {
                        dateItem = mDateList.get(getAdapterPosition() % mDateList.size());
                        mOnItemClickListener.dateOnItemClick(itemView, getAdapterPosition(), dateItem, currentTextView);
                    }
                }
            } else {
                if(mDateList != null && mDateList.size() > 0) {
                    if (getAdapterPosition() >= 0
                            && getAdapterPosition() < mDateList.size()) {
                        dateItem = mDateList.get(getAdapterPosition());
                        mOnItemClickListener.dateOnItemClick(itemView, getAdapterPosition(), dateItem, currentTextView);
                    }
                }
            }

        }
    }

    public interface DateOnItemClickListener {
        void dateOnItemClick(View itemView, int infinitePosition, DateData item, TextView currentTextView);
    }

    /**
     * Method for setting OnItemClickListener
     * @param onItemClickListener - Date on item click listener
     */
    public void setOnItemClickListener (DateOnItemClickListener onItemClickListener) {
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
     * Method for opening time selection dialog
     */
    public void openTimeSelectionDialog() {
        TimeSelectionDialog dialog = new TimeSelectionDialog();
        dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "OrderTimeDealogFragment");

    } // end method openTimeSelectionDialog

    /**
     * Method for getting isClickEnable
     * @return - is click enable
     */
    public boolean isClickEnable() {
        return isClickEnable;

    } // end method isClickEnable

    /**
     * Method for getting isAddedDate
     * @return isAddedDate
     */
    public boolean isAddedDate() {
        return isAddedDate;

    } // end method isAddedDate

    /**
     * Method for setting isAddedDate
     * @param isAddedDate - Is added date
     */
    public void setIsAddedDate(boolean isAddedDate) {
        this.isAddedDate = isAddedDate;

    } // end method setIsAddedDate

    /**
     * Method for checking is adapter infinite
     * @return - true if infinite
     */
    public boolean isInfinite() {
        return isInfinite;

    } // end method isInfinite

    /**
     * Method for setting infinite to adapter
     * @param infinite - infinite
     */
    public void setInfinite(boolean infinite) {
        isInfinite = infinite;

    } // end method setInfinite

    /**
     * Method for getting default date list
     * @param context - context
     * @return - default date list
     */
    public static List<DateData> getDateList(Context context) {
        String[] stringDatesTitles = context.getResources().getStringArray(R.array.so_when_items);
        List<DateData> dateList = new ArrayList<>();
        for (int i = 0; i < stringDatesTitles.length; i++) {
            DateData dateData = new DateData();
            if (i == 0) {
                dateData.setHurry(true);
            }
            dateData.setTitle(stringDatesTitles[i]);
            dateList.add(dateData);
        }

        return dateList;

    } // end method getDateList

}
