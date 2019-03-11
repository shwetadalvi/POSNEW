package com.abremiratesintl.KOT.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.utils.PrefUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserManagementFragment extends Fragment {


    public UserManagementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);
        PrefUtils prefUtils = new PrefUtils(getContext());
        return view;
    }

    private static final String TAG = "UserManagementFragment";
}
