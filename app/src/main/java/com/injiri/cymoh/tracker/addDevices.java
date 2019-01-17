package com.injiri.cymoh.tracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.injiri.cymoh.tracker.device_configurations.Device;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class addDevices extends Fragment {


    public addDevices() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_devices, container, false);
    }
    public  void add_devices(){
        ArrayList<Device> deviceArrayList= new ArrayList<Device>();
        
    }

}
