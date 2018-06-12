package com.app.oh.outfithelp.Vistas;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.Localizacion;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgregarPeticion extends Fragment {

    private static final String IP = "http://104.210.40.93/";
    private OnFragmentInteractionListener mListener;
    private View view;
    private Spinner spinnerEventos;
    private Spinner spinnerAvatar;
    private ImageButton IBCalendario;
    private ImageButton IBReloj;
    private ImageView IVAvatarAgregarPeticion;
    private TextView TVCerrarAgregarPeticion;
    private TextView TVFechaAgregarPeticion;
    private TextView TVHoraAgregarPeticion;
    private Button BTAgregarPeticion;
    private EditText ETDescripcionAgregarPeticion;
    private TextView TVFaltaTipoEvento;
    private TextView TVFaltaFecha;
    private TextView TVFaltaHora;
    private TextView TVFaltaDescripcion;
    private int dia, mes, año, hora, minuto;
    private boolean tipoDeHora;
    private boolean eventoHoy;
    private int tipoDeEvento;
    private Calendar calendar;
    private String fecha, tiempo;
    private HashMap<String, Integer> HashEventos = new HashMap<>();
    private ArrayList<String> listaAvatares;
    private int avatarSeleccionado;

    public AgregarPeticion() {
        // Required empty public constructor
    }

    public static AgregarPeticion newInstance(String param1, String param2) {
        AgregarPeticion fragment = new AgregarPeticion();
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
        view = inflater.inflate(R.layout.fragment_agregar_peticion, container, false);
        spinnerEventos = view.findViewById(R.id.SpinnerEventos);
        spinnerAvatar = view.findViewById(R.id.SpinnerAvatar);
        TVCerrarAgregarPeticion = view.findViewById(R.id.TVCerrarAgregarPeticion);
        IBCalendario = view.findViewById(R.id.IBCalendario);
        IBReloj = view.findViewById(R.id.IBReloj);
        listaAvatares = new ArrayList<String>();
        IVAvatarAgregarPeticion = view.findViewById(R.id.IVAvatarAgregarPeticion);
        TVFechaAgregarPeticion = view.findViewById(R.id.TVFechaAgregarPeticion);
        TVHoraAgregarPeticion = view.findViewById(R.id.TVHoraAgregarPeticion);
        ETDescripcionAgregarPeticion = view.findViewById(R.id.ETDescripcionAgregarPeticion);
        BTAgregarPeticion = view.findViewById(R.id.BTAgregarPeticion);
        ETDescripcionAgregarPeticion = view.findViewById(R.id.ETDescripcionAgregarPeticion);
        TVFaltaTipoEvento = view.findViewById(R.id.TVFaltaTipoEvento);
        TVFaltaFecha = view.findViewById(R.id.TVFaltaFecha);
        TVFaltaHora = view.findViewById(R.id.TVFaltaHora);
        TVFaltaDescripcion = view.findViewById(R.id.TVFaltaDescripcion);
        calendar = Calendar.getInstance();
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH);
        año = calendar.get(Calendar.YEAR);
        hora = calendar.get(Calendar.HOUR_OF_DAY);
        minuto = calendar.get(Calendar.MINUTE);
        IBReloj.setEnabled(false);
        try {
            Localizacion.getInstancia(view.getContext()).iniciarDeteccionUbicacion();
        }catch (SecurityException ex){
            pedirPermisosUbicacion();
        }
        obtenerAvatares();
        obtenerTiposDeEvento();
        TVCerrarAgregarPeticion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(AgregarPeticion.this).commit();
            }
        });
        BTAgregarPeticion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        IBCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                abrirCalendario();
            }
        });
        IBReloj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirReloj();
            }
        });
        spinnerEventos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tipoDeEvento = i;
                TVFaltaTipoEvento.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerAvatar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View vista, int seleccion, long l) {
                String url = IP + listaAvatares.get(seleccion);
                Picasso.get().load(url).fit().into(IVAvatarAgregarPeticion);
                avatarSeleccionado = seleccion + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        BTAgregarPeticion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(AgregarPeticion.this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AgregarPeticion.this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    pedirPermisosUbicacion();
                }else checarDatos();
            }
        });
        ETDescripcionAgregarPeticion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TVFaltaDescripcion.setText("");
            }
        });
        return view;
    }

    private void pedirPermisosUbicacion() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());
        builder.setMessage("Por favor activa los servicios de ubicacion y dale permisos a Outfithelp");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", AgregarPeticion.this.getContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.create();
        builder.show();
    }

    public void checarDatos()
    {
        boolean completo = true;
        if (tipoDeEvento == 0){
            TVFaltaTipoEvento.setText("* Selecciona tipo de evento");
            completo = false;
        }
        if (TVFechaAgregarPeticion.getText().equals("")){
            TVFaltaFecha.setText("* Determina una fecha");
            completo = false;
        }
        if (TVHoraAgregarPeticion.getText().equals("")){
            TVFaltaHora.setText("* Determina una hora");
            completo = false;
        }
        if (ETDescripcionAgregarPeticion.getText().toString().equals("")){
            TVFaltaDescripcion.setText("* Agrega una descripcion");
            completo = false;
        }
        if (!TVFaltaFecha.getText().equals("")) completo = false;
        if (!TVFaltaHora.getText().equals("")) completo = false;
        if (completo)
        {
            enviarPeticion();
            Localizacion.getInstancia(view.getContext()).detenerActualizacionesUbicacion();
            getFragmentManager().beginTransaction().remove(AgregarPeticion.this).commit();
        }
    }
    public void obtenerAvatares()
    {
        String url = IP + "WebService.asmx/getAvatares";
        StringRequest avatares = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                spinnerAvatares(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Oops! Error al obtener imagenes", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstancia(view.getContext()).agregarACola(avatares);
    }

    public void spinnerAvatares (String response)
    {
        String respuesta = response.substring(67,response.length()-9);
        JSONArray ArrayAvatares;
        ArrayList<Integer> opciones = new ArrayList<Integer>();
        try {
            ArrayAvatares = new JSONArray(respuesta);
            for (int i = 0; i < ArrayAvatares.length(); i++)
            {
                JSONObject ObjetoEventos = ArrayAvatares.getJSONObject(i);
                opciones.add(ObjetoEventos.getInt("PkAvatar"));
                listaAvatares.add(ObjetoEventos.getString("Direccion"));
            }
            ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter(view.getContext(),
                    android.R.layout.simple_spinner_item, opciones);
            spinnerAvatar.setAdapter(spinnerAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(),"Oops! Error al leer avatares",Toast.LENGTH_SHORT).show();
        }
    }

    public void obtenerTiposDeEvento()
    {
        String url = IP + "WebService.asmx/getTiposDeEvento";
        StringRequest tiposDeEvento = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                spinnerTipoDeEvento(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Oops! Error al obtener tipos de evento", Toast.LENGTH_SHORT).show();
            }
        });
        tiposDeEvento.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,3,1));
        VolleySingleton.getInstancia(view.getContext()).agregarACola(tiposDeEvento);
    }

    public void spinnerTipoDeEvento( String response)
    {
        String respuesta = response.substring(67,response.length()-9);
        JSONArray ArraytiposDeEventos;
        ArrayList<String> listaEventos = new ArrayList<>();
        try {
            ArraytiposDeEventos = new JSONArray(respuesta);
            listaEventos.add("Seleccione");
            for (int i = 0; i < ArraytiposDeEventos.length(); i++)
            {
                JSONObject ObjetoEventos = ArraytiposDeEventos.getJSONObject(i);
                listaEventos.add(ObjetoEventos.getString("Nombre"));
                HashEventos.put(ObjetoEventos.getString("Nombre"), ObjetoEventos.getInt("PkEvento"));
            }
            ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter(view.getContext(),
                                                        android.R.layout.simple_spinner_item, listaEventos);
            spinnerEventos.setAdapter(spinnerAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(),"Oops! Error al leer tipos de evento",Toast.LENGTH_SHORT).show();
        }
    }

    public void enviarPeticion()
    {
        String url = IP + "WebService.asmx/sendPeticion";
        final String date = fecha + " " + tiempo;
        final int evento = HashEventos.get(spinnerEventos.getSelectedItem().toString());
        StringRequest agregarPeticion = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.contains("Exito")){
                    Toast.makeText(view.getContext(), "Oops! Error de autentificacion", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(),"Oops! Error al enviar peticion",Toast.LENGTH_SHORT).show();
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
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                try {
                    params.put("avatar", "" + avatarSeleccionado);
                    params.put("tipoDeEvento", "" + evento);
                    params.put("date", date);

                    params.put("descripcion", new String(ETDescripcionAgregarPeticion.getText().toString().getBytes(), "UTF-8"));
                    params.put("secret", PreferencesConfig.getInstancia(view.getContext()).getFromSharedPrefs("Secret"));
                }catch (UnsupportedEncodingException e) {
                    Toast.makeText(getContext(), "Oops! Error al codificar descripcion", Toast.LENGTH_SHORT).show();
                }
                return params;
            }
        };
        VolleySingleton.getInstancia(view.getContext()).agregarACola(agregarPeticion);

    }

    public void abrirCalendario ()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                TVFechaAgregarPeticion.setText(dayOfMonth+"/" + (monthOfYear+1) + "/" + year );
                if (dayOfMonth < dia && monthOfYear <= mes && year <= año)
                {
                    TVFaltaFecha.setText("Fecha Invalida");
                }
                else if (monthOfYear < mes && year <= año)
                {
                    TVFaltaFecha.setText("Fecha Invalida");
                }
                else if (year < año)
                {
                    TVFaltaFecha.setText("Fecha Invalida");
                }
                else {
                    IBReloj.setEnabled(true);
                    TVFaltaFecha.setText("");
                }
                fecha = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                if (dayOfMonth == dia && monthOfYear == mes && year == año) eventoHoy = true;
            }
        }, año, mes, dia);
        datePickerDialog.show();
    }

    private void abrirReloj ()
    {
        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                TVHoraAgregarPeticion.setText(hour+":" + minute );
                tiempo = hora+":" + minuto;
                if (eventoHoy) {
                    if (hour < hora && minute <minute) TVFaltaHora.setText("Hora invalida");
                    else if (hour == hora && minute < minuto) TVFaltaHora.setText("Hora invalida");
                }
                else {
                    TVFaltaHora.setText("");
                }
            }
        }, hora, minuto, tipoDeHora);
        timePickerDialog.show();
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
