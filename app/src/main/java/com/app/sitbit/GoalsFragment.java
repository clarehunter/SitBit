package com.app.sitbit;


import android.os.Bundle;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;


public class GoalsFragment extends Fragment {

    private SeekBar seekBar;
    private TextView seekBarValue;
    private ProgressBar progressBar;
    private TextView progressBarValue;

    private Globals globals;

    public GoalsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        seekBar = view.findViewById(R.id.GOALS_goal_seekbar);
        seekBarValue = view.findViewById(R.id.GOALS_goal_seekbar_value);
        progressBar = view.findViewById(R.id.GOALS_progressbar);
        progressBarValue = view.findViewById(R.id.GOALS_progressbar_value);

        globals = Globals.getInstance();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                update(i);

                if (b)
                    globals.setAttribute("ActivityGoal", i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        globals.getAttribute("ActivityGoal", new Consumer<Object>() {
            @Override
            public void accept(Object o) {

                if (o != null) {
                    int goal = ((Long) o).intValue();
                    if (goal >= 0 && goal <= 24) {
                        seekBar.setProgress(goal);
                        update(goal);
                    }
                } else {
                    Toast.makeText(getContext(), R.string.GOALS_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


    private void update(final int goal) {
        seekBarValue.setText(goal + " Hr(s)");

        if (goal != 0) {

            Calendar[] interval = Globals.getDayInterval(System.currentTimeMillis());

            globals.getDataEntries(interval[0].getTimeInMillis(), interval[1].getTimeInMillis(), new Consumer<HashMap<Long, Boolean>>() {
                @Override
                public void accept(HashMap<Long, Boolean> data) {

                    if (data == null) {
                        Toast.makeText(getContext(), R.string.GOALS_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int secondsActive = 0;

                    for (Boolean entry : data.values())
                        if (entry)
                            secondsActive += Globals.TRANSMISSION_FREQ;

                    int percent = secondsActive / (goal * 36);

                    percent = percent > 100 ? 100 : percent;

                    progressBar.setProgress(percent);
                    progressBarValue.setText(String.format("%d%%", percent));
                }
            });
        } else {
            progressBar.setProgress(0);
            progressBarValue.setText("");
        }
    }

}
