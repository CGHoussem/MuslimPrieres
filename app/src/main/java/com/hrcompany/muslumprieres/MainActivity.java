package com.hrcompany.muslumprieres;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Prayer> arrayOfPrayers = new ArrayList<Prayer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtCurrentDate = (TextView) findViewById(R.id.txtCurrentDate);
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM");
        txtCurrentDate.setText(dateFormat.format(currentTimeMillis));

        double latitude = 36.806495; // latitude of Tunis, Tunisia
        double longitude = 10.181532; // longitude of Tunis, Tunisia
        double timezone = (Calendar.getInstance().getTimeZone().getOffset(Calendar.getInstance().getTimeInMillis())) / (1000 * 60 * 60);
        PrayTime prayers = new PrayTime();

        prayers.setTimeFormat(prayers.Time12);
        prayers.setCalcMethod(prayers.MWL);
        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setAdjustHighLats(prayers.None);
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal, latitude, longitude, timezone);
        ArrayList<String> prayerNames = prayers.getTimeNames();

        for (int i = 0; i < prayerTimes.size(); i++) {
            arrayOfPrayers.add(new Prayer(prayerNames.get(i), prayerTimes.get(i)));
        }

        PrayerAdapter adapter = new PrayerAdapter(this, arrayOfPrayers);
        ListView listPrayers = (ListView) findViewById(R.id.listPrayers);
        listPrayers.setAdapter(adapter);
    }

    public void gotoTasbihActivity(View view) {
        Intent intent = new Intent(this, TasbihActivity.class);
        startActivity(intent);
    }

}
