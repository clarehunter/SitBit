package com.example.sitbit;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class GoalsFragment extends Fragment {

    private Button buttonEdit;
    private Button buttonAdd;
    private Button buttonDelete;

    public GoalsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_goals, container, false);

        buttonEdit = view.findViewById(R.id.editGoalsButton);
        buttonAdd = view.findViewById(R.id.addGoalButton);
        buttonDelete = view.findViewById(R.id.deleteGoalsButton);

        return view;
    }

}
