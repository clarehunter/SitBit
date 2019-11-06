package com.example.sitbit;


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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    public static final int MILLISECS_PER_BAR = 360000;
    public static final int N_BARS = Globals.MILLISECS_PER_DAY / MILLISECS_PER_BAR;

    private CalendarView calendar;
    private GraphView graph;
    private TextView graphLegend;
    private TextView date;

    private Globals globals;


    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // graphical artifact init
        graph = (GraphView) view.findViewById(R.id.hist_bar_graph);
        graphLegend = (TextView) view.findViewById(R.id.histGraphLegend);
        date = (TextView) view.findViewById(R.id.histGraphTitle);
        graphLegend.setText(Html.fromHtml(getString(R.string.HOME_graph_legend)));

        // calendar init
        calendar = (CalendarView) view.findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView cal, int year, int month, int day) {
                Date newDate = new Date(year, month, day);
                String dateText = (newDate.getMonth() + 1) + "/" + newDate.getDate();
                date.setText(dateText);
                createGraph(newDate.getTime());
            }
        });

        globals = Globals.getInstance();

        // set initial date text and graph
        Date curDate = new Date(calendar.getDate());
        String dateText = (curDate.getMonth() + 1) + "/" + curDate.getDate();
        date.setText(dateText);
        createGraph(calendar.getDate());

        return view;
    }

    private void createGraph(long date) {

        // creating graph for selected day's data
        // startTime = start of day
        // endTime = end of day
        final long startTime = date;
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

}
