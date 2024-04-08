package com.example.doseme.datadapt;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.doseme.R;
import com.example.doseme.dialog.NewMedDialog;
import com.example.doseme.medic.Dose;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class DoseAdapter extends RecyclerView.Adapter<DoseAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final EditText etName, etMult;
        private final FloatingActionButton btnRmv;

        public ViewHolder(View view) {
            super(view);

            etName = view.findViewById(R.id.ET_doselist_dosename);
            etMult = view.findViewById(R.id.ET_doselist_mult);
            btnRmv = view.findViewById(R.id.BTN_doselist_remove);
        }

        public TextView getEtName() {
            return etName;
        }

        public TextView getEtMult() {
            return etMult;
        }

        public FloatingActionButton getBtnRmv() {return btnRmv;}
    }

    ArrayList<Dose> doses;
    Context ctx;

    public DoseAdapter(ArrayList<Dose> doses, Context c) {
        this.doses = doses;
        ctx = c;
    }

    public void removeItem(DoseAdapter.ViewHolder viewHolder) {
        doses.remove(viewHolder.getAdapterPosition());
        notifyItemRemoved(viewHolder.getAdapterPosition());
    }

    public void changeName(DoseAdapter.ViewHolder viewHolder, String newname) {
        doses.get(viewHolder.getAdapterPosition()).setId(newname);
    }

    public void changeMult(DoseAdapter.ViewHolder viewHolder, float newmult) {
        doses.get(viewHolder.getAdapterPosition()).setMultiplier(newmult);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lstitm_dose, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getEtName().setText(doses.get(position).getId());
        holder.getEtName().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeName(holder, s.toString());
            }
        });

        holder.getEtMult().setText(String.format(Locale.getDefault(), "%.2f", doses.get(position).getMultiplier()));
        holder.getEtMult().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeMult(holder, Float.parseFloat(s.toString()));
            }
        });

        holder.getBtnRmv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(holder);
            }
        });

    }

    @Override
    public int getItemCount() {
        return doses.size();
    }
}
