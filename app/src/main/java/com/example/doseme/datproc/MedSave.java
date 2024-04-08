package com.example.doseme.datproc;

import java.io.Serializable;
import java.util.ArrayList;

public class MedSave implements Serializable {
    public ArrayList<DoseSave> doses;
    public String name;

    public MedSave(ArrayList<DoseSave> doses, String name) {
        this.doses = doses;
        this.name = name;
    }
}
