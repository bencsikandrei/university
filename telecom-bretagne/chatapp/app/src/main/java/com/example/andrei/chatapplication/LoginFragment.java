package com.example.andrei.chatapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Andrei
 *
 * Fragment for the VIEW of Chat Activity
 *
 * Has an AsyncTask for updating messages
 *
 * Uses one callback to send a message
 *
 *
 */
public class LoginFragment extends Fragment {

    private OnLoginClickListener mLoginHandler;
    private OnNewAccClickListener mNewAccHandler;


    private Button mLoginButton;
    private Button mNewAccButton;

    private EditText mLoginEdit;
    private EditText mPasswdEdit;

    public static Fragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mLoginEdit = (EditText) v.findViewById(R.id.subscribe_edit_text_login_uname);

        mPasswdEdit = (EditText) v.findViewById(R.id.subscribe_edit_text_passwd_again);

        mLoginButton = (Button) v.findViewById(R.id.subscribe_button_create);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uname = mLoginEdit.getText().toString();

                String passwd = mPasswdEdit.getText().toString();

                mLoginHandler.onLoginClick(uname, passwd);
            }
        });

        mNewAccButton = (Button) v.findViewById(R.id.button_new_account);

        mNewAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewAccHandler.onNewClick();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mLoginHandler = (OnLoginClickListener) context;
            mNewAccHandler = (OnNewAccClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoginClickListener and OnNewAccClickListener");
        }


    }

    public interface OnLoginClickListener {
        void onLoginClick(String... params);
    }

    public interface OnNewAccClickListener {
        void onNewClick();
    }
}
