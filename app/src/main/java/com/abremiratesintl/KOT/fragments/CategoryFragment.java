package com.abremiratesintl.KOT.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.adapters.CategoryAdapter;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.Cashier;
import com.abremiratesintl.KOT.models.Category;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

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
public class CategoryFragment extends BaseFragment implements ClickListeners.CategoryItemEvents<Category> {

    @BindView(R.id.categoryName) EditText categoryName;
    @BindView(R.id.categorySave) Button categorySave;
    @BindView(R.id.categoryRecycler) RecyclerView categoryRecycler;
    List<Category> mCategoryList = new ArrayList<>();
    CategoryAdapter mCategoryAdapter;
    LiveData<List<Category>> categoryLiveList;
    LiveData<Category> category;
    LiveData<Integer> maxIdLive;
    int nextId;
    private Unbinder mUnbinder;
    private PrefUtils mPrefUtils;
    private AppDatabase mDatabase;
    Completable mCompletable;
    int categoryIdOfEditItem =0;
    String categoryNameOfEditItem ="";
    private Cashier cashier = new Cashier();
    private boolean isCashier = false;
    List<Items> mItemList = new ArrayList<>();
    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mPrefUtils = new PrefUtils(getContext());
        ((MainActivity)getActivity()).changeTitle("NEW CATEGORY");

        mDatabase = AppDatabase.getInstance(getContext());

        Thread t = new Thread(() -> {
            cashier = mDatabase.mCashierDao().getCashier();
        });
        t.start();

        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER))
            isCashier = true;
        return view;
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCategoryList();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @SuppressLint("CheckResult") @OnClick(R.id.categorySave)
    public void onClickedSave(View view) {
        if((isCashier &&  (cashier== null )) || (isCashier && cashier!= null && (!cashier.isCategoryInsert())) )

            showSnackBar(getView(),"Not Allowed!!",1000);

        else {

            if (categorySave.getText().toString().equals(getStringfromResource(R.string.save))) {
                String categoryString = getString(categoryName);
                if (!categoryString.isEmpty()) {
                    for (Category cat : mCategoryList) {
                        if (cat.getCategoryName().equals(categoryString)) {
                            showSnackBar(getView(), getStringfromResource(R.string.category_name_exist), 1000);
                            return;
                        }
                    }
                    insertCategory(categoryString);
                }
            } else {
                Category category = new Category();
                category.setCategoryName(getString(categoryName));
                category.setCategoryId(categoryIdOfEditItem);
                Completable.fromAction(() -> mDatabase.mCategoryDao().editCategoryNameById(getString(categoryName), categoryIdOfEditItem))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> mCategoryAdapter.notifyDataSetChanged(), throwable -> showSnackBar(getView(), getStringfromResource(R.string.category_update_failed), 1000));
            }
            resetFields();
        }
    }

    @SuppressLint("CheckResult") private void insertCategory(String categoryString) {
        Category category = new Category();
        category.setCategoryName(categoryString);
        category.setCreatedDate(Constants.getCurrentDateTime());
        category.setDeleted(false);
        Completable.fromAction(() -> mDatabase.mCategoryDao().insertNewCategory(category))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()-> isInserted(true,category), throwable ->  isInserted(false, category));
    }

    private void isInserted(boolean inserted, Category category) {
        if (inserted) {
            getCategoryList();
        }
    }

    void resetFields(){
        categoryName.setText("");
        categorySave.setText(getStringfromResource(R.string.save));
    }

    private void getCategoryList(){
        categoryLiveList = mDatabase.mCategoryDao().getAllCategory();
        categoryLiveList.observe(this, categories -> {
            mCategoryList = categories;
            setUpRecyclerView();
        });
    }

    private void setUpRecyclerView() {

        mCategoryAdapter = new CategoryAdapter(mCategoryList, getContext(),this);
        categoryRecycler.setAdapter(mCategoryAdapter);
     /*   ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(getContext(),mCategoryAdapter));
        itemTouchHelper.attachToRecyclerView(categoryRecycler);*/
    }

    @Override public void onClickedEdit(Category category) {

            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.dialog_layout_item);
            // dialog.setTitle("Title...");


            TextView textName = (TextView) dialog.findViewById(R.id.textName);
            textName.setText(category.getCategoryName());


            Button dialogButtonEdit = (Button) dialog.findViewById(R.id.buttonEdit);
            Button dialogButtonDelete = (Button) dialog.findViewById(R.id.buttonDelete);
            // if button is clicked, close the custom dialog
            dialogButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((isCashier &&  (cashier== null )) || (isCashier && cashier!= null && (!cashier.isCategoryUpdate())) )

                        showSnackBar(getView(),"Not Allowed!!",1000);

                    else {
                        categoryName.setText(category.getCategoryName());
                        categoryName.setSelection(category.getCategoryName().length());
                        categorySave.setText(getStringfromResource(R.string.edit));
                        categoryIdOfEditItem = category.getCategoryId();
                        categoryNameOfEditItem = category.getCategoryName();
                    }
                        dialog.dismiss();

                }
            });
            dialogButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((isCashier &&  (cashier== null )) || (isCashier && cashier!= null && (!cashier.isCategoryDelete())) )

                        showSnackBar(getView(),"Not Allowed!!",1000);


                    else {
                       /* int catId = category.getCategoryId();
                        Thread t = new Thread(() -> {
                            mItemList = mDatabase.mItemsDao().findItemsByCategoryId1(catId);
                        });
                        t.start();

                        Log.e("Cat delete :","size "+mItemList.size()+"  Id :"+catId);
                        if (mItemList != null && mItemList.size() > 0) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                            builder1.setMessage("Can not delete category having Items");
                            builder1.setCancelable(false);

                            builder1.setPositiveButton(
                                    "Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();

                                            mCategoryAdapter.notifyDataSetChanged();
                                        }
                                    });


                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }else {*/
                            showSnackBar(getView(), getStringfromResource(R.string.deleted), 1000);
                            Completable.fromAction(() -> mDatabase.mCategoryDao().editCategoryDeleteById(true, category.getCategoryId()))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() ->
                                                    mCategoryAdapter.notifyDataSetChanged(),
                                            throwable ->
                                                    showSnackBar(getView(), getStringfromResource(R.string.category_update_failed), 1000));
                            getCategoryList();

                    }
                    dialog.dismiss();

                }
            });
            dialog.show();


    }

    @SuppressLint("CheckResult") @Override public void onDeletedItem(Category category) {
        showSnackBar(getView(),getStringfromResource(R.string.deleted),1000);
        Completable.fromAction(() -> mDatabase.mCategoryDao().editCategoryDeleteById(true,category.getCategoryId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()->
                        mCategoryAdapter.notifyDataSetChanged(),
                        throwable ->
                        showSnackBar(getView(),getStringfromResource(R.string.category_update_failed),1000));
        getCategoryList();
    }
    @Override public void onPause() {
        super.onPause();

    }
}