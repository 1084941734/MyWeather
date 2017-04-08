package com.android.mytest.myweather.gson;

/**
 * Created by 12701 on 2017-04-08.
 */

public class Weather_Forecast {


    public String date; //日期
    public int pop;     //降水概率
    public Tmp tmp;   //温度
    public Astro astro; //日出日落
    public Cond cond;   //天气状况
    public Wind wind;   //风力风向
    public int uv;

    public class Tmp{
        public int max; //最高温度
        public int min; //最低温度
    }

    public class Astro{
        public String sr; //日出
        public String ss; //日落
    }
    public class Cond{
        public int code_d;//白天天气状况代码
        public int code_n;//夜间天气状况代码
        public String text_d;//白天天气状况描述
        public String text_n;//夜间天气状况描述
    }

    public class Wind{
        public String dir;//风向
        public String sc;//风力
    }

}
