package com.app.oh.outfithelp.Vistas;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class DetallesRopa extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View view;
    private String imagen;
    private String nombreImagen;
    private ImageView imageView;
    private ImageButton IBEditar;
    private ImageButton IBBorrar;
    private TextView textView;
    private TextView TVInfoFoto;
    private Uri uriFoto;
    private File archivo;
    private int CROP = 1;

    public DetallesRopa() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imagen = getArguments().getString(MostrarRopa.IMAGEN, "0");
            nombreImagen = getArguments().getString(MostrarRopa.DIRECCIONIMG, "0");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detalles_ropa, container, false);
        imageView = view.findViewById(R.id.IVDetallesRopa);
        textView = view.findViewById(R.id.TVCerrarDetallesRopa);

        String nombreArchivo = nombreImagen.replace("img/", "");
        archivo = AgregarPrenda.crearArchivo(nombreArchivo, this.getContext());
        if (archivo.exists()) Picasso.get().load(archivo).into(imageView);
        else Picasso.get().load(imagen).into(imageView);
        IBEditar = view.findViewById(R.id.IBEditar);
        IBEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recortar(archivo);
            }
        });
        IBBorrar = view.findViewById(R.id.IBBorrar);
        TVInfoFoto = view.findViewById(R.id.TVInfoFoto);
        IBBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrar();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrar();
            }
        });
        return view;
    }

    private void recortar(File archivo) {
        uriFoto = FileProvider.getUriForFile(getContext(), "com.app.oh.outfithelp", archivo);
        try {
            Intent Recorte = new Intent("com.android.camera.action.CROP");
            Recorte.setDataAndType(uriFoto, "image/*");
            Recorte.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            Recorte.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Recorte.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(archivo));
            startActivityForResult(Recorte, CROP);
        }
        catch (ActivityNotFoundException e) {

        }
    }

    private void borrar() {
        TVInfoFoto.setText("Borrando...");
        textView.setEnabled(false);
        IBBorrar.setEnabled(false);
        IBEditar.setEnabled(false);
        nombreImagen = nombreImagen.replace("img/", "");
        StringRequest peticionBorrarPrenda = new StringRequest(Request.Method.POST, SignIn.url + "borrarPrenda", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta = response.substring(67, response.length() - 9);
                if (respuesta.equals("\"Exito\"")) {
                    File imagenABorrar = AgregarPrenda.crearArchivo(nombreImagen, DetallesRopa.this.getContext());
                    if (imagenABorrar.exists()) {
                        if (imagenABorrar.delete())
                            Toast.makeText(DetallesRopa.this.getContext(), "Imagen Borrada", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(DetallesRopa.this.getContext(), "Oops! Error al borrar", Toast.LENGTH_SHORT).show();
                    }
                    cerrar();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetallesRopa.this.getContext(), "Oops! Error al pedir borrado", Toast.LENGTH_SHORT).show();
                TVInfoFoto.setText("Error borrando");
                textView.setEnabled(true);
                IBBorrar.setEnabled(true);
                IBEditar.setEnabled(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("secret", PreferencesConfig.getInstancia(DetallesRopa.this.getContext()).getFromSharedPrefs(SignIn.SECRET));
                params.put("nombreImagen", getArguments().getString(MostrarRopa.DIRECCIONIMG, "0"));
                return params;
            }
        };
        VolleySingleton.getInstancia(DetallesRopa.this.getContext()).agregarACola(peticionBorrarPrenda);
    }

    public void cerrar()
    {
        getFragmentManager().beginTransaction().remove(DetallesRopa.this).commit();
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CROP && resultCode == RESULT_OK) {
            Picasso.get().load(archivo).into(imageView);
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
