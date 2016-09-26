package com.locservice.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.Tariff;
import com.locservice.api.entities.TariffInterval;
import com.locservice.api.entities.TariffService;
import com.locservice.application.LocServicePreferences;
import com.locservice.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 15 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class TariffDBManager extends DatabaseManager {

    private static final String TAG = TariffDBManager.class.getSimpleName();

    private Context mContext;

    public TariffDBManager(Context context) {
        super(context);
    }


    /**
     * SetTariffs: Method for setting tariffs in DB
     * @param tariffsList - list of tariffs
     * @param last_tr_id - last translation id
     * @param language_id - language id
     */
    public void setTariffs(List<Tariff> tariffsList, int last_tr_id, int language_id, int city_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffDBManager.setTariffs : tariffsList: " + tariffsList
                + " : last_tr_id : " + last_tr_id
                + " : language_id : " + language_id
                + " : city_id : " + city_id);

        SQLiteDatabase db = getWritableDatabase();
        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        TariffServiceDBManager tariffServiceDBManager = new TariffServiceDBManager(this.mContext);
        TariffIntervalDBManager tariffIntervalDBManager = new TariffIntervalDBManager(this.mContext);
        TariffFixedPriceDBManager tariffFixedPriceDBManager = new TariffFixedPriceDBManager(this.mContext);

        for (Tariff item :
                tariffsList) {
            if (item != null) {
                db.beginTransaction();
                try {
                    if(CheckIsDataAlreadyInDBorNot(DBUtlis.TABLE_TARIFF, DBUtlis.KEY_ID, String.valueOf(item.getID()))) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffDBManager.setTariffs : UPDATE : tariff id : " + item.getID());

                        String selectQuery = "select " + DBUtlis.CITY_ID +
                                ", " + DBUtlis.NAME_TR_ID +
                                ", " + DBUtlis.IS_DEFAULT +
                                ", " + DBUtlis.CAR_MODELS_TR_ID +
                                ", " + DBUtlis.SHORTNAME_TR_ID +
                                ", " + DBUtlis.AIRPORT_MIN_PRICE +
                                " from " + DBUtlis.TABLE_TARIFF +
                                " where " + DBUtlis.KEY_ID + " = " + item.getID();
                        Cursor c = null;
                        try {
                            c = db.rawQuery(selectQuery, null);
                            int name_tr_id = 0;
                            int car_models_tr_id = 0;
                            int shortname_tr_id = 0;
                            if (c != null && c.moveToFirst()) {
                                do {
                                    name_tr_id = c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID));
                                    car_models_tr_id = c.getInt(c.getColumnIndex(DBUtlis.CAR_MODELS_TR_ID));
                                    shortname_tr_id = c.getInt(c.getColumnIndex(DBUtlis.SHORTNAME_TR_ID));
                                } while (c.moveToNext());
                            }
                            if(name_tr_id > 0)
                                translationDBManager.createTranslation(db, name_tr_id, last_tr_id, language_id, item.getName());
                            if(car_models_tr_id > 0)
                                translationDBManager.createTranslation(db, car_models_tr_id, last_tr_id, language_id, item.getCarModels());
                            if(shortname_tr_id > 0)
                                translationDBManager.createTranslation(db, shortname_tr_id, last_tr_id, language_id, item.getShortname());
                        } catch (Exception e) {
                            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TariffDBManager.setTariffs : error : " + e.getMessage()!=null?e.getMessage():"");
                        } finally {
                            if (c != null && !c.isClosed()) {
                                c.close();
                            }
                        }
                    } else {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffDBManager.setTariffs : CREATE : tariff id : " + item.getID());

                        // TRANSLATIONS
                        int name_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getName());
                        last_tr_id = name_tr_id;
                        int car_models_tr_id = 0;
                        if(item.getCarModels() != null && !item.getCarModels().equals("")) {
                            car_models_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getCarModels());
                            last_tr_id = car_models_tr_id;
                        }
                        int shortname_tr_id = 0;
                        if(item.getShortname()!= null && !item.getShortname().equals("")) {
                            shortname_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getShortname());
                            last_tr_id = shortname_tr_id;
                        }
                        String airport_min_price = (item.getAirportMinPrice()!=null&&!item.getAirportMinPrice().equals(""))?item.getAirportMinPrice():"0";
                        // CREATE tariff
                        db.execSQL("insert into " + DBUtlis.TABLE_TARIFF + "("
                                + DBUtlis.KEY_ID + ","
                                + DBUtlis.CITY_ID + ","
                                + DBUtlis.NAME_TR_ID + ","
                                + DBUtlis.IS_DEFAULT + ","
                                + DBUtlis.CAR_MODELS_TR_ID + ","
                                + DBUtlis.SHORTNAME_TR_ID + ","
                                + DBUtlis.AIRPORT_MIN_PRICE +
                                ") values ("
                                + item.getID() + ","
                                + city_id + ","
                                + name_tr_id + ","
                                + item.getIsDefault() + ","
                                + car_models_tr_id + ","
                                + shortname_tr_id + ",'"
                                + airport_min_price
                                + "')");
                    }
                    // INSERT/UPDATE services/intervals/fixedPrices to DB
                    last_tr_id = tariffServiceDBManager.setTariffServices(db, item.getServices(), Integer.parseInt(item.getID()), last_tr_id, language_id);
                    last_tr_id = tariffIntervalDBManager.setTariffIntervals(db, item.getIntervals(), Integer.parseInt(item.getID()), last_tr_id, language_id);
//                    last_tr_id = tariffFixedPriceDBManager.setTariffFixedPrices(db, item.getFixedPriceRoutes(), Integer.parseInt(item.getID()), last_tr_id, language_id);

                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffDBManager.setTariffs : END last_tr_id : " + last_tr_id);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }
        LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.LAST_TR_ID, last_tr_id);

    } // end method setTariffs

    /**
     * GetAllTariffs: getting all Tariffs
     *
     * @return
     */
    public List<Tariff> getAllTariffs(int language_id, int city_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffDBManager.getAllTariffs : language_id : " + language_id);

        SQLiteDatabase db = getReadableDatabase();
        List<Tariff> tariffs = new ArrayList<Tariff>();
        Cursor c = null;

        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        TariffIntervalDBManager tariffIntervalDBManager = new TariffIntervalDBManager(this.mContext);
        TariffServiceDBManager tariffServiceDBManager = new TariffServiceDBManager(this.mContext);
        TariffFixedPriceDBManager tariffFixedPriceDBManager = new TariffFixedPriceDBManager(this.mContext);

        String selectQuery = "SELECT * FROM " + DBUtlis.TABLE_TARIFF +
                        " WHERE " + DBUtlis.CITY_ID + " = " + city_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    Tariff tariff = new Tariff();
                    tariff.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.KEY_ID))));
                    tariff.setCityId(c.getInt(c.getColumnIndex(DBUtlis.CITY_ID)));
                    tariff.setName(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID)), language_id));
                    tariff.setIsDefault(c.getString(c.getColumnIndex(DBUtlis.IS_DEFAULT)));
                    tariff.setCarModels(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.CAR_MODELS_TR_ID)), language_id));
                    tariff.setShortname(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.SHORTNAME_TR_ID)), language_id));
                    tariff.setAirportMinPrice(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.AIRPORT_MIN_PRICE))));

                    // add interval list
                    List<TariffInterval> allIntervals = tariffIntervalDBManager.getAllTariffIntervals(db, Integer.parseInt(tariff.getID()), language_id);
                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : INTERVAL LIST : tariff_id : " + tariff.getID() + " : list : " + allIntervals);
                    tariff.setIntervals(allIntervals);
                    // add service list
                    List<TariffService> allServices = tariffServiceDBManager.getAllTariffServices(db, Integer.parseInt(tariff.getID()), language_id);
                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : SERVICE LIST : tariff_id : " + tariff.getID() + " : list : " + allServices);
                    tariff.setServices(allServices);
//                    // add fixed price list
//                    List<FixedPriceRoute> allFixedPrices = tariffFixedPriceDBManager.getAllTariffFixedPrices(db, Integer.parseInt(tariff.getID()));
//                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DB : FIXEDPRICES LIST : tariff_id : " + tariff.getID() + " : list : " + allFixedPrices);
//                    tariff.setFixedPriceRoutes(allFixedPrices);

                    // adding to tariff list
                    tariffs.add(tariff);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TariffDBManager.getAllTariffs : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                db.close();
            }
        }
        return tariffs;

    } // end method getAllTariffs


    /**
     * GetAllTariffs: getting all Tariffs
     *
     * @return
     */
    public List<Tariff> getAllTariffs(int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffDBManager.getAllTariffs : language_id : " + language_id);

        SQLiteDatabase db = getReadableDatabase();
        List<Tariff> tariffs = new ArrayList<Tariff>();
        Cursor c = null;

        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        TariffIntervalDBManager tariffIntervalDBManager = new TariffIntervalDBManager(this.mContext);
        TariffServiceDBManager tariffServiceDBManager = new TariffServiceDBManager(this.mContext);
        TariffFixedPriceDBManager tariffFixedPriceDBManager = new TariffFixedPriceDBManager(this.mContext);

        String selectQuery = "SELECT * FROM " + DBUtlis.TABLE_TARIFF;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    Tariff tariff = new Tariff();
                    tariff.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.KEY_ID))));
                    tariff.setCityId(c.getInt(c.getColumnIndex(DBUtlis.CITY_ID)));
                    tariff.setName(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID)), language_id));
                    tariff.setIsDefault(c.getString(c.getColumnIndex(DBUtlis.IS_DEFAULT)));
                    tariff.setCarModels(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.CAR_MODELS_TR_ID)), language_id));
                    tariff.setShortname(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.SHORTNAME_TR_ID)), language_id));
                    tariff.setAirportMinPrice(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.AIRPORT_MIN_PRICE))));

                    // add interval list
                    List<TariffInterval> allIntervals = tariffIntervalDBManager.getAllTariffIntervals(db, Integer.parseInt(tariff.getID()), language_id);
                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : INTERVAL LIST : tariff_id : " + tariff.getID() + " : list : " + allIntervals);
                    tariff.setIntervals(allIntervals);
                    // add service list
                    List<TariffService> allServices = tariffServiceDBManager.getAllTariffServices(db, Integer.parseInt(tariff.getID()), language_id);
                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : SERVICE LIST : tariff_id : " + tariff.getID() + " : list : " + allServices);
                    tariff.setServices(allServices);
//                    // add fixed price list
//                    List<FixedPriceRoute> allFixedPrices = tariffFixedPriceDBManager.getAllTariffFixedPrices(db, Integer.parseInt(tariff.getID()));
//                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DB : FIXEDPRICES LIST : tariff_id : " + tariff.getID() + " : list : " + allFixedPrices);
//                    tariff.setFixedPriceRoutes(allFixedPrices);

                    // adding to tariff list
                    tariffs.add(tariff);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TariffDBManager.getAllTariffs : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                db.close();
            }
        }
        return tariffs;

    } // end method getAllTariffs

}

