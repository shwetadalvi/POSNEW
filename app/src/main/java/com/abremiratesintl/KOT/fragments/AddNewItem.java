package com.abremiratesintl.KOT.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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
import com.abremiratesintl.KOT.models.CartItems;
import com.abremiratesintl.KOT.models.Cashier;
import com.abremiratesintl.KOT.models.Category;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;
import com.abremiratesintl.KOT.views.CustomSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewItem extends BaseFragment implements AdapterView.OnItemSelectedListener, CustomSpinner.OnSpinnerEventsListener, ClickListeners.ItemClick<Items>, ClickListeners.OnItemChangedListener {

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
    private Unbinder mUnbinder;
    private AppDatabase mDatabase;
    private List<Category> mCategoryList;
    private Category mSelectedCategory;
    private List<Items> mItemList;
    public List<Items> mCartItems = new ArrayList<>();
    private float currentVat = 0;
    private float currentTotal;
    int totalItemCount = 0, menuReturnClickCount = 0;
    float totalItemPrice = 0;
    CartItems cartItem = new CartItems();
    POSRecyclerAdapter mItemsAdapter;
    private  AudioManager audioManager;
    private Cashier cashier;
    private PrefUtils mPrefUtils;
    public AddNewItem() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_item, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mDatabase = AppDatabase.getInstance(getContext());
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
        LiveData<Cashier> cashierLiveData = mDatabase.mCashierDao().getCashier();
        cashierLiveData.observe(this, cashier -> {
            if (cashier != null ) {

                fillFields(cashier);
            }
        });
        //   Log.e("cashier nmae :","cat :"+cashier.isCategoryView() +"name :"+cashier.getCashierName());
        return view;
    }
    private void fillFields(Cashier cashier1){
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

        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER) &&  (!cashier.isItemInsert()) )
            showSnackBar(getView(),"Not Allowed!!",5000);
        else {
            audioManager.playSoundEffect(SoundEffectConstants.CLICK);
            audioManager.playSoundEffect(SoundEffectConstants.CLICK, 0.5F);


            int mItemCountCount = 0;
            float mTotalItemAmount = 0;
            if (menuReturnClickCount == 1) {


                for (int i = 0; i < mCartItems.size(); i++) {
                    mTotalItemAmount = Float.valueOf(getString(totalAmount)) - mCartItems.get(i).getPrice();
                    mItemCountCount = Integer.valueOf(getString(itemCount)) - 1;
                   /* if (item.getItemId() == mCartItems.get(i).getItemId()) {
                        item.setTotalItemPrice(mCartItems.get(i).getQty() * mCartItems.get(i).getPrice());
                        item.setQty(mCartItems.get(i).getQty());
                    }*/
                }
                if (mCartItems.size() == 0) {
                    mTotalItemAmount = item.getPrice();
                    mItemCountCount = 1;
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
                                mDatabase.mItemsDao().insertNewItems(item);
                            });
                            t.start();

                            dialog.dismiss();
                            addItem(item);
                        }
                    });

                    dialog.show();
                } else {
                    addItem(item);
                }
            }
        }
    }
void addItem(Items item){
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
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

    void returnFromList(Items item) {

        int qty = -1;
        float price = item.getPrice() * qty;
        item.setQty(qty);
        item.setTotalItemPrice(price);
        item.setSaleReturned(true);
        mCartItems.add(item);

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
        cartItem.setCartItems(mCartItems);
        Navigation.findNavController(view).navigate(AddNewItemDirections.actionAddNewItemToCheckoutFragment(cartItem));
    }

    @Override
    public void itemChanged(List<Items> list) {
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
