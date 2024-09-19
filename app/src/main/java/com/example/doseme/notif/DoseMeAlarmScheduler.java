package com.example.doseme.notif;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.doseme.datadapt.MedAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class DoseMeAlarmScheduler implements AlarmScheduler{
    private Context ctx;
    private AlarmManager alarmMan;


    public DoseMeAlarmScheduler(Context c) {
        this.ctx=c;
        alarmMan = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
    }


    @Override
    public void schedule(AlarmItem item) {
        Intent intent = new Intent(ctx, AlarmReceiver.class).putExtra("XTRA_MSG", item.msg).putExtra("XTRA_CHNL", item.chnl).putExtra("XTRA_MEDID", item.medid);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if(alarmMan.canScheduleExactAlarms()) {
                set_alarm(item, intent);
            } else {
                Log.e("ALARMSCHEDULE", "Not allowed to set exact notifs");
            }
        } else set_alarm(item, intent);


    }

    private void set_alarm(AlarmItem item, Intent intent) {
        alarmMan.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                item.timestamp.atZone(ZoneId.systemDefault()).toEpochSecond()*1000,
                PendingIntent.getBroadcast(ctx, item.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE)
        );
        Log.d("ALARMSCHEDULE", "Alarm set at "+ LocalDateTime.now().toString());
    }

    @Override
    public void cancel(AlarmItem item) {
        alarmMan.cancel(PendingIntent.getBroadcast(ctx, item.hashCode(), new Intent(), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        Log.d("ALARMCANCEL", "Alarm cancelled at "+ LocalDateTime.now().toString());
    }
}
