package com.locservice.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.Terminal;
import com.locservice.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 22 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class TerminalDBManager extends DatabaseManager {

    private static final String TAG = TerminalDBManager.class.getSimpleName();

    private Context mContext;

    public TerminalDBManager(Context context) {
        super(context);
    }


    /**
     * Method for adding airport terminals in DB
     * @param terminalList - list of airport terminals
     * @param airport_id - airport id
     * @param last_tr_id - last translation id
     * @param language_id - language id
     */
    public int setAirportTerminals(SQLiteDatabase db, List<Terminal> terminalList, int airport_id, int last_tr_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)
            Logger.i(TAG, ":: DB : TerminalDBManager.setAirportTerminals : terminalList: " + terminalList
                    + " : airport_id : " + airport_id
                    + " : last_tr_id : " + last_tr_id
                    + " : language_id : " + language_id);

        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);

        for (Terminal item :
                terminalList) {
            if (item != null) {
                db.beginTransaction();
                try {
                    if(!CheckIsAirportTerminalPairExists(db, DBUtlis.TABLE_AIRPORT_TERMINAL, DBUtlis.AIRPORT_ID,
                            String.valueOf(airport_id), DBUtlis.TERMINAL_ID, String.valueOf(item.getId()))) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TerminalDBManager.setAirportTerminals : CREATE AIRPORT/TERMINAL Pair : airport_id : " +
                                airport_id + " : terminal_id : " + item.getId());
                        // CREATE AirportTerminal
                        db.execSQL("insert into " + DBUtlis.TABLE_AIRPORT_TERMINAL + "("
                                + DBUtlis.AIRPORT_ID + ","
                                + DBUtlis.TERMINAL_ID +
                                ") values("
                                + airport_id + ","
                                + item.getId()
                                + ")");
                    }
                    if(CheckIsDataAlreadyInDBorNot(db, DBUtlis.TABLE_TERMINAL, DBUtlis.KEY_ID, String.valueOf(item.getId()))) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TerminalDBManager.setAirportTerminals : UPDATE TERMINAL : terminal_id : " + item.getId());

                        String selectQuery = "select " + DBUtlis.NAME_TR_ID +
                                " from " + DBUtlis.TABLE_TERMINAL +
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
                            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TerminalDBManager.setAirportTerminals : error : " + e.getMessage()!=null?e.getMessage():"");
                        } finally {
                            if (c != null && !c.isClosed()) {
                                c.close();
                            }
                        }
                    } else {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TerminalDBManager.setAirportTerminals : CREATE TERMINAL : airport_id : "
                                + airport_id + " : terminal_id : " + item.getId());

                        // TRANSLATIONS
                        int name_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, item.getName());
                        last_tr_id = name_tr_id;
                        // CREATE Terminal
                        db.execSQL("insert into " + DBUtlis.TABLE_TERMINAL + "("
                                + DBUtlis.KEY_ID + ","
                                + DBUtlis.NAME_TR_ID + ","
                                + DBUtlis.LATITUDE + ","
                                + DBUtlis.LONGITUDE +
                                ") values("
                                + item.getId() + ","
                                + name_tr_id + ","
                                + item.getLatitude() + ","
                                + item.getLongitude()
                                + ")");
                    }
                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TerminalDBManager.setAirportTerminals : END last_tr_id : " + last_tr_id);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }
        return last_tr_id;

    } // end method setAirportTerminals

    /**
     * GetAllTerminals: getting all Airport Terminals
     *
     * @return - list of terminals
     */
    public List<Terminal> getAllTerminals(SQLiteDatabase db, int airport_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TerminalDBManager.getAllTerminals : airport_id : " + airport_id);

        List<Terminal> terminals = new ArrayList<Terminal>();
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_AIRPORT_TERMINAL + " WHERE " + DBUtlis.AIRPORT_ID + " = " + airport_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    int terminal_id = c.getInt(c.getColumnIndex(DBUtlis.TERMINAL_ID));
                    Terminal terminal = getTerminalById(db, terminal_id, language_id);

                    // adding to terminals list
                    terminals.add(terminal);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TerminalDBManager.getAllTerminals : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return terminals;

    } // end method GetAllTerminals

    /**
     * Method for getting terminal by id
     * @param db - database
     * @param terminal_id - terminal id
     * @param language_id - language id
     * @return - terminal
     */
    public Terminal getTerminalById(SQLiteDatabase db, int terminal_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: TerminalDBManager.getTerminalById : terminal_id : " + terminal_id + " : language_id : " + language_id);

        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        Terminal terminal = new Terminal();
        terminal.setId(String.valueOf(terminal_id));
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_TERMINAL + " WHERE " + DBUtlis.KEY_ID + " = " + terminal_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    terminal.setName(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID)), language_id));
                    terminal.setLatitude(c.getString(c.getColumnIndex(DBUtlis.LATITUDE)));
                    terminal.setLongitude(c.getString(c.getColumnIndex(DBUtlis.LONGITUDE)));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TerminalDBManager.getTerminalById : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return terminal;

    } // end method getTerminalById

    /**
     * Method for getting terminal by id
     * @param terminal_id - terminal id
     * @param language_id - language id
     * @return - terminal
     */
    public Terminal getTerminalById(int terminal_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: TerminalDBManager.getTerminalById : terminal_id : " + terminal_id + " : language_id : " + language_id);

        SQLiteDatabase db = getReadableDatabase();
        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);
        Terminal terminal = new Terminal();
        terminal.setId(String.valueOf(terminal_id));
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_TERMINAL + " WHERE " + DBUtlis.KEY_ID + " = " + terminal_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    terminal.setName(translationDBManager.getTranslation(db, c.getInt(c.getColumnIndex(DBUtlis.NAME_TR_ID)), language_id));
                    terminal.setLatitude(c.getString(c.getColumnIndex(DBUtlis.LATITUDE)));
                    terminal.setLongitude(c.getString(c.getColumnIndex(DBUtlis.LONGITUDE)));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TerminalDBManager.getTerminalById : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return terminal;

    } // end method getTerminalById


    /**
     * Method for checking db data
     *
     * @param TableName
     * @param airportField
     * @param airportFieldValue
     * @param terminalField
     * @param terminalFieldValue
     * @return
     */
    public boolean CheckIsAirportTerminalPairExists(SQLiteDatabase sqldb,
                                                  String TableName,
                                                  String airportField, String airportFieldValue,
                                                  String terminalField, String terminalFieldValue) {
        String Query = "Select * from " + TableName + " where "
                + airportField + " = " + airportFieldValue + " and "
                + terminalField + " = " + terminalFieldValue;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    } // end method CheckIsAirportTerminalPairExists


}
