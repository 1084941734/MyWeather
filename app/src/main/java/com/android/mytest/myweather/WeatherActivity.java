package com.android.mytest.myweather;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.mytest.myweather.gson.Weather;
import com.android.mytest.myweather.gson.Weather_Forecast;
import com.android.mytest.myweather.service.AutoUpDataWeatherService;
import com.android.mytest.myweather.util.HttpUtil;
import com.android.mytest.myweather.util.ParserUtil;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.R.id.edit;


public class WeatherActivity extends AppCompatActivity {

    private TextView mText_title;
    private TextView mText_fl;
    private TextView mText_now;
    private TextView mText_today;
    private TextView mText_todayInfo;
    private TextView mText_tomorrow;
    private TextView mText_tomorrowInfo;
    private TextView mText_upTime;
    private TextView mText_dy;
    public DrawerLayout mDrawerLayout;
    private ImageView mImage_dy;
    private Button mButton_add;
    private LinearLayout mLinearLayout;
    private TextView mText_aqi;
    private TextView mText_pm25;
    private TextView mText_ssd;
    private TextView mText_cy;
    private TextView mText_yd;
    private TextView mText_gm;
    private TextView mText_xc;
    private TextView mText_lx;
    private SwipeRefreshLayout mSwipeRefresh;
    private ImageView mImage_bg;

    private AutoUpDataWeatherService.MyBinder myBinder;



    //创建服务连接对象
    private ServiceConnection serviceConnection=new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (AutoUpDataWeatherService.MyBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ImageView mImage_tomorrow;
    private ImageView mImage_today;
    private Button mButton_more;
    private Intent intent_service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置透明状态栏，需要Android5.0以上才可以实现，
        if (Build.VERSION.SDK_INT>=21){ //判断系统版本，21=安卓5.1
            View decorView = getWindow().getDecorView();//获取decorView实例
            //调用setSystemUiVisibility改变UI的显示，SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN表示全屏
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置状态栏颜色，Color.TRANSPARENT:表示透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView();
        initSwipeRefreshListener();
        initButtonListener();
        //获取通过Intent传递过来的天气ID
        Intent intent=getIntent();
        String weather_id_Intent = intent.getStringExtra("weather_ID");
        //获取缓存中的天气ID
        SharedPreferences preferences = getSharedPreferences("weatherInfo",MODE_PRIVATE);
        String weather_id_Prefs = preferences.getString("weather_ID", null);
        //判断 缓存和Intent  中 获取的天气ID ：如果Intent中存在天气ID则优先使用Intent传递过来的天气ID
        if (!TextUtils.isEmpty(weather_id_Intent)){
            requestWeatherInfo(weather_id_Intent);
        }else if (!TextUtils.isEmpty(weather_id_Prefs)){
            requestWeatherInfo(weather_id_Prefs);
        }
        int refreshTimes = getIntent().getIntExtra("refreshTimes",3);//默认是3小时启动一次启动一次服务刷新数据
        //开启后台自更新天气服务
        intent_service = new Intent(WeatherActivity.this, AutoUpDataWeatherService.class);
        intent_service.putExtra("refreshTimes",refreshTimes);
        startService(intent_service);
        bindService(intent_service,serviceConnection,BIND_AUTO_CREATE);//绑定服务




    }


    //请求天气信息
    public void requestWeatherInfo(String weatherID) {

        String URL="https://way.jd.com/he/freeweather?city="+weatherID+"&appkey=7259667a8b27e8181a9e4e7d883cecf6";

        HttpUtil.sendOkHttpRequest(URL, new Callback() {
            @Override//请求失败
            public void onFailure(Call call, IOException e) {

            }

            @Override//请求成功
            public void onResponse(Call call, Response response) throws IOException {
                String weatherResponse = response.body().string();
                final Weather weather = ParserUtil.parserWeather(weatherResponse);
                if ( weather!=null && weather.status.equals("ok")) {
                    SharedPreferences.Editor edit = getSharedPreferences("weatherInfo",MODE_PRIVATE).edit();
                    edit.putString("weather_ID",weather.basic.cityID);
                    edit.putString("weatherResponse",weatherResponse);
                    edit.apply();
                    edit.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeatherInfo(weather);
                            requestBackgroundImage();
                        }
                    });
                }
            }
        });
    }

    public void requestBackgroundImage(){
        String URL= "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(URL, new Callback() {
            @Override//请求失败
            public void onFailure(Call call, IOException e) {
                //请求失败，使用默认背景图
                Glide.with(WeatherActivity.this).load(R.mipmap.default_bg).into(mImage_bg);
            }

            @Override//请求成功
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText_URL = response.body().string();

                SharedPreferences.Editor edit = getSharedPreferences("weatherInfo",MODE_PRIVATE).edit();
                edit.putString("BackgroundURL",responseText_URL);
                edit.apply();
                edit.commit();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(responseText_URL).into(mImage_bg);
                        mSwipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }



    //在UI上显示天气信息
    public void showWeatherInfo(Weather weather) {

        mText_title.setText(weather.basic.cityName); //城市名
        mText_now.setText(weather.now.tmp +"°C");  //当前温度
        mText_upTime.setText(weather.basic.update.UpdataTime); //更新时间
        mText_fl.setText(weather.now.wind.sc+"级"); // 风力
        mText_dy.setText(weather.now.cond.txt); //天气情况

        mText_cy.setText(weather.suggestion.drsg.brf);//穿衣指数
        mText_ssd.setText(weather.suggestion.comf.brf);//舒适度
        mText_yd.setText(weather.suggestion.sport.brf);//运动指数
        mText_gm.setText(weather.suggestion.flu.brf);//感冒指数
        mText_xc.setText(weather.suggestion.cw.brf); //洗车指数
        mText_lx.setText(weather.suggestion.trav.brf);//旅行指数
        String aqi = weather.aqi.city.aqi;
        String pm25 = weather.aqi.city.pm25;
        mText_aqi.setText(aqi);  //AQI
        mText_pm25.setText(pm25); //PM2.5
        Integer AQI = Integer.valueOf(aqi);
        Integer PM25 = Integer.valueOf(pm25);
        if (AQI<=50){
            mText_aqi.setTextColor(ContextCompat.getColor(this,R.color.colorPM25_1));
        }else if (AQI>50&&AQI<=100){
            mText_aqi.setTextColor(ContextCompat.getColor(this,R.color.colorPM25_2));
        }else if (AQI>100&&AQI<=150){
            mText_aqi.setTextColor(ContextCompat.getColor(this,R.color.colorPM25_3));
        }else if (AQI>150&&AQI<=200){
            mText_aqi.setTextColor(ContextCompat.getColor(this,R.color.colorPM25_4));
        }else if (AQI>200&&AQI<=300){
            mText_aqi.setTextColor(ContextCompat.getColor(this,R.color.colorPM25_5));
        }else if (AQI>300){
            mText_aqi.setTextColor(ContextCompat.getColor(this,R.color.colorPM25_6));
        }
        if (PM25<=35){
            mText_pm25.setTextColor(ContextCompat.getColor(this,R.color.colorPM25_1));
        }else if (PM25>35&&PM25<=75){
            mText_pm25.setTextColor(ContextCompat.getColor(this,R.color.colorPM25_2));
        }else if (PM25>75&&PM25<=115){
            mText_pm25.setTextColor(ContextCompat.getColor(this,R.color.colorPM25_3));
        }else if (PM25>115&&PM25<=150){
            mText_pm25.setTextColor(ContextCompat.getColor(this,R.color.colorPM25_4));
        }else if (PM25>150&&PM25<=250){
            mText_pm25.setTextColor(ContextCompat.getColor(this,R.color.colorPM25_5));
        }else if (AQI>300||PM25>250){
            mText_pm25.setTextColor(ContextCompat.getColor(this,R.color.colorPM25_6));
        }


        List<String>forecastList = new ArrayList<>();
        List<String>Cond_Code=new ArrayList<>();
        List<String>Cond_Text=new ArrayList<>();
        mLinearLayout.removeAllViews();//移除LinearLayout中的所有控件
        for (Weather_Forecast forecast:weather.weather_forecasts){ //遍历7天天气情况
            int max = forecast.tmp.max;//最高温度
            int min = forecast.tmp.min;//最低温度
            int uv = forecast.uv; //紫外线
            int code_d = forecast.cond.code_d; //天气对应的图片id
            String text_d = forecast.cond.txt_d; //天气情况
            forecastList.add(max+"°C"+"～"+min+"°C"); //将最高，最低温度放到数组中

            Cond_Text.add(text_d);
            Cond_Code.add(code_d+"");


            View mView= LayoutInflater.from(this).inflate(R.layout.forecast_item,null);
            TextView mText_date = (TextView) mView.findViewById(R.id.textView_forecast_date);
            TextView mText_tmp = (TextView) mView.findViewById(R.id.textView_forecast_tmp);
            TextView mText_cond = (TextView) mView.findViewById(R.id.textView_forecast_cond);
            TextView mText_uv = (TextView) mView.findViewById(R.id.textView_forecast_uv);
            ImageView mImageView = (ImageView) mView.findViewById(R.id.image_forecast_cond);
            String date = forecast.date; //日期
            String week = getWeek(date);//调用getWeek()方法将日期转换成星期
            mText_date.setText(week);

            mText_tmp.setText(max+"°C"+"～"+min+"°C");
            mText_cond.setText(text_d);

            if (uv<3){
                mText_uv.setText("UV:"+" 一级"+"  最弱");
                mText_uv.setBackground(ContextCompat.getDrawable(this,R.drawable.text_bg_zr));
            }else if (uv>2&&uv<5){
                mText_uv.setText("UV:"+" 二级"+"  弱");
                mText_uv.setBackground(ContextCompat.getDrawable(this,R.drawable.text_bg_r));
            }else if (uv>4&&uv<7){
                mText_uv.setText("UV:"+" 三级"+"  中等");
                mText_uv.setBackground(ContextCompat.getDrawable(this,R.drawable.text_bg_zd));
            }else if (uv>6&&uv<10){
                mText_uv.setText("UV:"+" 四级"+"  较强");
                mText_uv.setBackground(ContextCompat.getDrawable(this,R.drawable.text_bg_q));
            }else if (uv>9){
                mText_uv.setText("UV:"+" 五级"+"  最强");
                mText_uv.setBackground(ContextCompat.getDrawable(this,R.drawable.text_bg_hq));
            }
            ApplicationInfo info=getApplicationInfo();
            int condImage = getResources().getIdentifier("t"+code_d,"mipmap",info.packageName);
            mImageView.setImageResource(condImage);

            mLinearLayout.addView(mView);
        }
        mText_today.setText(forecastList.get(0)); //今天温度
        mText_tomorrow.setText(forecastList.get(1));//明天温度

        mText_todayInfo.setText(Cond_Text.get(0));
        mText_tomorrowInfo.setText(Cond_Text.get(2));
        String todayCode = Cond_Code.get(0);
        String tomorrowCode = Cond_Code.get(1);
        mImage_today.setImageResource(getResources().getIdentifier("t"+todayCode,"mipmap",getApplicationInfo().packageName));
        mImage_tomorrow.setImageResource(getResources().getIdentifier("t"+tomorrowCode,"mipmap",getApplicationInfo().packageName));


        //设置天气所对应的图片
        int code = weather.now.cond.code;
        ApplicationInfo info = getApplicationInfo();
        int resID = getResources().getIdentifier("t"+code,"mipmap",info.packageName);
        mImage_dy.setImageResource(resID);


    }

    //初始化UI控件
    private void initView() {
        mText_title = (TextView) findViewById(R.id.textView_Title);//标题位置
        mText_fl = (TextView) findViewById(R.id.text_FL);//风力
        mText_now = (TextView) findViewById(R.id.textView_Now);//当前温度
        mText_today = (TextView) findViewById(R.id.textView_Today);//今天温度
        mText_todayInfo = (TextView) findViewById(R.id.textView_TodayInfo);//今天天气信息
        mText_tomorrow = (TextView) findViewById(R.id.textView_Tomorrow);//明天温度
        mText_tomorrowInfo = (TextView) findViewById(R.id.textView_TomorrowInfo);//明天天气信息
        mText_upTime = (TextView) findViewById(R.id.textView_UpTime);//更新时间
        mText_dy = (TextView) findViewById(R.id.textView_DY);//当前天气状况

        mText_aqi = (TextView) findViewById(R.id.textView_AQI);//AQI
        mText_pm25 = (TextView) findViewById(R.id.textView_PM25);//PM2.5
        mText_ssd = (TextView) findViewById(R.id.text_ssd);//舒适度
        mText_cy = (TextView) findViewById(R.id.text_cy);//穿衣
        mText_yd = (TextView) findViewById(R.id.text_yd);//运动
        mText_gm = (TextView) findViewById(R.id.text_gm);//感冒
        mText_xc = (TextView) findViewById(R.id.text_xc);//洗车
        mText_lx = (TextView) findViewById(R.id.text_lx);//旅行


        mButton_add = (Button) findViewById(R.id.button_Add);
        mButton_more = (Button) findViewById(R.id.button_More);

        mImage_dy = (ImageView) findViewById(R.id.imageView_DY);
        mImage_bg = (ImageView) findViewById(R.id.imageView_Bg);
        mImage_today = (ImageView) findViewById(R.id.image_Today);
        mImage_tomorrow = (ImageView)findViewById(R.id.image_Tomorrow);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    }

    //按钮点击事件
    private void initButtonListener(){
        mButton_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        mButton_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WeatherActivity.this,Weather_More.class);
                startActivity(intent);
            }
        });
    }
    //下拉刷新监听事件
    private void initSwipeRefreshListener() {
        mSwipeRefresh.setColorSchemeColors(Color.parseColor("#cc0033"));
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                SharedPreferences prefs = getSharedPreferences("weatherInfo",MODE_PRIVATE);
                String weather_id = prefs.getString("weather_ID", null);
                requestWeatherInfo(weather_id);
            }
        });
    }


    //获取星期几
    private String getWeek(String date){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");

        Calendar instance = Calendar.getInstance();
        String week="周";
        try {
            Date parse = format.parse(date);
            long time = parse.getTime();
            instance.setTime(format.parse(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==1){
            week+="日";
            return week;
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==2){
            week+="一";
            return week;
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==3){
            week+="二";
            return week;
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==4){
            week+="三";
            return week;
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==5){
            week+="四";
            return week;
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==6){
            week+="五";
            return week;
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==7){
            week+="六";
            return week;
        }
        return null;
    }

    @Override//onResume()方法确保每次打开应用时都是最新的天气信息(暂时无效)
    protected void onResume() {
        SharedPreferences prefs = getSharedPreferences("weatherInfo",MODE_PRIVATE);
        String weatherInfo = prefs.getString("weatherResponse", null);
        if (!TextUtils.isEmpty(weatherInfo)){
            Weather weather = ParserUtil.parserWeather(weatherInfo);
            showWeatherInfo(weather);
        }
        super.onResume();
    }


    @Override//重写onDestroy()确保 应用关闭时解除服务绑定
    protected void onDestroy() {
        unbindService(serviceConnection);//解除服务绑定
        super.onDestroy();
    }




}
