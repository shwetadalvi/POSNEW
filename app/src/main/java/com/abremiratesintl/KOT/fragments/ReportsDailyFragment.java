package com.abremiratesintl.KOT.fragments;


import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.Dialog;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.adapters.ReportAdapter;
import com.abremiratesintl.KOT.adapters.ReportDailyAdapter;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.BtDevice;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.DefaultPrinter;
import com.mazenrashed.printooth.data.Printable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import vpos.apipackage.PosApiHelper;

import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ADDRESS;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_DATE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_AMOUNT;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_DESCRIPTION;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_DISCOUNT;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_GROSS_AMOUNT;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_NET_AMOUNT;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_PRICE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_QUANTITY;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_TOTAL;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_VAT;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_NAME;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ORDER_NO;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TAX;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TELE;
import static com.abremiratesintl.KOT.utils.Constants.DEAFULT_PREFS;
import static com.abremiratesintl.KOT.utils.Constants.REQUEST_CODE_ENABLE_BLUETOOTH;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsDailyFragment extends BaseFragment implements ClickListeners.ItemClick<TransactionMaster>, DatePickerDialog.OnDateSetListener {

    @BindView(R.id.reportRecyclerViewDaily)
    RecyclerView reportRecyclerview;
    @BindView(R.id.filter)
    LinearLayout filter;

    @BindView(R.id.toDate)
    EditText toDate;
    @BindView(R.id.emptyReportView)
    ConstraintLayout emptyView;
    private Unbinder mUnbinder;
    private AppDatabase mDatabase;
    View mSelectedDateView;
    private PrefUtils mPrefUtils;
    private List<Items> mItemsList;
    private BluetoothAdapter bluetoothAdapter;
    private int ret;

    public ReportsDailyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports_daily, container, false);
        mDatabase = AppDatabase.getInstance(getContext());
        mUnbinder = ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).changeTitle(" Daily Reports");

        mPrefUtils = new PrefUtils(getContext());
        setHasOptionsMenu(true);
        fetchTransactions();
        return view;
    }

    private void fetchTransactions() {
        LiveData<List<TransactionMaster>> listLiveData = mDatabase.mTransactionMasterDao().findItemsByDate(Constants.getCurrentDate());
        listLiveData.observe(this, this::setUpRecycler);
    }

    private void setUpRecycler(List<TransactionMaster> transactionMasterList){
        if(transactionMasterList.size()==0){
            emptyView.setVisibility(View.VISIBLE);
            reportRecyclerview.setVisibility(View.GONE);
            return;
        }
        ReportDailyAdapter adapter = new ReportDailyAdapter(transactionMasterList, this);
        reportRecyclerview.setAdapter(adapter);
    }


    @OnClick(R.id.toDate) public void onClickedToDate() {
        mSelectedDateView = toDate;
        showDatePicker();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.day_menu, menu);


    }
     public void interacterOne(BtDevice btDevice) {
        Printooth.INSTANCE.setPrinter(btDevice.getDeviceName(), btDevice.getDeviceMac());
        printViaBluetoothPrinter();
    }





    int menuClickCount = 0;

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.filter:
                menuClickCount++;
                if (menuClickCount % 2 == 0) {

                    onClickedFilter(false);
                } else {

                    onClickedFilter(true);

                }

                break;
            /*case R.id.menu_all:
                LiveData<List<TransactionMaster>> listLiveData = mDatabase.mTransactionMasterDao().findItemsByDate(getString(toDate));
                listLiveData.observe(this, this::setUpRecycler);
                break;*/
            case R.id.menu_print:
                printDayReport();
                break;
        }

        return true;
    }

    private void printDayReport() {
        String printerCategory = mPrefUtils.getStringPrefrence(DEAFULT_PREFS, Constants.PRINTER_PREF_KEY, "3");
        switch (printerCategory) {
            case "1":
                printDayReportBluetooth();
                break;
            case "2":
                //printDayReportBuiltin();
                break;
            case "3":
                //showDayReport();
                break;
        }
    }


    private void printDayReportBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothChecking()) {
            printViaBluetoothPrinter();
        }
    }





    private void onClickedFilter(boolean b) {
        if (b) {
            filter.setVisibility(View.VISIBLE);
        } else {

            filter.setVisibility(View.GONE);

        }
    }
    private void onClickedFilter1() {

        if(filter.getVisibility() == View.VISIBLE)
            filter.setVisibility(View.GONE);
         else {

            filter.setVisibility(View.VISIBLE);
            Log.e("Daily Report","Inside3");
        }
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override public void onClickedItem(TransactionMaster item) {

    }


    public void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(), this,calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year + "-" + getProperDate(++month) + "-" + getProperDate(dayOfMonth);
        switch (mSelectedDateView.getId()) {

            case R.id.toDate:
                toDate.setText(date);
                sortedItem( getString(toDate));
                break;
        }
    }

    private void sortedItem( String toDate) {
        if (toDate.equals(getStringfromResource(R.string.present))) {
            toDate = Constants.getCurrentDate();
        }

        LiveData<List<TransactionMaster>> listLiveData = mDatabase.mTransactionMasterDao().findItemsByDate(toDate);
        listLiveData.observe(this, this::setUpRecycler);
    }

    private String getProperDate(int dayOrMonth) {
        if (dayOrMonth < 10) {
            return "0" + dayOrMonth;
        }
        return String.valueOf(dayOrMonth);
    }
    @Override public void onPause() {
        super.onPause();

    }


    private boolean bluetoothChecking() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_CODE_ENABLE_BLUETOOTH);
        } else {
            if (!Printooth.INSTANCE.hasPairedPrinter()) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    Dialog dialog = new Dialog(getContext());
                    dialog.showBluetoothDevices(getPairedDevices());
                    dialog.setInteractorToFragment(ReportsDailyFragment.this::interacterOne);
                    dialog.show();
                });

            }
            return true;
        }
        return false;
    }



    private List<BtDevice> getPairedDevices() {

        // Get a set of currently paired devices
        List<BtDevice> devices = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        Iterator<BluetoothDevice> itr = pairedDevices.iterator();
        while (itr.hasNext()) {
            BtDevice btDevice = new BtDevice();
            BluetoothDevice bluetoothDevice = itr.next();
            btDevice.setDeviceName(bluetoothDevice.getName());
            btDevice.setDeviceMac(bluetoothDevice.getAddress());
            devices.add(btDevice);
        }
        if (devices.size() == 0) {
            BtDevice device = new BtDevice();
            device.setDeviceName("No devices Found\n Please got to bluetooth settings and pair the device");
        }
        return devices;
    }


    private void printViaBluetoothPrinter() {
        if (Printooth.INSTANCE.hasPairedPrinter()) {
            new Thread(() -> {
                ArrayList<Printable> printables = new ArrayList<>();
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText(COMPANY_NAME)
                        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_LARGE())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText(COMPANY_TELE)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText(COMPANY_ADDRESS)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText(COMPANY_TAX)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("TRN   : 1234567890")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());

                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_DATE + Constants.getCurrentDateTime())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());

                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                       /* .setText(COMPANY_ITEM_DESCRIPTION + createSpace(COMPANY_ITEM_DESCRIPTION, COMPANY_ITEM_DESCRIPTION.length(), false) +
                                COMPANY_ITEM_QUANTITY + createSpace(COMPANY_ITEM_QUANTITY, COMPANY_ITEM_QUANTITY.length(), false) +
                                COMPANY_ITEM_PRICE + createSpace(COMPANY_ITEM_PRICE, COMPANY_ITEM_PRICE.length(), false) +
                                COMPANY_ITEM_AMOUNT + createSpace(COMPANY_ITEM_AMOUNT, COMPANY_ITEM_AMOUNT.length(), false))*/
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
               //
                for (Items order :mItemsList ) {
                    String price = decimalAdjust(order.getPrice());
                    String totalprice = decimalAdjust(order.getTotalItemPrice());
                    if (price == null) price = String.valueOf(order.getPrice());
                    if (totalprice == null) totalprice = String.valueOf(order.getTotalItemPrice());
                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                           /* .setText(order.getItemName() + createSpace(COMPANY_ITEM_DESCRIPTION, order.getItemName().length(), false) +
                                    order.getQty() + createSpace(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                    price + createSpace(COMPANY_ITEM_PRICE, String.format("%.2f", order.getPrice()).length(), false) +
                                    totalprice + createSpace(COMPANY_ITEM_AMOUNT, String.format("%.2f", order.getTotalItemPrice()).length(), false))*/
                            .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                            .setNewLinesAfter(2)
                            .build());
                }
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
              /*  printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_TOTAL + createSpace(COMPANY_ITEM_TOTAL.length(), String.format("%.2f", getTotalItemAmount()).length()) + getTotalItemAmount())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());*/
              /*  if (!getString(mFooterDiscount).isEmpty()) {
                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setText(COMPANY_ITEM_DISCOUNT + createSpace(COMPANY_ITEM_DISCOUNT.length(), String.valueOf(getString(mFooterDiscount)).length()) + getString(mFooterDiscount))
                            .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                            .setNewLinesAfter(2)
                            .build());
                }*/
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                  .build());
                    /*  printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_GROSS_AMOUNT + createSpace(COMPANY_ITEM_GROSS_AMOUNT.length(), String.format("%.2f", getPriceExcludingVat()).length()) + getPriceExcludingVat())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());*/

               /* printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_VAT + createSpace(COMPANY_ITEM_VAT.length(), String.valueOf(getString(mFooterVat)).length()) + getString(mFooterVat))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());*/
               /* printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_NET_AMOUNT + createSpace(COMPANY_ITEM_NET_AMOUNT.length(), String.valueOf(getString(mFooterTotal)).length()) + getString(mFooterTotal))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());*/
                Printooth.INSTANCE.printer().print(printables);
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }).start();
        }
    }
   /* private void printDayReportBuiltin() {
        Thread thread;

        thread = new Thread(() -> {
            PosApiHelper posApiHelper = PosApiHelper.getInstance();
            ret = posApiHelper.PrintInit(2, 18, 18, 0);
            ret = posApiHelper.PrintSetFont((byte) 18, (byte) 18, (byte) 0x00);
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr(centerAlignment(32, COMPANY_NAME) + COMPANY_NAME);
            posApiHelper.PrintStr(centerAlignment(32, COMPANY_TELE) + COMPANY_TELE);
            posApiHelper.PrintStr(centerAlignment(32, COMPANY_ADDRESS) + COMPANY_ADDRESS);
            posApiHelper.PrintStr(centerAlignment(32, COMPANY_TAX) + COMPANY_TAX);
            posApiHelper.PrintStr(centerAlignment(32, "TRN  : 1234567890"));
            posApiHelper.PrintStr("................................");
            posApiHelper.PrintStr(COMPANY_ORDER_NO + " " + newInvoiceNo);
            posApiHelper.PrintStr(COMPANY_DATE + Constants.getCurrentDateTime().substring(0, Constants.getCurrentDateTime().length() - 3));

            posApiHelper.PrintStr("................................");
            posApiHelper.PrintStr(COMPANY_ITEM_DESCRIPTION + createSpace(COMPANY_ITEM_DESCRIPTION, COMPANY_ITEM_DESCRIPTION.length(), false) +
                    COMPANY_ITEM_QUANTITY + createSpace(COMPANY_ITEM_QUANTITY, COMPANY_ITEM_QUANTITY.length(), false) +
//                    COMPANY_ITEM_PRICE + createSpace(COMPANY_ITEM_PRICE, COMPANY_ITEM_PRICE.length(), false) +
                    COMPANY_ITEM_AMOUNT + createSpace(COMPANY_ITEM_AMOUNT, COMPANY_ITEM_AMOUNT.length(), false));
            posApiHelper.PrintStr("................................");


            for (Items order : mItemsList) {
                String price = decimalAdjust(order.getPrice());
                String totalprice = decimalAdjust(order.getTotalItemPrice());
                if (price == null) price = String.valueOf(order.getPrice());
                if (totalprice == null) totalprice = String.valueOf(order.getTotalItemPrice());
                posApiHelper.PrintStr(order.getItemName() + createSpace(COMPANY_ITEM_DESCRIPTION, order.getItemName().length(), false) +
                        order.getQty() + createSpace(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
//                        price + createSpace(COMPANY_ITEM_PRICE, String.format("%.2f", order.getPrice()).length(), false) +
                        totalprice + createSpace(COMPANY_ITEM_AMOUNT, String.format("%.2f", order.getTotalItemPrice()).length(), false));
            }
            posApiHelper.PrintStr("................................");
            posApiHelper.PrintStr(COMPANY_ITEM_TOTAL + createSpace(COMPANY_ITEM_TOTAL.length(), String.format("%.2f", getTotalItemAmount()).length()) + getTotalItemAmount());
            if (!getString(mFooterDiscount).isEmpty()) {
                posApiHelper.PrintStr(COMPANY_ITEM_DISCOUNT + createSpace(COMPANY_ITEM_DISCOUNT.length(), String.valueOf(getString(mFooterDiscount)).length()) + getString(mFooterDiscount));
            }
            posApiHelper.PrintStr("................................");
            posApiHelper.PrintStr(COMPANY_ITEM_GROSS_AMOUNT + createSpace(COMPANY_ITEM_GROSS_AMOUNT.length(), String.format("%.2f", getPriceExcludingVat()).length()) + getPriceExcludingVat());
            posApiHelper.PrintStr(COMPANY_ITEM_VAT + createSpace(COMPANY_ITEM_VAT.length(), String.valueOf(getString(mFooterVat)).length()) + getString(mFooterVat));
            posApiHelper.PrintStr("................................");
            posApiHelper.PrintStr(COMPANY_ITEM_NET_AMOUNT + createSpace(COMPANY_ITEM_NET_AMOUNT.length(), String.valueOf(getString(mFooterTotal)).length()) + getString(mFooterTotal));
            posApiHelper.PrintStr("................................");
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr("\n");
            ret = posApiHelper.PrintStart();
        });
        *//*int printStat = posApiHelper.PrintCheckStatus();
        if (ret == -1) {
            RESULT_CODE = -1;
            showSnackBar(getView(), "Error, No Paper ", 1000);
            return;
        } else if (ret == -2) {
            RESULT_CODE = -1;
            showSnackBar(getView(), "Error, Printer Too Hot ", 1000);
            return;
        } else if (ret == -3) {
            RESULT_CODE = -1;
            showSnackBar(getView(), "Battery less :" + (BatteryV * 2), 1000);
            return;
        } else {*//*
        thread.start();
//        }
    }*/


    private String createSpace(int firstLength, int secondLegth) {
        int num = 32 - firstLength;
        num = num - secondLegth;
        return new String(new char[num]).replace('\0', ' ');
    }


    private String decimalAdjust(float value) {
        String stringValue = String.valueOf(value);
        if (stringValue.substring(stringValue.length() - 1).equals("0")) {
            return stringValue + 0;
        }
        return null;
    }


}