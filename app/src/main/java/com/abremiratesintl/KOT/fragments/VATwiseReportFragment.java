package com.abremiratesintl.KOT.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.Dialog;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.adapters.ItemSpinnerAdapter;
import com.abremiratesintl.KOT.adapters.ItemwiseReportAdapter;
import com.abremiratesintl.KOT.adapters.ReportAdapter;
import com.abremiratesintl.KOT.adapters.VATwiseReportAdapter;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.BtDevice;
import com.abremiratesintl.KOT.models.Cashier;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.Transaction;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;
import com.abremiratesintl.KOT.views.CustomSpinner;
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

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ADDRESS;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_Email;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_INV_NO;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_AMOUNT;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_DESCRIPTION;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_QUANTITY;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_TOTAL;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_NAME;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_PREFIX;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TELE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TRN;
import static com.abremiratesintl.KOT.utils.Constants.DEAFULT_PREFS;
import static com.abremiratesintl.KOT.utils.Constants.INV_NO;
import static com.abremiratesintl.KOT.utils.Constants.REPORT_DATE;
import static com.abremiratesintl.KOT.utils.Constants.REQUEST_CODE_ENABLE_BLUETOOTH;
import static com.abremiratesintl.KOT.utils.Constants.Sl_NO;
import static com.abremiratesintl.KOT.utils.Constants.TOTAL_VATABLE_AMT;
import static com.abremiratesintl.KOT.utils.Constants.TOTAL_VAT_AMT;
import static com.abremiratesintl.KOT.utils.Constants.VATABLE_AMT;
import static com.abremiratesintl.KOT.utils.Constants.VAT_AMT;
import static com.abremiratesintl.KOT.utils.Constants.VAT_EXCLUSIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class VATwiseReportFragment extends BaseFragment implements ClickListeners.ItemClick<TransactionMaster>, DatePickerDialog.OnDateSetListener {
    @BindView(R.id.footer)
    RelativeLayout footer;
    @BindView(R.id.header)
    LinearLayout header;
    @BindView(R.id.textTotal)
    TextView textTotal;
    @BindView(R.id.reportRecyclerViewReport)
    RecyclerView reportRecyclerview;
    @BindView(R.id.filter)
    LinearLayout filter;
    @BindView(R.id.fromDate)
    TextView fromDate;
    @BindView(R.id.toDate)
    TextView toDate;
    @BindView(R.id.emptyReportView)
    ConstraintLayout emptyView;
    private Unbinder mUnbinder;
    private AppDatabase mDatabase;
    View mSelectedDateView;
    private List<TransactionMaster> mTransactionMasterList;
    private Cashier cashier = new Cashier();
    private boolean isCashier = false;
    PrefUtils mPrefUtils ;
    @BindView(R.id.textVat)
    TextView textVat;
    @BindView(R.id.textVatAmt)
    TextView textVatAmt;
    private BluetoothAdapter bluetoothAdapter;
    public VATwiseReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vatwise_report, container, false);
        mDatabase = AppDatabase.getInstance(getContext());
        mUnbinder = ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).changeTitle(" VAT REPORTS");
        mPrefUtils = new PrefUtils(getContext());

        Thread t = new Thread(() -> {
            cashier = mDatabase.mCashierDao().getCashier();
        });
        t.start();

        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER))
            isCashier = true;

        setHasOptionsMenu(true);
        fetchTransactions();
        return view;
    }

    private void fetchTransactions() {
        LiveData<List<TransactionMaster>> listLiveData = mDatabase.mTransactionMasterDao().getAllItems();
        listLiveData.observe(this, this::setUpRecycler);
    }

    private void setUpRecycler(List<TransactionMaster> transactionMasterList){
        mTransactionMasterList = transactionMasterList;
        if(transactionMasterList.size()==0){
            emptyView.setVisibility(View.VISIBLE);
            reportRecyclerview.setVisibility(View.GONE);
            footer.setVisibility(View.GONE);
            header.setVisibility(View.GONE);
            return;
        }
        footer.setVisibility(View.VISIBLE);
        header.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        reportRecyclerview.setVisibility(View.VISIBLE);
        VATwiseReportAdapter adapter = new VATwiseReportAdapter(getActivity(),transactionMasterList, this);
        reportRecyclerview.setAdapter(adapter) ;
        setFooterValues(transactionMasterList);
    }
    private void setFooterValues(List<TransactionMaster> transactionMasterList) {
        String str_vat = mPrefUtils.getStringPrefrence(DEAFULT_PREFS, Constants.VAT_EXCLUSIVE, getActivity().getResources().getString(R.string.vat_exclusive));
        float total = 0,vat = 0,vatable_amt=0;
        for (TransactionMaster items : transactionMasterList) {

            total = total + items.getGrandTotal();
            vat = vat +items.getVatAmount();
          /*  if(items.getDiscountAmount() > 0)
                vatable_amt = vatable_amt + items.getItemTotalAmount() - items.getDiscountAmount();
            else*/
            if (str_vat.equals(getActivity().getResources().getString(R.string.vat_inclusive)))
            vatable_amt = vatable_amt + items.getItemTotalAmount() - items.getVatAmount()-items.getDiscountAmount();
            else
                vatable_amt = vatable_amt + items.getItemTotalAmount()-items.getDiscountAmount();
        }

        textTotal.setText(getResources().getString(R.string.currency)+" "+String.valueOf(Constants.round(total,2)));
        textVat.setText(getResources().getString(R.string.currency)+" "+String.valueOf(Constants.round(vat,2)));
        textVatAmt.setText(getResources().getString(R.string.currency)+" "+String.valueOf(Constants.round(vatable_amt,2)));
    }
    @OnClick(R.id.fromDate) public void onClickedFromDate() {
        mSelectedDateView = fromDate;
        showDatePicker();
    }

    @OnClick(R.id.toDate) public void onClickedToDate() {
        mSelectedDateView = toDate;
        showDatePicker();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.day_menu, menu);
    }
    private void printReport() {
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
                        .setText(COMPANY_ADDRESS)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("Email : "+COMPANY_Email)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("Tel No : "+COMPANY_TELE)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("TRN : "+COMPANY_TRN)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                String prefix = mPrefUtils.getStringPrefrence(DEAFULT_PREFS, COMPANY_PREFIX, "SJ");
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText(prefix)
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
                        .setText("VAT Report")
                        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
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
                        .setText(Sl_NO  + createSpacePrinterHeading(Sl_NO, Sl_NO.length(), false)+COMPANY_INV_NO + createSpacePrinterHeading(COMPANY_INV_NO, COMPANY_INV_NO.length(), false) +
                                REPORT_DATE + createSpacePrinterHeading(REPORT_DATE, REPORT_DATE.length(), false) +
                                VATABLE_AMT + createSpacePrinterHeading(VATABLE_AMT, VATABLE_AMT.length(), false) +
                                VAT_AMT + createSpaceAmtPrinter(VAT_AMT.length(), COMPANY_ITEM_TOTAL.length()) +
                                COMPANY_ITEM_TOTAL )
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                double total = 0,vatable_amt1=0,vat_amt1=0;
                int i = 0;
                String str_vat = mPrefUtils.getStringPrefrence(DEAFULT_PREFS, Constants.VAT_EXCLUSIVE, getActivity().getResources().getString(R.string.vat_exclusive));
                for (TransactionMaster order : mTransactionMasterList) {
                    i += 1;
                    total = total+ order.getGrandTotal();
                    vat_amt1 = vat_amt1 +order.getVatAmount();
                    String inv_no ;
                    float vatable_amt=0;
                    if(order.getName()==null)
                        inv_no=order.getInvoiceNo();
                    else
                        inv_no=order.getInvoiceNo()+"(Ref"+order.getName()+")";
                    if (str_vat.equals(getActivity().getResources().getString(R.string.vat_inclusive))) {
                         vatable_amt = order.getItemTotalAmount() - order.getVatAmount()-order.getDiscountAmount();

                    }else {
                         vatable_amt = order.getItemTotalAmount() - order.getDiscountAmount();

                    }
                    vatable_amt1 = vatable_amt1 +vatable_amt;
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(i + createSpacePrinterData(Sl_NO, String.valueOf(i).length(), false) + inv_no + createSpacePrinterData(COMPANY_INV_NO, inv_no.length(), false) +
                                        order.getInvoiceDate() + createSpacePrinterData(REPORT_DATE, order.getInvoiceDate().length(), false) +
                                        vatable_amt + createSpacePrinterData(VATABLE_AMT, String.valueOf(vatable_amt).length(), false) +
                                        order.getVatAmount() + createSpaceAmtPrinter(String.valueOf(order.getVatAmount()).length(), String.valueOf(order.getGrandTotal()).length()) +
                                        order.getGrandTotal())
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(2)
                                .build());

                }

                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(TOTAL_VATABLE_AMT +createSpacePrinter(TOTAL_VATABLE_AMT.length(),String.valueOf(vatable_amt1).length())+vatable_amt1)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(TOTAL_VAT_AMT+createSpacePrinter(TOTAL_VAT_AMT.length(),String.valueOf(vat_amt1).length())+vat_amt1)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_TOTAL +createSpacePrinter(COMPANY_ITEM_TOTAL.length(),String.valueOf(total).length())+total)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());

                Printooth.INSTANCE.printer().print(printables);

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
    private String createSpaceAmtPrinter(int firstLength, int secondLegth) {
        //   int num = 32 - firstLength;
        int num = 14 - firstLength ;
        num = num - secondLegth;
        return new String(new char[num]).replace('\0', ' ');
    }

    private String createSpacePrinterHeading(String item, int length, boolean isBluetooth) {
        int total;
        int num;
        switch (item) {
            case Sl_NO:
                total=!isBluetooth ? 4: 5;
                num = 2;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_INV_NO:
                total = !isBluetooth ? 21 : 48;
                num = 1;
                return new String(new char[num]).replace('\0', ' ');
            case REPORT_DATE:
                total = !isBluetooth ? 5 : 7;
                num = 7;
                return new String(new char[num]).replace('\0', ' ');
            case VATABLE_AMT:
                total = !isBluetooth ? 11 : 15;
                num = 1;
                return new String(new char[num]).replace('\0', ' ');
            case VAT_AMT:
                total = !isBluetooth ? 7 : 10;
                num = 1;
                return new String(new char[num]).replace('\0', ' ');

        }
        return null;
    }

    private String createSpacePrinterData(String item, int length, boolean isBluetooth) {
        int num;
        switch (item) {
            case Sl_NO:

                num = 4 - length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_INV_NO:

                num = 7-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case REPORT_DATE:

                num = 11-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case VATABLE_AMT:

                num = 12-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case VAT_AMT:
                num = 7 - length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
        }
        return null;
    }

    private String createSpacePrinter(int firstLength, int secondLegth) {
        int num = 48- firstLength;
        num = num - secondLegth;
        return new String(new char[num]).replace('\0', ' ');
    }
    private void printReciptBluetooth() {
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) return;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothChecking()) {

            printViaBluetoothPrinter();


        }
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
                    dialog.setInteractorToFragment(VATwiseReportFragment.this::interacterOne);
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


    public void interacterOne(BtDevice btDevice) {

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
                onClickedFilter(false);
                menuClickCount = 0;
                LiveData<List<TransactionMaster>> listLiveData = mDatabase.mTransactionMasterDao().getAllItems();
                listLiveData.observe(this, this::setUpRecycler);
                break;*/
            case R.id.export:
                if((isCashier &&  (cashier== null )) || (isCashier && cashier!= null && (!cashier.isVatReportExport())) )
                    showSnackBar(getView(),"Not Allowed!!",1000);
                else
                exportFileToExcel();
                break;
            case R.id.menu_print:
                if(mTransactionMasterList.size() > 0)
                    printReport();
                break;
        }

        return true;
    }
    private void exportFileToExcel() {

        File sd = Environment.getExternalStorageDirectory();

        String csvFile ="pos_vat_report"+System.currentTimeMillis()+ ".xls";
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
                WritableSheet sheet = workbook.createSheet("Vat Report", 0);
                // column and row

                sheet.addCell(new Label(0, 0, Constants.COMPANY_SL_NO));
                sheet.addCell(new Label(1, 0, Constants.COMPANY_ORDER_NO));

                sheet.addCell(new Label(2, 0, Constants.COMPANY_DATE));
                sheet.addCell(new Label(3, 0, "Vatable Amount"));
                sheet.addCell(new Label(4, 0, "Vat Amount"));
                sheet.addCell(new Label(5, 0, Constants.COMPANY_ITEM_AMOUNT));
                int i = 0;

                Log.e("Inside123"," data"+mTransactionMasterList.size());
                String str_vat = mPrefUtils.getStringPrefrence(DEAFULT_PREFS, Constants.VAT_EXCLUSIVE, getActivity().getResources().getString(R.string.vat_exclusive));
                float vatable_amt1;
                for (TransactionMaster item : mTransactionMasterList) {


                    if (str_vat.equals(getActivity().getResources().getString(R.string.vat_inclusive))) {
                         vatable_amt1 = item.getItemTotalAmount() - item.getVatAmount()-item.getDiscountAmount();

                    }else {
                         vatable_amt1 = item.getItemTotalAmount() - item.getDiscountAmount();

                    }
                    i = i + 1;
                    sheet.addCell(new Label(0, i, String.valueOf(i)));
                    sheet.addCell(new Label(1, i, String.valueOf(item.getInvoiceNo())));
                    sheet.addCell(new Label(2, i, String.valueOf(item.getInvoiceDate())));
                    sheet.addCell(new Label(3, i,String.valueOf(vatable_amt1)));
                    sheet.addCell(new Label(4, i, String.valueOf(item.getVatAmount())));
                    sheet.addCell(new Label(5, i, String.valueOf(item.getGrandTotal())));
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
                                Intent intent = new Intent (Intent.ACTION_GET_CONTENT);
                                Uri uri = Uri.parse (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Reports");
                                intent.setDataAndType (uri, "resource/folder");
                                startActivity (Intent.createChooser (intent, "Open folder"));

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
        String date = year + "-" + getProperDate(++month) + "-" + getProperDate(dayOfMonth);
        switch (mSelectedDateView.getId()) {
            case R.id.fromDate:
                fromDate.setText(date);
                sortedItem(getString(fromDate), getString(toDate));
                break;
            case R.id.toDate:
                toDate.setText(date);
                sortedItem(getString(fromDate), getString(toDate));
                break;
        }
    }

    private void sortedItem(String fromDate, String toDate) {
        if (toDate.equals(getStringfromResource(R.string.present))) {
            toDate = Constants.getCurrentDate();
        }
     /*   if (fromDate.equals("")){
            showSnackBar(getView(),getStringfromResource(R.string.present),1000);
            return;
        }*/
        LiveData<List<TransactionMaster>> listLiveData = mDatabase.mTransactionMasterDao().findItemsByBetween(fromDate, toDate);
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
}
