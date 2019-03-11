package com.abremiratesintl.KOT.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.Transaction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemwiseReportAdapter extends RecyclerView.Adapter<ItemwiseReportAdapter.ViewHolder> {

    List<Transaction> mTransactionList = new ArrayList<>();
    private ClickListeners.ItemClick<Transaction> mTransactionItemClick;

    public ItemwiseReportAdapter(List<Transaction> TransactionList, ClickListeners.ItemClick<Transaction> TransactionItemClick) {
        mTransactionList = TransactionList;
        mTransactionItemClick = TransactionItemClick;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemwise_report_data,viewGroup,false));
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
        @BindView(R.id.itemlayout)
        TextView itemlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bind(Transaction Transaction) {
            slNo.setText(getAdapterPosition() + 1);
            item.setText(Transaction.getTransactionId());
            qty.setText(Transaction.getQty());
            date.setText(String.valueOf(Transaction.getCreatedDate()));
            amount.setText(String.valueOf(Transaction.getPrice()));
            textTotal.setText(String.valueOf(Transaction.getGrandTotal()));
            itemlayout.setOnClickListener(view -> mTransactionItemClick.onClickedItem(Transaction));
        }
    }
}
