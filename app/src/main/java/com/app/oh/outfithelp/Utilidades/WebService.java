package com.app.oh.outfithelp.Utilidades;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Vistas.LogIn;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Grecia on 02/05/2018.
 */

public class WebService {
    private static WebService miInstancia;
    private static Context miContext;
    private static final String ip = "http://104.210.40.93/";
    private String url;
    private String res;
    private WebService (Context context)
    {
        miContext = context;
    }

    public static synchronized WebService getInstancia(Context context)
    {
        if (miInstancia == null)
        {
            miInstancia = new WebService(context);
        }
        return miInstancia;
    }
    public String logIn(Context c, final String secret)
    {
        url = ip + "WebService.asmx/LogIn";
        StringRequest logIn = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta = response.substring(67, response.length()-9);
                res = respuesta;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("secret", secret);
                return params;
            }
        };
        logIn.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,1,1));
        VolleySingleton.getInstancia(miContext).agregarACola(logIn);
        while (res == null){}
        return res;
    }
    public void getImagenes (View v)
    {
        final View view = v;
        url = ip + "WebService.asmx/getPrendas";
        StringRequest Imagenes = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Guardar(response, view);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Error volley : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Secret",PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Secret"));
                params.put("Categoria", "2");
                return params;
            }
        };
        Imagenes.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,3,1));
        VolleySingleton.getInstancia(view.getContext()).agregarACola(Imagenes);
    }
    public void Guardar (String response, View view){
        String res = response.substring(67,response.length()-9);
        Toast.makeText(view.getContext(), res, Toast.LENGTH_LONG).show();
        JSONArray playeras;
        try {
            playeras = new JSONArray(res);
           /* if (playeras != null)
            {
                Toast.makeText(view.getContext(), "Si entre", Toast.LENGTH_SHORT).show();
                String y = ip + playeras.getString(0);
                final ImageView Pica;
                Pica = view.findViewById(R.id.imagenPicasso);
                Picasso.with(view.getContext()).load(y).into(Pica);
                /*ImageRequest ir = new ImageRequest(y, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Pica.setImageBitmap(response);
                    }
                }, 500, 500, ImageView.ScaleType.CENTER, Bitmap.Config.ALPHA_8, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(view.getContext(), "Error bitmap Volley: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                VolleySingleton.getInstancia(view.getContext()).agregarACola(ir);
            }*/
        } catch (JSONException e) {
            Toast.makeText(view.getContext(), "Error json: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


}
