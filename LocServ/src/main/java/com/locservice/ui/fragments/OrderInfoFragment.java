package com.locservice.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.adapters.ChildSeatRVAdapter;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.CardInfo;
import com.locservice.api.entities.CardsData;
import com.locservice.api.entities.ChildSeat;
import com.locservice.api.entities.ChildSeatData;
import com.locservice.api.entities.ClientInfo;
import com.locservice.api.entities.Corporation;
import com.locservice.api.entities.DriverInfo;
import com.locservice.api.entities.Order;
import com.locservice.api.entities.OrderStatusAdditionalField;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.entities.OrderStatusService;
import com.locservice.api.entities.Tariff;
import com.locservice.api.entities.TariffService;
import com.locservice.api.manager.CreditCardManager;
import com.locservice.api.manager.DriverManager;
import com.locservice.api.manager.OrderManager;
import com.locservice.db.LanguageDBManager;
import com.locservice.db.TariffDBManager;
import com.locservice.db.TariffServiceDBManager;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.ChatActivity;
import com.locservice.ui.OrderInfoActivity;
import com.locservice.ui.controls.AutoResizeTextView;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.controls.SlidingUpPanelLayout;
import com.locservice.ui.controls.SwipeRelativeLayout;
import com.locservice.ui.helpers.DateHelper;
import com.locservice.ui.utils.ResizeViewAnimation;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderInfoFragment extends Fragment {

	private static final String TAG = OrderInfoFragment.class.getSimpleName();
	
	private FragmentActivity mContext;
	private View rootView;
	private String orderID;
	public int topHeight;

	private TextView textViewTopContent;
	private LinearLayout layoutTripCounter;
	private RelativeLayout layoutRatePart;
	private TextView textViewGetFromCard;
	
	private TextView textViewTopTitle;

	private MapDriverFragment mapFragment = null;

	private RelativeLayout layoutTop;

	private Animation animTopLayout;
	private float rate = 0;
	private boolean isCloseFeedback;

	private CustomTextView textViewSendFeedback;
	private View viewForAnimate;
	private ImageView imageViewArrow;

	private DriverManager driverManager;
	private ImageView imageViewBack;
	private CustomTextView textViewArrivedAnotherBonus;
	private CustomTextView textVewConnectToService;
	private int orderRate;
	private TextView textViewSetFeedback;
	private ScrollView scrollViewSlidePanelContainer;
	private SlidingUpPanelLayout mSlidingUpPanelLayout;


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView != null)
            return rootView;
		rootView = inflater.inflate(R.layout.fragment_order_info, container, false);
		mContext = getActivity();

		if (((OrderInfoActivity)mContext).getOrder() != null) {
			setServiceOptions(((OrderInfoActivity)mContext).getOrder());
		}

		// setting blur gradient drawables
		setBlurGradientDrawables();
		// driver manager
		driverManager = new DriverManager((ICallBack) mContext);

		ScrollView scrollviewTripContent = (ScrollView) rootView.findViewById(R.id.scrollviewTripContent);
		layoutTop = (RelativeLayout) rootView.findViewById(R.id.layoutTop);
		((SwipeRelativeLayout)layoutTop).setScrollableView(scrollviewTripContent);
		((SwipeRelativeLayout)layoutTop).setOnSwipeListener(new SwipeRelativeLayout.OnSwipeListener() {
			@Override
			public void onSwipeDown() {
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoFragment.layoutTop  : setOnSwipeListener : onSwipeDown");
				if (ratingBarFeedback == null)
					ratingBarFeedback = (RatingBar) rootView.findViewById(R.id.ratingBarFeedback);
				if (ratingBarFeedback.getVisibility() != View.VISIBLE) {
					// check network status
					if (Utils.checkNetworkStatus(mContext)) {

						if (layoutTripContent.getVisibility() == View.GONE) {
							isCloseFeedback = false;
							layoutTripContent.setVisibility(View.VISIBLE);
							if (layoutClose.getVisibility() == View.INVISIBLE) {
								layoutClose.setEnabled(true);
								layoutClose.setVisibility(View.VISIBLE);
							}
							imageViewArrow.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_up_black));

							Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alfa_anim_appear);
							layoutTripContent.startAnimation(fadeInAnimation);
							Animation fadeOutAnimation = new AlphaAnimation(1f, 0f);
							fadeOutAnimation.setDuration(300);
							layoutRatePart.startAnimation(fadeOutAnimation);

							if (layoutRatePart.getVisibility() == View.VISIBLE)
								layoutRatePart.setVisibility(View.GONE);

							int Measuredheight = 0;
							Point size = new Point();
							WindowManager w = mContext.getWindowManager();
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								w.getDefaultDisplay().getSize(size);
								Measuredheight = size.y;
							} else {
								Display d = w.getDefaultDisplay();
								Measuredheight = d.getHeight();
							}
							ResizeViewAnimation a = new ResizeViewAnimation(layoutTop);
							a.setDuration(500);
							a.setAnimationListener(new AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
								}

								@Override
								public void onAnimationRepeat(Animation animation) {
								}

								@Override
								public void onAnimationEnd(Animation animation) {
									// FADE IN ANIMATION
									Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alfa_anim_appear);
									textViewGetFromCard.startAnimation(fadeInAnimation);
									textViewGetFromCard.setVisibility(View.VISIBLE);
								}
							});
							LayoutParams layoutParams = layoutTop.getLayoutParams();
							topHeight = layoutParams.height;
							if (CMAppGlobals.DEBUG)
								Logger.i(TAG, ":: OrderInfoFragment.layoutTripCounter.onClick : GONE : Measuredheight : " + Measuredheight + " : topHeight : " + topHeight);
//						int addedAnimationSize = getResources().getInteger(R.integer.int_order_info_added_animation_size);
							int addedAnimationSize = -24; // status bar size 24dp
							a.setParams(topHeight, Measuredheight + CMApplication.dpToPx(addedAnimationSize));
							layoutTop.startAnimation(a);
						}
					}
				}

			}

			@Override
			public void onSwipeUp() {
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoFragment.layoutTop  : setOnSwipeListener : onSwipeUp");
				if (ratingBarFeedback == null)
					ratingBarFeedback = (RatingBar) rootView.findViewById(R.id.ratingBarFeedback);
				if (ratingBarFeedback.getVisibility() != View.VISIBLE) {
					// check network status
					if (Utils.checkNetworkStatus(mContext)) {

						if (layoutTripContent.getVisibility() == View.VISIBLE) {
							layoutClose.setVisibility(View.INVISIBLE);
							closeOrderInfoPanel();
						}
					}
				}
			}
		});
//


		scrollViewSlidePanelContainer = (ScrollView) rootView.findViewById(R.id.scrollViewSlidePanelContainer);
		mSlidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
		mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);
		mSlidingUpPanelLayout.setScrollableView(scrollViewSlidePanelContainer, 0);

		// set arguments
		SetArguments();

		viewForAnimate = rootView.findViewById(R.id.viewForAnimate);
		textViewSendFeedback = (CustomTextView) rootView.findViewById(R.id.textViewSendFeedback);
		textViewSendFeedback.setOnClickListener(new View.OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi") @Override
			public void onClick(View v) {
				// check network status
				if (Utils.checkNetworkStatus(mContext)) {
					if (((OrderInfoActivity)mContext).getOrder() != null && isNotEmptyString(editTextViewFeedbackContent.getText().toString())) {
						OrderManager orderManager = new OrderManager((ICallBack) mContext);
						Order order = ((OrderInfoActivity)mContext).getOrder();
						orderManager.SetOrderRating(order.getIdOrder(), (int) ratingBarOrder.getRating(),editTextViewFeedbackContent.getText().toString());
					}
				}
			}
		});
		
		// CREATE MAP FRAGMENT
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		mapFragment = (MapDriverFragment) getFragmentManager().findFragmentById(R.id.map_container);
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.onCreateView ");
		if (mapFragment == null) {
			Bundle args = new Bundle();
			args.putBoolean("driver_show", true);
			mapFragment = new MapGoogleFragment();
			mapFragment.setArguments(args);
			fragmentTransaction.replace(R.id.info_fragment_container, mapFragment);
			fragmentTransaction.commit();
		}

		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoFragment.onBackPressed ");
				// check network status
				if (Utils.checkNetworkStatus(mContext)) {
					InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(editTextViewFeedbackContent.getWindowToken(), 0);
					mContext.onBackPressed();
				}
			}
		});
		
		// timer
		this.topBarHandler.postDelayed(this.updateTimeRunnable, 1500);

		final View activityRootView = rootView.findViewById(R.id.main_layout);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
				int rootView = activityRootView.getRootView().getHeight();
				if (CMAppGlobals.DEBUG) Logger.d(TAG, ":: OrderInfoFragment.onGlobalLayout : getRootView : " + activityRootView.getRootView().getHeight()
								+ " : main_layout : " + activityRootView.getHeight());
				if (heightDiff > rootView / 4) { // if more than 100 pixels, its probably a keyboard...
					if (CMAppGlobals.DEBUG) Logger.d(TAG, ":: OrderInfoFragment.onGlobalLayout : 1");
					viewForAnimate.setVisibility(View.GONE);
				} else {
					viewForAnimate.setVisibility(View.VISIBLE);
					if (CMAppGlobals.DEBUG) Logger.d(TAG, ":: OrderInfoFragment.onGlobalLayout : 2");
				}
			}
		});

		textViewArrivedAnotherBonus = (CustomTextView) rootView.findViewById(R.id.textViewArrivedAnotherBonus);
		textViewArrivedAnotherBonus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, textViewArrivedAnotherBonus.getText().toString(), Toast.LENGTH_SHORT).show();
			}
		});
		textVewConnectToService = (CustomTextView) rootView.findViewById(R.id.textVewConnectToService);
		textVewConnectToService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, ChatActivity.class));
			}
		});

		return rootView;
	}
	
	private Handler topBarHandler = new Handler();

	protected LinearLayout layoutTripContent;

	protected FrameLayout layoutClose;

	private final Runnable updateTimeRunnable = new Runnable() {
		@Override
		public void run() {
			// set items
			SetItems(rootView);
		}
	};

	private RatingBar ratingBarOrder;

	private TextView textViewArrivedVote;

	private RatingBar ratingBarFeedback;

	private LinearLayout layoutFeedback;

	private EditText editTextViewFeedbackContent;
	
	@Override
	public void onResume() {
		super.onResume();
		// map buttons
		mapFragment.hideControls(rootView);
	}

	/**
	 * Method for setting bundle arguments
	 */
	private void SetArguments() {
		Bundle arguments = getArguments();
		if(arguments != null) {
			if(arguments.containsKey(CMAppGlobals.EXTRA_ORDER_ID)) {
				orderID = arguments.getString(CMAppGlobals.EXTRA_ORDER_ID);
			}
			if(arguments.containsKey(CMAppGlobals.EXTRA_ORDER_RATE)) {
				orderRate = arguments.getInt(CMAppGlobals.EXTRA_ORDER_RATE);
			}

		}

		if (((OrderInfoActivity)mContext).getOrder() != null) {
			setFeedbackContent(((OrderInfoActivity)mContext).getOrder());
		}
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.SetArguments : orderID : " + orderID);

		// get driver information
		driverManager.GetDriverInfo(String.valueOf(orderID), "");

	} // end method SetArguments
	
	/**
	 * Method for setting top bar items
	 *
	 * @param view - view for set items
	 */
	public void SetItems(View view) {
		
		ratingBarFeedback = (RatingBar) rootView.findViewById(R.id.ratingBarFeedback);
		editTextViewFeedbackContent = (EditText) rootView.findViewById(R.id.editTextViewFeedbackContent);
//		editTextViewFeedbackContent.setOnKeyListener(new OnKeyListener() {
//			
//			@SuppressWarnings("deprecation")
//			@SuppressLint("NewApi") @Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
				
//				if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER) {
//					int Measuredheight = 0;  
//					Point size = new Point();
//					WindowManager w = mContext.getWindowManager();
//					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
//						w.getDefaultDisplay().getSize(size);
//						Measuredheight = size.y; 
//					}else{
//						Display d = w.getDefaultDisplay(); 
//						Measuredheight = d.getHeight(); 
//					}
//					ResizeViewAnimation a = new ResizeViewAnimation(layoutTop);
//					a.setDuration(500);
//					a.setParams(Measuredheight, topHeight);
//					layoutTop.startAnimation(a);
//					ratingBarFeedback.setVisibility(View.INVISIBLE);
//					layoutClose.setVisibility(View.INVISIBLE);
//					layoutFeedback.setVisibility(View.GONE);
//					layoutTripContent.setVisibility(View.VISIBLE);
//					layoutRatePart.setVisibility(View.VISIBLE);
//					layoutTripCounter.setVisibility(View.VISIBLE);
//					textViewTopTitle.setVisibility(View.VISIBLE);
//					InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.hideSoftInputFromWindow(editTextViewFeedbackContent.getWindowToken(), 0);
//				}
				
//				return false;
//			}
//		});
		layoutFeedback = (LinearLayout)rootView.findViewById(R.id.layoutFeedback);
		textViewSetFeedback = (TextView)rootView.findViewById(R.id.textViewSetFeedback);
		textViewGetFromCard = (TextView)rootView.findViewById(R.id.textViewGetFromCard);
		textViewArrivedVote = (TextView)rootView.findViewById(R.id.textViewArrivedVote);
		if (orderRate != 0) {
			rate = orderRate;
		}
		textViewArrivedVote.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi") 
			@Override
			public void onClick(View v) {
				// check network status
				if (Utils.checkNetworkStatus(mContext)) {

					if (rate != 0) {
						// resize background
						int Measuredheight = 0;
						Point size = new Point();
						WindowManager w = mContext.getWindowManager();
						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
							w.getDefaultDisplay().getSize(size);
							Measuredheight = size.y;
						}else{
							Display d = w.getDefaultDisplay();
							Measuredheight = d.getHeight();
						}
						ResizeViewAnimation a = new ResizeViewAnimation(layoutTop);
						a.setDuration(500);
						LayoutParams layoutParams = layoutTop.getLayoutParams();
						topHeight = layoutParams.height;
						a.setParams(topHeight, Measuredheight + 100);
						layoutTop.startAnimation(a);
						isCloseFeedback = true;
						ratingBarFeedback.setVisibility(View.VISIBLE);
						layoutClose.setVisibility(View.VISIBLE);
						layoutFeedback.setVisibility(View.VISIBLE);
						layoutTripContent.setVisibility(View.GONE);
						layoutRatePart.setVisibility(View.INVISIBLE);
						layoutTripCounter.setVisibility(View.INVISIBLE);
						textViewTopTitle.setVisibility(View.INVISIBLE);
						ratingBarFeedback.setRating(rate);
						editTextViewFeedbackContent.requestFocus();

						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {

							@Override
							public void run() {
								InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
								mgr.showSoftInput(editTextViewFeedbackContent, InputMethodManager.SHOW_IMPLICIT);
							}
						}, 600);
					}
				}
			}
		});
		layoutTop = (RelativeLayout) rootView.findViewById(R.id.layoutTop);
		layoutTop.setVisibility(View.VISIBLE);
		animTopLayout = AnimationUtils.loadAnimation(mContext, R.anim.top_layout_to_bottom_anim);
		animTopLayout.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				changeSlidePanelContentHeight(false);

				ViewGroup.LayoutParams params = layoutTop.getLayoutParams();
				params.height = getLayoutTopHeight();
				layoutTop.setLayoutParams(params);
				layoutTop.requestLayout();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		layoutTop.setAnimation(animTopLayout);
		layoutTop.bringToFront();
		
		textViewTopContent = (TextView) view.findViewById(R.id.textViewTopContent);
		layoutRatePart = (RelativeLayout)view.findViewById(R.id.layoutRatePart);
		layoutTripCounter = (LinearLayout) view.findViewById(R.id.layoutTripCounter);
		textViewTopTitle = (TextView) view.findViewById(R.id.textViewTopTitle);
		imageViewArrow = (ImageView) view.findViewById(R.id.imageViewArrow);
		layoutTripCounter.setVisibility(View.VISIBLE);

        ratingBarOrder = (RatingBar)rootView.findViewById(R.id.ratingBarOrder);
		if (orderRate != 0) {
			ratingBarOrder.setRating(orderRate);
			ratingBarOrder.setIsIndicator(true);
			if (((OrderInfoActivity)mContext).getOrder() != null
					&& ((OrderInfoActivity)mContext).getOrder().getFeedback() != null
					&& !((OrderInfoActivity)mContext).getOrder().getFeedback().isEmpty()) {
				textViewArrivedVote.setText(R.string.str_comments);
			} else {
				switch (orderRate) {
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
		}
		ratingBarOrder.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            	rate = rating;
            	ratingBar.setRating(rating);
            	ratingBarOrder.setRating(rating);
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment : RATING_BAR : rate : " + rate);
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
				Order order = ((OrderInfoActivity)mContext).getOrder();
				if (order != null) {
					OrderManager orderManager = new OrderManager((ICallBack) mContext);
					orderManager.SetOrderRating(order.getIdOrder(), (int) rating, editTextViewFeedbackContent.getText().toString());
				}
            }
        });
        
		layoutClose = (FrameLayout) layoutTop.findViewById(R.id.layoutClose);
		layoutClose.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi") @Override
			public void onClick(View v) {
				if(layoutClose.isEnabled()) {
					layoutClose.setVisibility(View.INVISIBLE);

					closeOrderInfoPanel();
				}
			}
		});
		
		layoutTripContent = (LinearLayout) layoutTop.findViewById(R.id.layoutTripContent);
		layoutTripCounter = (LinearLayout) layoutTop.findViewById(R.id.layoutTripCounter);
		layoutTripCounter.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi") @Override
			public void onClick(View v) {
				// check network status
				if (Utils.checkNetworkStatus(mContext)) {

					if(layoutTripContent.getVisibility() == View.GONE) {
						isCloseFeedback = false;
						layoutTripContent.setVisibility(View.VISIBLE);
						if(layoutClose.getVisibility() == View.INVISIBLE) {
							layoutClose.setEnabled(true);
							layoutClose.setVisibility(View.VISIBLE);
						}
						imageViewArrow.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_up_black));

						Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alfa_anim_appear);
						layoutTripContent.startAnimation(fadeInAnimation);
						Animation fadeOutAnimation = new AlphaAnimation(1f, 0f);
						fadeOutAnimation.setDuration(300);
						layoutRatePart.startAnimation(fadeOutAnimation);

						if(layoutRatePart.getVisibility() == View.VISIBLE)
							layoutRatePart.setVisibility(View.GONE);

						int Measuredheight = 0;
						Point size = new Point();
						WindowManager w = mContext.getWindowManager();
						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
							w.getDefaultDisplay().getSize(size);
							Measuredheight = size.y;
						}else{
							Display d = w.getDefaultDisplay();
							Measuredheight = d.getHeight();
						}
						ResizeViewAnimation a = new ResizeViewAnimation(layoutTop);
						a.setDuration(500);
						a.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {
							}
							@Override
							public void onAnimationRepeat(Animation animation) {
							}
							@Override
							public void onAnimationEnd(Animation animation) {
								// FADE IN ANIMATION
								Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alfa_anim_appear);
								textViewGetFromCard.startAnimation(fadeInAnimation);
								textViewGetFromCard.setVisibility(View.VISIBLE);
							}
						});
						LayoutParams layoutParams = layoutTop.getLayoutParams();
						topHeight = layoutParams.height;
						if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoFragment.layoutTripCounter.onClick : GONE : Measuredheight : " + Measuredheight + " : topHeight : " + topHeight);
						int addedAnimationSize = -24; // status bar size 24dp
						a.setParams(topHeight, Measuredheight + CMApplication.dpToPx(addedAnimationSize));
						layoutTop.startAnimation(a);
					} else {
						layoutClose.setVisibility(View.INVISIBLE);
						closeOrderInfoPanel();
					}
				}
			}
		});
		
	} // end method setTopBarItems

	/**
	 * Method for closing order info panel
	 */
	public void closeOrderInfoPanel() {
		if (isCloseFeedback) {
			int Measuredheight = 0;
			Point size = new Point();
			WindowManager w = mContext.getWindowManager();
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				w.getDefaultDisplay().getSize(size);
				Measuredheight = size.y;
			}else{
				Display d = w.getDefaultDisplay();
				Measuredheight = d.getHeight();
			}
			ResizeViewAnimation a = new ResizeViewAnimation(layoutTop);
			a.setDuration(500);
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoFragment.closeOrderInfoPanel : isCloseFeedback : true : Measuredheight : " + Measuredheight + " : topHeight : " + topHeight);
			a.setParams(Measuredheight, topHeight);
			layoutTop.startAnimation(a);
			ratingBarFeedback.setVisibility(View.INVISIBLE);
			layoutClose.setVisibility(View.INVISIBLE);
			layoutFeedback.setVisibility(View.GONE);
			layoutTripContent.setVisibility(View.GONE);
			layoutRatePart.setVisibility(View.VISIBLE);
			layoutTripCounter.setVisibility(View.VISIBLE);
			textViewTopTitle.setVisibility(View.VISIBLE);
			InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editTextViewFeedbackContent.getWindowToken(), 0);
		} else {
			if(layoutTripContent.getVisibility() == View.VISIBLE) {
				layoutTripContent.setVisibility(View.GONE);
			}
			// FADE IN/OUT ANIMATION
			Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alfa_anim_appear);
			layoutRatePart.startAnimation(fadeInAnimation);
			Animation fadeOutAnimation = new AlphaAnimation(1f, 0f);
			fadeOutAnimation.setDuration(300);
			layoutTripContent.startAnimation(fadeOutAnimation);
			layoutTripCounter.setEnabled(true);
			imageViewArrow.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_down_black));

			int Measuredheight = 0;
			Point size = new Point();
			WindowManager w = mContext.getWindowManager();
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
				w.getDefaultDisplay().getSize(size);
				Measuredheight = size.y;
			}else{
				Display d = w.getDefaultDisplay();
				Measuredheight = d.getHeight();
			}
			ResizeViewAnimation a = new ResizeViewAnimation(layoutTop);
			a.setDuration(500);
			a.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					textViewGetFromCard.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					if(layoutRatePart.getVisibility() == View.GONE)
						layoutRatePart.setVisibility(View.VISIBLE);
				}
			});
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoFragment.closeOrderInfoPanel : isCloseFeedback : false : Measuredheight : " + Measuredheight + " : topHeight : " + topHeight);
			a.setParams(Measuredheight, topHeight);
			layoutTop.startAnimation(a);
		}

	} // end method closeOrderInfoPanel


	/**
	 * Method for setting current driver information
	 *
	 * @param driverInfo - driver information
	 * @param show - show driver ui
	 */
	public void setDriverUI(DriverInfo driverInfo, boolean show) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.setDriverUI : "
				+ driverInfo + " : show : " + show);

		if(mapFragment != null)
			mapFragment.setDriverUI(driverInfo, show);

	} // end method setDriverUI

	/**
	 * Method for adding Additional Fields And Services
	 * @param orderStatusData - order status data
	 */
	public void addAdditionalFieldsAndServices (OrderStatusData orderStatusData) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoFragment.addAdditionalFieldsAndServices : orderStatusData : " + orderStatusData);

		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoFragment.addAdditionalFieldsAndServices : status : " + orderStatusData.getStatus());
		if(mContext.getApplicationContext() != null && ((CMApplication)mContext.getApplicationContext()).getCurrentActivity() instanceof OrderInfoActivity) {

			if (orderStatusData.getStatus() != null && (orderStatusData.getStatus().equals("CC") || orderStatusData.getStatus().equals("NC"))) {
				ratingBarOrder = (RatingBar) rootView.findViewById(R.id.ratingBarOrder);
				ratingBarOrder.setIsIndicator(true);
				textViewArrivedVote = (TextView) rootView.findViewById(R.id.textViewArrivedVote);
				textViewArrivedVote.setVisibility(View.INVISIBLE);

			}

			if (textViewTopTitle == null)
				textViewTopTitle = (TextView) rootView.findViewById(R.id.textViewTopTitle);
			if (orderStatusData.getTitle() != null && !orderStatusData.getTitle().isEmpty()) {
				textViewTopTitle.setText(orderStatusData.getTitle());
			} else {
				textViewTopTitle.setText(mContext.getResources().getString(R.string.str_order_payment));
			}

			TextView textViewCounterBack = (TextView) rootView.findViewById(R.id.textViewCounterBack);
			if (orderStatusData.getPrice() != null && !orderStatusData.getPrice().isEmpty())  {
				textViewCounterBack.setText(orderStatusData.getPrice());
			}

			AutoResizeTextView textViewTopContent = (AutoResizeTextView) rootView.findViewById(R.id.textViewTopContent);
			if (!orderStatusData.getSubTitle().isEmpty()) {
				int minTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, mContext.getResources().getDisplayMetrics());
				textViewTopContent.setMinTextSize(minTextSize);
				Spanned spanned = Html.fromHtml(orderStatusData.getSubTitle());
				textViewTopContent.setText(spanned);
			}
			TextView textViewBonus = (TextView) rootView.findViewById(R.id.textViewBonus);
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.addAdditionalFieldsAndServices : bonus : " + orderStatusData.getBonus());
			if (orderStatusData.getBonus() != null && !orderStatusData.getBonus().isEmpty()) {
				String bonus = "+" + orderStatusData.getBonus();
				textViewBonus.setText(bonus);
			}

			List<OrderStatusAdditionalField> additionalFields = orderStatusData.getAdditionalFields();
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.addAdditionalFieldsAndServices : additionalFields : " + additionalFields);
			LinearLayout layoutAdditional = (LinearLayout) rootView.findViewById(R.id.layoutAdditionalFields);
			if (layoutAdditional.getChildCount() != 0) {
				layoutAdditional.removeAllViews();
			}
			if (additionalFields != null && !additionalFields.isEmpty()) {
				for (OrderStatusAdditionalField item : additionalFields) {
					View viewAdditional = LayoutInflater.from(mContext).inflate(R.layout.list_item_additional_fields_black, null);
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
					View viewService = LayoutInflater.from(mContext).inflate(R.layout.list_item_service_black, null);
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
	 * Method for getting mapFragment
	 * @return
	 */
	public MapDriverFragment getMapFragment() {
		return mapFragment;

	} // end method getMapFragment

	/**
	 * Method for showing pins on map
	 */
	public void showPinsOnMap() {
		// show point A and B
		if (((OrderInfoActivity)mContext).getOrder() != null) {
			mapFragment.drawRoadDirections(null, true, true, false);
			setEnableRatingBar();
		} else {
			OrderManager orderManager = new OrderManager((ICallBack) mContext);
			orderManager.GetOrdersInfo("", orderID, RequestTypes.REQUEST_GET_ORDERS_INFO);
		}

	} // end method showPinsOnMap

	public void drawTrack(List<LatLng> track) {
		mapFragment.drawRoadDirections(track);
	}

	/**
	 * Method for setting enable ratingBarOrder and textViewArrivedVote
	 */
	public void setEnableRatingBar () {
		ratingBarOrder = (RatingBar) rootView.findViewById(R.id.ratingBarOrder);
		if (ratingBarOrder.getRating() != 0)
//			ratingBarOrder.setEnabled(false);
			ratingBarOrder.setIsIndicator(true);

		textViewArrivedVote = (TextView)rootView.findViewById(R.id.textViewArrivedVote);
		editTextViewFeedbackContent = (EditText)rootView.findViewById(R.id.editTextViewFeedbackContent);
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.setEnableRatingBar : editTextViewFeedbackContent.getText : " + editTextViewFeedbackContent.getText().toString());
		if (!editTextViewFeedbackContent.getText().toString().isEmpty()) {
			textViewArrivedVote.setText(R.string.str_comments);
			editTextViewFeedbackContent.setKeyListener(null);
			textViewSendFeedback.setVisibility(View.INVISIBLE);
		} else {
//			textViewArrivedVote.setText(R.string.str_feedback_order_info);
			switch ((int) ratingBarOrder.getRating()) {
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


	} // end method setEnableRatingBar

	/**
	 * Method for doing action by sanding feedback
	 */
	public void sandFeedbackAction() {

		int Measuredheight = 0;
		Point size = new Point();
		WindowManager w = mContext.getWindowManager();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
			w.getDefaultDisplay().getSize(size);
			Measuredheight = size.y;
		}else{
			Display d = w.getDefaultDisplay();
			Measuredheight = d.getHeight();
		}
		ResizeViewAnimation a = new ResizeViewAnimation(layoutTop);
		a.setDuration(500);
		a.setParams(Measuredheight, topHeight);
		layoutTop.startAnimation(a);
		ratingBarFeedback.setVisibility(View.INVISIBLE);
		layoutClose.setVisibility(View.INVISIBLE);
		layoutFeedback.setVisibility(View.GONE);
		//				layoutTripContent.setVisibility(View.VISIBLE);
		layoutTripContent.setVisibility(View.GONE);
		layoutRatePart.setVisibility(View.VISIBLE);
		layoutTripCounter.setVisibility(View.VISIBLE);
		textViewTopTitle.setVisibility(View.VISIBLE);
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editTextViewFeedbackContent.getWindowToken(), 0);

	} // end method sandFeedbackAction


	/**
	 * Method for checking is string not empty
	 * @param str
	 * @return
	 */
	public boolean isNotEmptyString(String str) {
		str.trim();
		if (str.isEmpty()) {
			return false;
		}

		return true;

	} // end method isNotEmptyString

	/**
	 * Method for getting layoutFeedback
	 * @return layoutFeedback
	 */
	public LinearLayout getLayoutFeedback() {
		return layoutFeedback;

	} // end method getLayoutFeedback

	/**
	 * Method for setting Feedback Content
	 */
	public void setFeedbackContent(Order order) {
		if (editTextViewFeedbackContent == null)
			editTextViewFeedbackContent = (EditText) rootView.findViewById(R.id.editTextViewFeedbackContent);

		if (order != null) {
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.setEnableRatingBar : feedback : " + order.getFeedback());
			editTextViewFeedbackContent.setText(order.getFeedback());
		}

	} // end method setFeedbackContent

	/**
	 * Method for changing slide panel height
	 */
	public void changeSlidePanelContentHeight(boolean revert) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.changeSlidePanelContentHeight : revert : " + revert);

		if(!revert) {
			Point appUsableScreenSize = CMApplication.getAppUsableScreenSize(mContext);
			Point realScreenSize = CMApplication.getRealScreenSize(mContext);
//			int topHeight = layoutTop.getHeight();
			int topHeight = getLayoutTopHeight();

			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.changeSlidePanelContentHeight : appUsableScreenSize.y : " + appUsableScreenSize.y);
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.changeSlidePanelContentHeight : realScreenSize.y : " + realScreenSize.y);
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.changeSlidePanelContentHeight : realScreenSize.y_dp : " + CMApplication.pxToDp(realScreenSize.y));
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.changeSlidePanelContentHeight : realScreenSize.x : " + realScreenSize.x);
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.changeSlidePanelContentHeight : realScreenSize.x_dp : " + CMApplication.pxToDp(realScreenSize.x));
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.changeSlidePanelContentHeight : topHeight : " + topHeight);

			int newHeight = appUsableScreenSize.y - topHeight;

			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.changeSlidePanelContentHeight : newHeight : " + newHeight);

			ViewGroup.LayoutParams layoutParams = scrollViewSlidePanelContainer.getLayoutParams();
//			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//				layoutParams.height = newHeight-CMApplication.dpToPx(24);
//			} else {
//				layoutParams.height = newHeight;
//			}
			layoutParams.height = newHeight-CMApplication.dpToPx(24);
			scrollViewSlidePanelContainer.setLayoutParams(layoutParams);
		} else {
			ViewGroup.LayoutParams layoutParams = scrollViewSlidePanelContainer.getLayoutParams();
			layoutParams.height = (int) mContext.getResources().getDimension(R.dimen.order_info_slide_panel_content_height);
			scrollViewSlidePanelContainer.setLayoutParams(layoutParams);
		}

	} // end method changeSlidePanelContentHeight

	/**
	 * Method for setting service options
	 * @param order - current order
     */
	public void setServiceOptions(Order order) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.setServiceOptions : order : " + order);
		// FROM
		TextView textViewItemFrom = (TextView) rootView.findViewById(R.id.textViewItemFrom);
		if (order.getCollAddressText() != null && !order.getCollAddressText().isEmpty()) {
			textViewItemFrom.setText(order.getCollAddressText());
		}
		// TO
		TextView textViewItemTo = (TextView) rootView.findViewById(R.id.textViewItemTo);
		if (order.getDeliveryAddressText() != null && !order.getDeliveryAddressText().isEmpty()) {
			textViewItemTo.setText(order.getDeliveryAddressText());
		} else {
			textViewItemTo.setText(mContext.getResources().getString(R.string.str_say_to_driver));
		}
		// DATE
		TextView textViewItemDate = (TextView) rootView.findViewById(R.id.textViewItemDate);
		if (order.getHurry() == 1) {
			textViewItemDate.setText(mContext.getResources().getString(R.string.str_now));
		} else {
			if (order.getCollDate() != null && !order.getCollDate().isEmpty()
					&& order.getCollTime() != null && !order.getCollTime().isEmpty()) {
				String strDate = order.getCollDate() + " " + order.getCollTime();
				Date date = DateHelper.getDateByStringDateFormat(strDate, "dd-MM-yyyy HHmm");
				textViewItemDate.setText(DateHelper.getDateString(date));
			}
		}

		// TARIFF
		TextView textViewItemTariff = (TextView) rootView.findViewById(R.id.textViewItemTariff);
		// Getting tariff list
		TariffDBManager tariffDBManager = new TariffDBManager(mContext);
		int languageID = new LanguageDBManager(mContext).getLanguageIdByLocale("ru");
		ArrayList<Tariff> tariff_list = (ArrayList<Tariff>) tariffDBManager.getAllTariffs(languageID, order.getIdLocality());
		Tariff currentTariff = null;
		if (order.getTariff() != null && !order.getTariff().isEmpty()) {
			for (Tariff item : tariff_list) {
				if (item.getID().equals(order.getTariff())) {
					currentTariff = item;
					textViewItemTariff.setText(item.getName());
					break;
				}
			}

			// TARIFF SERVICE
			TextView textViewItemService = (TextView) rootView.findViewById(R.id.textViewItemService);
			List<String> tariffServicesString = MapViewFragment.getTariffServiceFields(order);
			if (currentTariff != null) {
				List<TariffService> tariff_services = currentTariff.getServices();
				if (tariff_services != null && !tariff_services.isEmpty()
						&& tariffServicesString != null &&!tariffServicesString.isEmpty()) {
					String strServices = "";
					for (String item : tariffServicesString) {
						for (TariffService serviceItem : tariff_services) {
							if (item.equals(serviceItem.getField()))
								strServices += serviceItem.getTitle() + "  ";
						}
					}
					textViewItemService.setText(strServices);
				} else {
					LinearLayout layoutItemService = (LinearLayout) rootView.findViewById(R.id.layoutItemService);
					layoutItemService.setVisibility(View.GONE);
				}
			}
		}
		// CHILD SEAT
		TextView textViewItemChildSeat = (TextView) rootView.findViewById(R.id.textViewItemChildSeat);
		if (order.getChildSeats() != null && !order.getChildSeats().isEmpty()) {
			String strChildSeat = "";
			List<ChildSeat> childSeatList = ChildSeatRVAdapter.getChildSeatList(mContext);
			List<ChildSeatData> childSeatDataList = order.getChildSeats();
			for (ChildSeatData item : childSeatDataList) {
				for (ChildSeat childItem : childSeatList) {
					if (item.getWeight() != null && !item.getWeight().isEmpty() &&
							Integer.parseInt(item.getWeight()) == childItem.getPosition()) {
						strChildSeat += childItem.getWeight() + "  ";
						break;
					}
				}
			}
			textViewItemChildSeat.setText(strChildSeat);
		} else {
			LinearLayout layoutItemChild = (LinearLayout) rootView.findViewById(R.id.layoutItemChild);
			layoutItemChild.setVisibility(View.GONE);
		}
		// COMMENT
		TextView textViewComment = (TextView) rootView.findViewById(R.id.textViewComment);
		String strComment = "";
		if (order.getCollPodjed() != null && !order.getCollPodjed().isEmpty()) {
			strComment += mContext.getResources().getString(R.string.str_entrance) + " " + order.getCollPodjed() + ". ";
		}
		if (order.getOrderComment() != null && !order.getOrderComment().isEmpty()) {
			strComment += order.getOrderComment();
		}
		if (!strComment.isEmpty()) {
			textViewComment.setText(strComment);
		}
		// PAYMENT
		TextView textViewItemPayment = (TextView) rootView.findViewById(R.id.textViewItemPayment);
		String[] payment_items = mContext.getResources().getStringArray(R.array.so_payment_items);
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.setServiceOptions : PAYMENT"
				+ " : NoCashPayment : " + order.getNoCashPayment()
				+ " : OnlinePayment : " + order.getOnlinePayment()
				+ " : UseBonus : " + order.getUseBonus()
				+ " : NeedCard : " + order.getNeedCard()
		);
		if (order.getNoCashPayment() == 0
				&& order.getOnlinePayment() != null && order.getOnlinePayment().equals("0")
				&& order.getUseBonus() == 1) {
			// payment type is bonus
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.setServiceOptions : payment_type : bonus");
			textViewItemPayment.setText(mContext.getResources().getString(R.string.payment_bonus));
		} else if (order.getNoCashPayment() == 0
				&& order.getOnlinePayment() != null && order.getOnlinePayment().equals("1")) {
			// payment type is card
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.setServiceOptions : payment_type : card");
			CreditCardManager creditCardManager = new CreditCardManager((ICallBack) mContext);
			creditCardManager.GetCreditCards();
		} else if (order.getNoCashPayment() == 1
				&& order.getOnlinePayment() != null && order.getOnlinePayment().equals("0")) {
			// payment type is corporation
			// TODO request for getting corporate
//			ClientManager clientManager = new ClientManager((ICallBack) mContext);
//			clientManager.GetClientInfo();
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.setServiceOptions : payment_type : corporation");
			textViewItemPayment.setText(mContext.getResources().getString(R.string.payment_corporate));
		} else if (order.getNoCashPayment() == 0
				&& order.getOnlinePayment() != null && order.getOnlinePayment().equals("0")) {
			// payment type is cash
			textViewItemPayment.setText(mContext.getResources().getString(R.string.payment_cash));
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.setServiceOptions : payment_type : cash");
		}



	} // end method setServiceOptions

	/**
	 * Method for setting payment type
	 * @param order - order info
	 * @param clientInfo - clinet info
     */
	public void setPaymentType(Order order, ClientInfo clientInfo){
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.setServiceOptions : order : " + order + " : clientInfo : " + clientInfo);

		TextView textViewItemPayment = (TextView) rootView.findViewById(R.id.textViewItemPayment);
		List<Corporation> corporations = clientInfo.getCorporation();
		if (corporations != null && !corporations.isEmpty()) {
			// TODO set corporate name
		}

	} // end method setPaymentType

	/**
	 * Method for setting payment type
	 * @param order - order info
	 * @param cardsData - cards card data info
	 */
	public void setPaymentType(Order order, CardsData cardsData){
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.setServiceOptions : order : " + order + " : cardsData : " + cardsData);

		TextView textViewItemPayment = (TextView) rootView.findViewById(R.id.textViewItemPayment);
		String cardName = mContext.getResources().getString(R.string.payment_credit_card);
		if (order != null && cardsData != null) {
			List<CardInfo> cardInfoList = cardsData.getCards();
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderInfoFragment.setServiceOptions : cardInfoList : " + cardInfoList);
			if (cardInfoList != null && !cardInfoList.isEmpty()) {
				for (CardInfo item : cardInfoList) {
					if (item.getId() != null && !item.getId().isEmpty() &&
							order.getId_card() == Integer.parseInt(item.getId())) {
						cardName = item.getName();
						break;
					}
				}
			}
		}
		textViewItemPayment.setText(cardName);

	} // end method setPaymentType

	/**
	 * Method for setting blur gradient drawable
	 */
	public void setBlurGradientDrawables() {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment.setBlurGradientDrawables");

		GradientDrawable blurGradient = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[]{0x00FFFFFF,  //start color of gradient
				0xFFFFFFFF}); //end color of gradient
		blurGradient.setGradientType(GradientDrawable.RADIAL_GRADIENT); // making it circular gradient
		blurGradient.setGradientRadius(20);  // radius of the circle
		blurGradient.setGradientCenter(1, 0.53f); // center of gradient

		ImageView imageViewBlurFrom = (ImageView) rootView.findViewById(R.id.imageViewBlurFrom);
		imageViewBlurFrom.setBackgroundDrawable(blurGradient);
		ImageView imageViewBlurTo = (ImageView) rootView.findViewById(R.id.imageViewBlurTo);
		imageViewBlurTo.setBackgroundDrawable(blurGradient);
		ImageView imageViewBlurDate = (ImageView) rootView.findViewById(R.id.imageViewBlurDate);
		imageViewBlurDate.setBackgroundDrawable(blurGradient);
		ImageView imageViewBlurTariff = (ImageView) rootView.findViewById(R.id.imageViewBlurTariff);
		imageViewBlurTariff.setBackgroundDrawable(blurGradient);
		ImageView imageViewBlurPayment = (ImageView) rootView.findViewById(R.id.imageViewBlurPayment);
		imageViewBlurPayment.setBackgroundDrawable(blurGradient);
		ImageView imageViewBlurService = (ImageView) rootView.findViewById(R.id.imageViewBlurService);
		imageViewBlurService.setBackgroundDrawable(blurGradient);
		ImageView imageViewBlurChildSeat = (ImageView) rootView.findViewById(R.id.imageViewBlurChildSeat);
		imageViewBlurChildSeat.setBackgroundDrawable(blurGradient);

	} // end method setBlurGradientDrawables

	/**
	 * Method for getting layout top height
	 * @return - Layout top height
     */
	public int  getLayoutTopHeight() {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoFragment.setLayoutTopHeight");

		RelativeLayout layoutToolBar = (RelativeLayout) rootView.findViewById(R.id.layoutToolBar);

		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderInfoFragment.setLayoutTopHeight"
				+ " : layoutToolBar.getHeight : " + layoutToolBar.getHeight()
				+ " : layoutTripCounter.getHeight : " + layoutTripCounter.getHeight()
				+ " : textViewTopContent.getHeight : " + textViewTopContent.getHeight()
				+ " : ratingBarOrder.getHeight : " + ratingBarOrder.getHeight()
				+ " : textViewArrivedVote.getHeight : " + textViewArrivedVote.getHeight()
				+ " : layoutRatePart.getHeight : " + layoutRatePart.getHeight()
		);

		return layoutToolBar.getHeight()
				+ layoutTripCounter.getHeight()
				+ textViewTopContent.getHeight()
				+ layoutRatePart.getHeight();

	} // end method getLayoutTopHeight;

}
