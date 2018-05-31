package com.app.oh.outfithelp.Utilidades;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.oh.outfithelp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Grecia on 31/05/2018.
 */

public class RecyclerViewAdapterRecomendacion extends RecyclerView.Adapter<RecyclerViewAdapterRecomendacion.ViewHolder>{
    View view;
    JSONArray lista;

    public RecyclerViewAdapterRecomendacion (JSONArray lista)
    {
        this.lista = lista;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_recomendacion,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterRecomendacion.ViewHolder holder, int position) {
        holder.mostrarDatos(position);
    }

    @Override
    public int getItemCount() {
        return lista.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView IVVestido;
        ImageView IVZapatos;
        ImageView IVPlayera;
        ImageView IVPantalon;
        TextView  TVUsername;
        public ViewHolder(View itemView) {
            super(itemView);
            IVVestido = itemView.findViewById(R.id.IVVestidoPR);
            IVZapatos = itemView.findViewById(R.id.IVZapatosPR);
            IVPlayera = itemView.findViewById(R.id.IVPlayeraPR);
            IVPantalon = itemView.findViewById(R.id.IVPantalonPR);
            TVUsername = itemView.findViewById(R.id.TVUsernamePR);
        }

        public void mostrarDatos(int position) {
            //Picasso.with(itemView.getContext()).load()
        }
    }
}
