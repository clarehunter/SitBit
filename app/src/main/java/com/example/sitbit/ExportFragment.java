package com.example.sitbit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class ExportFragment extends Fragment {

    public static final int WRITE_CODE = 420;

    private Button exportButton;

    private Globals globals;

    public ExportFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export, container, false);

        exportButton = view.findViewById(R.id.EXPORT_export_button);

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, "SedentaryData_" + System.currentTimeMillis());
                startActivityForResult(intent, WRITE_CODE);
            }
        });

        globals = Globals.getInstance();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ExportFragment.WRITE_CODE && resultCode == Activity.RESULT_OK) {

            try (Scanner reader = new Scanner(globals.getSedentaryDataFile())){

                OutputStream outputStream = getActivity().getContentResolver().openOutputStream(data.getData());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

                // write from firebase?

                // temp local export
                while (reader.hasNextLine())
                    writer.write(reader.nextLine() + "\n");

                writer.close();
            } catch (Exception e) {}
        }
    }
}
