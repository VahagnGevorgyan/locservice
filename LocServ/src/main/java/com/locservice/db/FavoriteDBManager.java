package com.locservice.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.GetFavoriteData;
import com.locservice.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 02 May 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class FavoriteDBManager extends DatabaseManager {

    private static final String TAG = FavoriteDBManager.class.getSimpleName();

    private Context mContext;

    public FavoriteDBManager(Context context) {
        super(context);
    }


    /**
     * SetFavoriteAddresses: Method for setting airports in DB
     * @param favoriteAddressList - list of favorite addresses
     */
    public void setFavoriteAddresses(List<GetFavoriteData> favoriteAddressList) {
        if(CMAppGlobals.DB_DEBUG)
            Logger.i(TAG, ":: DB : FavoriteDBManager.setFavoriteAddresses : favoriteAddressList : " + favoriteAddressList);

        SQLiteDatabase db = getWritableDatabase();

        for (GetFavoriteData item :
                favoriteAddressList) {
            if (item != null) {
                db.beginTransaction();
                try {
                    if(CheckIsDataAlreadyInDBorNot(DBUtlis.TABLE_FAVORITE, DBUtlis.KEY_ID, item.getId())) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : FavoriteDBManager.setFavoriteAddresses : UPDATE : favorite id : " + item.getId());

                        String updateQuery = "update " + DBUtlis.TABLE_FAVORITE + " SET " +
                                DBUtlis.ADDRESS + " = '" + item.getAddress() + "', " +
                                DBUtlis.LATITUDE + " = '" + item.getLatitude() + "', " +
                                DBUtlis.LONGITUDE + " = '" + item.getLongitude() + "', " +
                                DBUtlis.ENTRANCE + " = '" + item.getEntrance() + "', " +
                                DBUtlis.COMMENT + " = '" + item.getComment() + "', " +
                                DBUtlis.NAME + " = '" + item.getName() + "'" +
                                " where " + DBUtlis.KEY_ID + " = " + item.getId();
                        try {
                            db.execSQL(updateQuery);
                        } catch (Exception e) {
                            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : FavoriteDBManager.setFavoriteAddresses : error : " + e.getMessage()!=null?e.getMessage():"");
                        }
                    } else {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : FavoriteDBManager.setFavoriteAddresses : CREATE : favorite id : " + item.getId());

                        // CREATE railstation
                        db.execSQL("insert into " + DBUtlis.TABLE_FAVORITE + "("
                                + DBUtlis.KEY_ID + ","
                                + DBUtlis.ID_LOCALITY + ","
                                + DBUtlis.ADDRESS + ","
                                + DBUtlis.LATITUDE + ","
                                + DBUtlis.LONGITUDE + ","
                                + DBUtlis.ENTRANCE + ","
                                + DBUtlis.COMMENT + ","
                                + DBUtlis.NAME +
                                ") values ("
                                + item.getId() + ",'"
                                + item.getIdLocality() + "','"
                                + item.getAddress() + "','"
                                + item.getLatitude() + "','"
                                + item.getLongitude() + "','"
                                + item.getEntrance() + "','"
                                + item.getComment() + "','"
                                + item.getName()
                                + "')");
                    }

                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : FavoriteDBManager.setFavoriteAddresses : END ");

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }

    } // end method setFavoriteAddresses

    /**
     * GetAllFavorites: getting all Favorites
     *
     * @return - list of favorites
     */
    public List<GetFavoriteData> GetAllFavorites(int city_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : FavoriteDBManager.GetAllFavorites : city_id : " + city_id);

        SQLiteDatabase db = getReadableDatabase();
        List<GetFavoriteData> favorites = new ArrayList<GetFavoriteData>();
        Cursor c = null;

        String selectQuery = "SELECT * FROM " + DBUtlis.TABLE_FAVORITE +
                " WHERE " + DBUtlis.ID_LOCALITY + " = " + city_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    GetFavoriteData getFavoriteData = new GetFavoriteData();
                    getFavoriteData.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.KEY_ID))));
                    getFavoriteData.setIdLocality(c.getString(c.getColumnIndex(DBUtlis.ID_LOCALITY)));
                    getFavoriteData.setAddress(c.getString(c.getColumnIndex(DBUtlis.ADDRESS)));
                    getFavoriteData.setLatitude(c.getString(c.getColumnIndex(DBUtlis.LATITUDE)));
                    getFavoriteData.setLongitude(c.getString(c.getColumnIndex(DBUtlis.LONGITUDE)));
                    getFavoriteData.setEntrance(c.getString(c.getColumnIndex(DBUtlis.ENTRANCE)));
                    getFavoriteData.setComment(c.getString(c.getColumnIndex(DBUtlis.COMMENT)));
                    getFavoriteData.setName(c.getString(c.getColumnIndex(DBUtlis.NAME)));

                    // adding to favorite list
                    favorites.add(getFavoriteData);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : FavoriteDBManager.GetAllFavorites : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                db.close();
            }
        }
        return favorites;

    } // end method GetAllFavorites




}
