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

public class CategoryReportAdapter extends RecyclerView.Adapter<CategoryReportAdapter.ViewHolder> {

    List<Transaction> mTransactionList = new ArrayList<>() ;
    private ClickListeners.ItemClick<Transaction> mTransactionItemClick;

    public CategoryReportAdapter(List<Transaction> TransactionList, ClickListeners.ItemClick<Transaction> TransactionItemClick) {
        mTransactionList = TransactionList;
        mTransactionItemClick = TransactionItemClick;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_report_data,viewGroup,false));
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
        @BindView(R.id.invNo)
        TextView invNo;
        @BindView(R.id.totalItem)
        TextView totalItem;
        @BindView(R.id.item_name)
        TextView item_name;
        @BindView(R.id.total)
        TextView total;
       // @BindView(R.id.itemlayout)
       // RelativeLayout itemlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bind(Transaction Transaction) {
            slNo.setText(String.valueOf(getAdapterPosition() + 1));
            /*if(Transaction.getName() == null)
                invNo.setText(String.valueOf(Transaction.getTransMasterId()));
            else
                invNo.setText(String.valueOf(Transaction.getTransMasterId())+" (Ref "+String.valueOf(Transaction.getTransMasterId())+")");*/

             invNo.setText(String.valueOf(Transaction.getTransMasterId()));
            totalItem.setText(String.valueOf(Transaction.getQty()));
            item_name.setText(Transaction.getItemName());
            total.setText(String.valueOf(Transaction.getPrice()));
           // textTotal.setText("Total : "+String.valueOf(Transaction.getGrandTotal()));
          //  textCategory.setText(Transaction.getCategory());
            //itemlayout.setOnClickListener(view -> mTransactionItemClick.onClickedItem(Transaction));
        }
    }
}
