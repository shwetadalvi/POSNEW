package com.abremiratesintl.KOT.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.abremiratesintl.KOT.utils.Constants.DEAFULT_PREFS;

public class VATwiseReportAdapter extends RecyclerView.Adapter<VATwiseReportAdapter.ViewHolder> {

    List<TransactionMaster> mTransactionMasterList = new ArrayList<>();
    private ClickListeners.ItemClick<TransactionMaster> mTransactionMasterItemClick;
    Context context;
    private PrefUtils mPrefUtils;
    public VATwiseReportAdapter(Context context,List<TransactionMaster> transactionMasterList, ClickListeners.ItemClick<TransactionMaster> transactionMasterItemClick) {
        this.context = context;
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
            float vat_amt1 = 0;
            mPrefUtils = new PrefUtils(context);
            slNo.setText(String.valueOf(getAdapterPosition() + 1));
            if(transactionMaster.getName()==null)
                inv_no.setText(transactionMaster.getInvoiceNo());
            else
                inv_no.setText(transactionMaster.getInvoiceNo()+" (Ref "+transactionMaster.getName()+")");

            date.setText(transactionMaster.getInvoiceDate());
           /* if(transactionMaster.getDiscountAmount() > 0)
                vat_amt1 = transactionMaster.getItemTotalAmount() - transactionMaster.getDiscountAmount();
            else*/
                vat_amt1 = transactionMaster.getItemTotalAmount();
            String str_vat = mPrefUtils.getStringPrefrence(DEAFULT_PREFS, Constants.VAT_EXCLUSIVE, context.getResources().getString(R.string.vat_exclusive));

              if (str_vat.equals(context.getResources().getString(R.string.vat_inclusive))) {
                  float vatable_amt1 = transactionMaster.getItemTotalAmount() - transactionMaster.getVatAmount();
                  vatable_amt.setText(String.valueOf(vatable_amt1));
              }else
                  vatable_amt.setText(String.valueOf(transactionMaster.getItemTotalAmount()));
            vat_amt.setText(String.valueOf(transactionMaster.getVatAmount()));
            net_amt.setText(String.valueOf(transactionMaster.getGrandTotal()));
            //itemView.setOnClickListener(view -> mTransactionMasterItemClick.onClickedItem(transactionMaster));
        }
    }
}
