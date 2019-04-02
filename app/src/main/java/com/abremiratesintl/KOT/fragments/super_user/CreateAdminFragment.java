package com.abremiratesintl.KOT.fragments.super_user;


import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.models.Admin;
import com.abremiratesintl.KOT.models.Company;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.abremiratesintl.KOT.utils.Constants.ADMIN;
import static com.abremiratesintl.KOT.utils.Constants.DEAFULT_PREFS;
import static com.abremiratesintl.KOT.utils.Constants.FEASYCOM;
import static com.abremiratesintl.KOT.utils.Constants.PRINTER;
import static com.abremiratesintl.KOT.utils.Constants.PRINTER_TYPE;
import static com.abremiratesintl.KOT.utils.Constants.USER_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAdminFragment extends BaseFragment {
    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.editPasscode)
    EditText editPasscode;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    private AppDatabase mDatabase;
    private Unbinder mUnbinder;
    Admin admin = new Admin();
    @BindView(R.id.checkBoxFeasycome)
    CheckBox checkBoxFeasycome;
    @BindView(R.id.checkBoxPrinter)
    CheckBox checkBoxPrinter;
    private PrefUtils mPrefUtils;
    public CreateAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_admin, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mDatabase = AppDatabase.getInstance(getContext());
        mPrefUtils = new PrefUtils(getContext());
        LiveData<Admin> adminLiveData = mDatabase.mAdminDao().getAdmin();
        adminLiveData.observe(this, admin -> {
            if (admin != null) {
                fillFields(admin);
            }
        });
        checkBoxFeasycome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                        @Override
                                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                            if (isChecked) {

                                                                mPrefUtils.putStringPreference(DEAFULT_PREFS,PRINTER_TYPE,FEASYCOM);


                                                            }

                                                        }
                                                    }
        );
        checkBoxPrinter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                        @Override
                                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                            if (isChecked) {

                                                                mPrefUtils.putStringPreference(DEAFULT_PREFS,PRINTER_TYPE,PRINTER);


                                                            }

                                                        }
                                                    }
        );
        return view;
    }

    private void fillFields(Admin admin) {
        editName.setText(admin.getAdminName());
        editPasscode.setText(admin.getPassword());

    }

    @OnClick(R.id.btnSubmit)
    public void onClickedSave() {
        String adminName = getString(editName);
        String password = getString(editPasscode);


        if (isEmpty(adminName) && isEmpty(password)) {
            showSnackBar(getView(), getStringfromResource(R.string.every_fields_are_mandatory), 1500);
            return;
        } else {

            Admin admin1 = new Admin();
            admin1.setAdminName(adminName);
            admin1.setPassword(password);

            Thread t = new Thread(() -> {
                mDatabase.mAdminDao().insertAdmin(admin1);
            });
            t.start();

            Toast.makeText(getActivity(), "Data Added Successfully !", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmpty(String companyParam) {
        return companyParam == null || companyParam.isEmpty();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
