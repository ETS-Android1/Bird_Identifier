package com.example.bird_identifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


public class splash_screen extends AppCompatActivity {
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        txt=findViewById(R.id.apptitle);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.status));
        Animation anim= AnimationUtils.loadAnimation(this,R.anim.anim);
        txt.startAnimation(anim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                finish();
            }
        },5000);
    }
}