package com.android.mytest.myweather;

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

import com.android.mytest.myweather.db.City;
import com.android.mytest.myweather.db.County;
import com.android.mytest.myweather.db.Province;
import com.android.mytest.myweather.util.HttpUtil;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_choose_area, container, false);
        Button mButton_Back = (Button) mView.findViewById(R.id.button_back);
        Button mButton_Search = (Button) mView.findViewById(R.id.button_search);
        EditText mEditText = (EditText) mView.findViewById(R.id.editText);
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
                    selectedCounty=mCountyList.get(position);

                }

            }
        });
        queryProvince();

    }

    private void queryProvince() {
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
            queryFormServer(URL);
        }
    }

    private void queryCity() {
        mCityList=DataSupport.where("provinceID=?",
                String.valueOf(selectedProvince.getprovinceId())).find(City.class);
        if (mCityList.size()>0){
            for (City city:mCityList){
                mList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            NowPage=CityPage;
        }else {
            String URL="http://guolin.tech/api/china"+selectedProvince.getprovinceId();
            queryFormServer(URL);
        }

    }
    private void queryCounty() {
        mCountyList=DataSupport.where("cityID=?", String.valueOf(selectedCity.getId())).find(County.class);
        if (mCountyList.size()>0){
            for (County county:mCountyList){
                mList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            NowPage=CountyPage;
        }else {
            String URL="http://guolin.tech/api/china"+selectedProvince.getprovinceId()+selectedCity.getId();
            queryFormServer(URL);
        }
    }

    private void queryFormServer(String Url) {
        HttpUtil.sendOkHttpRequest(Url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

}
