package com.abremiratesintl.KOT.fragments.super_user;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abremiratesintl.KOT.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAdminFragment extends Fragment {


    public CreateAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_admin, container, false);
    }

}
