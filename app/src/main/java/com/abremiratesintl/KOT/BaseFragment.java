package com.abremiratesintl.KOT;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class BaseFragment extends Fragment {
    public String getString(View tv) {

        if (tv instanceof EditText) {
            return ((EditText) tv).getText().toString();
        } else {
            return ((TextView) tv).getText().toString();
        }
    }

    public void showSnackBar(View view, String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }

    public String getStringfromResource(int id) {
        return getContext().getString(id);
    }
}
