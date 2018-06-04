package com.app.oh.outfithelp.Vistas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.oh.outfithelp.R;

public class DetallesPeticion extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View view;
    private ImageView IVAvatar;
    private TextView TVEvento, TVFecha, TVDescripcion;
    private RecyclerView RVDetallesPeticion;
    private ImageButton IBRegresar;
    private String avatar, evento, fecha, descripcion, PkPeticion;

    public DetallesPeticion() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            avatar = getArguments().getString("Avatar", "NULL");
            evento = getArguments().getString("Evento", "NULL");
            fecha = getArguments().getString("Fecha", "NULL");
            descripcion = getArguments().getString("Descripcion", "NULL");
            PkPeticion = getArguments().getString("PkPeticion");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detalles_peticion, container, false);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
