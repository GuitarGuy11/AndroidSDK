package com.guitarview.models;

import android.arch.lifecycle.ViewModel;

public class GuitarModel extends ViewModel {
    private String categoryId;

    public void init(String catId) {
        categoryId = catId;
    }
}