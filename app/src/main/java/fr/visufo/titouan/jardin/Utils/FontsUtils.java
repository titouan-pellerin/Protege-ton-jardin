package fr.visufo.titouan.jardin.Utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontsUtils {

    public static Typeface getRalewayLight(Context context){
        return Typeface.createFromAsset(context.getAssets(), "raleway_light.ttf");
    }
    public static Typeface getRalewayRegular(Context context){
        return Typeface.createFromAsset(context.getAssets(), "raleway_regular.ttf");
    }
    public static Typeface getRalewaySemiBold(Context context){
        return Typeface.createFromAsset(context.getAssets(), "raleway_semibold.ttf");
    }
    public static Typeface getRobotoMedium(Context context){
        return Typeface.createFromAsset(context.getAssets(), "roboto_medium.ttf");
    }
}
