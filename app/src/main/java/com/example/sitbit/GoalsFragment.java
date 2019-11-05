package com.example.sitbit;


import android.os.Bundle;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

        ((Button) view.findViewById(R.id.addGoalButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.add_goal_fragment);
            }
        });
        ((Button) view.findViewById(R.id.editGoalsButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.settings_account_fragment);
            }
        });
        ((Button) view.findViewById(R.id.deleteGoalsButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.settings_account_fragment);
            }
        });




        return view;
    }



}
