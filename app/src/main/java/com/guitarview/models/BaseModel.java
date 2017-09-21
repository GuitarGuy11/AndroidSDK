package com.guitarview.models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;

import com.google.android.gms.auth.api.Auth;
import com.guitarview.common.OnPostExecuteListener;
import com.guitarview.api.repository.ProfileRepository;

public class BaseModel extends ViewModel {
    private OnPostExecuteListener profileListener;

    private ProfileRepository profileRepository;
    private LiveData<ProfileRepository> profile;

    public BaseModel()
    {
    }

    public void init(ProfileRepository profileRepo)
    {
        profileRepository = profileRepo;

        if (this.profileRepository == null) {
            return;
        }
        this.profile = profileRepository.silentSignIn();
    }

    public void setOnPostProfileListener(OnPostExecuteListener listener)
    {
        profileListener = listener;
    }

    public ProfileRepository getProfile() {
        if(profile == null) return null;
        return profile.getValue();
    }

    public void signIn()
    {
        if (this.profileRepository.Account == null) {
            profileRepository.signIn();
            return;
        }
        profileListener.onPostExecute("complete");
    }

    public void signOut()
    {
        if (this.profileRepository.Account != null) {
            this.profile = profileRepository.signOut();
        }
        profileListener.onPostExecute("complete");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == ProfileRepository.LOGIN) {
            profileRepository.setAccount(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
            this.profile = profileRepository.toMutableData();
            profileListener.onPostExecute("complete");
        }
    }
}