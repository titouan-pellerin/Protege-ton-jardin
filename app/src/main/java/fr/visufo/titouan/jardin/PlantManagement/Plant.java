package fr.visufo.titouan.jardin.PlantManagement;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Classe permettant de créer un nouveau type d'objet, "Plant"
 */


public class Plant {

    //Constructeur de la classe
    public Plant(Context context, String plantName, String plantDegree, boolean isMovable) {
        String isMovableStr = Boolean.toString(isMovable);
        //Création d'un nouveau fichier .txt avec écrit à l'intérieur "nomPlante;degré;déplaçable ou non"
        writeToFile(plantName + ";" + plantDegree + ";" + isMovableStr, plantName, context);
    }
    private void writeToFile(String data, String plantName, Context context) {
        File file = new File(context.getFilesDir(), plantName);
        if (!file.exists()) {
            try {
                //Créer fichier txt avec attributs de la plante
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(plantName + ".txt", Context.MODE_PRIVATE));
                outputStreamWriter.write(data);
                outputStreamWriter.close();
            } catch (IOException e) {
                Log.e("Exception", "Erreur d'écriture: " + e.toString());
            }
        }
    }
}
