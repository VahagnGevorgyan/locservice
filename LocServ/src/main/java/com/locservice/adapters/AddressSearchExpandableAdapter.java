package com.locservice.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.locservice.R;
import com.locservice.api.entities.GooglePlace;
import com.locservice.api.entities.GooglePrediction;
import com.locservice.api.entities.Terminal;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.fragments.SearchFragment;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 15 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class AddressSearchExpandableAdapter extends BaseExpandableListAdapter {

    private static final String TAG = AddressSearchExpandableAdapter.class.getSimpleName();
    private Context mContext;
    private List<SearchAddressItem> mSearchAddressList = new ArrayList<>();
    private ExpandableListView mExpandableListView;

    public AddressSearchExpandableAdapter(Context context, List<SearchAddressItem> searchAddressList, ExpandableListView expandableListView) {
        mContext = context;
        mSearchAddressList = searchAddressList;
        this.mExpandableListView = expandableListView;
    }

    @Override
    public int getGroupCount() {
        return mSearchAddressList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(mSearchAddressList != null && mSearchAddressList.size() > 0)
            return mSearchAddressList.get(groupPosition).getTerminalList() != null ? mSearchAddressList.get(groupPosition).getTerminalList().size() : 0;
        return 0;
    }

    @Override
    public SearchAddressItem getGroup(int groupPosition) {
        if(mSearchAddressList != null && mSearchAddressList.size() > 0)
            return mSearchAddressList.get(groupPosition);
        return new SearchAddressItem();
    }

    @Override
    public List<Terminal> getChild(int groupPosition, int childPosition) {
        if(mSearchAddressList != null && mSearchAddressList.size() > 0)
            return mSearchAddressList.get(groupPosition).getTerminalList();
        return new ArrayList<>();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_address_search, parent, false);
        }

        GooglePrediction addressItem = getGroup(groupPosition).getGooglePrediction();
        if (addressItem != null) {
            ArrayList<Integer> types = (ArrayList<Integer>) addressItem.getTypes();

            // primary text
            if (addressItem.getDescription() != null) {
                ((TextView) convertView.findViewById(R.id.textViewAddress)).setText(addressItem.getDescription());
            }

            // secondary text
            if (addressItem.getAddress_secondary() != null) {
                (convertView.findViewById(R.id.textViewAddressSecondary)).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.textViewAddressSecondary)).setText(addressItem.getAddress_secondary());
            } else {
                (convertView.findViewById(R.id.textViewAddressSecondary)).setVisibility(View.GONE);
            }

            if (types.contains(Place.TYPE_AIRPORT)) {
                ((ImageView) convertView.findViewById(R.id.imageViewSearchAddress)).setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_airport));
            } else if (types.contains(Place.TYPE_TRANSIT_STATION) || types.contains(Place.TYPE_TRAIN_STATION)) {
                ((ImageView) convertView.findViewById(R.id.imageViewSearchAddress)).setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_station));
            } else if (types.contains(Place.TYPE_STREET_ADDRESS)) {
                ((ImageView) convertView.findViewById(R.id.imageViewSearchAddress)).setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_road));
            } else if (types.contains(Place.TYPE_ESTABLISHMENT)) {
                ((ImageView) convertView.findViewById(R.id.imageViewSearchAddress)).setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_place_black));
            } else if (types.contains(SearchFragment.TYPE_FAVORITE)) {
                ((ImageView) convertView.findViewById(R.id.imageViewSearchAddress)).setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_heart));
            } else if (types.contains(SearchFragment.TYPE_LAST_ADDRESS)) {
                ((ImageView) convertView.findViewById(R.id.imageViewSearchAddress)).setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_heart_empty));
            } else {
                ((ImageView) convertView.findViewById(R.id.imageViewSearchAddress)).setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_place_black));
            }
        }

        if (getChildrenCount(groupPosition) == 0) {
            convertView.findViewById(R.id.indicator).setVisibility(View.INVISIBLE);
        }
//        else {
//            convertView.findViewById(R.id.indicator).setVisibility(View.VISIBLE);
//            ((ImageView) convertView.findViewById(R.id.indicator)).setImageResource(isExpanded ? R.drawable.arrow_down : R.drawable.arrow_up);
//        }

        mExpandableListView.expandGroup(groupPosition);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_address_terminal_search, parent, false);
        }
        if (mSearchAddressList != null && !mSearchAddressList.isEmpty())
            ((CustomTextView) convertView.findViewById(R.id.textViewAddress)).setText(getChild(groupPosition, childPosition).get(childPosition).getName());
        return convertView;
    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return super.getCombinedChildId(groupId, childId);
    }

    public List<SearchAddressItem> getSearchAddressList() {
        return mSearchAddressList;
    }

    public void clearItems() {
        mSearchAddressList.clear();
    }

    public static class SearchAddressItem {
        private GooglePrediction googlePrediction;
        private GooglePlace googlePlace;
        private List<Terminal> terminalList;

        public GooglePrediction getGooglePrediction() {
            return googlePrediction;
        }

        public void setGooglePrediction(GooglePrediction googlePrediction) {
            this.googlePrediction = googlePrediction;
        }

        public GooglePlace getGooglePlace() {
            return googlePlace;
        }

        public void setGooglePlace(GooglePlace googlePlace) {
            this.googlePlace = googlePlace;
        }

        public List<Terminal> getTerminalList() {
            return terminalList;
        }

        public void setTerminalList(List<Terminal> terminalList) {
            this.terminalList = terminalList;
        }
    }
}
