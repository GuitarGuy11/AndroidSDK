package com.guitarview.api.bll;

import android.support.annotation.NonNull;

import com.guitarview.common.OnPostExecuteListener;
import com.guitarview.api.json.JsonRetriever;

public class Guitar {

    private static OnPostExecuteListener _listener;

    public static void setOnLoadListener(OnPostExecuteListener listener)
    {
        _listener = listener;

        JsonRetriever retriever = new JsonRetriever();
        retriever.setOnPostExecuteListener(new OnPostExecuteListener() {
            @Override
            public boolean onPostExecute(@NonNull String result) {
                loaded(result);
                return false;
            }
        });
        retriever.execute("http://192.168.0.16:8081/listGuitars");//// TODO: 9/19/2017 Make this configurable?
    }

    private static void loaded(String result)
    {
        _listener.onPostExecute(result);
    }
}