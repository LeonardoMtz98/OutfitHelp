package com.app.oh.outfithelp.Utilidades;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.oh.outfithelp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Grecia on 04/06/2018.
 */

public class RecyclerViewAdapterDetallesPeticion extends RecyclerView.Adapter<RecyclerViewAdapterDetallesPeticion.ViewHolder>
        implements View.OnClickListener {
    public static final String IP = "http://104.210.40.93/";
    private JSONArray lista;
    private View view;
    private View.OnClickListener listener;
    public RecyclerViewAdapterDetallesPeticion(JSONArray lista) {
        this.lista = lista;
    }
    @Override
    public RecyclerViewAdapterDetallesPeticion.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_recomendacion,null,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterDetallesPeticion.ViewHolder holder, int position) {
        try {
            JSONObject recomendacion = lista.getJSONObject(position);
            holder.mostrarDatos(recomendacion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return lista.length();
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
        ImageView IVVestido, IVZapatos, IVCamisa, IVPantalon;
        TextView TVUsuario, TVIndiceFashion;
        public ViewHolder(View itemView) {
            super(itemView);
            IVVestido = itemView.findViewById(R.id.IVVestidoPR);
            IVZapatos = itemView.findViewById(R.id.IVZapatosPR);
            IVCamisa = itemView.findViewById(R.id.IVCamisaPR);
            IVPantalon = itemView.findViewById(R.id.IVPantalonPR);
            TVUsuario = itemView.findViewById(R.id.TVUsernamePR);
            TVIndiceFashion = itemView.findViewById(R.id.TVIndiceFashionPR);
        }

        public void mostrarDatos(JSONObject recomendacion) {
            try {
                Picasso.get().load(IP + recomendacion.getString("vestido")).fit().into(IVVestido);
                Picasso.get().load(IP + recomendacion.getString("zapatos")).fit().into(IVZapatos);
                Picasso.get().load(IP + recomendacion.getString("camisa")).fit().into(IVCamisa);
                Picasso.get().load(IP + recomendacion.getString("pantalon")).fit().into(IVPantalon);
                TVUsuario.setText(recomendacion.getString("usuarioRecomendo"));
                TVIndiceFashion.setText(recomendacion.getString("indiceFashion"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
