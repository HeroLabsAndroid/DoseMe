package com.example.doseme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doseme.R;
import com.example.doseme.datadapt.DoseAdapter;
import com.example.doseme.datadapt.MedAdapter;
import com.example.doseme.medic.Dose;
import com.example.doseme.medic.Medication;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;

public class NewMedDialog extends DialogFragment implements NewDoseDialog.NewDoseDialogListener {

    boolean change = false;
    int pos;

    Medication med;

    public void openNewDoseDialog() {
        FragmentManager fragMan = requireActivity().getSupportFragmentManager();
        NewDoseDialog doseDial = new NewDoseDialog(this);

        doseDial.show(fragMan, "newmed");
    }




    public NewMedDialog() {
        change = false;
    }

    public NewMedDialog(int p, Medication med, MedChangedListener mclist) {
        change = true;
        this.med = med;
        doses = med.getDoses();
        pos = p;
        mchnglist = (MedChangedListener) mclist;
    }

    @Override
    public void onNewDoseConfirmBtnClick(Dose dose) {
        doses.add(dose);
        rclvwDoses.getAdapter().notifyItemInserted(doses.size()-1);
        rclvwDoses.getAdapter().notifyItemRangeChanged(doses.size()-1, 1);
        //Snackbar.make(rclvwDoses, String.format(Locale.getDefault(), "Added dose %s", dose.getId()), Snackbar.LENGTH_SHORT).show();
    }

    public interface NewMedDialogListener {
        public void onNewMedConfirmBtnClick(DialogFragment dialog, Medication medi);
    }

    public interface MedChangedListener {
        public void onMedChanged(DialogFragment dialog, Medication medi, int pos);
    }

    EditText etName, etTimer;
    Button btnNewDose, btnConfirm;

    SwitchMaterial swtchTimer;

    RecyclerView rclvwDoses;

    NewMedDialogListener listener;

    MedChangedListener mchnglist;


    ArrayList<Dose> doses = new ArrayList<>();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dlg_newmed, null);

        etName = (EditText) layout.findViewById(R.id.ET_newmed_name);
        etTimer = (EditText) layout.findViewById(R.id.ET_newmed_timer);
        if(change) etName.setText(med.getName());
        btnNewDose = (Button) layout.findViewById(R.id.BTN_newmed_newdose);
        btnConfirm = (Button) layout.findViewById(R.id.BTN_newmed_confirm);
        rclvwDoses = (RecyclerView) layout.findViewById(R.id.RCLVW_doses);
        swtchTimer = (SwitchMaterial) layout.findViewById(R.id.SWTCH_newmed_timer);
        if(change) {
            swtchTimer.setChecked(med.isHas_timer());
            if(med.isHas_timer()) etTimer.setText(String.format(Locale.getDefault(), "%d", med.getTimer_minutes()));
        }

        if(!swtchTimer.isChecked()) {
            etTimer.setEnabled(false);
        }

        swtchTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etTimer.setEnabled(isChecked);
            }
        });


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!change) {
                    listener.onNewMedConfirmBtnClick(NewMedDialog.this, new Medication(etName.getText().toString(), doses));
                } else {
                    med.setDoses(doses);
                    med.setName(etName.getText().toString());
                    med.setHas_timer(swtchTimer.isChecked());
                    if(swtchTimer.isChecked()) {
                        med.setTimer_minutes(Integer.parseInt(etTimer.getText().toString()));
                    }
                    mchnglist.onMedChanged(NewMedDialog.this, med, pos);
                }
                NewMedDialog.this.dismiss();
            }
        });

        btnNewDose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewDoseDialog();
            }
        });


        rclvwDoses.setLayoutManager(new LinearLayoutManager(requireActivity()));
        DoseAdapter dAdapt = new DoseAdapter(doses, requireContext());
        rclvwDoses.setAdapter(dAdapt);

        builder.setView(layout);


        // Create the AlertDialog object and return it.
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface.
        try {
            listener = (NewMedDialogListener) context;

        } catch (ClassCastException e) {
            // The activity doesn't implement the interface. Throw exception.
            throw new ClassCastException(context.toString()
                    + " must implement NewMedDialogListener");
        }

    }

}
