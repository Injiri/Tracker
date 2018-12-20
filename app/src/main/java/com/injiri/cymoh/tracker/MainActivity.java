package com.injiri.cymoh.tracker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Boolean userservice_running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String lat = intent.getStringExtra(userlocation_service.USER_LONGITUDE);
                String lng = intent.getStringExtra(userlocation_service.USER_LONGITUDE);
                if (lat != null && lng != null) {
                    Toast.makeText(getApplicationContext(), "Latitude " + lat + "\n Longitude:" + lng, Toast.LENGTH_LONG).show();

                }
            }
        }, new IntentFilter(userlocation_service.LOCATION_BROADCAST_ACTION));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_contactus) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            contactus contactus_fragment = new contactus();
            fragmentManager.beginTransaction().replace(R.id.main_layout, contactus_fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkgoogle_services();
    }

    public void checkgoogle_services() {

        if (isgoogleservices_available()) {
            checkinternet_connectivity(null);
        } else {
            Toast.makeText(getApplicationContext(), "google services not available", Toast.LENGTH_LONG).show();

        }
    }

    public Boolean checkinternet_connectivity(DialogInterface dialogInterface) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active_networkinfo = connectivityManager.getActiveNetworkInfo();
        if (active_networkinfo == null || !active_networkinfo.isConnected()) {
            connecttoInternet_dialog();
            return false;
        }
        if (dialogInterface != null) {
            dialogInterface.dismiss();

        }
        // if connection is active, proceed and check the permissions
        if (permission_granted()) {
            start_userlocation_service();
        } else {
            request_permisions();
        }
        return true;

    }

    public void connecttoInternet_dialog() {
        AlertDialog.Builder allert_builder = new AlertDialog.Builder(MainActivity.this);
        allert_builder.setTitle("No Internet");
        allert_builder.setMessage("sorry refresh connection and try agaain");
        String refresh_btnText = "Reconnect";
        allert_builder.setPositiveButton(refresh_btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //proceed only after  a user grrants all the permissions needed
                if (checkinternet_connectivity(dialog)) {
                    // now is location permission granted?
                    if (permission_granted()) {
                        //all resources are available to start the service
                        start_userlocation_service();

                    }


                }
            }
        });
        AlertDialog dialog = allert_builder.create();
        dialog.show();
    }

    public void start_userlocation_service() {
//        it runs untill the activity is stoped/clossed
        if (!userservice_running) {
            Toast.makeText(this, "user service running", Toast.LENGTH_LONG).show();
            //start broadcast sharering
            Intent intent = new Intent(this, userlocation_service.class);
            startService(intent);
            userservice_running = true;
        }
    }

    public boolean permission_granted() {
        int state1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int state2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return state1 == PackageManager.PERMISSION_GRANTED && state2 == PackageManager.PERMISSION_GRANTED;


    }

    public void request_permisions() {
        boolean provide_rationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        boolean provide_rationale2 = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (provide_rationale || provide_rationale2) {
            display_snackbar(R.string.rationale_permission, android.R.string.ok, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 34);
                }
            });
        } else {
//    Log.i(TAG,"Requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 34);
        }
    }


    public void display_snackbar(final int textstring_id, final int actionStringid, View.OnClickListener listener) {
        Snackbar.make(findViewById(R.id.content), getString(actionStringid), Snackbar.LENGTH_INDEFINITE).setAction(getString(actionStringid), listener).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 34) {
            if (grantResults.length <= 0) {
                Log.i("SNACKBAR", "user Interaction canceled");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("SNACKBAR", "permissions succesfully granted");
                start_userlocation_service();
            } else {
                //all permissions have been denied
                display_snackbar(R.string.deniedpermission_exp, R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //display the app setting intent
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                });
            }
        }
    }

    public boolean isgoogleservices_available() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();

            }
            return false;
        }
        return true;

    }

    @Override
    protected void onDestroy() {
        //stop location service broadcast
        stopService(new Intent(this, userlocation_service.class));
        userservice_running = false;
        super.onDestroy();
    }
}
