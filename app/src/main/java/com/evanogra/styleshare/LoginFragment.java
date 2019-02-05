package com.evanogra.styleshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;


/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {

    private Firebase mFirebase;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_login, container, false);
        final Context c = getActivity().getApplicationContext();
        Firebase.setAndroidContext(c);
        mFirebase = new Firebase("https://resplendent-inferno-4210.firebaseio.com/");

        Button loginButton = (Button) v.findViewById(R.id.username_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button registerButton = (Button) v.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });


        return v;
    }


    private void attemptRegister() {
        TextView usernameTextView = (TextView) getView().findViewById(R.id.username_edittext);
        TextView passwordTextView = (TextView) getView().findViewById(R.id.password_edittext);

        final String email = usernameTextView.getText().toString();
        final String password = passwordTextView.getText().toString();

        mFirebase.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                Toast t = Toast.makeText(getActivity().getApplicationContext(), "Registration Successful!", Toast.LENGTH_LONG);
                t.show();
                String uid = stringObjectMap.get("uid").toString();

                mFirebase.child("users").child(uid).child("email").setValue(email);
                mFirebase.child("users").child(uid).child("setup").setValue(false);


                attemptLogin();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Log.d("Error", firebaseError.getDetails());
                Toast t = Toast.makeText(getActivity().getApplicationContext(), "error", Toast.LENGTH_LONG);
                t.show();
                hideLoadingPanel();
                showLoginPanel();
            }
        });
    }

    private void attemptLogin() {
        hideLoginPanel();
        showLoadingPanel();

        TextView usernameTextView = (TextView) getView().findViewById(R.id.username_edittext);
        TextView passwordTextView = (TextView) getView().findViewById(R.id.password_edittext);

        String email = usernameTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        mFirebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {

            @Override
            public void onAuthenticated(AuthData authData) {
                Toast t = Toast.makeText(getActivity().getApplicationContext(), "Login Success", Toast.LENGTH_LONG);
                t.show();

                Intent intent = new Intent(getActivity().getApplicationContext(), TabActivity.class);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Toast toast;
                switch (firebaseError.getCode()) {
                    case FirebaseError.INVALID_EMAIL:
                        toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid Email", Toast.LENGTH_LONG);
                        toast.show();
                        hideLoadingPanel();
                        showLoginPanel();
                        break;
                    case FirebaseError.INVALID_PASSWORD:
                        toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid Password", Toast.LENGTH_LONG);
                        toast.show();
                        hideLoadingPanel();
                        showLoginPanel();
                        break;
                    default:
                        toast = Toast.makeText(getActivity().getApplicationContext(), "Authentication Error", Toast.LENGTH_LONG);
                        toast.show();
                        hideLoadingPanel();
                        showLoginPanel();
                        break;
                }
            }
        });
    }


    private void hideLoginPanel() {
        RelativeLayout loginPanel = (RelativeLayout) getView().findViewById(R.id.login_panel);
        loginPanel.setVisibility(View.GONE);
    }

    private void showLoginPanel() {
        RelativeLayout loginPanel = (RelativeLayout) getView().findViewById(R.id.login_panel);
        loginPanel.setVisibility(View.VISIBLE);
    }

    private void hideLoadingPanel() {
        RelativeLayout loadingPanel = (RelativeLayout) getView().findViewById(R.id.loadingPanel);
        loadingPanel.setVisibility(View.GONE);
    }

    private void showLoadingPanel() {
        RelativeLayout loadingPanel = (RelativeLayout) getView().findViewById(R.id.loadingPanel);
        loadingPanel.setVisibility(View.VISIBLE);
    }
}
