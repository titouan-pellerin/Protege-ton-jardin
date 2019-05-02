package fr.visufo.titouan.jardin;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.Gravity.CENTER;

/**
 * Classe permettant de créer des vues customisées de type "PlantView"
 */

public class PlantView extends FrameLayout {

    //Views
    private UnderlinedTextView nameView;
    private TextView infoView;
    private TextView degreeView;
    private CircleImageView imageView;
    private LinearLayout planteBg;
    private Button removeButton;
    private LinearLayout btnContainer;
    private LinearLayout textsView;

    //Attributes
    private String nameText;
    private String infoText;
    private String degreeText;
    private Drawable plantImage;

    private boolean isExtended = false;

    /****************
     * CONSTRUCTEURS
     ***************/

    /**
     * Constructeur de la classe.
     *
     * @param context the context.
     */
    public PlantView(@NonNull Context context) {
        super(context);
        obtainStyledAttributes(context, null, 0);
        init();
    }

    /**
     * Constructeur.
     *
     * @param context the context.
     * @param attrs   the attributes from the layout.
     */
    public PlantView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        obtainStyledAttributes(context, attrs, 0);
        init();
    }

    /**
     * Constructeur.
     *
     * @param context      the context.
     * @param attrs        the attributes from the layout.
     * @param defStyleAttr the attributes from the default style.
     */
    public PlantView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttributes(context, attrs, defStyleAttr);
        init();
    }


    /**********
    * FONCTIONS
    ***********/

    //Fonction utile pour récupérer les attributs si l'on ajoute une "PlantView" directement en XML
    private void obtainStyledAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlantView, defStyleAttr, 0);
            nameText = typedArray.getString(R.styleable.PlantView_name);
            infoText = typedArray.getString(R.styleable.PlantView_info);
            degreeText = typedArray.getString(R.styleable.PlantView_degree);
            plantImage = typedArray.getDrawable(R.styleable.PlantView_android_src);


        }
    }
    //Fonction permettant d'initialiser la vue
    private void init() {
        inflate(getContext(), R.layout.plantview, this);
        nameView = findViewById(R.id.nomPlante);
        infoView = findViewById(R.id.info);
        degreeView = findViewById(R.id.degree);
        imageView = findViewById(R.id.image);
        planteBg = findViewById(R.id.plante_bg);
        textsView = findViewById(R.id.textes);
        setupView();
    }
    //Fonction permettant d'"installer" la vue
    private void setupView() {

        btnContainer = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = 20;
        params.height = 110;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.bottomMargin = 15;
        params.gravity = Gravity.START|CENTER;


        btnContainer.setLayoutParams(params);



        removeButton = new Button(getContext());
        removeButton.setText("Supprimer");
        removeButton.setLayoutParams(new LinearLayout.LayoutParams(180, 100));
        removeButton.setBackgroundResource(R.drawable.remove_plant_bg);
        removeButton.setTextSize(10);
        removeButton.setAllCaps(false);
        removeButton.setTypeface(FontsUtils.getRalewayRegular(getContext()));



        nameView.setText(nameText);
        infoView.setText(infoText);
        degreeView.setText(degreeText +" °C");
        imageView.setImageDrawable(plantImage);




        planteBg.setOnClickListener(new OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {

                if(isExtended){

                    textsView.removeView(btnContainer);
                    btnContainer.removeView(removeButton);
                    isExtended = false;


                }else if(!isExtended) {
                    textsView.addView(btnContainer);
                    btnContainer.addView(removeButton);
                    isExtended = true;
                }
            }
        });
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getContext().getFilesDir(),nameView.getText()+".txt");
                file.delete();
                LinearLayout parent = (LinearLayout) getParent();
                parent.removeView(PlantView.this);
            }
        });

        LayoutTransition transition = new LayoutTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.enableTransitionType(LayoutTransition.CHANGING);
        }
        transition.setDuration(300);
        this.setLayoutTransition(transition);


    }

    /**
     * Fonctions permettant de changer les attributs
     * de la vue par programmation
     */
    public void setName(String name) {
        nameView.setText(name);
        invalidate();
        requestLayout();
    }
    public void setInfo(String info){
        infoView.setText(info);
        invalidate();
        requestLayout();
    }
    public void setDegree(String degree){
        degreeView.setText(degree+" °C");
    }
    public void setImage(Bitmap image){
        imageView.setImageBitmap(image);
        invalidate();
        requestLayout();
    }

    public void changeBackgroundColor(String color){
        planteBg.getBackground().setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_OVER);
        Log.v("Plant View", "Changing background color");
        invalidate();
        requestLayout();
    }

    public void changeTextColor(String color){
        infoView.setTextColor(Color.parseColor(color));
        Log.v("Plant View", "Changing text color");
        invalidate();
        requestLayout();
    }


    public void showToast(String str){
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }


}