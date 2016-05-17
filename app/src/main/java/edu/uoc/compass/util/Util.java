package edu.uoc.compass.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by aortega on 5/5/16.
 */
public class Util {
    public static void showMessage(final Activity activity, String title, String message, final DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onClickListener != null) {
                            onClickListener.onClick(dialog, which);
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    public static void showErrorMessage(final Activity activity, String title, String message, final DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onClickListener != null) {
                            onClickListener.onClick(dialog, which);
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void showAcceptCancelDialog(final Activity activity, String title, String message, final DialogInterface.OnClickListener onAcceptListener, final DialogInterface.OnClickListener onCancelListener) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onAcceptListener != null) {
                            onAcceptListener.onClick(dialog, which);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onCancelListener!= null) {
                            onCancelListener.onClick(dialog, which);
                        }
                    }
                })
                .show();
    }

    public static void manageImageButtonColor(ImageButton button, int enabledColor, int disabledColor){
        if (button.isEnabled()) {
            button.setColorFilter(enabledColor);
        } else {
            button.setColorFilter(disabledColor);
        }
    }

    public static void disableCharacters(EditText editText, String characters) {
        editText.setFilters(new InputFilter[]{ new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String blockCharacterSet = ",";
                if (source != null && blockCharacterSet.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        }});
    }
}
