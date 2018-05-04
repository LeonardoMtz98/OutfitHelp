package com.app.oh.outfithelp.Vistas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

public class LogIn extends Activity {

    private static final String ip = "http://104.210.40.93/";
    private String url;
    String res;
    EditText ETCorreoElectronico;
    EditText ETContraseña;
    Button BTIniciarSesion;
    TextView TVInfo;
    TextView TVRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        ETCorreoElectronico = findViewById(R.id.ETCorreoElectronico);
        ETContraseña = findViewById(R.id.ETContraseña);
        BTIniciarSesion = findViewById(R.id.BTIniciarSesion);
        TVInfo = findViewById(R.id.TVInfo);
        TVRegistrarse = findViewById(R.id.TVRegistrate);

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
        String email = ETCorreoElectronico.getText().toString();
        String pass = ETContraseña.getText().toString();
        String secret = Secret.getInstancia(this).code(email, pass);
        logIn(secret);
    }
    public void logIn(final String secret)
    {
        url = ip + "WebService.asmx/LogIn";
        StringRequest logIn = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                respuesta(response, secret);
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
        VolleySingleton.getInstancia(LogIn.this).agregarACola(logIn);
    }
    public void respuesta (String response, String secret)
    {
        String respuesta = response.substring(67, response.length()-9);
        if (!respuesta.equals("Exito")) {
            TVInfo.setText(respuesta);
            ETCorreoElectronico.setText("");
            ETContraseña.setText("");
        }
        else {
            PreferencesConfig.getInstancia(LogIn.this).agregarASharedPrefs("Secret", secret);
            Intent miIntent = new Intent(this, OutfitHelp.class);
            startActivity(miIntent);
            LogIn.this.finish();
            //Hola bebe
        }
    }
}
