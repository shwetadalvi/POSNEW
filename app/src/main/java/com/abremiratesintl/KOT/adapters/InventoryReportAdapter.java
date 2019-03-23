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
import com.abremiratesintl.KOT.models.InventoryTransaction;
import com.abremiratesintl.KOT.models.Transaction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryReportAdapter extends RecyclerView.Adapter<InventoryReportAdapter.ViewHolder> {

    List<InventoryTransaction> mTransactionList = new ArrayList<>() ;
    private ClickListeners.ItemClickWithView<InventoryTransaction> mTransactionItemClick;

    public InventoryReportAdapter(List<InventoryTransaction> TransactionList, ClickListeners.ItemClickWithView<InventoryTransaction> TransactionItemClick) {
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
        @BindView(R.id.textCategory)
        TextView textCategory;
       @BindView(R.id.itemlayout)
       ConstraintLayout itemlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bind(InventoryTransaction Transaction) {
            slNo.setText(String.valueOf(getAdapterPosition() + 1));
            item.setText(String.valueOf(Transaction.getItemName()));
            qty.setText(String.valueOf(Transaction.getQty()));
            date.setText(String.valueOf(Transaction.getInvoiceDate()));
            amount.setText(String.valueOf(Transaction.getPrice()));
            textTotal.setText("Total : "+String.valueOf(Transaction.getGrandTotal()));
            textCategory.setText(Transaction.getCategory());
            itemlayout.setOnClickListener(view -> mTransactionItemClick.onClickedItem(view,Transaction));
        }
    }
}
