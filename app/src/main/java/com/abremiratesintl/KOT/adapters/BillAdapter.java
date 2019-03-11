package com.abremiratesintl.KOT.adapters;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.TransactionMaster;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {

    List<Items> mTransactionMasterList = new ArrayList<>();
    private List<Items> mItems;
    private LifecycleOwner mLifecycleOwner;
    private ClickListeners.ItemClick<TransactionMaster> mTransactionMasterItemClick;

    public BillAdapter(List<Items> transactionMasterList,LifecycleOwner lifecycleOwner) {
        mTransactionMasterList = transactionMasterList;
        mLifecycleOwner = lifecycleOwner;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bill_item, viewGroup, false));
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mTransactionMasterList.get(i));
    }

    @Override public int getItemCount() {
        return mTransactionMasterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.t1)
        TextView item;
        @BindView(R.id.t2)
        TextView qty;
        @BindView(R.id.t3)
        TextView price;
        @BindView(R.id.t4)
        TextView total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Items transaction) {
                item.setText(String.valueOf(transaction.getItemName()));
                qty.setText(String.valueOf(transaction.getQty()));
                price.setText(String.valueOf(transaction.getPrice()));
                total.setText(String.valueOf(transaction.getTotalItemPrice()));
        }
    }
}
