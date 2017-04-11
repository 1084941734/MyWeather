package com.android.mytest.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("weatherInfo",MODE_PRIVATE);
        String weather = preferences.getString("weather_ID", null);
        if (weather!=null){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
