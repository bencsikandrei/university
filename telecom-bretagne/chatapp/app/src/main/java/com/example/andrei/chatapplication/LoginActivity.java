package com.example.andrei.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.andrei.chatapplication.helper.DisplayProgress;
import com.example.andrei.chatapplication.helper.SingleFragmentActivity;
import com.example.andrei.chatapplication.network.NetworkHelper;
import com.example.andrei.chatapplication.parser.JsonParser;

/**
 * @author Andrei
 * Make the class for the Login
 *
 * Uses an AsyncTask to do the POST for login
 *
 * Uses a Fragment to displa the whole view
 *
 *
 *
 *
 */
public class LoginActivity extends SingleFragmentActivity
        implements LoginFragment.OnLoginClickListener, LoginFragment.OnNewAccClickListener {

    public static final String EXTRA_TOKEN = "eu.tb.afbencsi.token";
    public static final String EXTRA_MESSAGES = "eu.tb.afbencsi.msg";

    private DisplayProgress mDisplayProgress = new DisplayProgress(this);

    private AsyncTaskLogin mAsyncTaskLogin;

    private String mToken;

    @Override
    public Fragment createFragment() {
        Bundle args = new Bundle();

        return LoginFragment.newInstance();

    }

    @Override
    public void onLoginClick(String... params) {
        // take the data and make the call
        //mDisplayProgress.displayProgressDialog();
        mAsyncTaskLogin = new AsyncTaskLogin(this);
        try {

            mToken = mAsyncTaskLogin.execute(params).get();
            //mDisplayProgress.hideProgressDialog();
            mToken = JsonParser.getToken(mToken);

            Log.i("Andrei", "onLoginClick: " + mToken);

            Intent i = new Intent(this, ChatActivity.class);

            i.putExtra(EXTRA_TOKEN, mToken);

            startActivity(i);

        } catch (Exception iex) {
            Log.w("Andrei", "Could not run the task !");
            iex.printStackTrace();
        }
    }

    @Override
    public void onNewClick() {
        Intent i = new Intent(this, SubscribeActivity.class);

        startActivity(i);
    }

    private class AsyncTaskLogin extends AsyncTask<String, Void, String> {

        Context context;

        public AsyncTaskLogin(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            if (!NetworkHelper.isInternetAvailable(context)) {
                return "Internet is not available!";
            }

            return NetworkHelper.signin(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

    }
}
