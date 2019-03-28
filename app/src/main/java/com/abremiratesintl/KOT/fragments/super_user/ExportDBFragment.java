package com.abremiratesintl.KOT.fragments.super_user;


import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.models.Category;
import com.abremiratesintl.KOT.models.InventoryMaster;
import com.abremiratesintl.KOT.models.InventoryTransaction;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.Transaction;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExportDBFragment extends Fragment {

    @BindView(R.id.export)
    Button mbtnExport;
    @BindView(R.id.deleteDB)
    Button mbtnDeleteDB;
    @BindView(R.id.layoutCheckbox)
    LinearLayout layoutCheckbox;
    @BindView(R.id.checkBoxCategory)
    CheckBox checkBoxCategory;
    @BindView(R.id.checkTransaction)
    CheckBox checkTransaction;
    @BindView(R.id.checkTransactionMaster)
    CheckBox checkTransactionMaster;
    @BindView(R.id.checkCompany)
    CheckBox checkCompany;
    @BindView(R.id.checkInventoryTrans)
    CheckBox checkInventoryTrans;
    @BindView(R.id.checkInventoryMaster)
    CheckBox checkInventoryMaster;
    @BindView(R.id.progress)
    ProgressBar progress;
    private Unbinder mUnbinder;
    private AppDatabase mDatabase;
    int deleteDBCount = 0;

    public ExportDBFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_export_db, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mDatabase = AppDatabase.getInstance(getContext());


        mbtnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                getItems();


            }
        });
        mbtnDeleteDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteDBCount++;
                if (deleteDBCount % 2 == 0) {
                    layoutCheckbox.setVisibility(View.GONE);
                } else {
                    layoutCheckbox.setVisibility(View.VISIBLE);


                }

            }
        });
        checkBoxCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                        @Override
                                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                            if (isChecked) {

                                                                deleteCategoryTable();


                                                            }

                                                        }
                                                    }
        );
        checkTransaction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                        @Override
                                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                            if (isChecked) {

                                                                deleteTransactionTable();


                                                            }

                                                        }
                                                    }
        );
        checkTransactionMaster.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                              @Override
                                                              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                  if (isChecked) {

                                                                      deleteTransactionMasterTable();


                                                                  }

                                                              }
                                                          }
        );
        checkCompany.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        if (isChecked) {


                                                            deleteCompanyTable();

                                                        }
                                                    }
                                                }
        );
        checkInventoryMaster.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                            @Override
                                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                if (isChecked) {

                                                                    deleteInventoryMaster();


                                                                }

                                                            }
                                                        }
        );
        checkInventoryTrans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                           @Override
                                                           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                               if (isChecked) {

                                                                   deleteInventoryTansaction();


                                                               }

                                                           }
                                                       }
        );
        return view;
    }

    private void deleteInventoryTansaction() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mDatabase.mInventoryTransactionDao().deleteAll();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                        Toast.makeText(getActivity(), "Inventory Transaction table deleted.", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        Log.d(TAG, "throwable.getMessage(): " + throwable.getMessage());


                    }
                });
    }

    private void deleteInventoryMaster() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mDatabase.mInventoryMasterDao().deleteAll();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                        Toast.makeText(getActivity(), "Inventory Master table deleted.", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        Log.d(TAG, "throwable.getMessage(): " + throwable.getMessage());


                    }
                });
    }

    private void deleteCompanyTable() {

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mDatabase.mCompanyDao().deleteAll();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                        Toast.makeText(getActivity(), "Company table deleted.", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        Log.d(TAG, "throwable.getMessage(): " + throwable.getMessage());


                    }
                });

    }

    private void deleteTransactionMasterTable() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mDatabase.mTransactionMasterDao().deleteAll();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                        Toast.makeText(getActivity(), "Transaction Master table deleted.", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        Log.d(TAG, "throwable.getMessage(): " + throwable.getMessage());


                    }
                });
    }

    private void deleteTransactionTable() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mDatabase.mTransactionDao().deleteAll();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {


                        Toast.makeText(getActivity(), "Transaction table deleted.", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        Log.d(TAG, "throwable.getMessage(): " + throwable.getMessage());


                    }
                });
    }

    private void deleteCategoryTable() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mDatabase.mItemsDao().deleteAll();
                mDatabase.mCategoryDao().deleteAll();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                        Toast.makeText(getActivity(), "Category & Item tables deleted.", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        Log.d(TAG, "throwable.getMessage(): " + throwable.getMessage());


                    }
                });

    }

    private void getItems() {
        LiveData<List<Items>> listLiveData = mDatabase.mItemsDao().getAllItems();
        listLiveData.observe(this, this::exportItems);
        LiveData<List<Items>> itemLiveList = mDatabase.mItemsDao().getAllItems();
        itemLiveList.observe(this, items -> {
            if (items == null || items.size() == 0) {
                return;
            }
            List<Items> mItemsList = items;
            exportItems(mItemsList);
        });
    }

    private void exportItems(List<Items> mTransactionList) {
        File sd = Environment.getExternalStorageDirectory();

        String csvFile = "items" + System.currentTimeMillis() + ".xls";

        File directory = new File(sd.getAbsolutePath() + "/POS Backup");
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("items", 0);
            // column and row


            sheet.addCell(new Label(0, 0, "Sr No."));
            sheet.addCell(new Label(1, 0, "ItemId"));
            sheet.addCell(new Label(2, 0, "CategoryId"));
            sheet.addCell(new Label(3, 0, "ItemName"));
            sheet.addCell(new Label(4, 0, "Price"));
            sheet.addCell(new Label(5, 0, "Cost"));
            sheet.addCell(new Label(6, 0, "Vat"));
            sheet.addCell(new Label(7, 0, "ImagePath"));
            sheet.addCell(new Label(8, 0, "Is Deleted"));
            sheet.addCell(new Label(9, 0, "Quantity"));
            sheet.addCell(new Label(10, 0, "Total Item Price"));
            sheet.addCell(new Label(11, 0, "is Checked"));
            sheet.addCell(new Label(12, 0, "Is Open"));
            sheet.addCell(new Label(13, 0, "Created Date"));
            int i = 0;


            for (Items item : mTransactionList) {
                i = i + 1;
                sheet.addCell(new Label(0, i, String.valueOf(i)));
                sheet.addCell(new Label(1, i, String.valueOf(item.getItemId())));
                sheet.addCell(new Label(2, i, String.valueOf(item.getCategoryId())));
                sheet.addCell(new Label(3, i, item.getItemName()));
                sheet.addCell(new Label(4, i, String.valueOf(item.getPrice())));
                sheet.addCell(new Label(5, i, String.valueOf(item.getCost())));
                sheet.addCell(new Label(6, i, String.valueOf(item.getVat())));
                sheet.addCell(new Label(7, i, item.getImagePath()));
                sheet.addCell(new Label(8, i, String.valueOf(item.isDeleted())));
                sheet.addCell(new Label(9, i, String.valueOf(item.getQty())));
                sheet.addCell(new Label(10, i, String.valueOf(item.getTotalItemPrice())));
                sheet.addCell(new Label(11, i, String.valueOf(item.isChecked())));
                sheet.addCell(new Label(12, i, String.valueOf(item.isOpen())));
                sheet.addCell(new Label(13, i, item.getCreatedDate()));
            }

            workbook.write();
            workbook.close();
            // Toast.makeText(getActivity(),"Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();
            getCategories();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCategories() {
        LiveData<List<Category>> listLiveData = mDatabase.mCategoryDao().getAllCategory();
        listLiveData.observe(this, this::exportCategory);

    }

    private void exportCategory(List<Category> mTransactionList) {
        File sd = Environment.getExternalStorageDirectory();

        String csvFile = "category" + System.currentTimeMillis() + ".xls";

        File directory = new File(sd.getAbsolutePath() + "/POS Backup");
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("category", 0);
            // column and row


            sheet.addCell(new Label(0, 0, "Sr No."));
            sheet.addCell(new Label(1, 0, "Category Id"));
            sheet.addCell(new Label(2, 0, "category Name"));
            sheet.addCell(new Label(3, 0, "Is Deleted"));
            sheet.addCell(new Label(4, 0, "Created Date"));
            int i = 0;


            for (Category item : mTransactionList) {
                i = i + 1;
                sheet.addCell(new Label(0, i, String.valueOf(i)));
                sheet.addCell(new Label(1, i, String.valueOf(item.getCategoryId())));
                sheet.addCell(new Label(2, i, item.getCategoryName()));
                sheet.addCell(new Label(3, i, String.valueOf(item.isDeleted())));
                sheet.addCell(new Label(4, i, item.getCreatedDate()));
            }

            workbook.write();
            workbook.close();
            // Toast.makeText(getActivity(),"Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();
            getTransaction();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getTransaction() {
        LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().getAllItems();
        listLiveData.observe(this, this::exportTransaction);

    }

    private void exportTransaction(List<Transaction> mTransactionList) {
        File sd = Environment.getExternalStorageDirectory();

        String csvFile = "transaction" + System.currentTimeMillis() + ".xls";

        File directory = new File(sd.getAbsolutePath() + "/POS Backup");
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("Transaction", 0);
            // column and row


            sheet.addCell(new Label(0, 0, "Sr No."));
            sheet.addCell(new Label(1, 0, "Transaction Id"));
            sheet.addCell(new Label(2, 0, "TransMaster Id"));
            sheet.addCell(new Label(3, 0, "itemId"));
            sheet.addCell(new Label(4, 0, "qty"));
            sheet.addCell(new Label(5, 0, "price"));
            sheet.addCell(new Label(6, 0, "vatPercentage"));
            sheet.addCell(new Label(7, 0, "Vat"));
            sheet.addCell(new Label(8, 0, "grandTotal"));
            sheet.addCell(new Label(9, 0, "Created Date"));
            sheet.addCell(new Label(10, 0, "Item Name"));
            sheet.addCell(new Label(11, 0, "Category"));
            sheet.addCell(new Label(12, 0, "invoiceDate"));


            int i = 0;


            for (Transaction item : mTransactionList) {
                i = i + 1;
                sheet.addCell(new Label(0, i, String.valueOf(i)));
                sheet.addCell(new Label(1, i, String.valueOf(item.getTransactionId())));
                sheet.addCell(new Label(2, i, String.valueOf(item.getTransMasterId())));
                sheet.addCell(new Label(3, i, String.valueOf(item.getItemId())));
                sheet.addCell(new Label(4, i, String.valueOf(item.getQty())));
                sheet.addCell(new Label(5, i, String.valueOf(item.getPrice())));
                sheet.addCell(new Label(6, i, String.valueOf(item.getVatPercentage())));
                sheet.addCell(new Label(7, i, String.valueOf(item.getVat())));
                sheet.addCell(new Label(8, i, String.valueOf(item.getGrandTotal())));
                sheet.addCell(new Label(9, i, item.getCreatedDate()));
                sheet.addCell(new Label(10, i, item.getItemName()));
                sheet.addCell(new Label(11, i, String.valueOf(item.getCategory())));
                sheet.addCell(new Label(12, i, String.valueOf(item.getInvoiceDate())));


            }

            workbook.write();
            workbook.close();
            // Toast.makeText(getActivity(),"Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();
            getTransactionMaster();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getTransactionMaster() {
        LiveData<List<TransactionMaster>> listLiveData = mDatabase.mTransactionMasterDao().getAllItems();
        listLiveData.observe(this, this::exportTransactionMaster);

    }

    private void exportTransactionMaster(List<TransactionMaster> mTransactionList) {
        File sd = Environment.getExternalStorageDirectory();

        String csvFile = "transactionmaster" + System.currentTimeMillis() + ".xls";

        File directory = new File(sd.getAbsolutePath() + "/POS Backup");
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("Transaction Master", 0);
            // column and row


            sheet.addCell(new Label(0, 0, "Sr No."));
            sheet.addCell(new Label(1, 0, "TransMaster Id"));
            sheet.addCell(new Label(2, 0, "Item Total Amount"));
            sheet.addCell(new Label(3, 0, "vatAmount"));
            sheet.addCell(new Label(4, 0, "discountAmount"));
            sheet.addCell(new Label(5, 0, "totalQty"));
            sheet.addCell(new Label(6, 0, "grandTotal"));

            sheet.addCell(new Label(7, 0, "Created Date"));
            sheet.addCell(new Label(8, 0, "invoiceNo"));
            sheet.addCell(new Label(9, 0, "invoiceDate"));
            sheet.addCell(new Label(10, 0, "isSale"));
            sheet.addCell(new Label(11, 0, "isCashBankOrBoth"));
            sheet.addCell(new Label(12, 0, "shopId"));
            sheet.addCell(new Label(13, 0, "cash"));
            sheet.addCell(new Label(14, 0, "card"));
            sheet.addCell(new Label(15, 0, "type"));
            int i = 0;


            for (TransactionMaster item : mTransactionList) {
                i = i + 1;
                sheet.addCell(new Label(0, i, String.valueOf(i)));
                sheet.addCell(new Label(1, i, String.valueOf(item.getTransMasterId())));
                sheet.addCell(new Label(2, i, String.valueOf(item.getItemTotalAmount())));
                sheet.addCell(new Label(3, i, String.valueOf(item.getVatAmount())));
                sheet.addCell(new Label(4, i, String.valueOf(item.getDiscountAmount())));
                sheet.addCell(new Label(5, i, String.valueOf(item.getTotalQty())));
                sheet.addCell(new Label(6, i, String.valueOf(item.getGrandTotal())));
                sheet.addCell(new Label(7, i, item.getCreatedDate()));
                sheet.addCell(new Label(8, i, String.valueOf(item.getInvoiceNo())));
                sheet.addCell(new Label(9, i, String.valueOf(item.getInvoiceDate())));

                sheet.addCell(new Label(10, i, String.valueOf(item.isSale())));
                sheet.addCell(new Label(11, i, String.valueOf(item.getIsCashBankOrBoth())));
                sheet.addCell(new Label(12, i, String.valueOf(item.getShopId())));
                sheet.addCell(new Label(13, i, String.valueOf(item.getCash())));
                sheet.addCell(new Label(14, i, String.valueOf(item.getCard())));
                sheet.addCell(new Label(15, i, String.valueOf(item.getType())));
            }

            workbook.write();
            workbook.close();

            getInventoryMaster();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getInventoryMaster() {
        LiveData<List<InventoryMaster>> listLiveData = mDatabase.mInventoryMasterDao().getAllItems();
        listLiveData.observe(this, this::exportInventoryMaster);

    }

    private void exportInventoryMaster(List<InventoryMaster> mTransactionList) {
        File sd = Environment.getExternalStorageDirectory();

        String csvFile = "inventorynmaster" + System.currentTimeMillis() + ".xls";

        File directory = new File(sd.getAbsolutePath() + "/POS Backup");
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("Inventory Master", 0);


            sheet.addCell(new Label(0, 0, "Sr No."));
            sheet.addCell(new Label(1, 0, "TransMaster Id"));
            sheet.addCell(new Label(2, 0, "invoiceNo"));
            sheet.addCell(new Label(3, 0, "Item Total Amount"));
            sheet.addCell(new Label(4, 0, "Created Date"));
            sheet.addCell(new Label(5, 0, "supplier"));
            sheet.addCell(new Label(6, 0, "refference"));
            sheet.addCell(new Label(7, 0, "purchaseDate"));

            int i = 0;


            for (InventoryMaster item : mTransactionList) {
                i = i + 1;
                sheet.addCell(new Label(0, i, String.valueOf(i)));
                sheet.addCell(new Label(1, i, String.valueOf(item.getTransMasterId())));
                sheet.addCell(new Label(2, i, String.valueOf(item.getInvoiceNo())));
                sheet.addCell(new Label(3, i, String.valueOf(item.getItemTotalAmount())));
                sheet.addCell(new Label(4, i, item.getCreatedDate()));
                sheet.addCell(new Label(5, i, item.getSupplier()));
                sheet.addCell(new Label(6, i, item.getRefference()));
                sheet.addCell(new Label(7, i, item.getPurchaseDate()));
            }

            workbook.write();
            workbook.close();

            getInventoryTransaction();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getInventoryTransaction() {
        LiveData<List<InventoryTransaction>> listLiveData = mDatabase.mInventoryTransactionDao().getAllItems();
        listLiveData.observe(this, this::exportInventoryTransaction);

    }

    private void exportInventoryTransaction(List<InventoryTransaction> mTransactionList) {
        File sd = Environment.getExternalStorageDirectory();

        String csvFile = "inventoryntransaction" + System.currentTimeMillis() + ".xls";

        File directory = new File(sd.getAbsolutePath() + "/POS Backup");
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("Inventory Transaction", 0);

            sheet.addCell(new Label(0, 0, "Sr No."));
            sheet.addCell(new Label(1, 0, "Transaction Id"));
            sheet.addCell(new Label(2, 0, "TransMaster Id"));
            sheet.addCell(new Label(3, 0, "itemId"));
            sheet.addCell(new Label(4, 0, "qty"));
            sheet.addCell(new Label(5, 0, "price"));
            sheet.addCell(new Label(6, 0, "vatPercentage"));
            sheet.addCell(new Label(7, 0, "Vat"));
            sheet.addCell(new Label(8, 0, "grandTotal"));
            sheet.addCell(new Label(9, 0, "Created Date"));
            sheet.addCell(new Label(10, 0, "Item Name"));
            sheet.addCell(new Label(11, 0, "Category"));
            sheet.addCell(new Label(12, 0, "invoiceDate"));


            int i = 0;


            for (InventoryTransaction item : mTransactionList) {
                i = i + 1;
                sheet.addCell(new Label(0, i, String.valueOf(i)));
                sheet.addCell(new Label(1, i, String.valueOf(item.getTransactionId())));
                sheet.addCell(new Label(2, i, String.valueOf(item.getTransMasterId())));
                sheet.addCell(new Label(3, i, String.valueOf(item.getItemId())));
                sheet.addCell(new Label(4, i, String.valueOf(item.getQty())));
                sheet.addCell(new Label(5, i, String.valueOf(item.getPrice())));
                sheet.addCell(new Label(6, i, String.valueOf(item.getVatPercentage())));
                sheet.addCell(new Label(7, i, String.valueOf(item.getVat())));
                sheet.addCell(new Label(8, i, String.valueOf(item.getGrandTotal())));
                sheet.addCell(new Label(9, i, item.getCreatedDate()));
                sheet.addCell(new Label(10, i, item.getItemName()));
                sheet.addCell(new Label(11, i, String.valueOf(item.getCategory())));
                sheet.addCell(new Label(12, i, String.valueOf(item.getInvoiceDate())));


            }

            workbook.write();
            workbook.close();
            progress.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("ALL Tables Exported in a Excel Sheet to POS Backup folder");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            {
                                Intent intent = new Intent (Intent.ACTION_GET_CONTENT);
                                Uri uri = Uri.parse (Environment.getExternalStorageDirectory().getAbsolutePath() + "/POS Backup");
                                intent.setDataAndType (uri, "resource/folder");
                                startActivity (Intent.createChooser (intent, "Open folder"));

                            }
                        }
                    });


            AlertDialog alert11 = builder.create();
            alert11.show();
           // Toast.makeText(getActivity(), "ALL Tables Exported in a Excel Sheet to POS Backup folder", Toast.LENGTH_SHORT).show();

        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
