package com.locservice.messages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vahagn Gevorgyan
 * 29 July 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class WebViewDialogMessage extends Message implements Parcelable {

    private String strUrl;

    public WebViewDialogMessage(String strUrl) {
        this.strUrl = strUrl;
    }

    protected WebViewDialogMessage(Parcel in) {
        strUrl = in.readString();
    }

    public static final Creator<WebViewDialogMessage> CREATOR = new Creator<WebViewDialogMessage>() {
        @Override
        public WebViewDialogMessage createFromParcel(Parcel in) {
            return new WebViewDialogMessage(in);
        }

        @Override
        public WebViewDialogMessage[] newArray(int size) {
            return new WebViewDialogMessage[size];
        }
    };

    public String getStrUrl() {
        return strUrl;
    }

    public void setStrUrl(String strUrl) {
        this.strUrl = strUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strUrl);
    }
}
