package com.abremiratesintl.KOT.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.adapters.CategoryReportAdapter;
import com.abremiratesintl.KOT.adapters.CategorySpinnerAdapter;
import com.abremiratesintl.KOT.adapters.CategorySpinnerAdapter;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.Cashier;
import com.abremiratesintl.KOT.models.Category;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.Transaction;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.utils.PrefUtils;
import com.abremiratesintl.KOT.views.CustomSpinner;


import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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


public class CategoryReportFragment extends BaseFragment implements ClickListeners.ItemClick<Transaction>, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener, CustomSpinner.OnSpinnerEventsListener {
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
    @BindView(R.id.reportRecyclerView)
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
    private List<Category> mItemsList;
    private Category mSelectedItem;
private List<Transaction> mTransactionList;
    private Cashier cashier = new Cashier();
    private boolean isCashier = false;
    PrefUtils mPrefUtils ;
    String selFromDate,selToDate;
    public CategoryReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_report, container, false);
        mDatabase = AppDatabase.getInstance(getContext());
        mUnbinder = ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).changeTitle(" Category REPORTS");
        mPrefUtils = new PrefUtils(getContext());

        Thread t = new Thread(() -> {
            cashier = mDatabase.mCashierDao().getCashier();
        });
        t.start();

        if(mPrefUtils.getStringPrefrence(Constants.DEAFULT_PREFS,Constants.USER_TYPE,Constants.CASHIER).equals(Constants.CASHIER))
            isCashier = true;

        spinItem.setOnItemSelectedListener(this);
        spinItem.setSpinnerEventsListener(this);
        getCategories();

        setHasOptionsMenu(true);

        return view;
    }

    private void getCategories() {
        LiveData<List<Category>> itemLiveList = mDatabase.mCategoryDao().getAllCategory();
        itemLiveList.observe(this, items -> {
            if (items == null || items.size() == 0) {
                return;
            }
            mItemsList = items;
            Category category = new Category();
            category.setCategoryId(-1);
            category.setCategoryName("All");
            category.setCreatedDate(Constants.getCurrentDate());
            category.setDeleted(false);
            mItemsList.add(0,category);
            mSelectedItem = mItemsList.get(0);

            setUpSpinner();
        });
    }

    private void setUpSpinner() {
        Log.e("Inside :", "value " + mItemsList.size());
        CategorySpinnerAdapter<Category> CategorySpinnerAdapter = new CategorySpinnerAdapter<>(getContext(), R.id.categoryListItem, mItemsList);
        spinItem.setAdapter(CategorySpinnerAdapter);
        fetchTransactions();
    }

    private void fetchTransactions() {
        LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().getTodaysAllItems(Constants.getCurrentDate());

        listLiveData.observe(this, this::setUpRecycler);
    }

    private void setUpRecycler(List<Transaction> transactionList) {
        mTransactionList = transactionList;
        Log.e("INSERTION MASTER1", "inside22" + transactionList.size());
        if (transactionList.size() == 0) {
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
        CategoryReportAdapter adapter = new CategoryReportAdapter(transactionList, this);
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
    @OnClick(R.id.fromDate)
    public void onClickedFromDate() {
        mSelectedDateView = fromDate;
        showDatePicker();
    }

    @OnClick(R.id.toDate)
    public void onClickedToDate() {
        mSelectedDateView = toDate;
        showDatePicker();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.item_report_menu, menu);
    }

    int menuClickCount = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                menuClickCount++;
                if (menuClickCount % 2 == 0) {
                    onClickedFilter(false);
                } else {
                    onClickedFilter(true);


                }
                break;
            case R.id.export:
                if((isCashier &&  (cashier== null )) || (isCashier && cashier!= null && (!cashier.isCategoryReportExport())) )
                    showSnackBar(getView(),"Not Allowed!!",1000);
                else
                exportFileToExcel();
                break;
        }

        return true;
    }

    private void exportFileToExcel() {

        File sd = Environment.getExternalStorageDirectory();

        String csvFile ="pos_category_report"+System.currentTimeMillis()+ ".xls";
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
                WritableSheet sheet = workbook.createSheet("Category Report", 0);
                // column and row

                sheet.addCell(new Label(0, 0, Constants.COMPANY_SL_NO));
                sheet.addCell(new Label(1, 0, Constants.COMPANY_ORDER_NO));
                sheet.addCell(new Label(2, 0, Constants.COMPANY_ITEM_DESCRIPTION));
                sheet.addCell(new Label(3, 0, Constants.COMPANY_ITEM_QUANTITY));
                sheet.addCell(new Label(4, 0, Constants.COMPANY_ITEM_AMOUNT));



                int i = 0;

                Log.e("Inside123","live data"+mTransactionList.size());
                for (Transaction item : mTransactionList) {
                    i = i + 1;
                    sheet.addCell(new Label(0, i, String.valueOf(i)));
                    sheet.addCell(new Label(1, i, String.valueOf(item.getTransactionId())));
                    sheet.addCell(new Label(2, i, item.getItemName()));
                    sheet.addCell(new Label(3, i, String.valueOf(item.getQty())));
                    sheet.addCell(new Label(4, i, String.valueOf(item.getPrice())));
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onClickedItem(Transaction item) {

    }

    public void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
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
            selFromDate = Constants.getCurrentDate();
        }
       /* if (fromDate.equals("")) {
            showSnackBar(getView(), getStringfromResource(R.string.present), 1000);
            return;
        }*/
        if(mSelectedItem.getCategoryId() == -1) {
            LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().findAllItemsByCategoryBetween(fromDate, toDate);
            listLiveData.observe(this, this::setUpRecycler);
        }else {
            LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().findItemsByCategoryBetween(fromDate, toDate, mSelectedItem.getCategoryName());
            listLiveData.observe(this, this::setUpRecycler);
        }
    }

    private String getProperDate(int dayOrMonth) {
        if (dayOrMonth < 10) {
            return "0" + dayOrMonth;
        }
        return String.valueOf(dayOrMonth);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedItem = mItemsList.get(position);
        if(mSelectedItem.getCategoryId() == -1) {
            if(filter.getVisibility() == View.VISIBLE) {
                LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().findAllItemsByCategoryBetween(selFromDate, selToDate);
                listLiveData.observe(this, this::setUpRecycler);
            }else{
                LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().getTodaysAllItems(Constants.getCurrentDate());
                listLiveData.observe(this, this::setUpRecycler);
            }
        }else {
            if(filter.getVisibility() == View.VISIBLE) {
                LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().findItemsByCategoryBetween(selFromDate, selToDate, mSelectedItem.getCategoryName());
                listLiveData.observe(this, this::setUpRecycler);
            }else{
                LiveData<List<Transaction>> listLiveData = mDatabase.mTransactionDao().getAllItemsByCategoryName(mSelectedItem.getCategoryName());
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
}
