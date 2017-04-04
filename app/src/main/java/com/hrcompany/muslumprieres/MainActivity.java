package com.hrcompany.muslumprieres;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Prayer> arrayOfPrayers = new ArrayList<Prayer>();
    private TextClock txtClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtClock = (TextClock) findViewById(R.id.txtClock);

        ShowDate();
        calculateAndShowTimes();
        NotifyInTimes();
    }

    public void gotoTasbihActivity(View view) {
        Intent intent = new Intent(this, TasbihActivity.class);
        startActivity(intent);
    }

    private void ShowDate(){
        // Affichage de la date d'aujourd'hui
        TextView txtCurrentDate = (TextView) findViewById(R.id.txtCurrentDate);
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM");
        txtCurrentDate.setText(dateFormat.format(currentTimeMillis));
    }
    private void calculateAndShowTimes(){
        /*double latitude = 36.806495; // Latitude of Tunis, Tunisia
        double longitude = 10.181532; // Longitude of Tunis, Tunisia*/

        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            double timezone = (Calendar.getInstance().getTimeZone().getOffset(Calendar.getInstance().getTimeInMillis())) / (1000 * 60 * 60);
            PrayTime prayers = new PrayTime();

            // Check Device Time Format
            if (txtClock.is24HourModeEnabled())
                prayers.setTimeFormat(prayers.Time24);
            else
                prayers.setTimeFormat(prayers.Time12);
            prayers.setCalcMethod(prayers.MWL);
            prayers.setAsrJuristic(prayers.Shafii);
            prayers.setAdjustHighLats(prayers.None);
            int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr, Sunset,Maghrib,Isha}
            prayers.tune(offsets);

            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);

            ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal, latitude, longitude, timezone);
            ArrayList<String> prayerNames = prayers.getTimeNames();

            for (int i = 0; i < prayerTimes.size(); i++) {
                arrayOfPrayers.add(new Prayer(prayerNames.get(i), prayerTimes.get(i)));
            }
            gps.stopUsingGPS();
        } else {
            gps.showSettingsAlert();
        }

        // Affichage de la liste des priéres
        PrayerAdapter adapter = new PrayerAdapter(this, arrayOfPrayers);
        ListView listPrayers = (ListView) findViewById(R.id.listPrayers);
        listPrayers.setAdapter(adapter);
    }
    private void NotifyInTimes() {
        SetAthan(arrayOfPrayers.get(0)); // Fajr Alarm
        SetAthan(arrayOfPrayers.get(2)); // Dhuhur Alarm
        SetAthan(arrayOfPrayers.get(3)); // Asr Alarm
        SetAthan(arrayOfPrayers.get(5)); // Maghrib Alarm
        SetAthan(arrayOfPrayers.get(6)); // Isha Alarm
    }
    private void SetAthan(Prayer p){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        String time = p.getPrayerTime();
        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minute;
        if (time.contains("am") || time.contains("pm"))
            minute = Integer.parseInt(time.substring(time.indexOf(":") + 1, time.indexOf(" ")));
        else
            minute = Integer.parseInt(time.substring(time.indexOf(":") + 1));

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("SALAT_NAME", p.getPrayerName());
        intent.putExtra("SALAT_TIME", p.getPrayerTime());

        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        TimeZone currentTimeZone = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(currentTimeZone);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
            calendar.add(Calendar.HOUR_OF_DAY, 24);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }

}
