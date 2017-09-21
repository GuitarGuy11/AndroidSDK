package com.guitarview.common;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guitarview.R;
import com.guitarview.api.json.JsonRetriever;
import com.guitarview.controllers.GuitarController;
import com.guitarview.controllers.HomeController;

import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.widget.ListView;

public class NavigationItemAdapter extends BaseAdapter {

    private AppCompatActivity _activity;
    private ArrayList<String> _items;

    public NavigationItemAdapter(AppCompatActivity activity) {
        _activity = activity;
        _items = new ArrayList<>();
    }

    public void init()
    {
        JsonRetriever retriever = new JsonRetriever();
        retriever.setOnPostExecuteListener(new OnPostExecuteListener() {
            @Override
            public boolean onPostExecute(@NonNull String result) {
                addCategories(result);
                return false;
            }
        });
        retriever.execute("http://192.168.0.16:8081/listCategories");//// TODO: 9/19/2017 Make this configurable?
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        LayoutInflater inflater = _activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.navigation_item, container, false);
        setRowView(row, position);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectView(position);
            }
        });

        return (row);
    }

    public void selectView(int position)
    {
        Fragment fragment = getFragment(position);
        Bundle args = new Bundle();
        args.putInt("GuitarController", 0);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = _activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // how to hide the nav panel on click?
        DrawerLayout drawerLayout = _activity.findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawers();
    }

    private void setRowView(View row, int position)
    {
        TextView title, detail;
        ImageView i1;
        title = (TextView) row.findViewById(R.id.firstLine);
        detail = (TextView) row.findViewById(R.id.secondLine);
        i1=(ImageView)row.findViewById(R.id.navIcon);
        title.setText(_items.get(position));
        //detail.setText(Detail[position]);
        //i1.setImageResource(imge[position]);
    }

    private Fragment getFragment(int position)
    {
        String item = "";
        if(_items != null && _items.size() > position) {
            item = _items.get(position);
        }

        switch (item)
        {
            case GuitarController.NAVIGATION_KEY:
                return new GuitarController();
            default:
                return new HomeController();
        }
    }


    public int getCount() {
        return _items.size();
    }

    public Object getItem(int arg0) {
        return _items.get(arg0);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    private void addCategories(String json)
    {
        try
        {
            // iterate through the JSON object.
            JSONObject jsonObj = new JSONObject(json);
            JSONObject catObj = jsonObj.getJSONObject("categories");
            JSONArray categories = catObj.getJSONArray("category");

            final ArrayList<String> list = new ArrayList<String>();
            for(int i = 0;  i < categories.length(); i++)
            {
                JSONObject cat = categories.getJSONObject(i);
                list.add(cat.getString("name"));
            }

            _items = list;

            ListView menuView = _activity.findViewById(R.id.menu_view);
            menuView.setAdapter(this);
        }
        catch(JSONException ex)
        {
            //SendMessage("Error loading categories.");
        }
    }
}

