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

public class PathSettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private static final float MIN_FREQ = 1.0f;
    private static final float MAX_FREQ = 20.0f;
    private static final int FREQ_INTERVALS = 100;

    private TextView freqTextView;
    private EditText pathNameEdt;
    private SeekBar freqSeekBar;
    private CheckBox previewCheck, acceleratorCheck, gyroscopeCheck, globalReferenceCheck;
    private DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_settings);

        freqTextView = (TextView)findViewById(R.id.path_freq_lbl);
        freqSeekBar = (SeekBar)findViewById(R.id.path_freq_seek);
        previewCheck = (CheckBox) findViewById(R.id.path_preview);
        acceleratorCheck = (CheckBox) findViewById(R.id.path_record_accelerometer);
        gyroscopeCheck = (CheckBox) findViewById(R.id.path_record_gyroscope);
        globalReferenceCheck = (CheckBox) findViewById(R.id.path_record_global_reference);
        Button nextButton = (Button)findViewById(R.id.path_next_btn);

        pathNameEdt = (EditText)findViewById(R.id.path_name_edt);
        Util.disableCharacters(pathNameEdt, ",");

        dbHelper = new DBHelper(this);

        freqSeekBar.setOnSeekBarChangeListener(this);
        nextButton.setOnClickListener(this);
        freqSeekBar.setProgress(21);

        /*
        CheckBox debugCheck = (CheckBox) findViewById(R.id.path_debug);
        debugCheck.setOnCheckedChangeListener(this);
        */
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float samplingRate = getSamplingRate(progress);
        if (freqTextView != null) {
            freqTextView.setText(
                    getText(R.string.sampling_rate_dots) +
                            " " +
                            String.format("%.2f", samplingRate) +
                            " " +
                            getText(R.string.hertz_abbreviation));
        }
    }

    @Override
    public void onClick(View v) {
        String pathNameParam = pathNameEdt.getText().toString();
        if (pathNameParam.isEmpty()) {
            Util.showErrorMessage(
                    this,
                    getText(R.string.title_activity_path_settings).toString(),
                    getText(R.string.error_path_empty).toString(),
                    null);
        } else if (dbHelper.findPathByName(pathNameParam) != -1) {
            Util.showErrorMessage(
                    this,
                    getString(R.string.title_activity_path_settings),
                    getString(R.string.error_path_exits),
                    null);
        } else {
            float samplingRateParam = getSamplingRate(freqSeekBar.getProgress());
            boolean previewParam = previewCheck.isChecked();
            boolean acceleratorParam = acceleratorCheck.isChecked();
            boolean gyroscopeParam = gyroscopeCheck.isChecked();
            boolean globalReferenceParam = globalReferenceCheck.isChecked();
            Intent intent = new Intent(this,PathRecordingActivity.class);
            intent.putExtra(PathRecordingActivity.EXTRA_PATH_NAME, pathNameParam);
            intent.putExtra(PathRecordingActivity.EXTRA_SAMPLING_RATE, samplingRateParam);
            intent.putExtra(PathRecordingActivity.EXTRA_PREVIEW, previewParam);
            intent.putExtra(PathRecordingActivity.EXTRA_ACCELEROMETER, acceleratorParam);
            intent.putExtra(PathRecordingActivity.EXTRA_GYROSCOPE, gyroscopeParam);
            intent.putExtra(PathRecordingActivity.EXTRA_GLOBAL_REFERENCE, globalReferenceParam);
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
        float samplingRate = MIN_FREQ + progress*step;
        return Math.round(samplingRate*4) / 4f;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            String name = "Path";
            name += "-" + getSamplingRate(freqSeekBar.getProgress()) + "Hz";
            name += "-A" + (acceleratorCheck.isChecked()?"1":"0");
            name += "G" + (gyroscopeCheck.isChecked()?"1":"0");
            name += "P" + (previewCheck.isChecked()?"1":"0");
            pathNameEdt.setText(name);
        }
    }
}
