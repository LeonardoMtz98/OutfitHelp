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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.Utilidades.RecyclerViewAdapterComunidad;
import com.app.oh.outfithelp.Utilidades.RecyclerViewAdapterPeticiones;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Comunidad extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String IP = "http://104.210.40.93/";
    private HashMap<Integer, String> Eventos;
    private HashMap<Integer, String> Avatares;
    private RecyclerView recyclerComunidad;
    private Bundle bundle;
    private String [][] listaPeticiones;
    //private ArrayList<String> listaPeticiones;
    private View view;

    public Comunidad() {
        // Required empty public constructor
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
        view = inflater.inflate(R.layout.fragment_comunidad, container, false);
        //listaPeticiones = new ArrayList<>();
        Eventos = new HashMap<>();
        Avatares = new HashMap<>();
        bundle = new Bundle();
        recyclerComunidad = view.findViewById(R.id.RVComunidad);
        recyclerComunidad.setLayoutManager(new GridLayoutManager(view.getContext(),1));
        obtenerDatos();
        return view;
    }

    public void obtenerDatos()
    {
        String url = IP + "WebService.asmx/getAvatares";
        StringRequest avatares = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta = response.substring(67,response.length()-9);
                JSONArray ArrayAvatares;
                try {
                    ArrayAvatares = new JSONArray(respuesta);
                    for (int i = 0; i < ArrayAvatares.length(); i++)
                    {
                        JSONObject ObjetoAvatares = ArrayAvatares.getJSONObject(i);
                        Avatares.put(ObjetoAvatares.getInt("PkAvatar"), ObjetoAvatares.getString("Direccion"));
                    }
                    obtenerEventos();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(view.getContext(),"Oops! Error al leer avatares",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Oops! Error al obtener imagenes", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstancia(view.getContext()).agregarACola(avatares);
    }
    public void obtenerEventos()
    {
        String url = IP + "WebService.asmx/getTiposDeEvento";
        StringRequest eventos = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta = response.substring(67,response.length()-9);
                JSONArray ArrayEventos;
                try {
                    ArrayEventos = new JSONArray(respuesta);
                    for (int i = 0; i < ArrayEventos.length(); i++)
                    {
                        JSONObject ObjetoEventos = ArrayEventos.getJSONObject(i);
                        Eventos.put(ObjetoEventos.getInt("PkEvento"), ObjetoEventos.getString("Nombre"));
                    }
                    obtenerPeticiones();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(view.getContext(),"Oops! Error al leer eventos",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Oops! Error al obtener eventos", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstancia(view.getContext()).agregarACola(eventos);
    }
    public void obtenerPeticiones()
    {
        String url = IP + "WebService.asmx/getPeticiones";
        StringRequest obtenerPeticiones = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mostrarPeticiones(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(),"Oops! Error al cargar Peticiones", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Secret", PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Secret"));
                return params;
            }
        };
        VolleySingleton.getInstancia(view.getContext()).agregarACola(obtenerPeticiones);
    }

    public void mostrarPeticiones (String response)
    {
        String respuesta = response.substring(67,response.length()-9);
        JSONArray peticiones;
        try {
            peticiones = new JSONArray(respuesta);
            listaPeticiones = new String [peticiones.length()][6];
            for (int i=0; i<peticiones.length(); i++)
            {
                JSONObject peticion = peticiones.getJSONObject(i);
                //peticion.put()
                listaPeticiones[i][0] = IP + Avatares.get(peticion.getInt("FKAvatar"));
                listaPeticiones[i][1] = peticion.getString("Fecha").replace("T", " ");
                listaPeticiones[i][2] = Eventos.get(peticion.getInt("FkTipoEvento"));
                listaPeticiones[i][3] = peticion.getString("Descripcion");
                listaPeticiones[i][4] = peticion.getString("FkUsuario");
                listaPeticiones[i][5] = peticion.getString("PkPeticion");
            }
            RecyclerViewAdapterComunidad adapter = new RecyclerViewAdapterComunidad(listaPeticiones);
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vista) {
                    abrirRecomendacion(vista);
                }
            });
            recyclerComunidad.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(),"Oops! Error al obtener Peticiones", Toast.LENGTH_SHORT).show();
        }
    }

    public void abrirRecomendacion(View vista)
    {
        int seleccion =  recyclerComunidad.getChildAdapterPosition(vista);
        bundle.putString("Fecha", listaPeticiones[seleccion][1]);
        bundle.putString("Evento", listaPeticiones[seleccion][2]);
        bundle.putString("Descripcion", listaPeticiones[seleccion][3]);
        bundle.putString("Username", listaPeticiones[seleccion][4]);
        bundle.putString("PkPeticion", listaPeticiones[seleccion][5]);
        Fragment miFragment = new Recomendacion();
        miFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.LYComunidad, miFragment).commit();
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
