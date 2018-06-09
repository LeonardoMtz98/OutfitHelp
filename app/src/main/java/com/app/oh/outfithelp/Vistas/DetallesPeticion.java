package com.app.oh.outfithelp.Vistas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.Utilidades.RecyclerViewAdapterDetallesPeticion;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;
import com.app.oh.outfithelp.Utilidades.WebService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetallesPeticion extends Fragment {

    public static final String IP = "http://104.210.40.93/";
    private OnFragmentInteractionListener mListener;
    private View view;
    private String Fecha = "", Evento = "", Descripcion = "", Avatar = "", PkPeticion ="";
    private TextView TVFecha, TVEvento, TVDescripcion;
    private ImageView IVAvatar;
    private RecyclerView RVRecomendaciones;
    private ImageButton IBVolver, IBBorrar;
    private String PkRecomendacion = "";
    private JSONArray recomendaciones;
    private Dialog Recomendacion, Favoritos;
    private CheckBox CBLike;
    private CheckBox CBFavoritos;
    private JSONArray ListaFavoritos;
    private RecyclerView RVFavoritos;

    public DetallesPeticion() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Fecha = getArguments().getString("Fecha");
            Evento = getArguments().getString("Evento");
            Descripcion = getArguments().getString("Descripcion");
            Avatar = getArguments().getString("Avatar");
            PkPeticion = getArguments().getString("PkPeticion");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detalles_peticion, container, false);
        TVFecha = view.findViewById(R.id.TVFechaDP);
        TVEvento = view.findViewById(R.id.TVEventoDP);
        TVDescripcion = view.findViewById(R.id.TVDescripcionDP);
        IVAvatar = view.findViewById(R.id.IVAvatarDP);
        RVRecomendaciones = view.findViewById(R.id.RVDetallesPeticion);
        IBVolver = view.findViewById(R.id.IBVolverDP);
        IBBorrar = view.findViewById(R.id.IBBorrarDP);
        TVFecha.setText(Fecha);
        TVEvento.setText(Evento);
        TVDescripcion.setText(Descripcion);
        Picasso.get().load(Avatar).fit().into(IVAvatar);
        RVRecomendaciones.setLayoutManager(new GridLayoutManager(view.getContext(),2));
        obtenerRecomendaciones();
        IBVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(DetallesPeticion.this).commit();
            }
        });
        IBBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrarRecomendaciones();
            }
        });
        return view;
    }

    public void obtenerRecomendaciones()
    {
        String url = IP + "WebService.asmx/getRecomendaciones";
        StringRequest recomendaciones = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mostrarRecomendaciones(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Oops! Error al obtener recomendaciones", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("secret", PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Secret"));
                params.put("peticion", PkPeticion);
                return params;
            }
        };
        VolleySingleton.getInstancia(view.getContext()).agregarACola(recomendaciones);
    }

    public void mostrarRecomendaciones (String response)
    {
        String respuesta = response.substring(67,response.length()-9);
        try {
            recomendaciones = new JSONArray(respuesta);
            RecyclerViewAdapterDetallesPeticion adapter = new RecyclerViewAdapterDetallesPeticion(recomendaciones);
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vista) {
                    abrirRecomendacion(vista);
                }
            });
            RVRecomendaciones.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), "Oops! Error al cargar recomendaciones", Toast.LENGTH_SHORT).show();
        }
    }

    public void abrirRecomendacion (View vista)
    {
        Recomendacion = new Dialog(view.getContext());
        Recomendacion.setContentView(R.layout.mostrar_recomendacion);
        ImageView IVVestido = Recomendacion.findViewById(R.id.IVVestidoMR);
        ImageView IVZapatos = Recomendacion.findViewById(R.id.IVZapatosMR);
        ImageView IVCamisa = Recomendacion.findViewById(R.id.IVCamisaMR);
        ImageView IVPantalon = Recomendacion.findViewById(R.id.IVPantalonMR);
        TextView TVCerrar = Recomendacion.findViewById(R.id.TVCerrarMR);
        CBLike = Recomendacion.findViewById(R.id.CBLikeMR);
        CBFavoritos = Recomendacion.findViewById(R.id.CBFavoritoMR);
        String url = IP + "WebService.asmx/getStatusRecomendacion";

        try {
            final JSONObject datos = recomendaciones.getJSONObject(RVRecomendaciones.getChildAdapterPosition(vista));
            final String recomendacion = datos.getString("recomendacion");
            StringRequest status = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String respuesta =  response.substring(67, response.length()-9);
                    if(respuesta.equals("Guardada")){
                        CBLike.setChecked(true);
                        CBFavoritos.setChecked(true);
                    }
                    else if(respuesta.equals("Gustada")) CBLike.setChecked(true);
                    else Toast.makeText(view.getContext(), "Oops! "+ respuesta, Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(view.getContext(), "Oops! error al obtener estado", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret", PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Secret"));
                    params.put("recomendacion", recomendacion);
                    return params;
                }
            };
            VolleySingleton.getInstancia(view.getContext()).agregarACola(status);
            Picasso.get().load(IP + datos.getString("vestido")).fit().into(IVVestido);
            Picasso.get().load(IP + datos.getString("zapatos")).fit().into(IVZapatos);
            Picasso.get().load(IP + datos.getString("camisa")).fit().into(IVCamisa);
            Picasso.get().load(IP + datos.getString("pantalon")).fit().into(IVPantalon);
            PkRecomendacion = datos.getString("recomendacion");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CBLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CBLike.isChecked())cambiarStatus("Gustada");
                else cambiarStatus("Vista");
            }
        });
        CBFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checarEspacioFavoritos();
            }
        });
        TVCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recomendacion.dismiss();
            }
        });
        Recomendacion.show();
    }

    public void checarEspacioFavoritos()
    {
        String url =  IP + "WebService.asmx/checarFavoritos";
        StringRequest checarFavoritos = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respusta = response.substring(67, response.length()-9);
                if (respusta.equals("Disponibles"))
                {
                    if(CBFavoritos.isChecked()){
                        CBLike.setChecked(true);
                        CBLike.setEnabled(false);
                        cambiarStatus("Guardada");
                        Toast.makeText(Recomendacion.getContext(), "Guardada", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        CBLike.setEnabled(true);
                        cambiarStatus("Gustada");
                    }
                }
                else {
                    CBFavoritos.setChecked(false);
                    CBLike.setEnabled(true);
                    cambiarStatus("Gustada");
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Has llegado a tu limite de espacio");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNeutralButton("Remplazar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            mostrarFavoritos();
                        }
                    });
                    builder.create();
                    builder.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Oops! Error al agregar a favoritos", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Username"));
                return params;
            }
        };
        VolleySingleton.getInstancia(view.getContext()).agregarACola(checarFavoritos);
    }

    public void cambiarStatus (final String Status)
    {
        String url = IP + "WebService.asmx/setStatusRecomendacion";
        StringRequest status = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta = response.substring(67, response.length()-9);
                if (!respuesta.equals("Exito")) Toast.makeText(view.getContext(), "Oops! "+ respuesta, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Oops! Error al actualizar status", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("secret", PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Secret"));
                params.put("status", Status);
                params.put("recomendacion", PkRecomendacion);
                return params;
            }
        };
        VolleySingleton.getInstancia(view.getContext()).agregarACola(status);
    }

    public void mostrarFavoritos ()
    {
        Favoritos = new Dialog(view.getContext());
        Favoritos.setContentView(R.layout.fragment_favoritos);
        RVFavoritos = Favoritos.findViewById(R.id.RVFavoritos);
        RVFavoritos.setLayoutManager(new GridLayoutManager(view.getContext(),2));
        String url = IP + "WebService.asmx/getFavoritos";
        StringRequest favoritos = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta = response.substring(67,response.length()-9);
                try {
                    ListaFavoritos = new JSONArray(respuesta);
                    RecyclerViewAdapterDetallesPeticion adapter = new RecyclerViewAdapterDetallesPeticion(ListaFavoritos);
                    adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View vista) {
                            Favoritos.dismiss();
                            try {
                                JSONObject remplazar = ListaFavoritos.getJSONObject(RVFavoritos.getChildAdapterPosition(vista));
                                String temp = PkRecomendacion;
                                PkRecomendacion = remplazar.getString("recomendacion");
                                cambiarStatus("Gustada");
                                PkRecomendacion = temp;
                                cambiarStatus("Guardada");
                                CBFavoritos.setChecked(true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    RVFavoritos.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(view.getContext(), "Oops! Error al cargar recomendaciones", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Oops! Error al obtener favoritos", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("secret", PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Secret"));
                return params;
            }
        };
        VolleySingleton.getInstancia(view.getContext()).agregarACola(favoritos);
        Favoritos.show();
    }

    public void borrarRecomendaciones ()
    {
        String url = IP + "WebService.asmx/borrarRecomendaciones";
        StringRequest borrarRecomendaciones = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta = response.substring(67, response.length()-9);
                if(respuesta.equals("Exito")) obtenerRecomendaciones();
                else Toast.makeText(view.getContext(),"Oops! "+ respuesta, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(),"Oops! Error al borrar recomendaciones", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("secret", PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Secret"));
                params.put("peticion", PkPeticion);
                return  params;
            }
        };
        VolleySingleton.getInstancia(view.getContext()).agregarACola(borrarRecomendaciones);
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
