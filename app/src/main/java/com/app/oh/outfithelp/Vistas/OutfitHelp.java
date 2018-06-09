package com.app.oh.outfithelp.Vistas;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.Localizacion;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OutfitHelp extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Inicio.OnFragmentInteractionListener
        , MiArmario.OnFragmentInteractionListener, Comunidad.OnFragmentInteractionListener,
        MisPeticiones.OnFragmentInteractionListener, Favoritos.OnFragmentInteractionListener,
        MostrarRopa.OnFragmentInteractionListener, DetallesRopa.OnFragmentInteractionListener,
        AgregarPeticion.OnFragmentInteractionListener, Recomendacion.OnFragmentInteractionListener,
        AgregarPrenda.OnFragmentInteractionListener, configuracion.OnFragmentInteractionListener, CrearRecomendacion.OnFragmentInteractionListener,
        DetallesPeticion.OnFragmentInteractionListener{
    private View headerNav;
    private TextView TVNivel, TVIndice;
    private int cuentaBacks = 0;
    public static final String SECRET = "Secret";
    public static final String CORREO = "Correo";
    public static final String SEXO  = "Sexo";
    public static final String USERNAME = "Username";
    public static final String NIVEL = "Nivel";
    public static final String INDICE = "Indice";
    public static final String url = "http://104.210.40.93/WebService.asmx/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outfit_help);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                actualizarDatos();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (PreferencesConfig.getInstancia(this).getFromSharedPrefs("Secret").equals("NULL"))
        {
            Intent miIntent = new Intent(this, LogIn.class);
            startActivity(miIntent);
            OutfitHelp.this.finish();
        }
        Fragment mifragment = new Inicio();
        getSupportFragmentManager().beginTransaction().add(R.id.content_outfit_help ,mifragment).commit();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerNav = navigationView.getHeaderView(0);
        TextView TVUsername = headerNav.findViewById(R.id.TVUsername);
        TVUsername.setText(PreferencesConfig.getInstancia(OutfitHelp.this).getFromSharedPrefs(OutfitHelp.USERNAME));
        TVNivel = headerNav.findViewById(R.id.TVNivel);
        TVIndice = headerNav.findViewById(R.id.TVIndice);
        TextView TVEmail = headerNav.findViewById(R.id.TVEmail123);
        TVEmail.setText(PreferencesConfig.getInstancia(OutfitHelp.this).getFromSharedPrefs(OutfitHelp.CORREO));
        try {
            Localizacion.getInstancia(this).iniciarDeteccionUbicacion();
        }catch (SecurityException ex){
            pedirPermisosUbicacion();
        }
    }
    private void pedirPermisosUbicacion() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Por favor activa los servicios de ubicacion y dale permisos a Outfithelp");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", OutfitHelp.this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.create();
        builder.show();
    }
    private void actualizarDatos() {
        int rand = new Random().nextInt();
        if (rand % 2 == 0) {
            if(PreferencesConfig.getInstancia(this).getFromSharedPrefs(NIVEL).equals("NULL")){
                consultarDatos();
            }else {
                TVNivel.setText(PreferencesConfig.getInstancia(this).getFromSharedPrefs(NIVEL));
                TVIndice.setText("IndiceFashion:" + PreferencesConfig.getInstancia(this).getFromSharedPrefs(INDICE));
            }
        }else consultarDatos();
    }

    private void consultarDatos() {
        StringRequest peticionNivel = new StringRequest(Request.Method.POST, url + "getNivel", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta = response.substring(67, response.length() - 9);
                PreferencesConfig.getInstancia(OutfitHelp.this).agregarASharedPrefs(NIVEL, respuesta);
                TVNivel.setText(respuesta);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OutfitHelp.this, "Oops! Error obteniendo nivel", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", PreferencesConfig.getInstancia(OutfitHelp.this).getFromSharedPrefs(USERNAME));
                return params;
            }
        };
        StringRequest peticionIndice = new StringRequest(Request.Method.POST, url + "getIndice", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta = response.substring(67, response.length() - 9);
                PreferencesConfig.getInstancia(OutfitHelp.this).agregarASharedPrefs(INDICE, respuesta);
                TVIndice.setText("IndiceFashion: " + respuesta);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OutfitHelp.this, "Oops! Error obteniendo indice fashion", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", PreferencesConfig.getInstancia(OutfitHelp.this).getFromSharedPrefs(USERNAME));
                return params;
            }
        };
        VolleySingleton.getInstancia(this).agregarACola(peticionNivel);
        VolleySingleton.getInstancia(this).agregarACola(peticionIndice);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            cuentaBacks++;
            if (cuentaBacks >= 2) getSupportFragmentManager().beginTransaction().replace(R.id.content_outfit_help, new Inicio()).commit();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment mifragment = null;
        boolean seleccion = false;

        if (id == R.id.nav_inicio) {
            mifragment = new Inicio();
            seleccion = true;
        } else if (id == R.id.nav_favoritos) {
            mifragment = new Favoritos();
            seleccion = true;
        } else if (id == R.id.nav_configuraciones) {
            mifragment = new configuracion();
            seleccion = true;
        } else if (id == R.id.nav_cerrar) {
            CerrarSesion cerrar = new CerrarSesion(this);
            if (cerrar.createDialog() == true)
            {
                OutfitHelp.this.finish();
            }
        }
        if (seleccion)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_outfit_help, mifragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
