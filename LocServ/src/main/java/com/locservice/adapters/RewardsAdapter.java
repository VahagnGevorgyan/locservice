package com.locservice.adapters;

import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.entities.Rewards;
import com.locservice.application.LocServicePreferences;
import com.locservice.social.FBHelper;
import com.locservice.social.VKHelper;
import com.locservice.ui.AddCreditCardsActivity;
import com.locservice.ui.RewardsActivity;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.fragments.RewardsFragment;
import com.locservice.ui.utils.RewardsType;
import com.locservice.utils.Logger;
import com.vk.sdk.VKSdk;

/**
 * Created by Vahagn Gevorgyan
 * 29 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class RewardsAdapter extends ArrayAdapter<Rewards> {
	private static final String TAG = RewardsAdapter.class.getSimpleName();
	private Activity mContext;
	private List<Rewards> rewards;

	public RewardsAdapter(Activity context, List<Rewards> rewards) {
		super(context, R.layout.list_item_rewards, rewards);
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsAdapter()");
		this.mContext = context;
		this.rewards = rewards;
	}

	/**
	 * Class list view holder
	 */
	class ViewHolder {
		CustomTextView textViewRewardsSize;
		CustomTextView textViewSubInfo;
		CustomTextView textViewRewardsInfo;
		LinearLayout layoutReferralContainer;
		CustomTextView textViewReferralText;
		CustomTextView textViewButton;
		LinearLayout layoutListItem;
		LinearLayout layoutForBottomBackground;
		LinearLayout layoutRewardsSize;

		ViewHolder(View view) {
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ViewHolder ");
			textViewRewardsSize = (CustomTextView) view.findViewById(R.id.textViewRewardsSize);
			textViewSubInfo = (CustomTextView) view.findViewById(R.id.textViewSubInfo);
			textViewRewardsInfo = (CustomTextView) view.findViewById(R.id.textViewRewardsInfo);
			layoutReferralContainer = (LinearLayout) view.findViewById(R.id.layoutReferralContainer);
			textViewReferralText = (CustomTextView) view.findViewById(R.id.textViewReferralText);
			textViewButton = (CustomTextView) view.findViewById(R.id.textViewButton);
			layoutListItem = (LinearLayout) view.findViewById(R.id.layoutListItem);
			layoutForBottomBackground = (LinearLayout) view.findViewById(R.id.layoutForBottomBackground);
			layoutRewardsSize = (LinearLayout) view.findViewById(R.id.layoutRewardsSize);
		}

	} // end class ViewHolder

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		RewardsAdapter.ViewHolder holder = null;

		if (convertView == null) {
			LayoutInflater inflater = mContext.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_item_rewards, parent, false);
			holder = new RewardsAdapter.ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (RewardsAdapter.ViewHolder) convertView.getTag();
		}
		final Rewards reward = rewards.get(position);
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsAdapter.getView : reward : " + reward);

		holder.textViewRewardsSize.setText(reward.getRewardsSize());
		holder.textViewRewardsInfo.setText(reward.getInfoText());

		final RewardsType rewardsType = reward.getRewardsType();
		switch (rewardsType) {
			case FIRST_TRIP:
				holder.textViewButton.setVisibility(View.VISIBLE);
				holder.textViewButton.setText(reward.getButtonText());
				holder.layoutReferralContainer.setVisibility(View.VISIBLE);
				holder.textViewReferralText.setText(reward.getReferralText());
				holder.textViewSubInfo.setVisibility(View.GONE);
				holder.textViewRewardsSize.setTextColor(Color.parseColor("#B62D2D"));
				break;
			case SHARE:
				holder.textViewButton.setVisibility(View.VISIBLE);
				holder.textViewRewardsInfo.setVisibility(View.VISIBLE);
				holder.layoutRewardsSize.setVisibility(View.VISIBLE);
				holder.textViewButton.setText(reward.getButtonText());
				holder.layoutReferralContainer.setVisibility(View.GONE);
				holder.textViewSubInfo.setVisibility(View.GONE);
				holder.textViewRewardsSize.setTextColor(Color.parseColor("#F79325"));
				break;
			case BIND_CARD:
				int hasCards = LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.PROFILE_HAS_CARDS.key(), 0);
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsAdapter.getView : BIND_CARD : hasCards : " + hasCards);
				holder.textViewButton.setVisibility(View.VISIBLE);
				holder.textViewRewardsInfo.setVisibility(View.VISIBLE);
				holder.layoutRewardsSize.setVisibility(View.VISIBLE);

				holder.textViewButton.setText(reward.getButtonText());
				holder.layoutReferralContainer.setVisibility(View.GONE);
				holder.textViewReferralText.setText(reward.getReferralText());
				holder.textViewSubInfo.setVisibility(View.GONE);

				holder.textViewRewardsSize.setTextColor(Color.parseColor("#FDB933"));
				break;
			case EVERY_TRIP:
				holder.textViewRewardsInfo.setVisibility(View.VISIBLE);
				holder.layoutRewardsSize.setVisibility(View.VISIBLE);

				holder.textViewButton.setVisibility(View.GONE);
				holder.textViewButton.setText(reward.getButtonText());
				holder.layoutReferralContainer.setVisibility(View.GONE);
				holder.textViewReferralText.setText(reward.getReferralText());
				holder.textViewSubInfo.setVisibility(View.VISIBLE);
				holder.textViewSubInfo.setText(reward.getSubInfo());

				holder.textViewRewardsSize.setTextColor(Color.parseColor("#47B784"));
				break;

		}

		holder.textViewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonsOnClick(reward);
			}
		});


		return convertView;

	} // end method getView

	/**
	 * Method for getting buttons on click events
	 * @param reward - reward
     */
	public void buttonsOnClick(Rewards reward) {
		RewardsType rewardsType = reward.getRewardsType();
		switch (rewardsType) {
			case FIRST_TRIP:
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.setType("text/plain");
				String sendingText = mContext.getResources().getString(R.string.str_referral_share_text) + " " + reward.getReferralText();
				sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, sendingText);
				mContext.startActivity(Intent.createChooser(sendIntent, mContext.getResources().getString(R.string.str_send_to)));
				break;
			case SHARE:
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsAdapter.buttonsOnClick : SHARE ");
				if(((RewardsFragment)((RewardsActivity)mContext).getCurrentFragment()).getmBonusInfo().getShareFb().getCanGetBonus() > 0
						|| ((RewardsFragment)((RewardsActivity)mContext).getCurrentFragment()).getmBonusInfo().getShareVk().getCanGetBonus() > 0) {

					AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
					View view = LayoutInflater.from(mContext).inflate(R.layout.layout_social_dialog, null);
					builder.setView(view);
					builder.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					final AlertDialog alertDialog = builder.create();

					TextView textViewFb = (TextView) view.findViewById(R.id.textViewFB);
					if(((RewardsFragment)((RewardsActivity)mContext).getCurrentFragment()).getmBonusInfo().getShareFb().getCanGetBonus() > 0) {
						textViewFb.setVisibility(View.VISIBLE);
						// show facebook share button listener
						textViewFb.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								alertDialog.dismiss();
								FBHelper.showShareDialog(mContext,
										((RewardsActivity)mContext).getCallbackManager(),
										FBHelper.getShareLinkContent(mContext),
										(FBHelper.FBShareCallback) mContext
								);
							}
						});
					} else {
						// hide facebook share icon
						textViewFb.setVisibility(View.GONE);
					}

					TextView textViewVK = (TextView) view.findViewById(R.id.textViewVK);
					if(((RewardsFragment)((RewardsActivity)mContext).getCurrentFragment()).getmBonusInfo().getShareVk().getCanGetBonus() > 0) {
						textViewVK.setVisibility(View.VISIBLE);
						// show vk share button listener
						textViewVK.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								alertDialog.dismiss();
								String vkID = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_VK_ID.key(), "");
								if (vkID.isEmpty()) {
									VKSdk.login(mContext, VKHelper.vkScopesShare);
								} else {
									((RewardsActivity)mContext).shareVKWithDialog(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher),
											((RewardsActivity)mContext).getSupportFragmentManager(),
											mContext);
								}
							}
						});
					} else {
						// hide vk share icon
						textViewVK.setVisibility(View.GONE);
					}
					alertDialog.show();
				}
				break;
			case BIND_CARD:
				Intent intent = new Intent(mContext, AddCreditCardsActivity.class);
				mContext.startActivityForResult(intent, CMAppGlobals.REQUEST_CARD_ADD);
				break;
			case EVERY_TRIP:
				break;
			default:
				break;
		}

	} // end method buttonsOnClick

}
