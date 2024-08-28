package com.example.doseme;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
import com.example.doseme.notif.DoseMeAlarmScheduler;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewMedDialog.NewMedDialogListener {

    //TODO: Data export/import hübscher
    ArrayList<MedLog> logs = new ArrayList<>();

    Button btnNewMed, btnImport, btnExport;
    RecyclerView rclvwMedlist;
    ArrayList<String> notif_chnls = new ArrayList<>();

    DoseMeAlarmScheduler dmaSchedule;

    //public static String CHANNEL_ID = "NOTCHAN";
    public static int NOTPERMREQCODE = 420;



    private void createNotificationChannels() {
        for(MedLog ml: logs) {
            ml.getMed().mkNotifChannel(this);
            notif_chnls.add(ml.getMed().getNotifChannelID());
        }
    }

    public void export_dat() {
        //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);


        //startActivityForResult(intent, 3);
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "medlogsave");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, 3);

    }

    public void import_dat() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");


        startActivityForResult(intent, 4);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri =resultData.getData();

                if(DatProc.exportData(logs, uri, getContentResolver())) {
                    Snackbar.make(this, btnExport, "Exported "+logs.size()+" logs!", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(this, btnExport, "Error exporting "+logs.size()+" logs :(", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        }
        else if(requestCode == 4 && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri =resultData.getData();

                logs = DatProc.importData(uri, getContentResolver());
                assert logs != null;
                if(!logs.isEmpty()) {
                    Snackbar.make(this, btnExport, "Imported "+logs.size()+" logs!", BaseTransientBottomBar.LENGTH_SHORT).show();
                    rclvwMedlist.setAdapter(new MedAdapter(logs, this, getSupportFragmentManager(), dmaSchedule));
                    save_dat();
                } else {
                    Snackbar.make(this, btnExport, "Error importing "+logs.size()+" logs :(", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void save_dat() {
        DatProc.saveData(this, logs);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
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
        btnExport = findViewById(R.id.BTNexport);
        btnImport = findViewById(R.id.BTNimport);
        rclvwMedlist = findViewById(R.id.RCLVW_main_medlist);

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                export_dat();
            }
        });

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                import_dat();
            }
        });


        btnNewMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragMan = getSupportFragmentManager();
                NewMedDialog medDial = new NewMedDialog();

                medDial.show(fragMan, "newmed");
            }
        });

        dmaSchedule = new DoseMeAlarmScheduler(this);
        createNotificationChannels();

        rclvwMedlist.setLayoutManager(new LinearLayoutManager(this));
        MedAdapter medAdapt = new MedAdapter(logs, this, getSupportFragmentManager(), dmaSchedule);
        rclvwMedlist.setAdapter(medAdapt);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.POST_NOTIFICATIONS}, NOTPERMREQCODE);
        } else {
            Snackbar.make(this, rclvwMedlist, "Notification permission already given.", BaseTransientBottomBar.LENGTH_SHORT).show();
        }

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
    protected void onResume() {
        rclvwMedlist.getAdapter().notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onStop() {
        save_dat();
        super.onStop();
    }


}