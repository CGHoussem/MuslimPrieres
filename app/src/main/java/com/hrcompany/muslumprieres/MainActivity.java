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
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Prayer> arrayOfPrayers = new ArrayList<Prayer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        double longitude = 36.806495; // latitude of Tunis, Tunisia
        double latitude = 10.181532; // longitude of Tunis, Tunisia
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

        /*
        TextView txtCurrentDate = (TextView) findViewById(R.id.txtCurrentDate);
        long d = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM");
        txtCurrentDate.setText(sdf.format(d));

        new ReadPrayers().execute("http://muslimsalat.com/daily.json?key=39b0000720e7d53e14bdbabc5aa03a5e");

        PrayerAdapter adapter = new PrayerAdapter(this, arrayOfPrayers);
        ListView listPrayers = (ListView) findViewById(R.id.listPrayers);
        listPrayers.setAdapter(adapter);*/
    }

    public void gotoTasbihActivity(View view) {
        Intent intent = new Intent(this, TasbihActivity.class);
        startActivity(intent);
    }

    /*private class ReadPrayers extends AsyncTask<String, String, String> {
        private ProgressDialog pd;

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage(getString(R.string.patientez_message_text));
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            SharedPreferences prayersFile;

            if (pd.isShowing())
                pd.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray items = jsonObject.getJSONArray("items");
                JSONObject prayers = items.optJSONObject(0);

                // LOAD times FROM JSON (internet)
                arrayOfPrayers.add(new Prayer("Fajr", prayers.getString("fajr")));
                arrayOfPrayers.add(new Prayer("Shurooq", prayers.getString("shurooq")));
                arrayOfPrayers.add(new Prayer("Dhuhur", prayers.getString("dhuhr")));
                arrayOfPrayers.add(new Prayer("Asr", prayers.getString("asr")));
                arrayOfPrayers.add(new Prayer("Maghrib", prayers.getString("maghrib")));
                arrayOfPrayers.add(new Prayer("Isha", prayers.getString("isha")));

                // SAVE TO INTERNAL FILE FOR LATER USE
                prayersFile = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prayersFile.edit();
                editor.putString("Fajr", prayers.getString("fajr"));
                editor.putString("Shurooq", prayers.getString("shurooq"));
                editor.putString("Dhuhur", prayers.getString("dhuhr"));
                editor.putString("Asr", prayers.getString("asr"));
                editor.putString("Maghrib", prayers.getString("maghrib"));
                editor.putString("Isha", prayers.getString("isha"));
                editor.apply();
            } catch (NullPointerException e) {
                // Fires only when there is no internet connection
                // LOAD prayers FROM file (local)
                prayersFile = getPreferences(Context.MODE_PRIVATE);
                arrayOfPrayers.add(new Prayer("Fajr", prayersFile.getString("Fajr", "")));
                arrayOfPrayers.add(new Prayer("Shurooq", prayersFile.getString("Shurooq", "")));
                arrayOfPrayers.add(new Prayer("Dhuhur", prayersFile.getString("Dhuhur", "")));
                arrayOfPrayers.add(new Prayer("Asr", prayersFile.getString("Asr", "")));
                arrayOfPrayers.add(new Prayer("Maghrib", prayersFile.getString("Maghrib", "")));
                arrayOfPrayers.add(new Prayer("Isha", prayersFile.getString("Isha", "")));

                // Inform user that there is no internet connection
                Toast.makeText(MainActivity.this, "No Internet Connection !", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/

}
