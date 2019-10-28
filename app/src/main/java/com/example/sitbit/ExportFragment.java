package com.example.sitbit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ExportFragment extends Fragment {

    public static final int DAY_CODE = 47;
    public static final int WEEK_CODE = 48;
    public static final int MONTH_CODE = 49;
    public static final int FROM_CODE = 50;

    private RadioGroup radioGroup;

    private Button exportButton;

    private Globals globals;

    public ExportFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export, container, false);

        radioGroup = view.findViewById(R.id.EXPORT_radio_group_date);

        exportButton = view.findViewById(R.id.EXPORT_export_button);

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TITLE, "SedentaryData_" + System.currentTimeMillis());

                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.EXPORT_radio_day: startActivityForResult(intent, DAY_CODE); break;
                        case R.id.EXPORT_radio_week: startActivityForResult(intent, WEEK_CODE); break;
                        case R.id.EXPORT_radio_month: startActivityForResult(intent, MONTH_CODE); break;
                        case R.id.EXPORT_radio_from: startActivityForResult(intent, FROM_CODE); break;
                    }
                } else {

                }
            }
        });

        globals = Globals.getInstance();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {

            long currentTime = System.currentTimeMillis();
            long startTime, endTime;

            if (requestCode == DAY_CODE) {
                startTime = currentTime - currentTime % Globals.MILLISECS_PER_DAY;
                endTime = startTime + Globals.MILLISECS_PER_DAY;
            } else if (requestCode == WEEK_CODE) {
                startTime = currentTime - (6 * Globals.MILLISECS_PER_DAY) - currentTime % Globals.MILLISECS_PER_DAY;
                endTime = startTime + (7 * Globals.MILLISECS_PER_DAY);
            } else if (requestCode == MONTH_CODE) {
                startTime = currentTime - (30 * Globals.MILLISECS_PER_DAY) - currentTime % Globals.MILLISECS_PER_DAY;
                endTime = startTime + (31 * Globals.MILLISECS_PER_DAY);
            } else if (requestCode == FROM_CODE) {
                startTime = 0;
                endTime = 0;
            } else {
                return;
            }

            globals.getDataEntries(startTime, endTime, new Consumer<HashMap<Long, Boolean>>() {
                @Override
                public void accept(HashMap<Long, Boolean> data) {

                    try (OutputStream outputStream = getActivity().getContentResolver().openOutputStream(intent.getData());
                         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {

                        ArrayList<Long> keys = new ArrayList<>(data.keySet());
                        Collections.sort(keys);

                        for (Long key : keys)
                            writer.write(key + "," + (data.get(key) ? "active" : "sedentary") + "\n");

                    } catch (IOException e) {

                    }
                }
            });
        }
    }
}
