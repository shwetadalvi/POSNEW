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

public class ItemwiseReportAdapter extends RecyclerView.Adapter<ItemwiseReportAdapter.ViewHolder> {

    List<TransactionMaster> mTransactionMasterList = new ArrayList<>();
    private ClickListeners.ItemClick<TransactionMaster> mTransactionMasterItemClick;

    public ItemwiseReportAdapter(List<TransactionMaster> transactionMasterList, ClickListeners.ItemClick<TransactionMaster> transactionMasterItemClick) {
        mTransactionMasterList = transactionMasterList;
        mTransactionMasterItemClick = transactionMasterItemClick;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemwise_report_data,viewGroup,false));
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
        @BindView(R.id.item)
        TextView item;
        @BindView(R.id.qty)
        TextView qty;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.amount)
        TextView amount;
        @BindView(R.id.textTotal)
        TextView textTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bind(TransactionMaster transactionMaster) {
            slNo.setText(getAdapterPosition() + 1);
            item.setText(transactionMaster.getInvoiceNo());
            qty.setText(transactionMaster.getTotalQty());
            date.setText(String.valueOf(transactionMaster.getInvoiceDate()));
            amount.setText(String.valueOf(transactionMaster.getGrandTotal()));
            textTotal.setText(String.valueOf(transactionMaster.getItemTotalAmount()));
            //itemView.setOnClickListener(view -> mTransactionMasterItemClick.onClickedItem(transactionMaster));
        }
    }
}
