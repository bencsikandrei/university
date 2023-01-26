package com.example.andrei.chatapplication.helper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.example.andrei.chatapplication.R;

/**
 * @author Andrei
 * Helper class for all activities, since we want to use fragments
 */
public abstract class SingleFragmentActivity extends FragmentActivity {
    // the method to use when creating a fragment
    public abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);

        // get the fragment manager
        FragmentManager fm = getSupportFragmentManager();

        // use the fm to get a a fragment
        Fragment f = fm.findFragmentById(R.id.fragment_container);
        // if the fragment does not exist, create it and then add it
        if (f == null) {
            f = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, f)
                    .commit();
        }
    }
}
