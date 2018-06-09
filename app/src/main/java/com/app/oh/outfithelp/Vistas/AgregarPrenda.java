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
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.nio.channels.InterruptedByTimeoutException;
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
    private LinearLayout LYRotar;
    private ImageButton IBRotarIzq;
    private ImageButton IBRotarDer;
    private ImageButton IBTomarFoto;
    private ImageButton IBRecortarFoto;
    private ImageButton IBValidar;
    private Button BTEnviar;
    private FrameLayout LYEspera;
    private TextView TVInfoCarga;
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
        LYRotar = view.findViewById(R.id.LYRotar);
        LYRotar.setVisibility(View.GONE);
        IBRotarIzq = view.findViewById(R.id.IBRotarIzq);
        LYEspera = view.findViewById(R.id.LYEspera);
        TVInfoCarga = view.findViewById(R.id.TVInfoCarga);
        IBRotarIzq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                imagen = Bitmap.createBitmap(imagen, 0, 0, imagen.getWidth(), imagen.getHeight(), matrix, true);
                IVFoto.setImageBitmap(imagen);
            }
        });
        IBRotarDer = view.findViewById(R.id.IBRotarDer);
        IBRotarDer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                imagen = Bitmap.createBitmap(imagen, 0, 0, imagen.getWidth(), imagen.getHeight(), matrix, true);
                IVFoto.setImageBitmap(imagen);
            }
        });
        IBTomarFoto = view.findViewById(R.id.IBTomarFoto);
        IBTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFoto();
                IBValidar.setEnabled(true);
                TVInfoFoto.setText("");
                IBValidar.setVisibility(View.VISIBLE);
                BTEnviar.setVisibility(View.GONE);
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
        IBValidar = view.findViewById(R.id.IBValidar);
        IBValidar.setEnabled(false);
        IBValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarImagen();
            }
        });
        BTEnviar = view.findViewById(R.id.BTEnviar);
        BTEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPrenda();
            }
        });
        BTEnviar.setVisibility(View.GONE);

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
        LYEspera.setVisibility(View.VISIBLE);
        TVInfoFoto.setText("Enviando...");
        TVInfoCarga.setText("Enviando imagen...");
        IBCerrarVentana.setEnabled(false);
        IBTomarFoto.setEnabled(false);
        try {
            imagen = null;
            imagen = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), uriFoto);
            baos.reset();
            imagen.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            final byte [] imagenEnBytes = baos.toByteArray();
            baos.reset();
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
                    LYEspera.setVisibility(View.GONE);
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
                    LYEspera.setVisibility(View.GONE);
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
        } catch (IOException e) {
            Toast.makeText(this.getContext(), "Oops! Error obteniendo archivo a enviar", Toast.LENGTH_SHORT).show();
        }
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
                IVFoto.setImageBitmap(imagen);
                IBValidar.setEnabled(true);
                LYRotar.setVisibility(View.VISIBLE);
                IBRecortarFoto.setVisibility(View.GONE);
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
        IBCerrarVentana.setEnabled(false);
        BTEnviar.setVisibility(View.GONE);
        LYRotar.setEnabled(false);
        LYEspera.setVisibility(View.VISIBLE);
        TVInfoCarga.setText("Validando imagen...");
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
                LYRotar.setVisibility(View.GONE);
                IBRecortarFoto.setVisibility(View.GONE);
                IBValidar.setEnabled(true);
                LYEspera.setVisibility(View.GONE);
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
                baos.reset();
                imagen.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] imageBytes = baos.toByteArray();
                baos.reset();
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
                BTEnviar.setVisibility(View.GONE);
                IBValidar.setEnabled(false);
                IBTomarFoto.setEnabled(true);
                IBRecortarFoto.setVisibility(View.GONE);
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
                IBValidar.setVisibility(View.GONE);
                BTEnviar.setVisibility(View.VISIBLE);
            }
        } catch (IOException ex) {
            Toast.makeText(this.getContext(), "Oops! Error al guardar imagen validada", Toast.LENGTH_SHORT).show();
        } catch (JSONException ex) {
            Toast.makeText(this.getContext(), "Oops! Error al leer la respuesta del servidor", Toast.LENGTH_SHORT).show();
        }
        LYRotar.setVisibility(View.GONE);
        IBTomarFoto.setEnabled(true);
        IBCerrarVentana.setEnabled(true);
        LYEspera.setVisibility(View.GONE);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
