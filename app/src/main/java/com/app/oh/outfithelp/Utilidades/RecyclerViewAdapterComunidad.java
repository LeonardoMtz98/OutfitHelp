package com.app.oh.outfithelp.Utilidades;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.oh.outfithelp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Grecia on 29/05/2018.
 */

public class RecyclerViewAdapterComunidad  extends RecyclerView.Adapter<RecyclerViewAdapterComunidad.ViewHolder>
                                            implements View.OnClickListener{
    private View view;
    private String[][] lista;
    private View.OnClickListener listener;

    public RecyclerViewAdapterComunidad (String[][] lista)
    {
        this.lista = lista;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_comunidad,null,false);
        view.setOnClickListener(this);
        //view.setOnLongClickListener(this);
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

    @Override
    public void onClick(View view) {
        if (listener != null)
        {
            listener.onClick(view);
        }
    }
    public void setOnClickListener(View.OnClickListener listener)
    {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView IVAvatar;
        TextView TVTipoDeEvento;
        TextView TVFecha;
        TextView TVDescripcion;
        TextView TVUsuario;
        public ViewHolder(View itemView) {
            super(itemView);
            IVAvatar = itemView.findViewById(R.id.IVAvatarPC);
            TVTipoDeEvento = itemView.findViewById(R.id.TVTipoDeEventoPC);
            TVFecha = itemView.findViewById(R.id.TVFechaPC);
            TVDescripcion = itemView.findViewById(R.id.TVDescripcionPC);
            TVUsuario = itemView.findViewById(R.id.TVUsuarioPC);
        }
        public void mostrarDatos (int posicion) {
            Picasso.with(itemView.getContext()).load(lista[posicion][0]).into(IVAvatar);
            TVFecha.setText(lista[posicion][1]);
            TVTipoDeEvento.setText(lista[posicion][2]);
            TVDescripcion.setText(lista[posicion][3]);
            TVUsuario.setText(lista[posicion][4]);
        }
    }

}
