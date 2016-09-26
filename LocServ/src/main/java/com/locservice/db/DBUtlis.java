package com.locservice.db;


/**
 * Created by Vahagn Gevorgyan
 * 24 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DBUtlis {

    // TABLES
    public static final String TABLE_lANGUAGE = "language";
    public static final String TABLE_TRANSLATION = "translation";
    public static final String TABLE_CITY = "city";
    public static final String TABLE_COUNTRY = "country";
    public static final String TABLE_TARIFF = "tariff";
    public static final String TABLE_TARIFF_SERVICE = "tariff_service";
    public static final String TABLE_SERVICE = "service";
    public static final String TABLE_TARIFF_FIXED_PRICE = "tariff_fixed_price";
    public static final String TABLE_TARIFF_INTERVAL = "tariff_interval";
    public static final String TABLE_INTERVAL = "interval";
    public static final String TABLE_AIRPORT = "airport";
    public static final String TABLE_AIRPORT_TERMINAL = "airport_terminal";
    public static final String TABLE_TERMINAL = "terminal";
    public static final String TABLE_RAILSTATION = "railstation";
    public static final String TABLE_FAVORITE = "favorite";


    // TRANSLATION & LANGUAGE COLUMNS
    public static final String KEY_ID = "id";
    public static final String NAME = "name";
    public static final String SHORT_NAME = "short_name";
    public static final String LANGUAGE_ID = "language_id";
    public static final String IS_DELETED = "is_deleted";
    public static final String TEXT = "text";

    // COUNTRY COLUMNS
    public static final String LONG_NAME_TR_ID = "long_name_tr_id";
    public static final String LANGUAGE = "language";
    public static final String PHONE_PREFIX = "phone_prefix";
    public static final String PHONE_LENGTH = "phone_length";
    public static final String ID_CURRENCY = "id_currency";
    public static final String MAP_TYPE = "map_type";
    public static final String IS_DEFAULT = "is_default";
    public static final String CURRENCY_NAME_TR_ID = "currency_name_tr_id";
    public static final String CURRENCY_SHORT_NAME_TR_ID = "currency_short_name_tr_id";
    public static final String CURRENCY_NAME_ISO = "currency_name_iso";

    // CITY COLUMNS
    public static final String COUNTRY_ID = "country_id";
    public static final String NAME_TR_ID = "name_tr_id";
    public static final String NOUNS_TR_ID = "nouns_tr_id";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String PHONE = "phone";
    public static final String SEARCH_RADIUS = "search_radius";
    public static final String GMT = "gmt";
    public static final String AREA_TR_ID = "area_tr_id";

    // TARIFF COLUMNS
    public static final String TARIFF_ID = "tariff_id";
    public static final String CITY_ID = "city_id";
    public static final String CAR_MODELS_TR_ID = "car_models_tr_id";
    public static final String SHORTNAME_TR_ID = "shortname_tr_id";
    public static final String AIRPORT_MIN_PRICE = "airport_min_price";
    public static final String IS_NIGHT = "is_night";
    public static final String DESC_STATIC_TR_ID = "desc_static_tr_id";
    public static final String PRICE = "price";
    public static final String FROM_ID = "from_id";
    public static final String FROM_NAME_TR_ID = "from_name_tr_id";
    public static final String TO_ID = "to_id";
    public static final String TO_NAME_TR_ID = "to_name_tr_id";
    public static final String TITLE_TR_ID = "title_tr_id";
    public static final String SHORT_TITLE_TR_ID = "short_title_tr_id";
    public static final String FIELD = "field";
    public static final String PREPAID_TIME = "prepaid_time";

    // SERVICE
    public static final String SERVICE_ID = "service_id";
    // INTERVAL
    public static final String INTERVAL_ID = "interval_id";

    // AIRPORT
    public static final String AIRPORT_ID = "airport_id";
    // TERMINAL
    public static final String TERMINAL_ID = "terminal_id";
    // RAILSTATION
    public static final String RAILSTATION_ID = "railstation_id";

    // FAVORITE
    public static final String ID_LOCALITY = "id_locality";
    public static final String COMMENT = "comment";
    public static final String ENTRANCE = "entrance";
    public static final String ADDRESS = "address";

}
