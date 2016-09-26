package com.locservice.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.TariffInterval;
import com.locservice.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 15 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class TariffIntervalDBManager extends DatabaseManager {

    private static final String TAG = TranslationDBManager.class.getSimpleName();

    private Context mContext;

    public TariffIntervalDBManager(Context context) {
        super(context);
    }

    /**
     * Method for adding tariff intervals in DB
     * @param tariffIntervalList
     * @param last_tr_id
     * @param language_id
     */
    public int setTariffIntervals(SQLiteDatabase db, List<TariffInterval> tariffIntervalList, int tariff_id, int last_tr_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)
            Logger.i(TAG, ":: DB : TariffIntervalDBManager.setTariffIntervals : tariffIntervalList: " + tariffIntervalList
                    + " : tariff_id : " + tariff_id
                    + " : last_tr_id : " + last_tr_id

                    + " : language_id : " + language_id);

        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);

        for (TariffInterval item :
                tariffIntervalList) {
            if (item != null) {
                db.beginTransaction();
                try {
                    if(!CheckIsTariffIntervalPairExists(db, DBUtlis.TABLE_TARIFF_INTERVAL, DBUtlis.TARIFF_ID,
                            String.valueOf(tariff_id), DBUtlis.INTERVAL_ID, String.valueOf(item.getId()))) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffServiceDBManager.setTariffIntervals : CREATE TARIFF/INTERVAL Pair : tariff_id : " +
                                tariff_id + " : interval_id : " + item.getId());
                        // CREATE TariffInterval
                        db.execSQL("insert into " + DBUtlis.TABLE_TARIFF_INTERVAL + "("
                                + DBUtlis.INTERVAL_ID + ","
                                + DBUtlis.TARIFF_ID +
                                ") values("
                                + item.getId() + ","
                                + tariff_id
                                + ")");
                    }
                    if(CheckIsDataAlreadyInDBorNot(db, DBUtlis.TABLE_INTERVAL, DBUtlis.KEY_ID, String.valueOf(item.getId()))) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffIntervalDBManager.setTariffIntervals : UPDATE INTERVAL : tariff_id : " + tariff_id);

                        String selectQuery = "select " + DBUtlis.IS_NIGHT +
                                ", " + DBUtlis.PRICE +
                                ", " + DBUtlis.PREPAID_TIME +
                                ", " + DBUtlis.NAME_TR_ID +
                                ", " + DBUtlis.DESC_STATIC_TR_ID +
                                " from " + DBUtlis.TABLE_INTERVAL +
                                " where " + DBUtlis.KEY_ID + " = " + item.getId();

                        Cursor c = null;
                        try {
                            c = db.rawQuery(selectQuery, null);
                            int name_tr_id = 0;
                            int desc_static_tr_id = 0;
                            if (c != null && c.moveToFirst()) {
                                do {
                                    name_tr_id = c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID));
                                    desc_static_tr_id = c.getInt(c.getColumnIndex(DBUtlis.DESC_STATIC_TR_ID));
                                } while (c.moveToNext());
                            }
                            if(name_tr_id > 0)
                                translationDBManager.createTranslation(db, name_tr_id, last_tr_id, language_id, item.getName());
                            if(desc_static_tr_id > 0)
                                translationDBManager.createTranslation(db, desc_static_tr_id, last_tr_id, language_id, item.getDescStatic());
                        } catch (Exception e) {
                            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TariffIntervalDBManager.setTariffIntervals : error : " + e.getMessage()!=null?e.getMessage():"");
                        } finally {
                            if (c != null && !c.isClosed() && db != null) {
                                c.close();
                            }
                        }
                    } else {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffIntervalDBManager.setTariffIntervals : CREATE : tariff_id : " + tariff_id);

                        // TRANSLATIONS
                        int name_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getName());
                        last_tr_id = name_tr_id;
                        int desc_static_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getDescStatic());
                        last_tr_id = desc_static_tr_id;
                        // CREATE TariffInterval
                        db.execSQL("insert into " + DBUtlis.TABLE_INTERVAL + "("
                                + DBUtlis.KEY_ID + ","
                                + DBUtlis.IS_NIGHT + ","
                                + DBUtlis.PRICE + ","
                                + DBUtlis.PREPAID_TIME + ","
                                + DBUtlis.NAME_TR_ID + ","
                                + DBUtlis.DESC_STATIC_TR_ID +
                                ") values("
                                + item.getId() + ","
                                + item.getType_by_date() + ","
                                + item.getPrice() + ","
                                + item.getPrepaid_time() + ","
                                + name_tr_id + ","
                                + desc_static_tr_id
                                + ")");
                    }
                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffIntervalDBManager.setTariffIntervals : END last_tr_id : " + last_tr_id);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }
        return last_tr_id;

    } // end method setTariffIntervals

    /**
     * GetAllTariffIntervals: getting all Tariff Intervals
     *
     * @return
     */
    public List<TariffInterval> getAllTariffIntervals(SQLiteDatabase db, int tariff_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffIntervalDBManager.getAllTariffIntervals : tariff_id : " + tariff_id);

        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        List<TariffInterval> intervals = new ArrayList<TariffInterval>();
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_TARIFF_INTERVAL + " WHERE " + DBUtlis.TARIFF_ID + " = " + tariff_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    int interval_id = c.getInt(c.getColumnIndex(DBUtlis.INTERVAL_ID));
                    TariffInterval interval = getIntervalById(db, interval_id, language_id);

                    // adding to interval list
                    intervals.add(interval);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TariffIntervalDBManager.getAllTariffIntervals : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return intervals;

    } // end method getAllTariffIntervals

    /**
     * Method for getting service by id
     * @param db
     * @param interval_id
     * @param language_id
     * @return
     */
    public TariffInterval getIntervalById(SQLiteDatabase db, int interval_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: TariffServiceDBManager.getIntervalById : interval_id : " + interval_id + " : language_id : " + language_id);

        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        TariffInterval interval = new TariffInterval();
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_INTERVAL + " WHERE " + DBUtlis.KEY_ID + " = " + interval_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    interval.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.KEY_ID))));
                    interval.setType_by_date(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.IS_NIGHT))));
                    interval.setPrice(c.getString(c.getColumnIndex(DBUtlis.PRICE)));
                    interval.setPrepaid_time(c.getString(c.getColumnIndex(DBUtlis.PREPAID_TIME)));
                    interval.setName(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID)), language_id));
                    interval.setDescStatic(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.DESC_STATIC_TR_ID)), language_id));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TariffServiceDBManager.getIntervalById : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return interval;

    } // end method getIntervalById

    /**
     * Method for checking db data
     *
     * @param TableName
     * @param tariffField
     * @param tariffFieldValue
     * @param intervalField
     * @param intervalFieldValue
     * @return
     */
    public boolean CheckIsTariffIntervalPairExists(SQLiteDatabase sqldb,
                                                  String TableName,
                                                  String tariffField, String tariffFieldValue,
                                                  String intervalField, String intervalFieldValue) {
        String Query = "Select * from " + TableName + " where "
                + tariffField + " = " + tariffFieldValue + " and "
                + intervalField + " = " + intervalFieldValue;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    } // end method CheckIsTariffIntervalPairExists

}
