package com.android.mytest.myweather.util;

import android.text.TextUtils;

import com.android.mytest.myweather.db.City;
import com.android.mytest.myweather.db.County;
import com.android.mytest.myweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 12701 on 2017-04-06.
 */

public class ParserUtil {

    public static boolean parserProvince(String response){//解析省
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray array=new JSONArray(response);
                for (int i=0;i<array.length();i++){
                    JSONObject object=array.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(object.getString("name"));
                    province.setprovinceId(object.getInt("id"));
                    province.save();
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean parserCity(String response,int provinceId){//解析城市
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray array=new JSONArray(response);
                for (int i=0;i<array.length();i++){
                    JSONObject object=array.getJSONObject(i);
                    City city=new City();
                    city.setCityName(object.getString("name"));
                    city.setId(object.getInt("id"));
                    city.setProvinceID(provinceId);
                    city.save();
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean parserCounty(String response,int CityID){//解析区县
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray array=new JSONArray(response);
                for (int i=0;i<array.length();i++){
                    JSONObject object=array.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(object.getString("name"));
                    county.setWeatherID(object.getString("weather_id"));
                    county.setCityID(CityID);
                    county.save();
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
     return false;
    }
}
