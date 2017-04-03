package com.hrcompany.muslumprieres;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class PrayerAdapter extends ArrayAdapter<Prayer> {
    PrayerAdapter(@NonNull Context context, @NonNull List<Prayer> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.prayer_row, parent, false);
        }
        TextView prayerName = (TextView) convertView.findViewById(R.id.txtPrayerName);
        TextView prayerTime = (TextView) convertView.findViewById(R.id.txtPrayerTime);

        Prayer prayer = getItem(position);
        if (prayer != null) {
            prayerName.setText(prayer.getPrayerName());
            prayerTime.setText(prayer.getPrayerTime());
        }

        return convertView;
    }
}
