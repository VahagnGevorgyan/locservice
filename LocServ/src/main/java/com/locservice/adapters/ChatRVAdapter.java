package com.locservice.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.entities.Comment;
import com.locservice.application.LocServicePreferences;
import com.locservice.ui.ChatActivity;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.helpers.DateHelper;
import com.locservice.utils.ImageHelper;
import com.locservice.utils.Logger;

import java.util.Date;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 15 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ChatRVAdapter extends RecyclerView.Adapter<ChatRVAdapter.ViewHolder> {

    private static final String TAG = ChatRVAdapter.class.getSimpleName();

    private static final int TYPE_MESSAGE_SERVICE = 0;
    private static final int TYPE_MESSAGE_USER = 1;
    private GradientDrawable serviceGradientDrawable;
    private Bitmap userBitmap;
    private DateHelper mDateHelper;

    private Context mContext;
    private List<Comment> mCommentList;
    final float scaleToDp;

    public ChatRVAdapter(Context mContext, List<Comment> mCommentList) {
        this.mContext = mContext;
        this.mCommentList = mCommentList;
        String userBase64Image = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BASE_64_IMAGE.key(), "");
        if (!userBase64Image.isEmpty()) {
            this.userBitmap = CMApplication.decodeBase64ToBitmap(userBase64Image);
        }

        mDateHelper = new DateHelper(mContext);
        scaleToDp = mContext.getResources().getDisplayMetrics().density;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;
        switch (viewType) {
            case TYPE_MESSAGE_SERVICE:
                layout = R.layout.list_item_chat_service;
                break;
            case TYPE_MESSAGE_USER:
                layout = R.layout.list_item_chat_user;
                break;
            default:
                layout = R.layout.list_item_chat_service;
                break;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChatRVAdapter.onBindViewHolder : position : " + position);

        int type = holder.getItemViewType();
        final Comment item = mCommentList.get(position);

        holder.textViewMessage.setText(item.getComment());

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChatRVAdapter.onBindViewHolder : item.getComment : " + item.getComment());
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ChatRVAdapter.onBindViewHolder : date : " + item.getDate());

        if (type == TYPE_MESSAGE_USER) { //by user
            if (userBitmap != null) {
                holder.imageViewAvatar.setImageBitmap(userBitmap);
            }
            holder.textViewDoSomething.setVisibility(View.GONE);
            holder.textViewMessage.setTextColor(Color.BLACK);

            if (position != 0 && mCommentList.get(position - 1).getByuser() == 1) {
//                holder.layoutMessageContent.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_chat_user));
                holder.imageViewTriangle.setVisibility(View.INVISIBLE);
                holder.imageViewAvatar.setVisibility(View.INVISIBLE);
            } else {
//                holder.layoutMessageContent.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_chat_bottom));
                holder.imageViewTriangle.setVisibility(View.VISIBLE);
                holder.imageViewAvatar.setVisibility(View.VISIBLE);
            }
            if (position < mCommentList.size() - 1 && mCommentList.get(position + 1).getByuser() == 1) {
                holder.textViewOperatorOrTime.setVisibility(View.GONE);
                holder.textViewMessage.setPadding((int) (16 * scaleToDp + 0.5f), (int) (16 * scaleToDp + 0.5f), (int) (16 * scaleToDp + 0.5f), (int) (16 * scaleToDp + 0.5f));
            } else {
                holder.textViewOperatorOrTime.setVisibility(View.VISIBLE);
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ChatRVAdapter.onBindViewHolder : date : timestamp : " + item.getDate());
                Date date = mDateHelper.getDateByTimeStamp(item.getDate());
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ChatRVAdapter.onBindViewHolder : date : time : " + date);
                holder.textViewOperatorOrTime.setText(mDateHelper.getStringDateByUI(date));
                holder.textViewOperatorOrTime.setTextColor(Color.parseColor("#9d9d9d"));
                holder.textViewMessage.setPadding((int) (16 * scaleToDp + 0.5f), (int) (16 * scaleToDp + 0.5f), (int) (16 * scaleToDp + 0.5f), 0);
            }
        } else { // by service
            if (position != 0) {
                if (item.getUserPhoto() != null && !item.getUserPhoto().isEmpty()) {
                    Bitmap bitmap = ImageHelper.memCache.getBitmapFromMemCache(item.getUserPhoto());
                    if (bitmap != null) {
                        holder.imageViewAvatar.setImageBitmap(bitmap);
                    } else {
                        ImageHelper.loadImagesByLoader(mContext, item.getUserPhoto(), holder.imageViewAvatar);
                    }
                }
            } else {
                holder.imageViewAvatar.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.chat_logo));
            }
            if (position == 0) {
                holder.textViewDoSomething.setVisibility(View.VISIBLE);
                holder.textViewOperatorOrTime.setVisibility(View.GONE);
            } else {
                holder.textViewDoSomething.setVisibility(View.GONE);
                holder.textViewOperatorOrTime.setVisibility(View.VISIBLE);
            }
            String operator = item.getUserName() + " " + item.getUserPlace();
            holder.textViewOperatorOrTime.setText(operator);
            holder.textViewOperatorOrTime.setTextColor(Color.parseColor("#f7c68b"));
            holder.textViewMessage.setTextColor(Color.WHITE);
            if (position != 0 && mCommentList.get(position - 1).getByuser() != 1) {
//                holder.layoutMessageContent.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_chat_service_second));
                holder.imageViewTriangle.setVisibility(View.INVISIBLE);

                holder.imageViewAvatar.setVisibility(View.INVISIBLE);
            } else {
//                holder.layoutMessageContent.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_chat_service_first));
                holder.imageViewTriangle.setVisibility(View.VISIBLE);

                holder.imageViewAvatar.setVisibility(View.VISIBLE);
            }

            holder.textViewDoSomething.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // get current city phone number
                    String phoneNumber = ((ChatActivity)mContext).getCurrentCityPhone();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    mContext.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        if (mCommentList != null)
            return mCommentList.size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mCommentList.get(position).getByuser() == 1 ? TYPE_MESSAGE_USER : TYPE_MESSAGE_SERVICE;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAvatar;
        CustomTextView textViewMessage;
        CustomTextView textViewOperatorOrTime;
        CustomTextView textViewDoSomething;
        RelativeLayout layoutMessageContent;
        ImageView imageViewTriangle;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewAvatar = (ImageView) itemView.findViewById(R.id.imageViewAvatar);
            textViewMessage = (CustomTextView) itemView.findViewById(R.id.textViewMessage);
            textViewOperatorOrTime = (CustomTextView) itemView.findViewById(R.id.textViewOperatorOrTime);
            textViewDoSomething = (CustomTextView) itemView.findViewById(R.id.textViewDoSomething);
            layoutMessageContent = (RelativeLayout) itemView.findViewById(R.id.layoutMessageContent);
            imageViewTriangle = (ImageView) itemView.findViewById(R.id.imageViewTriangle);
        }
    }
}
