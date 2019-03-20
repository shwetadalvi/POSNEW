package com.abremiratesintl.KOT.fragments.super_user;


import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.models.Category;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.Transaction;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class VatSettingsFragment extends Fragment {
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    private Unbinder mUnbinder;
    private AppDatabase mDatabase;
    private PrefUtils mPrefUtils;

    public VatSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vat_setting, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mDatabase = AppDatabase.getInstance(getContext());
        mPrefUtils = new PrefUtils(getActivity());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb=(RadioButton)group.findViewById(checkedId);

                if(rb.getText().toString().equals(getActivity().getResources().getString(R.string.vat_exclusive)))
                mPrefUtils.putBooleanPreference(Constants.DEAFULT_PREFS, Constants.VAT_EXCLUSIVE, true);
                else
                    mPrefUtils.putBooleanPreference(Constants.DEAFULT_PREFS, Constants.VAT_EXCLUSIVE, false);
                Toast.makeText(getActivity(), rb.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
