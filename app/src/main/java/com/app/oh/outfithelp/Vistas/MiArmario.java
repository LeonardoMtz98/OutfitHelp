package com.app.oh.outfithelp.Vistas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;


public class MiArmario extends Fragment {
    public static final String CATEGORIA = "Categoria";
    private OnFragmentInteractionListener mListener;
    private View view;
    private Bundle bundle;
    private ViewGroup contenedor;
    private Fragment miFragment;


    public MiArmario() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MiArmario newInstance(String param1, String param2) {
        MiArmario fragment = new MiArmario();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contenedor = container;
        miFragment = new MostrarRopa();
        bundle = new Bundle();
        view = inflater.inflate(R.layout.fragment_mi_armario_h, container, false);
        String sexo = PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Sexo");
        if(sexo.equals("M"))
        {
            view = inflater.inflate(R.layout.fragment_mi_armario, container, false);
            armarioMujer();
        }
        else {
            armarioHombre();
        }
        return view;
    }
    public void armarioMujer ()
    {
        ImageButton IBBlusas;
        ImageButton IBPantalones;
        ImageButton IBZapatos;
        ImageButton IBVestidos;

        IBBlusas = view.findViewById(R.id.IBBlusas);
        IBPantalones = view.findViewById(R.id.IBPantalones);
        IBZapatos = view.findViewById(R.id.IBZapatos);
        IBVestidos = view.findViewById(R.id.IBVestidos);

        IBBlusas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString(CATEGORIA, "2");
                miFragment.setArguments(bundle);
                cambiarFragment();
            }
        });
        IBPantalones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString(CATEGORIA, "1");
                miFragment.setArguments(bundle);
                cambiarFragment();
            }
        });
        IBZapatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString(CATEGORIA, "4");
                miFragment.setArguments(bundle);
                cambiarFragment();
            }
        });
        IBVestidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString(CATEGORIA, "3");
                miFragment.setArguments(bundle);
                cambiarFragment();
            }
        });
    }
    public void cambiarFragment ()
    {
        getFragmentManager().beginTransaction().replace(R.id.LYMiArmario, miFragment).commit();
    }

    public void armarioHombre ()
    {
        ImageButton IBCamisas;
        ImageButton IBPantalones;
        ImageButton IBZapatos;
        IBCamisas = view.findViewById(R.id.IBCamisas);
        IBPantalones = view.findViewById(R.id.IBPantalonesH);
        IBZapatos = view.findViewById(R.id.IBZapatosH);
        IBCamisas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString(CATEGORIA, "2");
                miFragment.setArguments(bundle);
                cambiarFragmentH();
            }
        });
        IBPantalones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString(CATEGORIA, "1");
                miFragment.setArguments(bundle);
                cambiarFragmentH();
            }
        });
        IBZapatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString(CATEGORIA, "4");
                miFragment.setArguments(bundle);
                cambiarFragmentH();
            }
        });
    }

    public void cambiarFragmentH ()
    {
        getFragmentManager().beginTransaction().replace(R.id.LYMiArmarioH, miFragment).commit();
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
