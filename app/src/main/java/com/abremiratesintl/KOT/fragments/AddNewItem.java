package com.abremiratesintl.KOT.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.adapters.CategorySpinnerAdapter;
import com.abremiratesintl.KOT.adapters.POSRecyclerAdapter;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.BtDevice;
import com.abremiratesintl.KOT.models.CartItems;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.abremiratesintl.KOT.utils.Constants.CHANGE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ADDRESS;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_DATE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_Email;
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
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_PREFIX;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TAX;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TELE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_TRN;
import static com.abremiratesintl.KOT.utils.Constants.DEAFULT_PREFS;
import static com.abremiratesintl.KOT.utils.Constants.PAID_AMOUNT;
import static com.abremiratesintl.KOT.utils.Constants.REQUEST_CODE_ENABLE_BLUETOOTH;
import static com.abremiratesintl.KOT.utils.Constants.Sl_NO;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewItem extends BaseFragment implements AdapterView.OnItemSelectedListener, ClickListeners.BtResponseListener, CustomSpinner.OnSpinnerEventsListener, ClickListeners.ItemClick<Items>, ClickListeners.OnItemChangedListener {
    private BluetoothAdapter bluetoothAdapter;
    @BindView(R.id.addNewItemspinnerCategory)
    CustomSpinner addNewItemSpinnerCategory;
    @BindView(R.id.addNewItemRecyclerView)
    RecyclerView addNewItemRecyclerView;
    @BindView(R.id.addNewItemProceed)
    TextView addNewItemProceed;
    @BindView(R.id.totalAmount)
    TextView totalAmount;
    @BindView(R.id.itemCount)
    TextView itemCount;
    @BindView(R.id.current_item_and_count)
    TextView currentItemAndCount;
    @BindView(R.id.addNewItemSpinnerArrow)
    ImageView addNewItemSpinnerArrow;
    @BindDrawable(R.drawable.ic_arrow_down)
    Drawable arrowDown;
    @BindDrawable(R.drawable.ic_arrow_up)
    Drawable arrowUp;

    @BindView(R.id.editInvoiceNo)
    EditText editInvoiceNo;
    @BindView(R.id.btnInvoicePrint)
    Button btnInvoicePrint;

    private Unbinder mUnbinder;
    private AppDatabase mDatabase;
    private List<Category> mCategoryList;
    private Category mSelectedCategory;
    private List<Items> mItemList;
    public List<Items> mCartItems = new ArrayList<>();
    public List<Items> mCartItemsForSaleReturn = new ArrayList<>();
    private float currentVat = 0;
    private float currentTotal;
    int totalItemCount = 0, menuReturnClickCount = 0;
    float totalItemPrice = 0;
    CartItems cartItem = new CartItems();
    POSRecyclerAdapter mItemsAdapter;
    private AudioManager audioManager;
    private Cashier cashier = new Cashier();
    private PrefUtils mPrefUtils;
    private boolean isCashier = false;
    TransactionMaster invTransactionMaster = new TransactionMaster();
    List<Transaction> invTransactionList = new ArrayList<>();
    private String invNo = "";
    private boolean isSaleReturned = false;

    public AddNewItem() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_item, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mDatabase = AppDatabase.getInstance(getContext());
        mPrefUtils = new PrefUtils(getContext());
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        addNewItemSpinnerCategory.setOnItemSelectedListener(this);
        addNewItemSpinnerCategory.setSpinnerEventsListener(this);
        ((MainActivity) getActivity()).changeTitle("SHOPPING");
        new CheckoutFragment().setOnItemChangedListener(this);
        setHasOptionsMenu(true);
        menuReturnClickCount = 0;
        getCategoryList();
        if (cartItem.getCartItems() != null && cartItem.getCartItems().size() != 0) {
            int qty = 0;
            float price = 0;
            mCartItems = cartItem.getCartItems();
          /*  mCartItemsForSaleReturn = new ArrayList<>();
            for (Items item : mCartItems) {
                if (item.isSaleReturned()) {
                    mCartItemsForSaleReturn.add(item);
                    mCartItems.remove(item);
                }


            }
            mCartItemsForSaleReturn = cartItem.getCartItems();*/
            for (Items item : cartItem.getCartItems()) {
                qty = qty + item.getQty();
                price = price + item.getTotalItemPrice();
                itemCount.setText(String.valueOf(qty));
                totalAmount.setText(String.valueOf(price));
            }
        } else if (cartItem.getCartItems() == null || cartItem.getCartItems().size() == 0) {
            itemCount.setText(String.valueOf(0));
            totalAmount.setText(String.valueOf(0));
        } else {
            itemCount.setText(String.valueOf(totalItemCount));
            totalAmount.setText(String.valueOf(totalItemPrice));
        }
        if (mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS, Constants.USER_TYPE, Constants.CASHIER).equals(Constants.CASHIER))
            isCashier = true;
        editInvoiceNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            /*    if(s.length() == 1 && s.equals("-"))
                {return;}
                else if*/
                if (s.length() > 0) {
                    getTransactionByInvId(s.toString());
                    getItemsbyInvId(s.toString());
                }


            }
        });
        btnInvoicePrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getString(editInvoiceNo) != null) {
                    invNo = getString(editInvoiceNo);

                    if (invTransactionMaster.getName() == null)
                        isSaleReturned = false;
                    else
                        isSaleReturned = true;
//                    getTransactionByInvId(getString(editInvoiceNo));
//                    getItemsbyInvId(getString(editInvoiceNo));

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
                            //   printReciptBuiltin(newInvoiceNo);
                            break;
                        case "3":
                            //   showRecipt(newInvoiceNo);
                            break;
                    }

                }
            }
        });

        Thread t = new Thread(() -> {
            cashier = mDatabase.mCashierDao().getCashier();
        });
        t.start();

        //   Log.e("cashier nmae :","cat :"+cashier.isCategoryView() +"name :"+cashier.getCashierName());
        return view;
    }

    private void printReciptBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothChecking()) {
           /* if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.PRINTER_TYPE,Constants.FEASYCOM).equals(Constants.FEASYCOM))
                printViaBluetoothPrinter();
            else*/
            printInvoice();
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
                    com.abremiratesintl.KOT.Dialog dialog = new com.abremiratesintl.KOT.Dialog(getContext());
                    dialog.showBluetoothDevices(getPairedDevices());
                    dialog.setInteractorToFragment(AddNewItem.this::interacterOne);
                    dialog.show();
                });

            }
            return true;
        }
        return false;
    }

    @Override
    public void interacterOne(BtDevice btDevice) {
        Printooth.INSTANCE.setPrinter(btDevice.getDeviceName(), btDevice.getDeviceMac());
       /* if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.PRINTER_TYPE,Constants.FEASYCOM).equals(Constants.FEASYCOM))
            printViaBluetoothPrinter();
        else*/
        printInvoice();

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
            device.setDeviceName("No devices Found\n Please go to bluetooth settings and pair the device");
        }
        return devices;
    }

    private void getItemsbyInvId(String string) {
        int id = Integer.parseInt(string);
        LiveData<List<Transaction>> transactionLiveData = mDatabase.mTransactionDao().getItemsByItemInvId(id);
        transactionLiveData.observe(this, transaction -> {
            if (transaction == null) {
                return;
            }
            invTransactionList = transaction;


        });
    }

    private void printInvoice() {
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
                        .setText("Email : " + COMPANY_Email)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("Tel No : " + COMPANY_TELE)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText(COMPANY_TAX)
                        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("TRN : " + COMPANY_TRN)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(3)
                        .build());
                String prefix = mPrefUtils.getStringPrefrence(DEAFULT_PREFS, COMPANY_PREFIX, "SJ");
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText(prefix)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                if (isSaleReturned) {
                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
                            .setText(COMPANY_ORDER_NO + invNo + "  (Ref " + invTransactionMaster.getName() + ")")
                            .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                            .setNewLinesAfter(2)
                            .build());
                } else {
                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
                            .setText(COMPANY_ORDER_NO + invNo)
                            .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                            .setNewLinesAfter(2)
                            .build());
                }

                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_DATE + invTransactionMaster.getInvoiceDate())
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
                        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
                        .setText(Sl_NO + createSpace3(Sl_NO, Sl_NO.length(), false) + COMPANY_ITEM_DESCRIPTION + createSpace3(COMPANY_ITEM_DESCRIPTION, COMPANY_ITEM_DESCRIPTION.length(), false) +
                                COMPANY_ITEM_QUANTITY + createSpace3(COMPANY_ITEM_QUANTITY, COMPANY_ITEM_QUANTITY.length(), false) +
                                COMPANY_ITEM_PRICE + createSpace3(COMPANY_ITEM_PRICE, COMPANY_ITEM_PRICE.length(), false) +
                                COMPANY_ITEM_AMOUNT + createSpace3(COMPANY_ITEM_AMOUNT, COMPANY_ITEM_AMOUNT.length(), false))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                int i = 0;
                String[] items = {};

                for (Transaction order : invTransactionList) {

                    String item_name = order.getItemName();

                    if (item_name.length() > 19)
                        items = item_name.split(" ");
                    Log.e("Print :", "Item length :" + items.length);
                   /* for(int j = 0 ;j<= items.length;j++){

                    }*/
                    i += 1;

                    String price = decimalAdjust(order.getPrice());
                    String totalprice = decimalAdjust(order.getGrandTotal());
                    if (price == null) price = String.valueOf(order.getPrice());
                    if (totalprice == null) totalprice = String.valueOf(order.getGrandTotal());
                    if (item_name.length() <= 19 && items.length == 0) {

                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(i + createSpace11(Sl_NO, String.valueOf(i).length(), false) + item_name + createSpace11(COMPANY_ITEM_DESCRIPTION, item_name.length(), false) +
                                        order.getQty() + createSpace11(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                        price + createSpaceAmtPrinter(String.format("%.2f", order.getPrice()).length(), String.format("%.2f", order.getGrandTotal()).length())
                                        + totalprice)
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(2)
                                .build());
                    } else if (item_name.length() > 19 && items.length == 1) {

                        String str_first = item_name.substring(0, 19);
                        String str_next = item_name.substring(19, item_name.length());
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(i + createSpace11(Sl_NO, String.valueOf(i).length(), false) + str_first + createSpace11(COMPANY_ITEM_DESCRIPTION, str_first.length(), false) +
                                        order.getQty() + createSpace11(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                        price + createSpaceAmtPrinter(String.format("%.2f", order.getPrice()).length(), String.format("%.2f", order.getGrandTotal()).length()) +
                                        totalprice)
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())

                                .build());
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(createSpace11(Sl_NO, 0, false) + str_next + createSpace11(COMPANY_ITEM_DESCRIPTION, str_next.length(), false))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(2)
                                .build());
                    } else {

                        String parts[] = item_name.split(" ", 2);
                        if (parts[0].length() > 19) {
                            String str_first = parts[0].substring(0, 19);
                            String str_next = parts[0].substring(19, parts[0].length());
                            printables.add(new Printable.PrintableBuilder()
                                    .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                    .setText(i + createSpace11(Sl_NO, String.valueOf(i).length(), false) + str_first + createSpace11(COMPANY_ITEM_DESCRIPTION, str_first.length(), false) +
                                            order.getQty() + createSpace11(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                            price + createSpaceAmtPrinter(String.format("%.2f", order.getPrice()).length(), String.format("%.2f", order.getGrandTotal()).length()) +
                                            totalprice)
                                    .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())

                                    .build());
                            printables.add(new Printable.PrintableBuilder()
                                    .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                    .setText(createSpace11(Sl_NO, 0, false) + str_next + createSpace11(COMPANY_ITEM_DESCRIPTION, str_next.length(), false))
                                    .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                    .setNewLinesAfter(2)
                                    .build());
                        } else {
                            printables.add(new Printable.PrintableBuilder()
                                    .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                    .setText(i + createSpace11(Sl_NO, String.valueOf(i).length(), false) + parts[0] + createSpace11(COMPANY_ITEM_DESCRIPTION, parts[0].length(), false) +
                                            order.getQty() + createSpace11(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                            price + createSpaceAmtPrinter(String.format("%.2f", order.getPrice()).length(), String.format("%.2f", order.getGrandTotal()).length()) +
                                            totalprice)
                                    .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())

                                    .build());
                        }
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(createSpace11(Sl_NO, 0, false) + parts[1] + createSpace11(COMPANY_ITEM_DESCRIPTION, parts[1].length(), false))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(2)
                                .build());
                    }
                }
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                float t1 = invTransactionMaster.getItemTotalAmount();
                int qty = invTransactionMaster.getTotalQty();

                String str_total_amount = decimalAdjust(t1);
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_TOTAL + createSpaceQty() + qty + createSpaceQtyPrinter(String.valueOf(qty).length(), str_total_amount.length()) + str_total_amount)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                Log.d("Inside :", "Inside getDiscountAmount :" + invTransactionMaster.getDiscountAmount());
                if (invTransactionMaster.getDiscountAmount() != 0) {
                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setText(COMPANY_ITEM_DISCOUNT + createSpacePrinter(COMPANY_ITEM_DISCOUNT.length(), String.valueOf(invTransactionMaster.getDiscountAmount()).length()) + invTransactionMaster.getDiscountAmount())
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

                float t2 = invTransactionMaster.getItemTotalAmount();
                if (invTransactionMaster.getDiscountAmount() != 0)
                    t2 = t2 - invTransactionMaster.getDiscountAmount();

                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_GROSS_AMOUNT + createSpacePrinter(COMPANY_ITEM_GROSS_AMOUNT.length(), String.valueOf(t2).length()) + t2)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());


                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_VAT + createSpacePrinter(COMPANY_ITEM_VAT.length(), String.valueOf(invTransactionMaster.getVatAmount()).length()) + invTransactionMaster.getVatAmount())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_NET_AMOUNT + createSpacePrinter(COMPANY_ITEM_NET_AMOUNT.length(), String.valueOf(invTransactionMaster.getGrandTotal()).length()) + invTransactionMaster.getGrandTotal())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());


                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................................")
                        // .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("No Refunds")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................................")
                        // .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("Thank You !!")
                        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_LARGE())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("Visit Again")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................................")
                        // .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                Printooth.INSTANCE.printer().print(printables);
               /* if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }*/
            }).start();
        }

    }

    private String decimalAdjust(float value) {
        String stringValue = String.valueOf(value);
        if (stringValue.substring(stringValue.length() - 1).equals("0")) {
            return stringValue + 0;
        }
        return null;
    }

    private void getTransactionByInvId(String string) {
        LiveData<TransactionMaster> transactionMasterLiveData = mDatabase.mTransactionMasterDao().findByInvNo(getString(editInvoiceNo));
        transactionMasterLiveData.observe(this, transactionMaster -> {
            if (transactionMaster == null) {
                return;
            }
            invTransactionMaster = transactionMaster;

        });
    }


    private void fillFields(Cashier cashier1) {
        cashier = new Cashier();
        cashier.setCashierName(cashier1.getCashierName());
        cashier.setItemView(cashier1.isItemView());
        cashier.setItemInsert(cashier1.isItemInsert());
        cashier.setItemUpdate(cashier1.isItemUpdate());
        cashier.setItemDelete(cashier1.isItemDelete());
        cashier.setCategoryView(cashier1.isCategoryView());

        cashier.setCategoryInsert(cashier1.isCategoryInsert());
        cashier.setCategoryUpdate(cashier1.isCategoryUpdate());
        cashier.setCategoryDelete(cashier1.isCategoryDelete());
        cashier.setPOSView(cashier1.isPOSView());
        cashier.setPOSInsert(cashier1.isPOSInsert());
        cashier.setPOSPrint(cashier1.isPOSPrint());
        cashier.setPOSDelete(cashier1.isPOSDelete());
        cashier.setInventoryView(cashier1.isInventoryView());
        cashier.setInventoryInsert(cashier1.isInventoryInsert());
        cashier.setInventoryUpdate(cashier1.isInventoryUpdate());
        cashier.setInventoryDelete(cashier1.isInventoryDelete());
        cashier.setDailyReportView(cashier1.isDailyReportView());
        cashier.setDailyReportExport(cashier1.isDailyReportExport());
        cashier.setSaleReportView(cashier1.isSaleReportView());
        cashier.setSaleReportExport(cashier1.isSaleReportExport());
        cashier.setItemReportView(cashier1.isItemReportView());
        cashier.setItemReportExport(cashier1.isItemReportExport());
        cashier.setVatReportView(cashier1.isVatReportView());
        cashier.setVatReportExport(cashier1.isVatReportExport());
        cashier.setCategoryReportView(cashier1.isCategoryReportView());
        cashier.setCategoryReportExport(cashier1.isCategoryReportExport());
        cashier.setInventoryReportView(cashier1.isInventoryReportView());
        cashier.setInventoryReportExport(cashier1.isInventoryReportExport());


    }

    private void getCategoryList() {
        LiveData<List<Category>> categoryLiveList = mDatabase.mCategoryDao().getAllCategory();
        categoryLiveList.observe(this, categories -> {
            if (categories == null || categories.size() == 0) {
                return;
            }
            mCategoryList = categories;
            mSelectedCategory = categories.get(0);
            getItemsOfSelectedCategory();
            setUpSpinner();
        });
    }

    private void getItemsOfSelectedCategory() {
        LiveData<List<Items>> listLiveData = mDatabase.mItemsDao().findItemsByCategoryId(mSelectedCategory.getCategoryId());
        listLiveData.observe(this, items -> {
            mItemList = items;
            setUpRecyclerViews();
        });
    }

    private void setUpRecyclerViews() {
        if (mItemList != null || mItemList.size() > 0) {
            int selectedCategoryId = mSelectedCategory.getCategoryId();
            mItemsAdapter = new POSRecyclerAdapter(mItemList, this);
            addNewItemRecyclerView.setAdapter(mItemsAdapter);
        }
    }

    private void setUpSpinner() {
        if (mCategoryList != null || mCategoryList.size() > 0) {
            CategorySpinnerAdapter<Category> itemSpinnerAdapter = new CategorySpinnerAdapter<>(getContext(), R.id.categoryListItem, mCategoryList);
            addNewItemSpinnerCategory.setAdapter(itemSpinnerAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedCategory = mCategoryList.get(position);
        getItemsOfSelectedCategory();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onSpinnerOpened(Spinner spinner) {
        addNewItemSpinnerArrow.setImageDrawable(arrowUp);
    }

    @Override
    public void onSpinnerClosed(Spinner spinner) {
        addNewItemSpinnerArrow.setImageDrawable(arrowDown);
    }

    @Override
    public void onClickedItem(Items item) {

        if ((isCashier && (cashier == null)) || (isCashier && cashier != null && (!cashier.isPOSInsert())))
            showSnackBar(getView(), "Not Allowed!!", 1000);
        else {
            audioManager.playSoundEffect(SoundEffectConstants.CLICK);
            audioManager.playSoundEffect(SoundEffectConstants.CLICK, 0.5F);


            int mItemCountCount = 0;
            float mTotalItemAmount = 0;
            if (menuReturnClickCount == 1) {

               /* if (mCartItems.size() != 0) {
                    for (int i = 0; i < mCartItems.size(); i++) {
                        mTotalItemAmount = Float.valueOf(getString(totalAmount)) - mCartItems.get(i).getPrice();
                        mItemCountCount = Integer.valueOf(getString(itemCount)) + 1;
                   *//* if (item.getItemId() == mCartItems.get(i).getItemId()) {
                        item.setTotalItemPrice(mCartItems.get(i).getQty() * mCartItems.get(i).getPrice());
                        item.setQty(mCartItems.get(i).getQty());
                    }*//*
                        Log.d("Sale", "Inside sale return8"+mItemCountCount);
                    }
                }*/
                if (mCartItems.size() == 0) {
                    mTotalItemAmount = (item.getPrice());
                    mItemCountCount = 1;
                }else{
                    mTotalItemAmount = Float.valueOf(getString(totalAmount)) - item.getPrice();
                    mItemCountCount = Integer.valueOf(getString(itemCount)) - 1;
                }
                mTotalItemAmount = Constants.round(mTotalItemAmount, 2);
                returnFromList(item);
                setFooterAndVat(item, mTotalItemAmount, mItemCountCount);

                menuReturnClickCount = 0;

            } else {
                if (item.isOpen()) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_layout);
                    // dialog.setTitle("Title...");

                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    EditText editOpenPrice = (EditText) dialog.findViewById(R.id.editOpenPrice);
                    //text.setText("Android custom dialog example!");
                    editOpenPrice.setText(String.valueOf(item.getPrice()));

                    Button dialogButton = (Button) dialog.findViewById(R.id.buttonSave);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getString(editOpenPrice) != null)

                                item.setPrice(Float.parseFloat(getString(editOpenPrice)));
                            mItemsAdapter.notifyDataSetChanged();
                            Thread t = new Thread(() -> {
                                mDatabase.mItemsDao().editItemsPriceById(Float.parseFloat(getString(editOpenPrice)),item.getItemId());
                            });
                            t.start();

                            dialog.dismiss();


                 /*           boolean isItemReturn = true;
                            if (mCartItems.size() != 0) {
                                for (int i = 0; i < mCartItems.size(); i++) {
                                    if (item.getItemId() == mCartItems.get(i).getItemId() && !mCartItems.get(i).isSaleReturned()) {
                                        Log.d("Sale", "Inside sale return1");
                                        isItemReturn = false;
                                        item.setSaleReturned(false);
                                        addItem(item);
                                        return;
                                    }
                                    if (!isItemReturn) {
                                        Log.d("Sale", "Inside sale return2");
                                        item.setQty(0);
                                        item.setSaleReturned(false);
                                        addItem(item);
                                    }

                                }


                            }

                            item.setQty(0);
                            item.setSaleReturned(false);*/
                            addItem(item);
                        }
                    });

                    dialog.show();
                } else {
     /*               boolean isItemReturn = true;
                    if (mCartItems.size() != 0) {
                        for (int i = 0; i < mCartItems.size(); i++) {
                            if (item.getItemId() == mCartItems.get(i).getItemId() && !mCartItems.get(i).isSaleReturned()) {
                                Log.d("Sale","Inside sale return1");
                                isItemReturn = false;
                                item.setSaleReturned(false);
                                addItem(item);
                                return;
                            }
                            if(!isItemReturn) {
                                Log.d("Sale", "Inside sale return2");
                               // item.setQty(0);
                                item.setSaleReturned(false);
                                addItem(item);
                            }

                        }


                    }

                    //item.setQty(0);
                   item.setSaleReturned(false);*/
                    addItem(item);
                }
            }
        }
    }


    void setFooterAndVat(Items item, float mTotalItemAmount, int mItemCountCount) {
        Log.d("Sale", "Inside sale return9"+mItemCountCount);

        calculateVat(item.getVat(), item.getPrice());
        currentItemAndCount.setText(item.getItemName() + " x " + 1);
        totalAmount.setText(String.format("%.2f", mTotalItemAmount));
        itemCount.setText(String.valueOf(mItemCountCount));
    }

    void calculateVat(float vat, float total) {
        vat = total * (vat / 100);
        currentVat = currentVat + vat;
        currentTotal = currentTotal + total;
        currentTotal = currentVat + currentTotal;
        currentTotal = Constants.round(currentTotal, 2);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private String createSpaceQty() {

        int num = 18;

        return new String(new char[num]).replace('\0', ' ');
    }

    private String createSpaceQtyPrinter(int firstLength, int secondLegth) {
        //   int num = 32 - firstLength;
        int num = 25 - firstLength;
        num = num - secondLegth;
        return new String(new char[num]).replace('\0', ' ');
    }

    private String createSpaceQty(int firstLength, int secondLegth) {
        //   int num = 32 - firstLength;
        int num = 19 - firstLength;
        num = num - secondLegth;
        return new String(new char[num]).replace('\0', ' ');
    }

    private String createSpaceAmtPrinter(int firstLength, int secondLegth) {
        //   int num = 32 - firstLength;
        int num = 20 - firstLength;
        num = num - secondLegth;
        return new String(new char[num]).replace('\0', ' ');
    }

    private String createSpacePrinter(int firstLength, int secondLegth) {
        //   int num = 32 - firstLength;
        int num = 48 - firstLength;
        num = num - secondLegth;
        return new String(new char[num]).replace('\0', ' ');
    }

    private String createSpace3(String item, int length, boolean isBluetooth) {
        int total;
        int num;
        switch (item) {
            case Sl_NO:
                total = !isBluetooth ? 3 : 5;
                num = 1;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_DESCRIPTION:
                total = !isBluetooth ? 20 : 48;
                num = 16;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_QUANTITY:
                total = !isBluetooth ? 5 : 7;
                num = 2;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_PRICE:
                total = !isBluetooth ? 10 : 15;
                num = 9;
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

                num = 3 - length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_DESCRIPTION:

                num = 20 - length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_QUANTITY:

                num = 5 - length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_PRICE:

                num = 10 - length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_AMOUNT:

                num = 10 - length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
        }
        return null;
    }

    void addItem(Items item) {
        int mItemCountCount = 0;
        float mTotalItemAmount = 0;
      /*  for (int i = 0; i < mCartItems.size(); i++) {
            Log.d("Sale", "Inside addItem555");
            mTotalItemAmount = Float.valueOf(getString(totalAmount)) + mCartItems.get(i).getPrice();
            mItemCountCount = Integer.valueOf(getString(itemCount)) + 1;
           *//* if (item.getItemId() == mCartItems.get(i).getItemId() && !mCartItems.get(i).isSaleReturned()) {
                item.setTotalItemPrice(mCartItems.get(i).getQty() * mCartItems.get(i).getPrice());
                item.setQty(mCartItems.get(i).getQty());
            }*//*
        }*/

        if (mCartItems.size() == 0) {
            mTotalItemAmount = item.getPrice();
            mItemCountCount = 1;
        }else{
            mTotalItemAmount = Float.valueOf(getString(totalAmount)) + item.getPrice();
            mItemCountCount = Integer.valueOf(getString(itemCount)) + 1;
        }
        mTotalItemAmount = Constants.round(mTotalItemAmount, 2);
        insertToList(item);
        setFooterAndVat(item, mTotalItemAmount, mItemCountCount);
    }

    void insertToList(Items item1) {
        Items item = new Items();
        item.setQty(item1.getQty());
        item.setPrice(item1.getPrice());
        item.setItemId(item1.getItemId());
        item.setItemName(item1.getItemName());
        item.setOpen(item1.isOpen());
        item.setVat(item1.getVat());
        item.setCategoryId(item1.getCategoryId());
        item.setImagePath(item1.getImagePath());
        item.setChecked(item1.isChecked());
        Log.d("Sale", "Inside sale return"+item.getQty());
        int qty = item.getQty();

        qty += 1;
        float price = item1.getPrice() * qty;
        int saleReturn = 0;
        if (mCartItems.size() != 0) {
            for (int i = 0; i < mCartItems.size(); i++) {

                if (item.getItemId() == mCartItems.get(i).getItemId() && !mCartItems.get(i).isSaleReturned()) {
                    Log.d("Sale", "Inside sale return1"+item.getQty());
                    qty = mCartItems.get(i).getQty() + 1;
                    price = qty * mCartItems.get(i).getPrice();
                    saleReturn = 1;
                    mCartItems.get(i).setTotalItemPrice(price);
                    mCartItems.get(i).setQty(qty);
                    mCartItems.get(i).setSaleReturned(false);
                    return;
                }

                }
            if (saleReturn != 1) {
                Log.d("Sale", "Inside sale return2"+item.getPrice());

item.setTotalItemPrice(price);
                item.setPrice(price);
                item.setQty(1);
                item.setSaleReturned(false);
                mCartItems.add(item);
            }
           /* item.setQty(1);
            item.setTotalItemPrice(price);
            item.setSaleReturned(false);
            mCartItems.add(item);*/
        } else {
            Log.d("Sale", "Inside sale return3"+item.getQty());
            item.setQty(1);
            item.setTotalItemPrice(item.getPrice());
            item.setSaleReturned(false);
            mCartItems.add(item);
        }
    }

    void returnFromList(Items item1) {
        Log.d("Sale", "Inside sale return4"+item1.getQty());
Items item = new Items();
item.setQty(item1.getQty());
        item.setPrice(item1.getPrice());
        item.setItemId(item1.getItemId());
        item.setItemName(item1.getItemName());
        item.setOpen(item1.isOpen());
        item.setVat(item1.getVat());
        item.setCategoryId(item1.getCategoryId());
        item.setImagePath(item1.getImagePath());
        item.setChecked(item1.isChecked());
//item = item1;
        int qty = -1;
        float price = item.getPrice() * qty;
        item.setQty(qty);
        item.setTotalItemPrice(price);
        item.setSaleReturned(true);
        mCartItems.add(item);
        // mCartItemsForSaleReturn.add(item);
       // item1.setQty(0);
        Log.d("Sale", "Inside sale return5"+item1.getQty());
        Log.d("Sale", "Inside sale return6"+item.getQty());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_new_item, menu);
    }

    int menuClickCount = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_print:
                menuClickCount++;
                if (menuClickCount % 2 == 0) {

                    editInvoiceNo.setVisibility(View.GONE);
                    btnInvoicePrint.setVisibility(View.GONE);
                } else {

                    editInvoiceNo.setVisibility(View.VISIBLE);
                    btnInvoicePrint.setVisibility(View.VISIBLE);

                }

                break;
            case R.id.sale_return:
                menuReturnClickCount++;
                if (menuReturnClickCount > 1)
                    menuReturnClickCount = 0;
                break;
        }

        return true;
    }

    @OnClick(R.id.addNewItemProceed)
    public void onClickedProceed(View view) {
        totalItemCount = Integer.valueOf(getString(itemCount));
        totalItemPrice = Float.valueOf(getString(totalAmount));
      /*  if (mCartItemsForSaleReturn != null && mCartItemsForSaleReturn.size() != 0) {

            mCartItems.addAll(mCartItemsForSaleReturn);
        }*/
        cartItem.setCartItems(mCartItems);
        Navigation.findNavController(view).navigate(AddNewItemDirections.actionAddNewItemToCheckoutFragment(cartItem));
    }

    @Override
    public void itemChanged(List<Items> list) {
       // mItemList = list;
        int count = 0;
        float total = 0;

        for (Items item : list) {
            count = count + item.getQty();
            total = total + item.getTotalItemPrice();
//            setFooterAndVat(item, total, count);
        }
    }
}
