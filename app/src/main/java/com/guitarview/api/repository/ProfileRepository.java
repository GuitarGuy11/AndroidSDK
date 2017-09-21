package com.guitarview.api.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

public class ProfileRepository implements GoogleApiClient.OnConnectionFailedListener {

    public static int LOGIN = 2000;

    public GoogleSignInAccount Account;
    private GoogleApiClient _googleApiClient;
    private Intent _signInIntent;
    private AppCompatActivity _activity;

    public ProfileRepository(AppCompatActivity activity)
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

    public void signIn() {
        if (Account == null) {
            _signInIntent = Auth.GoogleSignInApi.getSignInIntent(_googleApiClient);
            _activity.startActivityForResult(_signInIntent, ProfileRepository.LOGIN);
        }
    }

    public LiveData<ProfileRepository> signOut() {
        if (Account != null) {
            Auth.GoogleSignInApi.signOut(_googleApiClient);
            Account = null;
        }
        return toMutableData();
    }

    public LiveData<ProfileRepository> silentSignIn() {
        if(Account == null) {
            OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(_googleApiClient);
            if (pendingResult.isDone()) {
                setAccount(pendingResult.get());
            }
        }
        return toMutableData();
    }

    public LiveData<ProfileRepository> toMutableData()
    {
        final MutableLiveData<ProfileRepository> data = new MutableLiveData<>();
        data.setValue(this);
        return data;
    }

    public void onConnectionFailed(ConnectionResult result) {
        Account = null;
    }

    public void setAccount(GoogleSignInResult result)
    {
        if (result != null && result.isSuccess()) {
            Account = result.getSignInAccount();
        }
    }
}
