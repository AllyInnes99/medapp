package com.example.medapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Class that extends the BroadcastReceiver superclass, that is used to define the action of
 * refreshing the database on a weekly basis
 */
public class RefreshReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.NOTIFY")) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            databaseHelper.refreshDoses();
        }
    }
}
