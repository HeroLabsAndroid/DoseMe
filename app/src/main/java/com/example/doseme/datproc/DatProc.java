package com.example.doseme.datproc;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.doseme.medic.MedLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class DatProc {
    static public ArrayList<MedLog> loadData(Context con) {
        ArrayList<MedLog> dat = new ArrayList<>();

        try {
           /* FileInputStream fis = con.openFileInput("medlog.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Log.d("RDDAT", String.format(Locale.getDefault(), "Opened file (%s) and Object Stream.", fis.toString()));
            int cnt = (int)ois.readObject();
            for(int i=0; i< cnt; i++) dat.add(new MedLog((MedLogSave) ois.readObject()));
            Log.d("RDDAT", String.format(Locale.getDefault(),"Read %d objects.",dat.size()));
            ois.close();
            fis.close();*/
            dat = getDataFromJSON(con);
        } catch(Exception e) {
            Log.e("RDDAT", e.toString());
        }

        return dat;
    }

    static public void saveData(Context con, ArrayList<MedLog> logs) {
        try {
            /*FileOutputStream fos = con.openFileOutput("medlog.dat", Context.MODE_PRIVATE);
            Log.d("SAVDAT", "Opened file.");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            Log.d("SAVDAT", "Opened Object Stream");
            oos.writeObject(logs.size());
            for(MedLog ml: logs) {
                oos.writeObject(ml.toSave());
            }
            Log.d("SAVDAT", String.format(Locale.getDefault(), "Wrote %d Objects.", logs.size()));
            oos.close();
            fos.close();*/
            saveDataToJSON(con, logs);
        } catch (Exception e) {
            Log.e("SAVDAT", e.toString());
        }

    }

    static public String saveDataToJSON(Context con, ArrayList<MedLog> logs) throws JSONException, IOException {
        JSONObject jsave = new JSONObject();

        JSONArray jmlsave = new JSONArray();
        for(MedLog ml: logs) {
            jmlsave.put(ml.toJSONSave());
        }

        jsave.put("medlogs", jmlsave);

        FileOutputStream fos = con.openFileOutput("jmedlog.dat", Context.MODE_PRIVATE);

        FileWriter fw;
        try {
            fw = new FileWriter(fos.getFD());
            fw.write(jsave.toString());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.getFD().sync();
            fos.close();
        }

        return jsave.toString();
    }

    static public ArrayList<MedLog> getDataFromJSON(Context con) throws IOException, JSONException {
        FileInputStream fis = con.openFileInput("jmedlog.dat");
        FileReader fileReader = new FileReader(fis.getFD());
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();

        String line = bufferedReader.readLine();
        while (line != null){
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();// This response will have Json Format String

        String response = stringBuilder.toString();

        Log.d("JSON DATA (LOADED)", response);

        JSONObject jsave = new JSONObject(response);
        
        ArrayList<MedLog> out = new ArrayList<>();
        
        JSONArray jarr = jsave.getJSONArray("medlogs");
        for(int i=0; i<jarr.length(); i++) {
            out.add(new MedLog(jarr.getJSONObject(i)));
        }
        
        return out;
    }

    @NonNull
    private static String getResponse(File file) throws IOException {
       return "";
    }
}
