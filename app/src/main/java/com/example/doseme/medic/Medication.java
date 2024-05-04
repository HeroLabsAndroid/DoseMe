package com.example.doseme.medic;

import com.example.doseme.datproc.DoseSave;
import com.example.doseme.datproc.MedSave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;

public class Medication {
    private String name;

    private int timer_minutes;

    private Timer tim;

    private boolean has_timer = false;
    private ArrayList<Dose> doses = new ArrayList<>();

    //---------Getter - Setter-----------------------------//

    public Timer getTim() {
        return tim;
    }

    public void setTim(Timer tim) {
        this.tim = tim;
    }

    public int getTimer_minutes() {
        return timer_minutes;
    }

    public void setTimer_minutes(int timer_minutes) {
        this.timer_minutes = timer_minutes;
    }

    public boolean isHas_timer() {
        return has_timer;
    }

    public void setHas_timer(boolean has_timer) {
        this.has_timer = has_timer;
    }

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

    public Medication(JSONObject jsave) throws JSONException {
        doses = new ArrayList<>();
        JSONArray jdoses = jsave.getJSONArray("doses");
        for(int i=0; i<jdoses.length(); i++) {
            doses.add(new Dose(jdoses.getJSONObject(i)));
        }

        name = jsave.getString("name");
        has_timer = jsave.optBoolean("hastimer", false);
        if(has_timer) {
            timer_minutes = jsave.getInt("timer");
        }
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

    public JSONObject toJSONSave() throws JSONException {
        JSONObject jsave = new JSONObject();

        JSONArray jdosesaves = new JSONArray();
        for (Dose d: doses) {
            jdosesaves.put(d.toJSONSave());
        }

        jsave.put("doses", jdosesaves);
        jsave.put("name", name);
        jsave.put("hastimer", has_timer);
        if(has_timer) {
            jsave.put("timer", timer_minutes);
        }

        return jsave;
    }

    public void setTimer() {

    }
}
