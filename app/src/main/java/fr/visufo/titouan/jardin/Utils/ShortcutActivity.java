package fr.visufo.titouan.jardin.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import fr.visufo.titouan.jardin.MainActivity;
import fr.visufo.titouan.jardin.R;

public class ShortcutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut);

        Intent intent = new Intent(ShortcutActivity.this, MainActivity.class);
        intent.putExtra("caller", "MainActivity");
        startActivity(intent);
        finish();
    }
}
