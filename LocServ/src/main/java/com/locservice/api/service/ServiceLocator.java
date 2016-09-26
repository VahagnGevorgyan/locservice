package com.locservice.api.service;

import com.locservice.api.models.AddressModel;
import com.locservice.api.models.BonusModel;
import com.locservice.api.models.ChatModel;
import com.locservice.api.models.ClientModel;
import com.locservice.api.models.CreditCardModel;
import com.locservice.api.models.DriverModel;
import com.locservice.api.models.EnterCouponModel;
import com.locservice.api.models.FavoriteAddressModel;
import com.locservice.api.models.GoogleModel;
import com.locservice.api.models.LandmarkModel;
import com.locservice.api.models.OrderModel;
import com.locservice.api.models.PlaceModel;
import com.locservice.api.models.RegisterModel;
import com.locservice.api.models.StatisticsModel;
import com.locservice.api.models.TariffModel;


/**
 * Created by Vahagn Gevorgyan
 * 18 November 2015
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ServiceLocator {

	// TARIFF
	private static TariffModel mTariffModel = null;
	public static TariffModel getTariffModel() {
		if(mTariffModel == null)mTariffModel = new TariffModel();
		return mTariffModel;
	}

	// PLACE
	private static PlaceModel mPlaceModel = null;
	public static PlaceModel getPlaceModel() {
		if(mPlaceModel == null)mPlaceModel = new PlaceModel();
		return mPlaceModel;
	}

	// REGISTER
	private static RegisterModel mRegisterModel = null;
	public static RegisterModel getRegisterModel() {
		if (mRegisterModel == null) mRegisterModel = new RegisterModel();
		return mRegisterModel;
	}

	// ORDER
	private static OrderModel mOrderModel = null;
	public static OrderModel getOrderModel() {
		if (mOrderModel == null) mOrderModel = new OrderModel();
		return mOrderModel;
	}

	//Driver
	private static DriverModel mDriverModel = null;
	public static DriverModel getDriverModel() {
		if (mDriverModel == null) mDriverModel = new DriverModel();
		return mDriverModel;
	}

	//Client
	private static ClientModel mClientModel = null;
	public static ClientModel getClientModel() {
		if (mClientModel == null) mClientModel = new ClientModel();
		return mClientModel;
	}

	//Google
	private static GoogleModel mGoogleModel = null;
	public static GoogleModel getGoogleModel() {
		if (mGoogleModel == null) mGoogleModel = new GoogleModel();
		return mGoogleModel;
	}

	// Statistics
	private static StatisticsModel mStatisticsModel = null;
	public static StatisticsModel getStatisticsModel() {
		if (mStatisticsModel == null) mStatisticsModel = new StatisticsModel();
		return mStatisticsModel;
	}

	// Chat
	private static ChatModel mChatModel = null;
	public static ChatModel getChatModel() {
		if (mChatModel == null) mChatModel = new ChatModel();
		return mChatModel;
	}

	// Enter Coupon
	private static EnterCouponModel mEnterCouponModel = null;
	public static EnterCouponModel getEnterCouponModel() {
		if (mEnterCouponModel == null) mEnterCouponModel = new EnterCouponModel();
		return mEnterCouponModel;
	}

	// LANDMARK
	private static LandmarkModel mLandmarkModel = null;
	public static LandmarkModel getLandmarkModel() {
		if(mLandmarkModel == null)mLandmarkModel = new LandmarkModel();
		return mLandmarkModel;
	}

	// FAVORITE ADDRESS
	private static FavoriteAddressModel mFavoriteAddressModel = null;
	public static FavoriteAddressModel getFavoriteAddressModel() {
		if(mFavoriteAddressModel == null)mFavoriteAddressModel = new FavoriteAddressModel();
		return mFavoriteAddressModel;
	}

	// CREDIT CARD
	private static CreditCardModel mCreditCardModel = null;
	public static CreditCardModel getCreditCardModel() {
		if(mCreditCardModel == null)mCreditCardModel = new CreditCardModel();
		return mCreditCardModel;
	}

	// ADDRESS
	private static AddressModel mAddressModel = null;
	public static AddressModel getAddressModel() {
		if(mAddressModel == null)mAddressModel = new AddressModel();
		return mAddressModel;
	}

	// BONUS
	private static BonusModel mBonusModel = null;
	public static BonusModel getBonusModel() {
		if(mBonusModel == null)mBonusModel = new BonusModel();
		return mBonusModel;
	}

}
