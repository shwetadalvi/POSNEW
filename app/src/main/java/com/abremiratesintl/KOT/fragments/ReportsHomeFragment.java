package com.abremiratesintl.KOT.fragments;

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

    public ReportsHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports_home2, container, false);

        ((MainActivity)getActivity()).changeTitle("ABR REPORTS");
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.dayReport) public void OnClickedDayReport(View view) {

        Navigation.findNavController(view).navigate(R.id.action_reportsHomeFragment_to_reportDailyFragment);
    }
    @OnClick(R.id.salesReport) public void OnClickedSalesReport(View view) {

        Navigation.findNavController(view).navigate(R.id.action_reportsHomeFragment_to_reportsFragment);
    }
    @OnClick(R.id.itemReport) public void OnClickedItemwiseReport(View view) {

        Navigation.findNavController(view).navigate(R.id.action_reportsHomeFragment_to_itemwiseReportFragment);
    }
    @OnClick(R.id.vatReport) public void OnClickedVATwiseReport(View view) {

        Navigation.findNavController(view).navigate(R.id.action_reportsHomeFragment_to_VATwiseReportFragment);
    }
    @OnClick(R.id.categoryReport) public void OnClickedCategoryReport(View view) {

        Navigation.findNavController(view).navigate(R.id.action_reportsHomeFragment_to_categoryReport2);
    }
    @OnClick(R.id.inventoryReport) public void OnClickedInventoryReport(View view) {

        Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_inventoryFragment);
    }
}
