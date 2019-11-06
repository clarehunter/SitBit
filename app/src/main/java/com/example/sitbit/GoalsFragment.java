package com.example.sitbit;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoalsFragment extends Fragment {
    private SeekBar sBar;
    private TextView tView;

    public GoalsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        sBar = (SeekBar) getView().findViewById(R.id.total_time_seek);
//        tView = (TextView) getView().findViewById(R.id.total_time_text);
//        tView.setText(sBar.getProgress() + "/" + sBar.getMax());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

}
