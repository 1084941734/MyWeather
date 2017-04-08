package com.android.mytest.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.mytest.myweather.gson.Weather;
import com.android.mytest.myweather.gson.Weather_Forecast;
import com.android.mytest.myweather.util.HttpUtil;
import com.android.mytest.myweather.util.ParserUtil;


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

import static android.R.attr.x;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        initButtonListener();
        Intent intent=getIntent();
        String weather_id = intent.getStringExtra("weather_ID");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherID = preferences.getString("weather_ID", null);

        if (weatherID!=null&&weather_id==null){
            requestWeatherInfo(weatherID);
        }else {
            requestWeatherInfo(weather_id);
        }
    }

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
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    edit.putString("weather_ID",weather.basic.cityID);
                    edit.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeatherInfo(weather);
                        }
                    });
                }
            }
        });
    }
    //在UI上显示天气信息
    private void showWeatherInfo(Weather weather) {
        mText_title.setText(String.valueOf(weather.basic.cityName));
        mText_now.setText(String.valueOf(weather.now.tmp)+"°C");
        mText_upTime.setText(String.valueOf(weather.basic.update.UpdataTime));
        mText_fl.setText(String.valueOf(weather.now.wind.sc));
        mText_dy.setText(String.valueOf(weather.now.cond.txt));
        List<String>ForecastList=new ArrayList<>();

        mLinearLayout.removeAllViews();
        for (Weather_Forecast forecast:weather.weather_forecasts){
            int max = forecast.tmp.max;
            int min = forecast.tmp.min;
            int uv = forecast.uv;
            int code_d = forecast.cond.code_d;
            String text_d = forecast.cond.text_d;

            ForecastList.add(max+"°C"+"～"+min+"°C");

            View mView= LayoutInflater.from(this).inflate(R.layout.forecast_item,null);
            TextView mText_date = (TextView) mView.findViewById(R.id.textView_forecast_date);
            TextView mText_tmp = (TextView) mView.findViewById(R.id.textView_forecast_tmp);
            TextView mText_cond = (TextView) mView.findViewById(R.id.textView_forecast_cond);
            TextView mText_uv = (TextView) mView.findViewById(R.id.textView_forecast_uv);
            ImageView mImageView = (ImageView) findViewById(R.id.image_forecast_cond);
            String date = forecast.date;
            String week = getWeek(date);
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
            mLinearLayout.addView(mView);


        }
        mText_today.setText(ForecastList.get(0)); //今天温度
        mText_tomorrow.setText(ForecastList.get(1));//明天温度





        //设置天气所对应的图片
        int code = weather.now.cond.code;
        ApplicationInfo info = getApplicationInfo();
        int resID = getResources().getIdentifier("t"+code,"mipmap",info.packageName);
        mImage_dy.setImageResource(resID);

    }

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

        mButton_add = (Button) findViewById(R.id.button_Add);

        mImage_dy = (ImageView) findViewById(R.id.imageView_DY);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);



    }

    private void initButtonListener(){
        mButton_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private String getWeek(String date){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");

        Calendar instance = Calendar.getInstance();
        String week="周";
        try {
            Date parse = format.parse(date);
            long time = parse.getTime();
            Log.e("getWeek: ", parse+"\n"+time);
            instance.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==1){
            week+="日";
            Log.e("getWeek: ",week );
            return week;
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==2){
            week+="一";
            Log.e("getWeek: ",week );
            return week;
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==3){
            week+="二";
            Log.e("getWeek: ",week );
            return week;
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==4){
            week+="三";
            Log.e("getWeek: ",week );
            return week;
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==5){
            week+="四";
            Log.e("getWeek: ",week );
            return week;
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==6){
            week+="五";
            Log.e("getWeek: ",week );
            return week;
        }
        if (instance.get(Calendar.DAY_OF_WEEK)==7){
            week+="六";
            Log.e("getWeek: ",week );
            return week;
        }

        return null;
    }
}
