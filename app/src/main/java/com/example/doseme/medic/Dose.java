package com.example.doseme.medic;

import com.example.doseme.datproc.DoseSave;

import java.util.ArrayList;

public class Dose {
    private String id;

    private double multiplier;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public double getMultiplier() {
        return multiplier;
    }
    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public Dose(String id, double multiplier) {
        this.id = id;
        this.multiplier = multiplier;
    }

    public Dose(DoseSave ds) {
        this.id = ds.id;
        this.multiplier = ds.multiplier;
    }



    DoseSave toSave() {
        return new DoseSave(id, multiplier);
    }

    public boolean equals(Dose d2) {
        return (id.compareTo(d2.getId()) == 0);
    }
}
