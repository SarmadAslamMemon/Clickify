package com.example.clickify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clickify.Login.LoginScreen;
import com.example.clickify.OverLayService.OverlayActivity;
import com.example.clickify.OverLayService.OverlayActivity;
import com.example.clickify.SessionManager.SessionManager;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        SessionManager sm=new SessionManager(this);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("jarvis","check"+  sm.isAdminLoggedIn());
                if(sm.isLoggedIn() && sm.isAdminLoggedIn())
                {

                    Intent intent = new Intent(SplashScreen.this, OverlayActivity.class);
                    intent.putExtra("intent_text", "admin"); // Add extra text
                    startActivity(intent);
                    finish();
                }else if(sm.isLoggedIn() && !sm.isAdminLoggedIn())
                {
                    startActivity(new Intent(SplashScreen.this, OverlayActivity.class));
                    finish();
                }else
                {
                    startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                    finish();
                }
            }
        },2000);


    }
}