package com.locservice.adapters;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.entities.DriverInfoFeedback;
import com.locservice.ui.helpers.DateHelper;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 19 November 2015
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DriverReviewAdapter extends ArrayAdapter<DriverInfoFeedback> {

	private static final String TAG = DriverReviewAdapter.class.getSimpleName();

	private final List<DriverInfoFeedback> reviews;
	private final Activity context;
	private DateHelper mDateHelper;


	public DriverReviewAdapter(Activity context, List<DriverInfoFeedback> reviews) {
		super(context, R.layout.list_item_order, reviews);
		this.context = context;
		this.reviews = reviews;
		mDateHelper = new DateHelper(this.context);
	}

	// Class to save to an external class and to
	// limit the access of the descendants of the class
	static class ViewHolder {
		public TextView textViewDate;
		public TextView textViewDescription;
		public LinearLayout layoutItem;
		public View dividerLine;
		public RatingBar ratingBarReview;
	}

	public final int getCount() {
    	return reviews.size();
    }

    public final DriverInfoFeedback getItem(int position) {
        return reviews.get(position);
    }

    public final long getItemId(int position) {
        return position;
    }

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DriverReviewAdapter.ADAPTER : position : " + position);

		ViewHolder holder;
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_item_review, parent, false);
			holder = new ViewHolder();

			holder.textViewDate = (TextView) rowView.findViewById(R.id.textViewDate);
			holder.textViewDescription = (TextView) rowView.findViewById(R.id.textViewDesc);
			holder.layoutItem = (LinearLayout) rowView.findViewById(R.id.layoutItem);
			holder.dividerLine = (View) rowView.findViewById(R.id.dividerLine);
			holder.ratingBarReview = (RatingBar) rowView.findViewById(R.id.ratingBarReview);


			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}

		Date itemDate = mDateHelper.getDateByStringDateFormat(reviews.get(position).getDate(), "dd-MM-yyyy HH:mm");
		holder.textViewDate.setText(mDateHelper.getStringDateByUI(itemDate));
		holder.textViewDescription.setText(reviews.get(position).getComment());
		holder.ratingBarReview.setRating(Float.parseFloat(reviews.get(position).getRating()));

		return rowView;
	}

}
