package edu.uoc.compass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import edu.uoc.compass.util.DBHelper;
import edu.uoc.compass.util.Util;

public class GridSettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private static final float MIN_FREQ = 1.0f;
    private static final float MAX_FREQ = 20.0f;
    private static final int FREQ_INTERVALS = 100;
    private static final float MIN_TIME = 1.0f;
    private static final float MAX_TIME = 60.0f;
    private static final int TIME_INTERVALS = 100;

    private TextView freqTextView, timeTextView;
    private EditText gridNameEdt, gridRowsEdt, gridColumnsEdt;
    private SeekBar freqSeekBar, timeSeekBar;
    private CheckBox previewCheck, acceleratorCheck, gyroscopeCheck;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_settings);

        freqTextView = (TextView)findViewById(R.id.grid_freq_lbl);
        timeTextView = (TextView)findViewById(R.id.grid_time_lbl);
        freqSeekBar = (SeekBar)findViewById(R.id.grid_freq_seek);
        timeSeekBar = (SeekBar)findViewById(R.id.grid_time_seek);
        previewCheck = (CheckBox) findViewById(R.id.grid_preview);
        acceleratorCheck = (CheckBox) findViewById(R.id.grid_record_accelerometer);
        gyroscopeCheck = (CheckBox) findViewById(R.id.grid_record_gyroscope);
        Button nextButton = (Button)findViewById(R.id.grid_next_btn);
        gridNameEdt = (EditText)findViewById(R.id.grid_name_edt);
        Util.disableCharacters(gridNameEdt, ",");
        gridRowsEdt = (EditText)findViewById(R.id.grid_rows_edt);
        gridColumnsEdt = (EditText)findViewById(R.id.grid_columns_edt);
        dbHelper = new DBHelper(this);

        freqSeekBar.setOnSeekBarChangeListener(this);
        timeSeekBar.setOnSeekBarChangeListener(this);
        nextButton.setOnClickListener(this);
        freqSeekBar.setProgress(21);
        timeSeekBar.setProgress(15);

        /*
        CheckBox debugCheck = (CheckBox) findViewById(R.id.grid_debug);
        debugCheck.setOnCheckedChangeListener(this);
        */
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.grid_freq_seek:
                freqTextView.setText(getText(R.string.sampling_rate_dots) +
                                " " +
                                Float.toString(getSamplingRate(progress)) +
                                " " +
                                getText(R.string.hertz_abbreviation).toString());

                break;
            case R.id.grid_time_seek:
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
        String gridNameParam = gridNameEdt.getText().toString();
        if (gridNameParam.isEmpty()) {
            Util.showErrorMessage(
                    this,
                    getText(R.string.title_activity_grid_settings).toString(),
                    getText(R.string.error_grid_empty).toString(),
                    null);

        } else if (dbHelper.findGridByName(gridNameParam) != -1) {
            Util.showErrorMessage(
                    this,
                    getText(R.string.title_activity_grid_settings).toString(),
                    getText(R.string.error_grid_exits).toString(),
                    null);
        } else if (gridRowsEdt.getText().toString().isEmpty()) {
            Util.showErrorMessage(
                    this,
                    getText(R.string.title_activity_grid_settings).toString(),
                    getText(R.string.error_rows_empty).toString(),
                    null);
        } else if (gridColumnsEdt.getText().toString().isEmpty()) {
            Util.showErrorMessage(
                    this,
                    getText(R.string.title_activity_grid_settings).toString(),
                    getText(R.string.error_columns_empty).toString(),
                    null);
        } else {
            float samplingRateParam = getSamplingRate(freqSeekBar.getProgress());
            float timeParam = getTime(timeSeekBar.getProgress());
            boolean previewParam = previewCheck.isChecked();
            boolean acceleratorParam = acceleratorCheck.isChecked();
            boolean gyroscopeParam = gyroscopeCheck.isChecked();
            int rows = getIntValue(gridRowsEdt);
            int columns = getIntValue(gridColumnsEdt);
            Intent intent = new Intent(this,GridRecordingActivity.class);
            intent.putExtra(GridRecordingActivity.EXTRA_GRID_NAME, gridNameParam);
            intent.putExtra(GridRecordingActivity.EXTRA_SAMPLING_RATE, samplingRateParam);
            intent.putExtra(GridRecordingActivity.EXTRA_TIME, timeParam);
            intent.putExtra(GridRecordingActivity.EXTRA_PREVIEW, previewParam);
            intent.putExtra(PathRecordingActivity.EXTRA_ACCELEROMETER, acceleratorParam);
            intent.putExtra(PathRecordingActivity.EXTRA_GYROSCOPE, gyroscopeParam);
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

    private float getSamplingRate(int progress) {
        float step = (MAX_FREQ-MIN_FREQ)/FREQ_INTERVALS;
        float samplingRate =  MIN_FREQ + progress*step;
        return Math.round(samplingRate*4) / 4f;
    }

    private float getTime(int progress) {
        float step = (MAX_TIME-MIN_TIME)/TIME_INTERVALS;
        float time =  MIN_TIME + progress*step;
        return Math.round(time);
    }

    private int getIntValue(EditText text) {
        return new Integer(text.getText().toString()).intValue();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
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
}
