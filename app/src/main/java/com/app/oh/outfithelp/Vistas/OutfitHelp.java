package com.app.oh.outfithelp.Vistas;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;

public class OutfitHelp extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Inicio.OnFragmentInteractionListener
        , MiArmario.OnFragmentInteractionListener, Comunidad.OnFragmentInteractionListener,
        MisPeticiones.OnFragmentInteractionListener, Favoritos.OnFragmentInteractionListener,
        MostrarRopa.OnFragmentInteractionListener, DetallesRopa.OnFragmentInteractionListener,
        AgregarPeticion.OnFragmentInteractionListener{
    TextView TVEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outfit_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TVEmail = findViewById(R.id.TVEmail);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //TVEmail.setText(PreferencesConfig.getInstancia(this).getFromSharedPrefs("Correo"));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
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

        } else if (id == R.id.nav_cerrar) {
            //mifragment = new CerrarSesion();
            //getSupportFragmentManager().beginTransaction().add(R.id.content_outfit_help, mifragment).commit();
            CerrarSesion cerrar = new CerrarSesion(this);
            //boolean end = cerrar.createDialog();
            if (cerrar.createDialog() == true)
            {

                OutfitHelp.this.finish();
            }
        }

        if (seleccion)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_outfit_help, mifragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
