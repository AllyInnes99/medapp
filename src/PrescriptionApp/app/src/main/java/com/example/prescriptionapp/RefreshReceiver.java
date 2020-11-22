package com.example.prescriptionapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.DefaultDatabaseErrorHandler;

public class RefreshReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.refreshApplications();
    }
}
