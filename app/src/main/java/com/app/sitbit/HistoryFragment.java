package com.app.sitbit;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;


public class HistoryFragment extends Fragment {

    public static final int MILLISECS_PER_BAR = 360000;
    public static final int N_BARS = Globals.MILLISECS_PER_DAY / MILLISECS_PER_BAR;

    private CalendarView calendar;
    private GraphView graph;
    private TextView graphLegend;
    private TextView date;
    private TextView sedentaryTime;
    private TextView activeTime;

    private Globals globals;


    public HistoryFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // graphical artifact init
        graph = view.findViewById(R.id.HIST_bar_graph);
        graphLegend = view.findViewById(R.id.HIST_graph_legend);
        date = view.findViewById(R.id.HIST_graph_title);
        graphLegend.setText(Html.fromHtml(getString(R.string.HOME_graph_legend)));
        sedentaryTime = view.findViewById(R.id.HIST_sedentary_time);
        activeTime = view.findViewById(R.id.HIST_active_time);

        // calendar init
        calendar = view.findViewById(R.id.HIST_calendar_view);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView cal, int year, int month, int day) {
                Calendar calDay = Calendar.getInstance();
                calDay.set(Calendar.YEAR, year);
                calDay.set(Calendar.MONTH, month);
                calDay.set(Calendar.DAY_OF_MONTH, day);

                String dateText = ((month + 1) + "/" + day);
                date.setText(dateText);
                createGraph(calDay.getTimeInMillis());
            }
        });

        globals = Globals.getInstance();

        // set initial date text and graph

        Calendar calDay = Calendar.getInstance();
        calDay.setTimeInMillis(System.currentTimeMillis());
        String dateText = (calDay.get(Calendar.MONTH) + 1) + "/" + calDay.get(Calendar.DAY_OF_MONTH);
        date.setText(dateText);
        createGraph(calendar.getDate());

        return view;
    }

    private void createGraph(long time) {

        // creating graph for selected day's data
        // startTime = start of day
        // endTime = end of day
        Calendar[] lastDay = Globals.getDayInterval(time);
        final long startTime = lastDay[0].getTimeInMillis();
        long endTime = lastDay[1].getTimeInMillis();


        globals.getDataEntries(startTime, endTime, new Consumer<HashMap<Long, Boolean>>() {
            @Override
            public void accept(HashMap<Long, Boolean> data) {

                if (data == null) {
                    Toast.makeText(getContext(), R.string.HISTORY_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
                    return;
                }

                // sort data from earliest to latest
                ArrayList<Long> keys = new ArrayList<>(data.keySet());
                Collections.sort(keys);

                int nActiveEntries = 0;

                for (Boolean entry : data.values())
                    if (entry)
                        nActiveEntries++;

                sedentaryTime.setText("" + (keys.size() - nActiveEntries));
                activeTime.setText("" + nActiveEntries);

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

}
