package com.locservice.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.entities.CardInfo;
import com.locservice.api.manager.CreditCardManager;
import com.locservice.api.request.UnBindCardRequest;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.utils.Logger;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 27 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CreditCardsRVAdapter extends RecyclerSwipeAdapter<CreditCardsRVAdapter.ViewHolder> {

    private static final String TAG = CreditCardsRVAdapter.class.getSimpleName();
    private Context mContext;
    private List<CardInfo> cardInfoList;
    private int removedPosition = -1;

    public CreditCardsRVAdapter(Context mContext, List<CardInfo> cardInfoList) {
        this.mContext = mContext;
        this.cardInfoList = cardInfoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card, parent, false);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardsRVAdapter : position : " + position);

        final CardInfo item = cardInfoList.get(position);

        viewHolder.textViewHolderName.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        viewHolder.textViewBankName.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        if (item.getCardholder() != null) {
            viewHolder.textViewHolderName.setText(item.getCardholder());
        } else {
            viewHolder.textViewHolderName.setText("");
        }

        if (item.getBank() != null) {
            viewHolder.textViewBankName.setText(item.getBank());
        } else {
            viewHolder.textViewBankName.setText("");
        }

        if(item.getPan() != null) {
            String cardPan = "**** **** **** " + item.getPan();
            viewHolder.textViewCardPan.setText(cardPan);
        } else {
            viewHolder.textViewCardPan.setText("");
        }

        viewHolder.imageViewClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardsRVAdapter : CLOSE_CREDIT_CARD : position : " + position + " : card_id : " + item.getId());
                // UNBIND CREDIT CARD
                CreditCardManager creditCardManager= new CreditCardManager((ICallBack) mContext);
                UnBindCardRequest unBindCardRequest = new UnBindCardRequest();
                unBindCardRequest.setId(item.getId());
                creditCardManager.UnbindCard(unBindCardRequest);
                removedPosition = position;
            }
        });

        // SET BACKGROUND
        if(item.getColor() != null) {
            GradientDrawable shape =  new GradientDrawable();
            shape.setCornerRadius(30);
            if(!item.getColor().equals("")) {
                String colorStr = "#" + item.getColor();
                shape.setColor(Color.parseColor(colorStr));
            } else {
                String colorStr = "#339966";
                shape.setColor(Color.parseColor(colorStr));
            }
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.layoutItem.setBackgroundDrawable(shape);
            } else {
                viewHolder.layoutItem.setBackground(shape);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (cardInfoList != null) {
            return cardInfoList.size();
        }
        return 0;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CustomTextView textViewBankName;
        CustomTextView textViewHolderName;
        ImageView imageViewClear;
        LinearLayout layoutItem;
        CustomTextView textViewCardPan;
        CustomTextView textViewDate;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewBankName = (CustomTextView) itemView.findViewById(R.id.textViewBankName);
            textViewHolderName = (CustomTextView) itemView.findViewById(R.id.textViewHolderName);
            textViewDate = (CustomTextView) itemView.findViewById(R.id.textViewDate);
            textViewCardPan = (CustomTextView) itemView.findViewById(R.id.textViewCardPan);
            imageViewClear = (ImageView) itemView.findViewById(R.id.imageViewClear);
            layoutItem = (LinearLayout) itemView.findViewById(R.id.layoutItem);
        }

    }

    /**
     * Method for getting removedPosition
     * @return removedPosition
     */
    public int getRemovedPosition() {
        return removedPosition;

    } // end method getRemovedPosition

}
