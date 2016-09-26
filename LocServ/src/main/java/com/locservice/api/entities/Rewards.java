package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.locservice.ui.utils.RewardsType;

public class Rewards implements Parcelable {
	
	private String rewardsSize;
	private String subInfo;
	private String buttonText;
	private String infoText;
	private String referralText;
	private RewardsType rewardsType;

	public Rewards() {

	}

	protected Rewards(Parcel in) {
		rewardsSize = in.readString();
		subInfo = in.readString();
		buttonText = in.readString();
		infoText = in.readString();
		referralText = in.readString();
		rewardsType = in.readParcelable(RewardsType.class.getClassLoader());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(rewardsSize);
		dest.writeString(subInfo);
		dest.writeString(buttonText);
		dest.writeString(infoText);
		dest.writeString(referralText);
		dest.writeParcelable(rewardsType, flags);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<Rewards> CREATOR = new Creator<Rewards>() {
		@Override
		public Rewards createFromParcel(Parcel in) {
			return new Rewards(in);
		}

		@Override
		public Rewards[] newArray(int size) {
			return new Rewards[size];
		}
	};

	public String getRewardsSize() {
		return rewardsSize;
	}

	public void setRewardsSize(String rewardsSize) {
		this.rewardsSize = rewardsSize;
	}

	public String getSubInfo() {
		return subInfo;
	}

	public void setSubInfo(String subInfo) {
		this.subInfo = subInfo;
	}

	public String getButtonText() {
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public String getInfoText() {
		return infoText;
	}

	public void setInfoText(String infoText) {
		this.infoText = infoText;
	}

	public String getReferralText() {
		return referralText;
	}

	public void setReferralText(String referralText) {
		this.referralText = referralText;
	}

	public RewardsType getRewardsType() {
		return rewardsType;
	}

	public void setRewardsType(RewardsType rewardsType) {
		this.rewardsType = rewardsType;
	}
}
