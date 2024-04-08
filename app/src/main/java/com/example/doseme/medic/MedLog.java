package com.example.doseme.medic;

import com.example.doseme.Util;
import com.example.doseme.datproc.DoseSave;
import com.example.doseme.datproc.IntakeSave;
import com.example.doseme.datproc.MedLogSave;
import com.example.doseme.statview.StatViewable;

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

    public MedLogSave toSave() {
        ArrayList<IntakeSave> is = new ArrayList<>();
        for (Intake it: log) {
            is.add(it.toSave());
        }

        return new MedLogSave(med.toSave(), is);
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
        double out = 0;
        for(Intake it: log) {
            if(Util.same_day(LocalDateTime.now(), it.getTimestamp())) {
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

    public MedLog mk_weeklog() {
        ArrayList<Intake> wkly_intak = new ArrayList<>();

        if(log.isEmpty()) return new MedLog(getMed());
        else {
            int pos = log.size()-1;
            int doy = LocalDateTime.now().getDayOfYear();
            int daycnt = 0;

            while(pos >= 0 && log.get(pos).getTimestamp().isAfter(Util.end_of_day(LocalDateTime.now().minusWeeks(1)))) {
                ArrayList<Intake> dly_intak = new ArrayList<>();
                while (pos >= 0 && log.get(pos).getTimestamp().getDayOfYear() == doy) {

                    dly_intak.add(log.get(pos));
                    pos--;

                }
                wkly_intak.add(Intake.combine(dly_intak, LocalDateTime.now().withDayOfYear(doy)));

                doy = (doy==0) ? 365 : doy-1 ;
            }

            MedLog out = new MedLog(getMed());
            out.setLog(wkly_intak);
            return out;
        }
    }

    public MedLog mk_monthlog() {
        ArrayList<Intake> mnthl_intak = new ArrayList<>();

        if(log.isEmpty()) return new MedLog(getMed());
        else {
            int pos = log.size()-1;
            int doy = LocalDateTime.now().getDayOfYear();
            int year = LocalDateTime.now().getYear();

            while(pos >= 0 && log.get(pos).getTimestamp().isAfter(Util.end_of_day(LocalDateTime.now().minusMonths(1)))) {
                ArrayList<Intake> dly_intak = new ArrayList<>();
                while (pos >= 0 && log.get(pos).getTimestamp().getDayOfYear() == doy && log.get(pos).getTimestamp().getYear() == year) {

                    dly_intak.add(log.get(pos));
                    pos--;

                }
                mnthl_intak.add(Intake.combine(dly_intak, LocalDateTime.now().withDayOfYear(doy)));

                if(doy == 0) {
                    year--;
                    doy = (Util.leap_year(LocalDateTime.now())) ? 366 : 365;
                } else doy--;
            }

            MedLog out = new MedLog(getMed());
            out.setLog(mnthl_intak);
            return out;
        }
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
}
