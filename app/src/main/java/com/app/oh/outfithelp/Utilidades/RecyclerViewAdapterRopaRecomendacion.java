package com.app.oh.outfithelp.Utilidades;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Vistas.AgregarPrenda;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Grecia on 03/06/2018.
 */

public class RecyclerViewAdapterRopaRecomendacion extends RecyclerView.Adapter<RecyclerViewAdapterRopaRecomendacion.ViewHolder>
        implements View.OnClickListener{

    private ArrayList<String> lista;
    private View view;
    private View.OnClickListener listener;
    public RecyclerViewAdapterRopaRecomendacion(ArrayList<String> lista) {
        this.lista = lista;
    }
    @Override
    public RecyclerViewAdapterRopaRecomendacion.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_ropa_recomendacion,null,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterRopaRecomendacion.ViewHolder holder, int position) {
        holder.mostrarDatos(lista.get(position).toString());
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
        LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.IVRopaRecomendacion);
            linearLayout = itemView.findViewById(R.id.LYPlantillaCR);
        }

        public void mostrarDatos(String url) {
            Picasso.get().load(url).fit().into(imagen);
        }
    }
}
