package com.example.doseme;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewMedDialog.NewMedDialogListener {

    ArrayList<MedLog> logs = new ArrayList<>();

    Button btnNewMed;
    RecyclerView rclvwMedlist;

    public static String CHANNEL_ID = "NOTCHAN";
    public static int NOTPERMREQCODE = 420;

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.POST_NOTIFICATIONS}, NOTPERMREQCODE);
        } else {
            Snackbar.make(this, rclvwMedlist, "Notification permission already given.", BaseTransientBottomBar.LENGTH_SHORT).show();
        }

        createNotificationChannel();

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