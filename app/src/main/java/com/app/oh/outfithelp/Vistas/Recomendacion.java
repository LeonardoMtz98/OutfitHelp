package com.app.oh.outfithelp.Vistas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Recomendacion extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String IP = "http://104.210.40.93/";
    private TextView TVFecha;
    private TextView TVEvento;
    private TextView TVDescripcion;
    private TextView TVUsername;
    private ImageView IVVestido;
    private ImageView IVZapatos;
    private ImageView IVPlayera;
    private ImageView IVPantalon;
    private Button BTCancelarR, BTEnviarR;
    private RecyclerView recyclerViewRecomendacion;
    private View view;
    private  String Fecha, Evento, Descripcion, Username, PkPeticion;
    private String Vestido = " ", Playera = " ", Pantalon = " ", Zapatos = " ";
    private int Peticion;
    RecyclerViewAdapterRopa adapter;
    private ArrayList<String> listaVestidos;
    private ArrayList<String> listaZapatos;
    private ArrayList<String> listaPlayeras;
    private ArrayList<String> listaPantalones;
    private HashMap<String, Integer> HashRopa;

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
        //Peticion = Integer.parseInt(PkPeticion);
        TVUsername = view.findViewById(R.id.TVUsernameR);
        TVFecha = view.findViewById(R.id.TVFechaR);
        TVEvento = view.findViewById(R.id.TVTipoDeEventoR);
        TVDescripcion = view.findViewById(R.id.TVDescripcionR);
        IVVestido = view.findViewById(R.id.IVVestidoR);
        IVZapatos = view.findViewById(R.id.IVZapatosR);
        IVPlayera = view.findViewById(R.id.IVPlayeraR);
        IVPantalon = view.findViewById(R.id.IVPantalonR);
        BTCancelarR = view.findViewById(R.id.BTCancelarR);
        BTEnviarR = view.findViewById(R.id.BTEnviarR);
        listaVestidos = new ArrayList<>();
        listaZapatos = new ArrayList<>();
        listaPlayeras = new ArrayList<>();
        listaPantalones = new ArrayList<>();
        recyclerViewRecomendacion = view.findViewById(R.id.RVRecomendacion);
        recyclerViewRecomendacion.setLayoutManager(new LinearLayoutManager(view.getContext()));
        BTCancelarR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(Recomendacion.this).commit();
            }
        });
        BTEnviarR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviar();
            }
        });
        IVVestido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listaVestidos.isEmpty()) obtenerRopa(3);
                else {
                    adapter = new RecyclerViewAdapterRopa(listaVestidos, null, null);
                    onClickVestido();
                    recyclerViewRecomendacion.setAdapter(adapter);
                }
            }
        });
        IVZapatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listaZapatos.isEmpty()) obtenerRopa(4);
                else {
                    adapter = new RecyclerViewAdapterRopa(listaZapatos, null, null);
                    onClickZapatos();
                    recyclerViewRecomendacion.setAdapter(adapter);
                }

            }
        });
        IVPlayera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listaPlayeras.isEmpty()) obtenerRopa(2);
                else {
                    adapter = new RecyclerViewAdapterRopa(listaPlayeras, null, null);
                    onClickPlayeras();
                    recyclerViewRecomendacion.setAdapter(adapter);
                }
            }
        });
        IVPantalon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listaPantalones.isEmpty()) obtenerRopa(1);
                else {
                    adapter = new RecyclerViewAdapterRopa(listaPantalones, null, null);
                    onClickPantalon();
                    recyclerViewRecomendacion.setAdapter(adapter);
                }

            }
        });
        TVUsername.setText(Username);
        TVFecha.setText(Fecha);
        TVEvento.setText(Evento);
        TVDescripcion.setText(Descripcion);

        return view;
    }

    public void enviar ()
    {
        boolean completo = true;
        if (Pantalon.equals(" ") && Playera.equals(" ") && Vestido.equals(" ")) completo = false;
        if (!Pantalon.equals(" ") && Playera.equals(" ") && Vestido.equals(" ")) completo = false;
        if (Zapatos.equals(" ")) completo = false;
        if (completo){
            agregarRecomendacion();
        }
        else Toast.makeText(view.getContext(), "Complete recomendacion", Toast.LENGTH_LONG).show();
    }

    public void agregarRecomendacion()
    {
        String url = IP + "WebService.asmx/sendRecomendacion";
        StringRequest Recomendacion = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta =  response.substring(67,response.length()-9);
                if (!respuesta.equals("Exito")) Toast.makeText(view.getContext(), "Oops! "+ respuesta, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Oops! Error al agregar recomendacion", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("secret", PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Secret"));
                params.put("peticion", PkPeticion);
                params.put("pantalon", Pantalon);
                params.put("playera", Playera);
                params.put("vestido", Vestido);
                params.put("zapatos", Zapatos);
                return params;
            }
        };
        VolleySingleton.getInstancia(view.getContext()).agregarACola(Recomendacion);
    }

    public void obtenerRopa (final int categoria)
    {
        String url = IP + "WebService.asmx/getPrendas";
        StringRequest imagenesRopa = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                obtenerListaRopa(response, categoria);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username", Username);
                params.put("categoria", ""+categoria);
                return params;
            }
        };
        imagenesRopa.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,3,1));
        VolleySingleton.getInstancia(view.getContext()).agregarACola(imagenesRopa);
    }

    public void obtenerListaRopa(String response, int categoria)
    {
        String res = response.substring(67,response.length()-9);
        JSONArray ropa;
        try {
            ropa = new JSONArray(res);
            if (ropa != null)
            {
                if (categoria == 1) listaPantalones.add(IP + "img/clear.png");
                if (categoria == 2) listaPlayeras.add(IP + "img/clear.png");
                if (categoria == 3) listaVestidos.add(IP + "img/clear.png");
                if (categoria == 4) listaZapatos.add(IP + "img/clear.png");
                for (int i=0; i<ropa.length(); i++)
                {
                    String y = IP + ropa.getString(i);
                    if (categoria == 1) listaPantalones.add(y);
                    if (categoria == 2) listaPlayeras.add(y);
                    if (categoria == 3) listaVestidos.add(y);
                    if (categoria == 4) listaZapatos.add(y);
                }
            }
            if (categoria == 1){
                adapter = new RecyclerViewAdapterRopa(listaPantalones, null, null);
                onClickPantalon();
            }
            if (categoria == 2) {
                adapter = new RecyclerViewAdapterRopa(listaPlayeras, null, null);
                onClickPlayeras();
            }
            if (categoria == 3){
                adapter = new RecyclerViewAdapterRopa(listaVestidos, null, null);
                onClickVestido();
            }
            if (categoria == 4){
                adapter = new RecyclerViewAdapterRopa(listaZapatos, null, null);
                onClickZapatos();
            }
            recyclerViewRecomendacion.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), "Oops! Error al bajar ropa", Toast.LENGTH_SHORT).show();
        }
    }
    public void onClickPantalon()
    {
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {

                if (recyclerViewRecomendacion.getChildAdapterPosition(vista) == 0) {
                    Picasso.with(view.getContext()).load(R.drawable.ic_pantalon_hombre).into(IVPantalon);
                    Pantalon = " ";
                }
                else{
                    Pantalon = listaPantalones.get(recyclerViewRecomendacion.getChildAdapterPosition(vista));
                    Picasso.get().load(Pantalon).fit().into(IVPantalon);
                }
            }
        });
    }

    public void onClickPlayeras()
    {
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                if (recyclerViewRecomendacion.getChildAdapterPosition(vista) == 0)
                {
                    Picasso.with(view.getContext()).load(R.drawable.ic_top_mujer).into(IVPlayera);
                    Playera = " ";
                }
                else {
                    Playera = listaPlayeras.get(recyclerViewRecomendacion.getChildAdapterPosition(vista));
                    Picasso.get().load(Playera).fit().into(IVPlayera);
                }
            }
        });
    }

    public void onClickVestido()
    {
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                    if (recyclerViewRecomendacion.getChildAdapterPosition(vista) == 0) {
                        Picasso.get().load(R.drawable.ic_vestido).fit().into(IVVestido);
                        Vestido = " ";
                    }
                    else {
                        Vestido = listaVestidos.get(recyclerViewRecomendacion.getChildAdapterPosition(vista));
                        Picasso.get().load(Vestido).fit().into(IVVestido);
                    }
            }
        });
    }

    public void onClickZapatos()
    {
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                if (recyclerViewRecomendacion.getChildAdapterPosition(vista) == 0){
                    Picasso.get().load(R.drawable.ic_zapatos_hombre).fit().into(IVZapatos);
                    Zapatos = " ";
                }
                else {
                    Zapatos = listaZapatos.get(recyclerViewRecomendacion.getChildAdapterPosition(vista));
                    Picasso.get().load(Zapatos).fit().into(IVZapatos);
                }
            }
        });
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
