package com.abremiratesintl.KOT.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.InventoryMaster;
import com.abremiratesintl.KOT.models.InventoryTransaction;
import com.abremiratesintl.KOT.models.Transaction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryReportAdapter extends RecyclerView.Adapter<InventoryReportAdapter.ViewHolder> {

    List<InventoryMaster> mTransactionList = new ArrayList<>() ;
    private ClickListeners.ItemClickWithView<InventoryMaster> mTransactionItemClick;

    public InventoryReportAdapter(List<InventoryMaster> TransactionList, ClickListeners.ItemClickWithView<InventoryMaster> TransactionItemClick) {
        mTransactionList = TransactionList;
        mTransactionItemClick = TransactionItemClick;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inventory_item,viewGroup,false));
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mTransactionList.get(i));
    }

    @Override public int getItemCount() {
        return mTransactionList.size();
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

        void bind(InventoryMaster transactionMaster) {
            invoiceNo.setText(transactionMaster.getInvoiceNo());
            invoiceDate.setText(transactionMaster.getPurchaseDate());
            invoiceVat.setText(transactionMaster.getVat());
            total.setText(String.valueOf(transactionMaster.getItemTotalAmount()));
         //   totalNoItems.setText(String.valueOf(transactionMaster.getTotalQty()));
            itemView.setOnClickListener(view -> mTransactionItemClick.onClickedItem(itemView,transactionMaster));
        }
    }
}
