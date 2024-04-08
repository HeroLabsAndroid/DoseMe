package com.example.doseme.dialog;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doseme.R;
import com.example.doseme.StatTimeframe;
import com.example.doseme.Util;
import com.example.doseme.datadapt.DoseAdapter;
import com.example.doseme.datadapt.IntakeAdapter;
import com.example.doseme.medic.Dose;
import com.example.doseme.medic.Intake;
import com.example.doseme.medic.MedLog;
import com.example.doseme.medic.Medication;
import com.example.doseme.statview.StatView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

public class IntakeStatsDialog extends DialogFragment implements IntakeAdapter.IntakeRemoveListener, DatePickerDialog.OnDateSetListener {


    TextView tvDaily, tvToday, tvTit, tvStatType;

    RecyclerView rclvwIntakes;

    StatView svLastWeek;
    StatTimeframe statframe = StatTimeframe.WEEKLY;

    LocalDate date_to_show;

    MedLog ml;
    MedLog today;
    boolean show_today;     //intakes are only removable if this is true

    private void switch_statmode() {
        statframe = statframe.cycle();

        switch (statframe) {
            case YEARLY:
                tvStatType.setText("Last Year");
                break;
            case MONTHLY:
                tvStatType.setText("Last Month");
                break;
            case WEEKLY:
                tvStatType.setText("Last Week");
                break;
            default:
                tvStatType.setText("ERROR");
                break;
        }

        MedLog tmp = ml.log_for_stattimeframe(statframe);

        Collections.reverse(tmp.getLog());
        svLastWeek.attachViewable(tmp, statframe==StatTimeframe.WEEKLY);
        svLastWeek.setShow_alt_label(statframe==StatTimeframe.WEEKLY);
        svLastWeek.setShow_min_label(statframe==StatTimeframe.MONTHLY);
    }

    private MedLog todays_log() {
        MedLog td = new MedLog(ml.getMed());
        int pos = ml.getLog().size()-1;

        while(pos >= 0 && ml.getLog().get(pos).getTimestamp().getDayOfYear() == LocalDateTime.now().getDayOfYear() &&
                ml.getLog().get(pos).getTimestamp().getYear() == LocalDateTime.now().getYear()) {
            td.getLog().add(ml.getLog().get(pos));
            pos--;
        }

        return td;
    }

    private MedLog log_for_date(LocalDate ld) {
        MedLog datelog= new MedLog(ml.getMed());
        int pos = ml.getLog().size()-1;

        while(pos >= 0 &&
                (ml.getLog().get(pos).getTimestamp().getDayOfYear() != ld.getDayOfYear() ||       //find pos
                ml.getLog().get(pos).getTimestamp().getYear() != ld.getYear())) {
            pos--;
        }

        while(pos >= 0 && ml.getLog().get(pos).getTimestamp().getDayOfYear() == ld.getDayOfYear() &&       //add elements
                ml.getLog().get(pos).getTimestamp().getYear() == ld.getYear()) {
            datelog.getLog().add(ml.getLog().get(pos));
            pos--;
        }

        return datelog;
    }

    public IntakeStatsDialog(MedLog ml) {
        this.ml = ml;
        today = todays_log();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dlg_intakestats, null);

        tvTit = layout.findViewById(R.id.TV_intakestats_name);
        tvDaily = layout.findViewById(R.id.TV_intakestats_daily);
        tvToday = layout.findViewById(R.id.TV_intakestats_today);
        rclvwIntakes = layout.findViewById(R.id.RCLVW_intakestats_intakes);
        svLastWeek = layout.findViewById(R.id.STATVW_intakestats_lastweek);
        tvStatType = layout.findViewById(R.id.TV_intakestats_stattype);


        MedLog tmp = ml.mk_weeklog();
        Collections.reverse(tmp.getLog());
        svLastWeek.attachViewable(tmp, true);
        svLastWeek.setShow_alt_label(true);

        tvStatType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_statmode();
            }
        });


        tvTit.setText(ml.getMed().getName());

        tvToday.setText(String.format(Locale.getDefault(), "Today: %.2f", ml.score_today()));
        tvDaily.setText(String.format(Locale.getDefault(), "Daily avg.: %.2f", ml.score_daily()));

        tvToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragMan = requireActivity().getSupportFragmentManager();
                DatePickerFragment dpFrag = new DatePickerFragment(IntakeStatsDialog.this);
                dpFrag.show(fragMan, "datepick");
            }
        });

        rclvwIntakes.setLayoutManager(new LinearLayoutManager(requireActivity()));
        IntakeAdapter inAdapt = new IntakeAdapter(today, true, IntakeStatsDialog.this);
        rclvwIntakes.setAdapter(inAdapt);

        builder.setView(layout);


        // Create the AlertDialog object and return it.
        return builder.create();
    }


    @Override
    public void onIntakeRemoved(Intake itk) {
        int idx = -1;
        for(int i=0; i<ml.getLog().size(); i++) {
            if(ml.getLog().get(i).getTimestamp().equals(itk.getTimestamp())) {
                ml.getLog().remove(i);
                idx = i;
                break;
            }
        }
        tvToday.setText(String.format(Locale.getDefault(), "Today: %.2f", ml.score_today()));
        tvDaily.setText(String.format(Locale.getDefault(), "Daily avg.: %.2f", ml.score_daily()));
        today = todays_log();
        if(idx>=0) rclvwIntakes.getAdapter().notifyItemRemoved(idx);

        MedLog tmp = ml.mk_weeklog();
        Collections.reverse(tmp.getLog());
        svLastWeek.attachViewable(tmp, true);
        svLastWeek.setShow_alt_label(true);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d("DEBUG", "entering onDateSet");
        date_to_show = LocalDate.of(year, month+1, dayOfMonth);

        today = log_for_date(date_to_show);
        IntakeAdapter inAdapt = new IntakeAdapter(today, date_to_show.isEqual(LocalDate.now()), IntakeStatsDialog.this);
        rclvwIntakes.setAdapter(inAdapt);

        if(!date_to_show.isEqual(LocalDate.now())) {
            tvToday.setText(String.format(Locale.getDefault(), "%s: %.2f", Util.DateToString(date_to_show), today.score_for_day(date_to_show)));
        } else tvToday.setText(String.format(Locale.getDefault(), "Today: %.2f", ml.score_today()));
    }
}
