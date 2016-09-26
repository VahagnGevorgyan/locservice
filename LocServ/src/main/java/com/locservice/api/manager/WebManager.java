package com.locservice.api.manager;

import com.locservice.protocol.ICallBack;

/**
 * Created by Vahagn Gevorgyan
 * 19 November 2015
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class WebManager {

    protected ICallBack mContext;

    public WebManager(){}

    public WebManager(ICallBack context) {
        this.mContext = context;
    }

}
