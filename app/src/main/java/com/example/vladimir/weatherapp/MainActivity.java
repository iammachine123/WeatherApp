package com.example.vladimir.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.vladimir.weatherapp.OpenApi.OpenApi;
import com.example.vladimir.weatherapp.models.City;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


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
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItem() != null) {
                    openApi.getCityData((City) spinner.getSelectedItem(),
                            OpenApi.Data.WEATHER,
                            response -> weather.setText(response.toString()),
                            error -> Toast.makeText(getApplicationContext(), "count not load weather data " + error.toString(), Toast.LENGTH_SHORT).show());
                    openApi.getCityData((City) spinner.getSelectedItem(),
                            OpenApi.Data.FORECAST,
                            response -> forecast.setText(response.toString()),
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
