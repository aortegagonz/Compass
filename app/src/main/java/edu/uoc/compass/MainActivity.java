package edu.uoc.compass;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import edu.uoc.compass.util.DBHelper;
import edu.uoc.compass.util.DBSender;
import edu.uoc.compass.util.Util;

/**
 * This activity just contain buttons to connect with the rest of activities
 *
 * @author Antonio Ortega
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.record_path_btn:
                // Starts Path recording
                intent = new Intent(this, PathSettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.record_grid_btn:
                // Starts Grid recording
                intent = new Intent(this, GridSettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.view_data_btn:
                // Starts View Data
                intent = new Intent(this, ViewRecordingsActivity.class);
                startActivity(intent);
                break;
            case R.id.delete_all_data_btn:
                // Get database connection
                final DBHelper dbHelper = new DBHelper(this);

                // Shows confirmation dialog to delete all data
                final Activity activity = this;
                Util.showAcceptCancelDialog(
                        this,
                        getString(R.string.app_name).toString(),
                        getString(R.string.confirm_delete_data).toString(),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete all information in database
                        dbHelper.deleteAllData();
                        Util.showMessage(
                                activity,
                                getString(R.string.app_name).toString(),
                                getString(R.string.all_data_deleted).toString(),
                                null);
                            }
                },null);
                break;
            case R.id.send_data_btn:
                // Send all data in CSV format
                DBSender.sendData(this);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }
}