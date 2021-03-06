package com.abremiratesintl.KOT.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
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
import com.abremiratesintl.KOT.adapters.InventoryReportAdapter;
import com.abremiratesintl.KOT.adapters.ItemSpinnerAdapter;
import com.abremiratesintl.KOT.adapters.ItemwiseReportAdapter;
import com.abremiratesintl.KOT.adapters.ReportAdapter;
import com.abremiratesintl.KOT.adapters.VATwiseReportAdapter;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.interfaces.ClickListeners;
import com.abremiratesintl.KOT.models.InventoryMaster;
import com.abremiratesintl.KOT.models.InventoryTransaction;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.Transaction;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.utils.Constants;
import com.abremiratesintl.KOT.views.CustomSpinner;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.navigation.Navigation;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryReportFragment extends BaseFragment implements ClickListeners.ItemClickWithView<InventoryMaster> {
  /*  @BindView(R.id.footer)
    RelativeLayout footer;
    @BindView(R.id.header)
    LinearLayout header;
    @BindView(R.id.textTotal)
    TextView textTotal;*/
    @BindView(R.id.reportRecyclerView)
    RecyclerView reportRecyclerview;
    /*@BindView(R.id.filter)
    LinearLayout filter;
    @BindView(R.id.fromDate)
    TextView fromDate;
    @BindView(R.id.toDate)
    TextView toDate;*/
    @BindView(R.id.emptyReportView)
    ConstraintLayout emptyView;
    private Unbinder mUnbinder;
    private AppDatabase mDatabase;
    View mSelectedDateView;
    private List<InventoryMaster> mTransactionMasterList;

    public InventoryReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_report, container, false);
        mDatabase = AppDatabase.getInstance(getContext());
        mUnbinder = ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).changeTitle(" Inventory REPORTS");

        setHasOptionsMenu(true);
        fetchTransactions();
        return view;
    }

    private void fetchTransactions() {
        LiveData<List<InventoryMaster>> listLiveData = mDatabase.mInventoryMasterDao().getAllItems();
        listLiveData.observe(this, this::setUpRecycler);
    }

    private void setUpRecycler(List<InventoryMaster> transactionMasterList) {
        mTransactionMasterList = transactionMasterList;
        if (transactionMasterList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            reportRecyclerview.setVisibility(View.GONE);
         //   footer.setVisibility(View.GONE);
           // header.setVisibility(View.GONE);
            return;
        }
       // footer.setVisibility(View.VISIBLE);
      //  header.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        reportRecyclerview.setVisibility(View.VISIBLE);
        InventoryReportAdapter adapter = new InventoryReportAdapter(transactionMasterList, this);
        reportRecyclerview.setAdapter(adapter);
      //  setFooterValues(transactionMasterList);
    }


    private void setFooterValues(List<InventoryMaster> transactionMasterList) {

        float total = 0;
        for (InventoryMaster items : transactionMasterList) {

            total = total + items.getItemTotalAmount();
        }

      //  textTotal.setText(getResources().getString(R.string.currency) + " " + String.valueOf(Constants.round(total, 2)));

    }


   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_filter, menu);
    }

    int menuClickCount = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                menuClickCount++;
                if (menuClickCount % 2 == 0) {
                    onClickedFilter(false);
                } else {
                    onClickedFilter(true);


                }
                break;
            case R.id.menu_all:
                onClickedFilter(false);
                menuClickCount = 0;
                LiveData<List<InventoryMaster>> listLiveData = mDatabase.mInventoryMasterDao().getAllItems();
                listLiveData.observe(this, this::setUpRecycler);
                break;
            case R.id.export:
                exportFileToExcel();
                break;
        }

        return true;
    }
*/
    private void exportFileToExcel() {

        File sd = Environment.getExternalStorageDirectory();

        String csvFile = "pos_vat_report" + System.currentTimeMillis() + ".xls";
        File directory = new File(sd.getAbsolutePath() + "/Reports");
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
                WritableSheet sheet = workbook.createSheet("Vat Report", 0);
                // column and row

                sheet.addCell(new Label(0, 0, Constants.COMPANY_SL_NO));
                sheet.addCell(new Label(1, 0, Constants.COMPANY_ORDER_NO));

                sheet.addCell(new Label(2, 0, Constants.COMPANY_DATE));
                sheet.addCell(new Label(3, 0, "Vatable Amount"));
                sheet.addCell(new Label(4, 0, "Vat Amount"));
                sheet.addCell(new Label(5, 0, Constants.COMPANY_ITEM_AMOUNT));
                int i = 0;

                Log.e("Inside123", " data" + mTransactionMasterList.size());
                for (InventoryMaster item : mTransactionMasterList) {
                    i = i + 1;
                  /*  sheet.addCell(new Label(0, i, String.valueOf(i)));
                    sheet.addCell(new Label(1, i, String.valueOf(item.getInvoiceNo())));
                    sheet.addCell(new Label(2, i, String.valueOf(item.getInvoiceDate())));
                    sheet.addCell(new Label(3, i,String.valueOf(item.getItemTotalAmount())));
                    sheet.addCell(new Label(4, i, String.valueOf(item.getVatAmount())));
                    sheet.addCell(new Label(5, i, String.valueOf(item.getGrandTotal())));*/
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            workbook.write();
            workbook.close();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Data Exported in a Excel Sheet");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            {
                               /* Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Reports");
                                intent.setDataAndType(uri, "resource/folder");
                                startActivity(Intent.createChooser(intent, "Open folder"));*/
                                Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Reports");
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(selectedUri, "resource/folder");
                                startActivity(intent);
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

 /*   private void onClickedFilter(boolean b) {
        if (b) {
            filter.setVisibility(View.VISIBLE);
        } else {
            filter.setVisibility(View.GONE);
        }
    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }




    private void sortedItem(String fromDate, String toDate) {
        if (toDate.equals(getStringfromResource(R.string.present))) {
            toDate = Constants.getCurrentDate();
        }
     /*   if (fromDate.equals("")){
            showSnackBar(getView(),getStringfromResource(R.string.present),1000);
            return;
        }*/
        LiveData<List<InventoryMaster>> listLiveData = mDatabase.mInventoryMasterDao().findItemsByBetween(fromDate, toDate);
        listLiveData.observe(this, this::setUpRecycler);
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
    public void onClickedItem(View view,InventoryMaster item) {
      //  Navigation.findNavController(view).navigate(InventoryReportFragmen.actionAddNewItemToCheckoutFragment(item));
        Bundle bundle = new Bundle();
        bundle.putInt("id", Integer.parseInt(item.getInvoiceNo()));
        Navigation.findNavController(getView()).navigate(R.id.action_inventoryReportFragment_to_inventoryDetailsFragment, bundle);
    }
}
