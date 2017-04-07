package com.android.mytest.myweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 12701 on 2017-04-06.
 */

public class County extends DataSupport {
    private String countyName;
    private String weatherID;
    private int cityID;

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherID() {
        return weatherID;
    }

    public void setWeatherID(String weatherID) {
        this.weatherID = weatherID;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }
}
