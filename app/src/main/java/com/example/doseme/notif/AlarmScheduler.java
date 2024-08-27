package com.example.doseme.notif;

public interface AlarmScheduler {
    void schedule(AlarmItem item);

    void cancel(AlarmItem item);
}
