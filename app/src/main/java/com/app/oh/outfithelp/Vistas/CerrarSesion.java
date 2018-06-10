package com.app.oh.outfithelp.Vistas;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;


public class CerrarSesion  {
    private Context miContext;
    private View view;
    boolean end;
    private Dialog miDialog;

    public CerrarSesion() {
        // Required empty public constructor
    }
    public CerrarSesion(Context context)
    {
        miContext = context;
        miDialog = new Dialog(miContext);
    }
    public boolean createDialog()
    {
        Button cerrar;
        Button cancelar;
        end = false;

        miDialog.setContentView(R.layout.fragment_cerrar_sesion);
        cerrar = miDialog.findViewById(R.id.BTCerrarSesion);
        cancelar = miDialog.findViewById(R.id.BTNoCerrarSesion);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miDialog.dismiss();
                end = false;
            }
        });
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cierraSesion();
                miDialog.dismiss();
                Intent miIntent = new Intent(view.getContext(),LogIn.class);
                view.getContext().startActivity(miIntent);
                end = true;
            }
        });
        miDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        miDialog.show();
        return end;
    }

    public void cierraSesion ()
    {
        PreferencesConfig.getInstancia(miContext).agregarASharedPrefs(OutfitHelp.SECRET, "NULL");
        PreferencesConfig.getInstancia(miContext).agregarASharedPrefs(OutfitHelp.NIVEL, "NULL");
        PreferencesConfig.getInstancia(miContext).agregarASharedPrefs(OutfitHelp.INDICE, "NULL");
        PreferencesConfig.getInstancia(miContext).agregarASharedPrefs(OutfitHelp.SESION_INICIADA, "false");
    }

}
