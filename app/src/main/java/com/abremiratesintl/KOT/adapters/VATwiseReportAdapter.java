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
        mTransactionMasterList = transactionMasterList;
        mTransactionMasterItemClick = transactionMasterItemClick;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.report_item,viewGroup,false));
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mTransactionMasterList.get(i));
    }

    @Override public int getItemCount() {
        return mTransactionMasterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.invoiceNo)
        TextView invoiceNo;
        @BindView(R.id.invoiceDate)
        TextView invoiceDate;
        @BindView(R.id.report_vat)
        TextView invoiceVat;
        @BindView(R.id.total)
        TextView total;
        @BindView(R.id.total_no_items)
        TextView totalNoItems;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bind(TransactionMaster transactionMaster) {
            invoiceNo.setText(transactionMaster.getInvoiceNo());
            invoiceDate.setText(transactionMaster.getInvoiceDate());
            invoiceVat.setText(String.valueOf(transactionMaster.getVatAmount()));
            total.setText(String.valueOf(transactionMaster.getGrandTotal()));
            totalNoItems.setText(String.valueOf(transactionMaster.getTotalQty()));
            itemView.setOnClickListener(view -> mTransactionMasterItemClick.onClickedItem(transactionMaster));
        }
    }
}
