package com.locservice.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.utils.Logger;

import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 22 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.ViewHolder> {

    private static final String TAG = LanguageListAdapter.class.getSimpleName();

    private Context mContext;
    private List<String> languageList;
    private OnItemClickListener mItemClickListener;

    public LanguageListAdapter(Context mContext, List<String> languageList) {
        this.mContext = mContext;
        this.languageList = languageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: LanguageListAdapter : onCreateViewHolder : parent : " + parent + " : viewType : " + viewType);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_language_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: LanguageListAdapter : onBindViewHolder : holder : " + holder + " : position : " + position);

        String language = languageList.get(position);

        holder.textViewLanguageItem.setText(language);
    }

    @Override
    public int getItemCount() {
        if (languageList != null) {
            return languageList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextView textViewLanguageItem;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewLanguageItem = (CustomTextView) itemView.findViewById(R.id.textViewLanguageItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(itemView, getAdapterPosition(), languageList.get(getAdapterPosition()));
        }
    }

    /**
     * interface OnItemClickListener
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position, String language);

    } // end interface OnItemClickListener

    /**
     * Method for  setting Item Click Listener
     * @param mItemClickListener
     */
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;

    } // end method setOnItemClickListener

}
