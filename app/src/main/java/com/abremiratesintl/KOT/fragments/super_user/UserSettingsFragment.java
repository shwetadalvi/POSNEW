package com.abremiratesintl.KOT.fragments.super_user;


import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.models.Cashier;
import com.abremiratesintl.KOT.models.Company;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.abremiratesintl.KOT.utils.Constants.ADMIN;
import static com.abremiratesintl.KOT.utils.Constants.CASHIER;
import static com.abremiratesintl.KOT.utils.Constants.DEAFULT_PREFS;
import static com.abremiratesintl.KOT.utils.Constants.USER_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserSettingsFragment extends BaseFragment implements CheckBox.OnCheckedChangeListener
{

    @BindView(R.id.checkItemView)
    CheckBox checkItemView;
    @BindView(R.id.checkItemInsert)
    CheckBox checkItemInsert;
    @BindView(R.id.checkItemUpdate)
    CheckBox checkItemUpdate;
    @BindView(R.id.checkItemDelete)
    CheckBox checkItemDelete;
    @BindView(R.id.checkCategoryView)
    CheckBox checkCategoryView;
    @BindView(R.id.checkCategoryInsert)
    CheckBox checkCategoryInsert;
    @BindView(R.id.checkCategoryUpdate)
    CheckBox checkCategoryUpdate;
    @BindView(R.id.checkCategoryDelete)
    CheckBox checkCategoryDelete;
    @BindView(R.id.checkPOSView)
    CheckBox checkPOSView;
    @BindView(R.id.checkPOSInsert)
    CheckBox checkPOSInsert;
    @BindView(R.id.checkPOSDelete)
    CheckBox checkPOSDelete;
    @BindView(R.id.checkPOSPrint)
    CheckBox checkPOSPrint;
    @BindView(R.id.checkInventoryView)
    CheckBox checkInventoryView;
    @BindView(R.id.checkInventoryInsert)
    CheckBox checkInventoryInsert;
    @BindView(R.id.checkInventoryUpdate)
    CheckBox checkInventoryUpdate;
    @BindView(R.id.checkInventoryDelete)
    CheckBox checkInventoryDelete;
    @BindView(R.id.checkDailyReportView)
    CheckBox checkDailyReportView;
    @BindView(R.id.checkDailyReporExport)
    CheckBox checkDailyReporExport;
    @BindView(R.id.checkSaleReportView)
    CheckBox checkSaleReportView;
    @BindView(R.id.checkSaleReporExport)
    CheckBox checkSaleReporExport;
    @BindView(R.id.checkItemReportView)
    CheckBox checkItemReportView;
    @BindView(R.id.checkItemReportExport)
    CheckBox checkItemReportExport;
    @BindView(R.id.checkVatReportView)
    CheckBox checkVatReportView;
    @BindView(R.id.checkVatReporExport)
    CheckBox checkVatReporExport;
    @BindView(R.id.checkCatReportView)
    CheckBox checkCatReportView;
    @BindView(R.id.checkCatReporExport)
    CheckBox checkCatReporExport;
    @BindView(R.id.checkInvReportView)
    CheckBox checkInvReportView;
    @BindView(R.id.checkInvReportExport)
    CheckBox checkInvReportExport;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.btnAdmin)
    Button btnAdmin;
    @BindView(R.id.btnCashier)
    Button btnCashier;
    @BindView(R.id.edtName)
    EditText edtName;

    private Unbinder mUnbinder;
    private AppDatabase mDatabase;
    private PrefUtils mPrefUtils;

    private Cashier cashier = new Cashier();

    private boolean isItemView = false,isItemInsert = false,isItemUpdate = false,isItemDelete = false;
    private boolean isCategoryView = false,isCategoryInsert = false,isCategoryUpdate = false,isCategoryDelete = false;
    private boolean isPOSView = false,isPOSInsert = false,isPOSPrint = false,isPOSDelete = false;
    private boolean isInventoryView = false,isInventoryInsert = false,isInventoryUpdate = false,isInventoryDelete = false;
    private boolean isDailyReportView = false,isDailyReportExport = false;
    private boolean isSaleReportView = false,isSaleReportExport = false;
    private boolean isItemReportView = false,isItemReportExport = false;
    private boolean isVatReportView = false,isVatReportExport = false;
    private boolean isCategoryReportView = false,isCategoryReportExport = false;
    private boolean isInventoryReportView = false,isInventoryReportExport = false;


    public UserSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_settings, container, false);
        ((MainActivity) getActivity()).changeTitle("Settings");
        mUnbinder = ButterKnife.bind(this, view);
        mDatabase = AppDatabase.getInstance(getContext());
        mPrefUtils = new PrefUtils(getContext());

        LiveData<Cashier> cashierLiveData = mDatabase.mCashierDao().getCashier1();
        cashierLiveData.observe(this, cashier -> {
            if (cashier != null) {
                fillFields(cashier);
            }
        });


        checkItemView.setOnCheckedChangeListener(this);
        checkItemInsert.setOnCheckedChangeListener(this);
        checkItemUpdate.setOnCheckedChangeListener(this);
        checkItemDelete.setOnCheckedChangeListener(this);
        checkCategoryView.setOnCheckedChangeListener(this);
        checkCategoryInsert.setOnCheckedChangeListener(this);
        checkCategoryUpdate.setOnCheckedChangeListener(this);
        checkCategoryDelete.setOnCheckedChangeListener(this);
        checkPOSView.setOnCheckedChangeListener(this);
        checkPOSInsert.setOnCheckedChangeListener(this);
        checkPOSPrint.setOnCheckedChangeListener(this);
        checkPOSDelete.setOnCheckedChangeListener(this);
        checkInventoryView.setOnCheckedChangeListener(this);
        checkInventoryInsert.setOnCheckedChangeListener(this);
        checkInventoryUpdate.setOnCheckedChangeListener(this);
        checkInventoryDelete.setOnCheckedChangeListener(this);
        checkDailyReportView.setOnCheckedChangeListener(this);
        checkDailyReporExport.setOnCheckedChangeListener(this);
        checkSaleReportView.setOnCheckedChangeListener(this);
        checkSaleReporExport.setOnCheckedChangeListener(this);
        checkItemReportView.setOnCheckedChangeListener(this);
        checkItemReportExport.setOnCheckedChangeListener(this);
        checkVatReportView.setOnCheckedChangeListener(this);
        checkVatReporExport.setOnCheckedChangeListener(this);
        checkCatReportView.setOnCheckedChangeListener(this);
        checkCatReporExport.setOnCheckedChangeListener(this);
        checkInvReportView.setOnCheckedChangeListener(this);
        checkInvReportExport.setOnCheckedChangeListener(this);

        return view;

    }

    private void fillFields(Cashier cashier) {

        edtName.setText(cashier.getCashierName());
        isItemView = cashier.isItemView();
        isItemInsert = cashier.isItemInsert();
        isItemUpdate = cashier.isItemUpdate();
        isItemDelete = cashier.isItemDelete();
        isCategoryView = cashier.isCategoryView();
        isCategoryInsert = cashier.isCategoryInsert();
        isCategoryUpdate = cashier.isCategoryUpdate();
        isCategoryDelete = cashier.isCategoryDelete();
        isPOSView = cashier.isPOSView();
        isPOSInsert = cashier.isPOSInsert();
        isPOSPrint = cashier.isPOSPrint();
        isPOSDelete = cashier.isPOSDelete();
        isInventoryView = cashier.isInventoryView();
        isInventoryInsert = cashier.isInventoryInsert();
        isInventoryUpdate = cashier.isInventoryUpdate();
        isInventoryDelete = cashier.isInventoryDelete();
        isDailyReportView = cashier.isDailyReportView();
        isDailyReportExport = cashier.isDailyReportExport();
        isSaleReportView = cashier.isSaleReportView();
        isSaleReportExport = cashier.isSaleReportExport();
        isItemReportView = cashier.isItemReportView();
        isItemReportExport = cashier.isItemReportExport();
        isVatReportView = cashier.isVatReportView();
        isVatReportExport = cashier.isVatReportExport();
        isCategoryReportView = cashier.isCategoryReportView();
        isCategoryReportExport = cashier.isCategoryReportExport();
        isInventoryReportView = cashier.isInventoryReportView();
        isInventoryReportExport = cashier.isInventoryReportExport();


        checkCategoryView.setChecked(isCategoryView);
        checkCategoryInsert.setChecked(isCategoryInsert);
        checkCategoryUpdate.setChecked(isCategoryUpdate);
        checkCategoryDelete.setChecked(isCategoryDelete);
        checkItemView.setChecked(isItemView);
        checkItemInsert.setChecked(isItemInsert);
        checkItemUpdate.setChecked(isItemUpdate);
        checkItemDelete.setChecked(isItemDelete);
        checkPOSView.setChecked(isPOSView);
        checkPOSInsert.setChecked(isPOSInsert);
        checkPOSPrint.setChecked(isPOSPrint);
        checkPOSDelete.setChecked(isPOSDelete);
        checkInventoryView.setChecked(isInventoryView);
        checkInventoryInsert.setChecked(isInventoryInsert);
        checkInventoryUpdate.setChecked(isInventoryUpdate);
        checkInventoryDelete.setChecked(isInventoryDelete);
        checkDailyReportView.setChecked(isDailyReportView);
        checkDailyReporExport.setChecked(isDailyReportExport);
        checkSaleReportView.setChecked(isSaleReportView);
        checkSaleReporExport.setChecked(isSaleReportExport);
        checkItemReportView.setChecked(isItemReportView);
        checkItemReportExport.setChecked(isItemReportExport);
        checkVatReportView.setChecked(isVatReportView);
        checkVatReporExport.setChecked(isVatReportExport);
        checkCatReportView.setChecked(isCategoryReportView);
        checkCatReporExport.setChecked(isCategoryReportExport);
        checkInvReportView.setChecked(isInventoryReportView);
        checkInvReportExport.setChecked(isInventoryReportExport);
    }

    @OnClick(R.id.btnAdmin)
    public void onClickAdmin(){
        boolean isCashier = false;
        mPrefUtils.putStringPreference(DEAFULT_PREFS,USER_TYPE,ADMIN);
        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER))
            isCashier = true;
        Log.e("Cashier settings: ","type  : :"+isCashier);
    }
    @OnClick(R.id.btnCashier)
    public void onClickCashier(){
        mPrefUtils.putStringPreference(DEAFULT_PREFS,USER_TYPE,CASHIER);
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }
    @OnClick(R.id.btnSubmit)
    public void onClickedSave(){

        String str_name = getString(edtName);
         cashier.setCashierName(str_name
         );

         cashier.setItemView(isItemView);
         cashier.setItemInsert(isItemInsert);
         cashier.setItemUpdate(isItemUpdate);
         cashier.setItemDelete(isItemDelete);
         cashier.setCategoryView(isCategoryView);
         cashier.setCategoryInsert(isCategoryInsert);
         cashier.setCategoryUpdate(isCategoryUpdate);
         cashier.setCategoryDelete(isCategoryDelete);
         cashier.setPOSView(isPOSView);
         cashier.setPOSInsert(isPOSInsert);
         cashier.setPOSPrint(isPOSPrint);
         cashier.setPOSDelete(isPOSDelete);
         cashier.setInventoryView(isInventoryView);
         cashier.setInventoryInsert(isInventoryInsert);
         cashier.setInventoryUpdate(isInventoryUpdate);
         cashier.setInventoryDelete(isInventoryDelete);
         cashier.setDailyReportView(isDailyReportView);
         cashier.setDailyReportExport(isDailyReportExport);
         cashier.setSaleReportView(isSaleReportView);
         cashier.setSaleReportExport(isSaleReportExport);
         cashier.setItemReportView(isItemReportView);
         cashier.setItemReportExport(isItemReportExport);
         cashier.setVatReportView(isVatReportView);
         cashier.setVatReportExport(isVatReportExport);
         cashier.setCategoryReportView(isCategoryReportView);
         cashier.setCategoryReportExport(isCategoryReportExport);
         cashier.setInventoryReportView(isInventoryReportView);
         cashier.setInventoryReportExport(isInventoryReportExport);


            Thread t = new Thread(() -> {
                mDatabase.mCashierDao().insertCashier(cashier);
            });
            t.start();

            Toast.makeText(getActivity(),"Data Added Successfully !",Toast.LENGTH_SHORT).show();

    }

    private boolean isEmpty(String companyParam) {
        return companyParam == null || companyParam.isEmpty();
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.checkItemView:
                if(isChecked)
               isItemView = true;
                    else
                isItemView = false;
                break;
            case R.id.checkItemInsert:
                if(isChecked)
               isItemInsert = true;
                    else
               isItemInsert = false;
                break;
            case R.id.checkItemUpdate:
                if(isChecked)
                    isItemUpdate = true;
                else
                    isItemView = false;
                break;
            case R.id.checkItemDelete:
                if(isChecked)
                    isItemDelete = true;
                else
                    isItemDelete = false;
                break;
            case R.id.checkCategoryView:
                if(isChecked)
                    isCategoryView = true;
                else
                    isCategoryView = false;
                break;
            case R.id.checkCategoryInsert:
                if(isChecked)
                    isCategoryInsert = true;
                else
                    isCategoryInsert = false;
                break;
            case R.id.checkCategoryUpdate:
                if(isChecked)
                    isCategoryUpdate = true;
                else
                    isCategoryUpdate = false;
                break;
            case R.id.checkCategoryDelete:
                if(isChecked)
                    isCategoryDelete = true;
                else
                    isCategoryDelete = false;
            case R.id.checkPOSView:
                if(isChecked)
                    isPOSView = true;
                else
                    isPOSView = false;
                break;
            case R.id.checkPOSInsert:
                if(isChecked)
                    isPOSInsert = true;
                else
                    isPOSInsert = false;
                break;
            case R.id.checkPOSPrint:
                if(isChecked)
                    isPOSPrint = true;
                else
                    isPOSPrint = false;
                break;
            case R.id.checkPOSDelete:
                if(isChecked)
                    isPOSDelete = true;
                else
                    isPOSDelete = false;
                break;
            case R.id.checkInventoryView:
                if(isChecked)
                    isInventoryView = true;
                else
                    isInventoryView = false;
                break;
            case R.id.checkInventoryInsert:
                if(isChecked)
                    isInventoryInsert = true;
                else
                    isInventoryInsert = false;
                break;
            case R.id.checkInventoryUpdate:
                if(isChecked)
                    isInventoryUpdate = true;
                else
                    isInventoryUpdate = false;
                break;
            case R.id.checkInventoryDelete:
                if(isChecked)
                    isInventoryDelete = true;
                else
                    isInventoryDelete = false;
                break;
            case R.id.checkDailyReportView:
                if(isChecked)
                    isDailyReportView = true;
                else
                    isDailyReportView = false;
                break;
            case R.id.checkDailyReporExport:
                if(isChecked)
                    isDailyReportExport = true;
                else
                    isDailyReportExport = false;
                break;
            case R.id.checkSaleReportView:
                if(isChecked)
                    isSaleReportView = true;
                else
                    isSaleReportView = false;
                break;
            case R.id.checkSaleReporExport:
                if(isChecked)
                    isSaleReportExport = true;
                else
                    isSaleReportExport = false;
                break;
            case R.id.checkItemReportView:
                if(isChecked)
                    isItemReportView = true;
                else
                    isItemReportView = false;
                break;
            case R.id.checkItemReportExport:
                if(isChecked)
                    isItemReportExport= true;
                else
                    isItemReportExport = false;
                break;
            case R.id.checkVatReportView:
                if(isChecked)
                    isVatReportView = true;
                else
                    isVatReportView = false;
                break;
            case R.id.checkVatReporExport:
                if(isChecked)
                    isVatReportExport= true;
                else
                    isVatReportExport = false;
                break;
            case R.id.checkCatReportView:
                if(isChecked)
                    isCategoryReportView = true;
                else
                    isCategoryReportView = false;
                break;
            case R.id.checkCatReporExport:
                if(isChecked)
                    isCategoryReportExport= true;
                else
                    isCategoryReportExport = false;
                break;
            case R.id.checkInvReportView:
                if(isChecked)
                    isInventoryReportView = true;
                else
                    isInventoryReportView = false;
                break;
            case R.id.checkInvReportExport:
                if(isChecked)
                    isInventoryReportExport= true;
                else
                    isInventoryReportExport = false;
                break;
        }

    }
}
