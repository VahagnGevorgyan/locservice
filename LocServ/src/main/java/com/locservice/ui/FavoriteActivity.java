package com.locservice.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.GetFavoriteData;
import com.locservice.api.entities.ResultData;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.fragments.FavoriteAddressesFragment;
import com.locservice.ui.helpers.FragmentHelper;
import com.locservice.ui.utils.FragmentTypes;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;

import java.util.List;

public class FavoriteActivity extends BaseActivity implements ICallBack {

    private FavoriteAddressesFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        // check network status
        if (Utils.checkNetworkStatus(this)) {
            // set favorite address fragment
            currentFragment = (FavoriteAddressesFragment) FragmentHelper.getInstance().openFragment(this, FragmentTypes.FAVORITE_ADDRESSES, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteActivity.onActivityResult : requestCode + " + requestCode
                + " : resultCode : " + resultCode
                + " : data : " + data);
    }

    @Override
    public void onFailure(Throwable error, int requestType) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteActivity.onFailure : error : " + error);
    }

    @Override
    public void onSuccess(Object response, int requestType) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: FavoriteActivity.onSuccess : requestType : " + requestType + " response : " + response);
        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_GET_FAVORITE:
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_FAVORITE")) {
                        //noinspection unchecked
                        List<GetFavoriteData> favorites = (List<GetFavoriteData>) response;

                        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: FavoriteActivity.onSuccess : REQUEST_GET_FAVORITE : favorites : " + favorites);
                        currentFragment.notifyAdapter(favorites);
                    }
                    break;
                case RequestTypes.REQUEST_REMOVE_FAVORITE:
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_REMOVE_FAVORITE")) {
                        if(response instanceof ResultData) {
                            ResultData resultData = (ResultData) response;
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: FavoriteActivity.onSuccess : REQUEST_REMOVE_FAVORITE : resultData : " + resultData);

                            if (resultData.getResult().equals("1")) {
                                currentFragment.removeFavoriteAddress();
                            } else {
                                Toast.makeText(FavoriteActivity.this, R.string.alert_remove_favorite_address, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    break;
            }
        }
    }
}
