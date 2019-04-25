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
import com.abremiratesintl.KOT.adapters.CategorySpinnerAdapter;
import com.abremiratesintl.KOT.adapters.ItemSpinnerAdapter;
import com.abremiratesintl.KOT.adapters.ItemwiseReportAdapter;
import com.abremiratesintl.KOT.adapters.ReportAdapter;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.BtDevice;
import com.abremiratesintl.KOT.models.Cashier;
import com.abremiratesintl.KOT.models.Category;
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
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_PAYMENT;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_QUANTITY;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ITEM_TOTAL;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_NAME;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_PREFIX;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TAX;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TELE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TRN;
import static com.abremiratesintl.KOT.utils.Constants.DEAFULT_PREFS;
import static com.abremiratesintl.KOT.utils.Constants.REQUEST_CODE_ENABLE_BLUETOOTH;
import static com.abremiratesintl.KOT.utils.Constants.Sl_NO;
import static com.abremiratesintl.KOT.utils.Constants.REPORT_DATE;
import static com.abremiratesintl.KOT.utils.Constants.CARD;
import static com.abremiratesintl.KOT.utils.Constants.CASH;
/**
 * A simple {@link Fragment} subclass.
 */
public class ItemwiseReportFragment extends BaseFragment implements ClickListeners.ItemClick<Transaction>, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener, CustomSpinner.OnSpinnerEventsListener  {
    @BindView(R.id.footer)
    RelativeLayout footer;
    @BindView(R.id.header)
    LinearLayout header;
    @BindView(R.id.textTotal)
    TextView textTotal;
    @BindView(R.id.spinItem)
    CustomSpinner spinItem;
    @BindView(R.id.spinner_arrow)
    ImageView mSpinnerArrow;
    @BindDrawable(R.drawable.ic_arrow_down)
    Drawable icDown;
    @BindDrawable(R.drawable.ic_arrow_up)
    Drawable icUp;
    @BindView(R.id.reportRecyclerViewReport)
    RecyclerView reportRecyclerview;
    @BindView(R.id.filter)
    LinearLayout filter;
    @BindView(R.id.fromDate)
    EditText fromDate;
    @BindView(R.id.toDate)
    EditText toDate;
    @BindView(R.id.emptyReportView)
    ConstraintLayout emptyView;
    private Unbinder mUnbinder;
    private AppDatabase mDatabase;
    View mSelectedDateView;
    private List<Items> mItemsList;
    private Items mSelectedItem;
    private List<Transaction> mTransactionList;
    private Cashier cashier = new Cashier();
    private boolean isCashier = false;
    PrefUtils mPrefUtils ;
    private BluetoothAdapter bluetoothAdapter;
    private boolean isAllSelected= true;
    String selFromDate,selToDate;
    public ItemwiseReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itemwise_report, container, false);
        mDatabase = AppDatabase.getInstance(getContext());
        mUnbinder = ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).changeTitle(" Item REPORTS");
        mPrefUtils = new PrefUtils(getContext());

        Thread t = new Thread(() -> {
            cashier = mDatabase.mCashierDao().getCashier();
        });
        t.start();

        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER))
            isCashier = true;

        spinItem.setOnItemSelectedListener(this);
        spinItem.setSpinnerEventsListener(this);
        getItems();

        setHasOptionsMenu(true);
      //  fetchTransactions();
        return view;
    }
    private void getItems() {
        LiveData<List<Items>> itemLiveList = mDatabase.mItemsDao().getAllItems();
        itemLiveList.observe(this, items -> {
            if (items == null || items.size() == 0) {
                return;
            }
            mItemsList = items;
            Items temp = new Items();
            temp.setItemId(-1);
            temp.setItemName("All");
            

            mItemsList.add(0,temp);
            mSelectedItem = mItemsList.get(0);

            setUpSpinner();
        });
    }

    private void setUpSpinner() {
        ItemSpinnerAdapter<Items> itemSpinnerAdapter = new ItemSpinnerAdapter<>(getContext(), R.id.categoryListItem, mItemsList);
        spinItem.setAdapter(itemSpinnerAdapter);
        fetchTransactions();
    }
    private void fetchTransactions() {
        Log.e("INSERTION MASTER1", "inside22"+Constants.getCurrentDate());
        LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().getTodaysAllItems(Constants.getCurrentDate());

        listLiveData.observe(this, this::setUpRecycler);
    }

    private void setUpRecycler(List<Transaction> transactionList){
        mTransactionList = transactionList;
        Log.e("INSERTION MASTER1", "inside22"+transactionList.size());
        if(transactionList.size()==0){
            emptyView.setVisibility(View.VISIBLE);
            reportRecyclerview.setVisibility(View.GONE);
            footer.setVisibility(View.GONE);
            header.setVisibility(View.GONE);
            return;
        }
        emptyView.setVisibility(View.GONE);
        reportRecyclerview.setVisibility(View.VISIBLE);
        header.setVisibility(View.VISIBLE);
        footer.setVisibility(View.VISIBLE);
        ItemwiseReportAdapter adapter = new ItemwiseReportAdapter(transactionList, this);
        reportRecyclerview.setAdapter(adapter);
        setFooterValues(transactionList);
    }

    private void setFooterValues(List<Transaction> transactionList) {

        float total = 0;
        for (Transaction items : transactionList) {

            total = total + items.getGrandTotal();
        }

        textTotal.setText(getResources().getString(R.string.currency)+" "+String.valueOf(Constants.round(total,2)));

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
                        .setText("Item Report of " + mSelectedItem.getItemName())
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
                        .setText(Sl_NO  + createSpacePrinterHeading(Sl_NO, Sl_NO.length(), false)+COMPANY_ITEM_DESCRIPTION + createSpacePrinterHeading(COMPANY_ITEM_DESCRIPTION, COMPANY_ITEM_DESCRIPTION.length(), false) +
                                COMPANY_ITEM_QUANTITY + createSpacePrinterHeading(COMPANY_ITEM_QUANTITY, COMPANY_ITEM_QUANTITY.length(), false) +
                                REPORT_DATE + createSpaceAmtPrinter(REPORT_DATE.length(), COMPANY_ITEM_AMOUNT.length()) +
                                COMPANY_ITEM_AMOUNT )
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                float total = 0;
                int i = 0;
                for (Transaction order : mTransactionList) {
                    i += 1;
                    total = total+ order.getGrandTotal();

                    String item_name = order.getItemName();
                    if(item_name.length() <= 19) {
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(i + createSpacePrinterData(Sl_NO, String.valueOf(i).length(), false) + order.getItemName() + createSpacePrinterData(COMPANY_ITEM_DESCRIPTION, String.valueOf(order.getItemName()).length(), false) +
                                        order.getQty() + createSpacePrinterData(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                        order.getInvoiceDate() + createSpaceAmtPrinter(String.valueOf(order.getInvoiceDate()).length(), String.format("%.2f", order.getPrice()).length()) +
                                        order.getPrice())
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(2)
                                .build());
                    }else{
                        String str_first = item_name.substring(0,19);
                        String str_next = item_name.substring(19,item_name.length());
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(i + createSpacePrinterData(Sl_NO, String.valueOf(i).length(), false) + str_first + createSpacePrinterData(COMPANY_ITEM_DESCRIPTION, String.valueOf(str_first).length(), false) +
                                        order.getQty() + createSpacePrinterData(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                        order.getInvoiceDate() + createSpaceAmtPrinter(String.valueOf(order.getInvoiceDate()).length(), String.format("%.2f", order.getPrice()).length()) +
                                        order.getPrice())
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(1)
                                .build());
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(createSpacePrinterData(Sl_NO, 0, false) + str_next + createSpacePrinterData(COMPANY_ITEM_DESCRIPTION, str_next.length(), false))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(2)
                                .build());
                    }
                }
                String str_total = decimalAdjust(total);
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_TOTAL +createSpacePrinter(COMPANY_ITEM_TOTAL.length(),str_total.length())+str_total)
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
    @OnClick(R.id.fromDate) public void onClickedFromDate() {
        mSelectedDateView = fromDate;
        showDatePicker();
    }

    @OnClick(R.id.toDate) public void onClickedToDate() {
        mSelectedDateView = toDate;
        showDatePicker();
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
                    dialog.setInteractorToFragment(ItemwiseReportFragment.this::interacterOne);
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

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.day_menu, menu);
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
            case R.id.export:
                if((isCashier &&  (cashier== null )) || (isCashier && cashier!= null && (!cashier.isItemReportExport())) )
                    showSnackBar(getView(),"Not Allowed!!",1000);
                else {
                    if(mTransactionList.size() > 0)
                    exportFileToExcel();
                }
                break;
            case R.id.menu_print:
                if(mTransactionList.size() > 0)
                printReport();
                break;
        }

        return true;
    }

    private void onClickedFilter(boolean b) {
        if (b) {
            filter.setVisibility(View.VISIBLE) ;
        } else {
            filter.setVisibility(View.GONE);
        }
    }
    private void exportFileToExcel() {

        File sd = Environment.getExternalStorageDirectory();

        String csvFile ="pos_item_report"+System.currentTimeMillis()+ ".xls";
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
                WritableSheet sheet = workbook.createSheet("Item Report", 0);
                // column and row

                sheet.addCell(new Label(0, 0, Constants.COMPANY_SL_NO));
                sheet.addCell(new Label(1, 0, Constants.COMPANY_ORDER_NO));
                sheet.addCell(new Label(2, 0, COMPANY_ITEM_DESCRIPTION));
                sheet.addCell(new Label(3, 0, Constants.CATEGORY));
                sheet.addCell(new Label(4, 0, Constants.COMPANY_ITEM_QUANTITY));
                sheet.addCell(new Label(5, 0, Constants.COMPANY_DATE));
                sheet.addCell(new Label(6, 0, Constants.COMPANY_ITEM_AMOUNT));


                int i = 0;

                Log.e("Inside123","live data"+mTransactionList.size());
                for (Transaction item : mTransactionList) {
                    i = i + 1;
                    sheet.addCell(new Label(0, i, String.valueOf(i)));
                    sheet.addCell(new Label(1, i, String.valueOf(item.getTransactionId())));
                    sheet.addCell(new Label(2, i, item.getItemName()));
                    sheet.addCell(new Label(3, i, item.getCategory()));
                    sheet.addCell(new Label(4, i, String.valueOf(item.getQty())));
                    sheet.addCell(new Label(5, i, item.getInvoiceDate()));
                    sheet.addCell(new Label(6, i, String.valueOf(item.getPrice())));
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
                               /* Intent intent = new Intent (Intent.ACTION_GET_CONTENT);
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
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override public void onClickedItem(Transaction item) {

    }

    public void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(), this,calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year + "-" + getProperDate(++month) + "-" + getProperDate(dayOfMonth);
        switch (mSelectedDateView.getId()) {
            case R.id.fromDate:
                selFromDate = date;
                fromDate.setText(date);
                sortedItem(getString(fromDate), getString(toDate));
                break;
            case R.id.toDate:
                selToDate = date;
                toDate.setText(date);
                sortedItem(getString(fromDate), getString(toDate));
                break;
        }
    }

    private void sortedItem(String fromDate, String toDate) {
        if (toDate.equals(getStringfromResource(R.string.present))) {
            toDate = Constants.getCurrentDate();
            selToDate = Constants.getCurrentDate();
        }
       /* if (fromDate.equals("")){
            showSnackBar(getView(),getStringfromResource(R.string.present),1000);
            return;
        }*/

       if(mSelectedItem.getItemId() == -1)
           isAllSelected= true;
       else
           isAllSelected = false;
       if(isAllSelected){
           LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().findAllItemsByItemIdBetween(fromDate, toDate);
           listLiveData.observe(this, this::setUpRecycler);
       }else {

           LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().findItemsByItemIdBetween(fromDate, toDate, mSelectedItem.getItemId());
           listLiveData.observe(this, this::setUpRecycler);
       }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedItem = mItemsList.get(position);
        if(mSelectedItem.getItemId() == -1){
            if(filter.getVisibility() == View.VISIBLE){
                LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().findAllItemsByItemIdBetween(selFromDate, selToDate);
                listLiveData.observe(this, this::setUpRecycler);
            }else {
                LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().getTodaysAllItems(Constants.getCurrentDate());
                listLiveData.observe(this, this::setUpRecycler);
            }
        }else {
            if(filter.getVisibility() == View.VISIBLE) {
                LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().findItemsByItemIdBetween(selFromDate, selToDate, mSelectedItem.getItemId());
                listLiveData.observe(this, this::setUpRecycler);
            }else {
                LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().getItemsByItemId(mSelectedItem.getItemId());

                listLiveData.observe(this, this::setUpRecycler);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onSpinnerOpened(Spinner spinner) {
        mSpinnerArrow.setImageDrawable(icUp);
    }

    @Override
    public void onSpinnerClosed(Spinner spinner) {
        mSpinnerArrow.setImageDrawable(icDown);
    }
    private String createSpaceAmtPrinter(int firstLength, int secondLegth) {
        //   int num = 32 - firstLength;
        int num = 18 - firstLength ;
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
            case COMPANY_ITEM_DESCRIPTION:
                total = !isBluetooth ? 21 : 48;
                num = 17;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_QUANTITY:
                total = !isBluetooth ? 5 : 7;
                num = 2;
                return new String(new char[num]).replace('\0', ' ');
            case REPORT_DATE:
                total = !isBluetooth ? 11 : 15;
                num = 1;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_AMOUNT:
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
            case COMPANY_ITEM_DESCRIPTION:

                num = 21-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_QUANTITY:

                num = 5-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case REPORT_DATE:

                num = 11-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_AMOUNT:
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
    private String decimalAdjust(float value) {
        String stringValue = String.valueOf(value);
        if (stringValue.substring(stringValue.length() - 1).equals("0")) {
            return stringValue + 0;
        }
        return null;
    }


}

