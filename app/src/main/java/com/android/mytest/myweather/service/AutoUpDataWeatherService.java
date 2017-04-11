package com.android.mytest.myweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.android.mytest.myweather.gson.Weather;
import com.android.mytest.myweather.util.HttpUtil;
import com.android.mytest.myweather.util.ParserUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpDataWeatherService extends Service {

    private MyBinder mBinder=new MyBinder();
    //绑定服务时用到
    public class MyBinder extends Binder {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Log.e("onStartCommand: ", "Service启动");
        int mRefreshTime = intent.getIntExtra("refreshTimes", 0);//获取Intent传递的数据(定时任务的时间)，
        //SystemClock.elapsedRealtime()表示从系统当前时间开始计时。设定时间30分钟，
        if (mRefreshTime>0) {//时间大于0 开启定时任务
            AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);//获取定时任务管理者
            //设置意图，(我们需要定时开启这个服务)，因此将意图设置为开启设置个服务
            Intent i=new Intent(this,AutoUpDataWeatherService.class);
            i.putExtra("refreshTimes",mRefreshTime);//将定时任务时间添加到意图中，确保每次启动这个服务时都能获取定时任务的时间，确保无限循环
            PendingIntent pi=PendingIntent.getService(this,1,i,0);//设置悬而未决的意图
            manager.cancel(pi);

            //设置定时启动服务时间(根据用户选择，最少1小时启动一次，或者不开开启定时任务) 1000ms * 60= 6min * 60 = 360min = 1h
            long Timing = SystemClock.elapsedRealtime() +1000*60*60*mRefreshTime;
            //判断系统版本，由于在安卓6.0以上，Alarm定时任变得不太精准，因此需要添加判断
            if (Build.VERSION.SDK_INT>=23) { //版本>=6.0
                manager.setExactAndAllowWhileIdle(2,Timing,pi); //使用setExactAndAllowWhileIdle()方法开启定时任务
            }else {
                manager.set(2, Timing, pi);//通过定时任务管理者开启意图，一个参数Type 设置成 2 表示 开启定时任务时会唤醒CPU
            }

            requestWeatherInfo();//调用requestWeatherInfo()方法.更新天气信息
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void requestWeatherInfo() {
        Log.e("requestWeatherInfo: ", "刷新天气信息");
        SharedPreferences prefs = getSharedPreferences("weatherInfo",MODE_PRIVATE);
        final String weather_ID = prefs.getString("weather_ID", null);
        if (weather_ID != null) {
            String URL = "https://way.jd.com/he/freeweather?city="
                    + weather_ID + "&appkey=7259667a8b27e8181a9e4e7d883cecf6";
            HttpUtil.sendOkHttpRequest(URL, new Callback() {
                @Override//请求失败调用
                public void onFailure(Call call, IOException e) {

                }

                @Override//请求成功调用
                public void onResponse(Call call, Response response) throws IOException {
                    String weatherResponse = response.body().string(); //天气信息 JSON格式
                    Weather weatherInfo = ParserUtil.parserWeather(weatherResponse);
                    //initNotification(weatherInfo);
                    if (weatherInfo!=null) {
                        SharedPreferences.Editor editor= getSharedPreferences("weatherInfo",MODE_PRIVATE).edit();
                        editor.putString("weatherResponse", weatherResponse);//保存天气信息 JSON格式
                        editor.apply();
                        editor.commit();
                        Log.e("onResponse: ","刷新成功" );
                    }
                }
            });

        }
    }

    /*private void initNotification(Weather w) {
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setContentTitle(w.now.tmp+"°C") //通知标题
                .setContentText(w.now.cond.txt)  //通知正文
                .setContentIntent(PendingIntent.getActivities(this,0,new Intent[]{new Intent(this,WeatherActivity.class)},0))//点击通知打开意图
                .setTicker("测试通知来啦")
                .setWhen(System.currentTimeMillis())//设置通知发送时间
                .setPriority(Notification.PRIORITY_MAX)//设置通知优先级，PRIORITY_MAX最高级
                .setOngoing(true)//常驻通知栏，不能通过左右滑动清除通知
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.icon). //设置小图标，状态栏显示
                setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.icon));//设置大图标，通知正文最左边显示
        Notification notification = mBuilder.build();
        manager.notify(1,notification);
    }*/


    @Override
    public void onCreate() {
        Log.d("onCreate: ", "Service创建");
        super.onCreate();
    }
}
