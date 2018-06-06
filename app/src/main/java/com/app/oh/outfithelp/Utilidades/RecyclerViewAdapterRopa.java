package com.app.oh.outfithelp.Utilidades;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Vistas.AgregarPrenda;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Grecia on 08/05/2018.
 */

public class RecyclerViewAdapterRopa extends RecyclerView.Adapter<RecyclerViewAdapterRopa.ViewHolder>
    implements View.OnClickListener{

    private Context context;
    private ArrayList<String> lista;
    private ArrayList<String> direcciones;
    private View view;
    private View.OnClickListener listener;
    public RecyclerViewAdapterRopa(ArrayList<String> lista, ArrayList<String> direcciones, Context context) {
        this.context = context;
        this.lista = lista;
        this.direcciones = direcciones;
    }
    @Override
    public RecyclerViewAdapterRopa.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_ropa,null,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterRopa.ViewHolder holder, int position) {
        if (direcciones == null) holder.mostrarDatos(lista.get(position).toString());
        else {
            String nombreArchivo = direcciones.get(position).replace("img/", "");
            File archivo = AgregarPrenda.crearArchivo(nombreArchivo, context);
            if (archivo.exists()) holder.mostrarDatos("file:" + archivo.getAbsolutePath());
            else holder.mostrarDatos(lista.get(position).toString());
        }

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void setOnClickListener(View.OnClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
        {
            listener.onClick(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        public ViewHolder(View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.RVPlantillaRopa);
        }

        public void mostrarDatos(String url) {
            Picasso.get().load(url).fit().into(imagen);
        }
    }
}
