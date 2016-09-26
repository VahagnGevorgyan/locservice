package com.locservice.db;

/**
 * Created by Vahagn Gevorgyan
 * 25 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DBQueries {

    // COUNTRY
    public static final String CREATE_COUNTRY_TABLE = "CREATE TABLE " + DBUtlis.TABLE_COUNTRY + "("
                + DBUtlis.KEY_ID + " INTEGER PRIMARY KEY NOT NULL,"
                + DBUtlis.SHORT_NAME + " TEXT,"
                + DBUtlis.LONG_NAME_TR_ID + " INTEGER,"
                + DBUtlis.LANGUAGE + " TEXT,"
                + DBUtlis.PHONE_PREFIX + " TEXT,"
                + DBUtlis.PHONE_LENGTH + " TEXT,"
                + DBUtlis.ID_CURRENCY + " INTEGER,"
                + DBUtlis.MAP_TYPE + " INTEGER,"
                + DBUtlis.IS_DEFAULT + " INTEGER NOT NULL,"
                + DBUtlis.CURRENCY_NAME_TR_ID + " INTEGER,"
                + DBUtlis.CURRENCY_SHORT_NAME_TR_ID + " INTEGER,"
                + DBUtlis.CURRENCY_NAME_ISO + " TEXT"
                + ")";
    // LANGUAGE
    public static final String CREATE_LANGUAGE_TABLE = "CREATE TABLE " + DBUtlis.TABLE_lANGUAGE + "("
            + DBUtlis.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DBUtlis.NAME + " TEXT,"
            + DBUtlis.SHORT_NAME + " TEXT " + ")";
    // TRANSLATION
    public static final String CREATE_TRANSLATION_TABLE = "CREATE TABLE " + DBUtlis.TABLE_TRANSLATION + "("
            + DBUtlis.KEY_ID + " INTEGER NOT NULL,"
            + DBUtlis.LANGUAGE_ID + " INTEGER NOT NULL,"
            + DBUtlis.TEXT + " TEXT,"
            + DBUtlis.IS_DELETED + " INTEGER,"
            + "FOREIGN KEY (" + DBUtlis.LANGUAGE_ID + ") REFERENCES "
            + DBUtlis.TABLE_lANGUAGE + "(" + DBUtlis.KEY_ID + ")" + ")";
    // CITY
    public static final String CREATE_CITY_TABLE = "CREATE TABLE " + DBUtlis.TABLE_CITY + "("
            + DBUtlis.KEY_ID + " INTEGER PRIMARY KEY NOT NULL,"
            + DBUtlis.COUNTRY_ID + " INTEGER NOT NULL,"
            + DBUtlis.NAME_TR_ID + " INTEGER,"
            + DBUtlis.NOUNS_TR_ID + " INTEGER,"
            + DBUtlis.LATITUDE + " REAL,"
            + DBUtlis.LONGITUDE + " REAL,"
            + DBUtlis.PHONE + " TEXT,"
            + DBUtlis.SEARCH_RADIUS + " INTEGER,"
            + DBUtlis.GMT + " INTEGER ,"
            + DBUtlis.AREA_TR_ID + " INTEGER,"
            + DBUtlis.IS_DEFAULT + " INTEGER"
            + ")";
    // TARIFF
    public static final String CREATE_TARIFF_TABLE = "CREATE TABLE " + DBUtlis.TABLE_TARIFF + "("
            + DBUtlis.KEY_ID + " INTEGER PRIMARY KEY NOT NULL,"
            + DBUtlis.CITY_ID + " INTEGER NOT NULL,"
            + DBUtlis.NAME_TR_ID + " INTEGER,"
            + DBUtlis.IS_DEFAULT + " INTEGER,"
            + DBUtlis.CAR_MODELS_TR_ID + " INTEGER,"
            + DBUtlis.SHORTNAME_TR_ID + " INTEGER,"
            + DBUtlis.AIRPORT_MIN_PRICE + " TEXT"
            + ")";
    // INTERVAL
    public static final String CREATE_INTERVAL_TABLE = "CREATE TABLE " + DBUtlis.TABLE_INTERVAL + "("
            + DBUtlis.KEY_ID + " INTEGER PRIMARY KEY NOT NULL,"
            + DBUtlis.IS_NIGHT + " INTEGER,"
            + DBUtlis.PRICE + " TEXT,"
            + DBUtlis.PREPAID_TIME + " TEXT,"
            + DBUtlis.NAME_TR_ID + " INTEGER,"
            + DBUtlis.DESC_STATIC_TR_ID + " INTEGER"
            + ")";
    public static final String CREATE_TARIFF_INTERVAL_TABLE = "CREATE TABLE " + DBUtlis.TABLE_TARIFF_INTERVAL + "("
            + DBUtlis.INTERVAL_ID + " INTEGER NOT NULL,"
            + DBUtlis.TARIFF_ID + " INTEGER NOT NULL"
            + ")";
    // SERVICE
    public static final String CREATE_SERVICE_TABLE = "CREATE TABLE " + DBUtlis.TABLE_SERVICE + "("
            + DBUtlis.KEY_ID + " INTEGER PRIMARY KEY NOT NULL,"
            + DBUtlis.TITLE_TR_ID + " INTEGER,"
            + DBUtlis.SHORT_TITLE_TR_ID + " INTEGER,"
            + DBUtlis.PRICE + " REAL,"
            + DBUtlis.FIELD + " TEXT"
            + ")";
    public static final String CREATE_TARIFF_SERVICE_TABLE = "CREATE TABLE " + DBUtlis.TABLE_TARIFF_SERVICE + "("
            + DBUtlis.SERVICE_ID + " INTEGER NOT NULL,"
            + DBUtlis.TARIFF_ID + " INTEGER NOT NULL"
            + ")";
    // FIXED PRICE
    public static final String CREATE_TARIFF_FIXED_PRICE_TABLE = "CREATE TABLE " + DBUtlis.TABLE_TARIFF_FIXED_PRICE + "("
            + DBUtlis.TARIFF_ID + " INTEGER NOT NULL,"
            + DBUtlis.PRICE + " REAL,"
            + DBUtlis.FROM_ID + " INTEGER,"
            + DBUtlis.FROM_NAME_TR_ID + " INTEGER,"
            + DBUtlis.TO_ID + " INTEGER,"
            + DBUtlis.TO_NAME_TR_ID + " INTEGER"
            + ")";
    // AIRPORT
    public static final String CREATE_AIRPORT = "CREATE TABLE " + DBUtlis.TABLE_AIRPORT + "("
            + DBUtlis.KEY_ID + " INTEGER NOT NULL,"
            + DBUtlis.CITY_ID + " INTEGER,"
            + DBUtlis.NAME_TR_ID + " INTEGER,"
            + DBUtlis.LATITUDE + " TEXT,"
            + DBUtlis.LONGITUDE + " TEXT"
            + ")";
    public static final String CREATE_AIRPORT_TERMINAL_TABLE = "CREATE TABLE " + DBUtlis.TABLE_AIRPORT_TERMINAL + "("
            + DBUtlis.AIRPORT_ID + " INTEGER NOT NULL,"
            + DBUtlis.TERMINAL_ID + " INTEGER NOT NULL"
            + ")";
    public static final String CREATE_TERMINAL = "CREATE TABLE " + DBUtlis.TABLE_TERMINAL + "("
            + DBUtlis.KEY_ID + " INTEGER NOT NULL,"
            + DBUtlis.NAME_TR_ID + " INTEGER,"
            + DBUtlis.LATITUDE + " TEXT,"
            + DBUtlis.LONGITUDE + " TEXT"
            + ")";
    public static final String CREATE_RAILSTATION = "CREATE TABLE " + DBUtlis.TABLE_RAILSTATION + "("
            + DBUtlis.KEY_ID + " INTEGER NOT NULL,"
            + DBUtlis.CITY_ID + " INTEGER,"
            + DBUtlis.NAME_TR_ID + " INTEGER,"
            + DBUtlis.LATITUDE + " TEXT,"
            + DBUtlis.LONGITUDE + " TEXT"
            + ")";
    public static final String CREATE_FAVORITE = "CREATE TABLE " + DBUtlis.TABLE_FAVORITE+ "("
            + DBUtlis.KEY_ID + " INTEGER NOT NULL,"
            + DBUtlis.ID_LOCALITY + " TEXT,"
            + DBUtlis.ADDRESS + " TEXT,"
            + DBUtlis.LATITUDE + " TEXT,"
            + DBUtlis.LONGITUDE + " TEXT,"
            + DBUtlis.ENTRANCE + " TEXT,"
            + DBUtlis.COMMENT + " TEXT,"
            + DBUtlis.NAME + " TEXT"
            + ")";

}
