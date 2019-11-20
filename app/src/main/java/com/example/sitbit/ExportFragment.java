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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class ExportFragment extends Fragment {

    public static final int DAY_CODE = 47;
    public static final int WEEK_CODE = 48;
    public static final int MONTH_CODE = 49;

    private RadioGroup radioGroup;
    private RadioButton radioLastDay;
    private RadioButton radioLastWeek;
    private RadioButton radioLastMonth;

    private Button exportButton;

    private Globals globals;

    public ExportFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export, container, false);

        radioGroup = view.findViewById(R.id.EXPORT_radio_group_date);

        long currentTime = System.currentTimeMillis();

        radioLastDay = view.findViewById(R.id.EXPORT_radio_day);
        final Calendar[] lastDay = Globals.getDayInterval(currentTime);
        String lastDayText = String.format("Last Day: [%d/%d, %d/%d]", lastDay[0].get(Calendar.DAY_OF_MONTH), lastDay[0].get(Calendar.MONTH) + 1, lastDay[1].get(Calendar.DAY_OF_MONTH), lastDay[1].get(Calendar.MONTH) + 1);
        radioLastDay.setText(lastDayText);

        radioLastWeek = view.findViewById(R.id.EXPORT_radio_week);
        final Calendar[] lastWeek = Globals.getWeekInterval(currentTime);
        String lastWeekText = String.format("Last Week: [%d/%d, %d/%d]", lastWeek[0].get(Calendar.DAY_OF_MONTH), lastWeek[0].get(Calendar.MONTH) + 1, lastWeek[1].get(Calendar.DAY_OF_MONTH), lastWeek[1].get(Calendar.MONTH) + 1);
        radioLastWeek.setText(lastWeekText);

        radioLastMonth = view.findViewById(R.id.EXPORT_radio_month);
        final Calendar[] lastMonth = Globals.getMonthInterval(currentTime);
        String lastMonthText = String.format("Last Month: [%d/%d, %d/%d]", lastMonth[0].get(Calendar.DAY_OF_MONTH), lastMonth[0].get(Calendar.MONTH) + 1, lastMonth[1].get(Calendar.DAY_OF_MONTH), lastMonth[1].get(Calendar.MONTH) + 1);
        radioLastMonth.setText(lastMonthText);

        exportButton = view.findViewById(R.id.EXPORT_export_button);

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TITLE, "SedentaryData");

                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.EXPORT_radio_day: startActivityForResult(intent, DAY_CODE); break;
                        case R.id.EXPORT_radio_week: startActivityForResult(intent, WEEK_CODE); break;
                        case R.id.EXPORT_radio_month: startActivityForResult(intent, MONTH_CODE); break;
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

            Calendar[] interval;

            switch (requestCode) {
                case DAY_CODE: interval = Globals.getDayInterval(currentTime); break;
                case WEEK_CODE: interval = Globals.getWeekInterval(currentTime); break;
                case MONTH_CODE: interval = Globals.getMonthInterval(currentTime); break;
                default: interval = null;
            }

            globals.getDataEntries(interval[0].getTimeInMillis(), interval[1].getTimeInMillis(), new Consumer<HashMap<Long, Boolean>>() {
                @Override
                public void accept(HashMap<Long, Boolean> data) {

                    try (OutputStream outputStream = getActivity().getContentResolver().openOutputStream(intent.getData());
                         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {

                        ArrayList<Long> keys = new ArrayList<>(data.keySet());
                        Collections.sort(keys);

                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());

                        writer.write("Exported On," + c.getTime() + "\n");
                        writer.write("Number Of Entries," + keys.size() + "\n");

                        int nActiveEntries = 0;

                        for (Boolean entry : data.values())
                            if (entry)
                                nActiveEntries++;

                        writer.write("Number of Active Entries," + nActiveEntries + "\n");
                        writer.write("Seconds Active," + (nActiveEntries * Globals.TRANSMISSION_FREQ) + "\n");
                        writer.write("Number of Sedentary Entries," + (keys.size() - nActiveEntries) + "\n");
                        writer.write("Seconds Sedentary," + ((keys.size() - nActiveEntries) *Globals.TRANSMISSION_FREQ) + "\n");

                        for (Long key : keys) {
                            c.setTimeInMillis(key);
                            writer.write(c.getTime() + "," + (data.get(key) ? "active" : "sedentary") + "\n");
                        }

                    } catch (IOException e) {

                    }
                }
            });
        }
    }
}
