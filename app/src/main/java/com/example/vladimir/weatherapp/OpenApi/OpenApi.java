package com.example.vladimir.weatherapp.OpenApi;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.vladimir.weatherapp.models.City;
import org.json.JSONObject;


//http://api.openweathermap.org/data/2.5/weather?q=London,uk&appid=4cae296740ca84cb880e305600622884
public class OpenApi {
    private Context context;

    private static final String ENDPOINT = "http://api.openweathermap.org/data/2.5/";

    private static final String key = "4cae296740ca84cb880e305600622884";

    public OpenApi(Context context) {
        this.context = context;
    }

    public void getCityData(City city, Data data, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String url = ENDPOINT + data.name().toLowerCase() + "?q=" + city.getName() + "," + city.getCountry() + "&appid=" + key;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    public enum Data {
        WEATHER,
        FORECAST
    }
}
