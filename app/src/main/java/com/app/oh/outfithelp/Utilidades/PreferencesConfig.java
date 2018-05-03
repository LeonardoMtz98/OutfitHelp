package com.app.oh.outfithelp.Utilidades;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by LeonardoYGrecia on 01/05/2018.
 */

public class PreferencesConfig {
    private static PreferencesConfig miInstancia;
    private static SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private static Context miContext;

    private PreferencesConfig (Context c)
    {
        miContext = c;
        sp = c.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public static synchronized PreferencesConfig getInstancia(Context c) {
        if (miInstancia == null) {
            miInstancia = new PreferencesConfig(c);
        }
        return miInstancia;
    }

    public void agregarASharedPrefs(String nombre, String valor){
        spEditor.putString(nombre, valor);
        spEditor.commit();
    }
    public String getFromSharedPrefs(String nombre) {
        return sp.getString(nombre, "NULL");

    }
}
