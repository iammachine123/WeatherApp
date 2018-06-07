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
    private EditText cityEditText, countryCodeEditText;
    private ArrayAdapter<City> citiesAdapter;

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
        cityEditText = findViewById(R.id.cityEditText);
        countryCodeEditText = findViewById(R.id.countryCodeEditText);
        citiesAdapter = new ArrayAdapter<City>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<>()) {{
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            addAll(addedCities);
        }};
        spinner.setAdapter(citiesAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                handleSelectedItem();
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCityButton:
                Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
                City city = new City(cityEditText.getText().toString(), countryCodeEditText.getText().toString());
                addedCities.add(city);
                citiesAdapter.clear();
                citiesAdapter.addAll(addedCities);
                spinner.setSelection(citiesAdapter.getPosition(city), true);
                handleSelectedItem();
                break;
        }
    }

    private void handleSelectedItem() {
        if (spinner.getSelectedItem() != null) {
            openApi.getCityData((City) spinner.getSelectedItem(),
                    OpenApi.Data.WEATHER,
                    response -> {
                        weatherText = parseWeather(response);
                        weather.setText(weatherText + forecastText);
                    },
                    error -> {
                        try {
                            Toast.makeText(getApplicationContext(), "count not load weather data " + new JSONObject(new String(error.networkResponse.data)).optString("message"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
            openApi.getCityData((City) spinner.getSelectedItem(),
                    OpenApi.Data.FORECAST,
                    response -> {
                        forecastText = parseForecast(response);
                        weather.setText(weatherText + forecastText);
                    },
                    error -> {
                        try {
                            Toast.makeText(getApplicationContext(), "count not load forecast data " + new JSONObject(new String(error.networkResponse.data)).optString("message"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
