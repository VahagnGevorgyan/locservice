package com.locservice.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.Airport;
import com.locservice.api.entities.Terminal;
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
public class AirportDBManager extends DatabaseManager {

    private static final String TAG = AirportDBManager.class.getSimpleName();

    private Context mContext;

    public AirportDBManager(Context context) {
        super(context);
    }


    /**
     * SetAirports: Method for setting airports in DB
     * @param airportList - list of airports
     * @param last_tr_id - last translation id
     * @param language_id - language id
     */
    public void setAirports(List<Airport> airportList, int last_tr_id, int language_id, int city_id) {
        if(CMAppGlobals.DB_DEBUG)
            Logger.i(TAG, ":: DB : AirportDBManager.setAirports : airportList : " + airportList
                    + " : last_tr_id : " + last_tr_id
                    + " : language_id : " + language_id
                    + " : city_id : " + city_id);

        SQLiteDatabase db = getWritableDatabase();
        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        TerminalDBManager terminalDBManager = new TerminalDBManager(this.mContext);

        for (Airport item :
                airportList) {
            if (item != null) {
                db.beginTransaction();
                try {
                    if(CheckIsDataAlreadyInDBorNot(DBUtlis.TABLE_AIRPORT, DBUtlis.KEY_ID, String.valueOf(item.getId()))) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : AirportDBManager.setAirports : UPDATE : airport id : " + item.getId());

                        String selectQuery = "select " + DBUtlis.CITY_ID +
                                ", " + DBUtlis.NAME_TR_ID +
                                ", " + DBUtlis.LATITUDE +
                                ", " + DBUtlis.LONGITUDE +
                                " from " + DBUtlis.TABLE_AIRPORT +
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
                            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : AirportDBManager.setAirports : error : " + e.getMessage()!=null?e.getMessage():"");
                        } finally {
                            if (c != null && !c.isClosed()) {
                                c.close();
                            }
                        }
                    } else {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : AirportDBManager.setAirports : CREATE : airport id : " + item.getId());

                        // TRANSLATIONS
                        int name_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getName());
                        last_tr_id = name_tr_id;
                        // CREATE airport
                        db.execSQL("insert into " + DBUtlis.TABLE_AIRPORT + "("
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
                    // INSERT/UPDATE terminals to DB
                    last_tr_id = terminalDBManager.setAirportTerminals(db, item.getTerminals(), Integer.parseInt(item.getId()), last_tr_id, language_id);

                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : AirportDBManager.setAirports : END last_tr_id : " + last_tr_id);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }
        LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.LAST_TR_ID, last_tr_id);

    } // end method setAirports

    /**
     * GetAllAirports: getting all Airports
     *
     * @return - list of all airports
     */
    public List<Airport> getAllAirports(int language_id, int city_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : AirportDBManager.getAllAirports : language_id : " +
                language_id + " : city_id : " + city_id);

        SQLiteDatabase db = getReadableDatabase();
        List<Airport> airports = new ArrayList<Airport>();
        Cursor c = null;

        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        TerminalDBManager terminalDBManager = new TerminalDBManager(this.mContext);

        String selectQuery = "SELECT * FROM " + DBUtlis.TABLE_AIRPORT +
                " WHERE " + DBUtlis.CITY_ID + " = " + city_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    Airport airport = new Airport();
                    airport.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.KEY_ID))));
                    airport.setName(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID)), language_id));
                    airport.setLatitude(c.getString(c.getColumnIndex(DBUtlis.LATITUDE)));
                    airport.setLongitude(c.getString(c.getColumnIndex(DBUtlis.LONGITUDE)));

                    // add terminal list
                    List<Terminal> allTerminals = terminalDBManager.getAllTerminals(db, Integer.parseInt(airport.getId()), language_id);
                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : AirportDBManager.getAllAirports : airport_id : " + airport.getId() + " : list : " + allTerminals);
                    airport.setTerminals(allTerminals);

                    // adding to airport list
                    airports.add(airport);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : AirportDBManager.getAllAirports : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                db.close();
            }
        }
        return airports;

    } // end method getAllAirports


    /**
     * Method for getting airport by id
     * @param airportId
     * @return
     */
    public Airport getAirportByID(String airportId, int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: AirportDBManager.getAirportByID : airportId : " + airportId);

        Airport airport = new Airport();

        SQLiteDatabase db = getReadableDatabase();
        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_AIRPORT + " WHERE " + DBUtlis.KEY_ID + " = " + airportId + "";

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    airport.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.KEY_ID))));
                    airport.setName(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID)), language_id));
                    airport.setLatitude(String.valueOf(c.getFloat(c.getColumnIndex(DBUtlis.LATITUDE))));
                    airport.setLongitude(String.valueOf(c.getFloat(c.getColumnIndex(DBUtlis.LONGITUDE))));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : AirportDBManager.getAirportByID : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return airport;

    } // end method getAirportByID

}
