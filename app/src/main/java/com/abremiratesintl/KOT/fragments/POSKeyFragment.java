package com.abremiratesintl.KOT.fragments;


import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.models.Admin;
import com.abremiratesintl.KOT.models.POSKeyResponse;
import com.abremiratesintl.KOT.utils.PrefUtils;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

import static com.abremiratesintl.KOT.utils.Constants.DEAFULT_PREFS;
import static com.abremiratesintl.KOT.utils.Constants.FEASYCOM;
import static com.abremiratesintl.KOT.utils.Constants.IS_VERIFIED;
import static com.abremiratesintl.KOT.utils.Constants.PRINTER;
import static com.abremiratesintl.KOT.utils.Constants.PRINTER_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class POSKeyFragment extends BaseFragment {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.editPasscode)
    EditText editPasscode;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    private Unbinder mUnbinder;
    Admin admin = new Admin();

    private PrefUtils mPrefUtils;
    private TelephonyManager telephonyManager;
    private String imeiNumber,androidId,deviceName;
    public POSKeyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pos_key, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mPrefUtils = new PrefUtils(getContext());

        Navigation.findNavController(view).navigate(R.id.action_POSKeyFragment_to_homeFragment22);
        deviceName = getDeviceName();
       imeiNumber = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        //deviceId();
        return view;
    }



    @OnClick(R.id.btnSubmit)
    public void onClickedSave(View view) {


        String password = getString(editPasscode);
       // showSnackBar(getView(), "Id :"+imeiNumber+" name "+deviceName, 1500);

        if (isEmpty(password)) {
            showSnackBar(getView(), "Enter POS key", 1500);
            return;
        } else {
            RequestParams params = new RequestParams();

            params.put("machineId",androidId);
            params.put("machineName", deviceName);
            params.put("key", password);
            String url = "http://148.72.64.138:3009/posKey/validate";

           callApiPost(params, url);

        }
    }
    public void callApiPost(RequestParams params,String url) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(50000);
        progressBar.setVisibility(View.VISIBLE);


        client.post( url, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("Failure-", "JSON:" + errorResponse);
                try {
                    progressBar.setVisibility(View.GONE);
                    // mActivity.getResponse(errorResponse.toString(), requestId);
                } catch (Exception e) {

                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Success-", "JSON:" + response.toString());
                progressBar.setVisibility(View.GONE);
                //   mActivity.getResponse(response.toString(), requestId);
                   Gson gson = new Gson();
                final POSKeyResponse keyResponse = gson.fromJson(response.toString(), POSKeyResponse.class);
                if(keyResponse.getResult().equalsIgnoreCase("Valid")) {
                    mPrefUtils.putBooleanPreference(DEAFULT_PREFS, IS_VERIFIED, true);
                    navigate();
                }else
                    showSnackBar(getView(), keyResponse.getResult(), 1500);
            }
        });
    }

    private void navigate() {
      //  Navigation.findNavController(view).navigate(R.id.action_POSKeyFragment_to_homeFragment22);
    }


    private boolean isEmpty(String companyParam) {
        return companyParam == null || companyParam.isEmpty();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
    private void deviceId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
            return;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
                        return;
                    }
                     imeiNumber = telephonyManager.getDeviceId();
                    Toast.makeText(getActivity(),imeiNumber,Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(),"Without permission we check",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }
    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}

