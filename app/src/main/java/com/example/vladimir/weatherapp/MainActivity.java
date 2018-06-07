package com.example.vladimir.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.vladimir.weatherapp.OpenApi.OpenApi;
import com.example.vladimir.weatherapp.models.City;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;


public class MainActivity extends AppCompatActivity {

    public static final int forecastParseItemsSize = 25;

    private Set<City> addedCities = new HashSet<City>() {{
        add(new City("Moscow", "ru"));
        add(new City("Saint Petersburg", "ru"));
    }};

    private Spinner spinner;
    private TextView weather;
    private String weatherText = "", forecastText = "";

    private OpenApi openApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openApi = new OpenApi(getApplicationContext());


        initUI();
    }

    private void initUI() {
        spinner = findViewById(R.id.spinner);
        weather = findViewById(R.id.weatherTextView);

        ArrayAdapter<City> adapter =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(addedCities));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItem() != null) {
                    openApi.getCityData((City) spinner.getSelectedItem(),
                            OpenApi.Data.WEATHER,
                            response -> {
                                weatherText = parseWeather(response);
                                weather.setText(weatherText + forecastText);
                            },
                            error -> Toast.makeText(getApplicationContext(), "count not load weather data " + error.toString(), Toast.LENGTH_SHORT).show());
                    openApi.getCityData((City) spinner.getSelectedItem(),
                            OpenApi.Data.FORECAST,
                            response -> {
                                forecastText = parseForecast(response);
                                weather.setText(weatherText + forecastText);
                            },
                            error -> Toast.makeText(getApplicationContext(), "count not load forecast data " + error.toString(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private String parseForecast(JSONObject response) {
        StringBuilder forecastData = new StringBuilder("Forecasts: \n");
        for (int j = 0; j < forecastParseItemsSize; j++) {
            try {
                JSONObject jsonObject = ((JSONObject) response.optJSONArray("list").get(j));
                forecastData.append(new SimpleDateFormat("dd-MM HH:mm", Locale.getDefault())
                        .format(new Date(Integer.parseInt(jsonObject.optString("dt")) * 1000L)))
                        .append(" Weather: ")
                        .append(((JSONObject) jsonObject.optJSONArray("weather").get(0)).optString("main"))
                        .append(" temperature is " + jsonObject.optJSONObject("main").optString("temp"))
                        .append("\n");
            } catch (JSONException e) {
                forecastText = "";
                forecastData = new StringBuilder();
                e.printStackTrace();
            }
        }
        return forecastData.toString();
    }

    private String parseWeather(JSONObject response) {
        try {
            return "Weather: \n" + ((JSONObject) response.optJSONArray("weather").get(0)).optString("main") +
                    " temperature is " + response.optJSONObject("main").optString("temp") + "\n";
        } catch (JSONException e) {
            weatherText = "";
            e.printStackTrace();
        }
        return null;
    }
}
