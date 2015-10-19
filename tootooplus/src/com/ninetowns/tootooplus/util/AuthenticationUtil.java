package com.ninetowns.tootooplus.util;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.ninetowns.tootooplus.R;


public class AuthenticationUtil {
    public static interface HttpAuthenticationHandler {
        void process(String name, String password);

        void cancel();
    }

    /**
     * Displays an http-authentication dialog.
     */
    public static void showHttpAuthentication(Context context,
            final HttpAuthenticationHandler httpHandler, final String host,
            final String realm, final String title, final String name,
            final String password, int focusId) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View v = factory.inflate(R.layout.http_authentication, null);
        if (name != null) {
            ((EditText) v.findViewById(R.id.username_edit)).setText(name);
        }
        if (password != null) {
            ((EditText) v.findViewById(R.id.password_edit)).setText(password);
        }

        String titleText = title;
        if (titleText == null) {
            titleText = "登录"
                    .replace("%s1", host).replace("%s2", realm);
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(titleText)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setView(v)
                .setPositiveButton("登录",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                String nm = ((EditText) v
                                        .findViewById(R.id.username_edit))
                                        .getText().toString();
                                String pw = ((EditText) v
                                        .findViewById(R.id.password_edit))
                                        .getText().toString();
                                if (httpHandler != null) {
                                    httpHandler.process(nm, pw);
                                }
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                if (httpHandler != null) {
                                    httpHandler.cancel();
                                }
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        if (httpHandler != null) {
                            httpHandler.cancel();
                        }
                    }
                }).create();
        // Make the IME appear when the dialog is displayed if applicable.
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        if (focusId != 0) {
            dialog.findViewById(focusId).requestFocus();
        } else {
            v.findViewById(R.id.username_edit).requestFocus();
        }
    }
}