package com.example.sitbit;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ArrayList<String[]> data;

    public HomeFragment() {
        data = new ArrayList<>();
    }

    public void readData() {
        try {
            InputStreamReader is = new InputStreamReader(getContext().getAssets().open("sedentary.csv"));
            BufferedReader reader = new BufferedReader(is);
            String line = reader.readLine();
            Toast.makeText(getActivity(), line, Toast.LENGTH_LONG).show();
            do {
                String[] entry = line.split(",");
                data.add(entry);
            } while ((line = reader.readLine()) != null);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error reading sed", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        readData();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
