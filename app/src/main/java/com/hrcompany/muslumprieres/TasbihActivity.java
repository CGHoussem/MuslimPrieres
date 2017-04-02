package com.hrcompany.muslumprieres;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class TasbihActivity extends AppCompatActivity {

    private TextView countTextView;
    private TextView totalTextView;
    private static int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasbih);

        countTextView = (TextView) findViewById(R.id.countTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
    }

    public void addCount(View view) {
        String ch = countTextView.getText().toString();
        int count = Integer.parseInt(ch.substring(0, ch.indexOf("/")));
        StringBuilder resultat = new StringBuilder();
        if (count == 33) {
            count = 1;
            resultat.append(count).append("/33");
        } else {
            count++;
            resultat.append(count).append("/33");
        }
        countTextView.setText(resultat.toString());

        total++;
        totalTextView.setText("Total: " + total);

        // vibrate
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
        v.vibrate(50);
    }

    public void resetCount(View view) {
        total = 0;
        countTextView.setText("0/33");
        totalTextView.setText("Total: 0");
    }

    public void closeActivity(View view) {
        finish();
    }


}
