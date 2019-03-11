package com.abremiratesintl.KOT;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.abremiratesintl.KOT.adapters.BluetoothDeviceAdapter;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.BtDevice;

import java.util.List;
import java.util.Objects;

public class Dialog extends AlertDialog implements View.OnClickListener, ClickListeners.BtResponseListener {

    private ClickListeners.BtResponseListener mInteractorToFragment;

    public Dialog(@NonNull Context context) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
    }

    public void showBluetoothDevices(List<BtDevice> availableDevices) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.active_bluetooth_devices, null);
        RecyclerView recyclerView = view.findViewById(R.id.active_blutooth_devices_recycler);
        recyclerView.setAdapter(new BluetoothDeviceAdapter(availableDevices,this));
        TextView textView = view.findViewById(R.id.heading);
        textView.setText(getContext().getResources().getString(R.string.select_bluetooth_device));
        this.setView(view);

    }

    public void setInteractorToFragment(ClickListeners.BtResponseListener interactorToFragment) {
        mInteractorToFragment = interactorToFragment;
    }

    @Override public void onClick(View v) {

    }

    @Override public void interacterOne(BtDevice btDevice) {
        mInteractorToFragment.interacterOne(btDevice);
        dismiss();
    }
}