package com.jacobandersson.dodskrok;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jacobandersson.dodskrok.cast.GoogleCast;
import com.jacobandersson.dodskrok.playing.PlayingActivity;

public class SplashscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        GoogleCast.getInstance(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashscreenActivity.this, PlayingActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}
