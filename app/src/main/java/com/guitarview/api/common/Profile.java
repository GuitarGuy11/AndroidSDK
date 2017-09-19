package com.guitarview.api.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import android.os.StrictMode;

public class Profile implements GoogleApiClient.OnConnectionFailedListener {

    public static int LOGIN = 2000;

    public GoogleSignInAccount Account;

    private GoogleApiClient _googleApiClient;
    private Intent _signInIntent;
    private AppCompatActivity _activity;

    public Profile(AppCompatActivity activity)
    {
        _activity = activity;
        initializeAuthentication();
    }

    private void initializeAuthentication()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        _googleApiClient = new GoogleApiClient.Builder(_activity)
                .enableAutoManage(_activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void SignIn() {
        if (Account == null) {
            _signInIntent = Auth.GoogleSignInApi.getSignInIntent(_googleApiClient);
            _activity.startActivityForResult(_signInIntent, Profile.LOGIN);
        }
    }

    public void SignOut() {
        if (Account == null) {
            SignIn();
        } else {
            Auth.GoogleSignInApi.signOut(_googleApiClient);
            Account = null;
        }
    }

    public GoogleSignInResult SilentSignIn() {
        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(_googleApiClient);
        if (pendingResult.isDone()) {
            return pendingResult.get();
        }
        return null;
    }

    public void onConnectionFailed(ConnectionResult result) {
        Account = null;
    }
}
