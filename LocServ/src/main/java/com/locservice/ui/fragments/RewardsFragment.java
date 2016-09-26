package com.locservice.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.adapters.RewardsAdapter;
import com.locservice.api.entities.BonusInfo;
import com.locservice.api.entities.BonusInfoItem;
import com.locservice.api.entities.Rewards;
import com.locservice.api.manager.ClientManager;
import com.locservice.api.manager.EnterCouponManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.RegisterActivity;
import com.locservice.ui.controls.CustomEditTextView;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.utils.RewardsType;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;

@SuppressLint("InflateParams") public class RewardsFragment extends Fragment implements View.OnClickListener {

	private static final String TAG = RewardsFragment.class.getSimpleName();

	private ListView listViewRewards;
	private RewardsAdapter adapter;
	private List<Rewards> rewards = new ArrayList<>();
	private View rootView;
	private FragmentActivity mContext;
	private LinearLayout layoutTitle;
	private CustomEditTextView editTextSetPromoCode;
	private CustomTextView textViewHeaderTopRewardsSize;

	private BonusInfo mBonusInfo = new BonusInfo();
	public BonusInfo getmBonusInfo() {
		return mBonusInfo;
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (rootView != null)
            return rootView;
		rootView = inflater.inflate(R.layout.fragment_rewards, container, false);
		mContext = getActivity();

		// Checking is user registered
		FrameLayout layoutMainContent = (FrameLayout) rootView.findViewById(R.id.layoutMainContent);
		RelativeLayout layoutRegisterContent = (RelativeLayout) rootView.findViewById(R.id.layoutRegisterContent);
		String auth_token = (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), ""));
		if (!auth_token.equals("")) {
			layoutMainContent.setVisibility(View.VISIBLE);
			layoutRegisterContent.setVisibility(View.GONE);
		} else {
			layoutMainContent.setVisibility(View.GONE);
			layoutRegisterContent.setVisibility(View.VISIBLE);
			if (CMApplication.hasNavigationBar(mContext)) {
				layoutRegisterContent.setPadding(0, 0, 0, CMApplication.dpToPx(48));
			}
			RelativeLayout layoutGoToRegister = (RelativeLayout) rootView.findViewById(R.id.layoutGoToRegister);
			layoutGoToRegister.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivityForResult(new Intent(mContext, RegisterActivity.class), CMAppGlobals.REQUEST_REGISTER);
				}
			});
		}

		// Setting listeners
		setListeners();

		if (!auth_token.isEmpty()) {
			// Request get client info
			ClientManager clientManager = new ClientManager((ICallBack) mContext);
			clientManager.GetClientInfo();
		}

		return rootView;

	} // end method onCreateView

	/**
	 * Method for setting listeners
	 */
	public void setListeners() {

		layoutTitle = (LinearLayout) rootView.findViewById(R.id.layoutTitle);
		FrameLayout layoutBack = (FrameLayout) rootView.findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(this);

		listViewRewards = (ListView) rootView.findViewById(R.id.listViewRewards);
		// Setting list view header
		setListViewHeader(listViewRewards);
		// create adapter
		adapter = new RewardsAdapter(mContext, rewards);
		listViewRewards.setAdapter(adapter);

		listViewRewards.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (listIsAtTop()) {
							layoutTitle.setVisibility(View.VISIBLE);
						} else {
							layoutTitle.setVisibility(View.GONE);
						}
					}
				}, 50);
			}
		});

	} // end method setListeners

	/**
	 * Method for adding & notify rewards list
	 * @param bonusInfo - user bonus information
     */
	public void updateRewardsList(BonusInfo bonusInfo) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.addingRewards : bonusInfo : " + bonusInfo);
		mBonusInfo = bonusInfo;
		if(rewards != null) {
			rewards.clear();
		}
		// add first friend trip item
		if(bonusInfo != null) {
			// get items from bonus information
			if(bonusInfo.getFriendFirstTrip() != null
					&& bonusInfo.getFriendFirstTrip().getCanGetBonus() > 0) {
				// friend trip bonus
				addFirstFriendTrip(bonusInfo.getFriendFirstTrip());
			}
//				|| bonusInfo.getShareOk().getCanGetBonus() > 0
			if((bonusInfo.getShareFb() != null && bonusInfo.getShareFb().getCanGetBonus() > 0)
				|| (bonusInfo.getShareVk() != null && bonusInfo.getShareVk().getCanGetBonus() > 0)) {
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.updateRewardsList : bonusInfo.getShareFb().getCanGetBonus() : " + bonusInfo.getShareFb().getCanGetBonus());
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.updateRewardsList : bonusInfo.getShareVk().getCanGetBonus() : " + bonusInfo.getShareVk().getCanGetBonus());
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.updateRewardsList : bonusInfo.getShareOk().getCanGetBonus() : " + bonusInfo.getShareOk().getCanGetBonus());

				Rewards rewardSharePost = new Rewards();
				rewardSharePost.setRewardsSize(bonusInfo.getShareFb().getValueTitle());
				rewardSharePost.setInfoText(bonusInfo.getShareFb().getText());
				rewardSharePost.setButtonText(mContext.getResources().getString(R.string.str_send_post));
				rewardSharePost.setRewardsType(RewardsType.SHARE);
				rewards.add(rewardSharePost);
			}
			if(bonusInfo.getCardBinding() != null
					&& bonusInfo.getCardBinding().getCanGetBonus() > 0) {
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.updateRewardsList : bonusInfo.getCardBinding().getValueTitle() : " + bonusInfo.getCardBinding().getValueTitle());
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.updateRewardsList : bonusInfo.getCardBinding().getText() : " + bonusInfo.getCardBinding().getText());
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.updateRewardsList : bonusInfo.getCardBinding().getValueDescription() : " + bonusInfo.getCardBinding().getValueDescription());

				Rewards rewardCardBonus = new Rewards();
				rewardCardBonus.setRewardsSize(bonusInfo.getCardBinding().getValueTitle());
				rewardCardBonus.setInfoText(bonusInfo.getCardBinding().getText());
				rewardCardBonus.setButtonText(mContext.getResources().getString(R.string.str_bind_card));
				rewardCardBonus.setRewardsType(RewardsType.BIND_CARD);
				rewards.add(rewardCardBonus);
			}
		}
		// add every trip
		addEveryTrip();

	} // end method updateRewardsList

	/**
	 * Method for adding friend trip bonus
	 * @param friendFirstTrip - friend trip bonus item
	 */
	public void addFirstFriendTrip(BonusInfoItem friendFirstTrip) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.addFirstFriendTrip : friendFirstTrip.getValueTitle() : " + friendFirstTrip.getValueTitle());
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.addFirstFriendTrip : friendFirstTrip.getText() : " + friendFirstTrip.getText());
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.addFirstFriendTrip : friendFirstTrip.getValueDescription() : " + friendFirstTrip.getValueDescription());
		Rewards reward = new Rewards();
		reward.setRewardsSize(friendFirstTrip.getValueTitle());
		reward.setInfoText(friendFirstTrip.getText());
		String referral = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_REFERAL.key(), "");
		reward.setReferralText(referral);
		reward.setButtonText(mContext.getResources().getString(R.string.str_send_to_friend));
		reward.setRewardsType(RewardsType.FIRST_TRIP);
		rewards.add(reward);

	} // end method addFirstFriendTrip

	/**
	 * Method for adding every trip
	 */
	public void addEveryTrip() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.addEveryTrip ");
		int rewardSize = 10;
		Rewards reward = new Rewards();
		reward.setRewardsSize("+" + rewardSize + "%");
		reward.setInfoText(mContext.getResources().getString(R.string.str_order_by_app)
				+ " "
				+ rewardSize
				+ " "
				+ mContext.getResources().getString(R.string.str_order_by_app_2));
		reward.setButtonText(mContext.getResources().getString(R.string.str_order_taxi));
		reward.setSubInfo(mContext.getResources().getString(R.string.str_cost_trip));
		reward.setRewardsType(RewardsType.EVERY_TRIP);
		rewards.add(reward);

	} // end method addEveryTrip
	
	@Override
	public void onClick(View v) {
		// check network status
		if (Utils.checkNetworkStatus(mContext)) {
			switch (v.getId()) {
			case R.id.layoutBack :
				if (mContext != null) {
					mContext.onBackPressed();
				}
				break;
			}
		}

	} // end method onClick

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CMAppGlobals.REQUEST_REGISTER && resultCode == Activity.RESULT_OK) {
			mContext.setResult(Activity.RESULT_OK);
			mContext.onBackPressed();
		}

	} // end method onActivityResult

	/**
	 * Method for checking is list at top
	 * @return - if at top true else false
     */
	private boolean listIsAtTop() {
		return listViewRewards.getChildCount() == 0 || listViewRewards.getChildAt(0).getTop() == 0;

	} // end method listIsAtTop

	/**
	 * Method for setting list view header
	 * @param listView - list view
     */
	public void setListViewHeader(ListView listView) {
		View headerView = mContext.getLayoutInflater().inflate(R.layout.rewards_list_header, null);
		editTextSetPromoCode = (CustomEditTextView)headerView.findViewById(R.id.editTextSetPromoCode);
		editTextSetPromoCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (CMAppGlobals.DEBUG)
					Logger.i(TAG, ":: RewardsFragment : textViewSetPromoCode : onEditorAction : actionId : " + actionId);
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (CMAppGlobals.DEBUG)
						Logger.i(TAG, ":: RewardsFragment : textViewSetPromoCode : onEditorAction : IME_ACTION_DONE");

					String promoCode = editTextSetPromoCode.getText().toString();
					if (isNotEmptyString(promoCode)) {
						// Request enter coupon
						EnterCouponManager couponManager = new EnterCouponManager((ICallBack) mContext);
						couponManager.EnterCouponRequest(0, promoCode);
					} else {
						Toast.makeText(mContext, R.string.alert_empty_promocode, Toast.LENGTH_SHORT).show();
					}
					return true;
				}
				return false;
			}
		});

		textViewHeaderTopRewardsSize = (CustomTextView) headerView.findViewById(R.id.textViewHeaderTopRewardsSize);
		String rewards = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BONUS.key(), "");
		if (!rewards.isEmpty()) {
			textViewHeaderTopRewardsSize.setText(rewards);
		}
		
		listView.addHeaderView(headerView);

	} // end method setListViewHeader

	/**
	 * Method for checking is string not empty
	 * @param str - string text
	 * @return - return true - if isn't empty string
	 */
	public boolean isNotEmptyString(String str) {
		str = str.trim();
		return !str.isEmpty();

	} // end method isNotEmptyString

	/**
	 * Method for closing keyboard
	 */
	public void closeKeyboard() {
		View view = mContext.getCurrentFocus();
		if (view != null) {
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: view != null");
			InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

	} // end method closeKeyboard

	/**
	 * Method for getting textViewHeaderTopRewardsSize
	 * @return textViewHeaderTopRewardsSize
	 */
	public CustomTextView getTextViewHeaderTopRewardsSize() {
		return textViewHeaderTopRewardsSize;

	} // end method getTextViewHeaderTopRewardsSize

	/**
	 * Method for notify adapter
	 */
	public void notifyAdapter() {
		String strRewards = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BONUS.key(), "");
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.notifyAdapter : PROFILE_BONUS : " + strRewards);
		if (!strRewards.isEmpty()) {
			textViewHeaderTopRewardsSize.setText(strRewards);
		}

		adapter.notifyDataSetChanged();

	} // end method notifyAdapter

	/**
	 * Method for setting social items
	 * @param social_id - current social
	 * @param can_repeat - can social network sharing
	 * @param current_bonus - current bonus
     */
	public void setSocialItems(int social_id, int can_repeat, int current_bonus) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsFragment.setSocialItems : social_id : "
				+ social_id + " : can_repeat : " + can_repeat + " : current_bonus : " + current_bonus);

		// vk
		if(social_id == 1) {
			if(can_repeat > 1) {
				mBonusInfo.getShareVk().setCanGetBonus(1);
			} else {
				mBonusInfo.getShareVk().setCanGetBonus(0);
			}
		}
		// fb
		else if(social_id == 2) {
			if(can_repeat > 1) {
				mBonusInfo.getShareFb().setCanGetBonus(1);
			} else {
				mBonusInfo.getShareFb().setCanGetBonus(0);
			}
		}
		// ok
		else if(social_id == 3) {
			if(can_repeat > 1) {
				mBonusInfo.getShareOk().setCanGetBonus(1);
			} else {
				mBonusInfo.getShareOk().setCanGetBonus(0);
			}
		}

		// set current bonus header text
		String bonus = String.valueOf(current_bonus);
		LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_BONUS, bonus);
		textViewHeaderTopRewardsSize.setText(bonus);

		// REFRESH BONUS LIST
		updateRewardsList(mBonusInfo);
		// NOTIFY ADAPTER
		notifyAdapter();

	} // end method setSocialItems

}
