package com.example.sitbit;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class ExportFragment extends Fragment {

    public static final int WRITE_CODE = 420;

    private Button exportButton;

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
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_TITLE, "SedentaryData_" + System.currentTimeMillis());
                startActivityForResult(intent, WRITE_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ExportFragment.WRITE_CODE && resultCode == Activity.RESULT_OK) {
            File file = new File(getContext().getFilesDir(), "SedentaryData");

            try {
                file.createNewFile();
                Scanner scanner = new Scanner(file);

                OutputStream outputStream = getActivity().getContentResolver().openOutputStream(data.getData());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

                writer.write("HELLO LEIGHANN");

                scanner.close();
                writer.close();
            } catch (Exception e) {}
        }
    }
}
