package com.app.oh.outfithelp.Vistas;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;

import java.lang.reflect.Method;

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
                StringRequest primeraConexion = new StringRequest(Request.Method.POST, OutfitHelp.url + "getPaises", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (PreferencesConfig.getInstancia(SplashScreen.this).getFromSharedPrefs("Secret").equals("NULL")) {
                            Intent miIntent = new Intent(SplashScreen.this, LogIn.class);
                            startActivity(miIntent);
                            SplashScreen.this.finish();
                        } else {
                            Intent miIntent = new Intent(SplashScreen.this, OutfitHelp.class);
                            startActivity(miIntent);
                            SplashScreen.this.finish();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashScreen.this);
                        builder.setMessage("Hay problemas con la conexion a internet, por favor asegurate de tener una conexion a internet y que OutfitHelp tenga permisos de internet ;D");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                System.exit(0);
                            }
                        });
                        builder.create();
                        builder.show();
                    }
                });
                VolleySingleton.getInstancia(SplashScreen.this).agregarACola(primeraConexion);
            }
        },4000);
    }
}
