package com.hrcompany.muslumprieres;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final int FAJR_REQUEST_CODE = 0;
    private final int DHUHUR_REQUEST_CODE = 1;
    private final int ASR_REQUEST_CODE = 2;
    private final int MAGHRIB_REQUEST_CODE = 3;
    private final int ISHA_REQUEST_CODE = 4;

    static ArrayList<Prayer> arrayOfPrayers = new ArrayList<Prayer>();
    private TextClock txtClock;
    private ImageView headerImageView;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayers);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        txtClock = (TextClock) findViewById(R.id.txtClock);
        headerImageView = (ImageView) findViewById(R.id.headerImageView);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        ShowBackgroundHeaderImage();
        ShowDate();
        calculateAndShowTimes();
        NotifyInTimes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_tasbih:
                gotoTasbihActivity();
                return true;
            case R.id.menu_item_qibla:
                gotoQiblaActivity();
                return true;
            case R.id.menu_item_settings:
                gotoSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void gotoTasbihActivity() {
        Intent intent = new Intent(this, TasbihActivity.class);
        startActivity(intent);
    }

    private void gotoQiblaActivity() {

    } //// TODO: 04/04/2017 CREATE QIBLA ACTIVITY

    private void gotoSettingsActivity() {

    } //// TODO: 04/04/2017 CREATE SETTINGS ACTIVITY

    private void ShowBackgroundHeaderImage() {
        int[] backgroundImages = new int[]{R.drawable.mosque_1, R.drawable.quran_1, R.drawable.sibha_1};
        int randomNumber = (int) (Math.random() * 3);
        headerImageView.setImageResource(backgroundImages[randomNumber]);
    }

    private void ShowDate() {
        // Affichage de la date d'aujourd'hui
        TextView txtCurrentDate = (TextView) findViewById(R.id.txtCurrentDate);
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM");
        txtCurrentDate.setText(dateFormat.format(currentTimeMillis));
    }

    private void calculateAndShowTimes() {
        /*double latitude = 36.806495; // Latitude of Tunis, Tunisia
        double longitude = 10.181532; // Longitude of Tunis, Tunisia*/

        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
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
            int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha}
            prayers.tune(offsets);

            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);

            ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal, latitude, longitude, timezone);
            ArrayList<String> prayerNames = prayers.getTimeNames();

            for (int i = 0; i < prayerTimes.size(); i++) {
                arrayOfPrayers.add(new Prayer(prayerNames.get(i), prayerTimes.get(i)));
            }
            arrayOfPrayers.remove(4);

            gps.stopUsingGPS();

            // Affichage de la liste des prières
            PrayerAdapter adapter = new PrayerAdapter(this, arrayOfPrayers);
            ListView listPrayers = (ListView) findViewById(R.id.listPrayers);
            listPrayers.setAdapter(adapter);
        } else {
            gps.showSettingsAlert();
        }

    }

    private void NotifyInTimes() {
        SetAthan(arrayOfPrayers.get(0), FAJR_REQUEST_CODE); // Fajr Alarm
        SetAthan(arrayOfPrayers.get(2), DHUHUR_REQUEST_CODE); // Dhuhur Alarm
        SetAthan(arrayOfPrayers.get(3), ASR_REQUEST_CODE); // Asr Alarm
        SetAthan(arrayOfPrayers.get(4), MAGHRIB_REQUEST_CODE); // Maghrib Alarm
        SetAthan(arrayOfPrayers.get(5), ISHA_REQUEST_CODE); // Isha Alarm
    }

    private void SetAthan(Prayer p, int requestCode) {

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("PRAYER_NAME", p.getPrayerName());
        alarmIntent.putExtra("PRAYER_TIME", p.getPrayerTime());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();

        String prayerTime = p.getPrayerTime();
        String prayerHour = prayerTime.substring(0, prayerTime.indexOf(':'));
        String prayerMinute;

        if (txtClock.is24HourModeEnabled())
            prayerMinute = prayerTime.substring(prayerTime.indexOf(':') + 1);
        else if (prayerHour.contains("am"))
            prayerMinute = prayerTime.substring(prayerTime.indexOf(':') + 1, prayerTime.indexOf(" am"));
        else
            prayerMinute = prayerTime.substring(prayerTime.indexOf(':') + 1, prayerTime.indexOf(" pm"));

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(prayerHour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(prayerMinute));
        calendar.set(Calendar.SECOND, 0);

        long difference = Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis();
        if (difference > 0)
            calendar.add(Calendar.DAY_OF_WEEK, 1);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
