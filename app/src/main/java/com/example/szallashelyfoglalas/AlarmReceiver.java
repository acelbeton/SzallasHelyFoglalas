package com.example.szallashelyfoglalas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.szallashelyfoglalas.NotificationHelper;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new NotificationHelper(context).send("It's time to book something!");
    }
}