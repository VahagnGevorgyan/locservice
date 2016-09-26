package com.locservice.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.Railstation;
import com.locservice.application.LocServicePreferences;
import com.locservice.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 22 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class RailstationDBManager extends DatabaseManager {

    private static final String TAG = RailstationDBManager.class.getSimpleName();

    private Context mContext;

    public RailstationDBManager(Context context) {
        super(context);
    }


    /**
     * SetRailstations: Method for setting airports in DB
     * @param railstationList - list of railstations
     * @param last_tr_id - last translation id
     * @param language_id - language id
     */
    public void setRailstations(List<Railstation> railstationList, int last_tr_id, int language_id, int city_id) {
        if(CMAppGlobals.DB_DEBUG)
            Logger.i(TAG, ":: DB : RailstationDBManager.setRailstations : railstationList : " + railstationList
                    + " : last_tr_id : " + last_tr_id
                    + " : language_id : " + language_id
                    + " : city_id : " + city_id);

        SQLiteDatabase db = getWritableDatabase();
        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);

        for (Railstation item :
                railstationList) {
            if (item != null) {
                db.beginTransaction();
                try {
                    if(CheckIsDataAlreadyInDBorNot(DBUtlis.TABLE_RAILSTATION, DBUtlis.KEY_ID, String.valueOf(item.getId()))) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : RailstationDBManager.setRailstations : UPDATE : airport id : " + item.getId());

                        String selectQuery = "select " + DBUtlis.CITY_ID +
                                ", " + DBUtlis.NAME_TR_ID +
                                ", " + DBUtlis.LATITUDE +
                                ", " + DBUtlis.LONGITUDE +
                                " from " + DBUtlis.TABLE_RAILSTATION +
                                " where " + DBUtlis.KEY_ID + " = " + item.getId();
                        Cursor c = null;
                        try {
                            c = db.rawQuery(selectQuery, null);
                            int name_tr_id = 0;
                            if (c != null && c.moveToFirst()) {
                                do {
                                    name_tr_id = c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID));
                                } while (c.moveToNext());
                            }
                            if(name_tr_id > 0)
                                translationDBManager.createTranslation(db, name_tr_id, last_tr_id, language_id, item.getName());
                        } catch (Exception e) {
                            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : RailstationDBManager.setRailstations : error : " + e.getMessage()!=null?e.getMessage():"");
                        } finally {
                            if (c != null && !c.isClosed()) {
                                c.close();
                            }
                        }
                    } else {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : RailstationDBManager.setRailstations : CREATE : airport id : " + item.getId());

                        // TRANSLATIONS
                        int name_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getName());
                        last_tr_id = name_tr_id;
                        // CREATE railstation
                        db.execSQL("insert into " + DBUtlis.TABLE_RAILSTATION + "("
                                + DBUtlis.KEY_ID + ","
                                + DBUtlis.CITY_ID + ","
                                + DBUtlis.NAME_TR_ID + ","
                                + DBUtlis.LATITUDE + ","
                                + DBUtlis.LONGITUDE+
                                ") values ("
                                + item.getId() + ","
                                + city_id + ","
                                + name_tr_id + ",'"
                                + item.getLatitude() + "','"
                                + item.getLongitude()
                                + "')");
                    }

                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : RailstationDBManager.setRailstations : END last_tr_id : " + last_tr_id);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }
        LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.LAST_TR_ID, last_tr_id);

    } // end method setRailstations

    /**
     * GetAllRailstations: getting all Railstations
     *
     * @return
     */
    public List<Railstation> getAllRailstations(int language_id, int city_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : RailstationDBManager.getAllRailstations : city_id : " + city_id + " : language_id : " + language_id);

        List<Railstation> railstations = new ArrayList<Railstation>();
        Cursor c = null;
        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);

        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_RAILSTATION + " WHERE " + DBUtlis.CITY_ID + " = " + city_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    Railstation railstation = new Railstation();
                    railstation.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.KEY_ID))));
                    railstation.setName(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID)), language_id));
                    railstation.setLatitude(String.valueOf(c.getString(c.getColumnIndex(DBUtlis.LATITUDE))));
                    railstation.setLongitude(String.valueOf(c.getString(c.getColumnIndex(DBUtlis.LONGITUDE))));

                    // adding to railstation list
                    railstations.add(railstation);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : RailstationDBManager.getAllRailstations : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed() && db != null) {
                c.close();
            }
        }
        return railstations;

    } // end method getAllRailstations

}
