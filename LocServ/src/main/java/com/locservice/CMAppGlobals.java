package com.locservice;

import android.graphics.Bitmap;


public class CMAppGlobals {

	public static final CMBuildType BUILD_TYPE = CMBuildType.DEBUGGING;
	
	public static final boolean DEBUG = false;
	public static final boolean DB_DEBUG = false;

	// api request/response
	public static final boolean API_TOAST = false;
	
	static final String CM_HOST_DEBUG = "SOME_URL";
	static final String CM_HOST_RELEASE = "SOME_URL";

	// email address
	public static  final String CM_EMAIL = "SOME_EMAIL";
	
	// fragment tags
	public static final String FRAGMENT_NONE = "fragment_none";
	public static final String FRAGMENT_MAP = "fragment_map";
	public static final String FRAGMENT_ORDER = "fragment_order";
	public static final String FRAGMENT_ORDER_HISTORY = "fragment_order_history";
	public static final String FRAGMENT_ORDER_RATE = "fragment_order_rate";

	// activity types
	public static final String ACTIVITY_TYPE = "activity_type";
	
	// scroll type
	public static final String SCROLL_TYPE = "scroll_type";
	
	public static final String EXTRA_STATUS_RECEIVER = "com.locservice.service.STATUS_RECEIVER";
	public static final String EXTRA_REQUEST_TYPE = "com.locservice.service.REQUEST_TYPE";


	public static final String DEFAULT_FONT = "fonts/RobotoRegular.ttf";
	public static final String CUSTOM_ATTR_SCHEMAS = "http://schemas.android.com/apk/res-auto";
	public static final String ERROR_CODE_EXTRA_BUNDLE = "error_code";

	public static final String TOKEN = "qkSLBa8sJsvWuipZs21QsTtEMY18lo10qnpchCZ_1L0";

	// ORDER EXTRAS
	public static final String EXTRA_ORDER_ID = "order_id";
	public static final String EXTRA_ORDER_PRICE = "extra_order_price";
	public static final String EXTRA_ORDER_BONUS = "extra_order_bonus";
	public static final String EXTRA_ORDER_TRIP_TIME = "extra_order_trip_time";
	public static final String EXTRA_ORDER_STATUS = "extra_order_status";
	public static final String EXTRA_ORDER = "order";
	public static final String EXTRA_ORDER_RATE = "extra_order_rate";
	public static final String EXTRA_ORDER_TRIP_TITLE = "extra_order_trip_title";

	// CARD WEB
	public static final String EXTRA_CARD_HTML = "extra_card_html";
	public static final String EXTRA_CARD_MD = "extra_card_md";
	public static final String EXTRA_CARD_PAREQ = "extra_card_pareq";
	public static final String EXTRA_CARD_TERM_URL = "extra_card_term_url";
	public static final String EXTRA_CARD_AUTH_URL = "extra_card_auth_url";
	public static final String EXTRA_CARD_ID_HASH = "extra_card_id_hash";

	public static final String EXTRA_MENU_ITEM_CLICK = "extra_menu_item_click";
	public static final String EXTRA_ORDER_STATUS_DATA = "extra_order_status_data";

	// FAVORITE ADDRESS
	public static final String EXTRA_FAVORITE_ADDRESS = "extra_favorite_address";
	public static final String EXTRA_FAVORITE_ID = "extra_favorite_id";
	public static final String EXTRA_ORDER_COMMENT = "extra_order_comment";
	// COMMENT
	public static final String EXTRA_ORDER_ENTRANCE = "extra_order_entrance";

	// SEARCH
	public static final String EXTRA_SEARCH_ADDRESS = "extra_search_address";
	// PROFILE IMAGE
	public static final String EXTRA_CHANGE_PROFILE_ITEM = "extra_change_profile_name";
	// FROM MENU
	public static final String EXTRA_FROM_MENU = "extra_from_menu";



	// LOAD IMAGE
	private static final int CHANGE_IMAGE = 0;
	public static final int REQUEST_CAPTURE_IMAGE = CHANGE_IMAGE + 1;
	public static final int REQUEST_LOAD_FROM_GALLERY = CHANGE_IMAGE + 2;
	public static final int REQUEST_CHANGE_PROFILE_INFO = CHANGE_IMAGE + 3;
	public static final int REQUEST_SEARCH_DATA = CHANGE_IMAGE + 4;
	// REGISTER
	public static final int REQUEST_REGISTER = CHANGE_IMAGE + 5;
	// FAVORITE ADDRESS
	public static final int REQUEST_ADD_FAVORITE_ADDRESS = CHANGE_IMAGE + 6;
	// COMMENT
	public static final int REQUEST_COMMENT = CHANGE_IMAGE + 7;
	// ORDER LIST
	public static final int REQUEST_ORDER_LIST = CHANGE_IMAGE + 8;

	// CARD WEB
	public static final int REQUEST_CARD_WEB = CHANGE_IMAGE + 9;
	// ADD CARD
	public static final int REQUEST_CARD_ADD = CHANGE_IMAGE + 10;
	// NETWORK
	public static final int REQUEST_NETWORK = CHANGE_IMAGE + 11;

	// ORDER INFO
	public static final int REQUEST_ORDER_INFO = CHANGE_IMAGE + 12;

	// PROFILE IMAGE
	public static final int REQUEST_CHANGE_PROFILE_ITEM = CHANGE_IMAGE + 13;

	// PROFILE IMAGE
	public static Bitmap profile_image = null;

	//MAPS
	public static final int DEFAULT_ZOOM_LEVEL = 16;
	// this key from old app
	public static final String GOOGLE_ANDROID_API_KEY = "AIzaSyDq3SsSblB4BLnpUplfhU1TOYCAW2QjUp4";
	// this key from Sash
//	public static final String GOOGLE_ANDROID_DISTANCE_API_KEY = "AIzaSyDXWNv6wQHxiXZYJ4AwH1X3sNjE-U5cVQo";
	// this key from Slav
//	public static final String GOOGLE_ANDROID_PLACE_API_KEY = "AIzaSyDpy5RqQolSfffYgfBXY1i6rkzRgBZEbuE";
	public static final String GOOGLE_ANDROID_PLACE_API_KEY = "AIzaSyDxqeWBOQyc_3EZwlPlmCzUP2Gj6b-__oo";

	public static final int SUCCESS_RESULT = 0;
	public static final int FAILURE_RESULT = 1;
	private static final String PACKAGE_NAME = "com.locservice";
	public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
	public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
	public static final String RESULT_MESSAGE_KEY = PACKAGE_NAME + ".RESULT_MESSAGE_KEY";
	public static final String LAT_LNG_DATA_EXTRA = PACKAGE_NAME + ".LAT_LNG_DATA_EXTRA";
	public static final String EXTRA_ACTIVE_ORDER_ID = "extra_active_order_id";
	public static final String EXTRA_ACTIVE_ORDER_STATUS_DATA = "extra_active_order_status_data";
	public static final String EXTRA_NO_PLACE_ID = "extra_no_place_id";
	public static final String EXTRA_CHANGED_FAVORITE_ADDRESS = "extra_new_favorite_address";

	// GCM
	public static final String GOOGLE_APIS_PROJECT_NUMBER = "766661429454";

	//Driver
	public static final float NEAREST_DRIVERS_DEFAULT_RADIUS = 5;
	public static final String EXTRA_DRIVER_INFO = "extra_driver_info";

	// WebView dialog argument
	public static final String ARGUMENT_URL = "argument_url";
	// Search fragment type
	public static final String ARGUMENT_SEARCH_FRAGMENT_TYPE = "ARGUMENT_SEARCH_FRAGMENT_TYPE";

	// WebView dialog fragment tag
	public static final String FRAGMENT_TAG_WEB_VIEW_DIALOG = "fragment_tag_web_view_dialog";

	// Share links
	public static final String LOC_SERVICE_LINK = "SOME_LINK";
	public static final String LOC_SERVICE_PICTURE_LINK = "SOME_LINK";


}
