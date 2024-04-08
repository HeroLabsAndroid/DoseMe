package com.example.doseme.datadapt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doseme.R;
import com.example.doseme.dialog.ChooseDoseDialog;
import com.example.doseme.medic.Dose;
import com.example.doseme.medic.MedLog;

import java.util.ArrayList;
import java.util.Locale;

public class ChooseDoseAdapter extends RecyclerView.Adapter<ChooseDoseAdapter.ViewHolder> {

    public interface DoseChosenAdapterListener {
        public void onDoseSelectedInAdapter(Dose dose);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvMult;

        private final Button btnSelect;
        public ViewHolder(@NonNull View view) {
            super(view);

            tvName = view.findViewById(R.id.TV_choosedose_name);
            tvMult = view.findViewById(R.id.TV_choosedose_mult);

            btnSelect = view.findViewById(R.id.BTN_choosedose);
        }

        public TextView getTvName() {
            return tvName;
        }

        public TextView getTvMult() {
            return tvMult;
        }

        public Button getBtnSelect() {
            return btnSelect;
        }
    }

    ArrayList<Dose> localDataSet;
    Context ctx;

    DoseChosenAdapterListener dcaListener;

    public ChooseDoseAdapter(ArrayList<Dose> doses, ChooseDoseDialog cdDial) {
        localDataSet = doses;
        dcaListener = (DoseChosenAdapterListener) cdDial;
    }


    @Override
    public ChooseDoseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lstitm_choosedose, parent, false);

        return new ChooseDoseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseDoseAdapter.ViewHolder holder, int position) {
        holder.getTvName().setText(localDataSet.get(position).getId());
        holder.getTvMult().setText(String.format(Locale.getDefault(), "%.2f", localDataSet.get(position).getMultiplier()));
        holder.getBtnSelect().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dcaListener.onDoseSelectedInAdapter(localDataSet.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
