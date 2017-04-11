package com.android.mytest.myweather.gson;

/**
 * Created by 12701 on 2017-04-09.
 */

public class Weather_suggestion {

    public Comf comf; //舒适度
    public Cw cw;   //洗车
    public Drsg drsg; //穿衣
    public Flu flu; //感冒
    public Sport sport; //运动
    public Trav trav; //旅行
    public UV uv; //紫外线
    public Air air; //空气质量



    public class Comf{ //舒适度
        public String brf;
        public String txt;
    }
    public class Cw{   //洗车指数
        public String brf;
        public String txt;
    }
    public class Drsg{  //穿衣指数
        public String brf;
        public String txt;
    }
    public class Flu{   //感冒指数
        public String brf;
        public String txt;
    }
    public class Sport{  //运动指数
        public String brf;
        public String txt;
    }
    public class Trav{  //旅行指数
        public String brf;
        public String txt;
    }

    public class Air{ //空气质量
        public String brf;
        public String txt;
    }
    public class UV{ //紫外线指数
        public String brf;
        public String txt;
    }

}
