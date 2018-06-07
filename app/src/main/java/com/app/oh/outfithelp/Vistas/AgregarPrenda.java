package com.app.oh.outfithelp.Vistas;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.CameraOH;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.provider.MediaStore;

import static android.app.Activity.RESULT_OK;
import static com.app.oh.outfithelp.Vistas.MiArmario.CATEGORIA;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AgregarPrenda.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AgregarPrenda extends Fragment {
    private OnFragmentInteractionListener mListener;
    private View view;
    private String categoria;
    private String PkCategoria;
    private ImageButton IBCerrarVentana;
    private TextView TVTitulo;
    private ImageView IVFoto;
    private TextView TVInfoFoto;
    private ImageButton IBTomarFoto;
    private ImageButton IBRecortarFoto;
    private Button BTEnviar;
    private Uri uriFoto;
    private String DireccionFoto;
    public int CAMARA = 1;
    public int CROP = 2;
    private Bitmap imagen;
    private Boolean agregada = false;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    public AgregarPrenda() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            PkCategoria = getArguments().getString("Categoria", "1");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_agregar_prenda, container, false);
        IBCerrarVentana = view.findViewById(R.id.IBCerrarVentana);
        IBCerrarVentana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarFragment();
            }
        });
        TVTitulo = view.findViewById(R.id.TVTitulo);
        TVTitulo.setText("Agregar ");
        getTitulo();
        IVFoto = view.findViewById(R.id.IVFoto);
        TVInfoFoto = view.findViewById(R.id.TVInfoFoto);
        IBTomarFoto = view.findViewById(R.id.IBTomarFoto);
        IBTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFoto();
            }
        });
        IBTomarFoto.setEnabled(false);
        IBRecortarFoto = view.findViewById(R.id.IBRecortarFoto);
        IBRecortarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recortar();
            }
        });
        IBRecortarFoto.setVisibility(View.GONE);
        BTEnviar = view.findViewById(R.id.BTEnviar);
        BTEnviar.setEnabled(false);
        BTEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPrenda();
            }
        });
        return view;
    }

    private void recortar() {
        try {
            Intent Recorte = new Intent("com.android.camera.action.CROP");
            Recorte.setDataAndType(uriFoto, "image/*");
            Recorte.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            Recorte.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Recorte.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(DireccionFoto)));
            startActivityForResult(Recorte, CROP);
        }
        catch (ActivityNotFoundException e) {

        }
    }

    private void cerrarFragment() {
        if (agregada) {

        } else {
            File imagen;
            if (!(DireccionFoto == null)) {
                if (DireccionFoto.isEmpty()) {
                } else {
                    imagen = new File(DireccionFoto);
                    if (imagen.exists()) imagen.delete();
                }
            }
        }
        getFragmentManager().beginTransaction().remove(AgregarPrenda.this).commit();
    }

    private void getTitulo() {
        StringRequest peticionCategorias = new StringRequest(Request.Method.POST, MostrarRopa.IP + "WebService.asmx/DevolverTablaCategorias", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respuesta = response.substring(67,response.length()-9);
                try {
                    JSONArray categorias = new JSONArray(respuesta);
                    for (int i = 0; i < categorias.length(); i++){
                        JSONObject unaCategoria = categorias.getJSONObject(i);
                        if(unaCategoria.getString("PkCategoria").equals(PkCategoria)) {
                            TVTitulo.append(unaCategoria.getString("Nombre"));
                            categoria = unaCategoria.getString("Nombre");
                            IBTomarFoto.setEnabled(true);
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(AgregarPrenda.this.getContext(), "Oops! Error al leer la lista de categorias.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AgregarPrenda.this.getContext(), "Oops! Error al pedir la lista de categorias.", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstancia(AgregarPrenda.this.getContext()).agregarACola(peticionCategorias);
    }

    private void sendPrenda() {
        TVInfoFoto.setText("Enviando...");
        IBCerrarVentana.setEnabled(false);
        IBTomarFoto.setEnabled(false);
        imagen.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        final byte [] imagenEnBytes = baos.toByteArray();
        final String imagenEnString = Base64.encodeToString(imagenEnBytes, Base64.NO_WRAP);
        StringRequest peticionEnvioPrenda = new StringRequest(Request.Method.POST, OutfitHelp.url + "sendPrenda", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray respuesta = null;
                Log.e("Respuesta pura:", response);
                try {
                    Log.e("Respuesta recortada:", response.substring(67, response.length() - 9));
                    respuesta = new JSONArray(response.substring(67, response.length() - 9));
                    if (respuesta.getString(0).equals("Exito")) {
                        Toast.makeText(AgregarPrenda.this.getContext(), respuesta.getString(0), Toast.LENGTH_SHORT).show();
                        TVInfoFoto.setText("Enviada");
                        String nombreArchivo = respuesta.getString(1).replace("img/", "");
                        File imageFile = new File(DireccionFoto);
                        if (imageFile.renameTo(crearArchivo(nombreArchivo, AgregarPrenda.this.getContext()))) {
                            Toast.makeText(AgregarPrenda.this.getContext(), "Renombrado con exito", Toast.LENGTH_SHORT).show();
                            DireccionFoto = imageFile.getAbsolutePath();
                        }
                        else Toast.makeText(AgregarPrenda.this.getContext(), "Error al renombrar", Toast.LENGTH_SHORT).show();
                        agregada = true;
                        IBCerrarVentana.setEnabled(true);
                        IBTomarFoto.setEnabled(true);
                        BTEnviar.setEnabled(true);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Oops! Error al leer la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                    TVInfoFoto.setText("Indefinido si se subio o no");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AgregarPrenda.this.getContext(), "Oops! Error al enviar prenda", Toast.LENGTH_SHORT).show();
                try {
                    if (error.networkResponse != null) {
                        if (error.networkResponse.data != null) {
                            String resp = new String(error.networkResponse.data, "UTF-8");
                            Log.e("VolleyError", error.toString());
                            Log.d("Error net", resp);
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                TVInfoFoto.setText("Error enviando");
                IBCerrarVentana.setEnabled(true);
                IBTomarFoto.setEnabled(true);
                BTEnviar.setEnabled(true);
                File imagen;
                if (!(DireccionFoto == null)) {
                    if (DireccionFoto.isEmpty()) {
                    } else {
                        imagen = new File(DireccionFoto);
                        if (imagen.exists()) imagen.delete();
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String secret = PreferencesConfig.getInstancia(AgregarPrenda.this.getContext()).getFromSharedPrefs(OutfitHelp.SECRET);
                params.put("secret", secret);
                params.put("categoria", PkCategoria);
                params.put("imagen", imagenEnString);
                return params;
            }
        };
        peticionEnvioPrenda.setRetryPolicy(new DefaultRetryPolicy(20000, 0, 0));
        VolleySingleton.getInstancia(this.getContext()).agregarACola(peticionEnvioPrenda);
    }

    private void tomarFoto() {
        Intent intentFoto = new Intent(this.getContext(), CameraOH.class);
        File imagen = crearArchivo();
        uriFoto = FileProvider.getUriForFile(AgregarPrenda.this.getContext(), "com.app.oh.outfithelp", imagen);
        DireccionFoto = imagen.getAbsolutePath();

        intentFoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentFoto.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intentFoto.putExtra(CATEGORIA, categoria);
        intentFoto.putExtra(CameraOH.EXTRA_OUTPUT, DireccionFoto);

        startActivityForResult(intentFoto, CAMARA);
    }
    public File crearArchivo() {
        File archivo = this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = null;
        try {
            imagen = File.createTempFile(
                    "OutfitHelpImg",
                    ".jpg",
                    archivo
            );
        } catch (IOException e) {
            Toast.makeText(AgregarPrenda.this.getContext(), "Oops! Error creando archivo", Toast.LENGTH_SHORT).show();
        }
        return imagen;
    }
    public static File crearArchivo(String nombreArchivo, Context context) {
        File archivo = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = new File(archivo, nombreArchivo);
        return imagen;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMARA && resultCode == RESULT_OK) {
            try {
                imagen = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), uriFoto);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                imagen = Bitmap.createBitmap(imagen, 0, 0, imagen.getWidth(), imagen.getHeight(), matrix, true);
                IVFoto.setImageBitmap(imagen);
                TVInfoFoto.setText("Validando...");
                IBTomarFoto.setEnabled(false);
                IBCerrarVentana.setEnabled(false);
                validarImagen();

            } catch (IOException e) {
                Toast.makeText(AgregarPrenda.this.getContext(), "Oops! Error obteniendo imagen", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == CROP && resultCode == RESULT_OK) {
            try {
                imagen =  MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), uriFoto);
                IVFoto.setImageBitmap(imagen);
            } catch (IOException e) {
                Toast.makeText(AgregarPrenda.this.getContext(), "Oops! Error obteniendo imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validarImagen() {
        final String SubKey = "b4ddd710c7904ae8acba1b6b9e9fd120";
        String URL = "https://westus.api.cognitive.microsoft.com/vision/v1.0/analyze?visualFeatures=Faces, Adult&language=en";

        StringRequest apiRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                validarRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Oops! Error al validar la imagen", Toast.LENGTH_SHORT).show();
                try {
                    if (error.networkResponse != null) {
                        if (error.networkResponse.data != null) {
                            String resp = new String(error.networkResponse.data, "UTF-8");
                            Log.e("VolleyError", error.toString());
                            Log.d("Error net", resp);
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                IBTomarFoto.setEnabled(true);
                IBCerrarVentana.setEnabled(true);
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/octet-stream";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Ocp-Apim-Subscription-Key", SubKey);
                return headers;
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                imagen.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                byte[] imageBytes = baos.toByteArray();
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return imageBytes;
            }
        };
        VolleySingleton.getInstancia(this.getContext()).agregarACola(apiRequest);
    }

    private void validarRespuesta(String response) {
        try {
            JSONObject Datos = new JSONObject(response);
            JSONArray faces = Datos.getJSONArray("faces");
            JSONObject datosAdulto = Datos.getJSONObject("adult");
            if (datosAdulto.getBoolean("isAdultContent") && datosAdulto.getBoolean("isRacyContent")) {
                TVInfoFoto.setText("Oh oh, Imagen inadecuada, intenta con otra");
                BTEnviar.setEnabled(false);
            }
            else {
                if (faces != null && faces.length() > 0) {
                    JSONObject caraMasAbajo = faces.getJSONObject(0).getJSONObject("faceRectangle");
                    for (int i = 0; i < faces.length(); i++) {
                        JSONObject cara = faces.getJSONObject(i).getJSONObject("faceRectangle");
                        if (cara.getInt("top") + cara.getInt("height") > caraMasAbajo.getInt("top") + caraMasAbajo.getInt("height"))
                            caraMasAbajo = cara;
                    }
                    int y = caraMasAbajo.getInt("top") + caraMasAbajo.getInt("height");
                    imagen = Bitmap.createBitmap(
                            imagen,
                            0,
                            y,
                            imagen.getWidth(),
                            imagen.getHeight() - y);
                    File archivo = new File(DireccionFoto);
                    FileOutputStream fout = new FileOutputStream(archivo);
                    imagen.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                    fout.close();
                }
                TVInfoFoto.setText("Imagen Válida");
                IVFoto.setImageBitmap(imagen);
                IBRecortarFoto.setVisibility(View.VISIBLE);
                BTEnviar.setEnabled(true);
            }
        } catch (IOException ex) {
            Toast.makeText(this.getContext(), "Oops! Error al guardar imagen validada", Toast.LENGTH_SHORT).show();
        } catch (JSONException ex) {
            Toast.makeText(this.getContext(), "Oops! Error al leer la respuesta del servidor", Toast.LENGTH_SHORT).show();
        }
        IBTomarFoto.setEnabled(true);
        IBCerrarVentana.setEnabled(true);
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
        cerrarFragment();
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
