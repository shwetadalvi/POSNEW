package com.abremiratesintl.KOT.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.Transaction;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.abremiratesintl.KOT.utils.Constants.DEAFULT_PREFS;

public class ItemwiseReportAdapter extends RecyclerView.Adapter<ItemwiseReportAdapter.ViewHolder> {

    List<Transaction> mTransactionList = new ArrayList<>() ;
    private ClickListeners.ItemClick<Transaction> mTransactionItemClick;
    PrefUtils mPrefUtils ;
    Context context;
    public ItemwiseReportAdapter(List<Transaction> TransactionList, ClickListeners.ItemClick<Transaction> TransactionItemClick, Context context) {
        mTransactionList = TransactionList;
        mTransactionItemClick = TransactionItemClick;
        this.context = context;
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
        @BindView(R.id.textCategory)
        TextView textCategory;
        @BindView(R.id.discount)
        TextView discount;
        @BindView(R.id.total)
        TextView total;

       // @BindView(R.id.itemlayout)
       // RelativeLayout itemlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bind(Transaction Transaction) {
            mPrefUtils = new PrefUtils(context);
            String str_vat = mPrefUtils.getStringPrefrence(DEAFULT_PREFS, Constants.VAT_EXCLUSIVE, context.getResources().getString(R.string.vat_exclusive));

          /*  if (str_vat.equals(context.getResources().getString(R.string.vat_inclusive))) {
                vat.setVisibility(View.VISIBLE);

            }*/

            slNo.setText(String.valueOf(getAdapterPosition() + 1));
            item.setText(String.valueOf(Transaction.getItemName()));
            qty.setText(String.valueOf(Transaction.getQty()));
            date.setText(String.valueOf(Transaction.getInvoiceDate()));
            float net = Transaction.getGrandTotal() - Transaction.getDiscount();
            float vat1 = 0;
            if (str_vat.equals(context.getResources().getString(R.string.vat_inclusive))) {
                vat1 = net * Transaction.getVat()/(100+Transaction.getVat());
             //   vat.setText(String.valueOf(vat1));
                net = net - vat1;
            }

            amount.setText(String.format("%.2f",net));
            total.setText(String.valueOf(Transaction.getGrandTotal()));
            textCategory.setText(Transaction.getCategory());
            discount.setText(String.valueOf(Transaction.getDiscount()));
            //itemlayout.setOnClickListener(view -> mTransactionItemClick.onClickedItem(Transaction));
        }
    }
}
