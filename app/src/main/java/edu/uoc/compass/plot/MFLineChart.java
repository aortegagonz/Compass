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
 * Created by aortega on 3/5/16.
 */
public class MFLineChart {
    private static final String X = "X";
    private static final String Y = "Y";
    private static final String Z = "Z";
    private static final String M = "M";
    private LineChart mfLevelsPlot = null;
    private int visibleXPoints;

    public MFLineChart(Activity context, int id, int visibleXPoints, String description) {
        this.visibleXPoints = visibleXPoints;
        mfLevelsPlot = (LineChart) context.findViewById(id);

        mfLevelsPlot.setDescription(description);
        LineDataSet xDataSet = new LineDataSet(null, X);
        LineDataSet yDataSet = new LineDataSet(null, Y);
        LineDataSet zDataSet = new LineDataSet(null, Z);
        LineDataSet mDataSet = new LineDataSet(null, M);
        xDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        yDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        zDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        mDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(xDataSet);
        dataSets.add(yDataSet);
        dataSets.add(zDataSet);
        dataSets.add(mDataSet);

        xDataSet.setColor(Color.BLUE);
        yDataSet.setColor(Color.GREEN);
        zDataSet.setColor(Color.BLACK);
        mDataSet.setColor(Color.RED);

        xDataSet.setCircleRadius(0);
        yDataSet.setCircleRadius(0);
        zDataSet.setCircleRadius(0);
        mDataSet.setCircleRadius(0);

        ArrayList<String> horizontalVals = new ArrayList<String>();
        LineData data = new LineData(horizontalVals, dataSets);

        mfLevelsPlot.setData(data);
    }

    public void addSensorData(float x, float y , float z) {
        LineData data = mfLevelsPlot.getData();
        int pos = data.getXValCount();
        float m = (float)Math.sqrt(x*x+y*y+z*z);

        data.addXValue(""+pos);
        data.addEntry(new Entry(x, pos),0);
        data.addEntry(new Entry(y, pos),1);
        data.addEntry(new Entry(z, pos),2);
        data.addEntry(new Entry(m, pos),3);

        float minX = data.getXValCount() - visibleXPoints;
        if (minX>0) {
            mfLevelsPlot.getXAxis().setAxisMinValue(minX);
        }

        data.notifyDataChanged();
        mfLevelsPlot.notifyDataSetChanged();
        mfLevelsPlot.invalidate();
    }
}
