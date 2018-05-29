package com.app.oh.outfithelp.Vistas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.Utilidades.RecyclerViewAdapterRopa;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MostrarRopa extends Fragment {

    private static final String CATEGORIA = "Categoria";
    public static final String IP = "http://104.210.40.93/";
    public static final String IMAGEN = "Imagen";
    private Bundle bundle;
    private String categoria;
    private View view;
    private ArrayList<String> lista = new ArrayList<String>();
    private OnFragmentInteractionListener mListener;
    private ImageButton IBBackCategorias;
    private RecyclerView recyclerView;
    private ImageButton IBAgregarPrenda;

    public MostrarRopa() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoria = getArguments().getString(CATEGORIA, "0");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mostrar_ropa, container, false);
        IBBackCategorias = view.findViewById(R.id.IBBackCategorias);
        IBBackCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment miFragment = new MiArmario();
                getFragmentManager().beginTransaction().add(R.id.LYMostrarRopa, miFragment).commit();
            }
        });
        recyclerView = view.findViewById(R.id.RVRopa);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),3));
        IBAgregarPrenda = view.findViewById(R.id.IBAgregarPrenda);
        IBAgregarPrenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment miFragment = new AgregarPrenda();
                bundle = new Bundle();
                bundle.putString("Categoria", categoria);
                miFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.LYMostrarRopa, miFragment).commit();
            }
        });
        getImagenes();
        return view;
    }

    private void getImagenes ()
    {
        String url = IP + "WebService.asmx/getPrendas";
        StringRequest imagenesRopa = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Guardar(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Secret",PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Secret") );
                params.put("Categoria", categoria);
                return params;
            }
        };
        imagenesRopa.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,3,1));
        VolleySingleton.getInstancia(view.getContext()).agregarACola(imagenesRopa);
    }

    public void Guardar (String response) {
        String res = response.substring(67,response.length()-9);
        JSONArray ropa;
        try {
            ropa = new JSONArray(res);
            if (ropa != null)
            {
                for (int i=0; i<ropa.length(); i++)
                {
                    String y = IP + ropa.getString(i);
                    //Toast.makeText(view.getContext(), y, Toast.LENGTH_LONG).show();
                    lista.add(y);
                }
            }

            RecyclerViewAdapter adapter = new RecyclerViewAdapter(lista);
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    detallesRopa(view);
                }
            });
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void detallesRopa (View vista)
    {
        Bundle bundle = new Bundle();
        Fragment miFragment = new DetallesRopa();
        bundle.putString(IMAGEN, lista.get(recyclerView.getChildAdapterPosition(vista)));
        miFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.LYMostrarRopa, miFragment).commit();
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
