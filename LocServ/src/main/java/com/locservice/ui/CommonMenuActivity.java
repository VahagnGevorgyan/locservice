package com.locservice.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.ChatAddCommentData;
import com.locservice.api.entities.GetOrderInfoData;
import com.locservice.api.entities.Order;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.manager.ClientManager;
import com.locservice.api.manager.OrderManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.gcm.GCMUtils;
import com.locservice.protocol.ICallBack;
import com.locservice.service.OrderStatusResultReceiver;
import com.locservice.ui.fragments.OrderRateFragment;
import com.locservice.ui.helpers.FragmentHelper;
import com.locservice.ui.helpers.MenuHelper;
import com.locservice.ui.helpers.OrderState;
import com.locservice.ui.listeners.MenuDrawerListener;
import com.locservice.ui.utils.ActivityTypes;
import com.locservice.ui.utils.FragmentTypes;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;
import com.locservice.utils.PermissionUtils;
import com.locservice.utils.Utils;

import java.util.List;



public class CommonMenuActivity extends BaseActivity implements MenuDrawerListener,
		ICallBack,
		OrderStatusResultReceiver.OnOrderStatusResultListener{

	private static final String TAG = CommonMenuActivity.class.getSimpleName();
	private ActivityTypes activityType;
	private Bitmap avatarBitmap;
	private Fragment current_fragment;
	private OrderManager orderManager;
	private int checkPayCount = 0;

	protected final OrderStatusResultReceiver orderStatusResultReceiver = new OrderStatusResultReceiver();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_common_menu);
		
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onCreate ");

		// check network status
		if (Utils.checkNetworkStatus(this)) {

			orderManager = new OrderManager(this);

			setActivityByType();

			// set menu listeners
			MenuHelper.getInstance().SetMenuListeners(this);
			String rewards = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BONUS.key(), "");
			if (!rewards.isEmpty()) {
				MenuHelper.getInstance().setRewards(CommonMenuActivity.this, rewards);
			}
			String sale = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_SALE.key(), "");
			if (!sale.isEmpty() && !sale.equals("0")) {
				MenuHelper.getInstance().showSale(true);
				String sateText = sale + " " + getResources().getString(R.string.str_sale);
				MenuHelper.getInstance().setSaleText(sateText);
			} else {
				MenuHelper.getInstance().showSale(false);
			}
		}

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			Bitmap imageBitmap = null;
			if (requestCode == CMAppGlobals.REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK && data != null) {
				ClientManager clientManager = new ClientManager(this);
		        Bundle extras = data.getExtras();
		        imageBitmap = (Bitmap) extras.get("data");
				clientManager.UpdateClientInfo(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), "Name"),
						LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_EMAIL.key(), "Email"),
						LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.PROFILE_SMS_NOTIFICATION.key(), 0),
						CMApplication.encodeBitmapToBase64(imageBitmap), 0);
		    }
			if (requestCode == CMAppGlobals.REQUEST_LOAD_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
				ClientManager clientManager = new ClientManager(this);
                final Uri imageUri = data.getData();
				imageBitmap = CMApplication.decodeSampledBitmap(CommonMenuActivity.this, imageUri, 100, 100);
				clientManager.UpdateClientInfo(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), "Name"),
						LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_EMAIL.key(), "Email"),
						LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.PROFILE_SMS_NOTIFICATION.key(), 0),
								CMApplication.encodeBitmapToBase64(imageBitmap), 0);
        	}
			if (requestCode == CMAppGlobals.REQUEST_CHANGE_PROFILE_INFO && resultCode == RESULT_OK){
				imageBitmap = CMApplication
						.decodeBase64ToBitmap(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BASE_64_IMAGE.key(), ""));
				if (imageBitmap != null){
					MenuHelper.getInstance().SetProfileImage(imageBitmap);
				}
				String name = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), "");

				MenuHelper.getInstance().setProfileName(CommonMenuActivity.this, name);

				String rewards = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BONUS.key(), "");
				if (!rewards.isEmpty()) {
					MenuHelper.getInstance().setRewards(CommonMenuActivity.this, rewards);
				}
			}
			if (requestCode == CMAppGlobals.REQUEST_ORDER_LIST && resultCode == Activity.RESULT_OK && data != null) {
				String IdOrder = data.getExtras().getString(CMAppGlobals.EXTRA_ACTIVE_ORDER_ID);

				if (IdOrder != null) {
					List<OrderStatusData> activeOrdersList = MenuHelper.getActiveOrdersList();
					int activeOrderPosition = -1;
					for (int i = 0; i < activeOrdersList.size(); i++) {
						if (IdOrder.equals(activeOrdersList.get(i).getIdOrder())){
							activeOrderPosition = i;
							break;
						}
					}
					if (activeOrderPosition != -1) {
						MenuHelper.getInstance().activeOrdersOnItemClick(this, null, activeOrderPosition, activeOrdersList.get(activeOrderPosition));
					}

				}
			}
			avatarBitmap = imageBitmap;
        }  catch (Exception e) {
        	e.printStackTrace();
        }
	}

	@Override
	protected void onResume() {
		super.onResume();
		// set order status receiver
		this.orderStatusResultReceiver.setListener(this);
		// filter for BroadcastReceiver
		IntentFilter intFilt = new IntentFilter(GCMUtils.UPDATE_ORDER_STATUS);
		// register BroadcastReceiver
		registerReceiver(this.orderStatusResultReceiver, intFilt);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// clear receiver
		this.orderStatusResultReceiver.clearListener();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unregister BroadcastReceiver
		unregisterReceiver(this.orderStatusResultReceiver);
	}

	/**
	 * Method for setting activity by type
	 */
	public void setActivityByType() {
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			if(extras.containsKey(CMAppGlobals.ACTIVITY_TYPE)) {
				activityType = (ActivityTypes) extras.get(CMAppGlobals.ACTIVITY_TYPE);
				if (activityType != null) {
					switch (activityType) {
                    case ORDER_INFO:
                        // set order info fragment
                        current_fragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.ORDER_INFO, extras);
                        break;
                    case ORDER_RATE:
                        // set order rate fragment
                        current_fragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.ORDER_RATE, extras);
                        break;
                    default:
                        break;
                    }
				}
			}
		}
		
	} // end method setActivityByType

	@Override
	public void OpenMenuDrawer(FragmentActivity context) {
		MenuHelper.getInstance().openDrawer(context, Gravity.LEFT);
	}

	@Override
	public void CloseMenuDrawer(FragmentActivity context) {
		MenuHelper.getInstance().closeDrawer(context, Gravity.LEFT);
	}
	
	@Override
	public void onBackPressed() {

	   if(activityType != ActivityTypes.ORDER_RATE) {
		   super.onBackPressed();
	   } 
	}

	@Override
	public void onFailure(Throwable error, int requestType) {

	}

	@Override
	public void onSuccess(Object response, int requestType) {
		if (response != null) {
			switch (requestType) {
				case RequestTypes.REQUEST_UPDATE_CLIENT_INFO:
					if(!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_UPDATE_CLIENT_INFO")) {
						if (response instanceof ResultData) {

							ResultData resultData = (ResultData) response;
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CommonMenuActivity.onSuccess REQUEST_UPDATE_CLIENT_INFO resultData : " + resultData);
							if (resultData.getResult().equals("1")) {
								MenuHelper.getInstance().SetProfileImage(avatarBitmap);
								LocServicePreferences.getAppSettings()
										.setSetting(LocServicePreferences.Settings.PROFILE_BASE_64_IMAGE, CMApplication.encodeBitmapToBase64(avatarBitmap));
							}else {
								Toast.makeText(CommonMenuActivity.this, R.string.alert_update_client_info, Toast.LENGTH_SHORT).show();
							}
						}
					}
					break;
				case RequestTypes.REQUEST_ADD_COMMENT:
					if(!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_ADD_COMMENT")) {
						if (response instanceof ChatAddCommentData) {

							ChatAddCommentData orderCommentData = (ChatAddCommentData) response;
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CommonMenuActivity.onSuccess REQUEST_ADD_COMMENT orderCommentData : " + orderCommentData);
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CommonMenuActivity.onSuccess REQUEST_ADD_COMMENT result : " + orderCommentData.getResult());

							if (orderCommentData.getResult() == 1) {
								if (current_fragment instanceof OrderRateFragment) {
									((OrderRateFragment)current_fragment).hideFeedbackContent();
//								Toast.makeText(CommonMenuActivity.this, orderCommentData.getMsg(), Toast.LENGTH_SHORT).show();
								}
							} else {
								Toast.makeText(CommonMenuActivity.this, R.string.alert_add_order_comment, Toast.LENGTH_SHORT).show();
							}
						}
					}
					break;
				case RequestTypes.REQUEST_SET_ORDER_RATE:
					if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CommonMenuActivity.onSuccess REQUEST_SET_ORDER_RATE response : " + response);

					if(!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_SET_ORDER_RATE")) {
						if (response instanceof ResultData) {

							ResultData resultData = (ResultData) response;
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CommonMenuActivity.onSuccess REQUEST_SET_ORDER_RATE resultData : " + resultData);

							if (resultData.getResult().equals("1")) {
								Intent intent = new Intent(CommonMenuActivity.this, MainActivity.class);
								finish();
								startActivity(intent);
							}
						}

					}
					break;
				case RequestTypes.REQUEST_GET_ORDER_STATUS_LIST:
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onSuccess : REQUEST_GET_ORDER_STATUS_LIST : response : " + response);

					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDER_STATUS_LIST")) {
						if (response instanceof List) {
							//noinspection unchecked
							List<OrderStatusData> orderStatusDataList = (List<OrderStatusData>) response;
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onSuccess : REQUEST_GET_ORDER_STATUS_LIST : orderStatusDataList : " + orderStatusDataList);
							if (!orderStatusDataList.isEmpty() && current_fragment != null) {
								((OrderRateFragment)current_fragment).addAdditionalFieldsAndServices(orderStatusDataList.get(0));
							}
						}
					}
					break;
				case RequestTypes.REQUEST_PAY_ORDER:
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onSuccess : REQUEST_PAY_ORDER : response : " + response);
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_PAY_ORDER")) {
						if (response instanceof ResultData) {

							ResultData resultData = (ResultData) response;

							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onSuccess : REQUEST_PAY_ORDER : result : " + resultData.getResult());
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onSuccess : REQUEST_PAY_ORDER : orderID : " + ((OrderRateFragment)current_fragment).getOrderId());

							if (resultData.getResult().equals("1")) {
								orderManager.CheckOrderPay(((OrderRateFragment)current_fragment).getOrderId());
							}
						}
					}
					break;
				case RequestTypes.REQUEST_GET_ORDERS_INFO:
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onSuccess : REQUEST_GET_ORDERS_INFO : response : " + response);
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_ORDERS_INFO")) {
						if (response instanceof GetOrderInfoData) {

							GetOrderInfoData orderInfoData = (GetOrderInfoData) response;
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CommonMenuActivity.onSuccess : REQUEST_GET_ORDERS_INFO : orderInfoData : " + orderInfoData);

							List<Order> orderList = orderInfoData.getOrders();
							if (orderList != null && !orderList.isEmpty()) {
								Order order = orderList.get(0);
								OrderManager orderManager = new OrderManager(this);
								orderManager.PayOrder(order.getIdOrder(), order.getId_card());
							}
						}
					}

					break;
				case RequestTypes.REQUEST_CHECK_ORDER_PAY:
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onSuccess : REQUEST_CHECK_ORDER_PAY : response : " + response);
					if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_CHECK_ORDER_PAY")) {
						if (response instanceof ResultData) {

							ResultData resultData = (ResultData) response;
							if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onSuccess : REQUEST_CHECK_ORDER_PAY : result : " + resultData.getResult());

							if (resultData.getResult().equals("1")) {
								((OrderRateFragment)current_fragment).hideOrderInfo(false);
								orderManager.GetOrderStatusList(((OrderRateFragment)current_fragment).getOrderId());
							} else {
								if (checkPayCount < 20) {
									new Handler().postDelayed(new Runnable() {
										@Override
										public void run() {
											orderManager.CheckOrderPay(((OrderRateFragment)current_fragment).getOrderId());
										}
									}, 5000);
									checkPayCount++;
								}
							}
						}
					}
					break;
			}
		}
	}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PermissionUtils.PERMISSION_REQUEST_CAMERA:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, CMAppGlobals.REQUEST_CAPTURE_IMAGE);
                    }
                    return;
                }

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        View view = findViewById(R.id.layout_common_menu);
						if (view != null) {
							Snackbar.make(view, getString(R.string.str_error_activate_permission), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.settings), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                            settingsIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivity(settingsIntent);
                                        }
                                    })
                                    .show();
						}
						return;
                    }
                }
                break;
            case PermissionUtils.READ_EXTERNAL_STORAGE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, CMAppGlobals.REQUEST_LOAD_FROM_GALLERY);
                    return;
                }

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        View view = findViewById(R.id.layout_common_menu);
						if (view != null) {
							Snackbar.make(view, getString(R.string.str_error_activate_permission), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.settings), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                            settingsIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivity(settingsIntent);
                                        }
                                    })
                                    .show();
						}
						return;
                    }
                }
                break;
        }
    }

	@Override
	public void onReceiveOrderStatusResult(Context context, Intent intent) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onReceiveOrderStatusResult : context : " + context + " : intent : " + intent);

		if(intent != null) {
			List<OrderStatusData> orders = intent.getParcelableArrayListExtra(GCMUtils.EXTRA_ORDER_LIST);
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onReceiveOrderStatusResult : orders : " + orders);

			if (orders != null && !orders.isEmpty()) {
				for (OrderStatusData item : orders) {
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onReceiveOrderStatusResult : id : item.getIdOrder : " + item.getIdOrder());
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onReceiveOrderStatusResult : id : CMApplication.getTrackingOrderId : " + CMApplication.getTrackingOrderId());
					if(item.getIdOrder().equals(CMApplication.getTrackingOrderId())) {
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonMenuActivity.onReceiveOrderStatusResult : orderId : " + item.getIdOrder()
								+ " : status : " + item.getStatus()
								+ " : trip_time : " + item.getTripTime()
								+ " : currentPrice : " + item.getPrice()
								+ " : bonus : " + item.getBonus()
								+ " : title : " + item.getTitle()
								+ " : subTitle : " + item.getSubTitle());

						if (OrderState.getStateByStatus(item.getStatus()) == OrderState.CP) {
							((OrderRateFragment)current_fragment).hideOrderInfo(false);
							if (current_fragment != null) {
								((OrderRateFragment)current_fragment).addAdditionalFieldsAndServices(item);
							}
							// remove order from active orders
							CMApplication.removeActiveOrder(item.getIdOrder());
							MenuHelper.removeActiveOrder(item.getIdOrder());
						}

					}
				}

			}
		}
	}
}
