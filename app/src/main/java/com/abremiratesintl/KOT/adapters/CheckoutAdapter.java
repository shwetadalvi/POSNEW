package com.abremiratesintl.KOT.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.fragments.CheckoutFragment;
import com.abremiratesintl.KOT.interfaces.ClickListeners.CheckoutCountClickListeners;
import com.abremiratesintl.KOT.interfaces.ClickListeners.MarkItemListener;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.TempItems;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    List<TempItems> mItemsList = new ArrayList<>();
    List<TempItems> selectedItemsList = new ArrayList<>();
    private CheckoutCountClickListeners mCheckoutCountClickListeners;
    private CheckoutFragment mCheckoutFragment;
    private MarkItemListener markItemListener;
    Context mContext;

    public CheckoutAdapter(List<TempItems> itemsList, CheckoutCountClickListeners checkoutCountClickListeners, CheckoutFragment checkoutFragment) {
        mItemsList = itemsList;
        mCheckoutCountClickListeners = checkoutCountClickListeners;
        mCheckoutFragment = checkoutFragment;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chekout_layout_item, viewGroup, false));
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mItemsList.get(i));
    }


    @Override public int getItemCount() {
        return mItemsList.size();
    }

    public void deleteCheck() {
        if (selectedItemsList.size() != 0) {
            alertDialog();
        }
    }

    private void delete() {
        for (TempItems itemsToBeDeleted : selectedItemsList) {
            mItemsList.remove(itemsToBeDeleted);
        }
        mCheckoutFragment.setItemsList(mItemsList);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chekoutItemName)
        TextView checkoutItemName;
        @BindView(R.id.checkoutQty)
        TextView checkoutItemQty;
        @BindView(R.id.chekoutPrice)
        TextView checkoutItemPrice;
        @BindView(R.id.slNo)
        TextView slNo;
        @BindView(R.id.checkout_qty_down)
        ImageView checkoutItemQtyDown;
        @BindView(R.id.checkout_qty_up)
        ImageView checkoutItemQtyUp;
        @BindView(R.id.markToDelete)
        CheckBox checkoutCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(TempItems items) {
            checkoutCheckBox.setChecked(false);
            slNo.setText(String.valueOf(getAdapterPosition() + 1));
            checkoutItemName.setText(items.getItemName());
            checkoutItemQty.setText(String.valueOf(items.getQty()));
            checkoutItemPrice.setText(String.format("%.2f", items.getTotalItemPrice()));
            checkoutItemQtyDown.setOnClickListener(v -> countDecrease());
            checkoutItemQtyUp.setOnClickListener(v -> countIncrease());

//            checkoutItemName.setText(items.getItemName());
        }

        @OnCheckedChanged(R.id.markToDelete)
        public void onItemChecked(CompoundButton button, boolean checked) {
            if (checked) {
                selectedItemsList.add(mItemsList.get(getAdapterPosition()));
            } else {
                if (selectedItemsList.contains(mItemsList.get(getAdapterPosition()))) {
                    selectedItemsList.remove(mItemsList.get(getAdapterPosition()));
                }
            }
        }

        private void countDecrease() {
            mCheckoutCountClickListeners.onClickedMinus(mItemsList.get(getAdapterPosition()));
        }

        private void countIncrease() {
            mCheckoutCountClickListeners.onClickedPlus(mItemsList.get(getAdapterPosition()));
        }
    }

    void alertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setMessage(mContext.getResources().getString(R.string.delete_conform));
        alert.setTitle(mContext.getResources().getString(R.string.alert));
        alert.setPositiveButton("OK", (dialog, which) -> {
            delete();
            dialog.dismiss();
        });
        alert.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alert.show();
    }

}
