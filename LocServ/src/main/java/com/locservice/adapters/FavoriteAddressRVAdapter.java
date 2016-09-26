package com.locservice.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.entities.GetFavoriteData;
import com.locservice.api.manager.FavoriteAddressManager;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.utils.Logger;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 25 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class FavoriteAddressRVAdapter extends RecyclerSwipeAdapter<FavoriteAddressRVAdapter.ViewHolder> {

    private static final String TAG = FavoriteAddressRVAdapter.class.getSimpleName();
    private Context mContext;
    private List<GetFavoriteData> favoriteList;
    private OnItemClickListener mItemClickListener;
    private SwipeLayout currentSwipeLayout;
    private int removedPosition = -1;

    public FavoriteAddressRVAdapter(Context mContext, List<GetFavoriteData> favoriteList) {
        this.mContext = mContext;
        this.favoriteList = favoriteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favorite_addresses, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressRVAdapter : position : " + position);

        final GetFavoriteData item = favoriteList.get(position);

        if (item.getName() != null)
            viewHolder.textViewAddressType.setText(item.getName());

        if (item.getAddress() != null)
            viewHolder.textViewAddress.setText(item.getAddress());

        viewHolder.textViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressRVAdapter : position : " + position);
                // Request remove favorite address
                FavoriteAddressManager favoriteAddressManager = new FavoriteAddressManager((ICallBack) mContext);
                favoriteAddressManager.RemoveFavorite(item.getId());
                removedPosition = position;
//                favoriteList.remove(position);
//                notifyItemRemoved(position);
//                notifyItemRangeChanged(position, favoriteList.size());
            }
        });

        viewHolder.swipeLayout.setRightSwipeEnabled(false);
        viewHolder.swipeLayout.setLeftSwipeEnabled(false);
        viewHolder.layoutItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                builder.setMessage(R.string.alert_favorite_address_delete);
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
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressRVAdapter : position : " + position);
                        // Request remove favorite address
                        FavoriteAddressManager favoriteAddressManager = new FavoriteAddressManager((ICallBack) mContext);
                        favoriteAddressManager.RemoveFavorite(item.getId());
                        removedPosition = position;
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });

//        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
//        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener(){
//            @Override
//            public void onStartOpen(SwipeLayout layout) {
//                super.onStartOpen(layout);
//                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryAdapter.addSwipeListener.onStartOpen layout : " + layout);
//                if (currentSwipeLayout != null)
//                    currentSwipeLayout.close(true);
//            }
//
//            @Override
//            public void onOpen(SwipeLayout layout) {
//                super.onOpen(layout);
//                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderHistoryAdapter.addSwipeListener.onOpen layout : " + layout);
//                currentSwipeLayout = viewHolder.swipeLayout;
//            }
//        });

        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: OrderHistoryAdapter.addSwipeListener.onStartOpen layout : " + layout);
//                if (currentSwipeLayout != null)
//                    currentSwipeLayout.close(true);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: OrderHistoryAdapter.addSwipeListener.onOpen layout : " + layout);
//                currentSwipeLayout = viewHolder.swipeLayout;
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: OrderHistoryAdapter.addSwipeListener.onStartClose layout : " + layout);
            }

            @Override
            public void onClose(SwipeLayout layout) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: OrderHistoryAdapter.addSwipeListener.onClose layout : " + layout);
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: OrderHistoryAdapter.addSwipeListener.onUpdate layout : " + layout);
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: OrderHistoryAdapter.addSwipeListener.onHandRelease layout : " + layout + " : xvel : " + xvel + " : yvel : " + yvel);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (favoriteList != null) {
            return favoriteList.size();
        }
        return 0;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SwipeLayout swipeLayout;
        CustomTextView textViewAddressType;
        CustomTextView textViewAddress;
        CustomTextView textViewDelete;
        LinearLayout layoutItem;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            textViewAddressType = (CustomTextView) itemView.findViewById(R.id.textViewAddressType);
            textViewAddress = (CustomTextView) itemView.findViewById(R.id.textViewAddress);
            textViewDelete = (CustomTextView) itemView.findViewById(R.id.textViewDelete);
            layoutItem = (LinearLayout) itemView.findViewById(R.id.layoutItem);
            layoutItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Logger.i(TAG, ":: OrderHistoryAdapter.onClick : mItemClickListener : " + mItemClickListener);
            if (mItemClickListener != null) {
                if(favoriteList != null && favoriteList.size() > 0) {
                    Logger.i(TAG, ":: OrderHistoryAdapter.onClick : favoriteList : " + favoriteList);
                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), favoriteList.get(getAdapterPosition()));
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, GetFavoriteData favoriteData);
    }

    /**
     * Method for setting On Item Click Listener
     * @param mItemClickListener
     */
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;

    } // end method setOnItemClickListener

    /**
     * Method for getting removedPosition
     * @return removedPosition
     */
    public int getRemovedPosition() {
        return removedPosition;

    } // end method getRemovedPosition
}
