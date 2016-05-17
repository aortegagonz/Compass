package edu.uoc.compass;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import edu.uoc.compass.plot.MFBarChart;
import edu.uoc.compass.util.DBHelper;
import edu.uoc.compass.util.Util;


public class GridRecordingActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    public static final String EXTRA_GRID_NAME = "grid_name";
    public static final String  EXTRA_SAMPLING_RATE = "sampling_rate";
    public static final String  EXTRA_TIME = "time";
    public static final String  EXTRA_ROWS = "rows";
    public static final String  EXTRA_COLUMNS = "columns";
    public static final String  EXTRA_PREVIEW = "preview";
    public static final String  EXTRA_ACCELEROMETER = "accelerometer";
    public static final String  EXTRA_GYROSCOPE = "gyroscope";
    public static final String  EXTRA_GLOBAL_REFERENCE = "global_reference";

    //private boolean isEmulator = "goldfish".equals(Build.HARDWARE);
    //private static float xDebug, yDebug, zDebug;

    private final float alpha = (float) 0.8;

    private boolean previewParam, accelerometerParam, gyroscopeParam, globalReferenceParam;
    private String gridNameParam;
    private float samplingRateParam, timeParam;

    private long gridId;
    private long startTime, samples;
    private boolean recording = false, dataSaved = false;
    int rows, columns, row, column;
    private SensorManager sensorManager;
    private Sensor magnetometerSensor, accelerometerSensor, gyroscopeSensor;
    private MFBarChart barChart;
    protected PowerManager.WakeLock wakelock;
    private DBHelper dbHelper;
    private ImageButton recordButton, undoButton, saveButton, discardButton;
    private ProgressBar progressBar;
    private TextView currentRowLbl, currentColumnLbl;
    private long endTimestamp;
    private TextView timeLbl, samplesLbl;

    private float gravity[] = new float[3];
    private float magnetic[] = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_recording);

        dbHelper = new DBHelper(this);

        //xDebug = yDebug = zDebug = 0;

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();

        Bundle extras = getIntent().getExtras();
        gridNameParam = extras.getString(EXTRA_GRID_NAME);
        samplingRateParam = extras.getFloat(EXTRA_SAMPLING_RATE);
        timeParam = extras.getFloat(EXTRA_TIME);
        rows = extras.getInt(EXTRA_ROWS);
        columns = extras.getInt(EXTRA_COLUMNS);
        previewParam = extras.getBoolean(EXTRA_PREVIEW);
        accelerometerParam = extras.getBoolean(EXTRA_ACCELEROMETER);
        gyroscopeParam = extras.getBoolean(EXTRA_GYROSCOPE);
        globalReferenceParam = extras.getBoolean(EXTRA_GLOBAL_REFERENCE);

        row = 1;
        column = 1;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        barChart = new MFBarChart(this, R.id.bar_chart, gridNameParam);
        recordButton = (ImageButton) findViewById(R.id.grid_record_btn);
        undoButton = (ImageButton) findViewById(R.id.grid_undo_btn);
        saveButton = (ImageButton) findViewById(R.id.grid_save_btn);
        discardButton = (ImageButton) findViewById(R.id.grid_discard_btn);
        progressBar = (ProgressBar) findViewById(R.id.grid_record_progress);

        currentRowLbl = (TextView) findViewById(R.id.grid_row_lbl);
        currentColumnLbl = (TextView) findViewById(R.id.grid_column_lbl);
        timeLbl = (TextView) findViewById(R.id.grid_time_lbl);
        samplesLbl = (TextView) findViewById(R.id.grid_samples_lbl);

        recordButton.setOnClickListener(this);
        undoButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        discardButton.setOnClickListener(this);

        int max = Math.round(samplingRateParam * timeParam);
        progressBar.setMax(max);

        gridId = dbHelper.insertGrid(gridNameParam, rows, columns, samplingRateParam);
        updateStatus();

        samples=0;
        updateStatus();
        updateCount(0);
    }

        @Override
    protected void onDestroy() {
        if (!dataSaved) {
            dbHelper.deleteGrid(gridId);
        }
        super.onDestroy();
        this.wakelock.release();
    }

    protected void onResume() {
        super.onResume();
        int sensorDelay = Math.round(1000000/samplingRateParam);
        sensorManager.registerListener(this, magnetometerSensor, sensorDelay);
        if (accelerometerParam  || globalReferenceParam) {
            sensorManager.registerListener(this, accelerometerSensor, sensorDelay);
        }
        if (gyroscopeParam) {
            sensorManager.registerListener(this, gyroscopeSensor, sensorDelay);
        }
        wakelock.acquire();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, magnetometerSensor);
        sensorManager.unregisterListener(this, accelerometerSensor);
        sensorManager.unregisterListener(this, gyroscopeSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        int sensorType = sensorEvent.sensor.getType();
        float [] A_W = new float[3];

        if (globalReferenceParam) {
            if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
                gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
                gravity[2] = alpha * gravity[2] + (1 - alpha) * z;

            } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
                magnetic[0] = x;
                magnetic[1] = y;
                magnetic[2] = z;

                float[] R = new float[9];
                float[] I = new float[9];
                float [] A_D = sensorEvent.values.clone();
                A_W[0] = R[0] * A_D[0] + R[1] * A_D[1] + R[2] * A_D[2];
                A_W[1] = R[3] * A_D[0] + R[4] * A_D[1] + R[5] * A_D[2];
                A_W[2] = R[6] * A_D[0] + R[7] * A_D[1] + R[8] * A_D[2];

            }
        }

/*
        if (isEmulator) {
            xDebug++;
            yDebug+=0.5;
            zDebug+=0.1;
            x = xDebug;
            y = yDebug;
            z = zDebug;
            if (xDebug >100) {
                xDebug = yDebug = zDebug = 0;
            }
        }
*/
        if (previewParam || !recording) {
            if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
                if (globalReferenceParam) {
                    barChart.addSensorData(A_W[0], A_W[1], A_W[2]);
                } else {
                    barChart.addSensorData(x, y, z);
                }
            }
        }

        if (recording) {
            long timestamp = System.currentTimeMillis();
            samples ++;
            dbHelper.insertGridData(
                    gridId,
                    sensorType,
                    row,
                    column,
                    x,
                    y,
                    z,
                    timestamp);
            if (globalReferenceParam) {
                dbHelper.insertGridData(
                        gridId,
                        100,
                        row,
                        column,
                        A_W[0],
                        A_W[1],
                        A_W[2],
                        timestamp);
            }

            updateCount(timestamp - startTime);

            if(sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
                progressBar.setProgress(progressBar.getProgress() + 1);
            }

            if (timestamp > endTimestamp) {
                recording = false;
                column ++ ;
                if (column > columns) {
                    row++;
                    column=1;
                }
                progressBar.setProgress(0);
                updateStatus();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.grid_record_btn:
                recordButton.setEnabled(false);
                undoButton.setEnabled(true);
                recording = true;
                endTimestamp = System.currentTimeMillis() + Math.round(timeParam * 1000);
                updateStatus();
                startTime = System.currentTimeMillis();
                samples=0;
                updateCount(0);
                break;
            case R.id.grid_undo_btn:
                recording = false;
                column--;
                if (column ==0) {
                    row--;
                    column=columns;
                }
                dbHelper.deleteCell(gridId, row, column);
                samples=0;
                updateCount(0);
                updateStatus();
                break;
            case R.id.grid_save_btn:
                Util.showMessage(
                        this,
                        getText(R.string.title_activity_grid_recording).toString(),
                        getText(R.string.all_data_saved).toString(),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataSaved = true;
                        finish();
                    }
                });
                break;
            case R.id.grid_discard_btn:
                recording = false;
                recordButton.setEnabled(false);
                undoButton.setEnabled(false);
                discardButton.setEnabled(false);
                dbHelper.deleteGrid(gridId);
                Util.showMessage(
                        this,
                        getText(R.string.title_activity_grid_recording).toString(),
                        getText(R.string.all_data_deleted).toString(),
                        new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                break;
        }
    }

    private void updateStatus() {
        boolean started = !(row == 1 && column == 1);
        boolean finished = row > rows;
        currentRowLbl.setText(getText(R.string.row_dots) + " " + row);
        currentColumnLbl.setText(getText(R.string.column_dots) + " " + column);

        undoButton.setEnabled(started);
        saveButton.setEnabled(started && !recording);
        recordButton.setEnabled(!recording && !finished);

        Util.manageImageButtonColor(recordButton, Color.RED, Color.GRAY);
        Util.manageImageButtonColor(saveButton, Color.BLACK, Color.GRAY);
        Util.manageImageButtonColor(undoButton, Color.BLACK, Color.GRAY);

    }

    private void updateCount(long time) {
        long seconds = time/1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;

        String timeStr  = String.format("%d:%02d:%02d", h,m,s);

        timeLbl.setText(getText(R.string.time_dots) + " " + timeStr);
        samplesLbl.setText(getText(R.string.samples_dots) + " " + samples);
    }
}