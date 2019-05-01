package fr.visufo.titouan.jardin;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    //Views
    SpeedDialView mSpeedDialView;

    //AddPlantDialog
    EditText plantEdit;
    EditText degreeEdit;
    Button addImageButton;
    Button addPlantDoneButton;
    Switch aSwitch;


    LinearLayout plantsView;
    Button firstPlantButton;
    TextView tempText;
    LinearLayout mainLinearLayout;

    //Variables
    Bitmap selectedImage;
    String plantName;
    String degree;

    static final int RESULT_LOAD_IMG = 1;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    static double temp = 0.0;

    //Actions au lancement de l'application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Attribuer le fichier activity_main comme Layout de notre activité principale
        setContentView(R.layout.activity_main);
        //Créer le bouton flotant (Floating Action Button)
        addFab();
        //Charge les plantes enregistrées au démarrage de l'application
        LinearLayout linearLayout = findViewById(R.id.mainLinearLayout);

        linearLayout.invalidate();
        refreshView(linearLayout);

        String data = readFromFile(getApplicationContext(),"Localisation.latLng");
        if(!(data.isEmpty())){

            String[] latLgn;
            latLgn = data.split(";");
            if(!(data.isEmpty())) {
                Log.v("Latitude", latLgn[0]);
                Log.v("Longitude", latLgn[1]);
                double latitude = Double.parseDouble(latLgn[0]);
                double longitude = Double.parseDouble(latLgn[1]);

                WeatherClass.getTemp(latitude,longitude, new IResult() {
                    @Override
                    public void onResult(double temp) {
                        MainActivity.temp = temp;
                        loadPlants(temp);
                        showNextDayTemp(temp);
                        //showToast(temp +"");
                        Log.v("Load", temp +"");
                    }
                },getApplicationContext());
            }else{
                temp = 100000;
            }
        }else{
            MainActivity.temp = 100000;
            loadPlants(100000);
            showNextDayTemp(100000);
        }

        //YO BG BG BGB GB GBGBGB
        Log.v("T", "T");


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
                .setLabel("Localisation")
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
                        showAddPlantDialog();
                        mSpeedDialView.close(); //Fermeture du bouton flottant
                        return true;
                    //Dans le cas ou il s'agit du deuxième bouton
                    case R.id.fab_settings:
                        //On créé une fenêtre de dialogue de type "Settings"

                        if(isServicesOK()) {
                            finish();
                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(intent);

                        }
                        /*cityNameEdit = (EditText) settingDialog.findViewById(R.id.cityName);
                        settingsDoneButton = (Button) settingDialog.findViewById(R.id.done_button_settings);
                        settingsDoneButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cityName = cityNameEdit.getText().toString().trim();
                                WeatherClass.setWeatherCity(cityName, getApplicationContext());
                                settingDialog.dismiss();
                            }
                        });*/

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

        if(firstPlantButton != null) {
            plantsView.removeView(firstPlantButton);
        }
        //On enregistre l'image sélectionnée plus tôt sur le stockage interne du téléphone au nom de la plante à l'aide d'une fonction
        saveToInternalStorage(selectedImage, name);


        //On défini la plante sélectionnée à "null" pour éviter que si l'utilisateur ajoute plusieurs plantes d'affilée, elles aient la même photo
        selectedImage = null;

        //Création d'une variable de type "Plant" avec les différents paramètres
        Plant plant = new Plant(getApplicationContext(), plantName, degree, isMovable);

        //On récupère l'id de la vue principale, dans laquelle on va afficher les plantes
        LinearLayout contentMain = findViewById(R.id.mainLinearLayout);

        //String data = readFromFile(getApplicationContext(),"Localisation.latLng");
        PlantView plantView = new PlantView(context, null);

        /*String[] latLgn;
        latLgn = data.split(";");
        if(!(data.isEmpty())){
            Log.v("Latitude", latLgn[0]);
            Log.v("Longitude", latLgn[1]);
            double latitude = Double.parseDouble(latLgn[0]);
            double longitude = Double.parseDouble(latLgn[1]);
            WeatherClass.getTemp(latitude, longitude, new IResult() {
                @Override
                public void onResult(double temp) {
                    MainActivity.temp = temp;
                }
            });
            showToast(temp +"");
            Log.v("Add", temp +"");
        }else{
            temp = 100000;
        }*/
        if(isNetworkAvailable()) {
            if(temp == 100000){
                plantView.setName(plantName);
                plantView.setDegree(degree);
                plantView.setInfo("Vous n'avez pas encore indiqué de localisation");
                contentMain.invalidate();
                contentMain.requestLayout();
            }else if(temp == -1000000){
                plantView.setName(plantName);
                plantView.setDegree(degree);
                plantView.setInfo("Problème lié au chargement de la météo");
                contentMain.invalidate();
                contentMain.requestLayout();
            }else if (temp <= Double.parseDouble(degree)+2) {
                if (isMovable) {
                    plantView.setName(plantName);
                    plantView.setDegree(degree);
                    plantView.setInfo("Pensez à rentrer votre plante");
                    plantView.changeBackgroundColor("#ff7961");
                    contentMain.invalidate();
                    contentMain.requestLayout();}
                else {
                    plantView.setName(plantName);
                    plantView.setDegree(degree);
                    plantView.setInfo("Pensez à couvrir votre plante");
                    plantView.changeBackgroundColor("#ff7961");
                    contentMain.invalidate();
                    contentMain.requestLayout();}

            }else if (temp > Double.parseDouble(degree)+2) {
                plantView.setName(plantName);
                plantView.setDegree(degree);
                plantView.setInfo("Pas de problème pour cette plante");
                contentMain.invalidate();
                contentMain.requestLayout();

            }else {
                plantView.setName(plantName);
                plantView.setDegree(degree);
                plantView.setInfo("Problème lié au chargement de la météo");
                contentMain.invalidate();
                contentMain.requestLayout();
            }
        }else if(!isNetworkAvailable()){
            plantView.setName(plantName);
            plantView.setDegree(degree);
            plantView.setInfo("Pas d'accès internet");
        }
        //On récupère l'image au nom de la plante depuis le stockage interne et on l'ajoute à la "PlantView" définie juste au-dessus
        showImageFromStorage(plantView, name);

        //Et finalement on ajoute la "PlantView" que l'on vient de définir à la vue principale
        contentMain.addView(plantView);
        showNextDayTemp(temp);


    }

    //Fonction utilisée pour charger les plantes au démarrage de l'application
    public void loadPlants(double temp) {
        Log.v("LOAD PLANT", "Chargement des plantes");
        File[] files = listTxt();
        //Si le tableau n'est pas vide
        if (files.length != 0) {

            /*String data = readFromFile(getApplicationContext(), "Localisation.latLng");
            String[] latLgn;
            latLgn = data.split(";");
            if(!(data.isEmpty())) {
                Log.v("Latitude", latLgn[0]);
                Log.v("Longitude", latLgn[1]);
                double latitude = Double.parseDouble(latLgn[0]);
                double longitude = Double.parseDouble(latLgn[1]);
                WeatherClass.getTemp(latitude,longitude, new IResult() {
                    @Override
                    public void onResult(double temp) {
                        MainActivity.temp = temp;
                    }
                });
                showToast(temp +"");
                Log.v("Load", temp +"");
            }else{
                temp = 100000;
            }*/
            //LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            //Boucle pour afficher chaque plante sur la vue principale, en fonction du nombre de fichiers, donc du nombre de plantes enregistrées
            for (File file : files) {

                //à chaque tour de boucle, on récupère le nom du fichier dans une variable
                String fileName = file.getName();
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
                //Plant plant = new Plant(getApplicationContext(), plantName, degree, isMovable);

                //On récupère l'id de la vue principale
                LinearLayout contentMain = findViewById(R.id.mainLinearLayout);


                PlantView plantView = new PlantView(getApplicationContext(), null);


                //Création d'une nouvelle vue de type "PlantView"

                if (isNetworkAvailable()) {
                    if (temp == 100000) {
                        plantView.setName(plantName);
                        plantView.setDegree(degree);
                        plantView.setInfo("Vous n'avez pas encore indiqué de localisation");
                        contentMain.invalidate();
                        refreshView(contentMain);

                    } else if (temp == -1000000) {
                        plantView.setName(plantName);
                        plantView.setDegree(degree);
                        plantView.setInfo("Problème lié au chargement de la météo");
                        contentMain.invalidate();
                        refreshView(contentMain);

                    } else if (temp == 0.0) {
                        /*finish();
                        startActivity(this.getIntent());*/
                        plantView.setName(plantName);
                        plantView.setDegree(degree);
                        plantView.setInfo("Problème lié au chargement de la météo");
                        contentMain.invalidate();
                        refreshView(contentMain);

                    } else if (temp < Double.parseDouble(degree)+2) {
                        if (isMovable) {
                            plantView.setName(plantName);
                            plantView.setDegree(degree);
                            plantView.setInfo("Pensez à rentrer votre plante");
                            plantView.changeBackgroundColor("#ff7961");
                            contentMain.invalidate();
                            refreshView(contentMain);}
                        else {
                            plantView.setName(plantName);
                            plantView.setDegree(degree);
                            plantView.setInfo("Pensez à couvrir votre plante");
                            plantView.changeBackgroundColor("#ff7961");
                            contentMain.invalidate();
                            refreshView(contentMain);}

                    } else if (temp > Double.parseDouble(degree)+2) {
                        plantView.setName(plantName);
                        plantView.setDegree(degree);
                        plantView.setInfo("Pas de problème pour cette plante");
                        contentMain.invalidate();
                        refreshView(contentMain);

                    } else {
                        plantView.setName(plantName);
                        plantView.setDegree(degree);
                        plantView.setInfo("Problème lié au chargement de la météo");
                        contentMain.invalidate();
                        refreshView(contentMain);
                    }
                } else if (!isNetworkAvailable()) {
                    plantView.setName(plantName);
                    plantView.setDegree(degree);
                    plantView.setInfo("Pas d'accès internet");
                }


                Log.v("Plantes:", plantName + ": " + degree + "°C " + "Déplaçable : " + isMovable);

                //On ajoute l'image de la photo en question à la vue
                showImageFromStorage(plantView, plantName);

                //On ajoute la "PlantView" à la vue principale
                contentMain.addView(plantView);
            }


        }else{
            //showToast("Vous n'avez pas encore de plantes enregistrées");

            plantsView = findViewById(R.id.mainLinearLayout);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(25, 0, 25, 0);
            firstPlantButton = new Button(this);
            firstPlantButton.setLayoutParams(params);
            firstPlantButton.setText(R.string.firstPlantText);
            firstPlantButton.setAllCaps(false);
            firstPlantButton.setBackgroundResource(R.drawable.ripple_bg_shape);
            firstPlantButton.setTypeface(FontsUtils.getRalewayRegular(getApplicationContext()));
            firstPlantButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddPlantDialog();
                }
            });
            plantsView.addView(firstPlantButton);



        }
        showNextDayTemp(temp);

    }

    public void showAddPlantDialog(){
        final AddPlantDialogClass addPlantDialog = new AddPlantDialogClass(MainActivity.this);
        addPlantDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //On affiche cette fenêtre de dialogue
        addPlantDialog.show();

        //On récupère l'id du bouton "Valider" de la fenêtre de dialogue que l'on vient de créer
        addPlantDoneButton = addPlantDialog.findViewById(R.id.done_button_addPlant);

        //On récupère l'id du bouton "Ajouter une image" de la fenêtre de dialogue que l'on vient de créer
        addImageButton = addPlantDialog.findViewById(R.id.addImage);

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
                plantEdit = addPlantDialog.findViewById(R.id.plant_name);

                //On récupère l'id du champ de texte correspondant au degré de la plante
                degreeEdit = addPlantDialog.findViewById(R.id.degree_nbr);

                //On récupère l'id de "l'interrupteur" indiquant si la plante est déplaçable ou non
                aSwitch = addPlantDialog.findViewById(R.id.moveable_plant);

                //Définition d'un boolean récupérant l'état de l'interrupteur
                boolean switchState = aSwitch.isChecked();

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
                    boolean bool = true;
                    while(bool) {
                        int alea = Randomizer.generate(1, 10);
                        int i = 0;
                        Log.v("TEST", alea+"");
                        if (alea == 1 && i!=1) {
                            selectedImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.plant_img_type1);
                            i = 1;
                            bool = false;
                        } else if (alea == 2 && i!=2) {
                            selectedImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.plant_img_type2);
                            i =2;
                            bool = false;
                        } else if (alea == 3 && i!=3) {
                            selectedImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.plant_img_type3);
                            i = 3;
                            bool = false;
                        } else if (alea == 4 && i!=4) {
                            selectedImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.plant_img_type4);
                            i = 4;
                            bool = false;
                        } else if (alea == 5 && i!=5) {
                            selectedImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.plant_img_type5);
                            i = 5;
                            bool = false;
                        } else if (alea == 6 && i!=6) {
                            selectedImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.plant_img_type6);
                            i = 6;
                            bool = false;
                        } else if (alea == 7 && i!=7) {
                            selectedImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.plant_img_type7);
                            i = 7;
                            bool = false;
                        } else if (alea == 8 && i!=8) {
                            selectedImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.plant_img_type8);
                            i = 8;
                            bool = false;
                        } else if (alea == 9 && i!=9) {
                            selectedImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.plant_img_type9);
                            i = 9;
                            bool = false;
                        } else if (alea == 10 && i!=10) {
                            selectedImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.plant_img_type10);
                            i = 10;
                            bool = false;
                        } else{
                            bool = true;
                        }

                    }
                    if(switchState) {
                        addPlantView(getApplicationContext(), plantName, degree,true);
                        //On ferme la fenêtre de dialogue
                        addPlantDialog.dismiss();
                        //Sinon, donc si elle ne l'est pas
                    }else{
                        addPlantView(getApplicationContext(), plantName, degree,false);
                        addPlantDialog.dismiss();
                    }

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
            bitmapImage = getResizedBitmap(bitmapImage,200);
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
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public boolean isServicesOK(){
        Log.d("Maps", "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d("Maps", "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d("Maps", "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void refreshView(ViewGroup view){
        view.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }

    public void showNextDayTemp(double temp){
        mainLinearLayout = findViewById(R.id.mainLinearLayout);
        if(tempText == null) {
            LayoutTransition transition = new LayoutTransition();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                transition.enableTransitionType(LayoutTransition.CHANGING);
            }
            transition.setDuration(300);
            mainLinearLayout.setLayoutTransition(transition);



            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            params.gravity = Gravity.CENTER;

            tempText = new TextView(getApplicationContext());
            if(temp == 100000) {
                tempText.setText("Veuillez indiquer une localisation");
            }else if(temp == -100000){
                tempText.setText("Problème lors du chargement de la météo");
            }else{
                tempText.setText("Température minimale prévue: " + (Math.round(temp*10.0)/10.0) + "°C");
            }

            tempText.setTextSize(9);
            tempText.setLayoutParams(params);
            tempText.setTypeface(FontsUtils.getRalewayLight(getApplicationContext()));
            tempText.setTextColor(Color.parseColor("#FFFFFF"));

            mainLinearLayout.addView(tempText);
        }else{
            mainLinearLayout.removeView(tempText);
            tempText = null;
            showNextDayTemp(temp);
        }
    }

}