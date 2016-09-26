package com.locservice.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.utils.Logger;


/**
 * Created by Vahagn Gevorgyan
 * 23 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static final String TAG = DatabaseManager.class.getSimpleName();

    private Context mContext;

    public DatabaseManager(Context context) {
        super(context, CMApplication.DATABASE_NAME, null, CMApplication.DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (CMAppGlobals.DB_DEBUG) Logger.i(TAG, ":: DB : DatabaseManager.onCreate ");

        db.execSQL(DBQueries.CREATE_LANGUAGE_TABLE);
        db.execSQL(DBQueries.CREATE_COUNTRY_TABLE);
        db.execSQL(DBQueries.CREATE_CITY_TABLE);
        db.execSQL(DBQueries.CREATE_TRANSLATION_TABLE);
        db.execSQL(DBQueries.CREATE_TARIFF_TABLE);
        db.execSQL(DBQueries.CREATE_TARIFF_SERVICE_TABLE);
        db.execSQL(DBQueries.CREATE_SERVICE_TABLE);
        db.execSQL(DBQueries.CREATE_TARIFF_FIXED_PRICE_TABLE);
        db.execSQL(DBQueries.CREATE_TARIFF_INTERVAL_TABLE);
        db.execSQL(DBQueries.CREATE_INTERVAL_TABLE);
        db.execSQL(DBQueries.CREATE_AIRPORT);
        db.execSQL(DBQueries.CREATE_AIRPORT_TERMINAL_TABLE);
        db.execSQL(DBQueries.CREATE_TERMINAL);
        db.execSQL(DBQueries.CREATE_RAILSTATION);
        db.execSQL(DBQueries.CREATE_FAVORITE);
        LanguageDBManager.fillLanguageTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (CMAppGlobals.DB_DEBUG) Logger.i(TAG, ":: DB : DatabaseManager.onUpgrade ");

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_lANGUAGE);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_TRANSLATION);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_COUNTRY);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_CITY);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_TARIFF);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_TARIFF_FIXED_PRICE);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_TARIFF_SERVICE);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_SERVICE);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_TARIFF_INTERVAL);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_INTERVAL);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_AIRPORT);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_AIRPORT_TERMINAL);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_TERMINAL);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_RAILSTATION);
        db.execSQL("DROP TABLE IF EXISTS " + DBUtlis.TABLE_FAVORITE);

        // Create tables again
        onCreate(db);
    }

    /**
     * Method for checking db data
     *
     * @param TableName
     * @param dbfield
     * @param fieldValue
     * @return
     */
    public boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                               String dbfield, String fieldValue) {
        SQLiteDatabase sqldb = getWritableDatabase();
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * Method for checking db data
     *
     * @param TableName
     * @param dbfield
     * @param fieldValue
     * @return
     */
    public boolean CheckIsDataAlreadyInDBorNot(SQLiteDatabase sqldb,
                                               String TableName,
                                               String dbfield, String fieldValue) {
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }


}