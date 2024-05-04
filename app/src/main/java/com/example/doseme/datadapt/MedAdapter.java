package com.example.doseme.datadapt;


import static android.icu.number.NumberFormatter.with;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doseme.MainActivity;
import com.example.doseme.R;
import com.example.doseme.datproc.DatProc;
import com.example.doseme.dialog.ChooseDoseDialog;
import com.example.doseme.dialog.IntakeStatsDialog;
import com.example.doseme.dialog.NewMedDialog;
import com.example.doseme.medic.Dose;
import com.example.doseme.medic.Intake;
import com.example.doseme.medic.MedLog;
import com.example.doseme.medic.Medication;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MedAdapter extends RecyclerView.Adapter<MedAdapter.ViewHolder> implements ChooseDoseDialog.DoseChosenListener, NewMedDialog.MedChangedListener {

    public TimerTask getTimerTask(int pos) {
        TimerTask timtas = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(ctx, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 4, intent, PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder notbuild = new NotificationCompat.Builder(ctx, MainActivity.CHANNEL_ID)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("achtung achtung")
                        .setContentText(String.format(Locale.getDefault(),
                                "Letzte %s Einnahme ist %d Minuten her.",
                                localDataSet.get(pos).getMed().getName(),
                                localDataSet.get(pos).getLog().get(localDataSet.get(pos).getLog().size() - 1).getTimestamp().until(LocalDateTime.now(), ChronoUnit.MINUTES)))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent);


                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                NotificationManagerCompat.from(ctx).notify(pos, notbuild.build());

                Snackbar.make(hold.getTvName(), "ding dong", BaseTransientBottomBar.LENGTH_SHORT).show();

            }
        };

        return timtas;
    }

    public void openChooseDoseDialog(int pos) {
        ChooseDoseDialog cdDial = new ChooseDoseDialog(this, pos, localDataSet.get(pos).getMed().getDoses(), localDataSet.get(pos).getMed().getName());
        cdDial.show(fragMan, "choosedose");
    }

    @Override
    public void onDoseChosen(Dose dose, int pos) {
        localDataSet.get(pos).takeMeds(new Intake(dose, LocalDateTime.now()));
        if(localDataSet.get(pos).getMed().isHas_timer()) {

            localDataSet.get(pos).getMed().setTim(new Timer());
            localDataSet.get(pos).getMed().getTim().schedule(getTimerTask(pos), (long) localDataSet.get(pos).getMed().getTimer_minutes() *1000*60);
            //hold.getTvDebug().setText(String.format(Locale.getDefault(), "%d (ON)", (long) localDataSet.get(pos).getMed().getTimer_minutes()));
            notifyItemChanged(pos);
        }
        try {
            DatProc.saveDataToJSON(ctx, localDataSet);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void removeItem(ViewHolder viewHolder) {
        localDataSet.remove(viewHolder.getAdapterPosition());
        notifyItemRemoved(viewHolder.getAdapterPosition());
    }


    @Override
    public void onMedChanged(DialogFragment dialog, Medication medi, int pos) {
        if(medi.getDoses().isEmpty()) {
        } else {
            localDataSet.get(pos).setMed(medi);
            notifyItemChanged(pos);
            try {
                DatProc.saveDataToJSON(ctx, localDataSet);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvDebug;

        private final Button btnTake, btnRemove, btnStats, btnEdits;
        public ViewHolder(@NonNull View view) {
            super(view);

            tvName = view.findViewById(R.id.TV_medlist_medname);
            tvDebug = view.findViewById(R.id.TV_main_debug);

            btnTake = view.findViewById(R.id.BTN_takemed);
            btnRemove = view.findViewById(R.id.BTN_remove);
            btnStats = view.findViewById(R.id.BTN_medstats);
            btnEdits = view.findViewById(R.id.BTN_main_edit);
        }

        public TextView getTvName() {
            return tvName;
        }

        public Button getBtnTake() {
            return btnTake;
        }

        public Button getBtnRemove() {
            return btnRemove;
        }

        public Button getBtnStats() {
            return btnStats;
        }

        public Button getBtnEdits() {return btnEdits;}

        public TextView getTvDebug() {return tvDebug;}

    }

    ArrayList<MedLog> localDataSet;
    Context ctx;

    FragmentManager fragMan;

    ViewHolder hold;

    public MedAdapter(ArrayList<MedLog> logs, Context ctx, FragmentManager fm) {
        localDataSet = logs;
        this.ctx = ctx;
        fragMan = fm;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lstitm_medication, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        hold = holder;
        holder.getTvName().setText(localDataSet.get(position).getMed().getName());

        if(localDataSet.get(position).getMed().isHas_timer()) {
            holder.getTvDebug().setText(
                    String.format(Locale.getDefault(), "%d", localDataSet.get(position).getMed().getTimer_minutes()));
        } else holder.getTvDebug().setText("No timer");

        holder.getBtnTake().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChooseDoseDialog(holder.getAdapterPosition());
            }
        });

        holder.getBtnStats().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntakeStatsDialog isDial = new IntakeStatsDialog(localDataSet.get(holder.getAdapterPosition()));
                isDial.show(fragMan, "intakestat");
            }
        });

        holder.getBtnEdits().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMedDialog nmDial = new NewMedDialog(holder.getAdapterPosition(), localDataSet.get(holder.getAdapterPosition()).getMed(), MedAdapter.this);
                nmDial.show(fragMan, "editmed");

            }
        });

        holder.getBtnRemove().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                boolean rpl = false;
                builder.setPositiveButton("I do!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeItem(holder);
                    }
                });
                builder.setNegativeButton("Nah fuck that", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancels the dialog.
                    }
                });

                builder.setTitle("r u sure about that? ( ͡° ͜ʖ ͡°)");

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
