package com.locservice.messages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vahagn Gevorgyan
 * 08 September  2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class Message implements Parcelable {

    public Message () {

    }

    protected Message(Parcel in) {
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
