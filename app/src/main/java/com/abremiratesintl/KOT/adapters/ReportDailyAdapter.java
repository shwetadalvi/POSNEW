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

public class ReportDailyAdapter extends RecyclerView.Adapter<ReportDailyAdapter.ViewHolder> {

    List<TransactionMaster> mTransactionMasterList = new ArrayList<>();
    private ClickListeners.ItemClick<TransactionMaster> mTransactionMasterItemClick;

    public ReportDailyAdapter(List<TransactionMaster> transactionMasterList, ClickListeners.ItemClick<TransactionMaster> transactionMasterItemClick) {
        mTransactionMasterList = transactionMasterList;
        mTransactionMasterItemClick = transactionMasterItemClick;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.daily_report_data,viewGroup,false));
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
        @BindView(R.id.invNo)
        TextView invoiceNo;
        @BindView(R.id.totalItem)
        TextView totalItem;
        @BindView(R.id.payment)
        TextView payment;
        @BindView(R.id.total)
        TextView total;
        @BindView(R.id.cash)
        TextView cash;
        @BindView(R.id.card)
        TextView card;
        @BindView(R.id.net)
        TextView net;
        @BindView(R.id.discount)
        TextView discount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bind(TransactionMaster transactionMaster) {
            slNo.setText(String.valueOf(getAdapterPosition() + 1));
            if(transactionMaster.getName() == null)
            invoiceNo.setText(transactionMaster.getInvoiceNo());
            else
                invoiceNo.setText(transactionMaster.getInvoiceNo()+" (Ref "+transactionMaster.getName()+")");
            payment.setText(transactionMaster.getType());
            totalItem.setText(String.valueOf(transactionMaster.getTotalQty()));
            total.setText(String.valueOf(transactionMaster.getItemTotalAmount()));
            discount.setText(String.valueOf(transactionMaster.getDiscountAmount()));
            net.setText(String.valueOf(transactionMaster.getGrandTotal()));
            card.setText(String.valueOf(transactionMaster.getCard()));
            cash.setText(String.valueOf(transactionMaster.getCash()));
            itemView.setOnClickListener(view -> mTransactionMasterItemClick.onClickedItem(transactionMaster));
        }
    }
}
