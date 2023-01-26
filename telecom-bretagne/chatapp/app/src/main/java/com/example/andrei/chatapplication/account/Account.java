package com.example.andrei.chatapplication.account;

/**
 * @author Andrei
 *         Simple POJO for messages
 */
/* account POJO */
public class Account {
    String mLogin;
    String mName;
    String mPasswd;


    public Account(String login, String name, String passwd) {
        mLogin = login;

        mName = name;

        mPasswd = passwd;
    }

    public String getLogin() {
        return mLogin;
    }

    public String getName() {
        return mName;
    }

    public String getPasswd() {
        return mPasswd;
    }
}
