package com.injiri.cymoh.tracker.tracker_settings;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClipboardMonitorService extends Service {
    private static final String TAG = "ClipboardManager";
    private static final String news_url = "";

    private ExecutorService mThreadPool = Executors.newSingleThreadExecutor();
    private ClipboardManager mClipboardManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //start the clipboard monito service
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(
                mOnPrimaryClipChangedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    Log.d(TAG, "onPrimaryClipChanged");
                    ClipData clip = mClipboardManager.getPrimaryClip();
//                    clip.getItemAt(0).getText()
                    if (clip != null) {
                        Toast.makeText(getApplicationContext(), "clip val: " + clip.toString(), Toast.LENGTH_LONG).show();
                        //request modelvalidation
                        Log.d(TAG, "sending location_info...");
                        Intent intent = new Intent(CLIPBORD_MONITOR_SERVICE_ACTION);
                        intent.putExtra(ML_RESPONSE, lat);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                    }
                }
            };
//    LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String ML_VALUE= intent.getStringExtra(ClipboardMonitorService.ML_RESPONSE);
//            if ( != null) {
//                Toast.makeText(getApplicationContext(), "Latitude " + lat + "\n Longitude:" + lng, Toast.LENGTH_LONG).show();
//Notification notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
//        .setSmallIcon(R.drawable.new_mail)
//        .setContentTitle(emailObject.getSenderName())
//        .setContentText(emailObject.getSubject())
//        .setLargeIcon(emailObject.getSenderAvatar())
//        .setStyle(new NotificationCompat.BigTextStyle()
//                .bigText(emailObject.getSubjectAndSnippet()))
//        .build();
//            }
//        }
//    }, new IntentFilter(userlocation_service.CLIPBORD_MONITOR_SERVICE_ACTION));
}