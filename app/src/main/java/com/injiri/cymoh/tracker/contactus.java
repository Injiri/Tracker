package com.injiri.cymoh.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class contactus extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private SupportMapFragment mapFragment;

    public contactus() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contactus_view = inflater.inflate(R.layout.fragment_contactus, container, false);
        return contactus_view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.contactus_map);
        mapFragment.getMapAsync(this);
//        public void onClick(View clicked){
//            if(clicked.getId() == R.id.Attack){
//                Spinner spinner = (Spinner) findViewById(R.id.UseItem);
//
//                //Sample String ArrayList
//                ArrayList<String> arrayList1 = new ArrayList<String>();
//
//                arrayList1.add("Bangalore");
//                arrayList1.add("Delhi");
//                arrayList1.add("Mumbai");
//                ArrayAdapter<String> adp = new ArrayAdapter<String> (this,android.R.layout.devices,arrayList1);
//                spinner.setAdapter(adp);
//
//                spinner.setVisibility(View.VISIBLE);
//                //Set listener Called when the item is selected in spinner
//                spinner.setOnItemSelectedListener(new OnItemSelectedListener()
//                {
//                    public void onItemSelected(AdapterView<?> parent, View view,
//                                               int position, long arg3)
//                    {
//                        String city = "The city is " + parent.getItemAtPosition(position).toString();
//                        Toast.makeText(parent.getContext(), city, Toast.LENGTH_LONG).show();
//
//                    }
//
//                    public void onNothingSelected(AdapterView<?> arg0)
//                    {
//                        // TODO Auto-generated method stub
//                    }
//                });
//
//                //BattleRun.dismiss();
//                Log.d("Item","Clicked");
//
//            }
//        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        update_mapui();
    }

    public void update_mapui() {

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String lat = intent.getStringExtra(userlocation_service.USER_LATITUDE);
                String lng = intent.getStringExtra(userlocation_service.USER_LONGITUDE);
                if (lat != null && lng != null) {
                    LatLng pos = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                    MarkerOptions option = new MarkerOptions();
                    option.position(pos).title("DSC_MMUST ,kakamega-Kenya");
                    map.clear();
                    map.addMarker(option);

                    Location deviceLocation = new Location("DSC_IOT");
                    deviceLocation.setLatitude(0.283);
                    deviceLocation.setLongitude(34.73);
                    LatLng device_latlng = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
                    map.addMarker(new MarkerOptions().position(device_latlng).title("device "));

                    map.moveCamera(CameraUpdateFactory.newLatLng(pos));
                    map.animateCamera(CameraUpdateFactory.newLatLng(pos));
                    map.animateCamera(CameraUpdateFactory.zoomTo(14

                    ));

                    // Getting URL to the Google Directions API
                    String url = getUrl(pos, device_latlng);
                    Log.d("onMapClick", url.toString());
                    FetchUrl FetchUrl = new FetchUrl();

                    // Start downloading json data from Google Directions API
                    FetchUrl.execute(url);
                }

            }
        }, new IntentFilter(userlocation_service.LOCATION_BROADCAST_ACTION));


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


