package com.injiri.cymoh.tracker;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;

public class userlocation_service extends Service {
    public userlocation_service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public  void requestLocationUpdater(){
        LocationRequest request= new LocationRequest();
        request.setInterval(1000);
        request.setFastestInterval(500);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);




    }
   public BroadcastReceiver stopReciever= new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           Log.d(TAG,"Recieved stop broadcast");
           //stop the service when the notification is taped
           unregisterReceiver(stopReciever);
           stopSelf();
       }
   };

    public void buildNotification(){
        String stop="stop";
        registerReceiver(stopReciever,new IntentFilter(stop));
        PendingIntent broadcastIntent= PendingIntent.getBroadcast(this,0,new Intent(stop),PendingIntent.FLAG_UPDATE_CURRENT);
        //make a persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContentTitle(getString(R.string.app_name)).setContentText(getString(R.string.notification_text)).setOngoing(true
        ).setContentIntent(broadcastIntent).setSmallIcon(R.drawable.ic_tracker);
        startForeground(1,builder.build());
    }
}
