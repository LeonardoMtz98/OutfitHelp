package com.app.oh.outfithelp.Utilidades;

/**
 * Created by Grecia on 02/05/2018.
 */

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton miInstancia;
    private RequestQueue miColaDePeticiones;
    private static Context miContext;
    private VolleySingleton (Context context)
    {
        miContext = context;
        miColaDePeticiones = getColaDePeticiones();
    }

    public static synchronized VolleySingleton getInstancia(Context context)
    {
        if (miInstancia == null)
        {
            miInstancia = new VolleySingleton(context);
        }
        return miInstancia;
    }

    public RequestQueue getColaDePeticiones() {
        if (miColaDePeticiones == null)
        {
            miColaDePeticiones = Volley.newRequestQueue(miContext.getApplicationContext());
        }
        return  miColaDePeticiones;
    }
    public <T> void agregarACola (Request<T> request) {
        getColaDePeticiones().add(request);
    }
}

