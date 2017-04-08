package com.android.mytest.myweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 12701 on 2017-04-07.
 */
//Gson实体类
public class Weather {
    public String status;//接口状态，返回OK 调用成功


    public Weather_Basic basic;
    public Weather_Now now;
    public Weather_Aqi aqi;
    @SerializedName("daily_forecast")
    public List<Weather_Forecast>weather_forecasts;

}
