package com.guitarview.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.app.Fragment;
import android.view.ViewGroup;

import com.guitarview.R;


public class GuitarView extends Fragment {

    public static final String NAVIGATION_KEY = "Guitars";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_guitar_view, container, false);
    }
}
