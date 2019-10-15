package com.example.sitbit;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ArrayList<String[]> data;
    private View rootView;
    private GraphView graph;

    public HomeFragment() {
        data = new ArrayList<>();
    }

    private void readData() {
        try {
            // TODO: change where file is read from
            InputStreamReader is = new InputStreamReader(getContext().getAssets().open("sedentary.csv"));
            BufferedReader reader = new BufferedReader(is);
            String line = reader.readLine();
            do {
                String[] entry = line.split(",");
                // TODO: deal with time stamps
                // filter data to one entry each minute
                if (Integer.parseInt(entry[1]) % 60 == 0) {
                    data.add(entry);
                }
            } while ((line = reader.readLine()) != null);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error reading sedentary data file", Toast.LENGTH_SHORT).show();
        }
    }

    private void createGraph() {
        DataPoint[] points = new DataPoint[data.size()];
        for (int i = 0; i < data.size(); i++) {
            // TODO: add something to deal with missing/no data
            points[i] = new DataPoint(i, 1);
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);
        graph.addSeries(series);

        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(1);

        // set manual X bounds
        //graph.getViewport().setXAxisBoundsManual(true);
        //graph.getViewport().setMinX(0);
        //graph.getViewport().setMaxX(1440);

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint point) {
                // sedentary or active
                String classification = data.get((int) point.getX())[0];
                if (classification.equals("sedentary")) {
                    return Color.RED;
                } else {
                    return Color.GREEN;
                }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        graph = (GraphView) rootView.findViewById(R.id.bar_graph);
        readData();
        createGraph();
        return rootView;
    }

}
