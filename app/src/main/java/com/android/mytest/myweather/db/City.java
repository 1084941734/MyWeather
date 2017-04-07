package com.android.mytest.myweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 12701 on 2017-04-06.
 */

public class City  extends DataSupport{
    private String cityName;
    private int provinceID;
    private int id;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
