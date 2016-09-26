package com.locservice.ui.helpers;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.locservice.CMAppGlobals;
import com.locservice.adapters.ChildSeatRVAdapter;
import com.locservice.api.entities.ChildSeat;
import com.locservice.ui.fragments.MapViewFragment;
import com.locservice.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 12 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class SlidePanelHelper {
    private static final String TAG = SlidePanelHelper.class.getSimpleName();

    /**
     * Method for getting child seats
     * @return
     */
    public static String getChildrenSeats(ChildSeatRVAdapter mAdapterChild) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: SlidePanelHelper.getChildrenSeats");

        String childrenSeats = null;
        if (mAdapterChild != null) {
            List<ChildSeat> selectedItems = mAdapterChild.getSelectedChildSeats();
            JSONArray jsonArray = new JSONArray();
            try {
                for (ChildSeat item : selectedItems) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("weight", item.getPosition());
                    jsonObject.put("age", "1");
                    jsonArray.put(jsonObject);
                }
                childrenSeats = jsonArray.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SlidePanelHelper.getChildrenSeats : childrenSeats : " + childrenSeats);

        return childrenSeats;

    } // end method getChildrenSeats

    /**
     * Method for setting from address field visible/invisible
     * @param layoutItemFrom
     * @param isVisible
     */
    public static void setFromItemVisible(MapViewFragment mapViewFragment, LinearLayout layoutItemFrom, boolean isVisible) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SlidePanelHelper.setFromItemVisible : isVisible : " + isVisible);

        if(isVisible) {
            mapViewFragment.setPanelItemsCount(7);
            layoutItemFrom.setVisibility(View.VISIBLE);
        } else {
            mapViewFragment.setPanelItemsCount(6);
            layoutItemFrom.setVisibility(View.GONE);
        }

    } // end method setFromItemVisible

    /**
     * Method for setting all items visible/invisible
     * @param isVisible
     */
    public static void setAllItemsVisible(MapViewFragment mapViewFragment,
                                          LinearLayout layoutItemFrom,
                                          LinearLayout layoutItemTo,
                                          LinearLayout layoutItemDate,
                                          LinearLayout layoutItemTariff,
                                          LinearLayout layoutItemService,
                                          LinearLayout layoutItemChild,
                                          RelativeLayout layoutItemComment,
                                          boolean isVisible) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SlidePanelHelper.setAllItemsVisible : isVisible : " + isVisible);

        if(isVisible) {
            mapViewFragment.setPanelItemsCount(7);
            layoutItemFrom.setVisibility(View.VISIBLE);
            layoutItemTo.setVisibility(View.VISIBLE);
            layoutItemDate.setVisibility(View.VISIBLE);
            layoutItemTariff.setVisibility(View.VISIBLE);
            layoutItemService.setVisibility(View.VISIBLE);
            layoutItemChild.setVisibility(View.VISIBLE);
            layoutItemComment.setVisibility(View.VISIBLE);
        } else {
            mapViewFragment.setPanelItemsCount(1);
            layoutItemFrom.setVisibility(View.GONE);
            layoutItemTo.setVisibility(View.GONE);
            layoutItemDate.setVisibility(View.GONE);
            layoutItemTariff.setVisibility(View.GONE);
            layoutItemService.setVisibility(View.GONE);
            layoutItemChild.setVisibility(View.GONE);
            layoutItemComment.setVisibility(View.GONE);
        }

    } // end method setAllItemsVisible

}
