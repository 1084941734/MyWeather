package com.android.mytest.myweather.gson;

/**
 * Created by 12701 on 2017-04-08.
 */

public class Weather_Aqi {
    public City city;

    public class City{
    public String aqi; // AQI具体值
    public String pm25;//PM2.5
    }
}
