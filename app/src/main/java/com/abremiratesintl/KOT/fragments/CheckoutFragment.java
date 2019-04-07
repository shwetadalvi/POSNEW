package com.abremiratesintl.KOT.fragments;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.abremiratesintl.KOT.adapters.CheckoutAdapter;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.interfaces.ClickListeners.MarkItemListener;
import com.abremiratesintl.KOT.models.BtDevice;
import com.abremiratesintl.KOT.models.CartItems;
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

import static com.abremiratesintl.KOT.utils.Constants.CHANGE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_ADDRESS;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_DATE;
import static com.abremiratesintl.KOT.utils.Constants.COMPANY_Email;
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
import static com.abremiratesintl.KOT.utils.Constants.PAID_AMOUNT;
import static com.abremiratesintl.KOT.utils.Constants.REQUEST_CODE_ENABLE_BLUETOOTH;
import static com.abremiratesintl.KOT.utils.Constants.Sl_NO;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckoutFragment extends BaseFragment implements ClickListeners.CheckoutCountClickListeners, TextWatcher, ClickListeners.BtResponseListener, AdapterView.OnItemSelectedListener, CustomSpinner.OnSpinnerEventsListener {

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
    @BindView(R.id.textTotal)
    TextView textTotal;
    @BindView(R.id.cash)
    EditText mCash;
    @BindView(R.id.card)
    EditText mCard;

    private AppDatabase mDatabase;
    private PrefUtils mPrefUtils;
    private Unbinder mUnbinder;
    private List<Items> mItemsList;
    private CheckoutAdapter mCheckoutAdapter;
    private AddNewItem mAddNewItem;
    private CartItems mCart;
    private String newInvoiceNo;
    private int ret;
    private int BatteryV;
    private boolean mIsPercentage;
    private String selectedItem = "CASH";
    private int btnCounter = 0;
    //private boolean disableDelete = false;
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

    public CheckoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        mPrefUtils = new PrefUtils(getContext());
        mDatabase = AppDatabase.getInstance(getContext());
        mUnbinder = ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).changeTitle("CHECKOUT");
        //  ((MainActivity) getActivity()).getToolbar().setNavigationIcon(R.drawable.plus_icon);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(((MainActivity) getActivity()), android.R.layout.simple_spinner_dropdown_item, items);
        mSpinner.setAdapter(adapter);

        // mSpinner.setSelection(1);

        btnCounter = 0;
      //  disableDelete = false;
        mSpinner.setOnItemSelectedListener(this);
        setHasOptionsMenu(true);
        CheckoutFragmentArgs args = CheckoutFragmentArgs.fromBundle(getArguments());
        mCart = args.getCart();
        mItemsList = mCart.getCartItems();
        getArguments().remove("cart");
        mFooterDiscount.addTextChangedListener(this);

        setUpRecyclerView();
        calculateTotal();

        /*mCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    if (selectedItem == "CASH+CARD") {
                        // if (mCash.hasFocus()){
                        mCash.setText("");
                        //  }
                        float total = Float.parseFloat(mFooterTotal.getText().toString());
                        float card = Float.parseFloat(mCard.getText().toString());

                        if (card > total) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                            builder1.setMessage("Card amount cant not be greater than total");
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
                        float cash = total - card;
                        mCash.setText(String.valueOf(cash));
                    }
                }
            }
        });*/

        mCash.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    if (selectedItem == "CASH+CARD") {
                        if (mCard.hasFocus()) {
                            mCard.setText("");
                        }
                        float total = Float.parseFloat(mFooterTotal.getText().toString());

                        float cash = Float.parseFloat(mCash.getText().toString());

                        if (cash > total) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                            builder1.setMessage("Card amount cant not be greater than total");
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
                        float card = total - cash;
                        mCard.setText(String.valueOf(card));
                    }
                }

            }
        });
        edtChange.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {

                    float total = Float.parseFloat(mFooterTotal.getText().toString());
                    float change = Float.parseFloat(edtChange.getText().toString());


                    float balance = change - total;
                    if (balance > 0)
                        edtBalance.setText(String.valueOf(balance));
                }
            }

        });

        return view;

    }


    void setUpRecyclerView() {
        mCheckoutAdapter = new CheckoutAdapter(mItemsList, this, this);
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
        inflater.inflate(R.menu.print_menu, menu);
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
                if(!Constants.disableDelete)
                mCheckoutAdapter.deleteCheck();
             /*   else{
                    item.setEnabled(false);
                    item.getIcon().setAlpha(130);
                }*/

                break;
            case R.id.add_new:
                //  Navigation.findNavController(item.getActionView().findViewById(R.id.add_new)).navigate(R.id.action_checkoutFragment_to_addNewItem);
                Constants.disableDelete = false;
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
            Constants.disableDelete = true;
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
        }
        else {

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
        TransactionMaster transactionMaster = new TransactionMaster();
        float itemAmount = getTotalItemAmount();
        float grantTotal = Float.parseFloat(getString(mFooterTotal));
        float itemVat = Float.parseFloat(getString(mFooterVat));
        float itemDiscount = Float.parseFloat((getString(mFooterDiscount).isEmpty() ? "0" : getString(mFooterVat)));
        boolean isSale = true;
        int isCashCardOrBoth = 1;
        float cash = Float.parseFloat((getString(mCash).isEmpty() ? "0" : getString(mCash)));
        float card = Float.parseFloat((getString(mCard).isEmpty() ? "0" : getString(mCard)));
        String ptype = (mSpinner.getSelectedItem().toString());
        String invoiceDate = Constants.getCurrentDate();
        transactionMaster.setDiscountAmount(itemDiscount);
        transactionMaster.setInvoiceDate(invoiceDate);
        transactionMaster.setSale(isSale);
        transactionMaster.setIsCashBankOrBoth(isCashCardOrBoth);
        transactionMaster.setVatAmount(itemVat);
        transactionMaster.setGrandTotal(grantTotal);
        transactionMaster.setItemTotalAmount(itemAmount);
        transactionMaster.setTotalQty(getTotalItemCount());
        transactionMaster.setCreatedDate(Constants.getCurrentDateTime());
        transactionMaster.setCash(cash);
        transactionMaster.setCard(card);
        transactionMaster.setType(ptype);
//        LiveData<Integer> t.ransactionMasterLiveData = mDatabase.mTransactionMasterDao().findTransMasterOfMaxId();
        new Thread(() -> {
            int transactionMasterMaxId = (mDatabase.mTransactionMasterDao().findTransMasterOfMaxId());
            transactionMasterMaxId = transactionMasterMaxId == 0 ? 1 : transactionMasterMaxId + 1;
         //   newInvoiceNo = mPrefUtils.getStringPrefrence(DEAFULT_PREFS, COMPANY_PREFIX, "SJ") + " "+transactionMasterMaxId;
            newInvoiceNo = String.valueOf(transactionMasterMaxId);
            transactionMaster.setInvoiceNo(newInvoiceNo);
            mDatabase.mTransactionMasterDao().insertNewItems(transactionMaster);
            for (Items item : mItemsList) {item.getItemName();

                insertTransactionMaster(true, item, transactionMasterMaxId);

            }
            /*DialogFragment dialogFragment = BillSampleDialog.newInstance(newInvoiceNo,mItemsList);
            dialogFragment.show(getFragmentManager(),"dialog");
*/


        }).start();
    }

    private void showRecipt(String newInvoiceNo) {

    }

    private void printReciptBluetooth(String newInvoiceNo) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothChecking()) {
           /* if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.PRINTER_TYPE,Constants.FEASYCOM).equals(Constants.FEASYCOM))
                printViaBluetoothPrinter();
            else*/
                printViaBluetoothPrinter1();
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
                        .setText(COMPANY_TAX)
                        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("TRN : "+COMPANY_TRN)
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
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
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
              /*  printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(Sl_NO  + createSpace(Sl_NO, Sl_NO.length(), false) +COMPANY_ITEM_DESCRIPTION+ createSpace(COMPANY_ITEM_DESCRIPTION, COMPANY_ITEM_DESCRIPTION.length(), false) +
                                COMPANY_ITEM_QUANTITY + createSpace(COMPANY_ITEM_QUANTITY, COMPANY_ITEM_QUANTITY.length(), false) +
                                COMPANY_ITEM_PRICE + createSpace(COMPANY_ITEM_PRICE, COMPANY_ITEM_PRICE.length(), false) +
                                COMPANY_ITEM_AMOUNT + createSpace(COMPANY_ITEM_AMOUNT, COMPANY_ITEM_AMOUNT.length(), false))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());*/
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
                        .setText(Sl_NO  + createSpace3(Sl_NO, Sl_NO.length(), false) +COMPANY_ITEM_DESCRIPTION+ createSpace3(COMPANY_ITEM_DESCRIPTION, COMPANY_ITEM_DESCRIPTION.length(), false) +
                                COMPANY_ITEM_QUANTITY + createSpace3(COMPANY_ITEM_QUANTITY, COMPANY_ITEM_QUANTITY.length(), false) +
                                COMPANY_ITEM_PRICE + createSpace3(COMPANY_ITEM_PRICE, COMPANY_ITEM_PRICE.length(), false) +
                                COMPANY_ITEM_AMOUNT + createSpace3(COMPANY_ITEM_AMOUNT, COMPANY_ITEM_AMOUNT.length(), false))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                int i = 0;
                String[] items ={};
                for (Items order : mItemsList) {

                    String item_name = order.getItemName();

                    if(item_name.length() > 19)
                        items = item_name.split(" ");
                    Log.e("Print :","Item length :"+items.length);
                   /* for(int j = 0 ;j<= items.length;j++){

                    }*/
                    i+= 1;
                    String price = decimalAdjust(order.getPrice());
                    String totalprice = decimalAdjust(order.getTotalItemPrice());
                    if (price == null) price = String.valueOf(order.getPrice());
                    if (totalprice == null) totalprice = String.valueOf(order.getTotalItemPrice());

                    if(item_name.length() <= 19 && items.length == 0){
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(i + createSpace11(Sl_NO, String.valueOf(i).length(), false) + item_name + createSpace11(COMPANY_ITEM_DESCRIPTION, item_name.length(), false) +
                                        order.getQty() + createSpace11(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                        price+ createSpaceAmtPrinter(String.format("%.2f", order.getPrice()).length(), String.format("%.2f", order.getTotalItemPrice()).length())
                                        +totalprice )
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(1)
                                .build());
                    }else if(item_name.length() > 19 && items.length == 1) {
                        String str_first = item_name.substring(0,19);
                        String str_next = item_name.substring(19,item_name.length());
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(i + createSpace11(Sl_NO, String.valueOf(i).length(), false) + str_first + createSpace11(COMPANY_ITEM_DESCRIPTION, str_first.length(), false) +
                                        order.getQty() + createSpace11(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                        price+ createSpaceAmtPrinter(String.format("%.2f", order.getPrice()).length(), String.format("%.2f", order.getTotalItemPrice()).length())+
                                        totalprice)
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())

                                .build());
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(createSpace11(Sl_NO, String.valueOf(i).length(), false) + str_next + createSpace11(COMPANY_ITEM_DESCRIPTION, str_next.length(), false))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(1)
                                .build());
                    }else{
                        String parts[] = item_name.split(" ", 2);
                        if(parts[0].length() > 19 ) {
                            String str_first = parts[0].substring(0,19);
                            String str_next = parts[0].substring(19,parts[0].length());
                            printables.add(new Printable.PrintableBuilder()
                                    .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                    .setText(i + createSpace11(Sl_NO, String.valueOf(i).length(), false) + str_first + createSpace11(COMPANY_ITEM_DESCRIPTION, str_first.length(), false) +
                                            order.getQty() + createSpace11(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                            price + createSpaceAmtPrinter(String.format("%.2f", order.getPrice()).length(), String.format("%.2f", order.getTotalItemPrice()).length()) +
                                            totalprice)
                                    .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())

                                    .build());
                            printables.add(new Printable.PrintableBuilder()
                                    .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                    .setText(createSpace11(Sl_NO, String.valueOf(i).length(), false) + str_next + createSpace11(COMPANY_ITEM_DESCRIPTION, str_next.length(), false))
                                    .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                    .setNewLinesAfter(1)
                                    .build());
                        }else {
                            printables.add(new Printable.PrintableBuilder()
                                    .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                    .setText(i + createSpace11(Sl_NO, String.valueOf(i).length(), false) + parts[0] + createSpace11(COMPANY_ITEM_DESCRIPTION, parts[0].length(), false) +
                                            order.getQty() + createSpace11(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                            price+ createSpaceAmtPrinter(String.format("%.2f", order.getPrice()).length(), String.format("%.2f", order.getTotalItemPrice()).length())+
                                            totalprice)
                                    .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())

                                    .build());
                        }
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(createSpace11(Sl_NO, String.valueOf(i).length(), false) + parts[1] + createSpace11(COMPANY_ITEM_DESCRIPTION, parts[1].length(), false))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(1)
                                .build());
                    }
                }
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                float t1 = getTotalItemAmount();
                int qty = getTotalItemQty();
                String str_total_amount = decimalAdjust(t1);
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_TOTAL + createSpaceQty() +getTotalItemQty()+createSpaceQtyPrinter(String.valueOf(qty).length(), str_total_amount.length()) + str_total_amount)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());

                if (!getString(mFooterDiscount).isEmpty()) {
                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setText(COMPANY_ITEM_DISCOUNT + createSpacePrinter(COMPANY_ITEM_DISCOUNT.length(), String.valueOf(getString(mFooterDiscount)).length()) + getString(mFooterDiscount))
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

                float t2 = getPriceExcludingVat();
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_GROSS_AMOUNT + createSpacePrinter(COMPANY_ITEM_GROSS_AMOUNT.length(), String.valueOf(t2).length()) + getPriceExcludingVat())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());

                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_VAT + createSpacePrinter(COMPANY_ITEM_VAT.length(), String.valueOf(getString(mFooterVat)).length()) + getString(mFooterVat))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_NET_AMOUNT + createSpacePrinter(COMPANY_ITEM_NET_AMOUNT.length(), String.valueOf(getString(mFooterTotal)).length()) + getString(mFooterTotal))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());

                if (selectedItem == "CASH"){
                    if(!getString(edtChange).isEmpty()) {
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(PAID_AMOUNT + createSpacePrinter(PAID_AMOUNT.length(), String.valueOf(getString(edtChange)).length()) + getString(edtChange))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(2)
                                .build());

                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(CHANGE + createSpacePrinter(CHANGE.length(), String.valueOf(getString(edtBalance)).length()) + getString(edtBalance))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(2)
                                .build());
                    }
                }
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
                        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("TRN   : "+COMPANY_TRN)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
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
                        .setText("................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())

                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
                        .setText(COMPANY_ORDER_NO + newInvoiceNo)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_DATE + Constants.getCurrentDateTime())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());

                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
              /*  printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(Sl_NO  + createSpace(Sl_NO, Sl_NO.length(), false) +COMPANY_ITEM_DESCRIPTION+ createSpace(COMPANY_ITEM_DESCRIPTION, COMPANY_ITEM_DESCRIPTION.length(), false) +
                                COMPANY_ITEM_QUANTITY + createSpace(COMPANY_ITEM_QUANTITY, COMPANY_ITEM_QUANTITY.length(), false) +
                                COMPANY_ITEM_PRICE + createSpace(COMPANY_ITEM_PRICE, COMPANY_ITEM_PRICE.length(), false) +
                                COMPANY_ITEM_AMOUNT + createSpace(COMPANY_ITEM_AMOUNT, COMPANY_ITEM_AMOUNT.length(), false))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(2)
                        .build());*/
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
                        .setText(Sl_NO  + createSpace33(Sl_NO, Sl_NO.length(), false) +COMPANY_ITEM_DESCRIPTION+ createSpace33(COMPANY_ITEM_DESCRIPTION, COMPANY_ITEM_DESCRIPTION.length(), false) +
                                COMPANY_ITEM_QUANTITY + createSpace33(COMPANY_ITEM_QUANTITY, COMPANY_ITEM_QUANTITY.length(), false) +
                                COMPANY_ITEM_PRICE + createSpace33(COMPANY_ITEM_PRICE, COMPANY_ITEM_PRICE.length(), false) +
                                COMPANY_ITEM_AMOUNT + createSpace33(COMPANY_ITEM_AMOUNT, COMPANY_ITEM_AMOUNT.length(), false))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());

                int i = 0;
                String[] items ={};
                for (Items order : mItemsList) {

                    String item_name = order.getItemName();

                    if(item_name.length() > 9)
                        items = item_name.split(" ");
                    Log.e("Print :","Item length :"+items.length);
                   /* for(int j = 0 ;j<= items.length;j++){

                    }*/
                    i+= 1;
                    String price = decimalAdjust(order.getPrice());
                    String totalprice = decimalAdjust(order.getTotalItemPrice());
                    if (price == null) price = String.valueOf(order.getPrice());
                    if (totalprice == null) totalprice = String.valueOf(order.getTotalItemPrice());
                   /* printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setText(i+createSpace(Sl_NO, String.valueOf(i).length(), false) +order.getItemName() + createSpace(COMPANY_ITEM_DESCRIPTION, order.getItemName().length(), false) +
                                    order.getQty() + createSpace(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                    price + createSpace(COMPANY_ITEM_PRICE, String.format("%.2f", order.getPrice()).length(), false) +
                                    totalprice + createSpace(COMPANY_ITEM_AMOUNT, String.format("%.2f", order.getTotalItemPrice()).length(), false))
                            .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                            .setNewLinesAfter(2)
                            .build());*/
                    if(item_name.length() <= 9 && items.length == 0){
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(i + createSpace(Sl_NO, String.valueOf(i).length(), false) + item_name + createSpace(COMPANY_ITEM_DESCRIPTION, item_name.length(), false) +
                                        order.getQty() + createSpace(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                        price + createSpace(COMPANY_ITEM_PRICE, String.format("%.2f", order.getPrice()).length(), false) +
                                        totalprice + createSpace(COMPANY_ITEM_AMOUNT, String.format("%.2f", order.getTotalItemPrice()).length(), false))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(1)
                                .build());
                    }else if(item_name.length() > 9 && items.length == 1) {
                        String str_first = item_name.substring(0,9);
                        String str_next = item_name.substring(9,item_name.length());
                                printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(i + createSpace(Sl_NO, String.valueOf(i).length(), false) + str_first + createSpace(COMPANY_ITEM_DESCRIPTION, str_first.length(), false) +
                                        order.getQty() + createSpace(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                        price + createSpace(COMPANY_ITEM_PRICE, String.format("%.2f", order.getPrice()).length(), false) +
                                        totalprice + createSpace(COMPANY_ITEM_AMOUNT, String.format("%.2f", order.getTotalItemPrice()).length(), false))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())

                                .build());
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(createSpace(Sl_NO, String.valueOf(i).length(), false) + str_next + createSpace(COMPANY_ITEM_DESCRIPTION, str_next.length(), false))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(1)
                                .build());
                    }else{
                        String parts[] = item_name.split(" ", 2);
                        if(parts[0].length() > 9 ) {
                            String str_first = parts[0].substring(0,9);
                            String str_next = parts[0].substring(9,parts[0].length());
                            printables.add(new Printable.PrintableBuilder()
                                    .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                    .setText(i + createSpace(Sl_NO, String.valueOf(i).length(), false) + str_first + createSpace(COMPANY_ITEM_DESCRIPTION, str_first.length(), false) +
                                            order.getQty() + createSpace(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                            price + createSpace(COMPANY_ITEM_PRICE, String.format("%.2f", order.getPrice()).length(), false) +
                                            totalprice + createSpace(COMPANY_ITEM_AMOUNT, String.format("%.2f", order.getTotalItemPrice()).length(), false))
                                    .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())

                                    .build());
                            printables.add(new Printable.PrintableBuilder()
                                    .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                    .setText(createSpace(Sl_NO, String.valueOf(i).length(), false) + str_next + createSpace(COMPANY_ITEM_DESCRIPTION, str_next.length(), false))
                                    .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                    .setNewLinesAfter(1)
                                    .build());
                        }else {
                            printables.add(new Printable.PrintableBuilder()
                                    .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                    .setText(i + createSpace(Sl_NO, String.valueOf(i).length(), false) + parts[0] + createSpace(COMPANY_ITEM_DESCRIPTION, parts[0].length(), false) +
                                            order.getQty() + createSpace(COMPANY_ITEM_QUANTITY, String.valueOf(order.getQty()).length(), false) +
                                            price + createSpace(COMPANY_ITEM_PRICE, String.format("%.2f", order.getPrice()).length(), false) +
                                            totalprice + createSpace(COMPANY_ITEM_AMOUNT, String.format("%.2f", order.getTotalItemPrice()).length(), false))
                                    .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())

                                    .build());
                        }
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(createSpace(Sl_NO, String.valueOf(i).length(), false) + parts[1] + createSpace(COMPANY_ITEM_DESCRIPTION, parts[1].length(), false))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(1)
                                .build());
                    }
                }
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                float t1 = getTotalItemAmount();
                int qty = getTotalItemQty();
                String str_total_amount = decimalAdjust(t1);
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_TOTAL + createSpaceQty1() +getTotalItemQty()+createSpaceQty(String.valueOf(qty).length(), str_total_amount.length()) + str_total_amount)
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                if (!getString(mFooterDiscount).isEmpty()) {
                    printables.add(new Printable.PrintableBuilder()
                            .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                            .setText(COMPANY_ITEM_DISCOUNT + createSpace(COMPANY_ITEM_DISCOUNT.length(), String.valueOf(getString(mFooterDiscount)).length()) + getString(mFooterDiscount))
                            .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                            .setNewLinesAfter(1)
                            .build());
                }
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());

                float t2 = getPriceExcludingVat();
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_GROSS_AMOUNT + createSpace(COMPANY_ITEM_GROSS_AMOUNT.length(), String.valueOf(t2).length()) + getPriceExcludingVat())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());

                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_VAT + createSpace(COMPANY_ITEM_VAT.length(), String.valueOf(getString(mFooterVat)).length()) + getString(mFooterVat))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText(COMPANY_ITEM_NET_AMOUNT + createSpace(COMPANY_ITEM_NET_AMOUNT.length(), String.valueOf(getString(mFooterTotal)).length()) + getString(mFooterTotal))
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());

                if (selectedItem == "CASH"){
                    if(!getString(edtChange).isEmpty()) {
                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(PAID_AMOUNT + createSpace(PAID_AMOUNT.length(), String.valueOf(getString(edtChange)).length()) + getString(edtChange))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(1)
                                .build());

                        printables.add(new Printable.PrintableBuilder()
                                .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                                .setText(CHANGE + createSpace(CHANGE.length(), String.valueOf(getString(edtBalance)).length()) + getString(edtBalance))
                                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                                .setNewLinesAfter(1)
                                .build());
                    }
                }
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................")
                        // .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("No Refunds")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................")
                        // .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())

                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("Thank You !!")
                        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASISED_MODE_BOLD())
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_LARGE())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_CENTER())
                        .setText("Visit Again")
                        .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                        .setNewLinesAfter(1)
                        .build());
                printables.add(new Printable.PrintableBuilder()
                        .setAlignment(DefaultPrinter.Companion.getALLIGMENT_LEFT())
                        .setText("................................")
                        // .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())

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
                    dialog.setInteractorToFragment(CheckoutFragment.this::interacterOne);
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
            device.setDeviceName("No devices Found\n Please go to bluetooth settings and pair the device");
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
    private String createSpaceQty1() {

        int num =8 ;

        return new String(new char[num]).replace('\0', ' ');
    }
    private String createSpaceQty() {

        int num =18 ;

        return new String(new char[num]).replace('\0', ' ');
    }

    private String createSpaceQtyPrinter(int firstLength, int secondLegth) {
        //   int num = 32 - firstLength;
        int num = 25 - firstLength ;
        num = num - secondLegth;
        return new String(new char[num]).replace('\0', ' ');
    }
    private String createSpaceAmtPrinter(int firstLength, int secondLegth) {
        //   int num = 32 - firstLength;
        int num = 20 - firstLength ;
        num = num - secondLegth;
        return new String(new char[num]).replace('\0', ' ');
    }
    private String createSpaceQty(int firstLength, int secondLegth) {
        //   int num = 32 - firstLength;
        int num = 19 - firstLength ;
        num = num - secondLegth;
        return new String(new char[num]).replace('\0', ' ');
    }
    private String createSpace(int firstLength, int secondLegth) {
        //   int num = 32 - firstLength;
        int num = 32 - firstLength ;
        num = num - secondLegth;
        return new String(new char[num]).replace('\0', ' ');
    }
    private String createSpacePrinter(int firstLength, int secondLegth) {
        //   int num = 32 - firstLength;
        int num = 48 - firstLength ;
        num = num - secondLegth;
        return new String(new char[num]).replace('\0', ' ');
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

                num = 20-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_QUANTITY:

                num = 5-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_PRICE:

                num = 10-length;
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
    private String createSpace(String item, int length, boolean isBluetooth) {
        int total;
        int num;
        switch (item) {
            case Sl_NO:
                total=!isBluetooth ? 5: 5;
                num = 3 - length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_DESCRIPTION:
                total = !isBluetooth ? 19 : 48;
                num = 10-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_QUANTITY:
                total = !isBluetooth ? 5 : 7;
                num = 4-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_PRICE:
                total = !isBluetooth ? 10 : 15;
                num = 8-length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_AMOUNT:
                total = !isBluetooth ? 10 : 10;
                num = 8 - length;
                if (num < 0)
                    num = 0;
                return new String(new char[num]).replace('\0', ' ');
        }
        return null;
    }
    private String createSpace3(String item, int length, boolean isBluetooth) {
        int total;
        int num;
        switch (item) {
            case Sl_NO:
                total=!isBluetooth ? 3: 5;
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
    private String createSpace33(String item, int length, boolean isBluetooth) {
        int total;
        int num;
        switch (item) {
            case Sl_NO:
                total=!isBluetooth ? 5: 5;
                num = 1;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_DESCRIPTION:
                total = !isBluetooth ? 19 : 48;
                num = 6;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_QUANTITY:
                total = !isBluetooth ? 5 : 7;
                num = 1;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_PRICE:
                total = !isBluetooth ? 10 : 15;
                num = 3;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_AMOUNT:
                total = !isBluetooth ? 10 : 10;
                num = 2;
                return new String(new char[num]).replace('\0', ' ');
        }
        return null;
    }
    private String createSpace2(String item, int length, boolean isBluetooth) {
        int total;
        int num;
        switch (item) {
            case Sl_NO:
                total=!isBluetooth ? 5: 5;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_DESCRIPTION:
                total = !isBluetooth ? 19 : 48;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_QUANTITY:
                total = !isBluetooth ? 5 : 7;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_PRICE:
                total = !isBluetooth ? 10 : 15;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_AMOUNT:
                total = !isBluetooth ? 10 : 10;
                num = 47 - length;
                return new String(new char[num]).replace('\0', ' ');
        }
        return null;
    }
    private String createSpace12(String item, int length, boolean isBluetooth) {
        int total;
        int num;
        switch (item) {
            case Sl_NO:
                total=!isBluetooth ? 5: 4;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_DESCRIPTION:
                total = !isBluetooth ? 10 : 48;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_QUANTITY:
                total = !isBluetooth ? 4 : 7;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_PRICE:
                total = !isBluetooth ? 8 : 15;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_AMOUNT:
                total = !isBluetooth ? 7 : 10;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
        }
        return null;
    }
    private String createSpace1(String item, int length, boolean isBluetooth) {
        int total;
        int num;
        switch (item) {
            case Sl_NO:
                total=!isBluetooth ? 4: 4;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_DESCRIPTION:
                total = !isBluetooth ? 19 : 48;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_QUANTITY:
                total = !isBluetooth ? 7 : 7;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_PRICE:
                total = !isBluetooth ? 15 : 15;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
            case COMPANY_ITEM_AMOUNT:
                total = !isBluetooth ? 10 : 10;
                num = total - length;
                return new String(new char[num]).replace('\0', ' ');
        }
        return null;
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
            Transaction transaction = new Transaction();
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
            mDatabase.mTransactionDao().insertNewItems(transaction);

        }
    }

    @Override
    public void onClickedPlus(Items item) {
       if(!Constants.disableDelete){
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
        }}
    }

    @Override
    public void onClickedMinus(Items item) {
        if(!Constants.disableDelete) {
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
    }
    int getTotalItemQty() {
        int qty = 0;
        for (Items item : mItemsList) {
            qty = qty + item.getQty();
        }
        return qty;
    }
    float calculateTotal() {
        String str_vat = mPrefUtils.getStringPrefrence(DEAFULT_PREFS, Constants.VAT_EXCLUSIVE, getActivity().getResources().getString(R.string.vat_exclusive));
        float total = 0;
        float vat = 0;
        for (Items items : mItemsList) {
            total = total + items.getTotalItemPrice();
            vat = vat + calculateVat(items.getVat(), items.getPrice(), items.getQty());
        }
        textTotal.setText("Gross Amount : "+String.valueOf(Constants.round(total,2)));
        String disString = String.format("%.2f", Float.valueOf((getString(mFooterDiscount).isEmpty() ? "0" : getString(mFooterDiscount))));
        float discount = 0;
        if (str_vat.equals(getActivity().getResources().getString(R.string.vat_exclusive)))
            total = total + vat;
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

        // updateFooters(vat, discount, total + vat);

        updateFooters(vat, discount, total);
        return Constants.round(total, 2);
    }

    float calculateVat(float vat, float price, int qty) {
        String str_vat = mPrefUtils.getStringPrefrence(DEAFULT_PREFS, Constants.VAT_EXCLUSIVE, getActivity().getResources().getString(R.string.vat_exclusive));
        if (qty == 0) {
            if (str_vat.equals(getActivity().getResources().getString(R.string.vat_exclusive)))
                price = price * vat / 100;
            else  if (str_vat.equals(getActivity().getResources().getString(R.string.vat_inclusive)))
                price = price * vat / (100 + vat);
            else
                price = 0;
            return Constants.round(price, 2);
        } else {
            if (str_vat.equals(getActivity().getResources().getString(R.string.vat_exclusive)))
                price = (qty * price) * vat / 100;
            else  if (str_vat.equals(getActivity().getResources().getString(R.string.vat_inclusive)))
                price = (qty * price) * vat / (100 + vat);
            else
                price = 0;
            return Constants.round(price, 2);
        }
    }

    void updateFooters(float vat, float discount, float total) {
        String totalStirng = String.valueOf(Constants.round(total,2));
        String vatString = String.valueOf(Constants.round(vat,2));
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
       /* if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.PRINTER_TYPE,Constants.FEASYCOM).equals(Constants.FEASYCOM))
            printViaBluetoothPrinter();
        else*/
            printViaBluetoothPrinter1();

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
            edtChange.setText("");
            edtBalance.setText("");
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
            voltage_level = intent.getExtras().getInt("level");// Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂµÃ¯Â¿Â½Ã‡Â°Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½Ã¯Â¿Â½
            Log.e("wbw", "current  = " + voltage_level);
            BatteryV = intent.getIntExtra("voltage", 0);  //Ã§â€ÂµÃ¦Â±Â Ã§â€ÂµÃ¥Å½â€¹
            Log.e("wbw", "BatteryV  = " + BatteryV);
            Log.e("wbw", "V  = " + BatteryV * 2 / 100);
            //	m_voltage = (int) (65+19*voltage_level/100); //Ã¦â€Â¾Ã¥Â¤Â§Ã¥ÂÂÃ¥â‚¬Â
            //   Log.e("wbw","m_voltage  = " + m_voltage );
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }


}
