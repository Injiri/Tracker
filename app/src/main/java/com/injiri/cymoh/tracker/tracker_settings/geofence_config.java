package com.injiri.cymoh.tracker.tracker_settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.injiri.cymoh.tracker.MainActivity;
import com.injiri.cymoh.tracker.userlocation_service;

public class geofence_config extends Fragment{

    public void set_geofence(final int estimateradius){

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String lat=intent.getStringExtra(userlocation_service.USER_LATITUDE);
                String lon=intent.getStringExtra(userlocation_service.USER_LONGITUDE);
//              double distance= distance between userloc to deviceloc
//                if (distance>estimateradius){
//                    notify the device owner wring thE ALARM
//                }
            }
        });
    }
}
