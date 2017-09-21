package com.guitarview.controllers;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guitarview.R;
import com.guitarview.models.GuitarModel;

public class GuitarController extends Fragment  {

    public static final String NAVIGATION_KEY = "Guitars";
    private static final String CAT_KEY = "all";

    private GuitarModel viewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        String category = getArguments().getString(CAT_KEY);
        viewModel = ViewModelProviders.of(this).get(GuitarModel.class);
        viewModel.init(category);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guitar, container, false);
    }
}
