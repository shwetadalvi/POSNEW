package com.abremiratesintl.KOT.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abremiratesintl.KOT.GlideApp;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.Items;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class POSRecyclerAdapter extends RecyclerView.Adapter<POSRecyclerAdapter.ViewHolder> {

    List<Items> mItemsList = new ArrayList<>();
    private ClickListeners.ItemClick mItemClickListener;
    Context mContext;

    public POSRecyclerAdapter(List<Items> itemsList, ClickListeners.ItemClick itemClickListener) {
        mItemsList = itemsList;
        mItemClickListener = itemClickListener;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.grid_layout_item, viewGroup, false));
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mItemsList.get(i));
    }

    @Override public int getItemCount() {
        return mItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView posItemName, posItemPrice;
        ImageView iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posItemName = itemView.findViewById(R.id.pos_item_name);
            posItemPrice = itemView.findViewById(R.id.pos_item_price);
            iv = itemView.findViewById(R.id.thumb);
        }

        public void bind(Items items) {
            Uri mSelectedImageUri = null;
            posItemName.setText(items.getItemName());
            posItemPrice.setText(String.valueOf(items.getPrice()));
            String mPath = items.getImagePath();
            if (mPath != null) {
                File f = new File(mPath);
                 mSelectedImageUri = Uri.fromFile(f);
            }
            GlideApp.with(itemView.getContext())
                    .load(mSelectedImageUri)
                    .override(600,600)
                    .placeholder(R.drawable.thumb)
                    .into(iv);

            itemView.setOnClickListener(view -> mItemClickListener.onClickedItem(items));
        }
    }
}