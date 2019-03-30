package fr.visufo.titouan.jardin;

import android.content.Context;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class Plant {

    private String TAG_WRITE_READ_FILE = "TAG_WRITE_READ_FILE";
    public final String[] plantAttributs;
    String plantName;
    String degree;

    boolean isMoveable;



    public Plant(Context context, String plantName, String plantDegree, boolean isMoveable) {

        String isMoveableStr = Boolean.toString(isMoveable);
        writeToFile(plantName+";"+plantDegree+";"+isMoveableStr, plantName, context);
        String string = readFromFile(context, plantName);
        plantAttributs = string.split(";");
        this.plantName = plantAttributs[0];
        degree = plantAttributs[1];
        this.isMoveable = Boolean.parseBoolean(plantAttributs[2]);

    }

    private void writeToFile(String data, String plantName, Context context) {
        File file = new File(context.getFilesDir(), plantName);
        try {
            //Créer fichier txt avec attributs de la plante
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(plantName+".txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readFromFile(Context context, String plantName) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(plantName+".txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public String getName(){
        return plantName;
    }
    public String getDegree(){
        return degree;
    }
    public boolean isMoveable(){
        return isMoveable;
    }
}
