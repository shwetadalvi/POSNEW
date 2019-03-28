package com.abremiratesintl.KOT.fragments;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.models.Cashier;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsHomeFragment extends BaseFragment  {
    private Unbinder mUnbinder;
    @BindView(R.id.dayReport)
    RelativeLayout mdayReport;
private Cashier cashier;
private AppDatabase mDatabase;
private PrefUtils mPrefUtils;
    public ReportsHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports_home2, container, false);
mDatabase = AppDatabase.getInstance(getContext());
        mPrefUtils = new PrefUtils(getContext());
        ((MainActivity)getActivity()).changeTitle("ABR REPORTS");
        mUnbinder = ButterKnife.bind(this, view);

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
    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.dayReport) public void OnClickedDayReport(View view) {
        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER) &&  (!cashier.isDailyReportView()) )
            showSnackBar(getView(),"Not Allowed!!",5000);
        else
        Navigation.findNavController(view).navigate(R.id.action_reportsHomeFragment_to_reportDailyFragment);
    }
    @OnClick(R.id.salesReport) public void OnClickedSalesReport(View view) {
        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER) &&  (!cashier.isSaleReportView()) )
            showSnackBar(getView(),"Not Allowed!!",5000);
        else
        Navigation.findNavController(view).navigate(R.id.action_reportsHomeFragment_to_reportsFragment);
    }
    @OnClick(R.id.itemReport) public void OnClickedItemwiseReport(View view) {
        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER) &&  (!cashier.isItemReportView()) )
            showSnackBar(getView(),"Not Allowed!!",5000);
        else
        Navigation.findNavController(view).navigate(R.id.action_reportsHomeFragment_to_itemwiseReportFragment);
    }
    @OnClick(R.id.vatReport) public void OnClickedVATwiseReport(View view) {
        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER) &&  (!cashier.isVatReportView()) )
            showSnackBar(getView(),"Not Allowed!!",5000);
        else
        Navigation.findNavController(view).navigate(R.id.action_reportsHomeFragment_to_VATwiseReportFragment);
    }
    @OnClick(R.id.categoryReport) public void OnClickedCategoryReport(View view) {
        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER) &&  (!cashier.isCategoryReportView()))
            showSnackBar(getView(),"Not Allowed!!",5000);
        else
        Navigation.findNavController(view).navigate(R.id.action_reportsHomeFragment_to_categoryReport2);
    }
    @OnClick(R.id.inventoryReport) public void OnClickedInventoryReport(View view) {
        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER) &&  (!cashier.isInventoryReportView()) )
            showSnackBar(getView(),"Not Allowed!!",5000);
        else
        Navigation.findNavController(view).navigate(R.id.action_reportsHomeFragment_to_inventoryReportFragment);
    }
}
