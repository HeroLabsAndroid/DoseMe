package com.example.doseme.datproc;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class DoseSave implements Serializable {
    public String id;
    public double multiplier;

    public DoseSave(String id, double multiplier) {
        this.id = id;
        this.multiplier = multiplier;
    }




}
