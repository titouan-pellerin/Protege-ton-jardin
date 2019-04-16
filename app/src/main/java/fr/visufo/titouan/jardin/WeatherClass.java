package fr.visufo.titouan.jardin;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.visuality.f32.temperature.Temperature;
import com.visuality.f32.temperature.TemperatureUnit;
import com.visuality.f32.weather.data.entity.Forecast;
import com.visuality.f32.weather.data.entity.Weather;
import com.visuality.f32.weather.manager.WeatherManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class WeatherClass {

    private static String OPEN_WEATHER_MAP_API = "f296d96e2cc0b7b08741e0b238731746";


    public static void setWeatherCity(final double latitude, double longitude, final Context context){
        new WeatherManager(OPEN_WEATHER_MAP_API).getFiveDayForecastByCoordinates(latitude,longitude,
                new WeatherManager.ForecastHandler() {
                    @Override
                    public void onReceivedForecast(WeatherManager manager, Forecast forecast) {
                        // Handle forecast

                        List<Double> list = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            long timestamp = forecast.getTimestampByIndex(i+3);
                            Weather weatherForTimestamp = forecast.getWeatherForTimestamp(timestamp);
                            Temperature tempMini = weatherForTimestamp.getTemperature().getMinimum();
                            double temperatureInCelcius = tempMini.getValue(TemperatureUnit.CELCIUS);
                            list.add(temperatureInCelcius);
                            Log.v("Weather", "" +weatherForTimestamp.getWind().getSpeed());
                            Log.v("Weather", "Température mini : " +  " "+ list.get(i));
                        }
                        int  minIndex = list.indexOf(Collections.min(list));
                        Log.v("Weather MINI", "Température mini : " + list.get(minIndex));
                        Toast.makeText(context, "Température mini: " + list.get(minIndex), Toast.LENGTH_LONG).show();




                        /*for (int timestampIndex = 0; timestampIndex < numberOfAvailableTimestamps; timestampIndex++) {
                            long timestamp = forecast.getTimestampByIndex(timestampIndex);
                            Weather weatherForTimestamp = forecast.getWeatherForTimestamp(timestamp);
                            Temperature tempMin = weatherForTimestamp.getTemperature().getMinimum();
                            double temperatureInCelcius = tempMin.getValue(TemperatureUnit.CELCIUS); // 0.0 degrees
                        }*/
                    }

                    @Override
                    public void onFailedToReceiveForecast(WeatherManager manager) {
                        Log.v("TAG",  " ERREUR");

                    }
                }
        );

    }



}
