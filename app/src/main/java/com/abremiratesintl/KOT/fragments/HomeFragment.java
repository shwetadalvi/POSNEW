package com.abremiratesintl.KOT.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.Navigation;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;

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
//    @BindView(R.id.userManagement)
//    CardView mUserManagement;
    private Unbinder mUnbinder;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).changeTitle("POS");

        return view;
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.categoryManagement) public void OnClickedCategoryManagement(View view) {
        Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_categoryFragment);
    }

    @OnClick(R.id.itemManagement) public void OnClickedItemManagement(View view) {
        Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_itemFragment);
    }

    @OnClick(R.id.pos) public void OnClickedPos(View view) {
        Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_addNewItem);
    }

    @OnClick(R.id.settings) public void OnClickedSettings(View view) {
//        showSnackBar(getView(), "This feature will added soon", 1000);
        Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_preferencesFragment);
    }

    @OnClick(R.id.reports) public void OnClickedReports(View view) {
        Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_reportsFragmentHome);

    }
    @OnClick(R.id.inventory) public void OnClickedInventory(View view) {
        Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_inventoryFragment);

    }
   /* @OnClick(R.id.userManagement) public void OnClickedUserManagement(View view) {
//        showSnackBar(getView(), "This feature will added soon", 1000);
        Bundle bundle = new Bundle();
        bundle.putInt("from_fragment", 2);
        Navigation.findNavController(getView()).navigate(R.id.action_homeFragment2_to_loginFragment4, bundle);
    }*/

}
