package com.example.andrei.chatapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.andrei.chatapplication.helper.SingleFragmentActivity;
import com.example.andrei.chatapplication.network.NetworkHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Andrei
 *         Make the class for the actual chat
 *         <p>
 *         Uses a RecyclerView for showing messages (for now using the same ViewHolder for all messages)
 *         <p>
 *         Uses a Fragment to displa the whole chat
 */
public class ChatActivity extends SingleFragmentActivity
        implements ChatFragment.OnSendButtonClick, ChatFragment.OnRefreshButtonClick {

    public static final String EXTRA_MESSAGES = "eu.tb.afbencsi.messages";

    private final Handler mHandler = new Handler();
    /* token received from the Login Activity */
    private String mToken;
    /* messages received as a HTTP response */
    private String mMessages;
    /* a timer for refresh -> TODO */
    private Timer mTimer;
    private TimerTask mTimerTask;

    // private DisplayProgress mDisplayProgress = new DisplayProgress(this);

    /**
     * The method used to create the fragment and pass it necessary args
     */
    @Override
    public Fragment createFragment() {

        mToken = getIntent().getStringExtra(LoginActivity.EXTRA_TOKEN);
        mMessages = getIntent().getStringExtra(LoginActivity.EXTRA_MESSAGES);

        ChatFragment cf = (ChatFragment) ChatFragment.newInstance();

        Bundle args = new Bundle();

        //updateMessages();

        args.putString(LoginActivity.EXTRA_TOKEN, mToken);
        args.putString(EXTRA_MESSAGES, mMessages);

        cf.setArguments(args);

        return cf;
    }

    /**
     * AsyncTask wrapper to update the messages
     *
     * @deprecated This functionality was moved to the fragment!! Still needs some refactoring
     */
    private void updateMessages() {

        try {
            mMessages = new AsyncTaskMessages(this).execute(mToken).get();
            Log.d("Andrei:UpdateMessage", "UpdateMessage:" + mMessages);
        } catch (Exception ex) {
            Log.e("Andrei:UpdateMessage", "UpdateMessage: error fetching the messages..");
        }

    }
    /*
        start the timer and make it reload every 10 seconds
     */
    private void startTimer() {
        // initialize timer
        mTimer = new Timer();
        // initialize timer task
        initializeTimerTask();
        // schedule
        mTimer.schedule(mTimerTask, 0, 10000);
    }

    /*
        stop the timer
     */
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /*
        make it an anonymous class
     */
    private void initializeTimerTask() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateMessages();
                    }
                });
            }
        };
    }

    @Override
    public String onSendButtonClick(String... params) {

        //mDisplayProgress.displayProgressDialog();
        new AsyncTaskSend(this).execute(params[0], mToken);
        updateMessages();
        return mMessages;

    }

    @Override
    public String onRefreshButtonClick() {
        //mDisplayProgress.displayProgressDialog();
        updateMessages();
        return mMessages;
    }

    /**
     * @deprecated MOVED TO FRAGMENT
     */
    private class AsyncTaskMessages extends AsyncTask<String, Void, String> {

        Context context;

        public AsyncTaskMessages(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            if (!NetworkHelper.isInternetAvailable(context)) {
                return "Internet is not available!";
            }

            return NetworkHelper.getMessages(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

    }

    /**
     * Used to send the messages !
     * Called in the callback
     */
    private class AsyncTaskSend extends AsyncTask<String, Void, String> {

        Context context;

        public AsyncTaskSend(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            if (!NetworkHelper.isInternetAvailable(context)) {
                return "Internet is not available!";
            }

            return NetworkHelper.sendMessage(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

    }
}
