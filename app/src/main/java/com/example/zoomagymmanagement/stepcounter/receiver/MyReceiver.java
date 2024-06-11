package com.example.zoomagymmanagement.stepcounter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.zoomagymmanagement.service.LocationService;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent i) {
        Log.d("gym", "Low Battery Received");
        Intent intent = new Intent(context, LocationService.class);
        Bundle b = new Bundle();
        b.putBoolean("battery", true);
        intent.putExtras(b);
        context.startService(intent);
    }
}
