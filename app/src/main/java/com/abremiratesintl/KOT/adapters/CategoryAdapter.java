package com.abremiratesintl.KOT.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.interfaces.ClickListeners.CategoryItemEvents;
import com.abremiratesintl.KOT.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    List<Category> categoryList = new ArrayList<>();
    Context mContext;
    private CategoryItemEvents mCategoryItemEvents;
    private View mView;
    private Category mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;

    public CategoryAdapter(List<Category> categoryList, Context context, CategoryItemEvents categoryItemEvents) {
        this.categoryList = categoryList;
        mContext = context;
        mCategoryItemEvents = categoryItemEvents;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.category_item, viewGroup, false);
        return new ViewHolder(mView);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(categoryList.get(i));
    }

    @Override public int getItemCount() {
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
        TextView categoryItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryItem = itemView.findViewById(R.id.categoryListItem);
        }

        public void bind(Category category) {
            categoryItem.setText(category.getCategoryName());
            itemView.setOnClickListener(view -> mCategoryItemEvents.onClickedEdit(category));
        }
    }
}
