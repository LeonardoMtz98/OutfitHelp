package com.app.oh.outfithelp.Vistas;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.Utilidades.RecyclerViewAdapterPeticiones;
import com.app.oh.outfithelp.Utilidades.RecyclerViewAdapterRopa;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MisPeticiones extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View view;
    private static final String IP = "http://104.210.40.93/";
    private RecyclerView recyclerPeticiones;
    private String [][] listaPeticiones;
    private HashMap<Integer, String> Eventos;
    private HashMap<Integer, String> Avatares;
    private SwipeRefreshLayout refreshPeticiones;
    private ImageButton IBBInfoBorrar;
    private Dialog miDialog;
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
        recyclerPeticiones = view.findViewById(R.id.RVPeticiones);
        refreshPeticiones = view.findViewById(R.id.RefreshMisPeticiones);
        IBBInfoBorrar = view.findViewById(R.id.IBInfoBorrar);
        Eventos = new HashMap<>();
        Avatares = new HashMap<>();
        recyclerPeticiones.setLayoutManager(new GridLayoutManager(view.getContext(),1));
        IBagregarPeticiones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment miFragment = new AgregarPeticion();
                getFragmentManager().beginTransaction().add(R.id.LYMisPeticiones, miFragment).commit();
            }
        });
        refreshPeticiones.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                obtenerPeticiones();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshPeticiones.setRefreshing(false);
                    }
                },3000);
            }
        });
        IBBInfoBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Para borrar mantenga presionado un elemento", Toast.LENGTH_LONG).show();
            }
        });
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
       String url = IP + "WebService.asmx/getMisPeticiones";
       StringRequest obtenerMisPeticiones = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
       VolleySingleton.getInstancia(view.getContext()).agregarACola(obtenerMisPeticiones);
   }

   public void mostrarPeticiones (String response)
   {
       String respuesta = response.substring(67,response.length()-9);
       Log.e("Respuesta json", respuesta);
       JSONArray peticiones;
       try {
           peticiones = new JSONArray(respuesta);
           listaPeticiones = new String [peticiones.length()][5];
           for (int i=0; i<peticiones.length(); i++)
           {
               JSONObject peticion = peticiones.getJSONObject(i);
               listaPeticiones[i][0] = IP + Avatares.get(peticion.getInt("FKAvatar"));
               listaPeticiones[i][1] = peticion.getString("Fecha").replace("T", " ");
               listaPeticiones[i][2] = Eventos.get(peticion.getInt("FkTipoEvento"));
               listaPeticiones[i][3] = peticion.getString("Descripcion");
               listaPeticiones[i][4] = peticion.getString("PkPeticion");
           }

       } catch (JSONException e) {
           e.printStackTrace();
           Toast.makeText(view.getContext(),"Oops! Error al obtener Peticiones", Toast.LENGTH_SHORT).show();
       }
       RecyclerViewAdapterPeticiones adapter = new RecyclerViewAdapterPeticiones(listaPeticiones);
       adapter.setLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {
               dialogEliminarPeticion(view);
               return false;
           }
       });
       adapter.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

           }
       });
       recyclerPeticiones.setAdapter(adapter);
   }
    public void dialogEliminarPeticion (final View vista)
    {
        final String PkPeticion = listaPeticiones[recyclerPeticiones.getChildAdapterPosition(vista)][4];
        Toast.makeText(view.getContext(), PkPeticion, Toast.LENGTH_SHORT).show();
        miDialog = new Dialog(view.getContext());
        Button eliminar;
        Button cancelar;

        miDialog.setContentView(R.layout.dialog_eliminar_peticion);
        eliminar = miDialog.findViewById(R.id.BTEliminarPeticionDE);
        cancelar = miDialog.findViewById(R.id.BTCancelarEliminarDE);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miDialog.dismiss();
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarPeticion(vista, PkPeticion);
                miDialog.dismiss();
            }
        });
        miDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        miDialog.show();
    }
    public void eliminarPeticion(View vista, final String PkPeticion)
    {
        String url = IP + "WebService.asmx/deletePeticiones";
        StringRequest eliminarPeticion = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                obtenerPeticiones();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(),"Oops! Error al borrar peticion", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("secret", PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Secret"));
                params.put("PkPeticion", PkPeticion);
                return  params;
            }
        };
        VolleySingleton.getInstancia(view.getContext()).agregarACola(eliminarPeticion);
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
