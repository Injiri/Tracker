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
    private static final String url="";

    private ExecutorService mThreadPool = Executors.newSingleThreadExecutor();
    private ClipboardManager mClipboardManager;

    @Override
    public void onCreate() {
        super.onCreate();

        //start the clipboard monito service
        mClipboardManager =  (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
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
                    if (clip != null) {
                        Toast.makeText(getApplicationContext(), "clip val: "+ clip.toString(), Toast.LENGTH_LONG).show();
                        //request modelvalidation
                        //show feedback poup
                        //reset clipboard to null
//                        show feedback popup

                    }

//
//  mThreadPool.execute(new WriteHistoryRunnable(
//                            clip.getItemAt(0).getText()));
                }
            };
}