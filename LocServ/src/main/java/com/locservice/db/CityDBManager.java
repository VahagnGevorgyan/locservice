package com.locservice.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.City;
import com.locservice.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 25 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CityDBManager extends DatabaseManager {

    private static final String TAG = CityDBManager.class.getSimpleName();

    private Context mContext;

    public CityDBManager(Context context) {
        super(context);
    }

    /**
     * Method for adding cities in DB
     * @param cityList
     * @param last_tr_id
     * @param language_id
     */
    public int setCities(SQLiteDatabase db, List<City> cityList, int country_id, int last_tr_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)
            Logger.i(TAG, ":: DB : CityDBManager.setCities : cityList: " + cityList
                    + " : country_id : " + country_id
                    + " : last_tr_id : " + last_tr_id
                    + " : language_id : " + language_id);

        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);

        for (City item :
                cityList) {
            if (item != null) {
                db.beginTransaction();
                try {
                    if(CheckIsDataAlreadyInDBorNot(db, DBUtlis.TABLE_CITY, DBUtlis.KEY_ID, item.getId())) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : CityDBManager.setCities : UPDATE : city id : " + item.getId());

                        String selectQuery = "select " + DBUtlis.NAME_TR_ID +
                                ", " + DBUtlis.NOUNS_TR_ID +
                                ", " + DBUtlis.AREA_TR_ID +
                                " from " + DBUtlis.TABLE_CITY +
                                " where " + DBUtlis.KEY_ID + " = " + item.getId();
                        Cursor c = null;
                        try {
                            if(db != null) {
                                c = db.rawQuery(selectQuery, null);
                            }
                            int name_tr_id = 0;
                            int nouns_tr_id = 0;
                            int area_tr_id = 0;
                            if (c != null && c.moveToFirst()) {
                                do {
                                    name_tr_id = c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID));
                                    nouns_tr_id = c.getInt(c.getColumnIndex(DBUtlis.NOUNS_TR_ID));
                                    area_tr_id = c.getInt(c.getColumnIndex(DBUtlis.AREA_TR_ID));
                                } while (c.moveToNext());
                            }
                            if(name_tr_id > 0)
                                translationDBManager.createTranslation(db, name_tr_id, last_tr_id, language_id, item.getName());
                            if(nouns_tr_id > 0)
                                translationDBManager.createTranslation(db, nouns_tr_id, last_tr_id, language_id, item.getNouns());
                            if(area_tr_id > 0)
                                translationDBManager.createTranslation(db, area_tr_id, last_tr_id, language_id, item.getArea());
                        } catch (Exception e) {
                            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : CityDBManager.setCities : error : " + e.getMessage()!=null?e.getMessage():"");
                        } finally {
                            if (c != null && !c.isClosed() && db != null) {
                                c.close();
                            }
                        }
                    } else {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : CityDBManager.setCities : CREATE : country id : " + item.getId());

                        // TRANSLATIONS
                        int name_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getName());
                        last_tr_id = name_tr_id;
                        int nouns_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getNouns());
                        last_tr_id = nouns_tr_id;
                        int area_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getArea());
                        last_tr_id = area_tr_id;
                        // CREATE City
                        db.execSQL("insert into " + DBUtlis.TABLE_CITY + "("
                                + DBUtlis.KEY_ID + ","
                                + DBUtlis.COUNTRY_ID + ","
                                + DBUtlis.NAME_TR_ID + ","
                                + DBUtlis.NOUNS_TR_ID + ","
                                + DBUtlis.LATITUDE + ","
                                + DBUtlis.LONGITUDE + ","
                                + DBUtlis.PHONE + ","
                                + DBUtlis.SEARCH_RADIUS + ","
                                + DBUtlis.GMT + ","
                                + DBUtlis.AREA_TR_ID + ","
                                + DBUtlis.IS_DEFAULT +
                                ") values("
                                + item.getId() + ","
                                + country_id + ","
                                + name_tr_id + ","
                                + nouns_tr_id + ","
                                + item.getLatitude() + ","
                                + item.getLongitude() + ",'"
                                + item.getPhone() + "',"
                                + item.getSearchRadius() + ","
                                + item.getGmt() + ","
                                + area_tr_id + ","
                                + item.getIsDefault()
                                + ")");
                    }
                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : CityDBManager.setCities : END last_tr_id : " + last_tr_id);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }
        return last_tr_id;

    } // end method setCities

    /**
    * GetAllCities: getting all Cities
    *
    * @return
    */
    public List<City> getAllCitiesByCountry(SQLiteDatabase db, int country_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : CityDBManager.getAllCitiesByCountry : country_id : " + country_id);

        List<City> cities = new ArrayList<City>();
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_CITY + " WHERE " + DBUtlis.COUNTRY_ID + " = " + country_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    City city = new City();
                    city.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.KEY_ID))));
                    city.setName(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID))));
                    city.setNouns(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.NOUNS_TR_ID))));
                    city.setLatitude(String.valueOf(c.getFloat(c.getColumnIndex(DBUtlis.LATITUDE))));
                    city.setLongitude(String.valueOf(c.getFloat(c.getColumnIndex(DBUtlis.LONGITUDE))));
                    city.setPhone(c.getString(c.getColumnIndex(DBUtlis.PHONE)));
                    city.setSearchRadius(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.SEARCH_RADIUS))));
                    city.setGmt(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.GMT))));
                    city.setArea(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.AREA_TR_ID))));
                    city.setIsDefault(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.IS_DEFAULT))));

                    // adding to city list
                    cities.add(city);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : CityDBManager.getAllCitiesByCountry : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed() && db != null) {
                c.close();
            }
        }
        return cities;

    } // end method getAllCitiesByCountry

    /**
     * getAllCities: getting all Cities
     *
     * @return
     */
    public List<City> getAllCities(int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : CityDBManager.getAllCities ");

        SQLiteDatabase db = getReadableDatabase();
        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        List<City> cities = new ArrayList<City>();
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_CITY;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    City city = new City();
                    city.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.KEY_ID))));
                    city.setName(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID)), language_id));
                    city.setNouns(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.NOUNS_TR_ID)), language_id));
                    city.setLatitude(String.valueOf(c.getFloat(c.getColumnIndex(DBUtlis.LATITUDE))));
                    city.setLongitude(String.valueOf(c.getFloat(c.getColumnIndex(DBUtlis.LONGITUDE))));
                    city.setPhone(c.getString(c.getColumnIndex(DBUtlis.PHONE)));
                    city.setSearchRadius(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.SEARCH_RADIUS))));
                    city.setGmt(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.GMT))));
                    city.setArea(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.AREA_TR_ID)), language_id));
                    city.setIsDefault(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.IS_DEFAULT))));

                    // adding to city list
                    cities.add(city);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : CityDBManager.getAllCities : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return cities;

    } // end method getAllCities


    /**
     * Method for getting city by id
     * @param cityId
     * @return
     */
    public City getCityByID(String cityId, int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: CityDBManager.getCityByID : cityId : " + cityId);

        City city = new City();

        SQLiteDatabase db = getReadableDatabase();
        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_CITY + " WHERE " + DBUtlis.KEY_ID + " = " + cityId + "";

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    city.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.KEY_ID))));
                    city.setName(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID)), language_id));
                    city.setNouns(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.NOUNS_TR_ID)), language_id));
                    city.setLatitude(String.valueOf(c.getFloat(c.getColumnIndex(DBUtlis.LATITUDE))));
                    city.setLongitude(String.valueOf(c.getFloat(c.getColumnIndex(DBUtlis.LONGITUDE))));
                    city.setPhone(c.getString(c.getColumnIndex(DBUtlis.PHONE)));
                    city.setSearchRadius(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.SEARCH_RADIUS))));
                    city.setGmt(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.GMT))));
                    city.setArea(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.AREA_TR_ID)), language_id));
                    city.setIsDefault(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.IS_DEFAULT))));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : CityDBManager.getCityByID : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return city;

    } // end method getCityByID

}
