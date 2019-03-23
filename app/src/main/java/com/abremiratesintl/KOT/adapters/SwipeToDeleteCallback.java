package com.abremiratesintl.KOT.adapters;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.models.Items;

import java.util.ArrayList;
import java.util.List;

public class SwipeToDeleteCallback<T> extends ItemTouchHelper.SimpleCallback {
    private AppDatabase mDatabase;
    private final ColorDrawable background;
    CategoryAdapter mCategoryAdapter;
    ItemsAdapter mItemsAdapter;
    private Drawable icon;
    Context mContext;
    List<Items> mItemList = new ArrayList<>();
    public SwipeToDeleteCallback(Context context, T adapter) {
        super(0, ItemTouchHelper.LEFT);

        this.mContext = context;
        mDatabase = AppDatabase.getInstance(mContext);
        if (adapter instanceof CategoryAdapter) {
            mCategoryAdapter = (CategoryAdapter) adapter;

        }else {
            mItemsAdapter = (ItemsAdapter) adapter;
        }
        icon = ContextCompat.getDrawable(context,
                R.drawable.ic_delete);
        background = new ColorDrawable(Color.RED);
    }

    @Override public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();
     /*   if (mCategoryAdapter instanceof CategoryAdapter) {

            int catId = mCategoryAdapter.getCategoryId(position);

            LiveData<List<Items>> listLiveData = mDatabase.mItemsDao().findItemsByCategoryId(catId);
            listLiveData.observe(viewHolder.getC, items -> {
                mItemList = items;

            });
            if (mItemList != null || mItemList.size() > 0) {

            }
        }*/

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Are you sure want to Delete ?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

        if (mCategoryAdapter instanceof CategoryAdapter) {
            mCategoryAdapter.deleteItem(position);
        }else {
            mItemsAdapter.deleteItem(position);
        }
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        if (mCategoryAdapter instanceof CategoryAdapter) {
                            mCategoryAdapter.notifyDataSetChanged();
                        }else {
                            mItemsAdapter.notifyDataSetChanged();
                        }

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX,
                dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        if (dX > 0) { // Swiping to the right
            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());

        } else if (dX < 0) { // Swiping to the left
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }
        background.draw(c);

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            int iconRight = itemView.getLeft() + iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }
}
