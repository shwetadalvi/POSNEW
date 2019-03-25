package com.abremiratesintl.KOT.fragments;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.Dialog;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.adapters.InventoryCheckoutAdapter;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.interfaces.ClickListeners.MarkItemListener;
import com.abremiratesintl.KOT.models.BtDevice;
import com.abremiratesintl.KOT.models.CartItems;
import com.abremiratesintl.KOT.models.InventoryMaster;
import com.abremiratesintl.KOT.models.InventoryTransaction;
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

import androidx.navigation.Navigation;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.Unbinder;
import vpos.apipackage.PosApiHelper;

import static android.content.Context.MODE_PRIVATE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ADDRESS;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_DATE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ID_PREF;
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
import static com.abremiratesintl.KOT.utils.Constants.REQUEST_CODE_ENABLE_BLUETOOTH;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryCheckoutFragment extends BaseFragment implements ClickListeners.CheckoutCountClickListeners, TextWatcher, ClickListeners.BtResponseListener, AdapterView.OnItemSelectedListener, CustomSpinner.OnSpinnerEventsListener {

    private static MarkItemListener mMarkItemListener;
    @BindView(R.id.checkout_recycler)
    RecyclerView mCheckoutRecyclerView;
    @BindView(R.id.spinner_arrow)
    ImageView mSpinnerArrow;
    @BindDrawable(R.drawable.ic_arrow_down)
    Drawable icDown;
    @BindDrawable(R.drawable.ic_arrow_up)
    Drawable icUp;
    @BindView(R.id.footer_total)
    TextView mFooterTotal;
    @BindView(R.id.footer_vat)
    TextView mFooterVat;
    @BindView(R.id.discount_footer)
    TextView mFooterDiscount;
    @BindView(R.id.checkBoxPercentage)
    CheckBox mCheckBoxPercentage;
    @BindView(R.id.spinner1)
    Spinner mSpinner;
    @BindView(R.id.edtChange)
    EditText edtChange;
    @BindView(R.id.edtBalance)
    EditText edtBalance;

    @BindView(R.id.cash)
    EditText mCash;
    @BindView(R.id.card)
    EditText mCard;
    @BindView(R.id.buttonSubmit)
    Button buttonSubmit;

    private AppDatabase mDatabase;
    private PrefUtils mPrefUtils;
    private Unbinder mUnbinder;
    private List<Items> mItemsList;
    private InventoryCheckoutAdapter mCheckoutAdapter;
    private AddNewItem mAddNewItem;
    private CartItems mCart;
    private String newInvoiceNo;
    private int ret;
    private int BatteryV;
    private boolean mIsPercentage;
    private String selectedItem = "CASH";
    private int btnCounter = 0;
    private boolean disableDelete = false;
    /*
   * private String decimalAdjust(float value) {
       String stringValue = String.valueOf(value);
       if (stringValue.substring(stringValue.length() - 1).equals("0")) {
           return stringValue + 0;
       }
       if (stringValue.contains(".")) {
           String stringAfterDecimalPoint = stringValue.split(".")[1];
           if ((stringAfterDecimalPoint.length() > 2) && Float.parseFloat(stringValue.substring(stringValue.length() - 1)) > 5) {
               String stringAdjust = stringValue.substring(0, stringValue.length() - 2);
               int adjust = Integer.parseInt(stringValue.substring(stringValue.length() - 2)) + 1;
               return stringValue.split(".")[0]+"."+st
           }
       }
       return null;
   }
   * */

    static ClickListeners.OnItemChangedListener mOnItemChangedListener;
    private int RESULT_CODE = 0;
    private int voltage_level;
    private BluetoothAdapter bluetoothAdapter;

    String[] items = new String[]{"CASH", "CARD", "CASH+CARD"};

    public InventoryCheckoutFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_inventory_checkout, container, false);
        mPrefUtils = new PrefUtils(getContext());
        mDatabase = AppDatabase.getInstance(getContext());
        mUnbinder = ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).changeTitle("Inventory");
        //  ((MainActivity) getActivity()).getToolbar().setNavigationIcon(R.drawable.plus_icon);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(((MainActivity) getActivity()), android.R.layout.simple_spinner_dropdown_item, items);
        mSpinner.setAdapter(adapter);

        // mSpinner.setSelection(1);

        btnCounter = 0;
        disableDelete = false;
        mSpinner.setOnItemSelectedListener(this);
        setHasOptionsMenu(true);
        InventoryCheckoutFragmentArgs args = InventoryCheckoutFragmentArgs.fromBundle(getArguments());
        mCart = args.getInventoryCart();
        mItemsList = mCart.getCartItems();
        getArguments().remove("cart");
        mFooterDiscount.addTextChangedListener(this);

        setUpRecyclerView();
        calculateTotal();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTransactions(mItemsList);
            }
        });
        return view;

    }


    void setUpRecyclerView() {

        mCheckoutAdapter = new InventoryCheckoutAdapter(mItemsList, this, this);
        mCheckoutRecyclerView.setAdapter(mCheckoutAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.inventory_menu, menu);
    }

    /*  @Override
    public void onPrepareOptionsMenu(Menu menu) {
         super.onPrepareOptionsMenu(menu);
         if (disableDelete) {
             menu.getItem(2).setEnabled(false);
             menu.getItem(2).getIcon().setAlpha(130);
         }
     }
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      /*  ImageButton locButton = (ImageButton) menu.findItem(R.id.menu_find).getActionView();
        locButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                createPopup();
                mQuickAction.show(v);
            }
        });
        return true;*/
        switch (item.getItemId()) {
            case R.id.menu_print:

                saveCart();
                break;
            case R.id.delete:
                //   if (btnCounter == 1)
                mCheckoutAdapter.deleteCheck();
             /*   else{
                    item.setEnabled(false);
                    item.getIcon().setAlpha(130);
                }*/

                break;
            case R.id.add_new:
                //  Navigation.findNavController(item.getActionView().findViewById(R.id.add_new)).navigate(R.id.action_checkoutFragment_to_addNewItem);
                mItemsList.clear();
                setUpRecyclerView();
                calculateTotal();
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
                break;
        }
        return true;
    }

    public void setItemsList(List<Items> itemsList) {
        mItemsList = itemsList;
        calculateTotal();
        if (mOnItemChangedListener != null)
            mOnItemChangedListener.itemChanged(itemsList);
    }

    @Override
    public void onResume() {
        super.onResume();
        calculateTotal();
    }

    public void setOnItemChangedListener(ClickListeners.OnItemChangedListener onItemChangedListener) {
        mOnItemChangedListener = onItemChangedListener;
    }

    private void saveCart() {
        btnCounter++;
        if (btnCounter == 1) {
            disableDelete = true;
            insertTransactions(mItemsList);
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
                    printReciptBluetooth(newInvoiceNo);
                    break;
                case "2":
                    printReciptBuiltin(newInvoiceNo);
                    break;
                case "3":
                    showRecipt(newInvoiceNo);
                    break;
            }
        } else {
            disableDelete = false;
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
                    printReciptBluetooth(newInvoiceNo);
                    break;
                case "2":
                    printReciptBuiltin(newInvoiceNo);
                    break;
                case "3":
                    showRecipt(newInvoiceNo);
                    break;
            }
        }
    }

    float getTotalItemAmount() {
        float total = 0;
        for (Items item : mItemsList) {
            total = total + item.getTotalItemPrice();
        }
        return Constants.round(total, 2);
    }

    private void insertTransactions(List<Items> itemsList) {
        InventoryMaster inventoryMaster = new InventoryMaster();
        float itemAmount = getTotalItemAmount();
        float grantTotal = Float.parseFloat(getString(mFooterTotal));
        float itemVat = Float.parseFloat(getString(mFooterVat));
        float itemDiscount = Float.parseFloat((getString(mFooterDiscount).isEmpty() ? "0" : getString(mFooterVat)));
        boolean isSale = true;
      /*  int isCashCardOrBoth = 1;
        float cash = Float.parseFloat((getString(mCash).isEmpty() ? "0" : getString(mCash)));
        float card = Float.parseFloat((getString(mCard).isEmpty() ? "0" : getString(mCard)));
        String ptype = (mSpinner.getSelectedItem().toString());
        String invoiceDate = Constants.getCurrentDate();*/


        SharedPreferences preferences = getActivity().getSharedPreferences("inventory", MODE_PRIVATE);


        inventoryMaster.setItemTotalAmount(grantTotal);
        inventoryMaster.setCreatedDate(Constants.getCurrentDateTime());
        inventoryMaster.setPurchaseDate(preferences.getString("date", ""));
        inventoryMaster.setRefference(preferences.getString("refference", ""));
        inventoryMaster.setSupplier(preferences.getString("supplier", ""));
        inventoryMaster.setVat(preferences.getString("vat", ""));
        newInvoiceNo = preferences.getString("id", "");
//        LiveData<Integer> t.ransactionMasterLiveData = mDatabase.mTransactionMasterDao().findTransMasterOfMaxId();
        Log.e("TransId in shared :","Id "+newInvoiceNo);
        new Thread(() -> {
            int transactionMasterMaxId = (mDatabase.mTransactionMasterDao().findTransMasterOfMaxId());
            transactionMasterMaxId = transactionMasterMaxId == 0 ? 1 : transactionMasterMaxId + 1;
           // newInvoiceNo = String.valueOf(transactionMasterMaxId);
            Log.e("TransId in insert :","Id "+newInvoiceNo);
            inventoryMaster.setInvoiceNo(newInvoiceNo);
            mDatabase.mInventoryMasterDao().insertNewItems(inventoryMaster);
            for (Items item : mItemsList) {
                //item.getItemName();

              //  insertTransactionMaster(true, item, transactionMasterMaxId);
                insertTransactionMaster(true, item, Integer.parseInt(newInvoiceNo));

            }
            /*DialogFragment dialogFragment = BillSampleDialog.newInstance(newInvoiceNo,mItemsList);
            dialogFragment.show(getFragmentManager(),"dialog");
*/


        }).start();
        SharedPreferences pref = getActivity().getSharedPreferences("inventory", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Inventory Added Successfully");
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

    private void showRecipt(String newInvoiceNo) {

    }

    private void printReciptBluetooth(String newInvoiceNo) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothChecking()) {
            printViaBluetoothPrinter();
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
                        .setText("TRN   : " + COMPANY_TRN)
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
                        .setText(COMPANY_ORDER_NO + newInvoiceNo)
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
                        .setText(COMPANY_ITEM_DESCRIPTION + createSpace(COMPANY_ITEM_DESCRIPTION, COMPANY_ITEM_DESCRIPTION.length(), false) +
                                COMPANY_ITEM_QUANTITY + createSpace(COMPANY_ITEM_QUANTITY, COMPANY_ITEM_QUANTITY.length(), false) +
                                COMPANY_ITEM_PRICE + createSpace(COMPANY_ITEM_PRICE, COMPANY_ITEM_PRICE.length(), false) +
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
                for (Items order : mItemsList) {
                    String price = decimalAdjust(order.getPrice());
                    String totalprice = decimalAdjust(order.getTotalItemPrice());
                    if (price == null) price = String.valueOf(order.getPrice());
                    if (totalprice == null) totalprice = String.valueOf(order.getTotalItemPrice());
                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setText(order.getItemName() + createSpace(COMPANY_ITEM_DESCRIPTION, order.getItemName().length(), false) +
                                    order.getQty() + createSpace(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                    price + createSpace(COMPANY_ITEM_PRICE, String.format("%.2f", order.getPrice()).length(), false) +
                                    totalprice + createSpace(COMPANY_ITEM_AMOUNT, String.format("%.2f", order.getTotalItemPrice()).length(), false))
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
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_TOTAL + createSpace(COMPANY_ITEM_TOTAL.length(), String.format("%.2f", getTotalItemAmount()).length()) + getTotalItemAmount())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                if (!getString(mFooterDiscount).isEmpty()) {
                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setText(COMPANY_ITEM_DISCOUNT + createSpace(COMPANY_ITEM_DISCOUNT.length(), String.valueOf(getString(mFooterDiscount)).length()) + getString(mFooterDiscount))
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
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_GROSS_AMOUNT + createSpace(COMPANY_ITEM_GROSS_AMOUNT.length(), String.format("%.2f", getPriceExcludingVat()).length()) + getPriceExcludingVat())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());

                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_VAT + createSpace(COMPANY_ITEM_VAT.length(), String.valueOf(getString(mFooterVat)).length()) + getString(mFooterVat))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_NET_AMOUNT + createSpace(COMPANY_ITEM_NET_AMOUNT.length(), String.valueOf(getString(mFooterTotal)).length()) + getString(mFooterTotal))
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
                    dialog.setInteractorToFragment(InventoryCheckoutFragment.this::interacterOne);
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

    private String centerAlignment(int totalLength, String item) {
        int length = item.length();
        int offset = (totalLength - length) / 2;
        offset = (offset % 2 == 0) ? offset : offset + 1;
        return new String(new char[offset]).replace('\0', ' ');
    }

    private String decimalAdjust(float value) {
        String stringValue = String.valueOf(value);
        if (stringValue.substring(stringValue.length() - 1).equals("0")) {
            return stringValue + 0;
        }
        return null;
    }

    private void printReciptBuiltin(String newInvoiceNo) {
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
        /*int printStat = posApiHelper.PrintCheckStatus();
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
        } else {*/
        thread.start();
//        }
    }

    float getPriceExcludingVat() {
        float vat = Float.parseFloat(getString(mFooterVat));
        float total = Float.parseFloat(getString(mFooterTotal));
        return Constants.round(total - vat, 2);
    }

    private String createSpace(String item, int length, boolean isBluetooth) {
        int total;
        int num;
        switch (item) {
            case COMPANY_ITEM_DESCRIPTION:
                total = !isBluetooth ? 19 : 48;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_QUANTITY:
                total = !isBluetooth ? 7 : 7;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_PRICE:
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

    int getTotalItemCount() {
        int totalCount = 0;
        for (Items item : mItemsList) {
            totalCount = totalCount + item.getQty();
        }
        return totalCount;
    }

    private void insertTransactionMaster(boolean b, Items item, Integer finalTransactionMasterMaxId) {
        if (b) {
            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setTransMasterId(finalTransactionMasterMaxId);
            transaction.setVat(item.getVat());
            transaction.setItemId(item.getItemId());
            transaction.setQty(item.getQty());
            transaction.setPrice(item.getPrice());
            transaction.setItemName(item.getItemName());
            transaction.setInvoiceDate(Constants.getCurrentDate());
            transaction.setGrandTotal(item.getQty() * item.getPrice());


            String category = mDatabase.mCategoryDao().getCategoryById(item.getCategoryId());
            transaction.setCategory(category);


           /* Completable.fromAction(() -> mDatabase.mTransactionDao().insertNewItems(transaction))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                    }, throwable -> Log.e("INSERTION MASTER2", "Count   "));
        } else {

        }*/
            mDatabase.mInventoryTransactionDao().insertNewItems(transaction);

        }
    }

    @Override
    public void onClickedPlus(Items item) {
        int qty = item.getQty();
        qty += 1;
        float price = item.getPrice() * qty;
        for (int i = 0; i < mCart.getCartItems().size(); i++) {
            if (item.getItemId() == mCart.getCartItems().get(i).getItemId()) {
                mCart.getCartItems().get(i).setTotalItemPrice(price);
                mCart.getCartItems().get(i).setQty(qty);
                mCheckoutAdapter.notifyDataSetChanged();
                calculateTotal();
                return;
            }
        }
    }

    @Override
    public void onClickedMinus(Items item) {
        if (item.getQty() > 1) {
            int qty = item.getQty();
            qty -= 1;
            float price = item.getPrice() * qty;
            for (int i = 0; i < mCart.getCartItems().size(); i++) {
                if (item.getItemId() == mCart.getCartItems().get(i).getItemId()) {
                    mCart.getCartItems().get(i).setTotalItemPrice(price);
                    mCart.getCartItems().get(i).setQty(qty);
                    mCheckoutAdapter.notifyDataSetChanged();
                    calculateTotal();
                    return;
                }
            }
        } else {
            showSnackBar(getView(), "Cannot be empty", 1000);
        }
    }

    float calculateTotal() {
        float total = 0;
        float vat = 0;
        for (Items items : mItemsList) {
            total = total + items.getTotalItemPrice();
            vat = vat + calculateVat(items.getVat(), items.getPrice(), items.getQty());
        }
        String disString = String.format("%.2f", Float.valueOf((getString(mFooterDiscount).isEmpty() ? "0" : getString(mFooterDiscount))));
        float discount = 0;
        if (!mIsPercentage) {
            discount = Float.parseFloat(disString);
            total = total - discount;
        } else {
            discount = Float.parseFloat(disString);
            discount = total * (discount / 100);
            total = total - discount;
        }
        Constants.round(vat, 2);
        Constants.round(total, 2);
        Constants.round(discount, 2);
        updateFooters(vat, discount, total + vat);
        return Constants.round(total, 2);
    }

    float calculateVat(float vat, float price, int qty) {
        if (qty == 0) {
            if (mPrefUtils.getBooleanPrefrence(DEAFULT_PREFS, Constants.VAT_EXCLUSIVE, true))
                price = price * vat / 100;
            else
                price = price * vat / (100 + vat);
            return Constants.round(price, 2);
        } else {
            if (mPrefUtils.getBooleanPrefrence(DEAFULT_PREFS, Constants.VAT_EXCLUSIVE, true))
                price = (qty * price) * vat / 100;
            else
                price = (qty * price) * vat / (100 + vat);
            return Constants.round(price, 2);
        }
    }

    void updateFooters(float vat, float discount, float total) {
        String totalStirng = String.valueOf(Constants.round(total, 2));
        String vatString = String.valueOf(Constants.round(vat, 2));
        if (totalStirng == null) totalStirng = String.valueOf(total);
        if (vatString == null) vatString = String.valueOf(vat);
        mFooterTotal.setText(totalStirng);
        mFooterVat.setText(vatString);
        mCash.setText(totalStirng);

//        mFooterDiscount.setText(String.valueOf(discount));
    }

    @OnCheckedChanged(R.id.checkBoxPercentage)
    void onPercentageSelected(CompoundButton button, boolean checked) {
        mIsPercentage = checked;
        calculateTotal();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals("0.0")) {
            calculateTotal();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void interacterOne(BtDevice btDevice) {
        Printooth.INSTANCE.setPrinter(btDevice.getDeviceName(), btDevice.getDeviceMac());
        printViaBluetoothPrinter();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedItem = items[position];
        String total = mFooterTotal.getText().toString();

        if (selectedItem == "CASH") {
            mCash.setText(total);
            mCash.setEnabled(false);
            mCard.setEnabled(false);
            mCard.setText("");

        }
        if (selectedItem == "CARD") {
            mCard.setText(total);
            mCash.setEnabled(false);
            mCard.setEnabled(false);
            mCash.setText("");
        }
        if (selectedItem == "CASH+CARD") {
            mCash.requestFocus();
            //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            mCash.setEnabled(true);
            mCard.setEnabled(true);


            mCard.setText("");
            mCash.setText("");
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

    public class BatteryReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            voltage_level = intent.getExtras().getInt("level");// ï¿½ï¿½Ãµï¿½Ç°ï¿½ï¿½ï¿½ï¿½
            Log.e("wbw", "current  = " + voltage_level);
            BatteryV = intent.getIntExtra("voltage", 0);  //ç”µæ± ç”µåŽ‹
            Log.e("wbw", "BatteryV  = " + BatteryV);
            Log.e("wbw", "V  = " + BatteryV * 2 / 100);
            //	m_voltage = (int) (65+19*voltage_level/100); //æ”¾å¤§åå€
            //   Log.e("wbw","m_voltage  = " + m_voltage );
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }


}
