package com.abremiratesintl.KOT.adapters;

import android.app.AlertDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners.CategoryItemEvents;
import com.abremiratesintl.KOT.models.Category;
import com.abremiratesintl.KOT.models.Items;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    List<Items> categoryList = new ArrayList<>();
    Context mContext;
    private CategoryItemEvents mCategoryItemEvents;
    private View mView;
    private Items mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    LifecycleOwner mLifecycleOwner;

    public ItemsAdapter(List<Items> categoryList, Context context, CategoryItemEvents categoryItemEvents, LifecycleOwner lifecycleOwner) {
        this.categoryList = categoryList;
        mContext = context;
        mCategoryItemEvents = categoryItemEvents;
        mLifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.item_list, viewGroup, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(categoryList.get(i));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void deleteItem(int position) {

                        mRecentlyDeletedItem = categoryList.get(position);
                        mRecentlyDeletedItemPosition = position;
                        categoryList.remove(position);
                        notifyItemRemoved(position);
                        mCategoryItemEvents.onDeletedItem(mRecentlyDeletedItem);
                        showUndoSnackbar();

    }

    private void showUndoSnackbar() {
//        View view = mActivity.findViewById(R.id.coordinator_layout);
//        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
//                Snackbar.LENGTH_LONG);
//        snackbar.setAction(R.string.snack_bar_undo, v -> undoDelete());
//        snackbar.show();
    }

    private void undoDelete() {
        categoryList.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemCategory;
        TextView itemPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.list_item_name);
            itemCategory = itemView.findViewById(R.id.list_item_category_name);
            itemPrice = itemView.findViewById(R.id.list_item_price);
        }

        public void bind(Items items) {
            LiveData<Category> categoryLiveData = AppDatabase.getInstance(itemView.getContext()).mCategoryDao().findCategoryById(items.getCategoryId());
            categoryLiveData.observe(mLifecycleOwner, category -> {
                itemName.setText(items.getItemName());
                itemCategory.setText(category.getCategoryName());
                itemPrice.setText(mContext.getResources().getString(R.string.currency)+" "+String.format("%.2f", items.getPrice()));

            });
            itemView.setOnClickListener(view -> mCategoryItemEvents.onClickedEdit(items));
        }
    }
}
