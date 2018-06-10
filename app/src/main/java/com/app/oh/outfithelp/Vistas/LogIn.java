package com.app.oh.outfithelp.Vistas;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.Utilidades.Secret;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;
import com.app.oh.outfithelp.Utilidades.WebService;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class LogIn extends Activity {

    private static final String ip = "http://104.210.40.93/";
    private String url;
    private String res;
    private EditText ETCorreoElectronico;
    private EditText ETContraseña;
    private Button BTIniciarSesion;
    private TextView TVInfo;
    private TextView TVRegistrarse;
    private String email;
    private String pass;

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        ETCorreoElectronico = findViewById(R.id.ETCorreoElectronico);
        ETContraseña = findViewById(R.id.ETContraseña);
        BTIniciarSesion = findViewById(R.id.BTIniciarSesion);
        TVInfo = findViewById(R.id.TVInfo);
        TVRegistrarse = findViewById(R.id.TVRegistrate);
        String correo = PreferencesConfig.getInstancia(this).getFromSharedPrefs("Correo");
        if (!correo.equals("NULL"))
        {
            ETCorreoElectronico.setText(correo);
        }
        BTIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnviarDatos();
            }
        });
        TVRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntent = new Intent(LogIn.this, SignIn.class);
                startActivity(miIntent);
                LogIn.this.finish();
            }
        });
    }
    public void EnviarDatos () {
        email = ETCorreoElectronico.getText().toString();
        pass = ETContraseña.getText().toString();
        String secret = Secret.getInstancia(this).code(email, pass);
        logIn(secret);
    }
    public void logIn(final String secret)
    {
        url = ip + "WebService.asmx/LogIn";
        StringRequest logIn = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                respuesta(response);
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
        logIn.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,3,1));
        VolleySingleton.getInstancia(LogIn.this).agregarACola(logIn);
    }
    public void respuesta (String response)
    {
        String respuesta = response.substring(67, response.length()-9);
        JSONArray usuario;
        try {
            usuario = new JSONArray(respuesta);
            if (!usuario.getString(0).equals("Exito")) {
                TVInfo.setText(respuesta);
                ETCorreoElectronico.setText("");
                ETContraseña.setText("");
            }
            else {
                String secret;
                secret = Secret.getInstancia(LogIn.this).code(usuario.getString(1),pass);
                PreferencesConfig.getInstancia(LogIn.this).agregarASharedPrefs(OutfitHelp.SECRET, secret);
                PreferencesConfig.getInstancia(LogIn.this).agregarASharedPrefs(OutfitHelp.CORREO, email);
                PreferencesConfig.getInstancia(LogIn.this).agregarASharedPrefs(OutfitHelp.SEXO, usuario.getString(2));
                PreferencesConfig.getInstancia(LogIn.this).agregarASharedPrefs(OutfitHelp.USERNAME, usuario.getString(1));
                PreferencesConfig.getInstancia(LogIn.this).agregarASharedPrefs(OutfitHelp.SESION_INICIADA, "true");
                Intent miIntent = new Intent(this, OutfitHelp.class);
                startActivity(miIntent);
                LogIn.this.finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error en la conexión", Toast.LENGTH_SHORT).show();
        }


    }
}
