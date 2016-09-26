package com.locservice.ui.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.entities.OrderStatusAdditionalField;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.entities.OrderStatusService;
import com.locservice.api.entities.TariffService;
import com.locservice.api.manager.OrderManager;
import com.locservice.db.LanguageDBManager;
import com.locservice.db.TariffServiceDBManager;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.CommonMenuActivity;
import com.locservice.ui.MainActivity;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.controls.SlidingUpPanelLayout;
import com.locservice.ui.controls.SlidingUpPanelLayout.SlideState;
import com.locservice.ui.fragments.MapDriverFragment;
import com.locservice.ui.fragments.MapGoogleFragment;
import com.locservice.ui.fragments.MapViewFragment;
import com.locservice.ui.utils.ActivityTypes;
import com.locservice.ui.utils.CounterDigitAnimation;
import com.locservice.ui.utils.CustomAnimationListener;
import com.locservice.ui.utils.ResizeViewAnimation;
import com.locservice.utils.Logger;

import java.util.List;

public class OrderStateHelper {

	private static final String TAG = OrderStateHelper.class.getSimpleName();

	private Context mContext;
	private RelativeLayout layoutTop;
	private MapDriverFragment mapFragment;
	private RelativeLayout layoutSectionTariff;
	private SlidingUpPanelLayout mSlidingUpPanelLayout;
	private View rootView;
	private ImageView imageViewTopBarShadow;

	private TextView textViewTopTitle;
	private TextView textViewTopContent;
	private TextView textViewChangeOrder;
	private Handler progressDialogHandler;
	private Runnable progressDialogRunnable;
	private AlertDialog cancelOrderDialog;


	/**
	 * Method for getting textView Confirm Order
	 *
	 * @return - textView confirm order
     */
	public TextView getTextViewConfirmOrder() {
		return textViewConfirmOrder;

	} // end methdo getTextViewConfirmOrder

	private TextView textViewConfirmOrder;


	public boolean isTopBarOpened = false;
	public boolean isTripClicked = false;
	public boolean clearAnimation = false;
	public boolean isSlidePanelOpened = false;

	protected Animation animTopLayout = null;
	protected TranslateAnimation translateTopLayout = null;
	protected TranslateAnimation translateSectionTariff = null;

	private ImageView imageViewLoadingRight;
	private ImageView imageViewLoadingLeft;

	private Animation orderLeftAnimation;
	private Animation orderRightAnimation;

	private TextView textViewCallDriver;
	private TextView textViewGoToDriver;
	private TextView textViewCancel;
	private TextView textViewCancelConfirm;
	private LinearLayout layoutTripCounter;
	private RelativeLayout layoutTopBottom;
	private LinearLayout layoutTripContent;
	private FrameLayout layoutClose;
	private ImageView imageViewArrow;

	private int tariff_layout_height = 0;
	private String price = "";
	private String currentPrice = "";
	private FrameLayout  layoutDigit2;
	private FrameLayout  layoutDigit3;
	private FrameLayout  layoutDigit4;
	private FrameLayout  layoutDigit5;
	private String driverPhoneNumber;

	public int topHeight;
	private CounterDigitAnimation cdAnimation;
	private String IdOrder;
	private boolean isCameOut = false;
	private boolean isAddedPinA;
	private MapViewFragment mapViewFragment;


	public OrderStateHelper(Context context, MapViewFragment mapViewFragment, View rootView, RelativeLayout layoutTop,
							RelativeLayout layoutSectionTariff, SlidingUpPanelLayout mSlidingUpPanelLayout,
							RelativeLayout mapContainer,
							MapDriverFragment mapFragment, OrderState orderState) {
		this.mContext = context;
		this.mapViewFragment = mapViewFragment;
		this.layoutTop = layoutTop;
		this.rootView = rootView;
		this.layoutSectionTariff = layoutSectionTariff;
		this.mapFragment = mapFragment;
		this.mSlidingUpPanelLayout = mSlidingUpPanelLayout;
		// set listeners
		SetEventListeners();
	}

	/**
	 * Method for setting all order listeners
	 */
	public void SetEventListeners() {
		float fontScale = mContext.getResources().getConfiguration().fontScale;

		TextView textViewCounterRuble = (TextView) rootView.findViewById(R.id.textViewCounterRuble);
		textViewCounterRuble.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				mContext.getResources().getInteger(R.integer.int_top_bar_counter_text_size) / fontScale);
		imageViewTopBarShadow = (ImageView) rootView.findViewById(R.id.imageViewTopBarShadow);
		layoutDigit2 = (FrameLayout) rootView.findViewById(R.id.layoutDigit2);
		layoutDigit2.setVisibility(View.GONE);
		layoutDigit3 = (FrameLayout) rootView.findViewById(R.id.layoutDigit3);
		layoutDigit3.setVisibility(View.GONE);
		layoutDigit4 = (FrameLayout) rootView.findViewById(R.id.layoutDigit4);
		layoutDigit4.setVisibility(View.GONE);
		layoutDigit5 = (FrameLayout) rootView.findViewById(R.id.layoutDigit5);
		layoutDigit5.setVisibility(View.GONE);
		imageViewArrow = (ImageView) layoutTop.findViewById(R.id.imageViewArrow);
		layoutTripContent = (LinearLayout) layoutTop.findViewById(R.id.layoutTripContent);
		layoutTopBottom = (RelativeLayout) layoutTop.findViewById(R.id.layoutTopBottom);
		layoutTripCounter = (LinearLayout) layoutTop.findViewById(R.id.layoutTripCounter);
		textViewCallDriver = (TextView) layoutTop.findViewById(R.id.textViewCallDriver);
		textViewCallDriver.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				mContext.getResources().getInteger(R.integer.int_top_bar_order_actions_text_size) / fontScale);
		textViewCallDriver.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (driverPhoneNumber != null && !driverPhoneNumber.isEmpty()) {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					// get current city phone number
					intent.setData(Uri.parse("tel:" + driverPhoneNumber));
					mContext.startActivity(intent);

				} else {
					Toast.makeText(mContext, R.string.alert_driver_phone_number, Toast.LENGTH_SHORT).show();
			}
			}
		});
		textViewGoToDriver = (TextView) layoutTop.findViewById(R.id.textViewGotoDriver);
		textViewGoToDriver.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				mContext.getResources().getInteger(R.integer.int_top_bar_order_actions_text_size) / fontScale);
		textViewGoToDriver.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (IdOrder != null && !IdOrder.isEmpty()) {
					// Request for informing driver that client came out
					OrderManager orderManager = new OrderManager((ICallBack) mContext);
					orderManager.CameOut(IdOrder);
				} else {
					Toast.makeText(mContext, R.string.alert_informing_driver_to_come, Toast.LENGTH_SHORT).show();
				}
			}
		});

		imageViewLoadingRight = (ImageView) layoutTop.findViewById(R.id.imageViewLoadingRight);
		imageViewLoadingLeft = (ImageView) layoutTop.findViewById(R.id.imageViewLoadingLeft);

		textViewTopTitle = (TextView) layoutTop.findViewById(R.id.textViewTopTitle);
		textViewTopTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				mContext.getResources().getInteger(R.integer.int_top_bar_top_title_text_size) / fontScale);
		textViewTopContent = (TextView) layoutTop.findViewById(R.id.textViewTopContent);
		textViewTopContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				mContext.getResources().getInteger(R.integer.int_top_bar_top_content_text_size) / fontScale);
		layoutClose = (FrameLayout) layoutTop.findViewById(R.id.layoutClose);
		layoutClose.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				if(layoutClose.isEnabled()) {
					layoutClose.setEnabled(false);
					layoutClose.setVisibility(View.INVISIBLE);
					imageViewArrow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_down));

					if(layoutTripContent.getVisibility() == View.VISIBLE) {
						layoutTripContent.setVisibility(View.GONE);
					}
					// FADE IN/OUT ANIMATION
					Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alfa_anim_appear);
					textViewTopContent.startAnimation(fadeInAnimation);
					Animation fadeOutAnimation = new AlphaAnimation(1f, 0f);
					fadeOutAnimation.setDuration(300);
					layoutTripContent.startAnimation(fadeOutAnimation);

					layoutTripCounter.setEnabled(true);
					int Measuredheight;
					Point size = new Point();
					WindowManager w = ((FragmentActivity)mContext).getWindowManager();
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
							if (textViewTopContent.getVisibility() == View.GONE)
								textViewTopContent.setVisibility(View.VISIBLE);
						}
					});
					a.setParams(Measuredheight, topHeight);
					layoutTop.startAnimation(a);

					Logger.i(TAG, ":: MapViewFragment : topHeight : " + topHeight + " : Measuredheight : " + Measuredheight);
				}
			}
		});
		textViewChangeOrder = (TextView) layoutTop.findViewById(R.id.textViewChangeOrder);
		textViewChangeOrder.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				mContext.getResources().getInteger(R.integer.int_top_bar_order_actions_text_size) / fontScale);
		textViewChangeOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// map buttons
				mapFragment.showControls(rootView);
				// hide driver layout
				mapFragment.hideDriver(rootView);

				isTopBarOpened = false;

				// STOP ANIMATIONS
				StopGradientAnimations();

				// CLOSE TOP LAYOUT
				animTopLayout = AnimationUtils.loadAnimation(mContext, R.anim.top_layout_to_top_anim);
				animTopLayout.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationEnd(Animation animation) {
						// set top search background
						textViewTopTitle.setTextColor(Color.WHITE);
						textViewTopContent.setTextColor(Color.WHITE);
					}

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
				layoutTop.startAnimation(animTopLayout);
				translateTopLayout = new TranslateAnimation(0, 0, (layoutTop.getHeight()), 0);
				translateTopLayout.setDuration(300);
				int modifierY=layoutSectionTariff.getHeight();
				translateSectionTariff = new TranslateAnimation(0, 0, modifierY, 0);
				translateSectionTariff.setDuration(300);
				layoutSectionTariff.startAnimation(translateSectionTariff);
				layoutSectionTariff.setEnabled(true);

				// slide up panel
				Animation translateAnimation=new TranslateAnimation(0, 0, 0, -modifierY);
				translateAnimation.setDuration(300);
				translateAnimation.setFillEnabled(true);
				CustomAnimationListener listener = new CustomAnimationListener(mSlidingUpPanelLayout, -modifierY, mContext);
				translateAnimation.setAnimationListener(listener);
				mSlidingUpPanelLayout.startAnimation(translateAnimation);
				if(isSlidePanelOpened) {
					mSlidingUpPanelLayout.CollapsePanelFirstTime(SlideState.EXPANDED);
				}
				layoutTop.setVisibility(View.GONE);
			}
		});

		textViewConfirmOrder = (TextView) layoutTop.findViewById(R.id.textViewConfirmOrder);
		textViewConfirmOrder.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				mContext.getResources().getInteger(R.integer.int_top_bar_order_actions_text_size) / fontScale);
		textViewConfirmOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(textViewConfirmOrder.isEnabled()) {
					textViewConfirmOrder.setEnabled(false);
					// CREATE NEW ORDER
					if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment : textViewConfirmOrder.setOnClick");
					mapViewFragment.setmConfirmVisible(false);
					((MapViewFragment) ((MainActivity) mContext).getCurrent_fragment()).createNewOrder();
				}
			}
		});

		textViewCancelConfirm = (TextView) layoutTop.findViewById(R.id.textViewCancelConfirm);
		textViewCancelConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				mContext.getResources().getInteger(R.integer.int_top_bar_order_actions_text_size) / fontScale);
		textViewCancelConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mapViewFragment.isHasAction()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
					builder.setMessage(R.string.str_are_you_sure_unset);
					builder.setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if(!textViewConfirmOrder.isEnabled()) {
								textViewConfirmOrder.setEnabled(true);
							}
							cancelOrder(false);
							// unset service options
							mapViewFragment.setServiceOptions(true, null);
							// get current address for address option
							((MapGoogleFragment)mapFragment).getAddressByCameraPosition();
						}
					});
					builder.setNegativeButton(mContext.getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

					builder.create().show();
				} else {
					if(!textViewConfirmOrder.isEnabled()) {
						textViewConfirmOrder.setEnabled(true);
					}
					cancelOrder(false);
					// unset service options
					mapViewFragment.setServiceOptions(true, null);
					// get current address for address option
					((MapGoogleFragment)mapFragment).getAddressByCameraPosition();
				}

			}
		});
		textViewCancel = (TextView) layoutTop.findViewById(R.id.textViewCancel);
		textViewCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				mContext.getResources().getInteger(R.integer.int_top_bar_order_actions_text_size) / fontScale);
		textViewCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				textViewCancel.setEnabled(false);

				if (((MainActivity)mContext).getCurrentOrderState() != null
						&&((MainActivity)mContext).getCurrentOrderState() == OrderState.RC) {
					OrderManager orderManager = new OrderManager((ICallBack) mContext);
					orderManager.PrepareCancelOrder(CMApplication.getTrackingOrderId());
				} else {
					// Show cancel order dialog
					showCancelOrderDialog();
				}
			}
		});

	} // end method SetEventListeners

	/**
	 * Method for showing cancel order dialog
	 */
	public void showCancelOrderDialog() {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.showCancelOrderDialog");

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
		builder.setMessage(R.string.str_are_you_sure);
		builder.setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// Request cancel order
				OrderManager orderManager = new OrderManager((ICallBack) mContext);
				orderManager.CancelOrder(CMApplication.getTrackingOrderId());
			}
		});
		builder.setNegativeButton(R.string.str_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();

		textViewCancel.setEnabled(true);

	} // end method showCancelOrderDialog


	/**
	 * Method for showing prepare dialog for cancel order
	 * @param message - message
     */
	public void showPrepareCancelOrderDialog(final String message) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.showPrepareCancelOrderDialog : message : "  + message + ":");

		LinearLayout layoutProgress = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_progress, null);
		final TextView textViewPrice = (TextView) layoutProgress.findViewById(R.id.textViewPrice);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
		String strPrice = message + " "
				+ " <font color='#009688'>"
				+ 60 + "</font> "
				+ mContext.getResources().getString(R.string.str_second_short);
		textViewPrice.setText(Html.fromHtml(strPrice), TextView.BufferType.SPANNABLE);
		builder.setView(layoutProgress);
		builder.setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (progressDialogHandler != null && progressDialogRunnable != null)
					progressDialogHandler.removeCallbacks(progressDialogRunnable);
				dialog.dismiss();
				// Request cancel order
				OrderManager orderManager = new OrderManager((ICallBack) mContext);
				orderManager.CancelOrder(CMApplication.getTrackingOrderId());
			}
		});
		builder.setNegativeButton(R.string.str_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (progressDialogHandler != null && progressDialogRunnable != null)
					progressDialogHandler.removeCallbacks(progressDialogRunnable);
				dialog.dismiss();
			}
		});

		cancelOrderDialog = builder.create();
		cancelOrderDialog.show();

		textViewCancel.setEnabled(true);

		if (progressDialogHandler != null && progressDialogRunnable != null)
			progressDialogHandler.removeCallbacks(progressDialogRunnable);
		progressDialogHandler = new Handler();
		progressDialogRunnable = new Runnable() {
			int count = 60;
			@Override
			public void run() {
				String strPrice = message + " "
						+ " <font color='#009688'>"
						+ count + "</font> "
						+ mContext.getResources().getString(R.string.str_second_short);
				textViewPrice.setText(Html.fromHtml(strPrice), TextView.BufferType.SPANNABLE);
				if (count > 0) {
					count--;
					progressDialogHandler.postDelayed(this, 1000);
				} else {
					cancelOrderDialog.dismiss();
				}
			}
		};

		progressDialogHandler.post(progressDialogRunnable);

	} // end method showPrepareCancelOrderDialog

	/**
	 * Method for changing top layout background
	 *
	 * @param orderState - current order state
	 */
	@SuppressWarnings("deprecation")
	public void ChangeTopLayoutBackground(OrderState orderState, boolean isNewState) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.ChangeTopLayoutBackground : orderState : " + orderState);
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.ChangeTopLayoutBackground : isNewState : " + isNewState);
		switch (orderState) {
			case ORDER_CONFIRM:
				layoutTop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_order_registered));
				imageViewTopBarShadow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_order_registered_shadow));
				break;
			case UNKNOWN:
				layoutTop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_order_registered));
				imageViewTopBarShadow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_order_registered_shadow));
				break;
			case R: // ORDER CREATED
				layoutTop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_search));
				imageViewTopBarShadow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_search_shadow));
				break;
			case LATER_R: // ORDER REGISTERED LATER
				layoutTop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_order_registered));
				imageViewTopBarShadow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_order_registered_shadow));
				break;
			case ASAP_R: // DRIVER SEARCH
				if(isNewState) {
					layoutTop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_search));

					Animation fadeIn2 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_in);
					layoutTop.startAnimation(fadeIn2);

					fadeIn2.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}
						@Override
						public void onAnimationEnd(Animation animation) {
							layoutTop.clearAnimation();
							imageViewTopBarShadow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_search_shadow));
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
				}
				break;
			case ASAP_A_BUSY:
				if(isNewState) {

					Animation fadeOutShadow4 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_out);
					imageViewTopBarShadow.startAnimation(fadeOutShadow4);

					Animation fadeOut4 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_out);
					layoutTop.startAnimation(fadeOut4);
					fadeOut4.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}
						@Override
						public void onAnimationEnd(Animation animation) {

							imageViewTopBarShadow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_found_shadow));
							Animation fadeInShadow4 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_in);
							imageViewTopBarShadow.startAnimation(fadeInShadow4);

							layoutTop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_found));
							Animation fadeIn4 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_in);
							layoutTop.startAnimation(fadeIn4);


							fadeIn4.setAnimationListener(new Animation.AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
								}
								@Override
								public void onAnimationEnd(Animation animation) {
									layoutTop.clearAnimation();
								}
								@Override
								public void onAnimationRepeat(Animation animation) {
								}
							});

						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
				}
				break;
			case A: // DRIVER APPOINTED
			case LATER_A:
			case ASAP_A_THIS: // DRIVER ON WAY
				if(isNewState) {

					Animation fadeOutShadow3 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_out);
					imageViewTopBarShadow.startAnimation(fadeOutShadow3);

					Animation fadeOut3 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_out);
					layoutTop.startAnimation(fadeOut3);
					fadeOut3.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}
						@Override
						public void onAnimationEnd(Animation animation) {

							imageViewTopBarShadow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_onway_shadow));
							Animation fadeInShadow4 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_in);
							imageViewTopBarShadow.startAnimation(fadeInShadow4);

							layoutTop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_onway));
							Animation fadeIn4 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_in);
							layoutTop.startAnimation(fadeIn4);

							fadeIn4.setAnimationListener(new Animation.AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
								}
								@Override
								public void onAnimationEnd(Animation animation) {
									layoutTop.clearAnimation();
								}
								@Override
								public void onAnimationRepeat(Animation animation) {
								}
							});
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
				}
				break;
			case RC: // DRIVER_ARRIVED
				if(isNewState) {

					Animation fadeOutShadow5 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_out);
					imageViewTopBarShadow.startAnimation(fadeOutShadow5);

					Animation fadeOut5 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_out);
					layoutTop.startAnimation(fadeOut5);
					fadeOut5.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}
						@Override
						public void onAnimationEnd(Animation animation) {

							imageViewTopBarShadow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_arrived_shadow));
							Animation fadeInShadow4 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_in);
							imageViewTopBarShadow.startAnimation(fadeInShadow4);

							layoutTop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_arrived));
							Animation fadeIn4 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_in);
							layoutTop.startAnimation(fadeIn4);

							fadeIn4.setAnimationListener(new Animation.AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
								}
								@Override
								public void onAnimationEnd(Animation animation) {
									layoutTop.clearAnimation();
								}
								@Override
								public void onAnimationRepeat(Animation animation) {
								}
							});

						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
				}
				break;
			case OW: // ORDER_FINISHED
				if(isNewState) {

					Animation fadeOutShadow6 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_out);
					imageViewTopBarShadow.startAnimation(fadeOutShadow6);

					Animation fadeOut6 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_out);
					layoutTop.startAnimation(fadeOut6);
					fadeOut6.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}
						@Override
						public void onAnimationEnd(Animation animation) {

							imageViewTopBarShadow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_trip_shadow));
							Animation fadeInShadow4 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_in);
							imageViewTopBarShadow.startAnimation(fadeInShadow4);

							layoutTop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_driver_trip));
							Animation fadeIn4 = AnimationUtils.loadAnimation(mContext, R.anim.bg_anim_fade_in);
							layoutTop.startAnimation(fadeIn4);

							fadeIn4.setAnimationListener(new Animation.AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
								}
								@Override
								public void onAnimationEnd(Animation animation) {
									layoutTop.clearAnimation();
								}
								@Override
								public void onAnimationRepeat(Animation animation) {
								}
							});
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
				}
				break;
			default:
				break;
		}

	} // end method ChangeTopLayout

	/**
	 * Method for setting top bar items
	 *
	 * @param view - view layout
	 */
	public void setTopBarItems(View view, OrderState orderState, String subTitle, String title) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.setTopBarItems : orderState : " + orderState + " : subTitle : " + subTitle + " : title : " + title);

		textViewTopContent.setPadding((int)mContext.getResources().getDimension(R.dimen.top_layout_content),
				(int)mContext.getResources().getDimension(R.dimen.top_padding_for_status_bar),
				(int)mContext.getResources().getDimension(R.dimen.top_layout_content),
				0);
		switch (orderState) {
			case ORDER_CONFIRM:
			{
				if(subTitle != null) setSubTitle(subTitle);
				if(title != null)textViewTopTitle.setText(title);
				// change texts
				textViewConfirmOrder.setVisibility(View.VISIBLE);
				textViewConfirmOrder.setTextColor(Color.WHITE);
				// hide left cancel
				textViewCancel.setVisibility(View.GONE);
				// show right cancel
				textViewCancelConfirm.setVisibility(View.VISIBLE);

				layoutTopBottom.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) textViewTopContent.getLayoutParams();
				layoutParams.setMargins(25, 25, 25, 25);
				textViewTopContent.setLayoutParams(layoutParams);
			}
			break;
			case UNKNOWN:
			{
				// change texts
				textViewConfirmOrder.setVisibility(View.GONE);
				textViewCancelConfirm.setVisibility(View.GONE);
				textViewChangeOrder.setVisibility(View.INVISIBLE);
				textViewChangeOrder.setTextColor(Color.WHITE);
				textViewCallDriver.setVisibility(View.INVISIBLE);
				textViewCallDriver.setTextColor(Color.WHITE);
				textViewGoToDriver.setVisibility(View.GONE);
				textViewCancel.setTextColor(Color.WHITE);
				layoutTopBottom.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) textViewTopContent.getLayoutParams();
				layoutParams.setMargins(25, 25, 25, 25);
				textViewTopContent.setLayoutParams(layoutParams);

				textViewCancel.setVisibility(View.VISIBLE);
			}
			break;
			case LATER_R:
			{
				if(subTitle != null) setSubTitle(subTitle);
				if(title != null)textViewTopTitle.setText(title);
				// change texts
				textViewChangeOrder.setVisibility(View.INVISIBLE);
				textViewChangeOrder.setTextColor(Color.WHITE);
				textViewCallDriver.setVisibility(View.INVISIBLE);
				textViewCallDriver.setTextColor(Color.WHITE);
				textViewGoToDriver.setVisibility(View.GONE);
				textViewCancel.setTextColor(Color.WHITE);
				layoutTopBottom.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) textViewTopContent.getLayoutParams();
				layoutParams.setMargins(25, 25, 25, 25);
				textViewTopContent.setLayoutParams(layoutParams);
				// hide driver layout
				this.mapFragment.hideDriver(view);
			}
			break;
			case R:
			case ASAP_R:
			{
				if(subTitle != null) setSubTitle(subTitle);
				if(title != null)textViewTopTitle.setText(title);
				textViewTopContent.setTextColor(Color.WHITE);
				textViewTopTitle.setTextColor(Color.WHITE);
				// change texts
				textViewChangeOrder.setVisibility(View.INVISIBLE);
				textViewCallDriver.setVisibility(View.INVISIBLE);
				textViewGoToDriver.setVisibility(View.GONE);
				textViewCancel.setVisibility(View.VISIBLE);
				layoutTopBottom.setVisibility(View.VISIBLE);
				// hide driver layout
				this.mapFragment.hideDriver(view);
			}
			break;
			case A:
			case LATER_A:
			case ASAP_A_BUSY:
			{
				if(subTitle != null) setSubTitle(subTitle);
				if(title != null)textViewTopTitle.setText(title);
				// change texts color
				textViewTopContent.setTextColor(Color.WHITE);
				textViewTopTitle.setTextColor(Color.WHITE);

				textViewCallDriver.setVisibility(View.VISIBLE);
				textViewChangeOrder.setVisibility(View.INVISIBLE);
				textViewGoToDriver.setVisibility(View.GONE);
				layoutTopBottom.setVisibility(View.VISIBLE);
				// show driver layout
				this.mapFragment.showDriver(view);
			}
			break;
			case ASAP_A_THIS:
			{
				if(subTitle != null) setSubTitle(subTitle);
				if(title != null)textViewTopTitle.setText(title);
				// change texts color
				textViewTopContent.setTextColor(Color.WHITE);
				textViewTopTitle.setTextColor(Color.WHITE);

				textViewCallDriver.setVisibility(View.VISIBLE);
				textViewChangeOrder.setVisibility(View.INVISIBLE);
				textViewGoToDriver.setVisibility(View.GONE);
				layoutTopBottom.setVisibility(View.VISIBLE);
				// show driver layout
				this.mapFragment.showDriver(view);
			}
			break;
			case RC:
			{
				if(subTitle != null) setSubTitle(subTitle);
				textViewTopContent.setTextColor(Color.BLACK);
				if(title != null)textViewTopTitle.setText(title);
				textViewTopTitle.setTextColor(Color.BLACK);
				// change menu image
				ImageView imageViewTopMenu = (ImageView) view.findViewById(R.id.imageViewTopMenu);
				imageViewTopMenu.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_menu_black));
				// change texts
				textViewChangeOrder.setVisibility(View.INVISIBLE);
				textViewCallDriver.setVisibility(View.VISIBLE);
				textViewCallDriver.setTextColor(Color.BLACK);
				if (!isCameOut) {
					textViewGoToDriver.setVisibility(View.VISIBLE);
				}
				textViewCancel.setTextColor(Color.BLACK);

				textViewCancel.setVisibility(View.VISIBLE);
				// show driver layout
				this.mapFragment.showDriver(view);
			}
			break;
			case OW:
			{
				RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) textViewTopContent.getLayoutParams();
				layoutParams.setMargins(25, 0, 25, 0);
				textViewTopContent.setLayoutParams(layoutParams);
				layoutTripCounter.setVisibility(View.VISIBLE);

				textViewTopContent.setPadding((int)mContext.getResources().getDimension(R.dimen.top_layout_content),
						0,
						(int)mContext.getResources().getDimension(R.dimen.top_layout_content),
						0);

				textViewTopContent.setTextColor(Color.WHITE);
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper ::: OW : subTitle : " + subTitle);
				if(subTitle != null) {
					String current_sub = getSubTitle();
					if(current_sub != null && current_sub.contains(", ")) {
						String[] strArray = current_sub.split(", ");
						String str2 = strArray[1];

						subTitle = subTitle + ", " + str2;
					}
				}
				if(subTitle != null) setSubTitle(subTitle);

				// start digit animation
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.setTopBarItems : price : " + price);
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.setTopBarItems : price.length : " + price.length());
				if (price != null && !currentPrice.equals(price)) {
					if (cdAnimation == null) {
						cdAnimation = new CounterDigitAnimation(mContext);
					}
					if (price.length() == 1) {
						cdAnimation.startAnimation(
								Integer.parseInt(String.valueOf(price.charAt(0))),
								-1,
								-1,
								-1,
								-1);
					} else if (price.length() == 2) {
						layoutDigit2.setVisibility(View.VISIBLE);
						cdAnimation.startAnimation(
								Integer.parseInt(String.valueOf(price.charAt(1))),
								Integer.parseInt(String.valueOf(price.charAt(0))),
								-1,
								-1,
								-1);
					} else if (price.length() == 3) {
						layoutDigit2.setVisibility(View.VISIBLE);
						layoutDigit3.setVisibility(View.VISIBLE);
						cdAnimation.startAnimation(
								Integer.parseInt(String.valueOf(price.charAt(2))),
								Integer.parseInt(String.valueOf(price.charAt(1))),
								Integer.parseInt(String.valueOf(price.charAt(0))),
								-1,
								-1);
					} else if (price.length() == 4) {
						layoutDigit2.setVisibility(View.VISIBLE);
						layoutDigit3.setVisibility(View.VISIBLE);
						layoutDigit4.setVisibility(View.VISIBLE);
						cdAnimation.startAnimation(
								Integer.parseInt(String.valueOf(price.charAt(3))),
								Integer.parseInt(String.valueOf(price.charAt(2))),
								Integer.parseInt(String.valueOf(price.charAt(1))),
								Integer.parseInt(String.valueOf(price.charAt(0))),
								-1);
					} else if (price.length() == 5) {
						layoutDigit2.setVisibility(View.VISIBLE);
						layoutDigit3.setVisibility(View.VISIBLE);
						layoutDigit4.setVisibility(View.VISIBLE);
						layoutDigit5.setVisibility(View.VISIBLE);
						cdAnimation.startAnimation(
								Integer.parseInt(String.valueOf(price.charAt(4))),
								Integer.parseInt(String.valueOf(price.charAt(3))),
								Integer.parseInt(String.valueOf(price.charAt(2))),
								Integer.parseInt(String.valueOf(price.charAt(1))),
								Integer.parseInt(String.valueOf(price.charAt(0))));
					}

					currentPrice = price;
				}

				if(title != null)textViewTopTitle.setText(title);
				textViewTopTitle.setTextColor(Color.WHITE);

				// change menu image
				ImageView imageViewTopMenu = (ImageView) view.findViewById(R.id.imageViewTopMenu);
				imageViewTopMenu.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_menu_white));
				layoutTopBottom.setVisibility(View.GONE);
				// hide driver layout
				this.mapFragment.showDriver(view);


				layoutTripCounter.setOnClickListener(new OnClickListener() {

					@SuppressWarnings("deprecation")
					@SuppressLint("NewApi") @Override
					public void onClick(View v) {
						layoutTripCounter.setEnabled(false);
						if(layoutTripContent.getVisibility() == View.GONE) {
							layoutTripContent.setVisibility(View.VISIBLE);

							imageViewArrow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_up));

							Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alfa_anim_appear);
							layoutTripContent.startAnimation(fadeInAnimation);
							Animation fadeOutAnimation = new AlphaAnimation(1f, 0f);
							fadeOutAnimation.setDuration(300);
							textViewTopContent.startAnimation(fadeOutAnimation);

							if(textViewTopContent.getVisibility() == View.VISIBLE)
								textViewTopContent.setVisibility(View.GONE);

							int Measuredheight;
							Point size = new Point();
							WindowManager w = ((FragmentActivity)mContext).getWindowManager();

							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
								w.getDefaultDisplay().getSize(size);
								Measuredheight = size.y;
							}else{
								Display d = w.getDefaultDisplay();
								Measuredheight = d.getHeight();
							}


							ResizeViewAnimation a = new ResizeViewAnimation(layoutTop);
							a.setDuration(500);
							isTripClicked = true;
							a.setAnimationListener(new AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {

								}

								@Override
								public void onAnimationEnd(Animation animation) {
									layoutTripCounter.setEnabled(true);
								}

								@Override
								public void onAnimationRepeat(Animation animation) {

								}
							});
							LayoutParams layoutParams = layoutTop.getLayoutParams();
							topHeight = layoutParams.height;
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.setTopBarItems : Measuredheight : " + Measuredheight + " : topHeight : " + topHeight);
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.setTopBarItems : topHeight by dp : " + CMApplication.pxToDp(topHeight));

							int addedAnimationSize = 0;
							if(CMApplication.hasNavigationBar(mContext)) {
								addedAnimationSize = mContext.getResources().getInteger(R.integer.int_order_ow_animation_size);
							}
							a.setParams(topHeight, Measuredheight + CMApplication.dpToPx(addedAnimationSize));
							layoutTop.startAnimation(a);

							if(layoutClose.getVisibility() == View.INVISIBLE) {
								layoutClose.setEnabled(true);
								layoutClose.setVisibility(View.VISIBLE);

							}

						} else {
							if(layoutTripContent.getVisibility() == View.VISIBLE) {
								layoutTripContent.setVisibility(View.GONE);
							}
							imageViewArrow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_down));
							// FADE IN/OUT ANIMATION
							Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alfa_anim_appear);
							textViewTopContent.startAnimation(fadeInAnimation);
							Animation fadeOutAnimation = new AlphaAnimation(1f, 0f);
							fadeOutAnimation.setDuration(300);
							layoutTripContent.startAnimation(fadeOutAnimation);
							int Measuredheight;
							Point size = new Point();
							WindowManager w = ((FragmentActivity)mContext).getWindowManager();
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
									layoutTripCounter.setEnabled(true);
									if (textViewTopContent.getVisibility() == View.GONE)
										textViewTopContent.setVisibility(View.VISIBLE);
								}
							});
							a.setParams(Measuredheight, topHeight);
							layoutTop.startAnimation(a);

							if(layoutClose.getVisibility() == View.VISIBLE) {
								layoutClose.setEnabled(false);
								layoutClose.setVisibility(View.INVISIBLE);

							}

							Logger.i(TAG, ":: MapViewFragment : topHeight : " + topHeight + " : Measuredheight : " + Measuredheight);

						}
					}
				});
			}
			break;
			default:
				break;
		}

	} // end method setTopBarItems

	/**
	 * Method for changing slide panel items by orderState
	 *
	 * @param orderState - order state
	 */
	public void changeSlidePanelItems(OrderState orderState) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.changeSlidePanelItems : orderState : " + orderState);
		// This functional changing slide panel items
		switch (orderState) {
			case UNKNOWN:
			case A:
			case R:
			case ASAP_R:
			case LATER_R:
			case LATER_A:
			case ASAP_A_BUSY:
			case ASAP_A_THIS:
			case RC: {
				// slide panel options disable
				((MapViewFragment) ((MainActivity)mContext).getCurrent_fragment()).setSlidePanelItemClickEnabled(false);
			}
				break;
			case OW:
				// slide panel options disable
				((MapViewFragment) ((MainActivity)mContext).getCurrent_fragment()).setSlidePanelItemClickEnabled(false);
				break;
			case CC:
				// slide panel options enable
				((MapViewFragment) ((MainActivity)mContext).getCurrent_fragment()).setSlidePanelItemClickEnabled(true);

				break;
		}


	} // end method changeSlidePanelItems

	/**
	 * Method for starting order animation
	 */
	private void orderStartAnimation(final OrderState nextState) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.orderStartAnimation : nextState : " + nextState);

		clearAnimation = false;
		isTopBarOpened = true;

		((MapViewFragment) ((MainActivity)mContext).getCurrent_fragment()).getLayoutTopMenu().setVisibility(View.VISIBLE);

		textViewCancel.setVisibility(View.VISIBLE);
		layoutTopBottom.setVisibility(View.VISIBLE);
		layoutTop.setVisibility(View.VISIBLE);
		layoutTop.setClickable(true);
		animTopLayout = AnimationUtils.loadAnimation(mContext, R.anim.top_layout_to_bottom_anim);
		layoutTop.setAnimation(animTopLayout);
		layoutTop.bringToFront();
		animTopLayout.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				imageViewTopBarShadow.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		tariff_layout_height = layoutSectionTariff.getHeight();
		ResizeViewAnimation a = new ResizeViewAnimation(layoutSectionTariff);
		a.setDuration(300);
		a.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (CMAppGlobals.DEBUG)
					Logger.i(TAG, ":: OrderStateHelper.onAnimationEnd : nextState : " + nextState);
				if (nextState != null) {
					changeSlidePanelItems(nextState);
				}
				// set order not created
				((MapViewFragment) ((MainActivity)mContext).getCurrent_fragment()).setIsOrderCreated(false);
			}
		});
		a.setParams(layoutSectionTariff.getHeight(), 0);
		layoutSectionTariff.startAnimation(a);



	} // end method orderStartAnimation

	/**
	 * Method for starting top bar gradient animation
	 *
	 * @param view - view layout
	 * @param orderState - current order state
	 */
	public void SetOrderState(final View view, final OrderState orderState,
							  boolean isNotification, String orderId, String price,
							  String bonus, String trip_time, String title, String subTitle,
							  boolean isNewState) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState " + orderState.toString() + ", isNotification : " + isNotification);
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : price " + price + " : subTitle : " + subTitle + " : title : " + title);
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : isNewState : " + isNewState);

		this.price = price;
		IdOrder = orderId;
		// driver section bar
		RelativeLayout layoutSectionDriver = (RelativeLayout)((MainActivity) mContext).findViewById(R.id.layoutSectionDriver);
		if(cdAnimation != null) {
			// STOP TRIP TIMER
			cdAnimation.StopAnimation();
		}
//		// STOP ANIMATIONS
//		StopGradientAnimations();
		// hide driver layout
		mapFragment.hideDriver(rootView);
		// change menu image
		ImageView imageViewTopMenu = (ImageView) view.findViewById(R.id.imageViewTopMenu);
		imageViewTopMenu.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_menu_white));
		if (orderState != OrderState.CP) {
			layoutTopBottom.setVisibility(View.GONE);
			layoutTripCounter.setVisibility(View.GONE);
		}

		// Hide A location icon
		if (mapFragment instanceof MapGoogleFragment) {
			((MapGoogleFragment)mapFragment).showOrHideLocationIcon(false);
		}

		switch (orderState) {
			case ORDER_CONFIRM:
				// start animation
				orderStartAnimation(orderState);
				if(isTopBarOpened) {
					setTopBarItems(view, OrderState.UNKNOWN, subTitle, title);
					// set top background
					ChangeTopLayoutBackground(orderState, isNewState);
					// set items
					setTopBarItems(view, OrderState.ORDER_CONFIRM, subTitle, title);
				}
				break;
			case UNKNOWN:
				orderStartAnimation(orderState);
			break;
			case LATER_R:
				if (isNotification) orderStartAnimation(orderState);
				// Draw pin A on map
				if (mapFragment instanceof MapGoogleFragment) {
					if (!isAddedPinA){
						isAddedPinA = true;
					}
				}

				if(isTopBarOpened) {
					setTopBarItems(view, OrderState.UNKNOWN, subTitle, title);
					// set top background
					ChangeTopLayoutBackground(orderState, isNewState);
					// set items
					setTopBarItems(view, OrderState.LATER_R, subTitle, title);
				}
				break;
			case R:
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState R");
				if (isNotification) orderStartAnimation(orderState);

				// Draw pin A on map
				if (mapFragment instanceof MapGoogleFragment) {
					if (!isAddedPinA){
						isAddedPinA = true;
					}
				}

				if(isTopBarOpened) {
					setTopBarItems(view, OrderState.UNKNOWN, subTitle, title);
					// set top background
					ChangeTopLayoutBackground(orderState, isNewState);
					// set items
					setTopBarItems(view, OrderState.R, subTitle, title);
					// start right animation
					clearAnimation = false;
					StartGradientAnimations();
				}
				break;
			case ASAP_R:
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState ASAP_R");
				if (isNotification) orderStartAnimation(OrderState.ASAP_R);

				// Draw pin A on map
				if (mapFragment instanceof MapGoogleFragment) {
					if (!isAddedPinA){
						isAddedPinA = true;
					}
				}
				if(isTopBarOpened) {
					setTopBarItems(view, OrderState.UNKNOWN, subTitle, title);
					// set top background
					ChangeTopLayoutBackground(orderState, isNewState);
					// set items
					setTopBarItems(view, OrderState.ASAP_R, subTitle, title);
					// start right animation
					clearAnimation = false;
					StartGradientAnimations();
				}
				break;
			case A:
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState A");

				if (isNotification) {
					if(layoutSectionDriver != null)
						layoutSectionDriver.setVisibility(View.VISIBLE);
					orderStartAnimation(OrderState.A);
				}
				if(isTopBarOpened) {
					setTopBarItems(view, OrderState.UNKNOWN, subTitle, title);
					// STOP ANIMATIONS
					StopGradientAnimations();
					// set top background
					ChangeTopLayoutBackground(orderState, isNewState);
					// set items
					setTopBarItems(view, orderState, subTitle, title);
				}
				break;
			case LATER_A:
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState : LATER_A");

				if (isNotification) {
					if(layoutSectionDriver != null)
						layoutSectionDriver.setVisibility(View.VISIBLE);
					orderStartAnimation(OrderState.LATER_A);
				}
				if(isTopBarOpened) {
					setTopBarItems(view, OrderState.UNKNOWN, subTitle, title);
					// STOP ANIMATIONS
					StopGradientAnimations();
					// set top background
					ChangeTopLayoutBackground(orderState, isNewState);
					// set items
					setTopBarItems(view, orderState, subTitle, title);
				}
				break;
			case ASAP_A_BUSY:
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState : ASAP_A_THIS");

				if (isNotification) {
					if(layoutSectionDriver != null)
						layoutSectionDriver.setVisibility(View.VISIBLE);
					orderStartAnimation(OrderState.ASAP_A_BUSY);
				}
				if(isTopBarOpened) {
					setTopBarItems(view, OrderState.UNKNOWN, subTitle, title);
					// STOP ANIMATIONS
					StopGradientAnimations();
					// set top background
					ChangeTopLayoutBackground(orderState, isNewState);
					// set items
					setTopBarItems(view, orderState, subTitle, title);
				}
				break;
			case ASAP_A_THIS:
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState : ASAP_A_BUSY");

				if (isNotification) {
					if(layoutSectionDriver != null)
						layoutSectionDriver.setVisibility(View.VISIBLE);
					orderStartAnimation(OrderState.ASAP_A_THIS);
				}
				if(isTopBarOpened) {
					setTopBarItems(view, OrderState.UNKNOWN, subTitle, title);
					// STOP ANIMATIONS
					StopGradientAnimations();
					// set top background
					ChangeTopLayoutBackground(orderState, isNewState);
					// set items
					setTopBarItems(view, orderState, subTitle, title);
				}
				break;
			case RC:
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState : RC");

				if (isNotification) {
					if(layoutSectionDriver != null)
						layoutSectionDriver.setVisibility(View.VISIBLE);
					orderStartAnimation(OrderState.RC);
				}
				if(isTopBarOpened) {
					setTopBarItems(view, OrderState.UNKNOWN, subTitle, title);
					// STOP ANIMATIONS
					StopGradientAnimations();
					// set top background
					ChangeTopLayoutBackground(orderState, isNewState);
					// set items
					setTopBarItems(view, orderState, subTitle, title);
				}
				break;
			case OW:
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState OW");

				// Closing cancel order dialog
				if (cancelOrderDialog != null)
					cancelOrderDialog.dismiss();
				if (progressDialogHandler != null && progressDialogRunnable != null)
					progressDialogHandler.removeCallbacks(progressDialogRunnable);

				if (isNotification) {
					if(layoutSectionDriver != null)
						layoutSectionDriver.setVisibility(View.VISIBLE);
					orderStartAnimation(OrderState.OW);
				}
				if(isTopBarOpened) {
					setTopBarItems(view, OrderState.UNKNOWN, subTitle, title);
					// STOP ANIMATIONS
					StopGradientAnimations();
					// set top background
					ChangeTopLayoutBackground(orderState, isNewState);
					// set items
					setTopBarItems(view, orderState, subTitle, title);
				}
				break;
			case NC:
			case CC:
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState : CC");
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState : " + orderState +" : orderId : " + orderId
						+ " : price : " + price + " : bonus : " + bonus + " : trip_time : " + trip_time + " : title : " + title);

				// remove active order form active orders list
				if (orderId != null && !orderId.isEmpty()) {
					MenuHelper.removeActiveOrder(orderId);
				}

				if(canceled_order_Id != null && !canceled_order_Id.equals("")
						&& canceled_order_Id.equals(orderId)) {
					canceled_order_Id = null;
				} else {
					if(orderId != null && !orderId.equals("") && title != null && !title.equals("")) {
						// Open rate fragment
						Intent intent = new Intent(mContext, CommonMenuActivity.class);
						intent.putExtra(CMAppGlobals.ACTIVITY_TYPE, ActivityTypes.ORDER_RATE);
						intent.putExtra(CMAppGlobals.EXTRA_ORDER_ID, orderId);
						intent.putExtra(CMAppGlobals.EXTRA_ORDER_STATUS, orderState);
						intent.putExtra(CMAppGlobals.EXTRA_ORDER_TRIP_TITLE, title);

						mContext.startActivity(intent);
						((FragmentActivity)mContext).finish();
					}
				}
				break;
			case AR:
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState : CC");
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState : " + orderState +" : orderId : " + orderId
						+ " : price : " + price + " : bonus : " + bonus + " : trip_time : " + trip_time + " : title : " + title);

				if(orderId != null && !orderId.equals("") && title != null && !title.equals("")) {
					// Open rate fragment
					Intent intent = new Intent(mContext, CommonMenuActivity.class);
					intent.putExtra(CMAppGlobals.ACTIVITY_TYPE, ActivityTypes.ORDER_RATE);
					intent.putExtra(CMAppGlobals.EXTRA_ORDER_ID, orderId);
					intent.putExtra(CMAppGlobals.EXTRA_ORDER_STATUS, orderState);
					if (((MainActivity) mContext).getCurrentOrder() != null)
						intent.putExtra(CMAppGlobals.EXTRA_CARD_ID_HASH, ((MainActivity) mContext).getCurrentOrder().getId_card());
					intent.putExtra(CMAppGlobals.EXTRA_ORDER_TRIP_TITLE, title);

					mContext.startActivity(intent);
					((FragmentActivity)mContext).finish();
				}
				break;
			case CP:
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState CP");
				// STOP ANIMATIONS
				StopGradientAnimations();

				// Remove active order form active orders list
				if (orderId != null && !orderId.isEmpty()) {
					MenuHelper.removeActiveOrder(orderId);
				}
				if (mContext instanceof MainActivity) {
					// Setting default values
					((MainActivity)mContext).setDefaultValuesAfterCancelOrder();
				}
				((MapGoogleFragment)mapFragment).clearMap();

				if(cdAnimation != null) {
					// STOP TRIP TIMER
					cdAnimation.StopAnimation();
				}

				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.SetOrderState : orderState CP : orderId : " + orderId
										+ " : price : " + price + " : bonus : " + bonus + " : trip_time : " + trip_time);

				if(orderId != null && !orderId.equals("")
//					&& price != null && !price.equals("")
					) {
					// Open Rate Fragment
					Intent intent = new Intent(mContext, CommonMenuActivity.class);
					intent.putExtra(CMAppGlobals.ACTIVITY_TYPE, ActivityTypes.ORDER_RATE);
					intent.putExtra(CMAppGlobals.EXTRA_ORDER_ID, orderId);
					intent.putExtra(CMAppGlobals.EXTRA_ORDER_PRICE, price);
					intent.putExtra(CMAppGlobals.EXTRA_ORDER_BONUS, bonus);
					intent.putExtra(CMAppGlobals.EXTRA_ORDER_TRIP_TIME, trip_time);

					mContext.startActivity(intent);
					((FragmentActivity)mContext).finish();
				}
				break;
			default:
				break;
		}

	} // end method SetOrderState

	public static String canceled_order_Id = null;


	/**
	 * Method for clearing order
	 * 			left/right animations
	 */
	public void StopGradientAnimations() {
		clearAnimation = true;

		isGradientAnimtaionStarted = false;

		// STOP ANIMATIONS
		if(orderLeftAnimation != null && orderLeftAnimation.isInitialized()) {
			orderLeftAnimation.cancel();
			imageViewLoadingLeft.clearAnimation();
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.ChangeOrder : imageViewLoadingLeft.clearAnimation()");
		}
		if(orderRightAnimation != null && orderRightAnimation.isInitialized()) {
			orderRightAnimation.cancel();
			imageViewLoadingRight.clearAnimation();
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MapViewFragment.ChangeOrder : imageViewLoadingRight.clearAnimation()");
		}

		imageViewLoadingRight.setVisibility(View.GONE);
		imageViewLoadingLeft.setVisibility(View.GONE);

	} // end method stopOrderAnimations

	private boolean isGradientAnimtaionStarted = false;

	/**
	 * Method for starting order
	 * 			left/right animations
	 */
	public void StartGradientAnimations() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.StartGradientAnimations : isGradientAnimtaionStarted : " + isGradientAnimtaionStarted);
		if(!isGradientAnimtaionStarted) {
			isGradientAnimtaionStarted = true;
			OrderRightAnimation();
		}

	} // end method StartGradientAnimations

	/**
	 * Method for starting NewOrderData bar LEFT animation
	 */
	public void OrderLeftAnimation() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.OrderLeftAnimation ");

		imageViewLoadingRight.setVisibility(View.GONE);
		imageViewLoadingLeft.setVisibility(View.VISIBLE);
		orderLeftAnimation = AnimationUtils.loadAnimation(mContext, R.anim.image_anim_left);
		orderLeftAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				imageViewLoadingLeft.clearAnimation();
				if(!clearAnimation) {
					OrderRightAnimation();
				}
			}
		});
		imageViewLoadingLeft.setAnimation(orderLeftAnimation);
		imageViewLoadingLeft.startAnimation(orderLeftAnimation);

	} // end method orderLeftAnimation



	/**
	 * Method for starting NewOrderData bar RIGHT animation
	 */
	public void OrderRightAnimation() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.OrderRightAnimation ");

		// CHECK if Left animation is started
		imageViewLoadingLeft.setVisibility(View.GONE);
		imageViewLoadingRight.setVisibility(View.VISIBLE);
		orderRightAnimation = AnimationUtils.loadAnimation(mContext, R.anim.image_anim_right);
		orderRightAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.OrderRightAnimation : onAnimationStart");

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				imageViewLoadingRight.clearAnimation();
				if(!clearAnimation) {
					OrderLeftAnimation();
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.OrderRightAnimation : onAnimationEnd");
				}
			}
		});
		imageViewLoadingRight.setAnimation(orderRightAnimation);
		imageViewLoadingRight.startAnimation(orderRightAnimation);
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.OrderRightAnimation : startAnimation");

	} // end method orderRightAnimation

	public void setPanelState(boolean isSlidePanelOpened2) {
		this.isSlidePanelOpened = isSlidePanelOpened2;
	}

	/**
	 * Method for updating UI by order status data
	 *
	 * @param rootView - root view layout
	 * @param orderStatusData - order status data
     */
	public void updateUIByOrderStatusData(View rootView, OrderStatusData orderStatusData) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.updateUIByOrderStatusData : orderStatusData : " + orderStatusData);
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.updateUIByOrderStatusData : orderStatusData : orderId : " + orderStatusData.getIdOrder());
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.updateUIByOrderStatusData : orderStatusData : status : " + orderStatusData.getStatus());
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.updateUIByOrderStatusData : orderStatusData : price : " + orderStatusData.getPrice());
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.updateUIByOrderStatusData : orderStatusData : bonus : " + orderStatusData.getBonus());
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.updateUIByOrderStatusData : orderStatusData : trip_time : " + orderStatusData.getTripTime());

		// Hiding layout item for scroll
		mapViewFragment.showOrHideLayoutItemForScroll(true);

		// Change Panel Bar height
		int oneFieldHeight = mContext.getResources().getDimensionPixelSize(R.dimen.slide_panel_height_one_field);
		mSlidingUpPanelLayout.setPanelHeight(oneFieldHeight);

		((MapViewFragment) ((MainActivity)mContext).getCurrent_fragment()).changeSlidePanelContentHeight(false);
		String subTitle = orderStatusData.getSubTitle();
		if (OrderState.getStateByStatus(orderStatusData.getStatus()) == OrderState.ASAP_A_BUSY
				|| OrderState.getStateByStatus(orderStatusData.getStatus()) == OrderState.ASAP_A_THIS) {

			subTitle = mContext.getResources().getString(R.string.str_driver_time_calc);
		} else if (OrderState.getStateByStatus(orderStatusData.getStatus()) == OrderState.LATER_A) {
			subTitle = mContext.getResources().getString(R.string.str_order_registered_content);
			subTitle = subTitle.replace("{0}", mContext.getResources().getString(R.string.str_minute_registered));
		}

		if (OrderState.getStateByStatus(orderStatusData.getStatus()) == OrderState.RC
				&& orderStatusData.getCameout() > 0) {
			isCameOut = true;
		}

		// top visible
		if(isTopBarOpened) {
			SetOrderState(rootView, OrderState.getStateByStatus(orderStatusData.getStatus()), false, orderStatusData.getIdOrder(), orderStatusData.getPrice(), "", "", orderStatusData.getTitle(), subTitle, true);
			changeSlidePanelItems(OrderState.getStateByStatus(orderStatusData.getStatus()));
		} else {
			SetOrderState(rootView, OrderState.getStateByStatus(orderStatusData.getStatus()), true, orderStatusData.getIdOrder(), orderStatusData.getPrice(), "", "", orderStatusData.getTitle(), subTitle, true);
		}

		// Add Additional Fields And Services when order status is OW
		if (OrderState.getStateByStatus(orderStatusData.getStatus()) == OrderState.OW) {
			addAdditionalFieldsAndServices(orderStatusData);
		}

		// Hide A location icon
		if (mapFragment instanceof MapGoogleFragment) {
			((MapGoogleFragment)mapFragment).showOrHideLocationIcon(false);
		}

	} // end method updateUIByOrderStatusData

	/**
	 * Method for setting top layout's subtitle
	 * @param text - sub title text
	 */
	public void setSubTitle(String text) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.setSubTitle : text : " + text);

		textViewTopContent.setText(Html.fromHtml(text));

	} // end method setSubTitle

	/**
	 * Method for getting top layout's subtitle
	 * @return - sub title text
     */
	public String getSubTitle() {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.getSubTitle ");

		if(textViewTopContent != null)
			return textViewTopContent.getText().toString();

		return null;

	} // end method setSubTitle

	/**
	 * Method for setting top layout's title
	 * @param text - title text
	 */
	public void setTitle(String text) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.setTitle : text : " + text);

		textViewTopTitle.setText(Html.fromHtml(text));

	} // end method setTitle


	/**
	 * Method for checking order validation
	 * @param orderId - order id
	 */
	public static boolean isOrderValid(String orderId) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStateHelper.isOrderValid : orderId : " + orderId);

		return orderId != null && !orderId.equals("");

	} // end method isOrderValid

	/**
	 * Method for adding Additional Fields And Services
	 *
	 * @param orderStatusData - order status data
	 */
	public void addAdditionalFieldsAndServices (OrderStatusData orderStatusData) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStateHelper.addAdditionalFieldsAndServices : orderStatusData : " + orderStatusData);

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

	} // end method addAdditionalFieldsAndServices


	/**
	 * Method for setting driverPhoneNumber
	 *
	 * @param driverPhoneNumber - driver phone number
	 */
	public void setDriverPhoneNumber(String driverPhoneNumber) {
		this.driverPhoneNumber = driverPhoneNumber;

	} // end method setDriverPhoneNumber

	/**
	 * Method for getting textViewGoToDriver
	 * @return textViewGoToDriver
	 */
	public TextView getTextViewGoToDriver() {
		return textViewGoToDriver;

	} // end method getTextViewGoToDriver

	/**
	 * Method for setting isCameOut
	 *
	 * @param isCameOut - is came out
	 */
	public void setIsCameOut(boolean isCameOut) {
		this.isCameOut = isCameOut;

	} // end method setIsCameOut

	/**
	 * Method for canceling order
	 */
	public void cancelOrder(boolean cancelOrder) {

		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MapViewFragment.CancelOrder : orderId : " + CMApplication.getTrackingOrderId());

		// Showing layout item for scroll
		mapViewFragment.getLayoutItemForScroll().setVisibility(View.VISIBLE);

		// stop gradient animations
		isGradientAnimtaionStarted = false;

		// STOP ORDER STATUS SERVICE
		((MainActivity)mContext).stopOrderStatusService();

		// set has action
		mapViewFragment.setHasAction(false);

		// set Map padding
		MapHelper.setMapPadding(mContext, mapViewFragment, true);

		((MapViewFragment) ((MainActivity)mContext).getCurrent_fragment()).getLayoutTopMenu().setVisibility(View.GONE);

		if (cancelOrder && !CMApplication.getTrackingOrderId().equals("")) {
			// Cancel Order
			OrderManager orderManager = new OrderManager((ICallBack) mContext);
			orderManager.CancelOrder(CMApplication.getTrackingOrderId());

			if (((MainActivity)mContext).getUpdateDriverHandler() != null) {
				// driver handler
				((MainActivity)mContext).getUpdateDriverHandler().postDelayed(((MainActivity)mContext).getUpdateDriverRunnable(), 10);
			}
		}

		textViewConfirmOrder.setEnabled(true);

		imageViewTopBarShadow.setVisibility(View.GONE);

		isAddedPinA = false;


		// Show A location icon
		((MapGoogleFragment)mapFragment).showOrHideLocationIcon(true);
		// Clear map
		((MapGoogleFragment)mapFragment).clearMap();
		// Setting default values
		((MainActivity)mContext).setDefaultValuesAfterCancelOrder();

		isTopBarOpened = false;

		// order created false
		((MapViewFragment) ((MainActivity)mContext).getCurrent_fragment()).setIsOrderCreated(false);
		// set order comment
		String orderComment = mContext.getResources().getString(R.string.str_comment_title);
		((MapViewFragment) ((MainActivity)mContext).getCurrent_fragment()).setOrderComment(orderComment, "");

		// slide panel options
		((MapViewFragment) ((MainActivity) mContext).getCurrent_fragment()).setSlidePanelItemClickEnabled(true);

		// STOP ANIMATIONS
		StopGradientAnimations();

		// map buttons
		mapFragment.showControls(rootView);
		// hide driver layout
		mapFragment.hideDriver(rootView);
		// hide confirmation page
		((MapViewFragment) ((MainActivity) mContext).getCurrent_fragment()).hideOrderConfirmPage();

		((MapGoogleFragment)mapFragment).setmMapIsTouched(false);


		// CLOSE TOP LAYOUT
		animTopLayout = AnimationUtils.loadAnimation(mContext, R.anim.top_layout_to_top_anim);
		animTopLayout.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				textViewTopTitle.setTextColor(Color.WHITE);
				textViewTopContent.setTextColor(Color.WHITE);
				// map buttons
				mapFragment.showControls(rootView);
				layoutTop.setClickable(false);
				layoutTop.setVisibility(View.GONE);
				textViewCancel.setVisibility(View.GONE);

				layoutTopBottom.setVisibility(View.GONE);

			}
		});
		layoutTop.startAnimation(animTopLayout);

		translateTopLayout = new TranslateAnimation(0, 0, (layoutTop.getHeight()), 0);
		translateTopLayout.setDuration(300);

		ResizeViewAnimation a = new ResizeViewAnimation(layoutSectionTariff);
		a.setDuration(300);
		a.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// set from address field visible
				((MapViewFragment) ((MainActivity) mContext).getCurrent_fragment()).setFromItemVisible(true);
			}
		});
		a.setParams(0, tariff_layout_height);
		layoutSectionTariff.startAnimation(a);

		// enable slide panel
		mSlidingUpPanelLayout.setSlidingEnabled(true);

		int panelHeight = mContext.getResources().getDimensionPixelSize(R.dimen.slide_panel_height);
		mSlidingUpPanelLayout.setPanelHeight(panelHeight);
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				mSlidingUpPanelLayout.collapsePane();
			}
		});

	} // end method cancelOrder

	/**
	 * Method for getting text View Cancel
	 * @return - text view cancel
     */
	public TextView getTextViewCancel() {
		return textViewCancel;

	} // end method getTextViewCancel
}
