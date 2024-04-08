package com.example.doseme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doseme.R;
import com.example.doseme.medic.Dose;
import com.example.doseme.medic.Medication;

import java.util.ArrayList;

public class NewDoseDialog extends DialogFragment {

    public interface NewDoseDialogListener {
        public void onNewDoseConfirmBtnClick(Dose dose);
    }

    EditText etName, etMult;
    Button btnConfirm;


    NewDoseDialogListener listener;

    public NewDoseDialog(NewMedDialog nmDial) {
        listener = (NewDoseDialogListener) nmDial;
    }

    public void confirmDose() {
        listener.onNewDoseConfirmBtnClick(new Dose(etName.getText().toString(), (etMult.getText().length()>0) ? Double.parseDouble(etMult.getText().toString()) : 0));
        NewDoseDialog.this.dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dlg_newdose, null);

        etName = (EditText) layout.findViewById(R.id.ET_newdose_name);
        etMult = (EditText) layout.findViewById(R.id.ET_newdose_mult);
        btnConfirm = (Button) layout.findViewById(R.id.BTN_newdose_confirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDose();
            }
        });


        builder.setView(layout);


        // Create the AlertDialog object and return it.
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface.
        try {
            //listener = (NewDoseDialogListener) context;

        } catch (ClassCastException e) {
            // The activity doesn't implement the interface. Throw exception.
            throw new ClassCastException(context.toString()
                    + " must implement NewDoseDialogListener");
        }

    }
}
