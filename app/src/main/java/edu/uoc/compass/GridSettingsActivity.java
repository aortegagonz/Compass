package edu.uoc.compass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import edu.uoc.compass.util.DBHelper;
import edu.uoc.compass.util.Util;

/**
 * Used to define parameters to record a grid composed of rows and columns
 *
 * @author Antonio Ortega
 */
public class GridSettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    // Frequency seekbar limit constants
    private static final float MIN_FREQ = 1.0f;
    private static final float MAX_FREQ = 20.0f;
    private static final int FREQ_INTERVALS = 100;

    // Time seekbar limit constants
    private static final float MIN_TIME = 1.0f;
    private static final float MAX_TIME = 60.0f;
    private static final int TIME_INTERVALS = 100;

    // Layout objects
    private TextView freqTextView, timeTextView;
    private EditText gridNameEdt, gridRowsEdt, gridColumnsEdt;
    private SeekBar freqSeekBar, timeSeekBar;
    private CheckBox previewCheck, acceleratorCheck, gyroscopeCheck, globalReferenceCheck;

    // Database variables
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_settings);

        // Find layout resources
        freqTextView = (TextView)findViewById(R.id.grid_freq_lbl);
        timeTextView = (TextView)findViewById(R.id.grid_time_lbl);
        freqSeekBar = (SeekBar)findViewById(R.id.grid_freq_seek);
        timeSeekBar = (SeekBar)findViewById(R.id.grid_time_seek);
        previewCheck = (CheckBox) findViewById(R.id.grid_preview);
        acceleratorCheck = (CheckBox) findViewById(R.id.grid_record_accelerometer);
        gyroscopeCheck = (CheckBox) findViewById(R.id.grid_record_gyroscope);
        globalReferenceCheck = (CheckBox) findViewById(R.id.grid_record_global_reference);
        Button nextButton = (Button)findViewById(R.id.grid_next_btn);
        gridNameEdt = (EditText)findViewById(R.id.grid_name_edt);
        gridRowsEdt = (EditText)findViewById(R.id.grid_rows_edt);
        gridColumnsEdt = (EditText)findViewById(R.id.grid_columns_edt);

        // Disable "," character in name editing to avoid problems in CSV files
        Util.disableCharacters(gridNameEdt, ",");

        // Get database connection
        dbHelper = new DBHelper(this);

        // The activity receives all button click and seek change events
        freqSeekBar.setOnSeekBarChangeListener(this);
        timeSeekBar.setOnSeekBarChangeListener(this);
        nextButton.setOnClickListener(this);

        // Set seek bars initial position
        freqSeekBar.setProgress(21);
        timeSeekBar.setProgress(15);

    }


    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.grid_freq_seek:   // Frequency seek bar value changed
                // Output sampling rate in desired format
                freqTextView.setText(getText(R.string.sampling_rate_dots) +
                                " " +
                                Float.toString(getSamplingRate(progress)) +
                                " " +
                                getText(R.string.hertz_abbreviation).toString());

                break;
            case R.id.grid_time_seek:   // Time seek bar value changed
                // Output time in desired format
                timeTextView.setText(
                        getText(R.string.time_dots) +
                                " " +
                                Float.toString(getTime(progress)) +
                                " " +
                                getText(R.string.seconds_abbreviation).toString());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        // Validate values from editable fields
        String gridNameParam = gridNameEdt.getText().toString();
        if (gridNameParam.isEmpty()) {
            // Grid name is empty
            Util.showErrorMessage(
                    this,
                    getText(R.string.title_activity_grid_settings).toString(),
                    getText(R.string.error_grid_empty).toString(),
                    null);

        } else if (dbHelper.findGridByName(gridNameParam) != -1) {
            // Grid name already exists
            Util.showErrorMessage(
                    this,
                    getText(R.string.title_activity_grid_settings).toString(),
                    getText(R.string.error_grid_exits).toString(),
                    null);
        } else if (gridRowsEdt.getText().toString().isEmpty()) {
            // Number of rows is empty
            Util.showErrorMessage(
                    this,
                    getText(R.string.title_activity_grid_settings).toString(),
                    getText(R.string.error_rows_empty).toString(),
                    null);
        } else if (gridColumnsEdt.getText().toString().isEmpty()) {
            // Number of columns us empty
            Util.showErrorMessage(
                    this,
                    getText(R.string.title_activity_grid_settings).toString(),
                    getText(R.string.error_columns_empty).toString(),
                    null);
        } else {
            // Get values of editable fields
            float samplingRateParam = getSamplingRate(freqSeekBar.getProgress());
            float timeParam = getTime(timeSeekBar.getProgress());
            boolean previewParam = previewCheck.isChecked();
            boolean acceleratorParam = acceleratorCheck.isChecked();
            boolean gyroscopeParam = gyroscopeCheck.isChecked();
            boolean globalReferenceParam = globalReferenceCheck.isChecked();
            int rows = getIntValue(gridRowsEdt);
            int columns = getIntValue(gridColumnsEdt);

            // Starts Grid recording activity with all params
            Intent intent = new Intent(this,GridRecordingActivity.class);
            intent.putExtra(GridRecordingActivity.EXTRA_GRID_NAME, gridNameParam);
            intent.putExtra(GridRecordingActivity.EXTRA_SAMPLING_RATE, samplingRateParam);
            intent.putExtra(GridRecordingActivity.EXTRA_TIME, timeParam);
            intent.putExtra(GridRecordingActivity.EXTRA_PREVIEW, previewParam);
            intent.putExtra(GridRecordingActivity.EXTRA_ACCELEROMETER, acceleratorParam);
            intent.putExtra(GridRecordingActivity.EXTRA_GYROSCOPE, gyroscopeParam);
            intent.putExtra(GridRecordingActivity.EXTRA_GLOBAL_REFERENCE, globalReferenceParam);
            intent.putExtra(GridRecordingActivity.EXTRA_ROWS, rows);
            intent.putExtra(GridRecordingActivity.EXTRA_COLUMNS, columns);
            startActivity(intent);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * Calculate sampling rate from seek bar progress value in 0.25 steps
     * @param progress seek bar current progress selected
     * @return sampling rate in hertzs
     */
    private float getSamplingRate(int progress) {
        float step = (MAX_FREQ-MIN_FREQ)/FREQ_INTERVALS;
        float samplingRate =  MIN_FREQ + progress*step;
        return Math.round(samplingRate*4) / 4f;
    }


    /**
     * Calculate time from seek bar progress value in 1.0 steps
     * @param progress seek bar current progress selected
     * @return time in secods
     */
    private float getTime(int progress) {
        float step = (MAX_TIME-MIN_TIME)/TIME_INTERVALS;
        float time =  MIN_TIME + progress*step;
        return Math.round(time);
    }

    /**
     * Gets integer value form an EditText
     * @param text EditText from current layout
     * @return edit text converted to int
     */
    private int getIntValue(EditText text) {
        return new Integer(text.getText().toString()).intValue();
    }

    /**
     * Used only for testing. Set Grid name from selected parameters
     */
    public void setDebugName() {
        String name = "Grid";
        name += "-" + gridRowsEdt.getText() + "x" + gridColumnsEdt.getText();
        name += "-" + getSamplingRate(freqSeekBar.getProgress()) + "Hz";
        name += "-" + getTime(timeSeekBar.getProgress()) + "s";
        name += "-A" + (acceleratorCheck.isChecked()?"1":"0");
        name += "G" + (gyroscopeCheck.isChecked()?"1":"0");
        name += "P" + (previewCheck.isChecked()?"1":"0");
        gridNameEdt.setText(name);
    }
}
