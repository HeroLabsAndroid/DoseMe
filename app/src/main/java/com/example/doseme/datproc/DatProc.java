package com.example.doseme.datproc;

import android.content.Context;
import android.util.Log;

import com.example.doseme.medic.MedLog;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class DatProc {
    static public ArrayList<MedLog> loadData(Context con) {
        ArrayList<MedLog> dat = new ArrayList<>();

        try {
            FileInputStream fis = con.openFileInput("medlog.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Log.d("RDDAT", String.format(Locale.getDefault(), "Opened file (%s) and Object Stream.", fis.toString()));
            int cnt = (int)ois.readObject();
            for(int i=0; i< cnt; i++) dat.add(new MedLog((MedLogSave) ois.readObject()));
            Log.d("RDDAT", String.format(Locale.getDefault(),"Read %d objects.",dat.size()));
            ois.close();
            fis.close();
        } catch(Exception e) {
            Log.e("RDDAT", e.toString());
        }

        return dat;
    }

    static public void saveData(Context con, ArrayList<MedLog> logs) {
        try {
            FileOutputStream fos = con.openFileOutput("medlog.dat", Context.MODE_PRIVATE);
            Log.d("SAVDAT", "Opened file.");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            Log.d("SAVDAT", "Opened Object Stream");
            oos.writeObject(logs.size());
            for(MedLog ml: logs) {
                oos.writeObject(ml.toSave());
            }
            Log.d("SAVDAT", String.format(Locale.getDefault(), "Wrote %d Objects.", logs.size()));
            oos.close();
            fos.close();
        } catch (Exception e) {
            Log.e("SAVDAT", e.toString());
        }

    }
}
