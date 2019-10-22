package com.example.sitbit;

import android.content.Context;

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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Scanner;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
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

    public static final int SECS_PER_BAR = 360;
    public static final int N_BARS = 86400 / SECS_PER_BAR;

    private SensorManager sensorManager;
    private Sensor linearAccelSensor;

    private GraphView graph;
    private TextView graphLegend1;
    private TextView graphLegend2;
    private Button recordButton;

    private BufferedWriter writer;

    private ArrayList<double[]> buffer;
    private int history;
    
    private HashMap<Integer, Boolean> data;

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

        try {
            writer = new BufferedWriter(new FileWriter(globals.getSedentaryDataFile(), true));
        } catch (IOException e) {}

        // sensor buffer init
        buffer = new ArrayList<>(BUFFER_SIZE);

        // history init
        history = HISTORY_SIZE / 2;

        getGraphData();
        createGraph();

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleRecording();
            }
        });

        return view;
    }

    private void getGraphData() {
        data = new HashMap<>();

        try (Scanner reader = new Scanner(globals.getSedentaryDataFile())){
            while (reader.hasNextLine()) {

                String[] tokens = reader.nextLine().split(",");

                Date date = new Date(Long.parseLong(tokens[1]));

                int second = date.getHours() * 3600 + date.getMinutes() * 60 + date.getSeconds();
                boolean isActive = tokens[0].equals("active");

                data.put(second, isActive);
            }
        } catch (Exception e) {}
    }

    private void createGraph() {

        DataPoint[] points = new DataPoint[N_BARS];

        for (int i = 0; i < N_BARS; i++) {

            boolean dataPresent = false;

            for (int j = 0; j < SECS_PER_BAR; j++)
                dataPresent |= data.containsKey(i * SECS_PER_BAR + j);

            points[i] = new DataPoint(i, dataPresent ? 1 : 0);
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);
        graph.addSeries(series);

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

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint point) {

                int nSed = 0;
                int nAct = 0;

                for (int i = 0; i < SECS_PER_BAR; i++) {

                    Boolean isSedentary = data.get(((int) point.getX()) * SECS_PER_BAR + i);

                    if (isSedentary != null)
                        if (isSedentary)
                            nSed++;
                        else
                            nAct++;
                }

                if (nSed > nAct)
                    return Color.RED;
                else if (nSed < nAct)
                    return getResources().getColor(R.color.colorPrimary);
                else
                    return Color.WHITE;
            }
        });

        series.setSpacing(0);
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

            double[] sensorReading = new double[] { sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], System.currentTimeMillis() };

            buffer.add(sensorReading);

            if (buffer.size() == BUFFER_SIZE) {

                double max = 0.0;
                long time = 0L;

                for (int i = 0; i < BUFFER_SIZE; i++) {
                    double accelMagnitude = Math.sqrt(Math.pow(sensorReading[0], 2) + Math.pow(sensorReading[1], 2) + Math.pow(sensorReading[2], 2));

                    if (accelMagnitude > max) {
                        max = accelMagnitude;
                        time = (long) sensorReading[3];
                    }
                }

                if (max > THRESHOLD && history < HISTORY_SIZE - 1)
                    history++;

                if (max <= THRESHOLD && history > 0)
                    history--;

                try {
                    writer.write((history < HISTORY_SIZE / 2 ? "sedentary" : "active") + "," + time + "\n");
                    writer.flush();
                } catch (IOException e) {
                    System.out.println("Failed to append to SedentaryData!");
                }

                buffer.clear();
            }
        }
    }
}
