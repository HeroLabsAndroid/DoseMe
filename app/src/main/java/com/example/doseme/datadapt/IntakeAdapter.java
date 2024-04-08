package com.example.doseme.datadapt;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doseme.R;
import com.example.doseme.dialog.IntakeStatsDialog;
import com.example.doseme.medic.Intake;
import com.example.doseme.medic.MedLog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class IntakeAdapter extends RecyclerView.Adapter<IntakeAdapter.ViewHolder>{

    public interface IntakeRemoveListener {
        void onIntakeRemoved(Intake itk);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDose, tvMult, tvTS;

        private final FloatingActionButton fabRmv;

        public ViewHolder(@NonNull View view) {
            super(view);

            tvDose = view.findViewById(R.id.TV_intakelist_dose);
            tvMult = view.findViewById(R.id.TV_intakelist_mult);
            tvTS = view.findViewById(R.id.TV_intakelist_ts);

            fabRmv = view.findViewById(R.id.FAB_intakelist_remove);
        }


        public TextView getTvMult() {
            return tvMult;
        }

        public TextView getTvDose() {
            return tvDose;
        }

        public TextView getTvTS() {
            return tvTS;
        }

        public FloatingActionButton getFabRmv() {return fabRmv;}
    }

    ArrayList<Intake> localDataSet;
    String title;

    IntakeRemoveListener listen;

    Context ctx;


    boolean allow_remove = true;



    public void setAllow_remove(boolean allow_remove) {
        this.allow_remove = allow_remove;
    }


    public IntakeAdapter(MedLog ml, boolean allow_remove, IntakeStatsDialog c) {
        localDataSet = ml.getLog();
        title = ml.getMed().getName();
        listen = (IntakeRemoveListener) c;
        ctx = c.requireContext();
        this.allow_remove = allow_remove;
    }

    public void removeItem(ViewHolder vh, Intake itk) {
        localDataSet.remove(vh.getAdapterPosition());
        notifyItemRemoved(vh.getAdapterPosition());
        listen.onIntakeRemoved(itk);
    }


    @Override
    public IntakeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lstitm_intake, parent, false);

        return new IntakeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntakeAdapter.ViewHolder holder, int position) {
        holder.getTvDose().setText(localDataSet.get(position).getDose().getId());
        holder.getTvMult().setText(String.format(Locale.getDefault(), "%.2f", localDataSet.get(position).getDose().getMultiplier()));
        holder.getTvTS().setText(String.format(Locale.getDefault(),"%2d:%2d", localDataSet.get(position).getTimestamp().getHour(), localDataSet.get(position).getTimestamp().getMinute()));

        if(allow_remove) {
            holder.getFabRmv().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(holder, localDataSet.get(holder.getAdapterPosition()));
                }
            });
        } else holder.getFabRmv().setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
