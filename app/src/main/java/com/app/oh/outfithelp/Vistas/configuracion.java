package com.app.oh.outfithelp.Vistas;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.util.SizeF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.Utilidades.Secret;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class configuracion extends Fragment {
    private OnFragmentInteractionListener mListener;
    private View view;
    private TextView TVUsername;
    private TextView TVCorreo;
    private RadioGroup RGGenero;
    private RadioButton RBHombre;
    private RadioButton RBMujer;
    private Button BTVerTyC;
    private Button BTCambiarPass;

    public configuracion() {
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
        view = inflater.inflate(R.layout.fragment_configuracion, container, false);
        TVUsername = view.findViewById(R.id.TVUsername);
        TVUsername.setText(PreferencesConfig.getInstancia(getContext()).getFromSharedPrefs(OutfitHelp.USERNAME));
        TVCorreo = view.findViewById(R.id.TVCorreo);
        TVCorreo.setText(PreferencesConfig.getInstancia(getContext()).getFromSharedPrefs(OutfitHelp.CORREO));
        RBHombre = view.findViewById(R.id.RBHombre);
        RBMujer = view.findViewById(R.id.RBMujer);
        BTVerTyC = view.findViewById(R.id.BTVerTyC);
        BTVerTyC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog miD = new Dialog(getContext());
                miD.setContentView(R.layout.dialog_terminos_y_condiciones);
                ImageButton IBCerrar = miD.findViewById(R.id.IBCerrarVentana);
                miD.show();
                IBCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        miD.dismiss();
                    }
                });
                final TextView TVTerminosYCondiciones = miD.findViewById(R.id.TVTerminosYCondiciones);
                StringRequest peticionTyC = new StringRequest(Request.Method.POST, OutfitHelp.url + "getTyC", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String respuesta = response.substring(67, response.length()-9);
                        TVTerminosYCondiciones.setText(respuesta);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Oops! Error al obtener terminos y condiciones", Toast.LENGTH_SHORT).show();
                    }
                });
                VolleySingleton.getInstancia(getContext()).agregarACola(peticionTyC);
            }
        });
        BTCambiarPass = view.findViewById(R.id.BTCambiarPass);

        if (PreferencesConfig.getInstancia(this.getContext()).getFromSharedPrefs(OutfitHelp.SEXO).equals("M")) {
            RBMujer.setChecked(true);
            RBHombre.setChecked(false);
        } else {
            RBMujer.setChecked(false);
            RBHombre.setChecked(true);
        }
        RBHombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarSexo();
            }
        });
        RBMujer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarSexo();
            }
        });
        BTCambiarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarPass();
            }
        });
        return view;
    }

    private void cambiarPass() {
        final Dialog dialogCambiarPass = new Dialog(view.getContext());
        dialogCambiarPass.setContentView(R.layout.dialog_cambiar_pass);
        final EditText ETContraseña = dialogCambiarPass.findViewById(R.id.ETContraseña);
        final TextView TVInfoPass = dialogCambiarPass.findViewById(R.id.TVInfoPass);
        final EditText ETContraseñaNueva = dialogCambiarPass.findViewById(R.id.ETContraseñaNueva);
        final EditText ETConfContraseñaNueva = dialogCambiarPass.findViewById(R.id.ETConfContraseñaNueva);
        final TextView TVInfoPassNueva = dialogCambiarPass.findViewById(R.id.TVInfoPassNueva);
        Button BTAceptar = dialogCambiarPass.findViewById(R.id.BTAceptarCambiarPass);
        Button BTCancelar = dialogCambiarPass.findViewById(R.id.BTCancelarCambiarPass);
        BTAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TVInfoPass.setText("");
                TVInfoPassNueva.setText("");
                final String passNueva = ETContraseñaNueva.getText().toString();
                String confPassNueva = ETConfContraseñaNueva.getText().toString();
                if (passNueva.equals(confPassNueva)){
                    StringRequest peticionCambioPass = new StringRequest(Request.Method.POST, OutfitHelp.url + "cambiarPass", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String respuesta = response.substring(67, response.length()-9);
                            Log.e("Respuesta", respuesta);
                            if (respuesta.equals("Exito")) {
                                String username = PreferencesConfig.getInstancia(getContext()).getFromSharedPrefs(OutfitHelp.USERNAME);
                                String secret = Secret.getInstancia(getContext()).code(username, passNueva);
                                PreferencesConfig.getInstancia(getContext()).agregarASharedPrefs(OutfitHelp.SECRET, secret);
                                Toast.makeText(getContext(), "Contraseña cambiada", Toast.LENGTH_SHORT).show();
                                dialogCambiarPass.dismiss();
                            }else{
                                TVInfoPass.setText("Contraseña errónea");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Oops! Error al cambiar la contraseña", Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            String Username = PreferencesConfig.getInstancia(getContext()).getFromSharedPrefs(OutfitHelp.USERNAME);
                            String secret = Secret.getInstancia(getContext()).code(Username, ETContraseña.getText().toString());
                            params.put("secret", secret);
                            params.put("passNueva", ETContraseñaNueva.getText().toString());
                            return params;
                        }
                    };
                    VolleySingleton.getInstancia(getContext()).agregarACola(peticionCambioPass);
                } else {
                    TVInfoPassNueva.setText("Las contraseñas no coinciden.");
                }
            }
        });
        BTCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCambiarPass.dismiss();
            }
        });
        dialogCambiarPass.show();
    }

    private void actualizarSexo() {
        final String sexo;
        if (RBHombre.isChecked() && !RBMujer.isChecked()) sexo = "H";
        else sexo = "M";
        PreferencesConfig.getInstancia(this.getContext()).agregarASharedPrefs(OutfitHelp.SEXO, sexo);
        StringRequest peticionCambioSexo = new StringRequest(Request.Method.POST, OutfitHelp.url + "cambiarSexo", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), "Sexo Actualizado", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(configuracion.this.getContext(), "Oops! Error al cambiar sexo en el servidor", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("secret",PreferencesConfig.getInstancia(configuracion.this.getContext()).getFromSharedPrefs(OutfitHelp.SECRET));
                params.put("sexo", sexo);
                return params;
            }
        };
        VolleySingleton.getInstancia(configuracion.this.getContext()).agregarACola(peticionCambioSexo);
    }

    // TODO: Rename method, update argument and hook method into UI event
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
