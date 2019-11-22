package com.example.sitbit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class ExportFragment extends Fragment {

    public static final int WRITE_CODE = 0xF;

    private Spinner fromSpinner;
    private Spinner toSpinner;
    private Button exportButton;

    private ArrayList<Calendar> days;

    private Globals globals;

    public ExportFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export, container, false);

        fromSpinner = view.findViewById(R.id.EXPORT_fromspinner);
        toSpinner = view.findViewById(R.id.EXPORT_tospinner);

        days = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        int today = cal.get(Calendar.DAY_OF_MONTH);

        do {
            days.add(cal);
            cal = (Calendar) cal.clone();
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
        } while (cal.get(Calendar.DAY_OF_MONTH) != today);

        ArrayList<String> arrayAdapterList = new ArrayList<>();

        for (Calendar c : days)
            arrayAdapterList.add((c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext(), R.layout.support_simple_spinner_dropdown_item, arrayAdapterList);

        fromSpinner.setAdapter(arrayAdapter);
        toSpinner.setAdapter(arrayAdapter);

        exportButton = view.findViewById(R.id.EXPORT_export_button);

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int fromPos = fromSpinner.getSelectedItemPosition();
                int toPos = toSpinner.getSelectedItemPosition();

                if (fromPos < 0 || fromPos > 31 || toPos < 0 || toPos > 31) {
                    Toast.makeText(getContext(), R.string.EXPORT_unknown_error, Toast.LENGTH_SHORT).show();
                } else if (toPos > fromPos) {
                    Toast.makeText(getContext(), R.string.EXPORT_invalid_date_interval, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TITLE, "SedentaryData");

                    startActivityForResult(intent, (WRITE_CODE << 12) | (fromPos << 6) | toPos);
                }
            }
        });

        globals = Globals.getInstance();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK && (requestCode >> 12) == WRITE_CODE) {

            int fromPos = (requestCode >> 6) & (0x3F);
            int toPos = requestCode & (0x3F);

            final Calendar startInterval = days.get(fromPos);
            final Calendar endInterval = (Calendar) days.get(toPos).clone();

            endInterval.set(Calendar.DAY_OF_MONTH, endInterval.get(Calendar.DAY_OF_MONTH) + 1);

            globals.getDataEntries(startInterval.getTimeInMillis(), endInterval.getTimeInMillis(), new Consumer<HashMap<Long, Boolean>>() {
                @Override
                public void accept(HashMap<Long, Boolean> data) {

                    if (data == null) {
                        Toast.makeText(getContext(), R.string.EXPORT_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try (OutputStream outputStream = getActivity().getContentResolver().openOutputStream(intent.getData());
                         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {

                        ArrayList<Long> keys = new ArrayList<>(data.keySet());
                        Collections.sort(keys);

                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());

                        writer.write("Exported On," + c.getTime() + "\n");
                        writer.write("Entries Start," + startInterval.getTime() + "\n");
                        writer.write("Entries End," + endInterval.getTime() + "\n");
                        writer.write("Number Of Entries," + keys.size() + "\n");

                        int nActiveEntries = 0;

                        for (Boolean entry : data.values())
                            if (entry)
                                nActiveEntries++;

                        writer.write("Number of Active Entries," + nActiveEntries + "\n");
                        writer.write("Minutes Active," + nActiveEntries + "\n");
                        writer.write("Number of Sedentary Entries," + (keys.size() - nActiveEntries) + "\n");
                        writer.write("Minutes Sedentary," + (keys.size() - nActiveEntries) + "\n");

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
