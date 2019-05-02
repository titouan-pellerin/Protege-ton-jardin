package fr.visufo.titouan.jardin.PlantManagement;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import fr.visufo.titouan.jardin.R;

public class AddPlantDialogClass extends Dialog {

    private Activity c;

    public AddPlantDialogClass(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.addplante_dialog);
    }
}