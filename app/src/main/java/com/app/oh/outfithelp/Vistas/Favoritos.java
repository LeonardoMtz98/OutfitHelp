package com.app.oh.outfithelp.Vistas;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.Localizacion;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.Utilidades.RecyclerViewAdapterDetallesPeticion;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Favoritos extends Fragment {
    private static final String IP = "http://104.210.40.93/";
    private OnFragmentInteractionListener mListener;
    private View view;
    public RecyclerView RVFavoritos;
    public JSONArray favoritos;
    public Dialog dialogFavoritos;
    private String PkRecomendacion;

    public Favoritos() {
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
        view = inflater.inflate(R.layout.fragment_favoritos, container, false);
        RVFavoritos = view.findViewById(R.id.RVFavoritos);
        RVFavoritos.setLayoutManager(new GridLayoutManager(view.getContext(),2));
        obtenerFavoritos();
        return view;
    }

    public void obtenerFavoritos()
    {
        String url = IP + "WebService.asmx/getFavoritos";
        StringRequest Favoritos = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mostrarRecomendaciones(response);
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
        VolleySingleton.getInstancia(view.getContext()).agregarACola(Favoritos);
    }

    public void mostrarRecomendaciones (String response)
    {
        String respuesta = response.substring(67,response.length()-9);
        try {
            favoritos = new JSONArray(respuesta);
            RecyclerViewAdapterDetallesPeticion adapter = new RecyclerViewAdapterDetallesPeticion(favoritos);
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vista) {
                    abrirDetalles(vista);
                }
            });
            RVFavoritos.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), "Oops! Error al cargar recomendaciones", Toast.LENGTH_SHORT).show();
        }
    }

    public void abrirDetalles( View vista)
    {
        dialogFavoritos = new Dialog(view.getContext());
        dialogFavoritos.setContentView(R.layout.dialog_detalles_favoritos);
        ImageView IVVestido = dialogFavoritos.findViewById(R.id.IVVestidoDF);
        ImageView IVZapatos = dialogFavoritos.findViewById(R.id.IVZapatosDF);
        ImageView IVCamisa = dialogFavoritos.findViewById(R.id.IVCamisaDF);
        ImageView IVPantalon = dialogFavoritos.findViewById(R.id.IVPantalonDF);
        ImageButton IBCerrar = dialogFavoritos.findViewById(R.id.IBCerrarDF);
        TextView TVFecha = dialogFavoritos.findViewById(R.id.TVFechaDF);
        TextView TVEvento = dialogFavoritos.findViewById(R.id.TVEventoDF);
        TextView TVDescripcion = dialogFavoritos.findViewById(R.id.TVDescripcionDF);
        final CheckBox CBFavoritos = dialogFavoritos.findViewById(R.id.CBFavoritoDF);
        CBFavoritos.setChecked(true);
        try {
            JSONObject datos = favoritos.getJSONObject(RVFavoritos.getChildAdapterPosition(vista));
            Picasso.get().load(IP + datos.getString("vestido")).fit().into(IVVestido);
            Picasso.get().load(IP + datos.getString("zapatos")).fit().into(IVZapatos);
            Picasso.get().load(IP + datos.getString("camisa")).fit().into(IVCamisa);
            Picasso.get().load(IP + datos.getString("pantalon")).fit().into(IVPantalon);
            TVFecha.setText(datos.getString("fecha").replace("T", " "));
            TVEvento.setText(datos.getString("evento"));
            TVDescripcion.setText(datos.getString("descripcion"));
            PkRecomendacion = datos.getString("recomendacion");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CBFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CBFavoritos.isChecked()) cambiarStatus("Gustada");
                else cambiarStatus("Guardada");
            }
        });
        IBCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFavoritos.dismiss();
            }
        });
        dialogFavoritos.show();
    }

    public void cambiarStatus (final String Status)
    {
        String url = IP + "WebService.asmx/setStatusRecomendacion";
        StringRequest status = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta = response.substring(67, response.length()-9);
                Log.e("Response", response);
                Log.e("Respuesta", respuesta);
                if (!respuesta.contains("Exito")) Toast.makeText(view.getContext(), "Oops! "+ respuesta, Toast.LENGTH_SHORT).show();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
