package com.example.sitbit;

import android.content.Context;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;

import android.text.Html;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class HomeFragment extends Fragment implements SensorEventListener {

    public static final int RECORDING_DELAY = 50000; // in microseconds; 50000 = 20 Hz
    public static final int BUFFER_SIZE = 20;
    public static final double THRESHOLD = 1.25;
    public static final int HISTORY_SIZE = 8;

    public static final int MILLISECS_PER_BAR = 360000;
    public static final int N_BARS = Globals.MILLISECS_PER_DAY / MILLISECS_PER_BAR;

    private SensorManager sensorManager;
    private Sensor linearAccelSensor;

    private GraphView graph;
    private TextView graphLegend1;
    private TextView graphLegend2;
    private Button recordButton;

    private ArrayList<double[]> buffer;
    private int history;

    private boolean recording = false;

    private Globals globals;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // sensor init
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        linearAccelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // graphical artifact init
        graph = (GraphView) view.findViewById(R.id.bar_graph);
        graphLegend1 = (TextView) view.findViewById(R.id.todayGraphLegend);
        graphLegend2 = (TextView) view.findViewById(R.id.weekGraphLegend);
        recordButton = (Button) view.findViewById(R.id.HOME_record_button);

        graphLegend1.setText(Html.fromHtml(getString(R.string.HOME_graph_legend)));
        graphLegend2.setText(Html.fromHtml(getString(R.string.HOME_graph_legend)));

        globals = Globals.getInstance();

        // sensor buffer init
        buffer = new ArrayList<>(BUFFER_SIZE);

        // history init
        history = HISTORY_SIZE / 2;

        createGraph();

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleRecording();
            }
        });

        return view;
    }

    private void createGraph() {

        long currentTime = System.currentTimeMillis();


        // creating graph for today's data
        // startTime = start of today
        // endTime = end of today
        final long startTime = currentTime - currentTime % Globals.MILLISECS_PER_DAY;
        long endTime = startTime + Globals.MILLISECS_PER_DAY;

        globals.getDataEntries(startTime, endTime, new Consumer<HashMap<Long, Boolean>>() {
            @Override
            public void accept(HashMap<Long, Boolean> data) {

                // sort data from earliest to latest
                ArrayList<Long> keys = new ArrayList<>(data.keySet());
                Collections.sort(keys);

                DataPoint[] points = new DataPoint[N_BARS];

                int curr = 0;

                // for every bar in the graph
                for (int i = 0; i < N_BARS; i++) {

                    long limit = startTime + (i + 1) * MILLISECS_PER_BAR;

                    int nSed = 0;
                    int nAct = 0;

                    while (curr < keys.size() && keys.get(curr) < limit) {
                        if (data.get(keys.get(curr)))
                            nAct++;
                        else
                            nSed++;
                        curr++;
                    }

                    if (nSed > nAct)
                        points[i] = new DataPoint(i, 0.5);
                    else if (nSed < nAct)
                        points[i] = new DataPoint(i, 1);
                    else
                        points[i] = new DataPoint(i, 0);
                }

                BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);
                graph.addSeries(series);

                series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                    @Override
                    public int get(DataPoint point) {
                        double y = point.getY();

                        if (y > 0.9)
                            return getResources().getColor(R.color.colorPrimary);
                        else if (y > 0.4)
                            return Color.RED;
                        else
                            return Color.WHITE;
                    }
                });

                series.setSpacing(0);
            }
        });

        // remove grid lines and labels
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);

        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(1);

        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(N_BARS);
    }



    private void toggleRecording() {

        if (recording) {
            sensorManager.unregisterListener(this);

            recordButton.setText(R.string.HOME_recording_button_disabled_text);
            recordButton.setBackgroundColor(getResources().getColor(R.color.HOME_recording_button_disabled_color));

            recording = false;

        } else {
            sensorManager.registerListener(this, linearAccelSensor, RECORDING_DELAY, RECORDING_DELAY);

            recordButton.setText(R.string.HOME_recording_button_enabled_text);
            recordButton.setBackgroundColor(getResources().getColor(R.color.HOME_recording_button_enabled_color));

            recording = true;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int a) {}

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor == linearAccelSensor) {

            double[] sensorReading = new double[] { sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2] };

            buffer.add(sensorReading);

            if (buffer.size() == BUFFER_SIZE) {

                double max = 0.0;

                for (int i = 0; i < BUFFER_SIZE; i++) {
                    double accelMagnitude = Math.sqrt(Math.pow(sensorReading[0], 2) + Math.pow(sensorReading[1], 2) + Math.pow(sensorReading[2], 2));

                    if (accelMagnitude > max)
                        max = accelMagnitude;
                }

                if (max > THRESHOLD && history < HISTORY_SIZE - 1)
                    history++;

                if (max <= THRESHOLD && history > 0)
                    history--;

                globals.saveDataEntry(System.currentTimeMillis(), history < HISTORY_SIZE / 2 ? false : true);

                buffer.clear();
            }
        }
    }
}
