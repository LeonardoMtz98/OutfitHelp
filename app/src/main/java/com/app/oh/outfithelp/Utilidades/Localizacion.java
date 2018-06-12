package com.app.oh.outfithelp.Utilidades;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Grecia on 29/05/2018.
 */

public class Localizacion {
    private static Localizacion miInstancia;
    private static Context miContext;
    private static final String IP = "http://104.210.40.93/";
    private double longitud;
    private double latitud;


    private LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            if (location.getAccuracy() < 100) mandarLocalizacion(latitud, longitud);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public Localizacion() {
    }

    public void iniciarDeteccionUbicacion() {
        if (ActivityCompat.checkSelfPermission(miContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(miContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(miContext, "Activa los permisos de ubicacion por favor", Toast.LENGTH_SHORT).show();
        }
        List<String> providers = locationManager.getAllProviders();
        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }
    public void detenerActualizacionesUbicacion(){
        locationManager.removeUpdates(locationListener);
    }
    public Localizacion(Context c) {
        miContext = c;
        locationManager =  (LocationManager) miContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public static synchronized Localizacion getInstancia(Context context) {
        if (miInstancia == null) {
            miInstancia = new Localizacion(context);
        }
        return miInstancia;
    }

    public void mandarLocalizacion (final double latitud, final double longitud)
    {
        String url = IP + "WebService.asmx/setLocalizacion";
        StringRequest mandarLocalizacion = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
        VolleySingleton.getInstancia(miContext).agregarACola(mandarLocalizacion);
    }
}
