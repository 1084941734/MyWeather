package com.android.mytest.myweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 12701 on 2017-04-06.
 */

public class Province extends DataSupport{
    private int id;
    private int provinceId;
    private String provinceName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getprovinceId() {
        return provinceId;
    }

    public void setprovinceId(int cityId) {
        this.provinceId = cityId;
    }
}
