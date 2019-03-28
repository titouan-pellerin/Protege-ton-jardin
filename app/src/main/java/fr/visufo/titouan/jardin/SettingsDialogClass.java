package fr.visufo.titouan.jardin;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class SettingsDialogClass extends Dialog{

    public Activity c;
    public Dialog d;
    public Button done;

    public SettingsDialogClass(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings_dialog);
        /*done = (Button) findViewById(R.id.done_button_settings);
        done.setOnClickListener(this);
*/

    }

    /*@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done_button_settings:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }*/
}
