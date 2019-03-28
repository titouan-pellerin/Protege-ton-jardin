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

    SpeedDialView mSpeedDialView;
    EditText plantEdit;
    EditText degreeEdit;
    String plantName;
    String degree;
    Switch aSwitch;

    private Button addImage;
    Bitmap selectedImage;

    static final int RESULT_LOAD_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        addFab();
        loadPlants();



    }



    public void addFab() {
        mSpeedDialView = findViewById(R.id.speedDial);
        //Ajouter le bouton ajouter une plante
        mSpeedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_add_plant, R.drawable.ic_plants)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme()))
                //  .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()))
                .setLabel("Ajouter une plante")
                .create());

        mSpeedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_settings, R.drawable.ic_settings)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme()))
                .setLabel("Paramètres")
                .create());

        mSpeedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.fab_add_plant:
                        final AddPlantDialogClass addPlantDialog = new AddPlantDialogClass(MainActivity.this);
                        addPlantDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        addPlantDialog.show();
                        Button btn = (Button) addPlantDialog.findViewById(R.id.done_button_addPlant);

                        addImage = (Button) addPlantDialog.findViewById(R.id.addImage);
                        addImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);

                            }
                        });
                        btn.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                plantEdit = (EditText) addPlantDialog.findViewById(R.id.plant_name);
                                degreeEdit = (EditText) addPlantDialog.findViewById(R.id.degree_nbr);
                                aSwitch = (Switch) addPlantDialog.findViewById(R.id.moveable_plant);
                                Boolean switchState = aSwitch.isChecked();
                                plantName = plantEdit.getText().toString().trim();
                                degree = degreeEdit.getText().toString().trim();
                                if(plantName.isEmpty() && degree.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Les champs ne sont pas remplis", Toast.LENGTH_LONG).show();
                                }else if (degree.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Indiquer un degré de gel", Toast.LENGTH_LONG).show();
                                }else if (plantName.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Indiquer un nom de plante", Toast.LENGTH_LONG).show();
                                }else if(selectedImage==null){
                                    Toast.makeText(getApplicationContext(), "Vous n'avez pas ajouté de photo", Toast.LENGTH_LONG).show();
                                }else{
                                    if(switchState) {
                                        addPlant(getApplicationContext(), plantName, degree,true);
                                        addPlantDialog.dismiss();
                                    }else{
                                        addPlant(getApplicationContext(), plantName, degree,false);
                                        addPlantDialog.dismiss();
                                    }
                                }

                            }
                        });


                        mSpeedDialView.close(); // To close the Speed Dial with animation
                        return true; // false will close it without animation
                    case R.id.fab_settings:
                        SettingsDialogClass settingDialog = new SettingsDialogClass(MainActivity.this);
                        settingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        settingDialog.show();

                        mSpeedDialView.close(); // To close the Speed Dial with animation

                        return true; // false will close it without animation
                    default:
                        break;
                }
                return true; // To keep the Speed Dial open
            }
        });
    }

    public void addPlant(Context context, String name, String degree, boolean isMoveable) {

        if(selectedImage!=null) {
            saveToInternalStorage(selectedImage, name);
            selectedImage = null;
            Plant plant = new Plant(context, name, degree);

            LinearLayout contentMain = (LinearLayout) findViewById(R.id.mainLinearLayout);


            PlantView plantView = new PlantView(context, null);
            plantView.setName(name);
            plantView.setDegree(degree);
            plantView.setInfo("Info info info info");

            loadImageFromStorage(getApplicationContext().getFilesDir().toString(), plantView, name);


            contentMain.addView(plantView);

        }

    }

    public void loadPlants() {
        String path = getApplicationContext().getFilesDir().toString();
        File directory = new File(path);
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.getPath().endsWith(".txt"));
            }
        });


        if (!(files == null)) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                String content = readFromFile(getApplicationContext(), fileName);
                String[] contentSplit;
                contentSplit = content.split(";");

                String plantName = contentSplit[0];
                String degree = contentSplit[1];

                Plant plant = new Plant(getApplicationContext(),plantName , degree);
                LinearLayout contentMain = (LinearLayout) findViewById(R.id.mainLinearLayout);
                PlantView plantView = new PlantView(getApplicationContext(), null);

                plantView.setName(plantName);
                plantView.setDegree(degree);
                plantView.setInfo("Info info info info");

                loadImageFromStorage(path, plantView, plantName);
                contentMain.addView(plantView);
                addPlant(getApplicationContext(), plantName, degree, false);
                Log.d("Files", "FileName:" + files[i].getName());

            }
        }
    }

    private String readFromFile(Context context, String fileName) {

        String ret = "";

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
        } catch (FileNotFoundException e) {
            Log.e("F", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("F", "Can not read file: " + e.toString());
        }

        return ret;
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);

                Drawable plantImg = new BitmapDrawable(getResources(), selectedImage);
                addImage.setBackgroundDrawable(plantImg);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Une erreur s'est produite", Toast.LENGTH_LONG).show();

            }

        } else {
            Toast.makeText(getApplicationContext(), "Vous n'avez pas choisi d'image", Toast.LENGTH_LONG).show();

        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String plantName) {
        File directory = getApplicationContext().getFilesDir();
        // Create imageDir
        File mypath = new File(directory, plantName + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage = getResizedBitmap(bitmapImage,100);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);


            Toast.makeText(getApplicationContext(), "Fichier enregistré" + plantName, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path, PlantView plantView, String plantName) {

        try {
            File f = new File(path, plantName + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            plantView.setImage(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public Bitmap getBitmap(String path) {
        Bitmap bitmap = null;

        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    return bitmap;
    }

    public Bitmap getResizedBitmap(Bitmap bitmap, int width) {
        float aspectRatio = bitmap.getWidth() /
                (float) bitmap.getHeight();
        int height = Math.round(width / aspectRatio);


    return Bitmap.createScaledBitmap(bitmap,width, height, false);
    }
}
