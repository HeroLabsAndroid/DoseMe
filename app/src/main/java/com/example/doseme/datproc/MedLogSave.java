package com.example.doseme.datproc;

import java.io.Serializable;
import java.util.ArrayList;

public class MedLogSave implements Serializable {
    public MedSave medSave;
    public ArrayList<IntakeSave> intakes;

    public MedLogSave(MedSave medSave, ArrayList<IntakeSave> intakes) {
        this.medSave = medSave;
        this.intakes = intakes;
    }
}
