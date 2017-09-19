package com.guitarview.views;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.content.Intent;
import android.net.Uri;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.app.AlertDialog;
import android.widget.ListView;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.net.URL;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.Auth;

import com.guitarview.R;
import com.guitarview.api.common.NavigationItemAdapter;
import com.guitarview.api.common.Profile;


public class BaseView extends AppCompatActivity {

    private Menu currentMenu;
    private Profile profile;
    public ActionBarDrawerToggle drawerToggle;
    public DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_view);

        initializeMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        currentMenu = menu;

        initializeProfile();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        int id = item.getItemId();

        switch (id) {
            case R.id.action_login:
                onSignInClick();
                break;
            case R.id.action_logout:
                onSignOutClick();
                break;
            case android.R.id.home:
                //onMenuExpand();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeMenu() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.bringToFront();
        ListView menuView = (ListView)findViewById(R.id.menu_view);
        menuView.bringToFront();

        new NavigationItemAdapter(this);
    }

    private void initializeProfile() {
        profile = new Profile(this);
        setProfileResult(profile.SilentSignIn());
    }

    private void onSignInClick() {
        try {
            profile.SignIn();
        } catch (Exception ex) {
            SendMessage(ex.getMessage());
        }
    }

    private void onSignOutClick() {
        try {
            profile.SignOut();
            setProfileResult(null);
        } catch (Exception ex) {
            SendMessage(ex.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Profile.LOGIN) {
            setProfileResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        }
    }

    private void setProfileResult(GoogleSignInResult result) {
        if (result != null && result.isSuccess()) {
            profile.Account = result.getSignInAccount();
        }

        MenuItem mnuLogin = currentMenu.findItem(R.id.action_login);
        MenuItem mnuLogout = currentMenu.findItem(R.id.action_logout);

        if (profile.Account  == null) {
            mnuLogin.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_login, null));
            mnuLogout.setTitle("Login");
        } else {
            Uri uri = profile.Account .getPhotoUrl();
            RetrieveImageTask imageTask = new RetrieveImageTask();
            mnuLogin.setIcon(imageTask.doInBackground(uri));
            mnuLogout.setTitle("Logout");
        }
    }

    public void SendMessage(String message) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("GuitarView...");
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public class RetrieveImageTask extends AsyncTask<Uri, Void, Drawable> {

        private Exception exception;

        @Override
        protected Drawable doInBackground(Uri... uris) {
            try {
                return drawableFromUrl(uris[0].toString());
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        private Drawable drawableFromUrl(String url) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

                connection.setRequestProperty("User-agent", "Mozilla/4.0");
                connection.connect();

                InputStream input = connection.getInputStream();

                Bitmap bm = BitmapFactory.decodeStream(input);

                return new BitmapDrawable(getResources(), bm);
            } catch (Exception ex) {
                return ResourcesCompat.getDrawable(getResources(), R.drawable.ic_login, null);
            }
        }

        protected void onPostExecute(Drawable feed) {
        }
    }
}
