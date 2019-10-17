package com.example.sitbit;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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
