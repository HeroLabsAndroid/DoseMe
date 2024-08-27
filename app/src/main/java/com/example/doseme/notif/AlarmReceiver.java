package com.example.doseme.notif;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.doseme.R;

import java.time.LocalDateTime;

public class AlarmReceiver extends BroadcastReceiver {


    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    void sendNotification(Context c, String msg, String channel) {
        NotificationCompat.Builder notbuild = new NotificationCompat.Builder(c, channel);
        notbuild.setContentTitle("ALARM!");
        notbuild.setContentText(msg);
        notbuild.setSmallIcon(R.drawable.ic_launcher_foreground);
        notbuild.setAutoCancel(true);
        NotificationManagerCompat notManComp = NotificationManagerCompat.from(c);

        notManComp.notify(msg.hashCode(), notbuild.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getStringExtra("XTRA_MSG");
        String chnl = intent.getStringExtra("XTRA_CHNL");

        int id = intent.getIntExtra("XTRA_MEDID", 0);
        if(msg!=null && chnl!=null) sendNotification(context, msg, chnl);


        Log.d("ALARMRECEIVE", "Alarm fired at "+ LocalDateTime.now().toString());
    }
}
