package com.abremiratesintl.KOT.fragments;


import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.adapters.CategorySpinnerAdapter;
import com.abremiratesintl.KOT.adapters.ItemsAdapter;
import com.abremiratesintl.KOT.adapters.SwipeToDeleteCallback;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.Category;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;
import com.abremiratesintl.KOT.views.CustomSpinner;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static com.abremiratesintl.KOT.utils.Constants.REQUEST_CODE_IMAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends BaseFragment implements ClickListeners.CategoryItemEvents<Items>, AdapterView.OnItemSelectedListener, CustomSpinner.OnSpinnerEventsListener {

    @BindView(R.id.itemName)
    EditText mItemName;
    @BindView(R.id.itemCategory)
    CustomSpinner mItemCategory;
    @BindView(R.id.itemBarcode)
    EditText mItemBarcode;
    @BindView(R.id.itemPrice)
    EditText mItemPrice;
    @BindView(R.id.itemCost)
    EditText mItemCost;
    @BindView(R.id.itemVatPercentage)
    EditText mItemVat;
    @BindView(R.id.item_recycler)
    RecyclerView mItemRecycler;
    @BindView(R.id.spinner_arrow)
    ImageView mSpinnerArrow;
    @BindView(R.id.thumb_image_view)
    ImageView thumbImageVIew;
    @BindView(R.id.thumbImage)
    ConstraintLayout thumb1;
    @BindView(R.id.thumImage)
    ConstraintLayout thumb2;
    @BindDrawable(R.drawable.ic_arrow_down)
    Drawable icDown;
    @BindDrawable(R.drawable.ic_arrow_up)
    Drawable icUp;
    @BindView(R.id.saveItem)
    LinearLayout saveItem;
    private Unbinder mUnbinder;

    private List<Category> mCategoryList;
    private Category mSelectedCategory;
    ItemsAdapter mItemsAdapter;
    private PrefUtils mPrefUtils;
    private AppDatabase mDatabase;
    private List<Items> mItemList;
    private Items mItems;
    private boolean isEditing;
    private Uri mSelectedImageUri;
    private String mPath="";
    private int itemId = 0;

    public ItemFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mPrefUtils = new PrefUtils(getContext());
        mDatabase = AppDatabase.getInstance(getContext());
        ((MainActivity) getActivity()).changeTitle("NEW ITEM");
//        setHasOptionsMenu(true);
        mItemCategory.setOnItemSelectedListener(this);
        mItemCategory.setSpinnerEventsListener(this);
        getCategoryList();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void getCategoryList() {
        LiveData<List<Category>> categoryLiveList = mDatabase.mCategoryDao().getAllCategory();
        categoryLiveList.observe(this, categories -> {
            if (categories == null || categories.size() == 0) {
                return;
            }
            mCategoryList = categories;
            mSelectedCategory = categories.get(0);
            getItemsFromDb();
        });
    }

    private void setUpSpinner() {
        CategorySpinnerAdapter<Category> itemSpinnerAdapter = new CategorySpinnerAdapter<>(getContext(), R.id.categoryListItem, mCategoryList);
        mItemCategory.setAdapter(itemSpinnerAdapter);
    }

   /* @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.removeItem(R.id.menu_undo);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        onClickedSave();
        return true;
    }*/

    @OnClick(R.id.saveItem)
    public void onClickedSave() {
        getItemsFromFields();
    }

    @SuppressLint("CheckResult")
    private void insetToDb(Items items) {
        Completable.fromAction(() -> mDatabase.mItemsDao().insertNewItems(items))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    inserted(true, items);
                    resetFeilds();
                }, throwable -> inserted(false, items));
    }

    private void updateItem(Items items) {

        Completable.fromAction(() -> mDatabase.mItemsDao().updateItem(items.getItemId(), items.getItemName(), items.getPrice(), items.getCost(), items.getVat(), items.getCategoryId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {

                    inserted(false, items);
                    resetFeilds();

                }, throwable -> inserted(false, items));
    }

    private void inserted(boolean inserted, Items items) {
        if (!inserted) {

            getItemsFromDb();

        } else {
            mItemList.add(items);
            mItemsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedCategory = mCategoryList.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    void setUpRecyclerViews() {
        if (mCategoryList != null || mCategoryList.size() > 0) {
            if (mItemList != null || mItemList.size() > 0) {
                mItemsAdapter = new ItemsAdapter(mItemList, getContext(), this, this);
                mItemRecycler.setAdapter(mItemsAdapter);
                ItemTouchHelper itemTouchHelper = new
                        ItemTouchHelper(new SwipeToDeleteCallback(getContext(), mItemsAdapter));
                itemTouchHelper.attachToRecyclerView(mItemRecycler);
            }
        }
    }

    void getItemsFromFields() {
        if (mSelectedCategory != null) {
            if (!emptyFields()) {
                mItems = new Items();

                mItems.setCategoryId(mSelectedCategory.getCategoryId());
                String itemName = getString(mItemName);
                float itemVat = Float.parseFloat((getString(mItemVat)).isEmpty() ? "0" : getString(mItemVat));
                String itemPrice = (getString(mItemPrice).isEmpty()) ? "0" : getString(mItemPrice);
                String itemCost = (getString(mItemCost).isEmpty()) ? "0" : getString(mItemCost);

                mItems.setItemName(getString(mItemName));

                mItems.setVat(itemVat);
                mItems.setItemId(itemId);
                mItems.setPrice(Float.parseFloat(itemPrice));
                mItems.setCost(Float.parseFloat(itemCost));
                mItems.setCreatedDate(Constants.getCurrentDateTime());
                mItems.setImagePath(mPath);
                if (isEditing)

                    updateItem(mItems);
                else
                    insetToDb(mItems);
            } else {
                showSnackBar(getView(), getStringfromResource(R.string.fields_cannot_be_empty), 1000);
            }
        } else {
            showSnackBar(getView(), getStringfromResource(R.string.emptyCategory), 1000);
        }
    }

    private boolean emptyFields() {
        return getString(mItemName).isEmpty() && getString(mItemPrice).isEmpty();
    }

    void resetFeilds() {
        mItemName.setText("");
        mItemBarcode.setText("");
        mItemPrice.setText("");
        mItemCost.setText("");
        mItemVat.setText("");
    }

    void getItemsFromDb() {
        LiveData<List<Items>> listLiveData = mDatabase.mItemsDao().getAllItems();
        listLiveData.observe(this, items -> {
            mItemList = items;
            setUpRecyclerViews();
            setUpSpinner();
        });
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    void fillToFields(Items items) {
        itemId = items.getItemId();
        isEditing = true;

        LiveData<Category> categoryLiveData = mDatabase.mCategoryDao().findCategoryById(items.getCategoryId());
        categoryLiveData.observe(this, category -> {
            mItemName.setText(items.getItemName());
            mItemCategory.setSelection(category.getCategoryId() - 1);
            mItemPrice.setText(String.format("%.2f", items.getPrice()));
            mItemCost.setText(String.format("%.2f", items.getCost()));
            mItemBarcode.setText(items.getItemName());
            mItemVat.setText(String.format("%.2f", items.getVat()));
        });
    }

    @OnClick(R.id.thumbImage)
    public void onClickedImageChange() {
        chengeThumbImage();
    }

    @OnClick(R.id.thumImage)
    public void onClickedImageChange1() {
        chengeThumbImage();
    }

    private void chengeThumbImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_CODE_IMAGE) {
                    mSelectedImageUri = data.getData();
                    // Get the path from the Uri
                   // mPath = getPathFromURI(mSelectedImageUri);
                    mPath = mSelectedImageUri.getPath();
                    if (mPath != null) {
                        File f = new File(mPath);
                        mSelectedImageUri = Uri.fromFile(f);
                    }
                    Glide.with(getContext())
                            .load(mSelectedImageUri)
//                            .placeholder(R.drawable.ic_hospital_place_holder)
                            .into(thumbImageVIew);
                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public void onClickedEdit(Items items) {
        fillToFields(items);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onDeletedItem(Items items) {
        showSnackBar(getView(), getStringfromResource(R.string.deleted), 1000);
        Completable.fromAction(() -> mDatabase.mItemsDao().editItemsDeleteById(true, items.getItemId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() ->
                                mItemsAdapter.notifyDataSetChanged(),
                        throwable ->
                                showSnackBar(getView(), getStringfromResource(R.string.category_update_failed), 1000));
        getItemsFromDb();
    }

    @Override
    public void onSpinnerOpened(Spinner spinner) {
        mSpinnerArrow.setImageDrawable(icUp);
    }

    @Override
    public void onSpinnerClosed(Spinner spinner) {
        mSpinnerArrow.setImageDrawable(icDown);
    }
}