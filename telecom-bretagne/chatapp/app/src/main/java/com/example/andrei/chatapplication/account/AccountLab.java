package com.example.andrei.chatapplication.account;

/**
 * @author Andrei
 *         TODO Account lab for later use
 */
public class AccountLab {

    private static AccountLab sAccoutLab;
    private Account mAccount;

    private AccountLab() {

    }

    public static AccountLab getInstance() {
        if (sAccoutLab == null) {
            sAccoutLab = new AccountLab();
        }
        return sAccoutLab;
    }

    public Account createAccount(String login, String name, String passwd) {
        mAccount = new Account(login, name, passwd);
        return this.getAccount();
    }

    public Account getAccount() {
        return mAccount;
    }

}
