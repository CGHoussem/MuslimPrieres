package com.hrcompany.muslumprieres;

/**
 * Created by Houssem on 29/03/2017.
 */

public class Prayer {
    private String prayerName, prayerTime;

    public Prayer(String prayerName, String prayerTime) {
        this.prayerName = prayerName;
        this.prayerTime = prayerTime;
    }

    public String getPrayerName() {
        return prayerName;
    }

    public String getPrayerTime() {
        return prayerTime;
    }
}
