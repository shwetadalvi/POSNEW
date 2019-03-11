package com.abremiratesintl.KOT.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.interfaces.ClickListeners.BtResponseListener;
import com.abremiratesintl.KOT.models.BtDevice;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {
    private List<BtDevice> mBtDeviceList = new ArrayList<>();
    private BtResponseListener mBtResponseListener;

    public BluetoothDeviceAdapter(List<BtDevice> btDeviceList, BtResponseListener btResponseListener) {
        mBtDeviceList = btDeviceList;
        mBtResponseListener = btResponseListener;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater lLayoutInflater = LayoutInflater.from(viewGroup.getContext());

        return new ViewHolder(lLayoutInflater.inflate(R.layout.active_bluetooth_device, viewGroup, false));
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        BtDevice deviceName = (BtDevice) mBtDeviceList.get(i);
        viewHolder.bind(deviceName);
    }

    @Override public int getItemCount() {
        return mBtDeviceList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;

        ViewHolder(View view) {
            super(view);
            deviceName = view.findViewById(R.id.device_name);
        }

        void bind(BtDevice obj) {
//            binding.setVariable(com.abremitatesintl.tomandjerry.BR.order, obj);
//            binding.setVariable(, obj);
            deviceName.setText(obj.getDeviceName());
//            binding.setVariable(items, obj);
            itemView.setOnClickListener(v -> mBtResponseListener.interacterOne(obj));
        }
    }
}
