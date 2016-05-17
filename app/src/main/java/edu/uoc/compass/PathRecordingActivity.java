package edu.uoc.compass;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import edu.uoc.compass.plot.MFLineChart;
import edu.uoc.compass.util.DBHelper;
import edu.uoc.compass.util.Util;


public class PathRecordingActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    public static final String EXTRA_PATH_NAME = "path_name";
    public static final String  EXTRA_SAMPLING_RATE = "sampling_rate";
    public static final String  EXTRA_PREVIEW = "preview";
    public static final String  EXTRA_ACCELEROMETER = "accelerometer";
    public static final String  EXTRA_GYROSCOPE = "gyroscope";
    public static final String  EXTRA_GLOBAL_REFERENCE = "global_reference";

    //private boolean isEmulator = "goldfish".equals(Build.HARDWARE);
    //private static float xDebug, yDebug, zDebug;

    private final float alpha = (float) 0.8;
    private static final int VISIBLE_NUM=50;

    private boolean previewParam, accelerometerParam, gyroscopeParam, globalReferenceParam;
    private String pathNameParam;
    private float samplingRateParam;
    private long pathId;
    private long startTime, samples;
    private boolean recording = false;
    private SensorManager sensorManager;
    private Sensor magnetometerSensor, accelerometerSensor, gyroscopeSensor;
    private MFLineChart lineChart;
    protected PowerManager.WakeLock wakelock;
    private DBHelper dbHelper;
    private ImageButton recordButton, saveButton, discardButton;
    private TextView timeLbl, samplesLbl;

    private float gravity[] = new float[3];
    private float magnetic[] = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_recording);

        dbHelper = new DBHelper(this);

        final PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();

        Bundle extras = getIntent().getExtras();
        pathNameParam = extras.getString(EXTRA_PATH_NAME);
        samplingRateParam = extras.getFloat(EXTRA_SAMPLING_RATE);
        previewParam = extras.getBoolean(EXTRA_PREVIEW);
        accelerometerParam = extras.getBoolean(EXTRA_ACCELEROMETER);
        gyroscopeParam = extras.getBoolean(EXTRA_GYROSCOPE);
        globalReferenceParam = extras.getBoolean(EXTRA_GLOBAL_REFERENCE);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        lineChart = new MFLineChart(this, R.id.line_chart, VISIBLE_NUM, pathNameParam);
        recordButton = (ImageButton)findViewById(R.id.path_record_btn);
        saveButton = (ImageButton)findViewById(R.id.path_save_btn);
        discardButton = (ImageButton)findViewById(R.id.path_discard_btn);

        timeLbl = (TextView) findViewById(R.id.path_time_lbl);
        samplesLbl = (TextView) findViewById(R.id.path_samples_lbl);

        recordButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        discardButton.setOnClickListener(this);

        samples=0;
        updateStatus();
        updateCount(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.wakelock.release();
    }

    protected void onResume() {
        super.onResume();
        int sensorDelay = Math.round(1000000/samplingRateParam);
        sensorManager.registerListener(this, magnetometerSensor, sensorDelay);
        if (accelerometerParam || globalReferenceParam) {
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
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                if (globalReferenceParam) {
                    lineChart.addSensorData(A_W[0], A_W[1], A_W[2]);
                } else {
                    lineChart.addSensorData(x, y, z);
                }
            }
        }

        if (recording) {
            long time = System.currentTimeMillis();
            samples ++;
            dbHelper.insertPathData(
                    pathId,
                    sensorEvent.sensor.getType(),
                    x,
                    y,
                    z,
                    time);
            if (globalReferenceParam) {
                dbHelper.insertPathData(
                        pathId,
                        100,
                        A_W[0],
                        A_W[1],
                        A_W[2],
                        time);
            }
            updateCount(time - startTime);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.path_record_btn:
                pathId = dbHelper.insertPath(pathNameParam, samplingRateParam);
                recording = true;
                startTime = System.currentTimeMillis();
                updateStatus();
                break;
            case R.id.path_save_btn:
                recording = false;
                discardButton.setEnabled(false);
                updateStatus();
                Util.showMessage(
                        this,
                        getString(R.string.title_activity_path_recording).toString(),
                        getString(R.string.all_data_saved).toString(),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                break;
            case R.id.path_discard_btn:
                boolean wasRecording = recording;
                recording = false;
                updateStatus();
                discardButton.setEnabled(false);
                if (wasRecording) {
                    dbHelper.deletePath(pathId);
                    Util.showMessage(
                            this,
                            getString(R.string.title_activity_path_recording).toString(),
                            getString(R.string.all_data_deleted).toString(),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                } else {
                    finish();
                }
                break;
        }
    }
    private void updateStatus() {

        recordButton.setEnabled(!recording);
        saveButton.setEnabled(recording);

        Util.manageImageButtonColor(recordButton, Color.RED, Color.GRAY);
        Util.manageImageButtonColor(saveButton, Color.BLACK, Color.GRAY);
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