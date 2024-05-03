package com.example.doseme.datproc;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;

public class IntakeSave implements Serializable {
    public DoseSave doseSave;
    public LocalDateTime ldt;

    public IntakeSave(DoseSave doseSave, LocalDateTime ldt) {
        this.doseSave = doseSave;
        this.ldt = ldt;
    }


}
