package com.example.doseme.datproc;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class LDTsave {
    int h, min, s, d, mon, y;

    public LDTsave(JSONObject jsave) {
        h = jsave.optInt("hour", 0);
        min = jsave.optInt("minute", 0);
        s = jsave.optInt("second", 0);
        d = jsave.optInt("day", 1);
        mon = jsave.optInt("month", 1);
        y = jsave.optInt("year", 0);
    }

    public LDTsave(LocalDateTime ldt) {
        h = ldt.getHour();
        min = ldt.getMinute();
        s = ldt.getSecond();

        d = ldt.getDayOfMonth();
        mon = ldt.getMonth().getValue();
        y = ldt.getYear();
    }

    public JSONObject toJSONSave() throws JSONException {
        JSONObject jsave = new JSONObject();
        jsave.put("hour", h);
        jsave.put("minute", min);
        jsave.put("second", s);
        jsave.put("day", d);
        jsave.put("month", mon);
        jsave.put("year", y);

        return jsave;
    }

    public LocalDateTime toLDT() {
        LocalDateTime ldt = LocalDateTime.of(y, mon, d, h, min, s);

        return ldt;
    }
}
