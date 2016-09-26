package com.locservice.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.City;
import com.locservice.api.entities.Country;
import com.locservice.application.LocServicePreferences;
import com.locservice.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 23 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CountryDBManager extends DatabaseManager {

    private static final String TAG = CountryDBManager.class.getSimpleName();

    private Context mContext;

    public CountryDBManager(Context context) {
        super(context);
    }


    /**
     *  Method for setting countries in DB
     * @param countryList
     * @param last_tr_id
     * @param language_id
     */
    public void setCountries(List<Country> countryList, int last_tr_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : CountryDBManager.setCountries : countryList: " + countryList
                                            + " : last_tr_id : " + last_tr_id
                                            + " : language_id : " + language_id);

        SQLiteDatabase db = getWritableDatabase();
        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        CityDBManager cityDBManager = new CityDBManager(this.mContext);

        for (Country item :
                countryList) {
            if (item != null) {
                db.beginTransaction();
                try {
                    if(CheckIsDataAlreadyInDBorNot(DBUtlis.TABLE_COUNTRY, DBUtlis.KEY_ID, String.valueOf(item.getId()))) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : CountryDBManager.setCountries : UPDATE : country id : " + item.getId());

                        String selectQuery = "select " + DBUtlis.LONG_NAME_TR_ID +
                                            ", " + DBUtlis.CURRENCY_NAME_TR_ID +
                                            ", " + DBUtlis.CURRENCY_SHORT_NAME_TR_ID +
                                            " from " + DBUtlis.TABLE_COUNTRY +
                                            " where " + DBUtlis.KEY_ID + " = " + item.getId();
                        Cursor c = null;
                        try {
                            if(db != null) {
                                c = db.rawQuery(selectQuery, null);
                            }
                            int long_name_tr_id = 0;
                            int currency_name_tr_id = 0;
                            int currency_short_name_tr_id = 0;
                            if (c != null && c.moveToFirst()) {
                                do {
                                    long_name_tr_id = c.getInt(c.getColumnIndex(DBUtlis.LONG_NAME_TR_ID));
                                    currency_name_tr_id = c.getInt(c.getColumnIndex(DBUtlis.CURRENCY_NAME_TR_ID));
                                    currency_short_name_tr_id = c.getInt(c.getColumnIndex(DBUtlis.CURRENCY_SHORT_NAME_TR_ID));
                                } while (c.moveToNext());
                            }
                            if(long_name_tr_id > 0)
                                translationDBManager.createTranslation(db, long_name_tr_id, last_tr_id, language_id, item.getLongName());
                            if(currency_name_tr_id > 0)
                                translationDBManager.createTranslation(db, currency_name_tr_id, last_tr_id, language_id, item.getCurrencyName());
                            if(currency_short_name_tr_id > 0)
                                translationDBManager.createTranslation(db, currency_short_name_tr_id, last_tr_id, language_id, item.getCurrencyShortName());
                        } catch (Exception e) {
                            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : CountryDBManager.setCountries : error : " + e.getMessage()!=null?e.getMessage():"");
                        } finally {
                            if (c != null && !c.isClosed() && db != null) {
                                c.close();
                            }
                        }
                    } else {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : CountryDBManager.setCountries : CREATE : country id : " + item.getId());

                        // TRANSLATIONS
                        int long_name_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getLongName());
                        last_tr_id = long_name_tr_id;
                        int currency_name_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getCurrencyName());
                        last_tr_id = currency_name_tr_id;
                        int currency_short_name_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getCurrencyShortName());
                        last_tr_id = currency_short_name_tr_id;
                        // CREATE country
                        db.execSQL("insert into " + DBUtlis.TABLE_COUNTRY + "("
                                + DBUtlis.KEY_ID + ","
                                + DBUtlis.SHORT_NAME + ","
                                + DBUtlis.LONG_NAME_TR_ID + ","
                                + DBUtlis.LANGUAGE + ","
                                + DBUtlis.PHONE_PREFIX + ","
                                + DBUtlis.PHONE_LENGTH + ","
                                + DBUtlis.ID_CURRENCY + ","
                                + DBUtlis.MAP_TYPE + ","
                                + DBUtlis.IS_DEFAULT + ","
                                + DBUtlis.CURRENCY_NAME_TR_ID + ","
                                + DBUtlis.CURRENCY_SHORT_NAME_TR_ID + ","
                                + DBUtlis.CURRENCY_NAME_ISO +
                                ") values("
                                + item.getId() + ",'"
                                + item.getShortName() + "',"
                                + long_name_tr_id + ",'"
                                + item.getLanguage() + "','"
                                + item.getPhonePrefix() + "','"
                                + item.getPhoneLength() + "',"
                                + item.getIdCurrency() + ","
                                + item.getMapType() + ","
                                + item.getIsDefault() + ","
                                + currency_name_tr_id + ","
                                + currency_short_name_tr_id + ",'"
                                + item.getCurrencyNameIso()
                                + "')");
                    }
                    // INSERT/UPDATE cities to DB
                    last_tr_id = cityDBManager.setCities(db, item.getCities(), Integer.parseInt(item.getId()), last_tr_id, language_id);

                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : CountryDBManager.setCountries : END last_tr_id : " + last_tr_id);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }
        LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.LAST_TR_ID, last_tr_id);

    } // end method setCountries

    /**
     * GetAllCountries: getting all Countries
     *
     * @return
     */
    public List<Country> getAllCountries() {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : CountryDBManager.getAllCountries ");

        SQLiteDatabase db = getReadableDatabase();
        List<Country> countries = new ArrayList<Country>();
        Cursor c = null;

        CityDBManager cityDBManager = new CityDBManager(this.mContext);

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_COUNTRY;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    Country country = new Country();
                    country.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.KEY_ID))));
                    country.setShortName(c.getString(c.getColumnIndex(DBUtlis.SHORT_NAME)));
                    country.setLongName(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.LONG_NAME_TR_ID)))); // TODO Translation
                    country.setLanguage(c.getString(c.getColumnIndex(DBUtlis.LANGUAGE)));
                    country.setPhonePrefix(c.getString(c.getColumnIndex(DBUtlis.PHONE_PREFIX)));
                    country.setPhoneLength(c.getString(c.getColumnIndex(DBUtlis.PHONE_LENGTH)));
                    country.setIdCurrency(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.ID_CURRENCY))));
                    country.setMapType(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.MAP_TYPE))));
                    country.setIsDefault(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.IS_DEFAULT))));
                    country.setCurrencyName(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.CURRENCY_NAME_TR_ID))));               // TODO Translation
                    country.setCurrencyShortName(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.CURRENCY_SHORT_NAME_TR_ID))));    // TODO Translation
                    country.setCurrencyNameIso(c.getString(c.getColumnIndex(DBUtlis.CURRENCY_NAME_ISO)));

                    // add city list
                    List<City> allCities = cityDBManager.getAllCitiesByCountry(db, Integer.parseInt(country.getId()));
                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : CITY LIST : country id : " + country.getId() + " : list : " + allCities);
                    country.setCities(allCities);

                    // adding to country list
                    countries.add(country);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : CountryDBManager.getAllCountries : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                db.close();
            }
        }
        return countries;

    } // end method getAllCountries

}
