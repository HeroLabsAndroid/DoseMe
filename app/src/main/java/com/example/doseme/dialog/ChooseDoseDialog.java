package com.example.doseme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doseme.R;
import com.example.doseme.datadapt.ChooseDoseAdapter;
import com.example.doseme.datadapt.MedAdapter;
import com.example.doseme.medic.Dose;

import java.util.ArrayList;

public class ChooseDoseDialog extends DialogFragment implements ChooseDoseAdapter.DoseChosenAdapterListener {
    @Override
    public void onDoseSelectedInAdapter(Dose dose) {
        listener.onDoseChosen(dose, pos);
        dismiss();
    }

    public interface DoseChosenListener {
        public void onDoseChosen(Dose dose, int pos);
    }

    TextView tvName;

    RecyclerView rclvwDoses;

    ArrayList<Dose> doses = new ArrayList<>();

    DoseChosenListener listener;

    int pos;

    String name;

    public ChooseDoseDialog(MedAdapter ma, int p, ArrayList<Dose> doses, String name) {
        listener = (DoseChosenListener) ma;
        pos = p;
        this.doses = doses;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dlg_choosedose, null);

        tvName = (TextView) layout.findViewById(R.id.TV_choosedose_medname);
        tvName.setText(name);

        rclvwDoses = (RecyclerView) layout.findViewById(R.id.RCLVW_choosedosedial_doselist);
        rclvwDoses.setLayoutManager(new LinearLayoutManager(requireActivity()));
        ChooseDoseAdapter cdAdapt = new ChooseDoseAdapter(doses, this);
        rclvwDoses.setAdapter(cdAdapt);

        builder.setView(layout);


        // Create the AlertDialog object and return it.
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface.
        try {

        } catch (ClassCastException e) {
            // The activity doesn't implement the interface. Throw exception.
            throw new ClassCastException(context.toString()
                    + " must implement NewDoseDialogListener");
        }

    }
}
