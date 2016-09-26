package com.locservice.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.OrderStatusAdditionalField;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.entities.OrderStatusService;
import com.locservice.api.entities.TariffService;
import com.locservice.api.manager.OrderManager;
import com.locservice.db.LanguageDBManager;
import com.locservice.db.TariffServiceDBManager;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.ChatActivity;
import com.locservice.ui.MainActivity;
import com.locservice.ui.controls.AutoResizeTextView;
import com.locservice.ui.controls.CustomEditTextView;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.helpers.MenuHelper;
import com.locservice.ui.helpers.OrderState;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;

import java.util.List;

public class OrderRateFragment extends Fragment implements View.OnClickListener {
	static final String TAG = OrderRateFragment.class.getSimpleName();
	
	private RatingBar ratingBar;
	private RatingBar ratingBarFeedback;
	private CustomTextView textViewArrivedVote;
	private CustomTextView textViewArrivedAnotherBonus;
	private CustomTextView textViewOk;
	private FrameLayout layoutClose;
	private LinearLayout layoutFeedback;
	private RelativeLayout layoutRateContent;
	private CustomEditTextView editTextViewFeedbackContent;
	private LinearLayout layoutTripContent;
	private LinearLayout layoutCounter;
	private RelativeLayout layoutRatePart;
	private AutoResizeTextView textViewArrived;
	private CustomTextView textViewArrivedInfo;
	private CustomTextView textViewScore;
	private CustomTextView textViewConnectToSupport;
	private boolean isCloseFeedback;
	private float rate = 0;
	private CustomTextView textViewArrivedThanks;
	private CustomTextView textViewSendFeedback;
	private ImageView imageViewArrow;

	private ImageView imageViewTopMenu;

	private View rootView;

	private FragmentActivity mContext;
	private OrderManager orderManager;
	private CustomTextView textViewArrivedCounter;
	private CustomTextView textViewArrivedBonusCount;
	private String orderId;
	private String feedbackText;
	private boolean isOpenTrip = false;
	private CustomTextView textViewSetFeedback;
	private LinearLayout layoutArrivedBonusCount;
	private TextView textViewArrivedBonus;
	private OrderState orderState;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (rootView != null)
            return rootView;
		rootView = inflater.inflate(R.layout.fragment_trip_arrived, container, false);
		mContext = getActivity();
		
		ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBarArrived);
		ratingBarFeedback = (RatingBar) rootView.findViewById(R.id.ratingBarFeedback);
		textViewArrivedVote = (CustomTextView) rootView.findViewById(R.id.textViewArrivedVote);
		textViewArrivedAnotherBonus = (CustomTextView) rootView.findViewById(R.id.textViewArrivedAnotherBonus);
		textViewOk = (CustomTextView) rootView.findViewById(R.id.textViewOk);
		layoutClose = (FrameLayout) rootView.findViewById(R.id.layoutClose);
		layoutFeedback = (LinearLayout) rootView.findViewById(R.id.layoutFeedback);
		textViewSetFeedback = (CustomTextView) rootView.findViewById(R.id.textViewSetFeedback);
		layoutRateContent = (RelativeLayout) rootView.findViewById(R.id.layoutRateContent);
		editTextViewFeedbackContent = (CustomEditTextView) rootView.findViewById(R.id.editTextViewFeedbackContent);
		layoutTripContent = (LinearLayout)rootView.findViewById(R.id.layoutTripContent);
		layoutCounter = (LinearLayout) rootView.findViewById(R.id.layoutCounter);
		layoutRatePart = (RelativeLayout) rootView.findViewById(R.id.layoutRatePart);
		textViewArrived = (AutoResizeTextView) rootView.findViewById(R.id.textViewArrived);
		int minTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 24, getResources().getDisplayMetrics());
		textViewArrived.setMinTextSize(minTextSize);
		textViewArrivedCounter = (CustomTextView) rootView.findViewById(R.id.textViewArrivedCounter);
		textViewArrivedBonusCount = (CustomTextView) rootView.findViewById(R.id.textViewArrivedBonusCount);
		textViewArrivedInfo = (CustomTextView) rootView.findViewById(R.id.textViewArrivedInfo);
		textViewScore = (CustomTextView) rootView.findViewById(R.id.textViewScore);
		textViewArrivedThanks = (CustomTextView) rootView.findViewById(R.id.textViewArrivedThanks);
		textViewSendFeedback = (CustomTextView) rootView.findViewById(R.id.textViewSendFeedback);
		textViewConnectToSupport = (CustomTextView) rootView.findViewById(R.id.textViewConnectToSupport);
		textViewConnectToSupport.setOnClickListener(this);
		imageViewArrow = (ImageView) rootView.findViewById(R.id.imageViewArrow);

		orderManager = new OrderManager((ICallBack) mContext);

		if (mContext.getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_ID) != null) {
			orderId = mContext.getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_ID);
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderRateFragment.onCreateView : orderID : " + orderId);
		}
		// Request for getting order status list
		orderManager.GetOrderStatusList(orderId);
		if (mContext.getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_PRICE) != null) {
			String price = mContext.getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_PRICE);
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderRateFragment.onCreateView : order_price : " + price);
			textViewArrivedCounter.setText(price);
		}
		if (mContext.getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_BONUS) != null) {
			String bonus = mContext.getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_BONUS);
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderRateFragment.onCreateView : bonus : " + bonus);
			textViewArrivedBonusCount.setText(bonus);
		}
		if (mContext.getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_TRIP_TIME) != null) {
			String trip_time = mContext.getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_TRIP_TIME);
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderRateFragment.onCreateView : trip_time : " + trip_time);
		}
		if (mContext.getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_TRIP_TITLE) != null) {
			String title = mContext.getIntent().getStringExtra(CMAppGlobals.EXTRA_ORDER_TRIP_TITLE);
			textViewArrived.setText(title);
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderRateFragment.onCreateView : title : " + title);
		}
		if (mContext.getIntent().getSerializableExtra(CMAppGlobals.EXTRA_ORDER_STATUS) != null) {
			orderState = (OrderState) mContext.getIntent().getSerializableExtra(CMAppGlobals.EXTRA_ORDER_STATUS);
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderRateFragment.onCreateView : orderState : " + orderState);
			if (orderState == OrderState.CC || orderState == OrderState.NC || orderState == OrderState.AR) {
				hideOrderInfo(true);
				if (orderState == OrderState.AR)
					textViewArrivedInfo.setVisibility(View.VISIBLE);
			}
		}

		imageViewTopMenu = (ImageView) rootView.findViewById(R.id.imageViewTopMenu);
		imageViewTopMenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editTextViewFeedbackContent.getWindowToken(), 0);
				// OPEN MENU DRAWER
				MenuHelper.getInstance().openDrawer(mContext, Gravity.LEFT);
			}
		});
		
        layoutClose.setOnClickListener(this);
		textViewOk.setOnClickListener(this);
		textViewArrivedVote.setOnClickListener(this);
		layoutCounter.setOnClickListener(this);
		textViewSendFeedback.setOnClickListener(this);
		
		ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            	rate = rating;
            	ratingBar.setRating(rating);
            	ratingBarFeedback.setRating(rating);
            	textViewArrivedAnotherBonus.setText(R.string.str_want_more);
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderRateFragment : RATING_BAR : rate : " + rate);
				switch ((int) rate) {
					case 0:
						textViewArrivedVote.setText(R.string.str_arrived_vote);
						break;
					case 1:
					case 2:
					case 3:
						textViewArrivedVote.setText(R.string.str_send_feedback_3);
						textViewSetFeedback.setText(R.string.str_send_feedback_3);
						break;
					case 4:
						textViewArrivedVote.setText(R.string.str_send_feedback_4);
						textViewSetFeedback.setText(R.string.str_send_feedback_4);
						break;
					case 5:
						textViewArrivedVote.setText(R.string.str_send_feedback_5);
						textViewSetFeedback.setText(R.string.str_send_feedback_5);
						break;
				}
			}
        });
		
		return rootView;
	}
	
	@Override
	public void onClick(View v) {
		// check network status
		if (Utils.checkNetworkStatus(mContext)) {

			switch (v.getId()) {
			case R.id.layoutClose :
				if (isCloseFeedback) {
					ratingBarFeedback.setVisibility(View.INVISIBLE);
					layoutClose.setVisibility(View.INVISIBLE);
					layoutFeedback.setVisibility(View.GONE);
					layoutRateContent.setVisibility(View.VISIBLE);
					InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(editTextViewFeedbackContent.getWindowToken(), 0);
				} else {
	//				layoutCounter.setEnabled(true);
					layoutTripContent.setVisibility(View.GONE);
					layoutRatePart.setVisibility(View.VISIBLE);
					layoutClose.setVisibility(View.GONE);
					textViewArrived.setVisibility(View.VISIBLE);
					textViewArrivedInfo.setVisibility(View.VISIBLE);
					textViewScore.setVisibility(View.GONE);
//					textViewArrivedThanks.setText(R.string.test_arrived_thanks);
					imageViewArrow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_down));
					isOpenTrip = false;
				}
				break;
			case R.id.textViewOk :
				if (orderState != null && orderState == OrderState.AR) {
					int idCreditCard = mContext.getIntent().getIntExtra(CMAppGlobals.EXTRA_CARD_ID_HASH, 0);
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderRateFragment.onCreateView : idCreditCard : " + idCreditCard);

					if (orderId != null && !orderId.isEmpty()) {
						if (idCreditCard == 0) {
							orderManager.GetOrdersInfo("", orderId, RequestTypes.REQUEST_GET_ORDERS_INFO);
						} else {
							orderManager.PayOrder(orderId, idCreditCard);
						}
					}
				} else {
					if (orderId != null && !orderId.isEmpty() && ratingBar.getRating() != 0) {
						orderManager.SetOrderRating(orderId, (int) ratingBar.getRating(), feedbackText);
					} else {
						Intent intent = new Intent(mContext, MainActivity.class);
						mContext.finish();
						startActivity(intent);
					}
				}
				break;
			case R.id.textViewArrivedVote :
				showFeedbackContent();
				break;
			case R.id.layoutCounter :
				if (isOpenTrip) {
					isOpenTrip = false;
	//				layoutCounter.setEnabled(true);
					layoutTripContent.setVisibility(View.GONE);
					layoutRatePart.setVisibility(View.VISIBLE);
					layoutClose.setVisibility(View.GONE);
					textViewArrived.setVisibility(View.VISIBLE);
					textViewArrivedInfo.setVisibility(View.VISIBLE);
					textViewScore.setVisibility(View.GONE);
//					textViewArrivedThanks.setText(R.string.test_arrived_thanks);
					imageViewArrow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_down));

				} else {
					isOpenTrip = true;
					isCloseFeedback = false;
	//				layoutCounter.setEnabled(false);
					layoutTripContent.setVisibility(View.VISIBLE);
					layoutRatePart.setVisibility(View.GONE);
					layoutClose.setVisibility(View.VISIBLE);
					textViewArrived.setVisibility(View.GONE);
					textViewArrivedInfo.setVisibility(View.GONE);
					textViewScore.setVisibility(View.VISIBLE);
//					textViewArrivedThanks.setText(R.string.test_pay_by_cash);
					imageViewArrow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_up));
				}

				break;
			case R.id.textViewSendFeedback :
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderRateFragment.onClick : textViewSendFeedback");

				feedbackText = editTextViewFeedbackContent.getText().toString();
				feedbackText.trim();
				if (!feedbackText.isEmpty()) {

					hideFeedbackContent();
				} else {
					Toast.makeText(mContext, R.string.alert_empty_order_comment, Toast.LENGTH_SHORT).show();
				}

				break;
				case R.id.textViewConnectToSupport:
					startActivity(new Intent(mContext, ChatActivity.class));
					break;
			}
		}

	}

	/**
	 * Method for closing feedback content
	 */
	public void hideFeedbackContent () {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderRateFragment.closeFeedbackContent");

		ratingBarFeedback.setVisibility(View.INVISIBLE);
		layoutClose.setVisibility(View.INVISIBLE);
		layoutFeedback.setVisibility(View.GONE);
		layoutRateContent.setVisibility(View.VISIBLE);
		textViewArrivedVote.setVisibility(View.INVISIBLE);
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editTextViewFeedbackContent.getWindowToken(), 0);

	} // end method closeFeedbackContent

	/**
	 * Method for showing feedback content
	 */
	public void showFeedbackContent () {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderRateFragment.showFeedbackContent");

		if (rate != 0) {
			isCloseFeedback = true;
			ratingBarFeedback.setVisibility(View.VISIBLE);
			layoutClose.setVisibility(View.VISIBLE);
			layoutFeedback.setVisibility(View.VISIBLE);
			layoutRateContent.setVisibility(View.GONE);
			editTextViewFeedbackContent.requestFocus();
			editTextViewFeedbackContent.setText("");
			InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.showSoftInput(editTextViewFeedbackContent, InputMethodManager.SHOW_IMPLICIT);
		}

	} // end method showFeedbackContent

	/**
	 * Method for hiding order info
	 *
	 * @param hide - if hide true else false
     */
	public void hideOrderInfo(boolean hide) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderRateFragment.hideOrderInfo : hide : " + hide);

		if (textViewArrivedInfo == null)
			textViewArrived = (AutoResizeTextView) rootView.findViewById(R.id.textViewArrived);
		if (textViewArrivedVote == null)
			textViewArrivedVote = (CustomTextView) rootView.findViewById(R.id.textViewArrivedVote);
		if (imageViewArrow == null)
			imageViewArrow = (ImageView) rootView.findViewById(R.id.imageViewArrow);
		if (ratingBar == null)
			ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBarArrived);
		if (layoutArrivedBonusCount == null)
			layoutArrivedBonusCount = (LinearLayout) rootView.findViewById(R.id.layoutArrivedBonusCount);
		if (textViewArrivedBonus == null)
			textViewArrivedBonus = (TextView) rootView.findViewById(R.id.textViewArrivedBonus);
		if (textViewArrivedThanks == null)
			textViewArrivedThanks = (CustomTextView) rootView.findViewById(R.id.textViewArrivedThanks);
		if (layoutCounter == null)
			layoutCounter = (LinearLayout) rootView.findViewById(R.id.layoutCounter);

		if (hide) {
			textViewArrivedInfo.setVisibility(View.INVISIBLE);
			textViewArrivedVote.setVisibility(View.INVISIBLE);
			imageViewArrow.setVisibility(View.GONE);
			ratingBar.setVisibility(View.GONE);
			layoutArrivedBonusCount.setVisibility(View.GONE);
			textViewArrivedBonus.setVisibility(View.GONE);
			textViewArrivedThanks.setVisibility(View.GONE);
			layoutCounter.setEnabled(false);
		} else {
			textViewArrivedInfo.setVisibility(View.VISIBLE);
			textViewArrivedVote.setVisibility(View.VISIBLE);
			imageViewArrow.setVisibility(View.VISIBLE);
			ratingBar.setVisibility(View.VISIBLE);
			layoutArrivedBonusCount.setVisibility(View.VISIBLE);
			textViewArrivedBonus.setVisibility(View.VISIBLE);
			textViewArrivedThanks.setVisibility(View.VISIBLE);
			layoutCounter.setEnabled(true);
		}

	} // end method hideOrderInfo


	/**
	 * Method for adding Additional Fields And Services
	 * @param orderStatusData - order status data
	 */
	public void addAdditionalFieldsAndServices (OrderStatusData orderStatusData) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderRateFragment.addAdditionalFieldsAndServices : orderStatusData : " + orderStatusData);

		if (isAdded()) {

			orderState = OrderState.getStateByStatus(orderStatusData.getStatus());

			if (orderStatusData.getStatus().equals("CC")
					|| orderStatusData.getStatus().equals("NC")
					|| orderStatusData.getStatus().equals("AR")) {
				TextView textViewArrived = (TextView) rootView.findViewById(R.id.textViewArrived);
				textViewArrived.setText(orderStatusData.getTitle());
				hideOrderInfo(true);
				if (orderStatusData.getStatus().equals("AR"))
					textViewArrivedInfo.setVisibility(View.VISIBLE);
			}

			if (orderStatusData.getStatus().equals("CP")) {
				textViewArrived.setText(getResources().getString(R.string.str_arrived));
			}

			TextView textViewArrivedCounter = (TextView) rootView.findViewById(R.id.textViewArrivedCounter);
			if (orderStatusData.getPrice() != null && !orderStatusData.getPrice().isEmpty())  {
				textViewArrivedCounter.setText(orderStatusData.getPrice());
			}

			TextView textViewArrivedInfo = (TextView) rootView.findViewById(R.id.textViewArrivedInfo);
			if (!orderStatusData.getSubTitle().isEmpty()) {
				textViewArrivedInfo.setText(Html.fromHtml(orderStatusData.getSubTitle()));
			}
			TextView textViewArrivedBonusCount = (TextView) rootView.findViewById(R.id.textViewArrivedBonusCount);
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.addAdditionalFieldsAndServices : bonus : " + orderStatusData.getBonus());
			if (orderStatusData.getBonus() != null && !orderStatusData.getBonus().isEmpty()) {
				String bonus = "+" + orderStatusData.getBonus();
				textViewArrivedBonusCount.setText(bonus);
			}

			List<OrderStatusAdditionalField> additionalFields = orderStatusData.getAdditionalFields();
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.addAdditionalFieldsAndServices : additionalFields : " + additionalFields);
			LinearLayout layoutAdditional = (LinearLayout) rootView.findViewById(R.id.layoutAdditionalFields);
			if (layoutAdditional.getChildCount() != 0) {
				layoutAdditional.removeAllViews();
			}
			if (additionalFields != null && !additionalFields.isEmpty()) {
				for (OrderStatusAdditionalField item : additionalFields) {
					View viewAdditional = LayoutInflater.from(mContext).inflate(R.layout.list_item_additional_fields, null);
					CustomTextView textViewAdditionalTitle = (CustomTextView) viewAdditional.findViewById(R.id.textViewAdditionalTitle);
					String title = item.getText();
					if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.addAdditionalFieldsAndServices : title : " + title);

					textViewAdditionalTitle.setText(Html.fromHtml(title));
					CustomTextView textViewAdditionalPrice = (CustomTextView) viewAdditional.findViewById(R.id.textViewAdditionalPrice);
					String priceStr = item.getPrice() + "";
					textViewAdditionalPrice.setText(priceStr);
					layoutAdditional.addView(viewAdditional);
				}
			}

			List<OrderStatusService> statusServices = orderStatusData.getServices();
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.addAdditionalFieldsAndServices : statusServices : " + statusServices);
			LinearLayout layoutServices = (LinearLayout) rootView.findViewById(R.id.layoutServices);
			if (layoutServices.getChildCount() != 0) {
				layoutServices.removeAllViews();
			}
			if (statusServices != null && !statusServices.isEmpty()) {
				for (OrderStatusService item : statusServices) {
					View viewService = LayoutInflater.from(mContext).inflate(R.layout.list_item_service, null);
					CustomTextView textViewServiceTitle = (CustomTextView) viewService.findViewById(R.id.textViewServiceTitle);

					int languageID = new LanguageDBManager(mContext).getLanguageIdByLocale("ru");
					TariffServiceDBManager tariffServiceDBManager = new TariffServiceDBManager(mContext);
					TariffService tariffService = tariffServiceDBManager.getServiceByField(item.getField(), languageID);
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.addAdditionalFieldsAndServices : tariff_service : " + tariffService.getShortTitle());
					textViewServiceTitle.setText(tariffService.getShortTitle());

					CustomTextView textViewServicePrice = (CustomTextView) viewService.findViewById(R.id.textViewServicePrice);
					String priceStr = item.getPrice() + "";
					textViewServicePrice.setText(priceStr);
					layoutServices.addView(viewService);
				}
			}
		}


	} // end method addAdditionalFieldsAndServices

	/**
	 * Method for getting order id
	 * @return - order id
     */
	public String getOrderId() {
		return orderId;

	} // end method getOrderId
}
