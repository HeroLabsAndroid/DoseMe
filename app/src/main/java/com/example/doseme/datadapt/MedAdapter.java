package com.example.doseme.datadapt;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doseme.R;
import com.example.doseme.dialog.ChooseDoseDialog;
import com.example.doseme.dialog.IntakeStatsDialog;
import com.example.doseme.dialog.NewMedDialog;
import com.example.doseme.medic.Dose;
import com.example.doseme.medic.Intake;
import com.example.doseme.medic.MedLog;
import com.example.doseme.medic.Medication;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MedAdapter extends RecyclerView.Adapter<MedAdapter.ViewHolder> implements ChooseDoseDialog.DoseChosenListener, NewMedDialog.MedChangedListener {

    public void openChooseDoseDialog(int pos) {
        ChooseDoseDialog cdDial = new ChooseDoseDialog(this, pos, localDataSet.get(pos).getMed().getDoses(), localDataSet.get(pos).getMed().getName());
        cdDial.show(fragMan, "choosedose");
    }

    @Override
    public void onDoseChosen(Dose dose, int pos) {
        localDataSet.get(pos).takeMeds(new Intake(dose, LocalDateTime.now()));
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
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;

        private final Button btnTake, btnRemove, btnStats, btnEdits;
        public ViewHolder(@NonNull View view) {
            super(view);

            tvName = view.findViewById(R.id.TV_medlist_medname);

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

    }

    ArrayList<MedLog> localDataSet;
    Context ctx;

    FragmentManager fragMan;

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
        holder.getTvName().setText(localDataSet.get(position).getMed().getName());

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
