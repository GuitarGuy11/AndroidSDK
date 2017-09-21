package com.guitarview.controllers;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.net.URL;

import com.guitarview.R;
import com.guitarview.common.NavigationItemAdapter;
import com.guitarview.common.OnPostExecuteListener;
import com.guitarview.api.repository.ProfileRepository;
import com.guitarview.models.BaseModel;

public class BaseController extends AppCompatActivity {

    private BaseModel baseViewModel;
    private Menu currentMenu;
    public ActionBarDrawerToggle drawerToggle;
    public DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initializeModel();
        initializeMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        currentMenu = menu;
        setProfileResult();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        int id = item.getItemId();

        switch (id) {
            case R.id.action_login:
                baseViewModel.signIn();
                break;
            case R.id.action_logout:
                if(item.getTitle() == "Login") {
                    baseViewModel.signIn();
                }
                else {
                    baseViewModel.signOut();
                }
                break;
            case android.R.id.home:
                //onMenuExpand();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void initializeModel() {
        try {
            baseViewModel = ViewModelProviders.of(this).get(BaseModel.class);
            baseViewModel.init(new ProfileRepository(this));

            baseViewModel.setOnPostProfileListener(new OnPostExecuteListener() {
                @Override
                public boolean onPostExecute(@NonNull String result) {
                    setProfileResult();
                    return false;
                }
            });
        }
        catch(Exception ex)
        {
            SendMessage(ex.getMessage());
        }

    }

    private void initializeMenu() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);

        findViewById(R.id.navigation_view).bringToFront();
        NavigationItemAdapter adapter = new NavigationItemAdapter(this);
        adapter.init();
        adapter.selectView(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        baseViewModel.onActivityResult(requestCode, resultCode, data);
    }

    private void setProfileResult() {

        ProfileRepository profile = baseViewModel.getProfile();

        MenuItem mnuLogin = currentMenu.findItem(R.id.action_login);
        MenuItem mnuLogout = currentMenu.findItem(R.id.action_logout);

        if(profile == null || profile.Account  == null) {
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
        dlgAlert.setTitle("GuitarController...");
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
