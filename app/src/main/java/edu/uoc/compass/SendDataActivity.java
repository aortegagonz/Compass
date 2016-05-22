package edu.uoc.compass;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import edu.uoc.compass.util.DBSender;
import edu.uoc.compass.util.Util;

/**
 * Used to export data in CVS format
 *
 * @author Antonio Ortega
 */

public class SendDataActivity extends AppCompatActivity {

    /**
     * ID used as event ID in onRequestPermissionsResult
     */
    private static final int REQUEST_WRITE_STORAGE = 1;
    /**
     * Permission needed to write CVS file into SD card
     */
    private static final String manifestPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);

        // Get layout references
        TextView messageLbl = (TextView)findViewById(R.id.send_wait_message);

        // Check for permission
        boolean hasPermission = ContextCompat.checkSelfPermission(this, manifestPermission) == PackageManager.PERMISSION_GRANTED;
        if (!hasPermission) {
            // If no permission try to get it
            requestPermission();
        } else {
            // Show waiting message
            messageLbl.setText(getText(R.string.send_wait));

            //Send data
            DBSender.sendData(this);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Check event ID (in this moment only REQUEST_WRITE_STORAGE event managed)
        if (requestCode == REQUEST_WRITE_STORAGE) {
            // Check if permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // When granted just reloads activity to send data
                finish();
                startActivity(getIntent());
            } else {
                // When not granted inform user permission is needed
                boolean shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(this, manifestPermission);
                if (shouldExplain) {
                    Util.showSureRetryDialog(
                            this,
                            getText(R.string.title_activity_sendig).toString(),
                            getText(R.string.explain_write_permissions).toString(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // If user is sure finishes activity, data can't be sent
                                    finish();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // When user accepts retrying tries to grant permission again
                                    requestPermission();
                                }
                            });
                } else {
                    // If user checked "Never show again" shows message and shows settings
                    Util.showMessage(
                            this,
                            getText(R.string.title_activity_sendig).toString(),
                            getText(R.string.error_write_permissions).toString(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    goToSettings();
                                }
                            });
                }
            }
        }
    }

    /**
     * Requests write permission to user
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{manifestPermission},
                REQUEST_WRITE_STORAGE);
    }

    /**
     * Shows application settings
     */
    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myAppSettings);
    }
}
