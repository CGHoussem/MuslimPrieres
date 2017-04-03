package com.hrcompany.muslumprieres;

class Prayer {
    private String prayerName, prayerTime;

    Prayer(String prayerName, String prayerTime) {
        this.prayerName = prayerName;
        this.prayerTime = prayerTime;
    }

    String getPrayerName() {
        return prayerName;
    }

    String getPrayerTime() {
        return prayerTime;
    }
}
