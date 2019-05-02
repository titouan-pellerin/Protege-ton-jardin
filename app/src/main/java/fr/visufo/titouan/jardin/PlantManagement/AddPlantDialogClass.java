package fr.visufo.titouan.jardin.PlantManagement;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import fr.visufo.titouan.jardin.R;

public class AddPlantDialogClass extends Dialog{

    public Activity c;
    public Button done;
    public Dialog d;
    String plantName;
    String degreeNbr;

    private static final String TAG = AddPlantDialogClass.class.getName();


    public AddPlantDialogClass(Activity a) {
        super(a);
        this.c = a;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.addplante_dialog);
        /*done = (Button) findViewById(R.id.done_button_addPlant);
      //  done.setOnClickListener(this);
        plantName = findViewById(R.id.plant_name).toString();
        degreeNbr = findViewById(R.id.degree_nbr).toString();*/






    }

  /* @Override
    public void onClick(View v) {
       switch (v.getId()) {
            case R.id.done_button_addPlant:
                Log.v(TAG,"TEST");

                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
*/
}