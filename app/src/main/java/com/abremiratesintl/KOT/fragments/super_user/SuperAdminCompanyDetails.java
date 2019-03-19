package com.abremiratesintl.KOT.fragments.super_user;


import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.models.Company;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuperAdminCompanyDetails extends BaseFragment {

    @BindView(R.id.company_name)
    EditText mCompanyName;
    @BindView(R.id.company_address)
    EditText mCompanyAddress;
    @BindView(R.id.company_tel)
    EditText mCompanyTel;
    @BindView(R.id.company_trn)
    EditText mCompanyTrn;
    @BindView(R.id.company_prefix)
    EditText mCompanyPrefix;
    @BindView(R.id.company_vat)
    EditText mCompanyVat;
    @BindView(R.id.admin_company_save)
    Button mCompanySave;
    private Unbinder mUnbinder;
    private AppDatabase mDatabase;

    Company company = new Company();


    public SuperAdminCompanyDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_super_admin_company_details, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mDatabase = AppDatabase.getInstance(getContext());
        LiveData<Company> companyLiveData = mDatabase.mCompanyDao().getCompany();
        companyLiveData.observe(this, company -> {
            if (company != null) {
                fillFields(company);
            }
        });
        return view;
    }

    private void fillFields(Company company) {
        mCompanyName.setText(company.getCompanyName());
        mCompanyAddress.setText(company.getCompanyAddress());
        mCompanyTel.setText(company.getCompanyTel());
        mCompanyTrn.setText(company.getCompanyTrn());
        mCompanyPrefix.setText(company.getCompanyPrefix());
        mCompanyVat.setText(company.getCompanyVat());
    }

    @OnClick(R.id.admin_company_save)
    public void onClickedSave(){
        String companyName = getString(mCompanyName);
        String companyTel = getString(mCompanyTel);
        String companyAddress = getString(mCompanyAddress);
        String companyTrn = getString(mCompanyTrn);
        String companyPrefix = getString(mCompanyPrefix);
        String companyVat = getString(mCompanyVat);
        if (isEmpty(companyName) && isEmpty(companyTel) && isEmpty(companyAddress) && isEmpty(companyTrn) && isEmpty(companyPrefix)) {
            showSnackBar(getView(), getStringfromResource(R.string.every_fields_are_mandatory), 1500);
            return;
        } else {
            company.setCompanyAddress(companyAddress);
            company.setCompanyName(companyName);
            company.setCompanyTel(companyTel);
            company.setCompanyTrn(companyTrn);
            company.setCompanyPrefix(companyPrefix);
            company.setCompanyVat(companyVat);
            Thread t = new Thread(() -> {
                mDatabase.mCompanyDao().insertCompany(company);
            });
            t.start();

            Toast.makeText(getActivity(),"Data Added Successfully !",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmpty(String companyParam) {
        return companyParam == null || companyParam.isEmpty();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
