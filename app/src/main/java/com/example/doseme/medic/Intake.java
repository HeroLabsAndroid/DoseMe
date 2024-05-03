package com.example.doseme.medic;

import com.example.doseme.datproc.DoseSave;
import com.example.doseme.datproc.IntakeSave;
import com.example.doseme.datproc.LDTsave;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Intake {
    private Dose dose;
    private LocalDateTime ts;

    public Dose getDose() {
        return dose;
    }
    public void setDose(Dose dose) {
        this.dose = dose;
    }

    public LocalDateTime getTimestamp() {
        return ts;
    }
    public void setTimestamp(LocalDateTime ts) {
        this.ts = ts;
    }

    public Intake(Dose dose, LocalDateTime ts) {
        this.dose = dose;
        this.ts = ts;
    }

    public Intake(IntakeSave is) {
        this.dose = new Dose(is.doseSave);
        this.ts = is.ldt;
    }

    public Intake(JSONObject jsave) throws JSONException {
        ts = (new LDTsave((JSONObject) jsave.opt("ldt"))).toLDT();
        if(jsave.has("dosesave")) {
            dose = new Dose(jsave.getJSONObject("dosesave"));
        }
    }

    public JSONObject toJSONSave() throws JSONException {
        JSONObject jsave = new JSONObject();

        jsave.put("dosesave", dose.toJSONSave());
        jsave.put("ldt", new LDTsave(ts).toJSONSave());

        return jsave;
    }

    public static Intake combine(ArrayList<Intake> intakes, LocalDateTime ldt) {
        if(intakes.isEmpty()) return new Intake(new Dose("ERR", 0), ldt);

        double m = 0;
        for(Intake itk: intakes) {
            m+=itk.getDose().getMultiplier();
        }

        return new Intake(new Dose("TOT", m), intakes.get(0).getTimestamp());
    }

    public IntakeSave toSave() {
        return new IntakeSave(dose.toSave(), ts);
    }
}
