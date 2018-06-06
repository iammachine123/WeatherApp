package com.example.vladimir.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.example.vladimir.weatherapp.OpenApi.OpenApi;
import com.example.vladimir.weatherapp.models.City;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private Set<City> addedCities = new HashSet<City>() {{
        add(new City("Moscow", "ru"));
        add(new City("Saint Petersburg", "ru"));
    }};

//    private JSONObject weather, forecast;

    private OpenApi openApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openApi = new OpenApi(getApplicationContext());
//        openApi.getCityData(new City("Moscow", "ru"),
//                OpenApi.Data.WEATHER,
//                response -> weather = response,
//                error -> Toast.makeText(getApplicationContext(), "count not load data " + error.toString(), Toast.LENGTH_SHORT).show());
    }
}
