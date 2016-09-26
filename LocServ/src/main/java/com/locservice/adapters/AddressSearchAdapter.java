package com.locservice.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.entities.GooglePlace;
import com.locservice.api.entities.GooglePrediction;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.utils.Logger;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vahagn Gevorgyan
 * 15 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class AddressSearchAdapter extends RecyclerView.Adapter<AddressSearchAdapter.ViewHolder> implements Filterable {

    private static final String TAG = AddressSearchAdapter.class.getSimpleName();

    private OnItemClickListener mItemClickListener;
//    private ArrayList<SearchAddressItem> addresses = new ArrayList<SearchAddressItem>();
    private List<SearchAddressItem> addresses;
    private Context context;
    private boolean start = true;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds mLatLngBounds;
    private AutocompleteFilter mAutocompleteFilter;
    private int layout;

    public AutocompleteFilter getmAutocompleteFilter() {
        return mAutocompleteFilter;
    }

    public void setAutocompleteFilter(AutocompleteFilter mAutocompleteFilter) {
        this.mAutocompleteFilter = mAutocompleteFilter;
    }

    public LatLngBounds getmLatLngBounds() {
        return mLatLngBounds;
    }

    public void setmLatLngBounds(LatLngBounds mLatLngBounds) {
        this.mLatLngBounds = mLatLngBounds;
    }

    public List<SearchAddressItem> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<SearchAddressItem> addresses) {
        this.addresses = addresses;
    }

    public void clearItems() {
        this.addresses.clear();
    }

    public AddressSearchAdapter(Context context, int layout, List<SearchAddressItem> addresses, GoogleApiClient googleApiClient, LatLngBounds latLngBounds, AutocompleteFilter autocompleteFilter) {
        this.context = context;
        this.mGoogleApiClient = googleApiClient;
        this.mLatLngBounds = latLngBounds;
        this.mAutocompleteFilter = autocompleteFilter;
        this.layout = layout;
        this.addresses = addresses;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddressSearchAdapter.onCreateViewHolder : position : " + arg1);

//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_address_search, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GooglePrediction addressItem = addresses.get(position).getGooglePrediction();

//        holder.imageViewSearchAddress.setVisibility(View.INVISIBLE);
        holder.textViewTime.setVisibility(View.INVISIBLE);

        if (addressItem.getDescription() != null) {
            holder.textViewAddress.setText(addressItem.getDescription());
        }

        // secondary text
        if (addressItem.getAddress_secondary() != null) {
            holder.textViewAddressSecondary.setVisibility(View.VISIBLE);
            holder.textViewAddressSecondary.setText(addressItem.getAddress_secondary());
        } else {
            holder.textViewAddressSecondary.setVisibility(View.GONE);
        }

        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: AddressSearchAdapter.onBindViewHolder : position : " + position);
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: AddressSearchAdapter.onBindViewHolder : addresses.size : " + addresses.size());
        ArrayList<Integer> types = (ArrayList<Integer>) addressItem.getTypes();

        if (types.contains(Place.TYPE_AIRPORT)) {
            holder.imageViewSearchAddress.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_airport));
        }else if (types.contains(Place.TYPE_TRANSIT_STATION) || types.contains(Place.TYPE_TRAIN_STATION)){
            holder.imageViewSearchAddress.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_station));
        } else if (types.contains(Place.TYPE_STREET_ADDRESS)){
            holder.imageViewSearchAddress.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_road));
        } else if (types.contains(Place.TYPE_ESTABLISHMENT)) {
            holder.imageViewSearchAddress.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_place_black));
        }


//		Landmark landmark = addressItem.getLandmark();
//		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddressSearchAdapter.onBindViewHolder : landmark : " + landmark);
//		if(landmark != null)
//        {
//            String icon = landmark.getIcon();
//            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddressSearchAdapter.onBindViewHolder : ICON : " + icon);
//            holder.imageViewSearchAddress.setVisibility(View.VISIBLE);
//            switch (icon) {
//                case "METRO":
//                    holder.imageViewSearchAddress.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_metro));
//                    break;
//                case "HIGHWAY_POINT":
//                    holder.imageViewSearchAddress.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_road));
//                    break;
//                case "AIRPORT":
//                    holder.imageViewSearchAddress.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_airport));
//                    break;
//                case "RAILWAY_STATION":
//                    holder.imageViewSearchAddress.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_station));
//                    break;
//                case "LOCALITY":
//                    holder.imageViewSearchAddress.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_place_black));
//                    break;
//                default:
//                    break;
//            }
//        }
    }

    @Override
    public int getItemCount() {
        if (addresses != null) {
            return addresses.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout layoutSearchAddressItem;
        ImageView imageViewSearchAddress;
        CustomTextView textViewAddress;
        CustomTextView textViewAddressSecondary;
        CustomTextView textViewTime;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewSearchAddress = (ImageView) itemView.findViewById(R.id.imageViewSearchAddress);
            textViewAddress = (CustomTextView) itemView.findViewById(R.id.textViewAddress);
            textViewAddressSecondary = (CustomTextView) itemView.findViewById(R.id.textViewAddressSecondary);
            textViewTime = (CustomTextView) itemView.findViewById(R.id.textViewTime);
            layoutSearchAddressItem = (LinearLayout) itemView.findViewById(R.id.layoutSearchAddressItem);
            layoutSearchAddressItem.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                if (addresses != null && addresses.size() > 0) {
                    if (getAdapterPosition() >=0 && getAdapterPosition() < addresses.size()) {
                        mItemClickListener.onItemClick(itemView, getAdapterPosition(), addresses.get(getAdapterPosition()));
                    }
                }
            }
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, SearchAddressItem address);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;

    }


	@Override
	public Filter getFilter() {
		Filter myFilter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if(CMAppGlobals.DEBUG)Logger.i(TAG, "::AddressSearchAdapter.getFilter : performFiltering with constraint: " + constraint);

				if (constraint != null) {
//                    addresses = getAutocomplete(constraint);
//                    if (addresses != null) {
//                        filterResults.values = AddressSearchAdapter.this.addresses;
//                        filterResults.count = ((ArrayList<GooglePrediction>) AddressSearchAdapter.this.addresses).size();
//                    }
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence contraint, FilterResults results) {
				if (results != null && results.count > 0) {
                    notifyDataSetChanged();
				} else {

				}
			}
		};
		return myFilter;
	}

    public void fetchAddressData(String street) {
        ArrayList<GooglePrediction> addressSet = new ArrayList<GooglePrediction>();
        try {

            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: AddressSearchAdapter.fetchAddressData : Request Finished : SIZE : " + this.addresses.size());
        } catch (final Exception e) {
            if (CMAppGlobals.DEBUG)
                Logger.e(TAG, ":: AddressSearchAdapter.fetchAddressData : Error : Exception while request : " + e.getMessage());
        }
    }

    public ArrayList<GooglePrediction> getAutocomplete (CharSequence charSequence) {
        if (mGoogleApiClient.isConnected()) {
            PendingResult<AutocompletePredictionBuffer> result =
                    Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, charSequence.toString(), mLatLngBounds, mAutocompleteFilter);
            AutocompletePredictionBuffer autocompletePredictions = result.await(60, TimeUnit.SECONDS);
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                if (CMAppGlobals.DEBUG) Logger.e(TAG, ":: AddressSearchAdapter.getAutocomplete : Error getting autocomplete API call");
                autocompletePredictions.release();
                return null;
            }

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();

                CharacterStyle cs = new CharacterStyle() {
                    @Override
                    public void updateDrawState(TextPaint tp) {

                    }
                };
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddressSearchAdapter.getAutocomplete : FullText : " + prediction.getFullText(cs));
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddressSearchAdapter.getAutocomplete : PrimaryText : " + prediction.getPrimaryText(cs));
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddressSearchAdapter.getAutocomplete : SecondaryText : " + prediction.getSecondaryText(cs));

                GooglePrediction googlePrediction = new GooglePrediction();
//                if(prediction.getPrimaryText(cs) != null) {
//                    String desc = removeCountryCityFromAddress(prediction.getPrimaryText(cs).toString());
//                    googlePrediction.setDescription(desc);
//                }

                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddressSearchAdapter.getAutocomplete : PLACE_ID : " + prediction.getPlaceId());

                googlePrediction.setDescription(prediction.getPrimaryText(cs).toString());
                googlePrediction.setPlaceId(prediction.getPlaceId());
                googlePrediction.setTypes(prediction.getPlaceTypes());

                resultList.add(googlePrediction);
            }
            autocompletePredictions.release();
            return resultList;

        } else {
            if (CMAppGlobals.DEBUG) Logger.e(TAG, "Error GoogleApiClient is not connected");
            return null;
        }
    }

    /**
     * Method for removing country & city from address full text
     * @param fullAddressText
     */
    public String removeCountryCityFromAddress(String fullAddressText) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddressSearchAdapter.removeCountryCityFromAddress : fullAddressText : " + fullAddressText);

        String result = "";
        if(fullAddressText != null && !fullAddressText.equals("")) {
            String [] strArray = fullAddressText.split(",");
            for (int i = 0; i<strArray.length; i++) {
                String item = strArray[i];
                if(!item.equals("") && i < strArray.length - 2) {
                    result += item;
                    if(strArray.length > 2 && i < strArray.length - 3)
                        result += ",";
                }
            }
        }

        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddressSearchAdapter.removeCountryCityFromAddress : result : " + result);
        return result;

    } // end method removeCountryCityFromAddress

    public boolean isStart() {
        return start;
    }


    public void setStart(boolean start) {
        this.start = start;
    }


    public static class SearchAddressItem {
        private GooglePrediction googlePrediction;
        private GooglePlace googlePlace;

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
    }

    public boolean isAddressContains(SearchAddressItem address) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddressSearchAdapter.isAddressContains : address : lat : " + address.getGooglePlace().getLatLng().latitude
                + " : lng : " + address.getGooglePlace().getLatLng().longitude);

        if(getAddresses()!=null && getAddresses().size() > 0) {
            for (SearchAddressItem item :
                    getAddresses()) {
                if(item != null && item.getGooglePlace() != null) {
                    if(item.getGooglePlace().getLatLng().latitude == address.getGooglePlace().getLatLng().latitude &&
                            item.getGooglePlace().getLatLng().longitude == address.getGooglePlace().getLatLng().longitude) {
                        if(CMAppGlobals.DEBUG)Logger.i(TAG, "::AddressSearchAdapter.isAddressContains : true ");
                        return true;
                    }
                }
            }
        }

        if(CMAppGlobals.DEBUG)Logger.i(TAG, "::AddressSearchAdapter.isAddressContains : false ");
        return false;

    } // end method isAddressContains
}
