package edu.uoc.compass;

import android.app.Activity;
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

    private void loadData() {
        DBHelper dbHelper = new DBHelper(this);
        addHeader(tableLayout);
        ArrayList<DBHelper.Recording> paths = dbHelper.getAllRecords();
        Iterator<DBHelper.Recording> iterator = paths.iterator();
        while (iterator.hasNext()) {
            DBHelper.Recording record = iterator.next();
            addRow(tableLayout, record);
        }
    }

    private void addHeader(TableLayout tableLayout) {
        TableRow tableRow = new TableRow(this);
        //tableRow.setBackgroundColor(Color.BLUE);
        int color = fetchAccentColor();
        tableRow.setBackgroundColor(color);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(layoutParams);
        //addColumn(tableRow, getString(R.string.delete));
        addColumn(tableRow, "", Color.WHITE);
        addColumn(tableRow, getString(R.string.type), color);
        addColumn(tableRow, getString(R.string.name), color);
        tableLayout.addView(tableRow);
    }

    private void addRow(TableLayout tableLayout, DBHelper.Recording recording) {
        int color = odd? Color.GRAY:Color.WHITE;
        odd = !odd;
        TableRow tableRow = new TableRow(this);
        tableRow.setBackgroundColor(color);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(layoutParams);
        addViewColumn(tableRow, color, recording);
        addColumn(tableRow, recording.getTypeName(this), color);
        addColumn(tableRow, recording.name, color);
        tableLayout.addView(tableRow);
        tableRow.setTag(recording);

    }

    private void addColumn(TableRow tableRow, String data, int backgroundColor) {
        TextView textView = new TextView(this);
        textView.setPadding(PADDING, PADDING, PADDING, PADDING);
        textView.setText(data);
        textView.setBackgroundColor(backgroundColor);
        tableRow.addView(textView);
    }

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
        final DBHelper.Recording recording = (DBHelper.Recording)v.getTag();
        final DBHelper dbHelper = new DBHelper(this);

        Util.showAcceptCancelDialog(
                this,
                getString(R.string.title_activity_view_recordings).toString(),
                getString(R.string.confirm_delete, recording.getTypeName(this)).toString(),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteRecording(recording);
                tableLayout.removeAllViews();
                loadData();
            }
        }, null);
    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = this.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

}
