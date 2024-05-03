package com.example.doseme.datproc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
