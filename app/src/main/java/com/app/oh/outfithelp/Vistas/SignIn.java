package com.app.oh.outfithelp.Vistas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.oh.outfithelp.R;
import com.app.oh.outfithelp.Utilidades.PreferencesConfig;
import com.app.oh.outfithelp.Utilidades.Secret;
import com.app.oh.outfithelp.Utilidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignIn extends Activity {
    private EditText ETCorreoElectronico;
    private TextView TVErrorMail;
    private EditText ETContraseña;
    private TextView TVErrorFormPass;
    private EditText ETConfirmarContraseña;
    private TextView TVErrorPass;
    private RadioGroup RadioGroupGenero;
    private RadioButton RadioHombre;
    private RadioButton RadioMujer;
    private TextView TVErrorSexo;
    private Spinner SpinnerPaises;
    private Spinner SpinnerEstados;
    private TextView TVErrorEstado;
    private TextView TVUsername;
    private ImageButton IBTActualizarUsername;
    private CheckBox CBTyC;
    private TextView TVErrorTyC;
    private TextView TVTerminosYCondiciones;
    private Button BTRegistrarse;
    private TextView tvIniciaSesion;
    private HashMap<String, Integer> Paises;
    private HashMap<String, Integer> Estados;
    private int GENERO_HOMBRE = 1;
    private int GENERO_MUJER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ETCorreoElectronico = findViewById(R.id.ETCorreoElectronico);
        TVErrorMail = findViewById(R.id.TVErrorMail);
        ETContraseña = findViewById(R.id.ETContraseña);
        TVErrorFormPass = findViewById(R.id.TVErrorFormPass);
        ETConfirmarContraseña = findViewById(R.id.ETConfirmarContraseña);
        TVErrorPass = findViewById(R.id.TVErrorPass);
        RadioGroupGenero = findViewById(R.id.RadioGroupGenero);
        RadioHombre = findViewById(R.id.RadioHombre);
        RadioMujer = findViewById(R.id.RadioMujer);
        TVErrorSexo = findViewById(R.id.TVErrorSexo);
        SpinnerPaises = findViewById(R.id.SpinnerPaises);
        SpinnerEstados = findViewById(R.id.SpinnerEstados);
        TVErrorEstado = findViewById(R.id.TVErrorEstado);
        SpinnerEstados.setEnabled(false);
        TVUsername = findViewById(R.id.TVUsername);
        IBTActualizarUsername= findViewById(R.id.IBTActualizarUsername);
        CBTyC = findViewById(R.id.CBTyC);
        TVTerminosYCondiciones = findViewById(R.id.TVTerminosYCondiciones);
        TVTerminosYCondiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog miD = new Dialog(SignIn.this);
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
                        Toast.makeText(SignIn.this, "Oops! Error al obtener terminos y condiciones", Toast.LENGTH_SHORT).show();
                    }
                });
                VolleySingleton.getInstancia(SignIn.this).agregarACola(peticionTyC);
            }
        });
        TVErrorTyC = findViewById(R.id.TVErrorTyC);
        BTRegistrarse = findViewById(R.id.BTRegistrarse);
        tvIniciaSesion = findViewById(R.id.TVIniciaSesion);

        Paises = new HashMap<>();
        Estados = new HashMap<>();
        RadioHombre.setId(GENERO_HOMBRE);
        RadioMujer.setId(GENERO_MUJER);
        IBTActualizarUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llenarUsername();
            }
        });
        BTRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registro();
            }
        });
        tvIniciaSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntent = new Intent(SignIn.this, LogIn.class);
                startActivity(miIntent);
                SignIn.this.finish();
            }
        });
        SpinnerPaises.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!SpinnerPaises.getSelectedItem().equals("Seleccione Pais")) {
                    SpinnerEstados.setEnabled(true);
                    llenarSpinnerEstados();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        llenarUsername();
        llenarSpinnerPaises();

    }

    private void llenarUsername() {
        StringRequest peticionUsername = new StringRequest(Request.Method.POST, OutfitHelp.url + "crearUsername", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String Respuesta = response.substring(67, response.length()-9);
                TVUsername.setText(Respuesta);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignIn.this, "Oops! Error obteniendo username.", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstancia(SignIn.this).agregarACola(peticionUsername);
    }

    private void registro() {
        TVErrorMail.setText("");
        TVErrorPass.setText("");
        TVErrorSexo.setText("");
        TVErrorEstado.setText("");
        TVErrorTyC.setText("");
        boolean formatoCorreo = isformatoCorreoCorrecto();
        boolean contraseñaCorrecta = isContraseñaCorrecta();
        boolean sexoElegido = isSexoElegido();
        boolean estadoElegido = isEstadoElegido();
        boolean formatoContraseña = isFormatoContraseñaCorrecto();
        boolean TyC = isTyCaceptados();
        if (formatoCorreo && contraseñaCorrecta && sexoElegido && estadoElegido && formatoContraseña && TyC) {
            String username = TVUsername.getText().toString();
            String correoElectronico = ETCorreoElectronico.getText().toString().trim().toLowerCase(Locale.ENGLISH);
            String contraseña = ETContraseña.getText().toString().trim();
            int sexo = RadioGroupGenero.getCheckedRadioButtonId();
            int estado = Estados.get(SpinnerEstados.getSelectedItem().toString());
            enviarRegistro(username, correoElectronico, contraseña, sexo, estado);
        }
        else {
            Toast.makeText(this, "Revisa los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isTyCaceptados() {
        if (CBTyC.isChecked()) return true;
        else {
            TVErrorTyC.setText("Acepta los terminos y condiciones");
            return false;
        }
    }

    private boolean isFormatoContraseñaCorrecto() {
        boolean res;
        String contraseña = ETContraseña.getText().toString();
        if (contraseña.contains("$") || contraseña.contains("!") || contraseña.contains("&") || contraseña.contains("%")) {
            TVErrorFormPass.setText("La contraseña no puede contener '$', '&', '!' ni '%'");
            return false;
        }
        else {
            if(contraseña.length() >= 8) {
                if (contraseña.contains("0") || contraseña.contains("1") || contraseña.contains("2") || contraseña.contains("3") || contraseña.contains("4") || contraseña.contains("5") ||contraseña.contains("6") || contraseña.contains("7") || contraseña.contains("8") || contraseña.contains("9")) return true;
                else {
                    TVErrorFormPass.setText("La contraseña debe tener al menos un numero");
                    res = false;
                }
            }
            else {
                TVErrorFormPass.setText("La contraseña debe tener 8 caracteres minimo, longitud: " + contraseña.length());
                return false;
            }
        }
        return res;
    }

    private boolean isEstadoElegido() {
        boolean res;
        if (SpinnerEstados.getSelectedItem() != null) {
            if (SpinnerEstados.getSelectedItem().toString().equals("Seleccione Estado") || SpinnerEstados.getSelectedItem() == null) {
                TVErrorEstado.setText("Selecciona un estado");
                res = false;
            }
            else res = true;
        }
        else {
            TVErrorEstado.setText("Selecciona un estado");
            res = false;
        }
        return res;
    }

    private boolean isSexoElegido() {
        boolean res;
        if (RadioGroupGenero.getCheckedRadioButtonId() == -1) {
            TVErrorSexo.setText("Elige un sexo");
            res = false;
        }
        else res = true;
        return res;
    }

    private boolean isContraseñaCorrecta() {
        boolean res;
        String pass, pass1;
        pass = ETContraseña.getText().toString();
        pass1 = ETConfirmarContraseña.getText().toString();
        if (pass.equals("") || pass1.equals("")) {
            TVErrorPass.setText("Ningun campo puede estar vacio");
            res = false;
        } else {
            if (pass.equals(pass1)) res = true;
            else {
                TVErrorPass.setText("Las contraseñas no coinciden");
                res = false;
            }
        }
        return res;
    }

    private boolean isformatoCorreoCorrecto() {
        boolean res;
        String correoElectronico = ETCorreoElectronico.getText().toString().trim();
        String dominio;
        if (correoElectronico.contains("@")) {
            dominio = correoElectronico.split("@")[1];
            if (dominio.contains(".")) {
                if (dominio.split("\\.")[1].equals("com")) {
                    if (correoElectronico.contains("$") || correoElectronico.contains("!") || correoElectronico.contains("&") || correoElectronico.contains("%")){
                        TVErrorMail.setText("El correo no puede contener '$', '&', '!' ni '%'");
                        res = false;
                    }
                    else res = true;
                } else res = false;
            }
            else {
                TVErrorMail.setText("El formato del correo electrónico es incorrecto, falta punto");
                res = false;
            }
        }
        else {
            TVErrorMail.setText("El formato del correo electrónico es incorrecto, falta @");
            res = false;
        }
        return res;
    }

    private void enviarRegistro(final String username, final String correoElectronico, final String contraseña, final int sexo, final int estado) {
        StringRequest peticionRegistro = new StringRequest(Request.Method.POST, OutfitHelp.url + "SignUp", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String Respuesta = response.substring(67, response.length() - 9);
                String Sexo;
                if (Respuesta.equals("Exito")) {
                    PreferencesConfig.getInstancia(SignIn.this).agregarASharedPrefs(OutfitHelp.SECRET, Secret.getInstancia(SignIn.this).code(username, contraseña));
                    PreferencesConfig.getInstancia(SignIn.this).agregarASharedPrefs(OutfitHelp.CORREO, correoElectronico);
                    PreferencesConfig.getInstancia(SignIn.this).agregarASharedPrefs(OutfitHelp.SESION_INICIADA, "true");
                    if (sexo == 0) Sexo = "M"; else Sexo = "H";
                    PreferencesConfig.getInstancia(SignIn.this).agregarASharedPrefs(OutfitHelp.SEXO, Sexo);
                    PreferencesConfig.getInstancia(SignIn.this).agregarASharedPrefs(OutfitHelp.USERNAME, username);
                    Intent miIntent = new Intent(SignIn.this, OutfitHelp.class);
                    startActivity(miIntent);
                    SignIn.this.finish();
                }else {
                    TVErrorMail.setText(Respuesta);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignIn.this, "Oops! Error enviando registro.", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String sexoString = "";
                if (sexo == 0) sexoString = "false";
                else if (sexo == 1) sexoString = "true";
                params.put("username", username);
                params.put("secret", Secret.getInstancia(SignIn.this).code(correoElectronico, contraseña));
                params.put("sexo", sexoString);
                params.put("estado", Integer.toString(estado));
                return params;
            }
        };
        VolleySingleton.getInstancia(SignIn.this).agregarACola(peticionRegistro);

    }


    private void llenarSpinnerPaises() {
        StringRequest PeticionPaises = new StringRequest(Request.Method.POST, OutfitHelp.url + "getPaises", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String Respuesta = response.substring(67, response.length()-9);
                try {
                    JSONArray jArrayPaises = new JSONArray(Respuesta);
                    ArrayList<String> listaPaises = new ArrayList<>();
                    listaPaises.add("Seleccione Pais");
                    for (int i = 0; i < jArrayPaises.length(); i++) {
                        JSONObject jObjectPais = jArrayPaises.getJSONObject(i);
                        Paises.put(jObjectPais.getString("Nombre"), jObjectPais.getInt("PkPais"));
                        listaPaises.add(jObjectPais.getString("Nombre"));
                    }
                    ArrayAdapter<CharSequence> SpinnerPaisesAdapter = new ArrayAdapter(SignIn.this, android.R.layout.simple_spinner_item, listaPaises);
                    SpinnerPaises.setAdapter(SpinnerPaisesAdapter);
                } catch (JSONException e) {
                    Toast.makeText(SignIn.this, "Oops! Error al leer la lista de paises", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignIn.this, "Oops! Error al obtener la lista de paises", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstancia(this).agregarACola(PeticionPaises);
    }
    private void llenarSpinnerEstados() {
        StringRequest PeticionEstados = new StringRequest(Request.Method.POST, OutfitHelp.url + "getEstados", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String Respuesta = response.substring(67, response.length()-9);
                try {
                    JSONArray jArrayEstados = new JSONArray(Respuesta);
                    ArrayList<String> listaEstados = new ArrayList<>();
                    listaEstados.add("Seleccione Estado");
                    for (int i = 0; i < jArrayEstados.length(); i++) {
                        JSONObject jObjectEstado = jArrayEstados.getJSONObject(i);
                        Estados.put(jObjectEstado.getString("Nombre"), jObjectEstado.getInt("PkEstado"));
                        listaEstados.add(jObjectEstado.getString("Nombre"));
                    }
                    ArrayAdapter<CharSequence> SpinnerEstadosAdapter = new ArrayAdapter(SignIn.this, android.R.layout.simple_spinner_item, listaEstados);
                    SpinnerEstados.setAdapter(SpinnerEstadosAdapter);
                } catch (JSONException e) {
                    Toast.makeText(SignIn.this, "Oops! Error al leer los estados", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignIn.this, "Oops! Error al obtener la lista de estados", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Pais", Paises.get(SpinnerPaises.getSelectedItem().toString()).toString());
                return params;
            }
        };
        VolleySingleton.getInstancia(SignIn.this).agregarACola(PeticionEstados);
    }
}
