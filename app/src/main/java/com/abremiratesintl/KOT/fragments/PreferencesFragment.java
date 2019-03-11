package com.abremiratesintl.KOT.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferencesFragment extends PreferenceFragmentCompat {

    DropDownPreference mDropDownPreference;

    public PreferencesFragment() {
        // Required empty public constructor
    }

    @Override public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDropDownPreference = (DropDownPreference) getPreferenceManager().findPreference("dropdown_preference");
        PrefUtils prefUtils = new PrefUtils(getContext());
        mDropDownPreference.setOnPreferenceChangeListener((preference, entryValue) -> {
            prefUtils.putStringPreference(Constants.DEAFULT_PREFS, Constants.PRINTER_PREF_KEY, entryValue.toString());
            return true;
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
