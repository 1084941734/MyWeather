package com.android.mytest.myweather.gson;

/**
 * Created by 12701 on 2017-04-07.
 */
//Gson实体类，对应Json中Now字段
public class Weather_Now {

    public int fl;//体感温度
    public int hum;//相对湿度（%）
    public int pres; //气压
    public int tmp; //当前温度
    public Cond cond;
    public Wind wind;

    public class Cond {//天气状况
        public int code;//天气状况代码
        public String txt;//天气状况描述
    }
    public class Wind{//风力风向
        public String sc;//风力
    }
}
