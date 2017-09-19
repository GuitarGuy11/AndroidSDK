package com.guitarview.api.common;

import android.support.annotation.NonNull;

public interface OnPostExecuteListener {

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     *
     * @return true to display the item as the selected item
     */
    public boolean onPostExecute(@NonNull String result);
}
