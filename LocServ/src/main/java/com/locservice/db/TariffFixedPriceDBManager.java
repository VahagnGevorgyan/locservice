package com.locservice.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.FixedPriceRoute;
import com.locservice.api.entities.PriceFrom;
import com.locservice.api.entities.PriceTo;
import com.locservice.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 15 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class TariffFixedPriceDBManager extends DatabaseManager {

    private static final String TAG = TranslationDBManager.class.getSimpleName();

    private Context mContext;

    public TariffFixedPriceDBManager(Context context) {
        super(context);
    }

    /**
     * Method for adding tariff fixed price in DB
     * @param tariffFixedPriceList
     * @param last_tr_id
     * @param language_id
     */
    public int setTariffFixedPrices(SQLiteDatabase db, List<FixedPriceRoute> tariffFixedPriceList, int tariff_id, int last_tr_id, int language_id) {
        if(CMAppGlobals.DB_DEBUG)
            Logger.i(TAG, ":: DB : TariffIntervalDBManager.setTariffFixedPrices : fixedPricesList : " + tariffFixedPriceList
                    + " : tariff_id : " + tariff_id
                    + " : last_tr_id : " + last_tr_id
                    + " : language_id : " + language_id);

        TranslationDBManager translationDBManager = new TranslationDBManager(this.mContext);

        for (FixedPriceRoute item :
                tariffFixedPriceList) {
            if (item != null) {
                db.beginTransaction();
                try {
                    if(CheckIsDataAlreadyInDBorNot(db, DBUtlis.TABLE_TARIFF_FIXED_PRICE, DBUtlis.TARIFF_ID, String.valueOf(tariff_id))) {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffIntervalDBManager.setTariffFixedPrices : UPDATE : tariff_id : " + tariff_id);

                        String selectQuery = "select " + DBUtlis.TARIFF_ID +
                                ", " + DBUtlis.PRICE +
                                ", " + DBUtlis.FROM_ID +
                                ", " + DBUtlis.FROM_NAME_TR_ID +
                                ", " + DBUtlis.TO_ID +
                                ", " + DBUtlis.TO_NAME_TR_ID +
                                " from " + DBUtlis.TABLE_TARIFF_FIXED_PRICE +
                                " where " + DBUtlis.TARIFF_ID + " = " + tariff_id;
                        Cursor c = null;
                        try {
                            c = db.rawQuery(selectQuery, null);
                            int from_name_tr_id = 0;
                            int to_name_tr_id = 0;
                            if (c != null && c.moveToFirst()) {
                                do {
                                    from_name_tr_id = c.getInt(c.getColumnIndex(DBUtlis.FROM_NAME_TR_ID));
                                    to_name_tr_id = c.getInt(c.getColumnIndex(DBUtlis.TO_NAME_TR_ID));
                                } while (c.moveToNext());
                            }
                            if(from_name_tr_id > 0)
                                translationDBManager.createTranslation(db, from_name_tr_id, last_tr_id, language_id, String.valueOf(item.getFrom().getName()));
                            if(to_name_tr_id > 0)
                                translationDBManager.createTranslation(db, to_name_tr_id, last_tr_id, language_id, String.valueOf(item.getTo().getName()));
                        } catch (Exception e) {
                            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TariffIntervalDBManager.setTariffFixedPrices : error : " + e.getMessage()!=null?e.getMessage():"");
                        } finally {
                            if (c != null && !c.isClosed()) {
                                c.close();
                            }
                        }
                    } else {
                        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffIntervalDBManager.setTariffFixedPrices : CREATE : tariff_id : " + tariff_id);

                        // TRANSLATIONS
                        int from_name_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, String.valueOf(item.getFrom().getName()));
                        last_tr_id = from_name_tr_id;
                        int to_name_tr_id = translationDBManager.createTranslation(db, 0, last_tr_id, language_id, String.valueOf(item.getTo().getName()));
                        last_tr_id = to_name_tr_id;
                        // CREATE TariffFixedPrice
                        db.execSQL("insert into " + DBUtlis.TABLE_TARIFF_FIXED_PRICE + "("
                                + DBUtlis.TARIFF_ID + ","
                                + DBUtlis.PRICE + ","
                                + DBUtlis.FROM_ID + ","
                                + DBUtlis.FROM_NAME_TR_ID + ","
                                + DBUtlis.TO_ID + ","
                                + DBUtlis.TO_NAME_TR_ID +
                                ") values("
                                + tariff_id + ","
                                + item.getPrice() + ","
                                + item.getFrom().getId() + ","
                                + from_name_tr_id
                                + item.getTo().getId() + ","
                                + to_name_tr_id
                                + ")");
                    }
                    if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffIntervalDBManager.setTariffFixedPrices : END last_tr_id : " + last_tr_id);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }
        return last_tr_id;

    } // end method setTariffFixedPrices

    /**
     * GetAllTariffFixedPrices: getting all Tariff FixedPrices
     *
     * @return
     */
    public List<FixedPriceRoute> getAllTariffFixedPrices(SQLiteDatabase db, int tariff_id) {
        if(CMAppGlobals.DB_DEBUG)Logger.i(TAG, ":: DB : TariffFixedPriceDBManager.getAllTariffIntervals : tariff_id : " + tariff_id);

        List<FixedPriceRoute> fixedPrices = new ArrayList<FixedPriceRoute>();
        Cursor c = null;

        String selectQuery = "SELECT  * FROM " + DBUtlis.TABLE_TARIFF_FIXED_PRICE + " WHERE " + DBUtlis.TARIFF_ID + " = " + tariff_id;

        try {
            if(db != null) {
                c = db.rawQuery(selectQuery, null);
            }
            // looping through all rows and adding to list
            if (c != null && c.moveToFirst()) {
                do {
                    FixedPriceRoute fixedPrice = new FixedPriceRoute();
                    fixedPrice.setPrice(String.valueOf(c.getFloat(c.getColumnIndex(DBUtlis.PRICE))));
                    PriceFrom priceFrom = new PriceFrom();
                    priceFrom.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.FROM_ID))));
                    priceFrom.setName(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.FROM_NAME_TR_ID))));
                    fixedPrice.setFrom(priceFrom);
                    PriceTo priceTo = new PriceTo();
                    priceTo.setId(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.TO_ID))));
                    priceTo.setName(String.valueOf(c.getInt(c.getColumnIndex(DBUtlis.TO_NAME_TR_ID))));
                    fixedPrice.setTo(priceTo);

                    // adding to fixed price list
                    fixedPrices.add(fixedPrice);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if(CMAppGlobals.DB_DEBUG)Logger.e(TAG, ":: DB : TariffFixedPriceDBManager.getAllTariffIntervals : error : " + e!=null?e.getMessage():"");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return fixedPrices;

    } // end method GetAllTariffFixedPrices
}
