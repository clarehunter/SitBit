package com.example.sitbit;


import android.graphics.Color;

import android.content.Context;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.widget.Button;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements SensorEventListener {

    public static final int RECORDING_DELAY = 50000; // in microseconds; 50000 = 20 Hz
    public static final int BUFFER_SIZE = 20;
    public static final double THRESHOLD = 1.25;
    public static final int HISTORY_SIZE = 8;

    private SensorManager sensorManager;
    private Sensor linearAccelSensor;

    private Button recordButton;

    private boolean recording = false;

    private ArrayList<double[]> buffer;

    private int history;

    private File file;
    private BufferedWriter writer;
    
    private ArrayList<String[]> data;
    private View rootView;
    private GraphView graph;
    private TextView graphLegend1;
    private TextView graphLegend2;

    private int minPerBar = 2;  // each bar is one piece of data from each n minute chunk
    private int modNum;  // the number to % by to get a datum for every n minute chunk
    private int numBars;  // the number of bars on the graph

    public HomeFragment() {
        data = new ArrayList<>();
    }

    private void readCSVData() {
        try {
            // TODO: change where file is read from
            InputStreamReader is = new InputStreamReader(getContext().getAssets().open("sedentary.csv"));
            BufferedReader reader = new BufferedReader(is);
            String line = reader.readLine();
            do {
                String[] entry = line.split(",");
                // TODO: deal with time stamps
                // filter data to one entry 5 minutes
                modNum = minPerBar * 60;
                if (Integer.parseInt(entry[1]) % modNum == 0) {
                    data.add(entry);
                }
            } while ((line = reader.readLine()) != null);
            System.out.println(data.size());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error reading sedentary data file", Toast.LENGTH_SHORT).show();
        }
    }

    private void createGraph() {
        numBars = (int) (86400 / modNum);
        DataPoint[] points = new DataPoint[numBars];
        // add data already captured
        for (int i = 0; i < data.size(); i++) {
            // TODO: add something to deal with missing/no data
            points[i] = new DataPoint(i, 1);
        }
        System.out.println(data.size());
        // add blank points for rest of day
        for (int i = data.size(); i < numBars; i++) {
            points[i] = new DataPoint(i, 0);
            System.out.println(i);
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
        graph.getViewport().setMaxX(numBars);

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint point) {
                // sedentary or active
                String classification = data.get((int) point.getX())[0];
                if (classification.equals("sedentary")) {
                    return Color.RED;
                } else if (classification.equals("active")){
                    return getResources().getColor(R.color.colorPrimary);
                } else {
                    return Color.WHITE;
                }
            }
        });

        series.setSpacing(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
      
        graph = (GraphView) view.findViewById(R.id.bar_graph);
        graphLegend1 = (TextView) view.findViewById(R.id.todayGraphLegend);
        graphLegend1.setText(Html.fromHtml(getString(R.string.HOME_graph_legend)));
        graphLegend2 = (TextView) view.findViewById(R.id.weekGraphLegend);
        graphLegend2.setText(Html.fromHtml(getString(R.string.HOME_graph_legend)));
        
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        linearAccelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        recordButton = (Button) view.findViewById(R.id.HOME_record_button);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleRecording();
            }
        });

        buffer = new ArrayList<>(BUFFER_SIZE);

        history = HISTORY_SIZE / 2;

        file = new File(getContext().getFilesDir(), "SedentaryData");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Couldn't create a new file for SedentaryData!");
            }
        }

        try {
            writer = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            System.out.println("Couldn't create the writer for SedentaryData!");
        }
      
        readCSVData();
        createGraph();

        return view;
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
