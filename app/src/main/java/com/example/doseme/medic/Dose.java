package com.example.doseme.medic;

import com.example.doseme.datproc.DoseSave;

import org.json.JSONException;
import org.json.JSONObject;

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

    public Dose(JSONObject jsave) {
        id = jsave.optString("id", "INVAL");
        multiplier = jsave.optDouble("mult", 0);
    }

    public JSONObject toJSONSave() throws JSONException {
        JSONObject jsave = new JSONObject();
        jsave.put("id", id);
        jsave.put("mult", multiplier);
        return jsave;
    }

    DoseSave toSave() {
        return new DoseSave(id, multiplier);
    }

    public boolean equals(Dose d2) {
        return (id.compareTo(d2.getId()) == 0);
    }
}
