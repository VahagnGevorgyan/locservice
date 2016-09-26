package com.locservice.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locservice.CMAppGlobals;
import com.locservice.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 24 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class TranslationDBManager extends DatabaseManager {

    private static final String TAG = TranslationDBManager.class.getSimpleName();

    private Context mContext;

    public TranslationDBManager(Context context) {
        super(context);
    }


    /**
     * Method for creating new translation item
     * @param db
     * @param last_tr_id
     * @param lang_id
     * @param text
     */
    public int createTranslation(SQLiteDatabase db, int tr_id, int last_tr_id, int lang_id, String text) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TranslationDBManager.createTranslation : tr_id : " + tr_id +
                                                " : lang_id : " + lang_id +
                                                " : last_tr_id : " + last_tr_id +
                                                " : text : " + text);

        if(tr_id > 0) {
            if(CheckIsDataAlreadyInDBorNot(db, DBUtlis.TABLE_TRANSLATION, DBUtlis.KEY_ID, String.valueOf(tr_id))) {

                if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TranslationDBManager.createTranslation : UPDATE : tr_id : " + tr_id
                        + " : text : " + text);

                // UPDATE tr_id
                String updateQuery = "update " + DBUtlis.TABLE_TRANSLATION + " SET " +
                        DBUtlis.LANGUAGE_ID + " = " + lang_id + ", " +
                        DBUtlis.TEXT + " = '" + text + "', " +
                        DBUtlis.IS_DELETED + " = " + 0 +
                        " where " + DBUtlis.KEY_ID + " = " + tr_id;
                try {
                    db.execSQL(updateQuery);
                } catch (Exception e) {
                    if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TranslationDBManager.createTranslation : error : " + e.getMessage()!=null?e.getMessage():"");
                }
            }
        } else {
            if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TranslationDBManager.createTranslation : INSERT : tr_id : " + tr_id
                    + " : text : " + text);

            // CREATE new last_tr_id
            last_tr_id++;
            db.execSQL("insert into " + DBUtlis.TABLE_TRANSLATION + "(" + DBUtlis.KEY_ID + "," + DBUtlis.LANGUAGE_ID + ","
                    + DBUtlis.TEXT + "," + DBUtlis.IS_DELETED + ") values(" + last_tr_id + "," + lang_id + ",'" + text + "'," + 0 + ")");
        }

        return last_tr_id;

    } // end method createTranslation


    /**
     * Method for getting translation text by lang_id
     * @param db
     * @param tr_id
     * @param lang_id
     * @return
     */
    public String getTranslation(SQLiteDatabase db, int tr_id, int lang_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: TranslationDBManager.getTranslation : tr_id : " + tr_id + " : lang_id : " + lang_id);

        String trText = "";
        if(lang_id > 0) {
            Cursor c = null;
            String selectQuery = "SELECT " + DBUtlis.TEXT + " FROM "
                    + DBUtlis.TABLE_TRANSLATION + " WHERE "
                    + DBUtlis.KEY_ID + " = " + tr_id + " AND "
                    + DBUtlis.LANGUAGE_ID + " = " + lang_id;

            try {
                if(db != null) {
                    c = db.rawQuery(selectQuery, null);
                }
                if(c != null && c.getCount() > 0) {

                    c.moveToFirst();
                    trText = c.getString(c.getColumnIndex(DBUtlis.TEXT));
                }
            } catch (Exception e) {
                if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TranslationDBManager.getTranslation : error : " + e!=null?e.getMessage():"");
            } finally {
                if (c != null && !c.isClosed()) {
                    c.close();
                }
            }
        }

        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: TranslationDBManager.getTranslation : text : " + trText);
        return trText;

    } // end method getTranslation


    public List<Translation> getAllTranslations() {
        List<Translation> translationList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        String selectQuery = "SELECT * FROM " + DBUtlis.TABLE_TRANSLATION;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    Translation translation = new Translation();
                    translation.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.KEY_ID))));
                    translation.setLangId(c.getInt(c.getColumnIndex(DBUtlis.LANGUAGE_ID)));
                    translation.setText(c.getString(c.getColumnIndex(DBUtlis.TEXT)));

                    // adding to translation list
                    translationList.add(translation);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TranslationDBManager.getAllTranslations : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                db.close();
            }
        }

        return translationList;

    }


    public class Translation {

        private String id;
        private int langId;
        private String text;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getLangId() {
            return langId;
        }

        public void setLangId(int langId) {
            this.langId = langId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }


}
