package com.hrcompany.muslumprieres;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class TasbihActivity extends AppCompatActivity {

    private TextView countTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasbih);

        countTextView = (TextView) findViewById(R.id.countTextView);
    }

    public void addCount(View view) {
        String ch = countTextView.getText().toString();
        int count = Integer.parseInt(ch.substring(0, ch.indexOf("/")));
        StringBuilder resultat = new StringBuilder();
        if (count + 1 != 33) {
            count++;
            resultat.append(count).append("/33");
        } else {
            resultat.append("0/33");
        }
        countTextView.setText(resultat.toString());

        // vibrate
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
        v.vibrate(100);
    }

    public void closeActivity(View view) {
        finish();
    }
}
