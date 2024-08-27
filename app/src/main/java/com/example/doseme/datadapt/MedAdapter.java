package com.example.doseme.datadapt;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doseme.R;
import com.example.doseme.datproc.DatProc;
import com.example.doseme.dialog.ChooseDoseDialog;
import com.example.doseme.dialog.IntakeStatsDialog;
import com.example.doseme.dialog.NewMedDialog;
import com.example.doseme.medic.Dose;
import com.example.doseme.medic.Intake;
import com.example.doseme.medic.MedLog;
import com.example.doseme.medic.Medication;
import com.example.doseme.notif.AlarmItem;
import com.example.doseme.notif.DoseMeAlarmScheduler;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;

public class MedAdapter extends RecyclerView.Adapter<MedAdapter.ViewHolder> implements ChooseDoseDialog.DoseChosenListener, NewMedDialog.MedChangedListener {


    public void openChooseDoseDialog(int pos) {
        ChooseDoseDialog cdDial = new ChooseDoseDialog(this, pos, localDataSet.get(pos).getMed().getDoses(), localDataSet.get(pos).getMed().getName());
        cdDial.show(fragMan, "choosedose");
    }

    @Override
    public void onDoseChosen(Dose dose, int pos) {
        localDataSet.get(pos).takeMeds(new Intake(dose, LocalDateTime.now()));
        if(localDataSet.get(pos).getMed().isHas_timer()) {
            AlarmItem item = new AlarmItem(
                    LocalDateTime.now().plusMinutes(localDataSet.get(pos).getMed().getTimer_minutes()),
                    "Achtung Achtung, letzte "+localDataSet.get(pos).getMed().getName()+"-Einnahme ist "+localDataSet.get(pos).getMed().getTimer_minutes()+" Minuten her.",
                    localDataSet.get(pos).getMed().getNotifChannelID(), pos);

            dmaSchedule.schedule(item);
            localDataSet.get(pos).getMed().setTimer_set_at(LocalDateTime.now());
            notifyItemChanged(pos);
            Snackbar.make(hold.itemView, localDataSet.get(pos).getMed().getName()+"-Alarm set at "+ LocalDateTime.now().toString()+" for "+LocalDateTime.now().plusMinutes(localDataSet.get(pos).getMed().getTimer_minutes()).toString(), Snackbar.LENGTH_LONG).show();
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

    DoseMeAlarmScheduler dmaSchedule;

    ViewHolder hold;

    public MedAdapter(ArrayList<MedLog> logs, Context ctx, FragmentManager fm, DoseMeAlarmScheduler dmaSchedule) {
        localDataSet = logs;
        this.ctx = ctx;
        fragMan = fm;
        this.dmaSchedule = dmaSchedule;
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

        if(!localDataSet.get(position).getMed().isHas_timer())
            holder.getTvDebug().setText("No Timer");
        else if(localDataSet.get(position).getMed().getTimer_set_at()==null)
            holder.getTvDebug().setText(String.format(Locale.getDefault(),"%d min", localDataSet.get(position).getMed().getTimer_minutes()));
        else if(localDataSet.get(position).getMed().getTimer_set_at().plusMinutes(localDataSet.get(position).getMed().getTimer_minutes()).isBefore(LocalDateTime.now())) {
            localDataSet.get(position).getMed().setTimer_set_at(null);
            holder.getTvDebug().setText(String.format(Locale.getDefault(),"%d min", localDataSet.get(position).getMed().getTimer_minutes()));
        } else {
            holder.getTvDebug().setText(String.format(Locale.getDefault(),"FIRE AWAY (in %d min)", LocalDateTime.now().until(localDataSet.get(position).getMed().getTimer_set_at().plusMinutes(localDataSet.get(position).getMed().getTimer_minutes()), ChronoUnit.MINUTES)));
        }

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
