package com.locservice.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.adapters.FavoriteAddressRVAdapter;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.GetFavoriteData;
import com.locservice.api.manager.FavoriteAddressManager;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.AddFavoriteAddressActivity;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;
import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.List;


public class FavoriteAddressesFragment extends Fragment implements View.OnClickListener {

	private static final String TAG = FavoriteAddressesFragment.class.getSimpleName();
	private LinearLayout layoutBack;
	private RecyclerView recyclerViewFavoriteAddresses;
	private FavoriteAddressRVAdapter adapter;
	private List<GetFavoriteData> favoriteAddresses = new ArrayList<>();
	private ImageView imageViewAdd;
	private View rootView;
	private FragmentActivity mContext;
	private FavoriteAddressManager favoriteAddressManager;

	private FavoriteAddressRVAdapter.OnItemClickListener mItemClickListener = new FavoriteAddressRVAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position, GetFavoriteData favoriteData) {
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressFragment.onItemClick : position : " + position + " : favoriteData : " + favoriteData);

			// check network status
			if (Utils.checkNetworkStatus(mContext)) {
				Intent intent = new Intent(mContext, AddFavoriteAddressActivity.class);
				intent.putExtra(CMAppGlobals.EXTRA_FAVORITE_ADDRESS, favoriteData);
				startActivityForResult(intent, CMAppGlobals.REQUEST_ADD_FAVORITE_ADDRESS);
			}
		}
	};

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (rootView != null)
            return rootView;
		rootView = inflater.inflate(R.layout.fragment_favorite_addresses, container, false);
		mContext = getActivity();

		recyclerViewFavoriteAddresses = (RecyclerView) rootView.findViewById(R.id.recyclerViewFavoriteAddresses);
		LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
		recyclerViewFavoriteAddresses.setLayoutManager(layoutManager);

		// Request for getting favorite addresses
		favoriteAddressManager = new FavoriteAddressManager((ICallBack) mContext);
		favoriteAddressManager.GetFavorite(RequestTypes.REQUEST_GET_FAVORITE);

		imageViewAdd = (ImageView) rootView.findViewById(R.id.imageViewAdd);
		imageViewAdd.setOnClickListener(this);
		layoutBack = (LinearLayout) rootView.findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(this);

		if(CMApplication.hasNavigationBar(mContext)) {
			final float scale = getResources().getDisplayMetrics().density;
			int padding_in_px = (int) (50 * scale + 0.5f);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imageViewAdd.getLayoutParams();
			params.setMargins(0,0,0,padding_in_px);
			imageViewAdd.setLayoutParams(params);
			recyclerViewFavoriteAddresses.setPadding(0,0,0,padding_in_px);
		}

		return rootView;
	}
	
	@Override
	public void onClick(View v) {
		// check network status
		if (Utils.checkNetworkStatus(mContext)) {

			switch (v.getId()) {
			case R.id.layoutBack:
				if (mContext !=  null) {
					mContext.onBackPressed();
				}
				break;
			case R.id.imageViewAdd:
				Intent intent = new Intent(mContext, AddFavoriteAddressActivity.class);
				startActivityForResult(intent, CMAppGlobals.REQUEST_ADD_FAVORITE_ADDRESS);
				break;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressFragment.onActivityResult : requestCode + " + requestCode
				+ " : resultCode : " + resultCode
				+ " : data : " + data);

		if (requestCode == CMAppGlobals.REQUEST_ADD_FAVORITE_ADDRESS && resultCode == Activity.RESULT_OK && data != null) {
			GetFavoriteData favoriteData = data.getExtras().getParcelable(CMAppGlobals.EXTRA_FAVORITE_ADDRESS);
			String id = data.getExtras().getString(CMAppGlobals.EXTRA_FAVORITE_ID);

			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressFragment.onActivityResult : REQUEST_ADD_FAVORITE_ADDRESS : favoriteData : " + favoriteData);
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressFragment.onActivityResult : REQUEST_ADD_FAVORITE_ADDRESS : id : " + id);

			if (favoriteData != null) {
				int position = getFavoriteListPositionIfContains(favoriteData);
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressFragment.onActivityResult : REQUEST_ADD_FAVORITE_ADDRESS : position : " + position);
				if (position == -1)  {
					if (id != null) {
						favoriteData.setId(id);
					}
					notifyAdapter(favoriteData);
				} else {
					if (id != null) {
						favoriteData.setId(id);
					}
					this.favoriteAddresses.set(position, favoriteData);
					adapter.notifyItemChanged(position);
				}
			}
		}
	}

	/**
	 * Method for getting favorite list position
	 *
	 * @param getFavoriteData - get favorite data
	 * @return - position of favorite
     */
	public int getFavoriteListPositionIfContains (GetFavoriteData getFavoriteData) {
		for (int i = 0; i < this.favoriteAddresses.size(); i++) {
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressFragment.getFavoriteListPositionIfContains : id : " + getFavoriteData.getId());
			if (favoriteAddresses.get(i).getId().equals(getFavoriteData.getId()))
				return i;
		}
		return -1;

	} // end method getFavoriteListPositionIfContains

	/**
	 * Method for notifying adapter
	 *
	 * @param favoriteAddresses - list of favorite addresses
	 */
	public void notifyAdapter(List<GetFavoriteData> favoriteAddresses) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressFragment.notifyAdapter : favoriteAddresses : " + favoriteAddresses);
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressFragment.notifyAdapter : size : " + favoriteAddresses.size());

		this.favoriteAddresses = favoriteAddresses;

		if (adapter == null) {
			adapter = new FavoriteAddressRVAdapter(mContext, this.favoriteAddresses);
			adapter.setOnItemClickListener(mItemClickListener);
			adapter.setMode(Attributes.Mode.Single);
			recyclerViewFavoriteAddresses.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}


	} // end method notifyAdapter

	/**
	 * Method for notifying adapter
	 *
	 * @param favoriteAddress - favorite address parameter
	 */
	public void notifyAdapter(GetFavoriteData favoriteAddress) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressFragment.notifyAdapter");

		this.favoriteAddresses.add(favoriteAddress);

		if (adapter == null) {
			adapter = new FavoriteAddressRVAdapter(mContext, this.favoriteAddresses);
			adapter.setOnItemClickListener(mItemClickListener);
			adapter.setMode(Attributes.Mode.Single);
			recyclerViewFavoriteAddresses.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}

	} // end method notifyAdapter

	/**
	 * Method for removing favorite address
	 */
	public void removeFavoriteAddress() {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressFragment.removeFavoriteAddress");

		int position = adapter.getRemovedPosition();
		if (position != -1) {
			favoriteAddresses.remove(position);
			adapter.notifyItemRemoved(position);
			adapter.notifyItemRangeChanged(position, favoriteAddresses.size());
		}

	} // end method removeFavoriteAddress

}