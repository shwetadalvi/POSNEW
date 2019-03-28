package com.abremiratesintl.KOT.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;

import androidx.navigation.Navigation;

import static com.abremiratesintl.KOT.utils.Constants.ADMIN;
import static com.abremiratesintl.KOT.utils.Constants.CASHIER;
import static com.abremiratesintl.KOT.utils.Constants.DEAFULT_PREFS;
import static com.abremiratesintl.KOT.utils.Constants.USER_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferencesFragment extends PreferenceFragmentCompat {

    DropDownPreference mDropDownPreference;

    public PreferencesFragment() {
        // Required empty public constructor
    }

    @Override public void onCreatePreferences(Bundle bundle, String rootKey) {
     //   setContentView(R.layout.buttonLayout);
        setPreferencesFromResource(R.xml.preferences, rootKey);

    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mDropDownPreference = (DropDownPreference) getPreferenceManager().findPreference("dropdown_preference");
        PrefUtils prefUtils = new PrefUtils(getContext());
        mDropDownPreference.setOnPreferenceChangeListener((preference, entryValue) -> {
            prefUtils.putStringPreference(DEAFULT_PREFS, Constants.PRINTER_PREF_KEY, entryValue.toString());
            return true;
        });

        Preference button_logout = (Preference)getPreferenceManager().findPreference("button_Logout");
        if(prefUtils.getStringPrefrence(DEAFULT_PREFS,Constants.USER_TYPE,CASHIER).equals(ADMIN))
            button_logout.setVisible(true);
        else
            button_logout.setVisible(false);

        if (button_logout != null) {
            button_logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                 prefUtils.putStringPreference(DEAFULT_PREFS,USER_TYPE,CASHIER);
                    button_logout.setVisible(false);
                    return true;
                }
            });
        }


        Preference button = (Preference)getPreferenceManager().findPreference("button_preference");

      //  button.setLayoutResource(R.layout.button_layout);


     if (button != null) {
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("from_fragment", 2);
                    Navigation.findNavController(getView()).navigate(R.id.action_preferencesFragment_to_loginFragment, bundle);
                    return true;
                }
            });
        }




            return super.onCreateView(inflater, container, savedInstanceState);
    }
}
