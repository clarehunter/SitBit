package com.example.sitbit;


import android.os.Bundle;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;


public class GoalsFragment extends Fragment {

    private SeekBar seekBar;
    private TextView seekBarValue;
    private ProgressBar progressBar;
    private TextView progressBarValue;
    private Button updateButton;

    private Globals globals;

    public GoalsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        seekBar = view.findViewById(R.id.GOALS_goal_seekbar);
        seekBarValue = view.findViewById(R.id.GOALS_goal_seekbar_value);
        progressBar = view.findViewById(R.id.GOALS_progressbar);
        progressBarValue = view.findViewById(R.id.GOALS_progressbar_value);
        updateButton = view.findViewById(R.id.GOALS_update_button);

        globals = Globals.getInstance();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                update(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globals.setGoal(seekBar.getProgress());
            }
        });

        globals.getGoal(new Consumer<Integer>() {
            @Override
            public void accept(Integer goal) {
                if (goal >= 0 && goal <= 24) {
                    seekBar.setProgress(goal);
                    update(goal);
                } else {
                    //error
                }
            }
        });

        return view;
    }


    private void update(final int goal) {
        seekBarValue.setText(goal + " Hr(s)");

        Calendar[] interval = Globals.getDayInterval(System.currentTimeMillis());

        globals.getDataEntries(interval[0].getTimeInMillis(), interval[1].getTimeInMillis(), new Consumer<HashMap<Long, Boolean>>() {
            @Override
            public void accept(HashMap<Long, Boolean> data) {

                double secondsActive = 0;

                for (Boolean entry : data.values())
                    if (entry)
                        secondsActive += HomeFragment.ENTRY_DURATION;

                progressBar.setProgress((int) (secondsActive / (goal * 3600)));
                progressBarValue.setText(goal == 0 ? "-" : String.format("%.1f%%", secondsActive / (goal * 36)));
            }
        });
    }

}
