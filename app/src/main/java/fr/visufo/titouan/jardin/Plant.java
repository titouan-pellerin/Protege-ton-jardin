package fr.visufo.titouan.jardin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Plant {

    private String TAG_WRITE_READ_FILE = "TAG_WRITE_READ_FILE";
    public final String[] plantAttributs;



    public Plant(Context context, String plantName, String plantDegree) {
        writeToFile(plantName+";"+plantDegree, plantName, context);
        String string = readFromFile(context, plantName);
        plantAttributs = string.split(";");

    }

    private void writeToFile(String data, String plantName, Context context) {
        File file = new File(context.getFilesDir(), plantName);
        try {
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

}
