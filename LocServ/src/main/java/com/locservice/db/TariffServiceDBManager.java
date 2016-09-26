package com.locservice.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.TariffService;
import com.locservice.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 15 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class TariffServiceDBManager extends DatabaseManager {

    private static final String TAG = TariffServiceDBManager.class.getSimpleName();

    private Context mContext;

    public TariffServiceDBManager(Context context) {
        super(context);
    }

    /**
     * Method for adding tariff services in DB
     * @param tariffServiceList
     * @param last_tr_id
     * @param language_id
     */
    public int setTariffServices(SQLiteDatabase db, List<TariffService> tariffServiceList, int tariff_id, int last_tr_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)
            Logger.i(TAG, ":: DB : TariffServiceDBManager.setTariffServices : tariffServiceList: " + tariffServiceList
                    + " : tariff_id : " + tariff_id
                    + " : last_tr_id : " + last_tr_id
                    + " : language_id : " + language_id);

        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);

        for (TariffService item :
                tariffServiceList) {
            if (item != null) {
                db.beginTransaction();
                try {
                    if(!CheckIsTariffServicePairExists(db, DBUtlis.TABLE_TARIFF_SERVICE, DBUtlis.TARIFF_ID,
                            String.valueOf(tariff_id), DBUtlis.SERVICE_ID, String.valueOf(item.getId()))) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffServiceDBManager.setTariffServices : CREATE TARIFF/SERVICE Pair : tariff_id : " +
                                tariff_id + " : service_id : " + item.getId());
                        // CREATE TariffService
                        db.execSQL("insert into " + DBUtlis.TABLE_TARIFF_SERVICE + "("
                                + DBUtlis.SERVICE_ID + ","
                                + DBUtlis.TARIFF_ID +
                                ") values("
                                + item.getId() + ","
                                + tariff_id
                                + ")");
                    }
                    if(CheckIsDataAlreadyInDBorNot(db, DBUtlis.TABLE_SERVICE, DBUtlis.KEY_ID, String.valueOf(item.getId()))) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffServiceDBManager.setTariffServices : UPDATE SERVICE : tariff_id : " + tariff_id);

                        String selectQuery = "select " + DBUtlis.TITLE_TR_ID +
                                ", " + DBUtlis.SHORT_TITLE_TR_ID +
                                ", " + DBUtlis.PRICE +
                                ", " + DBUtlis.FIELD +
                                " from " + DBUtlis.TABLE_SERVICE +
                                " where " + DBUtlis.KEY_ID + " = " + item.getId();
                        Cursor c = null;
                        try {
                            c = db.rawQuery(selectQuery, null);
                            int title_tr_id = 0;
                            int short_title_tr_id = 0;
                            if (c != null && c.moveToFirst()) {
                                do {
                                    title_tr_id = c.getInt(c.getColumnIndex(DBUtlis.TITLE_TR_ID));
                                    short_title_tr_id = c.getInt(c.getColumnIndex(DBUtlis.SHORT_TITLE_TR_ID));
                                } while (c.moveToNext());
                            }

                            if(title_tr_id > 0)
                                translationDBManager.createTranslation(db, title_tr_id, last_tr_id, language_id, item.getTitle());
                            if(short_title_tr_id > 0)
                                translationDBManager.createTranslation(db, short_title_tr_id, last_tr_id, language_id, item.getShortTitle());
//                            if(field_tr_id > 0)
//                                translationDBManager.createTranslation(db, field_tr_id, last_tr_id, language_id, item.getField());
                        } catch (Exception e) {
                            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TariffServiceDBManager.setTariffServices : error : " + e.getMessage()!=null?e.getMessage():"");
                        } finally {
                            if (c != null && !c.isClosed()) {
                                c.close();
                            }
                        }
                    } else {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffServiceDBManager.setTariffServices : CREATE SERVICE : tariff_id : "
                                + tariff_id + " : service_id : " + item.getId());

                        // TRANSLATIONS
                        int title_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getTitle());
                        last_tr_id = title_tr_id;
                        int short_title_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getShortTitle());
                        last_tr_id = short_title_tr_id;
                        // CREATE Service
                        db.execSQL("insert into " + DBUtlis.TABLE_SERVICE + "("
                                + DBUtlis.KEY_ID + ","
                                + DBUtlis.TITLE_TR_ID + ","
                                + DBUtlis.SHORT_TITLE_TR_ID + ","
                                + DBUtlis.PRICE + ","
                                + DBUtlis.FIELD +
                                ") values("
                                + item.getId() + ","
                                + title_tr_id + ","
                                + short_title_tr_id + ","
                                + item.getPrice() + ",'"
                                + item.getField()
                                + "')");
                    }
                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffServiceDBManager.setTariffServices : END last_tr_id : " + last_tr_id);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }
        return last_tr_id;

    } // end method setTariffServices

    /**
     * GetAllTariffServices: getting all Tariff Services
     *
     * @return
     */
    public List<TariffService> getAllTariffServices(SQLiteDatabase db, int tariff_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffServiceDBManager.getAllTariffServices : tariff_id : " + tariff_id);

        List<TariffService> services = new ArrayList<TariffService>();
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_TARIFF_SERVICE + " WHERE " + DBUtlis.TARIFF_ID + " = " + tariff_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    int service_id = c.getInt(c.getColumnIndex(DBUtlis.SERVICE_ID));
                    TariffService service = getServiceById(db, service_id, language_id);

                    // adding to services list
                    services.add(service);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TariffServiceDBManager.getAllTariffServices : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return services;

    } // end method getAllTariffServices

    /**
     * Method for getting service by id
     * @param db
     * @param service_id
     * @param language_id
     * @return
     */
    public TariffService getServiceById(SQLiteDatabase db, int service_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: TariffServiceDBManager.getServiceById : service_id : " + service_id + " : language_id : " + language_id);

        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        TariffService service = new TariffService();
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_SERVICE + " WHERE " + DBUtlis.KEY_ID + " = " + service_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    service.setTitle(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.TITLE_TR_ID)), language_id));
                    service.setShortTitle(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.SHORT_TITLE_TR_ID)), language_id));
                    service.setPrice(c.getInt(c.getColumnIndex(DBUtlis.PRICE)));
                    service.setField(c.getString(c.getColumnIndex(DBUtlis.FIELD)));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TariffServiceDBManager.getServiceById : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return service;

    } // end method getServiceById

    /**
     * Method for getting service by id
     * @param field
     * @param language_id
     * @return
     */
    public TariffService getServiceByField(String field, int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: TariffServiceDBManager.getServiceByField : field : " + field + " : language_id : " + language_id);

        SQLiteDatabase db = getReadableDatabase();
        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        TariffService service = new TariffService();
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_SERVICE + " WHERE " + DBUtlis.FIELD + " = '" + field + "'";

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    service.setTitle(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.TITLE_TR_ID)), language_id));
                    service.setShortTitle(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.SHORT_TITLE_TR_ID)), language_id));
                    service.setPrice(c.getInt(c.getColumnIndex(DBUtlis.PRICE)));
                    service.setField(c.getString(c.getColumnIndex(DBUtlis.FIELD)));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TariffServiceDBManager.getServiceById : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return service;

    } // end method getServiceByField

    /**
     * Method for checking db data
     *
     * @param TableName
     * @param tariffField
     * @param tariffFieldValue
     * @param serviceField
     * @param serviceFieldValue
     * @return
     */
    public boolean CheckIsTariffServicePairExists(SQLiteDatabase sqldb,
                                               String TableName,
                                               String tariffField, String tariffFieldValue,
                                               String serviceField, String serviceFieldValue) {
        String Query = "Select * from " + TableName + " where "
                + tariffField + " = " + tariffFieldValue + " and "
                + serviceField + " = " + serviceFieldValue;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    } // end method CheckIsTariffServicePairExists

}
