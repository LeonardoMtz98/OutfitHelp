package com.app.oh.outfithelp.Utilidades;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.oh.outfithelp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Grecia on 24/05/2018.
 */

public class RecyclerViewAdapterPeticiones extends RecyclerView.Adapter<RecyclerViewAdapterPeticiones.ViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {
    private String [][] lista;
    private View view;
    private View.OnClickListener listener;
    private View.OnLongClickListener longClickListener;
    public RecyclerViewAdapterPeticiones(String[][] lista) {
        this.lista = lista;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_peticion,null,false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mostrarDatos(position);
    }

    @Override
    public int getItemCount() {
        return lista.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView fecha;
        TextView tipoDeEvento;
        TextView descripcion;
        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.IVAvatarPeticion);
            fecha = itemView.findViewById(R.id.TVFechaPP);
            tipoDeEvento= itemView.findViewById(R.id.TVTipoDeEventoPP);
            descripcion = itemView.findViewById(R.id.TVDescripcionPP);
        }

        public void mostrarDatos(int position) {
            Picasso.with(itemView.getContext()).load(lista[position][0]).into(avatar);
            fecha.setText(lista[position][1]);
            tipoDeEvento.setText(lista[position][2]);
            descripcion.setText(lista[position][3]);
        }
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
        {
            listener.onClick(view);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (longClickListener != null)
        {
            longClickListener.onLongClick(view);
        }
        return false;
    }

    public void setOnClickListener(View.OnClickListener listener)
    {
        this.listener = listener;
    }
    public void setLongClickListener (View.OnLongClickListener longClickListener)
    {
        this.longClickListener = longClickListener;
    }
}

