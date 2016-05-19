package edu.uoc.compass.plot;

import android.app.Activity;
import android.graphics.Color;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * Wrapper class to configure a MFLineChar in an Activity to show x,y,z,m values
 * where (x,y,z) are the components of a 3D vector and m is the magnitude
 *
 * To use this class a com.github.mikephil.charting.charts.LineChart object must be declared
 * in Activity layout
 *
 * @author Antonio Ortega
 */
public class MFLineChart {
    // LineChar Object
    private LineChart mfLevelsPlot = null;

    // Legend constants
    private static final String X = "X", Y = "Y", Z = "Z", M = "M";

    // Number of points to show in X axis before scrolling
    private int visibleXPoints;

    /**
     * Constructor
     * @param context context that contains LineChar object
     * @param id resource id of LineChar declared in the layout
     * @param visibleXPoints Number of points to show in X axis before scrolling
     * @param description message to show as  description
     */
    public MFLineChart(Activity context, int id, int visibleXPoints, String description) {
        // Copy params object variables
        this.visibleXPoints = visibleXPoints;

        // Access LineChar object
        mfLevelsPlot = (LineChart) context.findViewById(id);

        // Set line description
        mfLevelsPlot.setDescription(description);

        // Define 1 LineDataSet for each line to show
        LineDataSet xDataSet = new LineDataSet(null, X);
        LineDataSet yDataSet = new LineDataSet(null, Y);
        LineDataSet zDataSet = new LineDataSet(null, Z);
        LineDataSet mDataSet = new LineDataSet(null, M);
        xDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        yDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        zDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        mDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        // Create DataSets for each line, initialized with no data
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(xDataSet);
        dataSets.add(yDataSet);
        dataSets.add(zDataSet);
        dataSets.add(mDataSet);

        // Set line colors
        xDataSet.setColor(Color.BLUE);
        yDataSet.setColor(Color.GREEN);
        zDataSet.setColor(Color.BLACK);
        mDataSet.setColor(Color.RED);

        // Avoid showing circles for each point
        xDataSet.setCircleRadius(0);
        yDataSet.setCircleRadius(0);
        zDataSet.setCircleRadius(0);
        mDataSet.setCircleRadius(0);

        // Create initial Graph with no data
        ArrayList<String> horizontalVals = new ArrayList<String>();
        LineData data = new LineData(horizontalVals, dataSets);
        mfLevelsPlot.setData(data);
    }

    /**
     * Call this method when data changes with new values for vector components.
     * Magnitude is calculated for new vector data
     *
     * @param x x component of vector
     * @param y y component of vector
     * @param z z component of vector
     */
    public void addSensorData(float x, float y , float z) {
        // Calculates vector magnitude
        float m = (float)Math.sqrt(x*x+y*y+z*z);

        // Access graph data
        LineData data = mfLevelsPlot.getData();

        // Get next position to insert data
        int pos = data.getXValCount();

        // Add new entry for X axis
        data.addXValue(""+pos);

        // Add new entry for each line
        data.addEntry(new Entry(x, pos),0);
        data.addEntry(new Entry(y, pos),1);
        data.addEntry(new Entry(z, pos),2);
        data.addEntry(new Entry(m, pos),3);

        // Scroll if more then visibleXPoints have been inserted
        float minX = data.getXValCount() - visibleXPoints;
        if (minX>0) {
            mfLevelsPlot.getXAxis().setAxisMinValue(minX);
        }

        // Force redraw
        data.notifyDataChanged();
        mfLevelsPlot.notifyDataSetChanged();
        mfLevelsPlot.invalidate();
    }
}
