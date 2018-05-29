package com.app.oh.outfithelp.Utilidades;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Grecia on 29/05/2018.
 */

public class Localizacion {
    private static Localizacion miInstancia;
    private static Context miContext;
    private static final String IP = "104.219.40.93/";
    private FusedLocationProviderClient mFusedLocationClient;
    private double longitud;
    private double latitud;
    private String localizacion;

    public Localizacion() {
    }

    public Localizacion(Context c) {
        miContext = c;
    }

    public static synchronized Localizacion getInstancia(Context context) {
        if (miInstancia == null) {
            miInstancia = new Localizacion(context);
        }
        return miInstancia;
    }

    public String getLocalizacion() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(miContext);
        if (ActivityCompat.checkSelfPermission(miContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(miContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) miContext, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitud = location.getLatitude();
                    longitud = location.getLongitude();
                    mandarLocalizacion(latitud, longitud);
                    localizacion = latitud + " " + longitud;
                }
            }
        });
        return localizacion;
    }
    public void mandarLocalizacion (final double latitud, final double longitud)
    {
        String url = IP + "WebService.asmx/setLocalizacion";
        StringRequest localizacion = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(miContext, "Oops! Error al mandar localizacion", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("secret", PreferencesConfig.getInstancia(miContext).getFromSharedPrefs("Secret"));
                params.put("latitud", ""+latitud);
                params.put( "longitud", ""+longitud);
                return params;
            }
        };
    }
}
