package com.android.mytest.myweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 12701 on 2017-04-06.
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String Url,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(Url).build();
        client.newCall(request).enqueue(callback);
    }
}
