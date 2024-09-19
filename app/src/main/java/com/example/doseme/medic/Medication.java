package com.example.doseme.medic;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.example.doseme.datproc.DoseSave;
import com.example.doseme.datproc.LDTsave;
import com.example.doseme.datproc.MedSave;
import com.example.doseme.notif.AlarmItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;

public class Medication {
    private String name;

    private int timer_minutes;

    private String debugMsg = " ";
    private boolean has_timer = false;

    private LocalDateTime timer_set_at;
    private ArrayList<Dose> doses = new ArrayList<>();

    //---------Getter - Setter-----------------------------//


    public LocalDateTime getTimer_set_at() {
        return timer_set_at;
    }

    public void setTimer_set_at(LocalDateTime timer_set_at) {
        this.timer_set_at = timer_set_at;
    }

    public String getDebugMsg() {
        return debugMsg;
    }

    public void setDebugMsg(String debugMsg) {
        this.debugMsg = debugMsg;
    }

    public int getTimer_minutes() {
        return timer_minutes;
    }

    public void setTimer_minutes(int timer_minutes) {
        this.timer_minutes = timer_minutes;
        debugMsg = ""+timer_minutes;
    }

    public boolean isHas_timer() {
        return has_timer;
    }

    public void setHas_timer(boolean has_timer) {
        this.has_timer = has_timer;
        debugMsg = has_timer ? ""+timer_minutes : "No Timer";
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

        JSONObject jtimerset = (JSONObject) jsave.opt("timerset");

        if(jtimerset != null)
            timer_set_at = new LDTsave(jtimerset).toLDT();
        debugMsg = has_timer ? ""+timer_minutes : "No Timer";
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
            if(timer_set_at!=null) jsave.put("timerset", new LDTsave(timer_set_at).toJSONSave());
        }

        return jsave;
    }

    public String getNotifChannelID() {
        return "c"+name.hashCode();
    }

    public void mkNotifChannel(Context c) {
        NotificationChannel notchan = new NotificationChannel(getNotifChannelID(), "notif_"+name, NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notman = c.getSystemService(NotificationManager.class);
        notman.createNotificationChannel(notchan);
    }

}
