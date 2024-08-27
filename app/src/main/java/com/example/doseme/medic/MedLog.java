package com.example.doseme.medic;

import com.example.doseme.StatTimeframe;
import com.example.doseme.Util;
import com.example.doseme.datproc.DoseSave;
import com.example.doseme.datproc.IntakeSave;
import com.example.doseme.datproc.MedLogSave;
import com.example.doseme.datproc.MedSave;
import com.example.doseme.statview.StatViewable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;

public class MedLog implements StatViewable {
    private Medication med;



    private ArrayList<Intake> log = new ArrayList<>();

    public MedLog(Medication med) {
        this.med = med;
    }

    public MedLog(MedLogSave mls) {
        med = new Medication(mls.medSave);
        for(IntakeSave is: mls.intakes) {
            log.add(new Intake(is));
        }
    }

    public MedLog(JSONObject jsave) throws JSONException {
        med = new Medication(jsave.getJSONObject("med"));

        JSONArray jitksave = jsave.getJSONArray("intakes");
        log = new ArrayList<>();
        for(int i=0; i< jitksave.length(); i++) {
            log.add(new Intake(jitksave.getJSONObject(i)));
        }
    }

    public MedLogSave toSave() {
        ArrayList<IntakeSave> is = new ArrayList<>();
        for (Intake it: log) {
            is.add(it.toSave());
        }

        return new MedLogSave(med.toSave(), is);
    }

    public JSONObject toJSONSave() throws JSONException {
        JSONObject jsave = new JSONObject();
        jsave.put("med", med.toJSONSave());

        JSONArray jitk = new JSONArray();
        for(Intake itk: log) {
            jitk.put(itk.toJSONSave());
        }

        jsave.put("intakes", jitk);

        return jsave;
    }

    public Medication getMed() {
        return med;
    }

    public void setMed(Medication med) {
        this.med = med;
    }

    public ArrayList<Intake> getLog() {
        return log;
    }

    public void setLog(ArrayList<Intake> log) {
        this.log = log;
    }

    public void takeMeds(Intake itk) {
        log.add(itk);
    }

    public double score_today() {
        return score_for_day(LocalDate.now());
    }

    public double score_for_day(LocalDate ld) {
        double out = 0;
        for(Intake it: log) {
            if(ld.isEqual(it.getTimestamp().toLocalDate())) {
                out += it.getDose().getMultiplier();
            }
        }

        return out;
    }

    public double score_daily() {
        if(log.isEmpty()) return 0;
        else {
            LocalDateTime earliest = log.get(0).getTimestamp();
            for(int i=0; i<log.size(); i++) {
                if(log.get(i).getTimestamp().isBefore(earliest)) earliest = log.get(i).getTimestamp();
            }
            double out = 0;
            for(Intake it: log) {
                out += it.getDose().getMultiplier();
            }

            return out/(double)Util.start_of_day(earliest).until(Util.start_of_day(LocalDateTime.now()), ChronoUnit.DAYS);
        }

    }

    private MedLog log_since(LocalDate ld) {
        ArrayList<Intake> intak = new ArrayList<>();

        if(log.isEmpty()) return new MedLog(getMed());
        else {
            int pos = log.size()-1;
            LocalDate tmpday = LocalDate.now();

            while(pos >= 0 && log.get(pos).getTimestamp().toLocalDate().isAfter(ld)) {
                ArrayList<Intake> dly_intak = new ArrayList<>();
                while (pos >= 0 && log.get(pos).getTimestamp().toLocalDate().isEqual(tmpday)) {

                    dly_intak.add(log.get(pos));
                    pos--;

                }
                intak.add(Intake.combine(dly_intak, LocalDateTime.now().with(tmpday)));

                tmpday = tmpday.minusDays(1);
            }

            MedLog out = new MedLog(getMed());
            out.setLog(intak);
            return out;
        }
    }

    public MedLog log_for_stattimeframe(StatTimeframe stf) {
        switch (stf) {
            case YEARLY:
                return mk_yearlog();
            case MONTHLY:
                return mk_monthlog();
            case WEEKLY:
            default:
                return mk_weeklog();
        }
    }

    public MedLog mk_weeklog() {
        return log_since(LocalDate.now().minusWeeks(1));
    }

    public MedLog mk_monthlog() {
        return log_since(LocalDate.now().minusMonths(1));
    }

    public MedLog mk_yearlog() {
        return log_since(LocalDate.now().minusYears(1));
    }

    @Override
    public int get_nr_dsets() {
        return log.size();
    }

    @Override
    public double get_val(int pos) {
        return log.get(pos).getDose().getMultiplier();
    }

    @Override
    public String get_label(int pos) {
        return String.format("%s",
                log.get(pos).getTimestamp().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
    }

    @Override
    public String get_alt_label(int pos) {
        return String.format("(%s)",
                Util.DateTimeToShortString(log.get(pos).getTimestamp()));
    }

    @Override
    public char get_min_label(int pos) {
        return String.format("%s",
                log.get(pos).getTimestamp().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())).charAt(0);
    }

    @Override
    public double get_maxval() {
        double max = 0;
        for(Intake itk: log) {
            if(itk.getDose().getMultiplier() > max) max = itk.getDose().getMultiplier();
        }

        return max;
    }

    @Override
    public ArrayList<Integer> get_highlight_idx() {
        ArrayList<Integer> out = new ArrayList<>();

        for(int i=0; i<log.size(); i++) {
            if(log.get(i).getTimestamp().toLocalDate().until(LocalDate.now(), ChronoUnit.DAYS) % 7 == 0) {
                out.add(i);
            }
        }

        return out;
    }
}
