package com.abremiratesintl.KOT;

import android.arch.lifecycle.LiveData;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.dbHandler.DatabaseCopier;
import com.abremiratesintl.KOT.models.Company;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;

import static com.abremiratesintl.KOT.utils.Constants.PERMISSION_STORAGE_READ;
import static com.abremiratesintl.KOT.utils.Constants.PERMISSION_STORAGE_WRITE;
import static com.abremiratesintl.KOT.utils.Constants.REQUEST_CODE_PERMISSION_STORAGE;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("keys");
    }

    public static native String getNativeKey();
    public static native String getAdminKey();

    private TextView mTitle;
    private ImageView imageAddItem;
private Toolbar toolbar;
    private AppDatabase mDatabase;
    private PrefUtils mPrefUtils;
    @RequiresApi(api = Build.VERSION_CODES.M) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mTitle = findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.app_name);
        imageAddItem = findViewById(R.id.imageAddItem);
        /*DatabaseCopier databaseCopier = new DatabaseCopier();
        AppDatabase appDatabase = databaseCopier.getRoomDatabase();*/
        mPrefUtils = new PrefUtils(getApplicationContext());
        mDatabase = AppDatabase.getInstance(getApplicationContext());
        LiveData<Company> companyLiveData = mDatabase.mCompanyDao().getCompany();
        companyLiveData.observe(this, company -> {
            if (company != null) {
                setConstants(company);
            }
        });

        imageAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();

            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        if (!hasPermission(new String[]{PERMISSION_STORAGE_WRITE, PERMISSION_STORAGE_READ})) {
            if (shouldShowRequestPermissionRationale(PERMISSION_STORAGE_WRITE) && shouldShowRequestPermissionRationale(PERMISSION_STORAGE_READ)) {
                permissionAlert();
            }
            requestPermissions(new String[]{PERMISSION_STORAGE_WRITE, PERMISSION_STORAGE_READ}, REQUEST_CODE_PERMISSION_STORAGE);
        }
    }

    private void setConstants(Company company) {
        Constants.COMPANY_NAME = company.getCompanyName();
        Constants.COMPANY_TELE = company.getCompanyTel();
        Constants.COMPANY_ADDRESS = company.getCompanyAddress();
        Constants.COMPANY_TRN = company.getCompanyTrn();
        Constants.COMPANY_VAT = company.getCompanyVat();

        Log.e("Company ","vat :"+company.getCompanyVat());
        mPrefUtils.putStringPreference(Constants.DEAFULT_PREFS, Constants.COMPANY_PREFIX, company.getCompanyPrefix());

    }

    private boolean hasPermission(String[] permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission[0]) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(permission[1]) == PackageManager.PERMISSION_GRANTED;
        }return true;
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }
void permissionAlert() {
        BaseFragment baseFragment = new BaseFragment();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(baseFragment.getStringfromResource(R.string.storage_permission_alert_title));
        alertBuilder.setMessage(baseFragment.getStringfromResource(R.string.storage_permission_alert_message));
        alertBuilder.setPositiveButton(baseFragment.getStringfromResource(R.string.ok), (dialog, which) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{PERMISSION_STORAGE_WRITE, PERMISSION_STORAGE_READ}, REQUEST_CODE_PERMISSION_STORAGE);
            }
        });
        alertBuilder.setNegativeButton(baseFragment.getStringfromResource(R.string.ok), (dialog, which) -> {
            dialog.dismiss();
        });
        alertBuilder.create().show();
    }

    public static String getPassword() {
        return new String(Base64.decode(getNativeKey(), Base64.DEFAULT));
    }

    public static String getKey() {
        return new String(Base64.decode(getAdminKey(), Base64.DEFAULT));
    }

    public void changeTitle(String title) {
        mTitle.setText(title);
        imageAddItem.setVisibility(View.GONE);
    }
    public void addNewItem() {

        imageAddItem.setVisibility(View.VISIBLE);
    }
    public Toolbar getToolbar() {

        return toolbar;
    }

}
