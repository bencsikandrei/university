package com.example.andrei.chatapplication.message;

import java.text.DateFormat;
import java.util.Date;

/**
 * @author Andrei
 *         Simple POJO for messages
 */
public class Message {
    /* message POJO */
    private String mUserName;
    private String mMessage;
    private long mMsgDate;

    public Message(String userName, String message, long date) {
        this.mMessage = message;
        this.mUserName = userName;
        this.mMsgDate = date;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getDate() {
        DateFormat df = DateFormat.getDateInstance();

        return df.format(new Date(mMsgDate));
    }
}
