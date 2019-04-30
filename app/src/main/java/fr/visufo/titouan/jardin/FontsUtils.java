package fr.visufo.titouan.jardin;

import android.content.Context;
import android.graphics.Typeface;

public class FontsUtils {

    static Typeface getRalewayLight(Context context){
        return Typeface.createFromAsset(context.getAssets(), "raleway_light.ttf");
    }
    static Typeface getRalewayRegular(Context context){
        return Typeface.createFromAsset(context.getAssets(), "raleway_regular.ttf");
    }
    static Typeface getRalewaySemiBold(Context context){
        return Typeface.createFromAsset(context.getAssets(), "raleway_semibold.ttf");
    }
    static Typeface getRobotoMedium(Context context){
        return Typeface.createFromAsset(context.getAssets(), "roboto_medium.ttf");
    }
}
