package com.android.mytest.myweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 12701 on 2017-04-07.
 */
//Gson实体类对应 JSON中 basic字段
public class Weather_Basic {

    @SerializedName("city")
    public String cityName; //城市名

    @SerializedName("id")
    public String cityID;   //城市ID

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String UpdataTime; //更新时间
    }

}
