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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class HomeFragment extends Fragment implements SensorEventListener {

    public static final int RECORDING_DELAY = 50000; // in microseconds; 50000 = 20 Hz
    public static final int BUFFER_SIZE = Globals.SECONDS_PER_CLASSIFICATION * 1000000 / RECORDING_DELAY;

    public static final double THRESHOLD = 1.25;
    public static final int HISTORY_SIZE = 8;

    public static final int MILLISECS_PER_BAR = 360000;
    public static final int N_BARS = Globals.MILLISECS_PER_DAY / MILLISECS_PER_BAR;

    private SensorManager sensorManager;
    private Sensor linearAccelSensor;

    private GraphView graph;
    private ArrayList<GraphView> weekGraphs;
    private TextView graphLegend1;
    private TextView graphLegend2;
    private Button recordButton;

    private ArrayList<double[]> buffer = new ArrayList<>();
    private int history = HISTORY_SIZE / 2;

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
        graph = view.findViewById(R.id.bar_graph);
        weekGraphs = new ArrayList<>();
        weekGraphs.add((GraphView) view.findViewById(R.id.sun_bar_graph));
        weekGraphs.add((GraphView) view.findViewById(R.id.mon_bar_graph));
        weekGraphs.add((GraphView) view.findViewById(R.id.tues_bar_graph));
        weekGraphs.add((GraphView) view.findViewById(R.id.wed_bar_graph));
        weekGraphs.add((GraphView) view.findViewById(R.id.thurs_bar_graph));
        weekGraphs.add((GraphView) view.findViewById(R.id.fri_bar_graph));
        weekGraphs.add((GraphView) view.findViewById(R.id.sat_bar_graph));
        graphLegend1 = view.findViewById(R.id.todayGraphLegend);
        graphLegend2 = view.findViewById(R.id.weekGraphLegend);
        recordButton = view.findViewById(R.id.HOME_record_button);

        graphLegend1.setText(Html.fromHtml(getString(R.string.HOME_graph_legend)));
        graphLegend2.setText(Html.fromHtml(getString(R.string.HOME_graph_legend)));

        globals = Globals.getInstance();

        createGraph(System.currentTimeMillis(), graph);
        createWeekGraphs();

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleRecording();
            }
        });

        if (recording) {
            recordButton.setText(R.string.HOME_recording_button_enabled_text);
            recordButton.setBackgroundColor(getResources().getColor(R.color.HOME_recording_button_enabled_color));
        }

        return view;
    }

    private void createGraph(long time, final GraphView graph) {

        // creating graph for today's data
        // startTime = start of today
        // endTime = end of today
        Calendar[] lastDay = Globals.getDayInterval(time);
        final long startTime = lastDay[0].getTimeInMillis();
        long endTime = lastDay[1].getTimeInMillis();

        globals.getDataEntries(startTime, endTime, new Consumer<HashMap<Long, Boolean>>() {
            @Override
            public void accept(HashMap<Long, Boolean> data) {

                // sort data from earliest to latest
                ArrayList<Long> keys = new ArrayList<>(data.keySet());
                Collections.sort(keys);

                DataPoint[] points = new DataPoint[N_BARS];

                int curr = 0;

                final ArrayList<Double> classification = new ArrayList<>();

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
                        classification.add(0.5);
                    else if (nSed < nAct)
                        classification.add(1.0);
                    else
                        classification.add(0.0);
                    points[i] = new DataPoint(i, 1);
                }

                BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);
                graph.removeAllSeries();
                graph.addSeries(series);

                series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                    @Override
                    public int get(DataPoint point) {
                        double c = classification.get((int) point.getX());

                        if (c > 0.9)
                            return getResources().getColor(R.color.colorPrimary);
                        else if (c > 0.4)
                            return Color.RED;
                        else
                            return Color.WHITE;
                    }
                });

                series.setSpacing(0);
            }
        });

        formatGraph(graph);
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


    private int nClassifications = 0;
    private int currentState = 0;

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

                if (history < HISTORY_SIZE / 2)
                    currentState--;
                else
                    currentState++;

                nClassifications++;

                if (nClassifications == Globals.TRANSMISSION_FREQ / Globals.SECONDS_PER_CLASSIFICATION) {
                    globals.saveDataEntry(System.currentTimeMillis(), currentState > 0);
                    nClassifications = 0;
                    currentState = 0;

                    // update graph in real time
                    createGraph(System.currentTimeMillis(), graph);
                    createWeekGraphs();
                }

                buffer.clear();
            }
        }
    }

    private void formatGraph(GraphView g) {
        // remove grid lines and labels
        g.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        g.getGridLabelRenderer().setVerticalLabelsVisible(false);
        g.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);

        // set manual Y bounds
        g.getViewport().setYAxisBoundsManual(true);
        g.getViewport().setMinY(0);
        g.getViewport().setMaxY(1);

        // set manual X bounds
        g.getViewport().setXAxisBoundsManual(true);
        g.getViewport().setMinX(0);
        g.getViewport().setMaxX(N_BARS);
    }

    // create the graphs for the week
    private void createWeekGraphs() {

        long curTime = System.currentTimeMillis();

        Calendar[] curDay = Globals.getDayInterval(curTime);
        long curDayStart = curDay[0].getTimeInMillis();
        int curDayInt = curDay[0].get(Calendar.DAY_OF_WEEK);

        Calendar[] weekStart = Globals.getDayInterval(curDayStart - ((curDayInt - 1) * Globals.MILLISECS_PER_DAY));
        long weekDayStart = weekStart[0].getTimeInMillis();

        // create the graphs for the days of the week so far
        long nextDay = weekDayStart;
        System.out.println(nextDay);
        for (int i = 0; i < weekGraphs.size(); i++) {
            if (i < curDayInt) {
                createGraph(nextDay, weekGraphs.get(i));
                nextDay = Globals.getDayInterval(nextDay)[1].getTimeInMillis();
            } else {
                formatGraph(weekGraphs.get(i));
            }
        }

    }
}
