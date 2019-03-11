package com.abremiratesintl.KOT.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.fragments.super_user.CreateAdminFragment;
import com.abremiratesintl.KOT.fragments.super_user.SuperAdminCompanyDetails;

public class SuperAdminPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SuperAdminPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new SuperAdminCompanyDetails();
            case 1:
                return new CreateAdminFragment();
        }
        return null;
    }

    @Nullable @Override public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.company_details);
            case 1:
                return mContext.getResources().getString(R.string.new_user);
        }
        return null;
    }

    @Override public int getCount() {
        return 2;
    }
}
