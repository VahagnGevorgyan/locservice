package com.locservice.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.Language;
import com.locservice.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 23 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class LanguageDBManager extends DatabaseManager {

    private static final String TAG = LanguageDBManager.class.getSimpleName();

    private Context mContext;

    public LanguageDBManager(Context context) {
        super(context);
    }


    /**
     * GetLanguages: Get all languages from language table
     *
     * @return languageList
     */
    public List<Language> getAllLanguages() {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : LanguageDBManager.getAllLanguages ");

        List<Language> languageList = new ArrayList<Language>();
        if(tableExist ()) {
            // Select All Query
            String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_lANGUAGE;

            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            try {
                // looping through all rows and adding to list
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Language language = new Language();
                        language.setId(Integer.parseInt(cursor.getString(0)));
                        language.setLong_name(cursor.getString(1));
                        language.setShort_name(cursor.getString(2));

                        // Adding language to list
                        languageList.add(language);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                if(CMAppGlobals.DB_DEBUG) Logger.e(TAG, ":: LanguageDBManager.getAllContacts : error : " + e!=null?e.getMessage():"");
            } finally {
                if (cursor != null && !cursor.isClosed() && db != null) {
                    cursor.close();
                    db.close();
                }
            }
        } else {
            languageList = fillLanguageTable();
        }

        return languageList;

    } // end method getAllLanguages

    /**
     * TableExist: Check whether table is empty
     *
     * @return
     */
    public boolean tableExist () {
        String countQuery = "SELECT  * FROM " + DBUtlis.TABLE_lANGUAGE;
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.rawQuery(countQuery, null);
        boolean rowExists;

        try {
            if (mCursor.moveToFirst()) {
                rowExists = true;
            } else {
                rowExists = false;
            }
        } finally {
            if(mCursor != null && !mCursor.isClosed()){
                mCursor.close();
                db.close();
            }
        }

        return rowExists;

    } // end method tableExist

    /**
     * TableExist: Check whether table is empty
     *
     * @return
     */
    public boolean tableExist (SQLiteDatabase db) {
        String countQuery = "SELECT  * FROM " + DBUtlis.TABLE_lANGUAGE;
        Cursor mCursor = db.rawQuery(countQuery, null);
        boolean rowExists;

        try {
            if (mCursor.moveToFirst()) {
                rowExists = true;
            } else {
                rowExists = false;
            }
        } finally {
            if(mCursor != null && !mCursor.isClosed()){
                mCursor.close();
                db.close();
            }
        }

        return rowExists;

    } // end method tableExist

    /**
     * FillTable: Fill table data
     *
     * @return
     */
    public List<Language> fillLanguageTable () {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : LanguageDBManager.fillTable ");

        List<Language> languages = getLanguagesList();
        SQLiteDatabase db = getWritableDatabase();
        if(languages != null && languages.size() > 0) {
            for (Language item :
                    languages) {
                db.execSQL("insert into " + DBUtlis.TABLE_lANGUAGE + "(" + DBUtlis.NAME + ","
                        + DBUtlis.SHORT_NAME + ") values('" + item.getLong_name() + "','" + item.getShort_name() + "')");
            }
        }
        return  languages;

    } // end method fillTable

    /**
     * FillTable: Fill table data
     *
     * @return
     */
    public static List<Language> fillLanguageTable (SQLiteDatabase db) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : LanguageDBManager.fillTable ");

        List<Language> languages = getLanguagesList();
        if(languages != null && languages.size() > 0) {
            for (Language item :
                    languages) {
                db.execSQL("insert into " + DBUtlis.TABLE_lANGUAGE + "(" + DBUtlis.NAME + ","
                        + DBUtlis.SHORT_NAME + ") values('" + item.getLong_name() + "','" + item.getShort_name() + "')");
            }
        }
        return  languages;

    } // end method fillTable

    /**
     * Method for getting language id by locale
     * @param locale
     * @return
     */
    public int getLanguageIdByLocale(String locale) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : LanguageDBManager.getLanguageIdByLocale : locale : " + locale);

        int languageId = 0;
        if(!tableExist ()) {
            fillLanguageTable();
        }
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT " + DBUtlis.KEY_ID + " FROM " + DBUtlis.TABLE_lANGUAGE + " WHERE " + DBUtlis.SHORT_NAME + " = '" + locale + "'";
        Cursor c = db.rawQuery(selectQuery, null);

        try {
            if (c != null &&  c.moveToFirst()) {
                languageId = c.getInt(c.getColumnIndex(DBUtlis.KEY_ID));
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : LanguageDBManager.getLanguageIdByLocale : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                db.close();
            }
        }
        return languageId;

    } // end method getLanguageIdByLocale

    /**
     * Method for getting language id by locale
     * @param locale
     * @return
     */
    public int getLanguageIdByLocale(SQLiteDatabase db, String locale) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : LanguageDBManager.getLanguageIdByLocale : locale : " + locale);

        int languageId = 0;

        String selectQuery = "SELECT " + DBUtlis.KEY_ID + " FROM " + DBUtlis.TABLE_lANGUAGE + " WHERE " + DBUtlis.SHORT_NAME + " = " + locale;
        Cursor c = db.rawQuery(selectQuery, null);

        try {
            if (c != null &&  c.moveToFirst()) {
                languageId = c.getInt(c.getColumnIndex(DBUtlis.KEY_ID));
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : LanguageDBManager.getLanguageIdByLocale : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                db.close();
            }
        }
        return languageId;

    } // end method getLanguageIdByLocale


    public static List<Language> getLanguagesList() {
        List<Language> languages = new ArrayList<Language>();

        Language item = new Language();
        item.setShort_name("en");
        item.setLong_name("English");
        languages.add(item);
        item = new Language();
        item.setShort_name("de");
        item.setLong_name("German");
        languages.add(item);
        item = new Language();
        item.setShort_name("fr");
        item.setLong_name("French");
        languages.add(item);
        item = new Language();
        item.setShort_name("ru");
        item.setLong_name("Russian");
        languages.add(item);
        item = new Language();
        item.setShort_name("am");
        item.setLong_name("Armenian");
        languages.add(item);
//        item = new Language();
//        item.setShort_name("ko");
//        item.setLong_name("Korean");
//        languages.add(item);
//        item = new Language();
//        item.setShort_name("kz");
//        item.setLong_name("Kazakh");
//        languages.add(item);

        return languages;

    } // end method getLanguages


}