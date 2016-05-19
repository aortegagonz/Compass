package edu.uoc.compass.plot;

import android.app.Activity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Wrapper class to configure a MFBarChar in an Activity to show bars for x,y,z,m values
 * where (x,y,z) are the components of a 3D vector and m is the magnitude
 *
 * To use this class a com.github.mikephil.charting.charts.BarChart object must be declared
 * in Activity layout
 *
 * @author Antonio Ortega
 */
public class MFBarChart {
    // BarChar Object
    private BarChart mfLevelsPlot = null;

    // Y-basis limit shown in the graph
    private static final float LIMIT = 180f;

    // Legend constants
    private static final String X = "X", Y = "Y", Z = "Z", M = "M";

    // Variables to store labels and data
    ArrayList<String> labels;
    ArrayList<BarEntry> entries;


    /**
     * Constructor
     * @param context context that contains BarChar object
     * @param id resource id of BarChar declared in the layout
     * @param description message to show as Bar description
     */
    public MFBarChart(Activity context, int id, String description) {
        // Access BarChar Object
        mfLevelsPlot = (BarChart) context.findViewById(id);

        // Set Bar Description
        mfLevelsPlot.setDescription(description);

        // Define 1 entry for each bar to show
        entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 0));
        entries.add(new BarEntry(0f, 1));
        entries.add(new BarEntry(0f, 2));
        entries.add(new BarEntry(0f, 3));

        // Set axis limits
        mfLevelsPlot.getAxisLeft().setAxisMaxValue(LIMIT);
        mfLevelsPlot.getAxisLeft().setAxisMinValue(-LIMIT);
        mfLevelsPlot.getAxisRight().setAxisMaxValue(LIMIT);
        mfLevelsPlot.getAxisRight().setAxisMinValue(-LIMIT);

        // Set leyend information
        BarDataSet dataset = new BarDataSet(entries, X + ", " + Y + ", " + Z + ", " + M);
        labels = new ArrayList<String>();
        labels.add(X);
        labels.add(Y);
        labels.add(Z);
        labels.add(M);

        // Create initial Graph with no data
        BarData data = new BarData(labels, dataset);
        mfLevelsPlot.setData(data);

        // Define graph colors
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
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
        // Clear old data
        entries.clear();

        // Calculates vector magnitude
        float m =(float) Math.sqrt(x*x+y*y+z*z);

        // Insert new data
        entries.add(new BarEntry(x, 0));
        entries.add(new BarEntry(y, 1));
        entries.add(new BarEntry(z, 2));
        entries.add(new BarEntry(m, 3));

        // Force redraw
        mfLevelsPlot.notifyDataSetChanged();
        mfLevelsPlot.invalidate();
    }
}
