package com.abremiratesintl.KOT.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.models.Category;
import com.abremiratesintl.KOT.models.Items;

import java.util.ArrayList;
import java.util.List;

public class ItemSpinnerAdapter<T> extends ArrayAdapter<T> {

    private final Context context;
    private List<T> spinnerItemList = new ArrayList<>();

    public ItemSpinnerAdapter(Context context, int textViewResourceId, List<T> spinnerItemList) {
        super(context, textViewResourceId);
        this.context = context;
        int textViewResourceId1 = textViewResourceId;
        this.spinnerItemList = spinnerItemList;
    }

    @Override
    public int getCount() {
        return spinnerItemList.size();
    }

    @Override
    public T getItem(int position) {
        return spinnerItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.category_item, parent, false);
            mViewHolder.item = convertView.findViewById(R.id.categoryListItem);
            convertView.findViewById(R.id.editCategory).setVisibility(View.GONE);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (spinnerItemList.get(position) instanceof Items) {
            mViewHolder.item.setText(((Items) spinnerItemList.get(position)).getItemName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private static class ViewHolder {
        TextView item;
    }

}

