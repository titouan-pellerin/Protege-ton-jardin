package fr.visufo.titouan.jardin;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.visuality.f32.temperature.Temperature;
import com.visuality.f32.temperature.TemperatureUnit;
import com.visuality.f32.weather.data.entity.Forecast;
import com.visuality.f32.weather.manager.WeatherManager;

import java.util.concurrent.TimeUnit;



public class Weather {

    private static String OPEN_WEATHER_MAP_API = "f296d96e2cc0b7b08741e0b238731746";


    public static void setWeatherCity(final String cityName, final Context context){
        new WeatherManager(OPEN_WEATHER_MAP_API).getFiveDayForecastByCityName(
                cityName,
                new WeatherManager.ForecastHandler() {
                    @Override
                    public void onReceivedForecast(WeatherManager manager, Forecast forecast) {
                        // Handle forecast

                        int numberOfAvailableTimestamps = forecast.getNumberOfTimestamps();


                        long rightNow = System.currentTimeMillis() / 1000;

                        long tomorrow = rightNow + TimeUnit.DAYS.toSeconds(1);
                        com.visuality.f32.weather.data.entity.Weather weatherInTwoDays = forecast.getWeatherForTimestamp(tomorrow);
                        Temperature temp = weatherInTwoDays.getTemperature().getMinimum();
                        double tempC = temp.getValue(TemperatureUnit.CELCIUS);

                        Toast.makeText(context,"Température " + tempC ,Toast.LENGTH_SHORT).show();
                        /*for (int timestampIndex = 0; timestampIndex < numberOfAvailableTimestamps; timestampIndex++) {
                            long timestamp = forecast.getTimestampByIndex(timestampIndex);
                            Weather weatherForTimestamp = forecast.getWeatherForTimestamp(timestamp);
                            Temperature tempMin = weatherForTimestamp.getTemperature().getMinimum();
                            double temperatureInCelcius = tempMin.getValue(TemperatureUnit.CELCIUS); // 0.0 degrees
                        }*/
                    }

                    @Override
                    public void onFailedToReceiveForecast(WeatherManager manager) {
                        Log.v("TAG", "Température à "+cityName + " ERREUR");

                    }
                }
        );

    }


}
