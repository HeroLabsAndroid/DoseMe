package com.example.doseme.medic;

import com.example.doseme.datproc.DoseSave;
import com.example.doseme.datproc.MedSave;

import java.util.ArrayList;

public class Medication {
    private String name;

    private ArrayList<Dose> doses = new ArrayList<>();

    //---------Getter - Setter-----------------------------//

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Dose> getDoses() {
        return doses;
    }
    public void setDoses(ArrayList<Dose> doses) {
        this.doses = doses;
    }

    //----------- CONSTRUCTORS ---------------------------//

    public Medication(MedSave ms) {
        this.name = ms.name;
        for(DoseSave ds: ms.doses) {
            doses.add(new Dose(ds));
        }
    }
    public Medication(String name) {
        this.name = name;
    }
    public Medication(String name, ArrayList<Dose> doses) {
        this.name = name; this.doses = doses;
    }

    //----------------------------------------------------//

    public void addDose(Dose ds) {
        for(Dose d: doses) {
            if(d.equals(ds)) return;
        }

        doses.add(ds);
    }

    public MedSave toSave() {
        ArrayList<DoseSave> ds = new ArrayList<>();
        for(Dose d: doses) {
            ds.add(d.toSave());
        }

        return new MedSave(ds, name);
    }
}
