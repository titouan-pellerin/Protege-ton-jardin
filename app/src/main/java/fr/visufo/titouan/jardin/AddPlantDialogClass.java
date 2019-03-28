package fr.visufo.titouan.jardin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



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
        done = (Button) findViewById(R.id.done_button_addPlant);
      //  done.setOnClickListener(this);
        plantName = findViewById(R.id.plant_name).toString();
        degreeNbr = findViewById(R.id.degree_nbr).toString();






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