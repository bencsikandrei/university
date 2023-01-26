package com.example.andrei.chatapplication.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

/**
 * Progress dialog class, to have a centralized way of showing
 * or hiding a PD
 */
public class DisplayProgress {

    Context mContext;
    ProgressDialog progressDialog;

    public DisplayProgress(Context context) {
        mContext = context;
    }

    /**
     * display a Progress Dialog.
     */
    public void displayProgressDialog(String task) {
        progressDialog = new ProgressDialog(mContext);

        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage(task + " in progress ...");
        progressDialog.show();
    }

    /**
     * Method to close progress dialog.
     */
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        } else {
            Log.w("Andrei", "trying to close Progress dialog that is not exist or opened");
        }
    }

}
