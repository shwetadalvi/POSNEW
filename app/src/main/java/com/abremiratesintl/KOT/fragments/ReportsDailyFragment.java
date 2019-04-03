package com.abremiratesintl.KOT.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.Dialog;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.adapters.ReportAdapter;
import com.abremiratesintl.KOT.adapters.ReportDailyAdapter;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.BtDevice;
import com.abremiratesintl.KOT.models.Cashier;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.Transaction;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.DefaultPrinter;
import com.mazenrashed.printooth.data.Printable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import vpos.apipackage.PosApiHelper;

import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ADDRESS;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_DATE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_Email;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_INV_NO;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_AMOUNT;

import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_DESCRIPTION;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_PAYMENT;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_PRICE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_QUANTITY;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_TOTAL;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_VAT;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_NAME;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ORDER_NO;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TAX;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TELE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TOTAL_ITEM;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TRN;
import static com.abremiratesintl.KOT.utils.Constants.DEAFULT_PREFS;
import static com.abremiratesintl.KOT.utils.Constants.REQUEST_CODE_ENABLE_BLUETOOTH;
import static com.abremiratesintl.KOT.utils.Constants.Sl_NO;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsDailyFragment extends BaseFragment implements ClickListeners.ItemClick<TransactionMaster>, DatePickerDialog.OnDateSetListener {

    @BindView(R.id.reportRecyclerViewDaily)
    RecyclerView reportRecyclerview;
    @BindView(R.id.filter)
    LinearLayout filter;
    @BindView(R.id.layout)
    LinearLayout layout;
    @BindView(R.id.toDate)
    EditText toDate;
    @BindView(R.id.emptyReportView)
    ConstraintLayout emptyView;
    @BindView(R.id.textCash)
    TextView textCash;
    @BindView(R.id.textCard)
    TextView textCard;
    @BindView(R.id.textVATAmount)
    TextView textVATAmount;
    @BindView(R.id.textTotalAmount)
    TextView textTotalAmount;
    @BindView(R.id.footer)
    LinearLayout footer;

    private Unbinder mUnbinder;
    private AppDatabase mDatabase;
    View mSelectedDateView;
    private PrefUtils mPrefUtils;
    private List<Items> mItemsList;
    private BluetoothAdapter bluetoothAdapter;
    private int ret;
    private Cashier cashier = new Cashier();
    private boolean isCashier = false;
    String mSelectedDate = Constants.getCurrentDate();
private List<TransactionMaster> mTransactionMasterList;
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

        Thread t = new Thread(() -> {
            cashier = mDatabase.mCashierDao().getCashier();
        });
        t.start();


        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER))
            isCashier = true;
        return view;
    }

    private void fetchTransactions() {
        LiveData<List<TransactionMaster>> listLiveData = mDatabase.mTransactionMasterDao().findItemsByDate(Constants.getCurrentDate());
        listLiveData.observe(this, this::setUpRecycler);
    }

    private void setUpRecycler(List<TransactionMaster> transactionMasterList){
        mTransactionMasterList = transactionMasterList;
        if(transactionMasterList.size()==0){
           emptyView.setVisibility(View.VISIBLE);
            reportRecyclerview.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);

            footer.setVisibility(View.GONE);

            return;
        }
        emptyView.setVisibility(View.GONE);
        footer.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        reportRecyclerview.setVisibility(View.VISIBLE);
        // layout.setVisibility(View.GONE);

        textCash.setVisibility(View.VISIBLE);
        textCard.setVisibility(View.VISIBLE);
        textVATAmount.setVisibility(View.VISIBLE);
        textTotalAmount.setVisibility(View.VISIBLE);
        ReportDailyAdapter adapter = new ReportDailyAdapter(transactionMasterList, this);
        reportRecyclerview.setAdapter(adapter);

        setFooterValues(transactionMasterList);
    }

    private void setFooterValues(List<TransactionMaster> transactionMasterList) {
        float cash = 0;
        float card = 0;
        float vat_amount = 0;
        float total = 0;
        for (TransactionMaster items : transactionMasterList) {
          cash = cash + items.getCash();
          card = card + items.getCard();
          vat_amount = vat_amount + items.getVatAmount();
          total = total + items.getGrandTotal();
        }
        textCash.setText("Cash : "+getResources().getString(R.string.currency)+" "+String.valueOf(Constants.round(cash, 2)));
        textCard.setText("Card : "+getResources().getString(R.string.currency)+" "+String.valueOf(Constants.round(card,2)));
        textVATAmount.setText("VAT Amount : "+getResources().getString(R.string.currency)+" "+String.valueOf(Constants.round(vat_amount,2)));
        textTotalAmount.setText("Total Amount : "+getResources().getString(R.string.currency)+" "+String.valueOf(Constants.round(total,2)));

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
         if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.PRINTER_TYPE,Constants.FEASYCOM).equals(Constants.FEASYCOM))
             printViaBluetoothPrinter();
         else
             printViaBluetoothPrinter1();

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
            case R.id.export:
                if((isCashier &&  (cashier== null )) || (isCashier && cashier!= null && (!cashier.isDailyReportExport())) )
                    showSnackBar(getView(),"Not Allowed!!",1000);
                else
                exportFileToExcel();
                break;
        }

        return true;
    }
    private void exportFileToExcel() {

        File sd = Environment.getExternalStorageDirectory();

        String csvFile ="pos_daily_report"+System.currentTimeMillis()+ ".xls";
        File directory = new File(sd.getAbsolutePath()+"/Reports");
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook = null;
            try {
                workbook = Workbook.createWorkbook(file, wbSettings);

                //Excel sheet name. 0 represents first sheet
                WritableSheet sheet = workbook.createSheet("Daily Report", 0);
                // column and row

                sheet.addCell(new Label(0, 0, Constants.COMPANY_SL_NO));
                sheet.addCell(new Label(1, 0, Constants.COMPANY_ORDER_NO));

                sheet.addCell(new Label(2, 0, Constants.COMPANY_ITEM_QUANTITY));
                sheet.addCell(new Label(3, 0, Constants.COMPANY_ITEM_PAYMENT));
                sheet.addCell(new Label(4, 0, Constants.COMPANY_ITEM_AMOUNT));

                int i = 0;

                Log.e("Inside123","live data"+mTransactionMasterList.size());
                for (TransactionMaster item : mTransactionMasterList) {
                    i = i + 1;
                    sheet.addCell(new Label(0, i, String.valueOf(i)));
                    sheet.addCell(new Label(1, i, String.valueOf(item.getInvoiceNo())));
                    sheet.addCell(new Label(2, i, String.valueOf(item.getTotalQty())));
                    sheet.addCell(new Label(3, i,item.getType()));
                    sheet.addCell(new Label(4, i, String.valueOf(item.getGrandTotal())));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            workbook.write();
            workbook.close();

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Data Exported in a Excel Sheet to Reports folder .");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            {
                              /*  Intent intent = new Intent (Intent.ACTION_GET_CONTENT);
                                Uri uri = Uri.parse (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Reports");
                                intent.setDataAndType (uri, "resource/folder");
                                startActivity (Intent.createChooser (intent, "Open folder"));*/

                            }
                        }
                    });


            AlertDialog alert11 = builder.create();
            alert11.show();
           // Toast.makeText(getContext(),"Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void printDayReport() {
        String printerCategory = mPrefUtils.getStringPrefrence(DEAFULT_PREFS, Constants.PRINTER_PREF_KEY, "0");
        switch (printerCategory) {
            case "0":
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Select Printer from Settings");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();
                break;
            case "1":
                printReciptBluetooth();
                break;
            case "2":
               // printReciptBuiltin(newInvoiceNo);
                break;
            case "3":
              //  showRecipt(newInvoiceNo);
                break;

        }
    }
    private void printReciptBluetooth() {
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) return;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothChecking()) {
            if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.PRINTER_TYPE,Constants.FEASYCOM).equals(Constants.FEASYCOM))
                printViaBluetoothPrinter();
            else
                printViaBluetoothPrinter1();


        }
    }








    private void onClickedFilter(boolean b) {
        if (b) {
            filter.setVisibility(View.VISIBLE);
        } else {

            filter.setVisibility(View.GONE);

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
        mSelectedDate = year + "-" + getProperDate(++month) + "-" + getProperDate(dayOfMonth);
        switch (mSelectedDateView.getId()) {

            case R.id.toDate:
                toDate.setText(mSelectedDate);
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
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText(COMPANY_TELE)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText(COMPANY_Email)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText(COMPANY_ADDRESS)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText(COMPANY_TAX)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("TRN   : "+COMPANY_TRN)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());

                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("Daily Report of " + mSelectedDate)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());

                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(Sl_NO  + createSpace(Sl_NO, Sl_NO.length(), false)+COMPANY_INV_NO + createSpace(COMPANY_INV_NO, COMPANY_INV_NO.length(), false) +
                                COMPANY_ITEM_QUANTITY + createSpace(COMPANY_ITEM_QUANTITY, COMPANY_ITEM_QUANTITY.length(), false) +
                                COMPANY_ITEM_PAYMENT + createSpace(COMPANY_ITEM_PAYMENT, COMPANY_ITEM_PAYMENT.length(), false) +
                                COMPANY_ITEM_AMOUNT + createSpace(COMPANY_ITEM_AMOUNT, COMPANY_ITEM_AMOUNT.length(), false))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
               //
                float total = 0;
                int i = 0;
                for (TransactionMaster order : mTransactionMasterList) {
                    i += 1;
                    total = total+ order.getGrandTotal();

                    String price = decimalAdjust(order.getGrandTotal());
                    String totalprice = decimalAdjust(order.getItemTotalAmount());
                    if (price == null) price = String.valueOf(order.getGrandTotal());
                    if (totalprice == null) totalprice = String.valueOf(order.getItemTotalAmount());


                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setText(i + createSpace11(Sl_NO, String.valueOf(i).length(), false) +order.getInvoiceNo() + createSpace11(COMPANY_INV_NO, String.valueOf(order.getInvoiceNo()).length(), false) +
                                    order.getTotalQty() + createSpace11(COMPANY_ITEM_QUANTITY, String.valueOf(order.getTotalQty()).length(), false) +
                                    order.getType() + createSpace11(COMPANY_ITEM_PAYMENT, String.valueOf(order.getType()).length(), false) +
                            order.getGrandTotal() + createSpace11(COMPANY_ITEM_AMOUNT, String.format("%.2f", order.getGrandTotal()).length(), false))
                            .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                            .setNewLinesAfter(1)
                            .build());
                }
                String str_total = decimalAdjust(total);
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_TOTAL +createSpace(COMPANY_ITEM_TOTAL.length(),String.valueOf(total).length())+String.valueOf(total))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
              /*  if (!getString(mFooterDiscount).isEmpty()) {
                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setText(COMPANY_ITEM_DISCOUNT + createSpace(COMPANY_ITEM_DISCOUNT.length(), String.valueOf(getString(mFooterDiscount)).length()) + getString(mFooterDiscount))
                            .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                            .setNewLinesAfter(2)
                            .build());
                }*/


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
               /* if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }*/
            }).start();
        }else{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setMessage("Select Printer from Settings");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });


            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

    }
    private void printViaBluetoothPrinter1() {
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
                        .setText("TRN   : "+COMPANY_TRN)
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
                        .setText("Daily Report of " + mSelectedDate)
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
                        .setText(COMPANY_ORDER_NO + createSpace(COMPANY_ORDER_NO, COMPANY_ORDER_NO.length(), false) +
                                COMPANY_TOTAL_ITEM + createSpace(COMPANY_TOTAL_ITEM, COMPANY_TOTAL_ITEM.length(), false) +
                                COMPANY_ITEM_PAYMENT + createSpace(COMPANY_ITEM_PAYMENT, COMPANY_ITEM_PAYMENT.length(), false) +
                                COMPANY_ITEM_AMOUNT + createSpace(COMPANY_ITEM_AMOUNT, COMPANY_ITEM_AMOUNT.length(), false))
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
                float total = 0;
                for (TransactionMaster order : mTransactionMasterList) {
                    total = total+ order.getGrandTotal();

                    String price = decimalAdjust(order.getGrandTotal());
                    String totalprice = decimalAdjust(order.getItemTotalAmount());
                    if (price == null) price = String.valueOf(order.getGrandTotal());
                    if (totalprice == null) totalprice = String.valueOf(order.getItemTotalAmount());


                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setText(order.getInvoiceNo() + createSpace(COMPANY_ORDER_NO, String.valueOf(order.getInvoiceNo()).length(), false) +
                                    order.getTotalQty() + createSpace(COMPANY_TOTAL_ITEM, String.valueOf(order.getTotalQty()).length(), false) +
                                    order.getType() + createSpace(COMPANY_ITEM_PAYMENT, String.valueOf(order.getType()).length(), false) +
                                    order.getGrandTotal() + createSpace(COMPANY_ITEM_AMOUNT, String.format("%.2f", order.getGrandTotal()).length(), false))
                            .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                            .setNewLinesAfter(2)
                            .build());
                }
                String str_total = decimalAdjust(total);
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(       COMPANY_ITEM_TOTAL +"                                    "+ String.valueOf(total))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
              /*  if (!getString(mFooterDiscount).isEmpty()) {
                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setText(COMPANY_ITEM_DISCOUNT + createSpace(COMPANY_ITEM_DISCOUNT.length(), String.valueOf(getString(mFooterDiscount)).length()) + getString(mFooterDiscount))
                            .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                            .setNewLinesAfter(2)
                            .build());
                }*/


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
               /* if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }*/
            }).start();
        }else{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setMessage("Select Printer from Settings");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });


            AlertDialog alert11 = builder1.create();
            alert11.show();
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
   private String createSpace(String item, int length, boolean isBluetooth) {
       int total;
       int num;
       switch (item) {
           case Sl_NO:
               total=!isBluetooth ? 5: 5;
               num = 1;
               return new String(new char[num]).replace('\0', ' ');
           case COMPANY_INV_NO:
               total = !isBluetooth ? 19 : 48;
               num = 1;
               return new String(new char[num]).replace('\0', ' ');
           case COMPANY_ITEM_QUANTITY:
               total = !isBluetooth ? 5 : 7;
               num = 1;
               return new String(new char[num]).replace('\0', ' ');
           case COMPANY_ITEM_PAYMENT:
               total = !isBluetooth ? 10 : 15;
               num = 1;
               return new String(new char[num]).replace('\0', ' ');
           case COMPANY_ITEM_AMOUNT:
               total = !isBluetooth ? 10 : 10;
               num = 4;
               return new String(new char[num]).replace('\0', ' ');
       }
       return null;
   }
    private String createSpace11(String item, int length, boolean isBluetooth) {
        int total;
        int num;
        switch (item) {
            case Sl_NO:
                total=!isBluetooth ? 5: 5;
                num = 3 - length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_INV_NO:
                total = !isBluetooth ? 19 : 48;
                num = 7-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_QUANTITY:
                total = !isBluetooth ? 5 : 7;
                num = 4-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_PAYMENT:
                total = !isBluetooth ? 10 : 15;
                num = 8-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_AMOUNT:
                total = !isBluetooth ? 10 : 10;
                num = 10 - length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
        }
        return null;
    }
    private String createSpace1(String item, int length, boolean isBluetooth) {
        int total;
        int num;

        switch (item) {
            case COMPANY_ORDER_NO:
                total = !isBluetooth ? 15 : 40;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_TOTAL_ITEM:
                total = !isBluetooth ? 12 : 12;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_PAYMENT:
                total = !isBluetooth ? 10 : 10;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_AMOUNT:
                total = !isBluetooth ? 10 : 10;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
        }
        return null;
    }


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
