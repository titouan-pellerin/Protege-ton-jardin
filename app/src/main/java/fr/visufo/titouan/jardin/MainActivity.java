package fr.visufo.titouan.jardin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;


import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.visuality.f32.temperature.Temperature;
import com.visuality.f32.temperature.TemperatureUnit;
import com.visuality.f32.weather.data.entity.Forecast;
import com.visuality.f32.weather.manager.WeatherManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    //Views
    SpeedDialView mSpeedDialView;

        //AddPlantDialog
        EditText plantEdit;
        EditText degreeEdit;
        Button addImageButton;
        Button addPlantDoneButton;
        Switch aSwitch;

        //SettingsDialog
        EditText cityNameEdit;
        Button settingsDoneButton;

    //Variables
    Bitmap selectedImage;
    String plantName;
    String degree;
    String cityName;

    static final int RESULT_LOAD_IMG = 1;

    //Actions au lancement de l'application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Attribuer le fichier activity_main comme Layout de notre activité principale
        setContentView(R.layout.activity_main);

        //Créer le bouton flotant (Floating Action Button)
        addFab();

        //Charge les plantes enregistrées au démarrage de l'application
        loadPlants();








    }

/*********************
 * FONCTIONS
 * *******************/




    //Fonction permettant de créer le bouton flottant
    public void addFab() {

        //On récupère l'id du bouton flottant dans le fichier activity_main
        mSpeedDialView = findViewById(R.id.speedDial);

        //Ajout du premier sous-bouton "Ajouter une plante" en lui donnant une id, une icone, une couleur pour le fond de l'icone ainsi qu'un label
        mSpeedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_add_plant, R.drawable.ic_plants)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme()))
                .setLabel("Ajouter une plante")
                .create());

        //Ajout du deuxième sous-bouton "Paramètres" en lui donnant une id, une icone, une couleur pour le fond de l'icone ainsi qu'un label
        mSpeedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_settings, R.drawable.ic_settings)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme()))
                .setLabel("Paramètres")
                .create());

        //Lecture des actions appliquées au bouton flottant
        mSpeedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {

                //On regarde si l'id de chaque sous-bouton est égale au premier bouton ou au deuxième
                switch (actionItem.getId()) {

                    //Dans le cas ou il s'agit du premier bouton
                    case R.id.fab_add_plant:

                        //On créé une fenêtre de dialogue de type "AddPlant"
                        final AddPlantDialogClass addPlantDialog = new AddPlantDialogClass(MainActivity.this);
                        addPlantDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        //On affiche cette fenêtre de dialogue
                        addPlantDialog.show();

                        //On récupère l'id du bouton "Valider" de la fenêtre de dialogue que l'on vient de créer
                        addPlantDoneButton = (Button) addPlantDialog.findViewById(R.id.done_button_addPlant);

                        //On récupère l'id du bouton "Ajouter une image" de la fenêtre de dialogue que l'on vient de créer
                        addImageButton = (Button) addPlantDialog.findViewById(R.id.addImage);

                        //Lecture des actions appliquées au bouton "Ajouter une image"
                        addImageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //Création de la requête Android permettant la sélection d'une image
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");

                                //Lancement de la requête à l'aide d'une fonction définie à la fin
                                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);

                            }
                        });


                        //Lecture des actions appliquées au bouton "Valider"
                        addPlantDoneButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                //On récupère l'id du champ de texte correspondant au nom de la plante
                                plantEdit = (EditText) addPlantDialog.findViewById(R.id.plant_name);

                                //On récupère l'id du champ de texte correspondant au degré de la plante
                                degreeEdit = (EditText) addPlantDialog.findViewById(R.id.degree_nbr);

                                //On récupère l'id de "l'interrupteur" indiquant si la plante est déplaçable ou non
                                aSwitch = (Switch) addPlantDialog.findViewById(R.id.moveable_plant);

                                //Définition d'un boolean récupérant l'état de l'interrupteur
                                Boolean switchState = aSwitch.isChecked();

                                //On récupère le texte entré dans le premier champ dans une variable de type String
                                plantName = plantEdit.getText().toString().trim();

                                //On récupère le texte entré dans le second champ dans une variable de type String
                                degree = degreeEdit.getText().toString().trim();

                                //Sécurités vérifiant si les champs sont bien remplis

                                //Si les variables plantName et degree sont vides, donc si les deux champs ne sont pas remplis, on en informe l'utilisateur
                                if(plantName.isEmpty() && degree.isEmpty()) {
                                    showToast("Les champs ne sont pas remplis");
                                //Sinon si seulement la variable degree est vide, donc si le 2e champs n'est pas rempli, on en informe l'utilisateur
                                }else if (degree.isEmpty()) {
                                    showToast("Indiquer un degré de gel");
                                //Sinon si seulement la variable plantName est vide, donc si le 1er champs n'est pas rempli, on en informe l'utilisateur
                                }else if (plantName.isEmpty()) {
                                    showToast("Indiquer un nom de plante");
                                //Sinon si il n'y a pas d'image de sélectionnée, on en informe l'utilisateur
                                }else if(selectedImage==null){
                                    showToast("Vous n'avez pas ajouté de photo");

                                //Et finalement si toutes les conditions sont remplies, on ajoute les plantes à la vue principale.
                                }else{
                                    //Si la plante est déplaçable:
                                    if(switchState) {
                                        addPlantView(getApplicationContext(), plantName, degree,true);
                                        //On ferme la fenêtre de dialogue
                                        addPlantDialog.dismiss();
                                    //Sinon, donc si elle ne l'est pas
                                    }else{
                                        addPlantView(getApplicationContext(), plantName, degree,false);
                                        addPlantDialog.dismiss();
                                    }
                                }

                            }
                        });

                        mSpeedDialView.close(); //Fermeture du bouton flottant
                        return true;

                    //Dans le cas ou il s'agit du deuxième bouton
                    case R.id.fab_settings:
                        //On créé une fenêtre de dialogue de type "Settings"
                        final SettingsDialogClass settingDialog = new SettingsDialogClass(MainActivity.this);
                        settingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        settingDialog.show();
                        cityNameEdit = (EditText) settingDialog.findViewById(R.id.cityName);
                        settingsDoneButton = (Button) settingDialog.findViewById(R.id.done_button_settings);

                        settingsDoneButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cityName = cityNameEdit.getText().toString().trim();
                                Weather.setWeatherCity(cityName, getApplicationContext());
                                settingDialog.dismiss();
                            }
                        });

                        //Ajouter la lecture des actions du bouton "Valider" de cette fenêtre

                        mSpeedDialView.close(); //Fermeture du bouton flottant
                        return true;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    //Fonction ajoutant une plante à la vue d'utilisateur en fonction des différents paramètres
    public void addPlantView(Context context, String name, String degree, boolean isMovable) {

        //On enregistre l'image sélectionnée plus tôt sur le stockage interne du téléphone au nom de la plante à l'aide d'une fonction
        saveToInternalStorage(selectedImage, name);

        //On défini la plante sélectionnée à "null" pour éviter que si l'utilisateur ajoute plusieurs plantes d'affilée, elles aient la même photo
        selectedImage = null;

        //Création d'une variable de type "Plant" avec les différents paramètres
        Plant plant = new Plant(context, name, degree, isMovable);

        //On récupère l'id de la vue principale, dans laquelle on va afficher les plantes
        LinearLayout contentMain = (LinearLayout) findViewById(R.id.mainLinearLayout);

        //Création d'une nouvelle vue de type "PlantView"
        PlantView plantView = new PlantView(context, null);
        plantView.setName(name);
        plantView.setDegree(degree);
        plantView.setInfo("Info info info info");

        //On récupère l'image au nom de la plante depuis le stockage interne et on l'ajoute à la "PlantView" définie juste au-dessus
        showImageFromStorage(plantView, name);

        //Et finalement on ajoute la "PlantView" que l'on vient de définir à la vue principale
        contentMain.addView(plantView);
    }

    //Fonction utilisée pour charger les plantes au démarrage de l'application
    public void loadPlants() {

        File[] files = listTxt();
        //Si le tableau n'est pas vide
        if (!(files == null)) {
            //Boucle pour afficher chaque plante sur la vue principale, en fonction du nombre de fichiers, donc du nombre de plantes enregistrées
            for (int i = 0; i < files.length; i++) {

                //à chaque tour de boucle, on récupère le nom du fichier dans une variable
                String fileName = files[i].getName();
                //on récupère le contenu du fichier possédant le nom défini juste au-dessus à l'aide d'une fonction
                String fileContent = readFromFile(getApplicationContext(), fileName);
                //création d'un tableau de String pour y ajouter le contenu du fichier txt
                String[] plantAttributs;
                //On ajoute le contenu du fichier .txt séparé en trois, grâce à la séparation du ";"
                plantAttributs = fileContent.split(";");

                //On récupère donc le nom de la plante correspondant à la première séparation
                String plantName = plantAttributs[0];
                //Puis le dégré, correspondant  à la deuxième séparation
                String degree = plantAttributs[1];
                //Et si la plante est déplaçable, correspondant à la troisième séparation
                boolean isMovable = Boolean.valueOf(plantAttributs[2]);

                //Création d'une nouvelle plante, en créant une variable de type "Plant"
                Plant plant = new Plant(getApplicationContext(),plantName , degree,isMovable);

                //On récupère l'id de la vue principale
                LinearLayout contentMain = (LinearLayout) findViewById(R.id.mainLinearLayout);

                //Création d'une nouvelle vue de type "PlantView"
                PlantView plantView = new PlantView(getApplicationContext(), null);

                plantView.setName(plantName);
                plantView.setDegree(degree);
                plantView.setInfo("Info info info info");

                Log.v("Plantes:", plantName+": "+ degree +"°C " + "Déplaçable : "+isMovable);

                //On ajoute l'image de la photo en question à la vue
                showImageFromStorage(plantView, plantName);

                //On ajoute la "PlantView" à la vue principale
                contentMain.addView(plantView);

            }
        }
    }

    //Fonction permettant de retourner le contenu d'un fichier
    private String readFromFile(Context context, String fileName) {

        //Défnition d'une variable qui retournera le contenu du fichier
        String ret = "";

        //On récupère le contenu du fichier ligne par ligne dans la variable définie plus haut
        try {
            InputStream inputStream = context.openFileInput(fileName);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        //si jamais le fichier n'existe pas
        } catch (FileNotFoundException e) {
            Log.e("File Error", "Fichier non trouvé: " + e.toString());
        //si jamais il y a une autre erreur
        } catch (IOException e) {
            Log.e("F", "Impossible de lire le fichier: " + e.toString());
        }
        //On retourne la variable définie au début, donc le contenu du fichier
        return ret;
    }

    //Fonction propre à Android permettant de récupérer le résultat d'une requête, ici notre requête de sélection d'une image
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        //Si l'utilisateur a bien sélectionné une image
        if (resultCode == RESULT_OK) {
            try {
                //On récupère l'image sélectionné dans une variable
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                //On change le fond du bouton par l'image sélectionnée
                Drawable plantImg = new BitmapDrawable(getResources(), selectedImage);
                addImageButton.setBackgroundDrawable(plantImg);
                //en cas d'erreur...
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Une erreur s'est produite", Toast.LENGTH_LONG).show();
            }
        //en cas de non sélection d'image
        } else {
            Toast.makeText(getApplicationContext(), "Vous n'avez pas choisi d'image", Toast.LENGTH_LONG).show();
        }
    }

    //Fonction permettant de sauvegarder une image au nom d'une plante
    private void saveToInternalStorage(Bitmap bitmapImage, String plantName) {
        //On récupère le dossier de fichier
        File directory = getApplicationContext().getFilesDir();
        //On créer une variable de type File avec comme chemin le "Fichiers de l'application/nomDeLaPlante.jpg"
        File mypath = new File(directory, plantName + ".jpg");
        //Enregistrement de l'image
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            //On recadre l'image à l'aide d'une fonction, avant de l'enregistrer pour pas prendre trop de place sur le téléphone et pour réduire le temps de chargement
            bitmapImage = getResizedBitmap(bitmapImage,100);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Toast.makeText(getApplicationContext(), "Fichier enregistré " + plantName, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //Fonction permettant d'afficher une image au nom d'une plante depuis le stockage
    private void showImageFromStorage(PlantView plantView, String plantName) {
        try {
            String path = getApplicationContext().getFilesDir().toString();

            //On récupère l'image s'appelant "nomPlante.jpg"
            File f = new File(path, plantName + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            //On ajoute l'image à la PlantView
            plantView.setImage(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Réduire la taille d'une image, tout en gardant ses proportions
    public Bitmap getResizedBitmap(Bitmap bitmap, int width) {
        float aspectRatio = bitmap.getWidth() /
                (float) bitmap.getHeight();
        int height = Math.round(width / aspectRatio);
    return Bitmap.createScaledBitmap(bitmap,width, height, false);
    }



    public File[] listTxt(){

        File[] files;
        //On récupère le chemin pour accéder aux fichiers de l'application
        String path = getApplicationContext().getFilesDir().toString();

        //On récupère le dossier de fichiers de l'application dans une variable
        File directory = new File(path);

        //Création d'un tableau de fichiers de type .txt
        files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.getPath().endsWith(".txt"));
            }
        });
        return files;
    }

    public void showToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }
}