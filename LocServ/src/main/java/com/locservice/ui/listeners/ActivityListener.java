package com.locservice.ui.listeners;

import android.app.Activity;

/**
 * Created by Vahagn Gevorgyan
 * 16 May 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface ActivityListener {

    public void setCurrentActivity(Activity context);

    public void clearCurrentActivity(Activity context);

}
