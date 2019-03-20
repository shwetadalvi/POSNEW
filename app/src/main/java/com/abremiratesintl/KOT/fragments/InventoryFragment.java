package com.abremiratesintl.KOT.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.adapters.CategorySpinnerAdapter;
import com.abremiratesintl.KOT.adapters.POSRecyclerAdapter;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.CartItems;
import com.abremiratesintl.KOT.models.Category;
import com.abremiratesintl.KOT.models.InventoryMaster;
import com.abremiratesintl.KOT.models.InventoryTransaction;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.Transaction;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.views.CustomSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class  InventoryFragment extends BaseFragment implements AdapterView.OnItemSelectedListener, CustomSpinner.OnSpinnerEventsListener, ClickListeners.ItemClick<Items>, ClickListeners.OnItemChangedListener,DatePickerDialog.OnDateSetListener {

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

    @BindView(R.id.editInvoice)
    EditText editInvoice;
    @BindView(R.id.editDate)
    EditText editDate;
    @BindView(R.id.editSupplier)
    EditText editSupplier;
    @BindView(R.id.editRef)
    EditText editRef;
    @BindView(R.id.addNewItemSpinnerArrow)
    ImageView addNewItemSpinnerArrow;
    @BindDrawable(R.drawable.ic_arrow_down)
    Drawable arrowDown;
    @BindDrawable(R.drawable.ic_arrow_up)
    Drawable arrowUp;
    private Unbinder mUnbinder;
    private AppDatabase mDatabase;
    private List<Category> mCategoryList;
    private Category mSelectedCategory;
    private List<Items> mItemList;
    public List<Items> mCartItems = new ArrayList<>();
    private float currentVat = 0;
    private float currentTotal;
    int totalItemCount = 0;
    float totalItemPrice = 0;
private int transactionMasterMaxId ;
    View mSelectedDateView;
    private String mSelectedDate = Constants.getCurrentDate();

    List<InventoryMaster> mItemsList = new ArrayList<>();
    List<InventoryTransaction> mItemsList1 = new ArrayList<>();
    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mDatabase = AppDatabase.getInstance(getActivity());
        addNewItemSpinnerCategory.setOnItemSelectedListener(this);
        addNewItemSpinnerCategory.setSpinnerEventsListener(this);

        ((MainActivity) getActivity()).changeTitle("Inventory");


        editInvoice.setEnabled(false);


        LiveData<List<InventoryMaster>> itemLiveList = mDatabase.mInventoryMasterDao().getAllItems();
        itemLiveList.observe(this, items -> {
            if (items == null || items.size() == 0) {
                return;
            }
            mItemsList = items;

        });
        Log.d("Inventory 1","size"+mItemsList.size());
        getCategoryList();

            itemCount.setText(String.valueOf(0));
            totalAmount.setText(String.valueOf(0));

        return view;
    }

    private void getCategoryList() {
        Thread t = new Thread(() -> {
            transactionMasterMaxId = (mDatabase.mInventoryMasterDao().findTransMasterOfMaxId());
        });
        t.start();
        transactionMasterMaxId = transactionMasterMaxId == 0 ? 1 : transactionMasterMaxId + 1;
        editInvoice.setText("Invoice No : "+String.valueOf(transactionMasterMaxId));

        LiveData<List<Category>> categoryLiveList = mDatabase.mCategoryDao().getAllCategory();
        categoryLiveList.observe(this, categories -> {
            if (categories==null||categories.size()==0){
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
            POSRecyclerAdapter mItemsAdapter = new POSRecyclerAdapter(mItemList, this);
            addNewItemRecyclerView.setAdapter(mItemsAdapter);
        }
    }

    private void setUpSpinner() {
        if (mCategoryList != null || mCategoryList.size() > 0) {
            CategorySpinnerAdapter<Category> itemSpinnerAdapter = new CategorySpinnerAdapter<>(getContext(), R.id.categoryListItem, mCategoryList);
            addNewItemSpinnerCategory.setAdapter(itemSpinnerAdapter);
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
    @OnClick(R.id.editDate) public void onClickedToDate() {
        mSelectedDateView = editDate;
        showDatePicker();
    }
    public void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(), this,calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mSelectedDate = year + "-" + getProperDate(++month) + "-" + getProperDate(dayOfMonth);
        switch (mSelectedDateView.getId()) {

            case R.id.editDate:
                editDate.setText(mSelectedDate);

                break;
        }
    }
    private String getProperDate(int dayOrMonth) {
        if (dayOrMonth < 10) {
            return "0" + dayOrMonth;
        }
        return String.valueOf(dayOrMonth);
    }
    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedCategory = mCategoryList.get(position);
        getItemsOfSelectedCategory();
    }

    @Override public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override public void onSpinnerOpened(Spinner spinner) {
        addNewItemSpinnerArrow.setImageDrawable(arrowUp);
    }

    @Override public void onSpinnerClosed(Spinner spinner) {
        addNewItemSpinnerArrow.setImageDrawable(arrowDown);
    }

    @Override public void onClickedItem(Items item) {


        int mItemCountCount = 0;
        float mTotalItemAmount = 0;


            for (int i = 0; i < mCartItems.size(); i++) {
                mTotalItemAmount = Float.valueOf(getString(totalAmount)) + mCartItems.get(i).getPrice();
                mItemCountCount = Integer.valueOf(getString(itemCount)) + 1;
                if (item.getItemId() == mCartItems.get(i).getItemId() && !mCartItems.get(i).isSaleReturned()) {
                    item.setTotalItemPrice(mCartItems.get(i).getQty() * mCartItems.get(i).getPrice());
                    item.setQty(mCartItems.get(i).getQty());
                }
            }
            if (mCartItems.size() == 0) {
                mTotalItemAmount = item.getPrice();
                mItemCountCount = 1;
            }
            mTotalItemAmount = Constants.round(mTotalItemAmount, 2);
            insertToList(item);
            setFooterAndVat(item, mTotalItemAmount, mItemCountCount);

    }

    void setFooterAndVat(Items item, float mTotalItemAmount, int mItemCountCount) {
        calculateVat(item.getVat(), item.getPrice());
        currentItemAndCount.setText(item.getItemName() + " x " + item.getQty());
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

    @Override public void onResume() {
        super.onResume();
    }

    @Override public void onPause() {
        super.onPause();

    }
    void insertToList(Items item) {
        int qty = item.getQty();

        qty += 1;
        float price = item.getPrice() * qty;
        if (mCartItems.size() != 0) {
            for (int i = 0; i < mCartItems.size(); i++) {
                if (item.getItemId() == mCartItems.get(i).getItemId() && !item.isSaleReturned()) {
                    mCartItems.get(i).setTotalItemPrice(price);
                    mCartItems.get(i).setQty(qty);
                    return;
                }
            }
            item.setQty(qty);
            item.setTotalItemPrice(price);
            mCartItems.add(item);
        } else {
            item.setQty(item.getQty() + 1);
            item.setTotalItemPrice(price);
            mCartItems.add(item);
        }
    }
    private void insertTransactions(List<Items> itemsList) {
        InventoryMaster inventoryMaster = new InventoryMaster();

        float grantTotal = Float.parseFloat(getString(totalAmount));
        String invoiceDate = Constants.getCurrentDate();
//inventoryMaster.setTransMasterId(transactionMasterMaxId);
        inventoryMaster.setInvoiceNo(String.valueOf(transactionMasterMaxId));
        inventoryMaster.setItemTotalAmount(grantTotal);
        inventoryMaster.setCreatedDate(Constants.getCurrentDateTime());
        inventoryMaster.setPurchaseDate(mSelectedDate);
        inventoryMaster.setRefference(editRef.getText().toString());
        inventoryMaster.setSupplier(editSupplier.getText().toString());


        new Thread(() -> {


            mDatabase.mInventoryMasterDao().insertNewItems(inventoryMaster);

            for (Items item : itemsList) {

                insertInventoryTransaction(true, item, transactionMasterMaxId);

            }


        }).start();


        LiveData<List<InventoryMaster>> itemLiveList = mDatabase.mInventoryMasterDao().getAllItems();
        itemLiveList.observe(this, items -> {
            if (items == null || items.size() == 0) {
                return;
            }
            mItemsList = items;

        });
        Log.d("Inventory 2","size"+mItemsList.size());
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Inventory Added Successfully");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        resetFields();

                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();


    }

    private void resetFields() {
        Thread t = new Thread(() -> {
            transactionMasterMaxId = (mDatabase.mInventoryMasterDao().findTransMasterOfMaxId());
        });
        t.start();
        transactionMasterMaxId = transactionMasterMaxId == 0 ? 1 : transactionMasterMaxId + 1;
        editInvoice.setText("Invoice No : "+String.valueOf(transactionMasterMaxId));
        editDate.setText("Present");
        mSelectedDate = Constants.getCurrentDate();
        editSupplier.setText("");
        editRef.setText("");
        mCartItems.clear();
        itemCount.setText(String.valueOf(0));
        totalAmount.setText(String.valueOf(0));
        currentItemAndCount.setText("");

    }

    private void insertInventoryTransaction(boolean b, Items item, Integer finalTransactionMasterMaxId) {
        if (b) {

            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setTransMasterId(finalTransactionMasterMaxId);
            transaction.setVat(item.getVat());
            transaction.setItemId(item.getItemId());
            transaction.setQty(item.getQty());
            transaction.setPrice(item.getPrice());
            transaction.setCreatedDate(Constants.getCurrentDate());
            transaction.setItemName(item.getItemName());
            transaction.setInvoiceDate(Constants.getCurrentDate());
            transaction.setGrandTotal(item.getQty() * item.getPrice());



            String category = mDatabase.mCategoryDao().getCategoryById(item.getCategoryId());
            transaction.setCategory(category);


            Completable.fromAction(() -> mDatabase.mInventoryTransactionDao().insertNewItems(transaction))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                    }, throwable -> Log.e("INSERTION MASTER2", "Count   "));

            LiveData<List<InventoryTransaction>> itemLiveList = mDatabase.mInventoryTransactionDao().getAllItems();
            itemLiveList.observe(this, items -> {
                if (items == null || items.size() == 0) {
                    return;
                }
                mItemsList1 = items;

            });
            Log.d("Inventory 3","size"+mItemsList1.size());
        } else {

        }
           // mDatabase.mInventoryTransactionDao().insertNewItems(transaction);


    }
    @OnClick(R.id.addNewItemProceed)
    public void onClickedProceed(View view) {
        totalItemCount = Integer.valueOf(getString(itemCount));
        totalItemPrice = Float.valueOf(getString(totalAmount));
        Log.d("Inventory 4","size"+mCartItems.size());
        if (mCartItems.size() != 0)
        insertTransactions(mCartItems);
        else{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setMessage("No Item is Added");
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

    @Override public void itemChanged(List<Items> list) {
        mItemList = list;
        int count = 0;
        float total = 0;

        for (Items item : list) {
            count = count + item.getQty();
            total = total + item.getTotalItemPrice();
//            setFooterAndVat(item, total, count);
        }
    }
}
