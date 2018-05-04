package com.app.oh.outfithelp.Vistas;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Bundle;
import android.widget.TextView;

import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.R;

public class SplashScreen extends Activity {
    TextView tvLogo;
    Typeface tF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PreferencesConfig.getInstancia(SplashScreen.this).getFromSharedPrefs("secret").equals("NULL"))
                {
                    Intent miIntent = new Intent(SplashScreen.this, LogIn.class);
                    startActivity(miIntent);
                    SplashScreen.this.finish();
                }
                else
                {
                    Intent miIntent = new Intent(SplashScreen.this, OutfitHelp.class);
                    startActivity(miIntent);
                    SplashScreen.this.finish();
                }
            }
        },4000);
    }
}
