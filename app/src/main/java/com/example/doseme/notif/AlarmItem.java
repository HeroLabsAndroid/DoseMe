package com.example.doseme.notif;

import java.time.LocalDateTime;

public class AlarmItem {
    LocalDateTime timestamp;
    String msg;
    String chnl;
    int medid;

    public AlarmItem(LocalDateTime timestamp, String msg, String chnl, int medid) {
        this.timestamp = timestamp;
        this.msg = msg;
        this.chnl = chnl;
        this.medid=medid;
    }
}
