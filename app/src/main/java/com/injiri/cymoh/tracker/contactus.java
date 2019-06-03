package com.injiri.cymoh.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.injiri.cymoh.tracker.device_configurations.Device;
import com.injiri.cymoh.tracker.device_configurations.usernode;
import com.injiri.cymoh.tracker.tracker_settings.DataParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.layout.simple_spinner_item;


/**
 * A simple {@link Fragment} subclass.
 */
public class contactus extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Spinner devicesSpinner;
    ArrayList<Device> devicesArray = new ArrayList<Device>();
    ArrayList<String> spinnerDevices = new ArrayList<String>();
    Device selectedDevice;


    private HashMap<String, Marker> markers = new HashMap<String, Marker>();
    TextView deviceNameTxt, deviceId, lastFix, deviceLocated;
    ImageView geofence, deviceInfo, batteryStatus;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("Accounts/val-235353");
    Boolean mapReady = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                initialize(); //initializes the firebase database with default devices.
        // Inflate the layout for this fragment

        View contactus_view = inflater.inflate(R.layout.fragment_contactus, container, false);
        return contactus_view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.contactus_map);
        mapFragment.getMapAsync(this);

        devicesSpinner = (Spinner) view.findViewById(R.id.devices_spinner);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object dataset = dataSnapshot.getValue(usernode.class);
                if (dataset != null) {
                    usernode currentuser = (usernode) dataset;

                    for (Device device : currentuser.getDevices()) {
                        //add all the snapshot data to the spinner
                        spinnerDevices.add(device.getDeviceName());
                        devicesArray.add(device);
                        selectedDevice = devicesArray.get(0);
                        update_mapui(selectedDevice);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Something fishy happened! Sorry for the inconvenience!", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter dataAdapter = new ArrayAdapter(this.getActivity(), simple_spinner_item, spinnerDevices);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
        devicesSpinner.setAdapter(dataAdapter);
        devicesSpinner.setVisibility(View.VISIBLE);
        devicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedDevice = devicesArray.get(position);
                update_mapui(selectedDevice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });


        deviceInfo = (ImageView) view.findViewById(R.id.details);
        deviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //display the device info for current marker.
                View popupView = getLayoutInflater().inflate(R.layout.device_detatails_popup, null);
                PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setAnimationStyle(R.style.deviceinfopopup_style);
                deviceId = (TextView) popupView.findViewById(R.id.device_id);
                deviceNameTxt = (TextView) popupView.findViewById(R.id.device_name);
                lastFix = (TextView) popupView.findViewById(R.id.last_updateTime);
                deviceLocated = (TextView) popupView.findViewById(R.id.device_location);

                deviceId.setText("Device id    : " + selectedDevice.getDeviceId());
                deviceNameTxt.setText("Device name:" + selectedDevice.getDeviceName());
                lastFix.setText("Last updated:" + selectedDevice.getLastupdated());
                deviceLocated.setText("Last updated:" + selectedDevice.getGeoRadius());

                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.GREEN));

                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);

                popupWindow.update();

                // Show popup window offset 1,1 to infobtn.
                popupWindow.showAsDropDown(devicesSpinner);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mapReady = true;

    }

    public void update_mapui(final Device selectedDevice) {

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String user_lat = intent.getStringExtra(userlocation_service.USER_LATITUDE);
                String user_lng = intent.getStringExtra(userlocation_service.USER_LONGITUDE);
                if (user_lat != null && user_lng != null) {
                    LatLng user_latlng = new LatLng(Double.valueOf(user_lat), Double.valueOf(user_lng));
                    MarkerOptions option = new MarkerOptions();
                    option.position(user_latlng).title("DSC_MMUST ,kakamega-Kenya");
                    map.clear();
                    map.addMarker(option);

                    Location userLocation = new Location("DSC");
                    userLocation.setLatitude(user_latlng.latitude);
                    userLocation.setLongitude(user_latlng.longitude);

                    Location deviceLocation = new Location("DSC_IOT");
                    deviceLocation.setLatitude(selectedDevice.getDeviceLat());
                    deviceLocation.setLongitude(selectedDevice.getDeviceLon());
                    LatLng device_latlng = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());


                    map.addMarker(new MarkerOptions().position(device_latlng).title("device "));
                    Marker mark = map.addMarker(new MarkerOptions().position(device_latlng).title("device "));

                    markers.put(selectedDevice.getDeviceId(), mark);


                    map.moveCamera(CameraUpdateFactory.newLatLng(user_latlng));
                    map.animateCamera(CameraUpdateFactory.zoomTo(14

                    ));
                    String last_update = selectedDevice.getLastupdated();
                    double distance = distance(userLocation, deviceLocation);

                    // Getting URL to the Google Directions API
                    String url = getUrl(user_latlng, device_latlng);
                    Log.d("onMapClick", url.toString());
                    contactus.FetchUrl FetchUrl = new contactus.FetchUrl();

                    // Start downloading json data from Google Directions API
                    FetchUrl.execute(url);
                }

            }
        }, new IntentFilter(userlocation_service.LOCATION_BROADCAST_ACTION));


    }


    private double distance(Location userLocation, Location deviceLocation) {
        return (userLocation.distanceTo(deviceLocation)) * 0.000621371;
    }

    private void initialize() {

        ArrayList<Device> deviceList = new ArrayList<Device>();
        deviceList.add(new Device("April 23,2017", "DVC21QHS2", "ACER AMD-E2", "100", 0.31126424, 34.2324234234, "3000", "90", 0.2888587, 34.7643458));
        deviceList.add(new Device("April 23,2017", "DVC51QRG2", "HP 1560 probook", "100", 0.31126424, 34.2324234234, "3000", "90", 0.2888587, 34.7643458));
        deviceList.add(new Device("April 23,2017", "DVC21QRS4", "DELL Vostro-3642", "100", 0.31126424, 34.2324234234, "3000", "90", 0.2888587, 34.7643458));

        usernode user1 = new usernode("simoninjiri", "otwero", deviceList);
        //pass the user credentials into the daadadbtabase via the userNode class instance.
        databaseReference.setValue(user1);

    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of path
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of path
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // enable sensor
        String sensor = "sensor=false";
        // prepare the parameters from the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    //download the json data from url
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Create a http con to communicate with the url
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // get data passed from url
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    //pass goole places in json format
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());
                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                map.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }


}


