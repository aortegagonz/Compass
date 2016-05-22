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

import edu.uoc.compass.R;

/**
 * Library with static functions used in the project
 *
 * @author Antonio Ortega
 */
public class Util {
    /**
     * Shows an Information Dialog with a message
     * @param activity parent activity
     * @param title dialog title
     * @param message information to show in dialog
     * @param onClickListener listener executed when accept button pressed
     */
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

    /**
     * Shows an Alert Dialog with a message
     * @param activity parent activity
     * @param title dialog title
     * @param message string describing error
     * @param onClickListener listener executed when accept button pressed
     */
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

    /**
     * Shows an alert message with two buttons:  "I'm sure" and "retry"
     * @param activity parent activity
     * @param title dialog title
     * @param message string to show
     * @param onSureListener listener executed when "sure" button pressed
     * @param onRetryListener listener executed when "retry" button pressed
     */
    public static void showSureRetryDialog(final Activity activity, String title, String message, final DialogInterface.OnClickListener onSureListener, final DialogInterface.OnClickListener onRetryListener) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.response_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onSureListener != null) {
                            onSureListener.onClick(dialog, which);
                        }
                    }
                })
                .setNegativeButton(R.string.response_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onRetryListener!= null) {
                            onRetryListener.onClick(dialog, which);
                        }
                    }
                })
                .show();
    }

    /**
     * Shows an alert message with two buttons:  "accept" and "cancel"
     * @param activity parent activity
     * @param title dialog title
     * @param message string to show
     * @param onAcceptListener listener executed when "accept" button pressed
     * @param onCancelListener listener executed when "cancel" button pressed
     */
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


    /**
     * Set button color depending of its status
     * @param button button to set color
     * @param enabledColor color to set when button is enabled
     * @param disabledColor color to set when button is disabled
     */
    public static void manageImageButtonColor(ImageButton button, int enabledColor, int disabledColor){
        if (button.isEnabled()) {
            button.setColorFilter(enabledColor);
        } else {
            button.setColorFilter(disabledColor);
        }
    }

    /**
     * Disables input characters in EditText
     * @param editText Edit text to process
     * @param characters String containing characters to disable (eg: ",;")
     */
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
