package fr.visufo.titouan.jardin.Weather;

import android.content.Context;
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

    private static double temp = 0.0;

    public static void getTemp(double latitude, double longitude, final IResult callback, Context context) {
        Toast.makeText(context, "Chargement des plantes", Toast.LENGTH_SHORT).show();
        String OPEN_WEATHER_MAP_API = "97e202a04a512514be6c36668fb2a5e3";
        new WeatherManager(OPEN_WEATHER_MAP_API)
                .getFiveDayForecastByCoordinates(latitude, longitude,
                        new WeatherManager.ForecastHandler() {
                            @Override
                            public void onReceivedForecast(WeatherManager manager, Forecast forecast) {
                                List<Double> list = new ArrayList<>();
                                for (int i = 0; i < 5; i++) {
                                    long timestamp = forecast.getTimestampByIndex(i + 3);
                                    Weather weatherForTimestamp = forecast.getWeatherForTimestamp(timestamp);
                                    Temperature tempMini = weatherForTimestamp.getTemperature().getMinimum();
                                    double temperatureInCelcius = tempMini.getValue(TemperatureUnit.CELCIUS);
                                    list.add(temperatureInCelcius);
                                }
                                int minIndex = list.indexOf(Collections.min(list));
                                temp = list.get(minIndex);
                                callback.onResult(temp);
                            }

                            @Override
                            public void onFailedToReceiveForecast(WeatherManager manager) {
                                temp = -100000;
                                callback.onResult(temp);
                            }
                        }
                );
    }
}





