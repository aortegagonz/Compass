package edu.uoc.compass;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import edu.uoc.compass.util.DBHelper;
import edu.uoc.compass.util.Util;

/**
 * This activity shows a table with data recorded in the database
 *
 * @author Antonio Ortega
 */
public class ViewRecordingsActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PADDING = 50;
    private boolean odd = true;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recordings);
        tableLayout = (TableLayout)findViewById(R.id.view_path_table);
        loadData();
    }

    /**
     * Loads data from database inserting rows in the table layout
     */
    private void loadData() {
        // Get database connection
        DBHelper dbHelper = new DBHelper(this);

        // Create table header
        addHeader(tableLayout);

        // Get information from database
        ArrayList<DBHelper.Recording> records = dbHelper.getAllRecords();

        // For each record a row is inserted
        Iterator<DBHelper.Recording> iterator = records.iterator();
        while (iterator.hasNext()) {
            DBHelper.Recording record = iterator.next();
            addRow(tableLayout, record);
        }
    }

    /**
     * Add a row to the table layout with the headers
     * @param tableLayout parent tableLayout
     */
    private void addHeader(TableLayout tableLayout) {
        // Create Row
        TableRow tableRow = new TableRow(this);

        // Set row format
        int color = fetchAccentColor();
        tableRow.setBackgroundColor(color);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(layoutParams);
        addColumn(tableRow, "", Color.WHITE);
        addColumn(tableRow, getString(R.string.type), color);
        addColumn(tableRow, getString(R.string.name), color);

        // Add row
        tableLayout.addView(tableRow);
    }

    /**
     * Adds a row to the table layout
     * @param tableLayout parent TableLayout
     * @param recording Information from database to insert in the row
     */
    private void addRow(TableLayout tableLayout, DBHelper.Recording recording) {
        // Checks if row is odd or even
        int color = odd? Color.GRAY:Color.WHITE;
        odd = !odd;

        // Create row
        TableRow tableRow = new TableRow(this);

        // Set row format
        tableRow.setBackgroundColor(color);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(layoutParams);

        // Add columns to row
        addViewColumn(tableRow, color, recording);
        addColumn(tableRow, recording.getTypeName(this), color);
        addColumn(tableRow, recording.name, color);

        // Add row to table
        tableLayout.addView(tableRow);

        // Set tag with information about row
        tableRow.setTag(recording);
    }

    /**
     * Adds a column to a row
     * @param tableRow parent table
     * @param data String data to show in the column
     * @param backgroundColor background color for the cell
     */
    private void addColumn(TableRow tableRow, String data, int backgroundColor) {
        // Create view
        TextView textView = new TextView(this);

        // Set view data
        textView.setText(data);

        // Set view format
        textView.setPadding(PADDING, PADDING, PADDING, PADDING);
        textView.setBackgroundColor(backgroundColor);

        // Add cell to row
        tableRow.addView(textView);
    }

    /**
     * Adds initial cell to row with butons
     * @param tableRow parent table
     * @param color cell background color
     * @param record record to record button tag
     */
    private void addViewColumn(TableRow tableRow, int color, DBHelper.Recording record) {
        ImageButton button = new ImageButton(this);
        button.setTag(record);
        button.setImageResource(R.drawable.ic_delete_black_24dp);
        button.setBackgroundColor(color);
        tableRow.addView(button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // When button clicked current row is deleted

        // Get record information from tag
        final DBHelper.Recording recording = (DBHelper.Recording)v.getTag();

        // Get database connection
        final DBHelper dbHelper = new DBHelper(this);

        // Show confirmation dialog
        Util.showAcceptCancelDialog(
                this,
                getString(R.string.title_activity_view_recordings).toString(),
                getString(R.string.confirm_delete, recording.getTypeName(this)).toString(),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete from database
                dbHelper.deleteRecording(recording);
                // Redraw table
                tableLayout.removeAllViews();
                loadData();
            }
        }, null);
    }

    /**
     * Gets accent color from Activity style
     * @return int value of accent color
     */
    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = this.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

}
