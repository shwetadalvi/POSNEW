package com.abremiratesintl.KOT;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;

public class AdminPasswordDialog extends AlertDialog {

    private Builder mAlertDialog;
    private AdminPasswordManagementListener mAdminPasswordManagementListener;
    private String password;

    public AdminPasswordDialog(Context context, AdminPasswordManagementListener adminPasswordManagementListener) {
        super(context);
        mAlertDialog = new Builder(context);
        mAdminPasswordManagementListener = adminPasswordManagementListener;
        mAlertDialog.setTitle(context.getResources().getString(R.string.admin_login));
        mAlertDialog.setMessage(context.getResources().getString(R.string.enter_password));
    }

    public void showAdminDialog() {
        String pass = MainActivity.getPassword();

        final EditText input = new EditText(getContext());
        input.setEms(15);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 0, 16, 0);
        input.setLayoutParams(lp);
        mAlertDialog.setView(input);
        mAlertDialog.setIcon(R.drawable.ic_key);

        mAlertDialog.setPositiveButton("YES",
                (dialog, which) -> {
                    password = input.getText().toString();
                    if (pass.equals(password)) {
                        mAdminPasswordManagementListener.passwordMatchResult(true);
                        dialog.dismiss();
                    } else {
                        mAdminPasswordManagementListener.passwordMatchResult(false);
                        dismiss();
                    }
                });

        mAlertDialog.setNegativeButton("NO",
                (dialog, which) -> dialog.cancel());

        mAlertDialog.show();
    }

    public interface AdminPasswordManagementListener {
        void passwordMatchResult(boolean isMatch);
    }
}