package com.app.oh.outfithelp.Vistas;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.app.oh.outfithelp.Utilidades.RecyclerViewAdapterRopaRecomendacion;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CrearRecomendacion extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View view;
    private static final String IP = "http://104.210.40.93/";
    private TextView TVFecha;
    private TextView TVEvento;
    private TextView TVDescripcion;
    //private TextView TVUsername;
    private ImageButton IBVestido;
    private ImageButton IBZapatos;
    private ImageButton IBCamisa;
    private ImageButton IBPantalon;
    private LinearLayout LYPrueba;
    private ImageButton IBView;
    private Dialog dVer;
    private Button BTCancelar; //, BTEnviarR;
    private RecyclerView recyclerViewRecomendacion;
    private String Fecha, Evento, Descripcion, Username, PkPeticion;
    private String Vestido = " ", Camisa = " ", Pantalon = " ", Zapatos = " ";
    RecyclerViewAdapterRopaRecomendacion adapter;
    private ArrayList<String> listaVestidos;
    private ArrayList<String> listaZapatos;
    private ArrayList<String> listaCamisas;
    private ArrayList<String> listaPantalones;

    public CrearRecomendacion() {
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
        view = inflater.inflate(R.layout.fragment_crear_recomendacion, container, false);
        //TVUsername = view.findViewById(R.id.TVUsernameR);
        TVFecha = view.findViewById(R.id.TVFechaCR);
        TVEvento = view.findViewById(R.id.TVEventoCR);
        TVDescripcion = view.findViewById(R.id.TVDescripcionCR);
        IBCamisa = view.findViewById(R.id.IBCamisaCR);
        IBPantalon = view.findViewById(R.id.IBPantalonCR);
        IBVestido = view.findViewById(R.id.IBVestidoCR);
        IBZapatos = view.findViewById(R.id.IBZapatosCR);
        BTCancelar = view.findViewById(R.id.BTCAncelarCR);
        IBView = view.findViewById(R.id.IBViewCR);
        listaVestidos = new ArrayList<>();
        listaZapatos = new ArrayList<>();
        listaCamisas = new ArrayList<>();
        listaPantalones = new ArrayList<>();
        recyclerViewRecomendacion = view.findViewById(R.id.RVCrearRecomendacion);
        recyclerViewRecomendacion.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        BTCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(CrearRecomendacion.this).commit();
            }
        });
        IBView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogVer();
            }
        });
        IBVestido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listaVestidos.isEmpty()) obtenerRopa(3);
                else {
                    adapter = new RecyclerViewAdapterRopaRecomendacion(listaVestidos);
                    onClickVestido();
                    recyclerViewRecomendacion.setAdapter(adapter);
                }
            }
        });
        IBZapatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listaZapatos.isEmpty()) obtenerRopa(4);
                else {
                    adapter = new RecyclerViewAdapterRopaRecomendacion(listaZapatos);
                    onClickZapatos();
                    recyclerViewRecomendacion.setAdapter(adapter);
                }

            }
        });
        IBCamisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listaCamisas.isEmpty()) obtenerRopa(2);
                else {
                    adapter = new RecyclerViewAdapterRopaRecomendacion(listaCamisas);
                    onClickCamisa();
                    recyclerViewRecomendacion.setAdapter(adapter);
                }
            }
        });
        IBPantalon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listaPantalones.isEmpty()) obtenerRopa(1);
                else {
                    adapter = new RecyclerViewAdapterRopaRecomendacion(listaPantalones);
                    onClickPantalon();
                    recyclerViewRecomendacion.setAdapter(adapter);
                }
            }
        });
        //TVUsername.setText(Username);
        TVFecha.setText(Fecha);
        TVEvento.setText(Evento);
        TVDescripcion.setText(Descripcion);
        return view;
    }
    public void dialogVer ()
    {
        dVer = new Dialog(view.getContext());
        dVer.setContentView(R.layout.dialog_ver_recomendacion);
        ImageView IVVestido = dVer.findViewById(R.id.IVVestidoVR);
        ImageView IVZapatos = dVer.findViewById(R.id.IVZapatosVR);
        ImageView IVCamisa = dVer.findViewById(R.id.IVCamisaVR);
        ImageView IVPantalon = dVer.findViewById(R.id.IVPantalonVR);
        ImageButton IBCancelar = dVer.findViewById(R.id.IBCancelarVR);
        ImageButton IBEnviar = dVer.findViewById(R.id.IBEnviarVR);

        if(!Vestido.equals(" ")) Picasso.get().load(Vestido).into(IVVestido);
        if(!Zapatos.equals(" ")) Picasso.get().load(Zapatos).into(IVZapatos);
        if(!Camisa.equals(" ")) Picasso.get().load(Camisa).into(IVCamisa);
        if(!Pantalon.equals(" ")) Picasso.get().load(Pantalon).into(IVPantalon);
        IBCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dVer.dismiss();
            }
        });
        IBEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviar();
            }
        });
        dVer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dVer.show();
        //Picasso.get().load("http://104.210.40.93/img/pantalon1").into(IVPantalon);
    }
    public void enviar ()
    {
        boolean completo = true;
        if (Pantalon.equals(" ") && Camisa.equals(" ") && Vestido.equals(" ")) completo = false;
        if (!Pantalon.equals(" ") && Camisa.equals(" ") && Vestido.equals(" ")) completo = false;
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
                params.put("playera", Camisa);
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
                if (categoria == 2) listaCamisas.add(IP + "img/clear.png");
                if (categoria == 3) listaVestidos.add(IP + "img/clear.png");
                if (categoria == 4) listaZapatos.add(IP + "img/clear.png");
                for (int i=0; i<ropa.length(); i++)
                {
                    String y = IP + ropa.getString(i);
                    if (categoria == 1) listaPantalones.add(y);
                    if (categoria == 2) listaCamisas.add(y);
                    if (categoria == 3) listaVestidos.add(y);
                    if (categoria == 4) listaZapatos.add(y);
                }
            }
            if (categoria == 1){
                adapter = new RecyclerViewAdapterRopaRecomendacion(listaPantalones);
                onClickPantalon();
            }
            if (categoria == 2) {
                adapter = new RecyclerViewAdapterRopaRecomendacion(listaCamisas);
                onClickCamisa();
            }
            if (categoria == 3){
                adapter = new RecyclerViewAdapterRopaRecomendacion(listaVestidos);
                onClickVestido();
            }
            if (categoria == 4){
                adapter = new RecyclerViewAdapterRopaRecomendacion(listaZapatos);
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
                if (recyclerViewRecomendacion.getChildAdapterPosition(vista) == 0){
                    Pantalon = " ";
                    IBPantalon.setColorFilter(Color.BLACK, PorterDuff.Mode.LIGHTEN);
                }
                else{
                    Pantalon = listaPantalones.get(recyclerViewRecomendacion.getChildAdapterPosition(vista));
                    IBPantalon.setColorFilter(Color.parseColor("#76D7C4"), PorterDuff.Mode.LIGHTEN);
                }
            }
        });
    }

    public void onClickCamisa()
    {
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                if (recyclerViewRecomendacion.getChildAdapterPosition(vista) == 0){
                    Camisa = " ";
                    IBCamisa.setColorFilter(Color.BLACK, PorterDuff.Mode.LIGHTEN);
                }
                else {
                    Camisa = listaCamisas.get(recyclerViewRecomendacion.getChildAdapterPosition(vista));
                    IBCamisa.setColorFilter(Color.parseColor("#76D7C4"), PorterDuff.Mode.LIGHTEN);
                }
            }
        });
    }

    public void onClickVestido()
    {
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                if (recyclerViewRecomendacion.getChildAdapterPosition(vista) == 0){
                    Vestido = " ";
                    IBVestido.setColorFilter(Color.BLACK, PorterDuff.Mode.LIGHTEN);
                }
                else{
                    Vestido = listaVestidos.get(recyclerViewRecomendacion.getChildAdapterPosition(vista));
                    IBVestido.setColorFilter(Color.parseColor("#76D7C4"), PorterDuff.Mode.LIGHTEN);
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
                    Zapatos = " ";
                    IBZapatos.setColorFilter(Color.BLACK, PorterDuff.Mode.LIGHTEN);
                }
                else{
                    Zapatos = listaZapatos.get(recyclerViewRecomendacion.getChildAdapterPosition(vista));
                    IBZapatos.setColorFilter(Color.parseColor("#76D7C4"), PorterDuff.Mode.LIGHTEN);
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
