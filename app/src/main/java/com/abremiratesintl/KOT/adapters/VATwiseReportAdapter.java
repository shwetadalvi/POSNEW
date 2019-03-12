package com.abremiratesintl.KOT.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.TransactionMaster;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VATwiseReportAdapter extends RecyclerView.Adapter<VATwiseReportAdapter.ViewHolder> {

    List<TransactionMaster> mTransactionMasterList = new ArrayList<>();
    private ClickListeners.ItemClick<TransactionMaster> mTransactionMasterItemClick;

    public VATwiseReportAdapter(List<TransactionMaster> transactionMasterList, ClickListeners.ItemClick<TransactionMaster> transactionMasterItemClick) {
        mTransactionMasterList = transactionMasterList ;
        mTransactionMasterItemClick = transactionMasterItemClick;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vatwise_report_data,viewGroup,false));
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mTransactionMasterList.get(i));
    }

    @Override public int getItemCount() {
        return mTransactionMasterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.slNo)
        TextView slNo;
        @BindView(R.id.inv_no)
        TextView inv_no;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.vatable_amt)
        TextView vatable_amt;
        @BindView(R.id.vat_amt)
        TextView vat_amt;
        @BindView(R.id.net_amt)
        TextView net_amt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bind(TransactionMaster transactionMaster) {
            slNo.setText("1");
            inv_no.setText(transactionMaster.getInvoiceNo());
            date.setText(transactionMaster.getInvoiceDate());
            vatable_amt.setText(String.valueOf(transactionMaster.getItemTotalAmount()));
            vat_amt.setText(String.valueOf(transactionMaster.getVatAmount()));
            net_amt.setText(String.valueOf(transactionMaster.getGrandTotal()));
            //itemView.setOnClickListener(view -> mTransactionMasterItemClick.onClickedItem(transactionMaster));
        }
    }
}
