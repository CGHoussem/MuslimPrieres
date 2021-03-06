package com.hrcompany.muslumprieres;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class TasbihActivity extends AppCompatActivity {

    private static int total = 0;
    private static int count = 0;
    private TextView countTextView;
    private TextView totalTextView;

    private Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasbih);

        v = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);

        countTextView = (TextView) findViewById(R.id.countTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);

        countTextView.setText(count + "/33");
        totalTextView.setText("Total: " + total);
    }

    public void addCount(View view) {
        count++;
        total++;

        if (count == 33)
            v.vibrate(100);
        else if (count > 33)
            count = 1;

        countTextView.setText(count + "/33");
        totalTextView.setText("Total: " + total);
    }

    public void resetCount(View view) {
        count = 0;
        total = 0;
        countTextView.setText(count + "/33");
        totalTextView.setText("Total: " + total);
    }

    public void closeActivity(View view) {
        finish();
    }

}
