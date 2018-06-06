package com.example.vladimir.weatherapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.vladimir.weatherapp.OpenApi.OpenApi;
import com.example.vladimir.weatherapp.models.City;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {

    private Set<City> addedCities = new HashSet<City>() {{
        add(new City("Moscow", "ru"));
        add(new City("Saint Petersburg", "ru"));
    }};

    private Spinner spinner;
    private TextView weather, forecast;
//    private JSONObject weather, forecast;

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
        forecast = findViewById(R.id.forecastTextView);

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
                                try {
                                    weather.setText("Weather is " + ((JSONObject) response.optJSONArray("weather").get(0)).optString("main") +
                                            " temperature is " + response.optJSONObject("main").optString("temp"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            error -> Toast.makeText(getApplicationContext(), "count not load weather data " + error.toString(), Toast.LENGTH_SHORT).show());
                    openApi.getCityData((City) spinner.getSelectedItem(),
                            OpenApi.Data.FORECAST,
                            response -> {
                                forecast.setText(response.toString());
                                List<Date> dateList = new ArrayList<>();
                                for (int j = 0; j < 23; j++) {
                                    try {
                                        dateList.add(new Date(Integer.parseInt(((JSONObject) response.optJSONArray("list").get(j)).optString("dt")) * 1000L));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                final String[] result = {""};
                                dateList.stream().map(Date::toString).forEach(s -> result[0] += s + "\n");
                                forecast.setText(result[0]);
                            },
                            error -> Toast.makeText(getApplicationContext(), "count not load forecast data " + error.toString(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }
}
