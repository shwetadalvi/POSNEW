package com.abremiratesintl.KOT.fragments;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Database;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.Navigation;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.models.Cashier;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.categoryManagement)
    CardView mCategoryManagement;
    @BindView(R.id.itemManagement)
    CardView mItemManagement;
    @BindView(R.id.pos)
    CardView mPos;
    @BindView(R.id.settings)
    CardView mSettings;
    @BindView(R.id.reports)
    CardView mReports;

    private PrefUtils mPrefUtils ;
//    @BindView(R.id.userManagement)
//    CardView mUserManagement;
    private Unbinder mUnbinder;
    private AppDatabase mDatabase;
private Cashier cashier ;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mPrefUtils = new PrefUtils(getContext());
        mDatabase = AppDatabase.getInstance(getContext());
        ((MainActivity) getActivity()).changeTitle("POS");

        LiveData<Cashier> cashierLiveData = mDatabase.mCashierDao().getCashier();
        cashierLiveData.observe(this, cashier -> {
            if (cashier != null ) {

                fillFields(cashier);
            }
        });
     //   Log.e("cashier nmae :","cat :"+cashier.isCategoryView() +"name :"+cashier.getCashierName());
        return view;
    }
    private void fillFields(Cashier cashier1){
        cashier = new Cashier();
        cashier.setCashierName(cashier1.getCashierName());
        cashier.setItemView(cashier1.isItemView());
        cashier.setItemInsert(cashier1.isItemInsert());
        cashier.setItemUpdate(cashier1.isItemUpdate());
        cashier.setItemDelete(cashier1.isItemDelete());
        cashier.setCategoryView(cashier1.isCategoryView());

        cashier.setCategoryInsert(cashier1.isCategoryInsert());
        cashier.setCategoryUpdate(cashier1.isCategoryUpdate());
        cashier.setCategoryDelete(cashier1.isCategoryDelete());
        cashier.setPOSView(cashier1.isPOSView());
        cashier.setPOSInsert(cashier1.isPOSInsert());
        cashier.setPOSPrint(cashier1.isPOSPrint());
        cashier.setPOSDelete(cashier1.isPOSDelete());
        cashier.setInventoryView(cashier1.isInventoryView());
        cashier.setInventoryInsert(cashier1.isInventoryInsert());
        cashier.setInventoryUpdate(cashier1.isInventoryUpdate());
        cashier.setInventoryDelete(cashier1.isInventoryDelete());
        cashier.setDailyReportView(cashier1.isDailyReportView());
        cashier.setDailyReportExport(cashier1.isDailyReportExport());
        cashier.setSaleReportView(cashier1.isSaleReportView());
        cashier.setSaleReportExport(cashier1.isSaleReportExport());
        cashier.setItemReportView(cashier1.isItemReportView());
        cashier.setItemReportExport(cashier1.isItemReportExport());
        cashier.setVatReportView(cashier1.isVatReportView());
        cashier.setVatReportExport(cashier1.isVatReportExport());
        cashier.setCategoryReportView(cashier1.isCategoryReportView());
        cashier.setCategoryReportExport(cashier1.isCategoryReportExport());
        cashier.setInventoryReportView(cashier1.isInventoryReportView());
        cashier.setInventoryReportExport(cashier1.isInventoryReportExport());




    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.categoryManagement) public void OnClickedCategoryManagement(View view) {

        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER) &&  (!cashier.isCategoryView()) )

          showSnackBar(getView(),"Not Allowed!!",5000);

        else
          Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_categoryFragment);
    }

    @OnClick(R.id.itemManagement) public void OnClickedItemManagement(View view) {
        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER) &&  (!cashier.isItemView()) )
            showSnackBar(getView(),"Not Allowed!!",5000);
        else
        Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_itemFragment);
    }

    @OnClick(R.id.pos) public void OnClickedPos(View view) {
        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER) &&  (!cashier.isPOSView()) )
            showSnackBar(getView(),"Not Allowed!!",5000);
        else
            Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_addNewItem);
    }

    @OnClick(R.id.settings) public void OnClickedSettings(View view) {
//        showSnackBar(getView(), "This feature will added soon", 1000);
        Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_preferencesFragment);
      //  Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_settingsFragment);
    }

    @OnClick(R.id.reports) public void OnClickedReports(View view) {


            Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_reportsFragmentHome);

    }
    @OnClick(R.id.inventory) public void OnClickedInventory(View view) {
        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER) &&  (!cashier.isInventoryView()))
            showSnackBar(getView(),"Not Allowed!!",5000);
        else
            Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_inventoryFragment);

    }
   /* @OnClick(R.id.userManagement) public void OnClickedUserManagement(View view) {
//        showSnackBar(getView(), "This feature will added soon", 1000);
        Bundle bundle = new Bundle();
        bundle.putInt("from_fragment", 2);
        Navigation.findNavController(getView()).navigate(R.id.action_homeFragment2_to_loginFragment4, bundle);
    }*/

}
