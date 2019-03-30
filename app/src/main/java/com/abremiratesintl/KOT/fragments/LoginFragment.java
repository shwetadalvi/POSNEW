package com.abremiratesintl.KOT.fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.abremiratesintl.KOT.AdminPasswordDialog;
import com.abremiratesintl.KOT.BaseFragment;
import com.abremiratesintl.KOT.MainActivity;
import com.abremiratesintl.KOT.R;
import com.abremiratesintl.KOT.dbHandler.AppDatabase;
import com.abremiratesintl.KOT.dbHandler.UserDbHandler;
import com.abremiratesintl.KOT.models.Admin;
import com.abremiratesintl.KOT.utils.PrefUtils;

import java.util.HashMap;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.abremiratesintl.KOT.utils.Constants.DEAFULT_PREFS;
import static com.abremiratesintl.KOT.utils.Constants.FIRST_RUN_PREF_KEY;
import static com.abremiratesintl.KOT.utils.Constants.LOGIN_PREF_KEY;

//import net.sqlcipher.database.SQLiteDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment implements View.OnFocusChangeListener, AdminPasswordDialog.AdminPasswordManagementListener {

    @BindView(R.id.pin1)
    TextView pin1;
    @BindView(R.id.pin2)
    TextView pin2;
    @BindView(R.id.pin3)
    TextView pin3;
    @BindView(R.id.pin4)
    TextView pin4;
    @BindView(R.id.btn1)
    TextView btn1;
    @BindView(R.id.btn2)
    TextView btn2;
    @BindView(R.id.btn3)
    TextView btn3;
    @BindView(R.id.btn4)
    TextView btn4;
    @BindView(R.id.btn5)
    TextView btn5;
    @BindView(R.id.btn6)
    TextView btn6;
    @BindView(R.id.btn7)
    TextView btn7;
    @BindView(R.id.btn8)
    TextView btn8;
    @BindView(R.id.btn9)
    TextView btn9;
    @BindView(R.id.ivBackSpace)
    ImageView ivBackSpace;
    @BindView(R.id.btnOk)
    TextView btnOk;
    @BindDrawable(R.drawable._line_colored)
    Drawable focusedText;
    @BindDrawable(R.drawable._line)
    Drawable unFocusedText;
    TextView currentFocusedTextView;
    private Unbinder unbinder;
    PrefUtils mPrefUtils;
    private int mFromWhere;
    AppDatabase mDatabase;
    private Admin admin = new Admin();
    private View mView;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, mView);
        mPrefUtils = new PrefUtils(getContext());
        mDatabase = AppDatabase.getInstance(getContext());
        LoginFragmentArgs args = LoginFragmentArgs.fromBundle(getArguments());
        mFromWhere = args.getFromFragment();


        Thread t = new Thread(() -> {
            admin = mDatabase.mAdminDao().getAdmin1();
        });
        t.start();

        if (mFromWhere == 1) {
            if (isAlreadyLogedIn()) {
                Navigation.findNavController(mView).navigate(R.id.action_loginFragment_to_homeFragment2);
            }
        }

        return mView;
    }

    private boolean isAlreadyLogedIn() {
        return mPrefUtils.getBooleanPrefrence(DEAFULT_PREFS, FIRST_RUN_PREF_KEY, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pin1.addTextChangedListener(new LoginTextWatcher(pin1));
        pin2.addTextChangedListener(new LoginTextWatcher(pin2));
        pin3.addTextChangedListener(new LoginTextWatcher(pin3));
        pin4.addTextChangedListener(new LoginTextWatcher(pin4));

        pin1.setOnFocusChangeListener(this);
        pin2.setOnFocusChangeListener(this);
        pin3.setOnFocusChangeListener(this);
        pin4.setOnFocusChangeListener(this);
        pin1.requestFocus();
        currentFocusedTextView = pin1;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override public void onFocusChange(View v, boolean hasFocus) {
        currentFocusedTextView = (TextView) v;
    }

    @OnClick(R.id.btn1)
    public void onClickedBtn1() {
//        currentFocusedTextView = (TextView) oc;
        currentFocusedTextView.setText("1");
    }

    @OnClick(R.id.btn2)
    public void onClickedBtn2() {
        currentFocusedTextView.setText("2");
    }

    @OnClick(R.id.btn3)
    public void onClickedBtn3() {
        currentFocusedTextView.setText("3");
    }

    @OnClick(R.id.btn4)
    public void onClickedBtn4() {
        currentFocusedTextView.setText("4");
    }

    @OnClick(R.id.btn5)
    public void onClickedBtn5() {
        currentFocusedTextView.setText("5");
    }

    @OnClick(R.id.btn6)
    public void onClickedBtn6() {
        currentFocusedTextView.setText("6");
    }

    @OnClick(R.id.btn7)
    public void onClickedBtn7() {
        currentFocusedTextView.setText("7");
    }

    @OnClick(R.id.btn8)
    public void onClickedBtn8() {
        currentFocusedTextView.setText("8");
    }

    @OnClick(R.id.btn9)
    public void onClickedBtn9() {
        currentFocusedTextView.setText("9");
    }

    @OnClick(R.id.btnOk)
    public void onClickedBtnOK(View view) {
        String pin = getString(pin1) + getString(pin2) + getString(pin3) + getString(pin4);
        mPrefUtils.putBooleanPreference(DEAFULT_PREFS, FIRST_RUN_PREF_KEY, false);
        mPrefUtils.putBooleanPreference(DEAFULT_PREFS, LOGIN_PREF_KEY, true);
        if (mFromWhere == 1) {
            doLogin(pin);
        } else if (mFromWhere == 2) {
            adminLogin(pin);
//
        }
    }

    private void adminLogin(String pin) {
        HashMap<String, String> map = new HashMap<>();
        UserDbHandler userDbHandler = new UserDbHandler();
        if(admin != null){
            if(pin.equals(admin.getPassword())) {
                showSnackBar(mView, "match", 1000);
                Navigation.findNavController(mView).navigate(R.id.action_loginFragment_to_userSettingsFragment);
            }
        }
        if (pin.equals(MainActivity.getKey())) {
            AdminPasswordDialog adminPasswordDialog = new AdminPasswordDialog(getContext(), this);
            adminPasswordDialog.showAdminDialog();
        }

    }

    private String getString(TextView textView) {
        return textView.getText().toString();
    }

    @OnClick(R.id.ivBackSpace)
    public void onClickedBackSpace() {
        currentFocusedTextView.setText("");
    }

    private void doLogin(String pin) {
        HashMap<String, String> map = new HashMap<>();
        if (pin.equals(MainActivity.getKey())) {
            AdminPasswordDialog adminPasswordDialog = new AdminPasswordDialog(getContext(), this);
            adminPasswordDialog.showAdminDialog();
        } else {

        }
    }

    private static final String TAG = "LoginFragment";

    @Override public void passwordMatchResult(boolean isMatch) {
        if (isMatch) {
            showSnackBar(mView, "match", 1000);
            Navigation.findNavController(mView).navigate(R.id.action_loginFragment_to_superAdminHome);
            return;
        }
        showSnackBar(mView.getRootView(), "unmatch", 1000);
    }

    class LoginTextWatcher implements TextWatcher {
        private View view;

        private LoginTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();

            switch (view.getId()) {
                case R.id.pin1:
                    if (text.length() == 1) {
                        pin2.requestFocus();
                        pin1.setBackground(focusedText);
                    }
                    break;
                case R.id.pin2:
                    if (text.length() == 1) {
                        pin3.requestFocus();
                        pin2.setBackground(focusedText);
                    } else {
                        pin1.requestFocus();
                        pin2.setBackground(unFocusedText);
                    }
                    break;
                case R.id.pin3:
                    if (text.length() == 1) {
                        pin4.requestFocus();
                        pin3.setBackground(focusedText);
                    } else {
                        pin2.requestFocus();
                        pin3.setBackground(unFocusedText);
                    }
                    break;
                case R.id.pin4:
                    pin4.setBackground(focusedText);
                    if (text.length() == 0) {
                        pin3.requestFocus();
                        pin4.setBackground(unFocusedText);
                    }
                    break;
            }
        }
    }

    @Override public void onPause() {
        super.onPause();

    }
}
