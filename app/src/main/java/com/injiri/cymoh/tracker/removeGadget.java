package com.injiri.cymoh.tracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.injiri.cymoh.tracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class removeGadget extends Fragment {


    public removeGadget() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_remove_gadget, container, false);
    }

}
