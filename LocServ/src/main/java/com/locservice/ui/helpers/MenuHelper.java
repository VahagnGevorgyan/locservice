package com.locservice.ui.helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.adapters.ActiveOrdersAdapter;
import com.locservice.adapters.MenuListAdapter;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.application.LocServicePreferences;
import com.locservice.ui.MainActivity;
import com.locservice.ui.ProfileActivity;
import com.locservice.ui.RegisterActivity;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.utils.ActivityTypes;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.Logger;
import com.locservice.utils.PermissionUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.lang.reflect.Constructor;
import java.util.List;

//import com.wnafee.vector.MorphButton;

public class MenuHelper implements ActiveOrdersAdapter.OnItemClickListener {

	private static final String TAG = MenuHelper.class.getSimpleName();

	private static MenuHelper mInstance = null;

	private ListView listViewMenuItems;

	private String[] mMenuItems;

	private LinearLayout layoutSettings;


	private DrawerLayout mDrawerLayout;

	private LinearLayout layoutActiveOrders;
	private boolean mDrawerOpen = false;
	private RelativeLayout layoutLeftDrawer;
	private RecyclerView recyclerViewActiveOrders;
	private ActiveOrdersAdapter activeOrdersAdapter;
	private static List<OrderStatusData> activeOrdersList;
	private CustomTextView textViewFullName;
	private CustomTextView textViewRewards;
	private int lastActiveOrdersListSize = 0;
	private boolean isAnimated = false;
	private TextView textViewSale;


	public static MenuHelper getInstance() {
		if(mInstance == null)
			mInstance = new MenuHelper();
		return mInstance;
	}

	/**
	 * {@link Constructor}
	 */
	private MenuHelper() {
	}


	public void lockDrawer(AppCompatActivity context){
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}

	public void unlockDrawer(AppCompatActivity context){
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	}

	/**
	 * Method for setting menu listeners
	 */
	public void SetMenuListeners(final AppCompatActivity context) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.SetMenuListeners : context : " + context.getLocalClassName());
		// menu layout
		layoutLeftDrawer = (RelativeLayout) context.findViewById(R.id.layoutMenu);
		mDrawerLayout = (DrawerLayout) context.findViewById(R.id.drawerLayout);
		mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onDrawerOpened(View arg0) {
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.setDrawerListener : onDrawerOpened");
				layoutActiveOrders = (LinearLayout) context.findViewById(R.id.layoutActiveOrders);
				recyclerViewActiveOrders = (RecyclerView) context.findViewById(R.id.recyclerViewActiveOrders);

				mDrawerOpen = true;
				layoutActiveOrders.setVisibility(View.VISIBLE);
				recyclerViewActiveOrders.setVisibility(View.VISIBLE);

				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.setDrawerListener : activeOrdersList : " + activeOrdersList);
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.setDrawerListener : static active_orders : " + CMApplication.getActiveOrders());
				if(activeOrdersList != null && activeOrdersList.size() > 0) {
					addActiveOrders(context, activeOrdersList);
					startActiveOrdersAnimation(context);
				}
			}

			@Override
			public void onDrawerClosed(View arg0) {
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.setDrawerListener : onDrawerClosed");

				layoutActiveOrders = (LinearLayout) context.findViewById(R.id.layoutActiveOrders);
				recyclerViewActiveOrders = (RecyclerView) context.findViewById(R.id.recyclerViewActiveOrders);

				mDrawerOpen = false;
				isAnimated = false;
				Animation anim = null;
				anim = AnimationUtils.loadAnimation(context, R.anim.top_green_bar_to_top);
				anim.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						layoutActiveOrders.setVisibility(View.GONE);
						recyclerViewActiveOrders.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
				layoutActiveOrders.startAnimation(anim);

				LinearLayout layoutTop = (LinearLayout) context.findViewById(R.id.layoutMenuTop);
				if (layoutTop != null) {
					layoutTop.setPadding(0, 0, 0, 0);
				}
			}
		});

		listViewMenuItems = (ListView) context.findViewById(R.id.listViewMenuItems);
		mMenuItems = context.getResources().getStringArray(R.array.menu_items);
		listViewMenuItems.setAdapter(MenuListAdapter.getInstance(context, R.layout.list_menu_item, mMenuItems));

		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MenuHelper.SetMenuListeners : listView.height : " + CMApplication.getHeightOfListView(listViewMenuItems));
		// Setting listView height
		ViewGroup.LayoutParams params = listViewMenuItems.getLayoutParams();
		params.height = CMApplication.getHeightOfListView(listViewMenuItems);
		listViewMenuItems.setLayoutParams(params);
		listViewMenuItems.requestLayout();

		layoutSettings = (LinearLayout) context.findViewById(R.id.layoutSettings);
		layoutSettings.setOnTouchListener(new OnTouchListener() {
			ImageView imageSettings = (ImageView) layoutSettings.findViewById(R.id.imageViewSettings);
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return CommonHelper.setOnTouchImage(imageSettings, event);
			}
		});
		layoutSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ProfileActivity.class);
				intent.putExtra(CMAppGlobals.ACTIVITY_TYPE, ActivityTypes.PROFILE);
				context.startActivityForResult(intent, CMAppGlobals.REQUEST_CHANGE_PROFILE_INFO);
			}
		});
		// set profile photo listeners
		SetPhotoListeners(context);
		// set profile image
		if (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BASE_64_IMAGE.key(), null) != null){
			Bitmap avatarBitmap = CMApplication
					.decodeBase64ToBitmap(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BASE_64_IMAGE.key(), null));
			if (avatarBitmap != null) {
				SetProfileImage(avatarBitmap);
			}
		}

		textViewFullName = (CustomTextView) mDrawerLayout.findViewById(R.id.textViewFullName);
		String auth_token = (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), ""));
		if (auth_token.isEmpty()) {
			textViewFullName.setText(R.string.str_enter);
			textViewFullName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					context.startActivityForResult(new Intent(context, RegisterActivity.class), CMAppGlobals.REQUEST_REGISTER);
				}
			});
		} else {
			String fullName = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), "");
			setProfileName(context, fullName);
		}

		textViewSale = (TextView) context.findViewById(R.id.textViewSale);

	} // end method SetMenuListeners

	/**
	 * Method for showing sale
	 * @param isShow - if true show else not show
     */
	public void showSale(boolean isShow) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MenuHelper.showDiscount : isShow : " + isShow);

		if (textViewSale != null) {
			if (isShow) {
				textViewSale.setVisibility(View.VISIBLE);
			} else {
				textViewSale.setVisibility(View.GONE);
			}
		}

	} // end method showDiscount

	/**
	 * Method for setting discount text
	 * @param sale - discount
	 */
	public void setSaleText(String sale) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MenuHelper.setDiscountText : sale : " + sale);

		if (textViewSale != null) {
			textViewSale.setText(sale);
		}

	} // end method setDiscountText

	/**
	 * Method for setting profile name
	 * @param context - current context
	 * @param name - profile name
	 */
	public void setProfileName(final AppCompatActivity context, String name) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MenuHelper.setProfileName : name : " + name);

		if (mDrawerLayout != null) {
			textViewFullName = (CustomTextView) mDrawerLayout.findViewById(R.id.textViewFullName);
			if (name != null) {
				String auth_token = (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), ""));
				if (!auth_token.isEmpty()) {
					if (name.isEmpty()) {
						textViewFullName.setText(R.string.str_name);
					} else {
						textViewFullName.setText(name);
					}
					textViewFullName.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(context, ProfileActivity.class);
							intent.putExtra(CMAppGlobals.ACTIVITY_TYPE, ActivityTypes.PROFILE);
							context.startActivityForResult(intent, CMAppGlobals.REQUEST_CHANGE_PROFILE_INFO);
						}
					});
				}

			}

		}

	} // end method setProfileName

	/**
	 * Method for setting profile rewards
	 * @param rewards - rewards
	 */
	public void setRewards (Context context, String rewards) {
		String auth_token = (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), ""));
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MenuHelper.setRewards : name : " + rewards + " : auth_token : " + auth_token);

		textViewRewards = (CustomTextView) mDrawerLayout.findViewById(R.id.textViewRewards);
		if (auth_token.isEmpty()) {
			textViewRewards.setVisibility(View.GONE);
		} else {
			textViewRewards.setVisibility(View.VISIBLE);
		}
		if (rewards != null && !rewards.isEmpty()) {
			textViewRewards.setText(rewards + context.getResources().getString(R.string.str_menu_rewards));
		}

	} // end method setProfileName

	/**
	 * Method for setting profile image
	 */
	public void SetProfileImage(Bitmap imageBitmap) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.SetProfile : imageBitmap : " + imageBitmap);
		if(imageBitmap!=null) {
			RoundedImageView imageViewAvatar = (RoundedImageView) mDrawerLayout.findViewById(R.id.imageViewAvatar);
			imageViewAvatar.setImageBitmap(imageBitmap);
			// set profile image
			CMAppGlobals.profile_image = imageBitmap;
			// remove old round image view
		}
	} // end method SetProfileImage

	/**
	 * Method for setting profile image listeners
	 * @param context - current context
	 */
	public void SetPhotoListeners (final AppCompatActivity context) {
		RoundedImageView imageViewAvatar = (RoundedImageView) context.findViewById(R.id.imageViewAvatar);
		final String[] array = new String[]{ context.getString(R.string.str_take_photo), context.getString(R.string.str_choose_from_gallery)};
		if (imageViewAvatar != null) {
			imageViewAvatar.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    String auth_token = (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), ""));
                    if (auth_token.isEmpty()) {
                        context.startActivityForResult(new Intent(context, RegisterActivity.class), CMAppGlobals.REQUEST_REGISTER);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                        builder.setItems(array, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        if (!PermissionUtils.ensurePermission(context, Manifest.permission.CAMERA, PermissionUtils.PERMISSION_REQUEST_CAMERA)) {
                                            return;
                                        }
                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                                            context.startActivityForResult(takePictureIntent, CMAppGlobals.REQUEST_CAPTURE_IMAGE);
                                        }
                                        break;
                                    case 1:
                                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        intent.setType("image/*");
                                        context.startActivityForResult(intent, CMAppGlobals.REQUEST_LOAD_FROM_GALLERY);
                                        break;

                                }
                            }
                        });

                        builder.setNegativeButton(R.string.str_cancel_low, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }


                }
            });
		}

	}// end method changePhoto

	/**
	 * Method for opening drawer layout
	 * @param context
	 * @param left
	 */
	public void openDrawer(FragmentActivity context, int left) {
		mDrawerLayout = (DrawerLayout) context.findViewById(R.id.drawerLayout);
		openDrawer(mDrawerLayout, left);
	}

	/**
	 * Method for opening drawer layout
	 * @param context
	 * @param left
	 */
	public void closeDrawer(FragmentActivity context, int left) {
		mDrawerLayout = (DrawerLayout) context.findViewById(R.id.drawerLayout);
		closeDrawer(mDrawerLayout, left);
	}

	public boolean isOpenDrawer() {
		return mDrawerOpen;
	}

	public void setDrawerOpen(boolean mDrawerOpen) {
		this.mDrawerOpen = mDrawerOpen;
	}

	/**
	 * Method for opening drawer layout
	 *
	 * @param mDrawerLayout - drawer layout
	 * @param left			- drawer state
	 */
	public void openDrawer(DrawerLayout mDrawerLayout, int left) {
		mDrawerLayout.openDrawer(left);

	}

	/**
	 * Method for opening drawer layout
	 *
	 * @param mDrawerLayout
	 * @param left
	 */
	public void closeDrawer(DrawerLayout mDrawerLayout, int left) {
		mDrawerLayout.closeDrawer(left);

	}

	/**
	 * Method for adding active orders list
	 *
	 * @param context 	- current context
	 * @param orderList - order status list
	 */
	public void addActiveOrders(Activity context, List<OrderStatusData> orderList) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MenuHelper.addActiveOrders : orderList : " + orderList);

		activeOrdersList = orderList;
		if (activeOrdersList != null) {
			recyclerViewActiveOrders = (RecyclerView) context.findViewById(R.id.recyclerViewActiveOrders);
			LinearLayoutManager layoutManager = new LinearLayoutManager(context);
			recyclerViewActiveOrders.setLayoutManager(layoutManager);
			activeOrdersAdapter = new ActiveOrdersAdapter(context, activeOrdersList);
			activeOrdersAdapter.setOnItemClickListener(this);
			recyclerViewActiveOrders.setAdapter(activeOrdersAdapter);

			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MenuHelper.addActiveOrders : lastActiveOrdersListSize : " + lastActiveOrdersListSize
					+ " : activeOrdersList.size : " + activeOrdersList.size()
					+ " : isAnimated : " + isAnimated
					+ " : mDrawerOpen : " + mDrawerOpen
			);
			if (lastActiveOrdersListSize == 0 && activeOrdersList.size() > 0 && !isAnimated && mDrawerOpen) {

				isAnimated = true;
				startActiveOrdersAnimation(context);
			}
			lastActiveOrdersListSize = activeOrdersList.size();
		}

	} // end method addActiveOrders

	/**
	 *	Method for starting order animation
	 *
	 * @param context - current context
	 */
	public void startActiveOrdersAnimation (final Activity context) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MenuHelper.startActiveOrdersAnimation : activeOrdersList : " + activeOrdersList);

		recyclerViewActiveOrders.setVisibility(View.VISIBLE);
		layoutActiveOrders = (LinearLayout) context.findViewById(R.id.layoutActiveOrders);

		Animation anim = AnimationUtils.loadAnimation(context, R.anim.top_green_bar_to_bottom);
		layoutActiveOrders.startAnimation(anim);

		ViewTreeObserver vto = recyclerViewActiveOrders.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					//noinspection deprecation
					recyclerViewActiveOrders.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					recyclerViewActiveOrders.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}
				final LinearLayout layoutTop = (LinearLayout) context.findViewById(R.id.layoutMenuTop);
				final Handler handler = new Handler();
				final double activeOrdersListHeight = recyclerViewActiveOrders.getMeasuredHeight();
				if (CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.startActiveOrdersAnimation : activeOrdersListHeight : " + activeOrdersListHeight);
				if (activeOrdersListHeight > 0) {
					isAnimated = true;
				}
				layoutTop.setPadding(0, (int) activeOrdersListHeight / 15, 0, 0);

				handler.postDelayed(new Runnable() {
					int time = 300;
					double padding = activeOrdersListHeight / 15;

					@Override
					public void run() {
						if (time > 20) {
							time -= 20;
							padding += activeOrdersListHeight / 15;
							layoutTop.setPadding(0, (int) padding, 0, 0);
							handler.post(this);
							if (CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.startActiveOrdersAnimation : time : " + time + " padding : " + padding);
						}
					}
				}, 20);
			}
		});


	} // end method startActiveOrdersAnimation


	/**
	 * Method for clicking active orders list item
	 * @param context 			- current context
	 * @param view				- current view
	 * @param position			- clicked position
	 * @param orderStatusData	- order status data
	 */
	@Override
	public void activeOrdersOnItemClick(Activity context, View view, int position, OrderStatusData orderStatusData) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MenuHelper.activeOrdersOnItemClick : context : " + context
				+ ", view : " + view
				+ ", position : " + position
				+ ", orderStatusData  " + orderStatusData);

		if (orderStatusData != null &&
				orderStatusData.getIdOrder() != null &&
				!orderStatusData.getIdOrder().isEmpty()) {

			if(context instanceof MainActivity) {
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.activeOrdersOnItemClick : context--MainActivity ");
				closeDrawer((MainActivity)context, Gravity.LEFT);

				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.activeOrdersOnItemClick : TrackingOrderId : " + CMApplication.getTrackingOrderId());
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.activeOrdersOnItemClick : OrderId : " + orderStatusData.getIdOrder());
				if (!CMApplication.getTrackingOrderId().equals(orderStatusData.getIdOrder())) {
					if (CMAppGlobals.DEBUG) Logger.i(TAG, ":::: MenuHelper.activeOrdersOnItemClick : REQUEST_GET_ORDER_STATUS_LIST : 2");
					((MainActivity)context).updateUIByOrderStatusData(orderStatusData);
				}

			} else {
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.activeOrdersOnItemClick : context--not MainActivity ");

				Intent intent = new Intent(context, MainActivity.class);
				intent.putExtra(CMAppGlobals.EXTRA_MENU_ITEM_CLICK, true);
				intent.putExtra(CMAppGlobals.EXTRA_ORDER_STATUS_DATA, orderStatusData);
				context.startActivity(intent);
				context.onBackPressed();
			}
		}

	} // end method activeOrdersOnItemCli

	public static List<OrderStatusData> getActiveOrdersList() {
		return activeOrdersList;
	}

	public static void setActiveOrdersList(List<OrderStatusData> activeOrdersList) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.setActiveOrdersList : activeOrdersList : " + activeOrdersList);
		MenuHelper.activeOrdersList = activeOrdersList;
	}

	public static void addActiveOrder(OrderStatusData activeOrder) {
		if(MenuHelper.getActiveOrdersList() != null && MenuHelper.getActiveOrdersList().size() > 0)
			MenuHelper.getActiveOrdersList().add(activeOrder);
	}

	public static void removeActiveOrder(String orderId) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.removeActiveOrder : orderId : " + orderId);
		if(MenuHelper.getActiveOrdersList() != null && MenuHelper.getActiveOrdersList().size() > 0)
			for (OrderStatusData item :
					MenuHelper.getActiveOrdersList()) {
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: MenuHelper.removeActiveOrder : item : orderId : " + item.getIdOrder());
				if (item.getIdOrder().equals(orderId)) {
					MenuHelper.getActiveOrdersList().remove(item);
					return;
				}
			}
	}



}
