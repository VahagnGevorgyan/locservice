package com.locservice.api;

public interface RequestTypes {

	int DEF_REQUEST = 0;
	// PLACE
	int CHECK_FILES_CHANGES = DEF_REQUEST + 1;
	//
	// COUNTRY
	int GET_COUNTRIES = DEF_REQUEST + 13;
	int REQUEST_GET_TARIFF_LINK = DEF_REQUEST + 14;
	int REQUEST_GET_TARIFF_LIST = DEF_REQUEST + 15;
	int REQUEST_ADD_NEW_CLIENT = DEF_REQUEST + 16;
	int REQUEST_CHECK_PHONE= DEF_REQUEST + 17;
	int REQUEST_NEW_ORDER = DEF_REQUEST + 18;
	int GET_TARIFFS = DEF_REQUEST + 19;
	int REQUEST_GET_DRIVERS = DEF_REQUEST + 20;
	int REQUEST_GET_DRIVER_INFO = DEF_REQUEST + 21;
	int REQUEST_GET_CLIENT_INFO = DEF_REQUEST + 22;
	int REQUEST_GET_ORDERS = DEF_REQUEST + 23;
	int REQUEST_UPDATE_CLIENT_INFO = DEF_REQUEST + 24;
	int REQUEST_GET_DRIVER_COORDINATES = DEF_REQUEST + 25;
	int REQUEST_GET_ORDERS_INFO = DEF_REQUEST + 26;
	int REQUEST_GET_ORDER_STATUS_LIST = DEF_REQUEST + 27;
	int REQUEST_CANCEL_ORDER = DEF_REQUEST + 28;
	int REQUEST_ADD_COMMENT = DEF_REQUEST + 29;
	int REQUEST_GOOGLE_DIRECTIONS = DEF_REQUEST + 30;
	int REQUEST_SET_ORDER_RATE = DEF_REQUEST + 31;
	int REQUEST_GOOGLE_DISTANCE_MATRIX = DEF_REQUEST + 32;
	int REQUEST_GET_DRIVERS_FROM_MAP = DEF_REQUEST + 33;
	int REQUEST_GET_ORDER_PRICE = DEF_REQUEST + 34;
	int REQUEST_GOOGLE_DISTANCE_MATRIX_FROM_A_TO_B = DEF_REQUEST + 35;
	int REQUEST_GET_STATISTICS = DEF_REQUEST + 36;
	int REQUEST_GET_COMMENTS = DEF_REQUEST + 37;
	int REQUEST_ENTER_COUPON = DEF_REQUEST + 38;
	int REQUEST_GET_LANDMARK_LIST = DEF_REQUEST + 39;
	int REQUEST_CAME_OUT = DEF_REQUEST + 40;
	int REQUEST_SET_FAVORITE = DEF_REQUEST + 41;
	int REQUEST_GET_FAVORITE = DEF_REQUEST + 42;
	int REQUEST_REMOVE_FAVORITE = DEF_REQUEST + 43;
	int REQUEST_BIND_CARD = DEF_REQUEST + 44;
	int REQUEST_CARD_AUTH_START = DEF_REQUEST + 45;
	int REQUEST_GET_CARDS = DEF_REQUEST + 46;
	int REQUEST_CHECK_CARD_BIND = DEF_REQUEST + 47;
	int REQUEST_CHECK_UNBIND_CARD = DEF_REQUEST + 48;
	int REQUEST_REMOVE_ORDER = DEF_REQUEST + 49;
	int REQUEST_GOOGLE_GEOCODE_FOR_COLL_ADDRESS = DEF_REQUEST + 50;
	int REQUEST_GOOGLE_GEOCODE_FOR_DELIVERY_ADDRESS = DEF_REQUEST + 51;
	int REQUEST_DRIVER_DISTANCE_MATRIX = DEF_REQUEST + 52;
	int REQUEST_GET_ORDER_STATUS_LIST_FROM_PUSH = DEF_REQUEST + 53;
	int REQUEST_GET_ORDERS_INFO_MENU = DEF_REQUEST + 54;
	int REQUEST_GET_ORDER_TRACK = DEF_REQUEST + 55;
	int REQUEST_GET_LAST_ADDRESSES = DEF_REQUEST + 56;
	int REQUEST_GET_AIRPORT_POLYGON = DEF_REQUEST + 57;
	int REQUEST_PREPARE_CANCEL_ORDER = DEF_REQUEST + 58;
	int REQUEST_PAY_ORDER = DEF_REQUEST + 59;
	int REQUEST_CHECK_ORDER_PAY = DEF_REQUEST + 60;
	int REQUEST_GET_FAVORITE_FROM_SEARCH = DEF_REQUEST + 61;
	int REQUEST_REMOVE_ADDRESS = DEF_REQUEST + 62;
	int REQUEST_GET_BONUS_INFO = DEF_REQUEST + 63;
	int REQUEST_REPOST_SOCIAL = DEF_REQUEST + 64;
	int REQUEST_GOOGLE_AUTOCOMPLETE = DEF_REQUEST + 65;
	int REQUEST_GOOGLE_PLACE = DEF_REQUEST + 66;
	int REQUEST_GOOGLE_DIRECTIONS_FOR_PRICE_ROUTE = DEF_REQUEST + 67;
	int REQUEST_DISTANCE_MATRIX_FOR_PRICE_OUT_POLYGON = DEF_REQUEST + 68;
}
