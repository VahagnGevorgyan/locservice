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
import com.locservice.api.entities.GooglePlace;
import com.locservice.protocol.IOptionAdapter;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.utils.ScrollType;
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
public class AddressRVAdapter extends RecyclerView.Adapter<AddressRVAdapter.ViewHolder> implements IOptionAdapter  {

    private static final String TAG = AddressRVAdapter.class.getSimpleName();

    private final Typeface robotoLight;
    private final Typeface robotoRegular;
    private boolean isInfinite;
    private ScrollType mScrollType;
    private int currentPosition;

    private Context mContext;
    private List<GooglePlace> mAddressList;
    private boolean isClickEnable = true;

    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE/2;
    public final int MIDDLE;
    private AddressOnItemClickListener mOnItemClickListener;
    private CustomTextView currentTextView;

    public AddressRVAdapter(Context mContext, List<GooglePlace> mAddressList, ScrollType scrollType, int currentPosition,  boolean isInfinite) {
        this.mContext = mContext;
        this.mAddressList = mAddressList;
        this.mScrollType = scrollType;
        this.currentPosition = currentPosition;
        if (mAddressList.size() > 0)
            MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % mAddressList.size();
        else
            MIDDLE = 0;
        robotoLight = Typefaces.get(mContext, "fonts/RobotoLight.ttf");
        robotoRegular = Typefaces.get(mContext, "fonts/RobotoRegular.ttf");
        this.isInfinite = isInfinite;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (mScrollType == ScrollType.SO_FROM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_so_from, parent, false);

        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_so_options, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mAddressList.size() > 0) {
            int realPosition;
            if (isInfinite)  {
                realPosition = position % mAddressList.size();
            } else {
                realPosition = position;
            }
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddressRVAdapter.onBindViewHolder : position : " + position + " : realPosition : " + realPosition);

            GooglePlace item = mAddressList.get(realPosition);
            float fontScale = mContext.getResources().getConfiguration().fontScale;
            if(holder.textViewItem != null) {
                holder.textViewItem.setText(item.getName());
                holder.textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
                holder.textViewItem.setTypeface(robotoLight);
                holder.textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                        mContext.getResources().getInteger(R.integer.int_slide_panel_items_unselected_text_size) / fontScale);
                holder.textViewItem.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.carousel_item_top_padding), 0, 0);

                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddressRVAdapter.onBindViewHolder : item.getArea() : " + item.getArea());

                if(item.getArea() != null && !item.getArea().equals("")) {
                    holder.textViewItemSmall.setVisibility(View.VISIBLE);
                    holder.textViewItemSmall.setText(item.getArea());
                    holder.textViewItemSmall.setTextColor(ContextCompat.getColor(mContext, R.color.grey_info));
                    holder.textViewItemSmall.setTypeface(robotoLight);
                    holder.textViewItemSmall.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                            mContext.getResources().getInteger(R.integer.int_slide_panel_items_small_text_size) / fontScale);
                } else {
                    if (mScrollType == ScrollType.SO_FROM) {
                        if (mAddressList.get(0).getArea() == null || mAddressList.get(0).getArea().isEmpty()) {
                            holder.textViewItemSmall.setVisibility(View.GONE);
                        } else {
                            holder.textViewItemSmall.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        holder.textViewItemSmall.setVisibility(View.GONE);
                    }
                }

                if (currentPosition == realPosition) {
                    holder.textViewItem.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                    holder.textViewItem.setTypeface(robotoRegular);
                    holder.textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                            mContext.getResources().getInteger(R.integer.int_slide_panel_items_selected_text_size)/ fontScale);
                    holder.textViewItem.setPadding(0, 0, 0, 0);
                    currentTextView = holder.textViewItem;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (isInfinite)
            return Integer.MAX_VALUE;
        else if (mAddressList != null)
            return mAddressList.size();

        return 0;
    }

    @Override
    public void setItemClickState(boolean isEnable) {
        isClickEnable = isEnable;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextView textViewItem;
        CustomTextView textViewItemSmall;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewItem = (CustomTextView) itemView.findViewById(R.id.textViewItem);
            textViewItemSmall = (CustomTextView) itemView.findViewById(R.id.textViewItemSmall);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            GooglePlace address;
            if (mAddressList!= null && mAddressList.size() > 0) {
                if (isInfinite) {
                    if (getAdapterPosition() % mAddressList.size() < mAddressList.size()
                            && getAdapterPosition() % mAddressList.size() >= 0
                            && getAdapterPosition() >= 0) {
                        address = mAddressList.get(getAdapterPosition() % mAddressList.size());
                        mOnItemClickListener.addressOnItemClick(itemView, getAdapterPosition(), address, currentTextView, mScrollType);
                    }
                } else {
                    if (getAdapterPosition() < mAddressList.size()
                            && getAdapterPosition() >= 0) {
                        address = mAddressList.get(getAdapterPosition());
                        mOnItemClickListener.addressOnItemClick(itemView, getAdapterPosition(), address, currentTextView, mScrollType);
                    }
                }
            }
        }
    }

    public interface AddressOnItemClickListener {
        void addressOnItemClick(View itemView, int position, GooglePlace item, TextView currentTextView, ScrollType scrollType);
    }

    /**
     * Method for setting OnItemClickListener
     *
     * @param onItemClickListener - item click listener
     */
    public void setOnItemClickListener (AddressOnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;

    } // end method setOnItemClickListener


    /**
     * Method for getting middle position
     *
     * @return - middle position
     */
    public long getMiddlePosition() {
        long middle = 0;
        if(mAddressList.size() > 0)
            middle = HALF_MAX_VALUE - HALF_MAX_VALUE % mAddressList.size();

        return middle;

    } // end method getMiddlePosition

    /**
     * Method for getting mScrollType
     *
     * @return - scroll type
     */
    public ScrollType getScrollType() {
        return mScrollType;

    } // end method getScrollType

    /**
     * Method for setting mScrollType
     * @param mScrollType - scroll type
     */
    public void setScrollType(ScrollType mScrollType) {
        this.mScrollType = mScrollType;

    } // end method setScrollType

    /**
     * Method for getting isInfinite
     * @return isInfinite
     */
    public boolean isInfinite() {
        return isInfinite;

    } // end method isInfinite

    /**
     * Method for setting isInfinite
     * @param isInfinite - is infinite
     */
    public void setIsInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;
    } // end method setIsInfinite

    /**
     * Method for getting currentPosition
     * @return currentPosition
     */
    public int getCurrentPosition() {
        return currentPosition;

    } // end method getCurrentPosition

    /**
     * Method for getting currentPosition
     * @param currentPosition - current position
     */
    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;

    } // end method setCurrentPosition

    /**
     * Method for getting isClickEnable
     * @return isClickEnable
     */
    public boolean isClickEnable() {
        return isClickEnable;

    } // end method isClickEnable

    /**
     * Method for setting isClickEnable
     * @param isClickEnable - is click enable
     */
    public void setIsClickEnable(boolean isClickEnable) {
        this.isClickEnable = isClickEnable;

    } // end method setIsClickEnable

    /**
     * Method for setting currentTextView
     * @param currentTextView - current text View
     */
    public void setCurrentTextView(CustomTextView currentTextView) {
        this.currentTextView = currentTextView;

    } // end method setCurrentTextView

    public static List<GooglePlace> getListByScrollType(Context context, ScrollType scrollType) {
        List<GooglePlace> list = new ArrayList<>();
        switch (scrollType) {
            case SO_FROM:
                String[] arrayFrom = context.getResources().getStringArray(R.array.so_from_items);
                for (String fromItem : arrayFrom) {
                    GooglePlace item = new GooglePlace();
                    item.setName(fromItem);
                    list.add(item);
                }
                break;
            case SO_WHERE:
                String[] arrayWhere = context.getResources().getStringArray(R.array.so_where_items);
                for (String whereItem : arrayWhere) {
                    GooglePlace item = new GooglePlace();
                    item.setName(whereItem);
                    list.add(item);
                }
                break;
            default:
                break;
        }
        return list;
    }
}
