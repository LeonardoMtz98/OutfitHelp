package com.app.oh.outfithelp.Utilidades;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.oh.outfithelp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Grecia on 08/05/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> lista = new ArrayList<>();
    private View view;
    public RecyclerViewAdapter(ArrayList<String> lista) {
        this.lista = lista;
    }
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_ropa,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mostrarDatos(lista.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        public ViewHolder(View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.RVPlantillaRopa);
        }

        public void mostrarDatos(String url) {
            Picasso.with(view.getContext()).load(url).into(imagen);
        }
    }
}
