package com.app.oh.outfithelp.Vistas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.app.oh.outfithelp.R;

public class MisPeticiones extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View view;

    public MisPeticiones() {
        // Required empty public constructor
    }

    public static MisPeticiones newInstance(String param1, String param2) {
        MisPeticiones fragment = new MisPeticiones();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mis_peticiones, container, false);
        ImageButton IBagregarPeticiones;
        IBagregarPeticiones = view.findViewById(R.id.IBAgregarPeticion);
        IBagregarPeticiones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment miFragment = new AgregarPeticion();
                getFragmentManager().beginTransaction().add(R.id.LYMisPeticiones, miFragment).commit();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
