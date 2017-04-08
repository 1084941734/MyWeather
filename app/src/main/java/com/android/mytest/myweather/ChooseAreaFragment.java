package com.android.mytest.myweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.mytest.myweather.db.City;
import com.android.mytest.myweather.db.County;
import com.android.mytest.myweather.db.Province;
import com.android.mytest.myweather.util.HttpUtil;
import com.android.mytest.myweather.util.ParserUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 12701 on 2017-04-06.
 */

public class ChooseAreaFragment extends Fragment {


    private List<String>mList=new ArrayList<>();
    private List<Province>mProvinceList;//省
    private List<City>mCityList;       //城市
    private List<County>mCountyList ;   //区县
    private ArrayAdapter mAdapter;
    private ListView mListView;

    private int ProvincePage=0;
    private int CityPage=1;
    private int CountyPage=2;
    private int NowPage=ProvincePage;

    private Province selectedProvince; //选中的省
    private City selectedCity;         //选中的城市
    private County selectedCounty;      //选中的区县

    private ProgressDialog dialog;
    private Button mButton_back;
    private EditText mEditText;
    private TextView mText_city;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_choose_area, container, false);
        mButton_back = (Button) mView.findViewById(R.id.button_back);
        mText_city = (TextView) mView.findViewById(R.id.textView_City);
        mListView = (ListView) mView.findViewById(R.id.listView);
        mAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,mList);
        mListView.setAdapter(mAdapter);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (NowPage==ProvincePage) {
                    selectedProvince= mProvinceList.get(position);
                    queryCity();
                }else if (NowPage==CityPage) {
                    selectedCity=mCityList.get(position);
                    queryCounty();
                }else if (NowPage==CountyPage) {
                    String weatherID = mCountyList.get(position).getWeatherID();
                    if (getActivity() instanceof MainActivity){
                        Intent intent=new Intent(getContext(),WeatherActivity.class);
                        intent.putExtra("weather_ID",weatherID);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity=(WeatherActivity)getActivity();
                        activity.mDrawerLayout.closeDrawers();
                        activity.requestWeatherInfo(weatherID);
                        queryProvince();
                    }

                }
            }
        });
        queryProvince();
        mButton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NowPage==CountyPage){
                    queryCity();
                }else if (NowPage==CityPage){
                    queryProvince();
                }
            }
        });
    }


    public void queryProvince() {
        mText_city.setText("选择城市");
        mButton_back.setVisibility(View.GONE);
        mProvinceList= DataSupport.findAll(Province.class);
        if (mProvinceList.size()>0){
            mList.clear();
            for (Province province:mProvinceList){
                mList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            NowPage = ProvincePage; //当前是省页面
        }else {
            String URL="http://guolin.tech/api/china";
            queryFormServer(URL,"provincePage");
        }
    }

    private void queryCity() {
        mButton_back.setVisibility(View.VISIBLE);
        mText_city.setText(selectedProvince.getProvinceName());
        mCityList=DataSupport.where("provinceID=?",
                String.valueOf(selectedProvince.getprovinceId())).find(City.class);
        if (mCityList.size()>0){
            mList.clear();
            for (City city:mCityList){
                mList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            NowPage=CityPage;
        }else {
            String URL="http://guolin.tech/api/china"+"/"+selectedProvince.getprovinceId();
            queryFormServer(URL,"cityPage");
        }

    }
    private void queryCounty() {
        mButton_back.setVisibility(View.VISIBLE);
        mText_city.setText(selectedCity.getCityName());
        mCountyList=DataSupport.where("cityID=?", String.valueOf(selectedCity.getcityId())).find(County.class);
        if (mCountyList.size()>0){
            mList.clear();
            for (County county:mCountyList){
                mList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            NowPage=CountyPage;
        }else {
            String URL="http://guolin.tech/api/china"+"/"+selectedProvince.getprovinceId()+"/"+selectedCity.getcityId();
            queryFormServer(URL,"countyPage");
        }
    }

    private void queryFormServer(String Url, final String page) {
        showProgress();
        HttpUtil.sendOkHttpRequest(Url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                boolean result=false;
                if (responseString!=null&&page.equals("provincePage")){
                    result = ParserUtil.parserProvince(responseString);
                }else if (responseString!=null&&page.equals("cityPage")){
                    result = ParserUtil.parserCity(responseString,selectedProvince.getprovinceId());
                }else if (responseString!=null&&page.equals("countyPage")){
                    result = ParserUtil.parserCounty(responseString,selectedCity.getcityId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgress();
                            if (page.equals("provincePage")){
                                queryProvince();
                            }else if (page.equals("cityPage")){
                                queryCity();
                            }else if (page.equals("countyPage")){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }
    private void showProgress(){
        if (dialog==null) {
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("正在查询...");
            dialog.setCanceledOnTouchOutside(false);//允许用户关闭进度对话框
            dialog.show();
        }
    }
    private void closeProgress(){
        if (dialog!=null){
            dialog.dismiss();//关闭进度条对话框
        }
    }



}
