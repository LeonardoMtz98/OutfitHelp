package com.app.oh.outfithelp.Utilidades;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Esta clase contiene los metodos necesarios para instanciar una sola vez la clase y escribir y leer
 * variables en un archivo local que permanece integro aun cuando la aplicacion no este activa.
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
    /**
     * Instancia la clase con el archivo que siempre se utilizara, segun el contexto por el que sea llamado
     * por primera vez
     * @param c el contexto desde el cual se llama al metodo
     * @return la instancia de la clase que se genero o ya existia.
     */
    public static synchronized PreferencesConfig getInstancia(Context c) {
        if (miInstancia == null) {
            miInstancia = new PreferencesConfig(c);
        }
        return miInstancia;
    }

    /**
     * Agrega una variable al archivo de SharedPreferences que se genero junto con la instancia
     * @param nombre el nombre de la variable que se agregara
     * @param valor el valor de la variable que se agregara
     */
    public void agregarASharedPrefs(String nombre, String valor){
        spEditor.putString(nombre, valor);
        spEditor.commit();
    }

    /**
     * Obtiene una variable del archivo SharedPreferences que se genero junto con la instancia
     * @param nombre el nombre de la variable que se quiere obtener
     * @return el valor de la variable requerida o "NULL" si no existe
     */
    public String getFromSharedPrefs(String nombre) {
        return sp.getString(nombre, "NULL");

    }
}
