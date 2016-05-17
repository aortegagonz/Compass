package edu.uoc.compass.plot;

import android.app.Activity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by aortega on 3/5/16.
 */
public class MFBarChart {
    private BarChart mfLevelsPlot = null;
    private static final float LIMIT = 180f;
    private static final String X = "X";
    private static final String Y = "Y";
    private static final String Z = "Z";
    private static final String M = "M";
    ArrayList<String> labels;
    ArrayList<BarEntry> entries;


    public MFBarChart(Activity context, int id, String description) {
        mfLevelsPlot = (BarChart) context.findViewById(id);

        mfLevelsPlot.setDescription(description);
        entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 0));
        entries.add(new BarEntry(0f, 1));
        entries.add(new BarEntry(0f, 2));
        entries.add(new BarEntry(0f, 3));
        mfLevelsPlot.getAxisLeft().setAxisMaxValue(LIMIT);
        mfLevelsPlot.getAxisLeft().setAxisMinValue(-LIMIT);
        mfLevelsPlot.getAxisRight().setAxisMaxValue(LIMIT);
        mfLevelsPlot.getAxisRight().setAxisMinValue(-LIMIT);


        BarDataSet dataset = new BarDataSet(entries, X + ", " + Y + ", " + Z + ", " + M);

        labels = new ArrayList<String>();
        labels.add(X);
        labels.add(Y);
        labels.add(Z);
        labels.add(M);

        BarData data = new BarData(labels, dataset);
        mfLevelsPlot.setData(data);

        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
    }

    public void addSensorData(float x, float y , float z) {
        entries.clear();

        float m =(float) Math.sqrt(x*x+y*y+z*z);

        entries.add(new BarEntry(x, 0));
        entries.add(new BarEntry(y, 1));
        entries.add(new BarEntry(z, 2));
        entries.add(new BarEntry(m, 3));

        mfLevelsPlot.notifyDataSetChanged();
        mfLevelsPlot.invalidate();
    }
}
