package com.android.mytest.myweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class Weather_More extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather__more);
        intent = new Intent(this,WeatherActivity.class);
        Spinner mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] refreshTimes=getResources().getStringArray(R.array.refreshTimes);
                switch (refreshTimes[position]){
                    case "无":
                        intent.putExtra("refreshTimes",0);
                        break;
                    case "每小时":
                        intent.putExtra("refreshTimes",1);
                        break;
                    case "每3小时":
                        intent.putExtra("refreshTimes",3);
                        break;
                    case "每6小时":
                        intent.putExtra("refreshTimes",6);
                        break;
                    case "每12小时":
                        intent.putExtra("refreshTimes",12);
                        break;
                    case "每24小时":
                        intent.putExtra("refreshTimes",24);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void onClick_Back(View view){

        startActivity(intent);

        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
