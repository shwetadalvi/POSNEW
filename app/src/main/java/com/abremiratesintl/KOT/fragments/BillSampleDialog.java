package com.abremiratesintl.KOT.fragments;


import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.adapters.BillAdapter;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.Transaction;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class BillSampleDialog extends DialogFragment {


    private static List<Items> list;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.inv_no)
    TextView invNom;

    @BindView(R.id.inv_date)
    TextView invDate;

    @BindView(R.id.total)
    TextView total;

    @BindView(R.id.vat)
    TextView vat;

    @BindView(R.id.discount)
    TextView disc;

    @BindView(R.id.netAmount)
    TextView net;

    private static String invNo;
    private static List<Transaction> itemList;
    private Unbinder mUnbinder;

    public BillSampleDialog() {
        // Required empty public constructor
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);

    }

    public static BillSampleDialog newInstance(String invNo, List<Items> list) {
        BillSampleDialog.list = list;
        Bundle args = new Bundle();
        args.putString("invNo", invNo);
        BillSampleDialog fragment = new BillSampleDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bill_sample_dialog, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        invNom.setText(invNo);
        invDate.setText(Constants.getCurrentDateTime());

        TextView toolbar = view.findViewById(R.id.toolbar_title);
        toolbar.setText("Receipt");
        String invNo = getArguments().getString("invNo");
        LiveData<TransactionMaster> liveData = AppDatabase.getInstance(getContext()).mTransactionMasterDao().findByInvNo(invNo);
        liveData.observe(this, transactionMaster -> {
            int tranMasterId = transactionMaster.getTransMasterId();
            BillAdapter adapter = new BillAdapter(list, this);
            mRecyclerView.setAdapter(adapter);
            vat.setText(String.format("%.2f", transactionMaster.getVatAmount()));
            float tot = transactionMaster.getGrandTotal() - transactionMaster.getVatAmount() - transactionMaster.getDiscountAmount();
            total.setText(String.format("%.2f", tot));
            net.setText(String.format("%.2f", transactionMaster.getGrandTotal()));
            if (transactionMaster.getDiscountAmount() != 0) {
                net.setText(String.format("%.2f", transactionMaster.getDiscountAmount()));
            }
        });
        return view;
    }

    @Override public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
