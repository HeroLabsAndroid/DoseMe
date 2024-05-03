package com.example.doseme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.doseme.datadapt.MedAdapter;
import com.example.doseme.datproc.DatProc;
import com.example.doseme.dialog.NewDoseDialog;
import com.example.doseme.dialog.NewMedDialog;
import com.example.doseme.medic.Dose;
import com.example.doseme.medic.MedLog;
import com.example.doseme.medic.Medication;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewMedDialog.NewMedDialogListener {

    ArrayList<MedLog> logs = new ArrayList<>();

    Button btnNewMed;
    RecyclerView rclvwMedlist;


    public void save_dat() {
        DatProc.saveData(this, logs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logs = DatProc.loadData(this);

        try {
            String jsavestr = DatProc.saveDataToJSON(this, logs);
            Log.d("JSON DATA", jsavestr);
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }

        btnNewMed = findViewById(R.id.BTN_main_addmed);
        rclvwMedlist = findViewById(R.id.RCLVW_main_medlist);


        btnNewMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragMan = getSupportFragmentManager();
                NewMedDialog medDial = new NewMedDialog();

                medDial.show(fragMan, "newmed");
            }
        });

        rclvwMedlist.setLayoutManager(new LinearLayoutManager(this));
        MedAdapter medAdapt = new MedAdapter(logs, this, getSupportFragmentManager());
        rclvwMedlist.setAdapter(medAdapt);

    }

    @Override
    public void onNewMedConfirmBtnClick(DialogFragment dialog, Medication medi) {
        if(medi.getDoses().isEmpty()) {
            Snackbar.make(btnNewMed, "Can't add Medication with no dosage!", Snackbar.LENGTH_SHORT).show();
        } else {
            logs.add(new MedLog(medi));
            rclvwMedlist.getAdapter().notifyItemInserted(logs.size()-1);
            rclvwMedlist.getAdapter().notifyItemRangeChanged(logs.size()-1, 1);
            save_dat();
        }


    }



    @Override
    protected void onStop() {
        save_dat();
        super.onStop();
    }
}