package com.app.oh.outfithelp.Vistas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.oh.outfithelp.R;

public class Recomendacion extends Fragment {

    private OnFragmentInteractionListener mListener;
    private TextView TVFecha;
    private TextView TVEvento;
    private TextView TVDescripcion;
    private TextView TVUsername;
    private View view;
    private  String Fecha;
    private String Evento;
    private String Descripcion;
    private String Username;
    private  String PkPeticion;
    private int Peticion;

    public Recomendacion() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Fecha = getArguments().getString("Fecha");
            Evento = getArguments().getString("Evento");
            Descripcion = getArguments().getString("Descripcion");
            Username = getArguments().getString("Username");
            PkPeticion = getArguments().getString("PkPeticion");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recomendacion, container, false);
        Peticion = Integer.parseInt(PkPeticion);
        TVUsername = view.findViewById(R.id.TVUsernameR);
        TVFecha = view.findViewById(R.id.TVFechaR);
        TVEvento = view.findViewById(R.id.TVTipoDeEventoR);
        TVDescripcion = view.findViewById(R.id.TVDescripcionR);
        TVUsername.setText(Username);
        TVFecha.setText(Fecha);
        TVEvento.setText(Evento);
        TVDescripcion.setText(Descripcion);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
