package com.lulapps.vivienda360;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RegistrateFragment extends Fragment {

    EditText correo, nombre, whatsapp, telefono, respuesta1, respuesta2, contrasena, confirmarContrasena;
    Spinner preguntas1, preguntas2;
    Button registrarme, iniciarsesion;
    TextView error;

    boolean corr, nom, what, preg1, resp1, preg2, resp2, contrase, confirmContrase;

    RequestQueue requestQueue;

    public RegistrateFragment() {
        corr=false;
        nom=false;
        what=false;
        preg1=false;
        resp1=false;
        preg2=false;
        resp2=false;
        contrase=false;
        confirmContrase=false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue= Volley.newRequestQueue(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registrate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        correo= (EditText) view.findViewById(R.id.editTextCorreoMiPerfil);
        nombre= (EditText) view.findViewById(R.id.editTextNombreMiPerfil);
        whatsapp= (EditText) view.findViewById(R.id.editTextWhatsappMiPerfil);
        telefono= (EditText) view.findViewById(R.id.editTextTelefonoMiPerfil);
        respuesta1= (EditText) view.findViewById(R.id.editTextRespuesta1MiPerfil);
        respuesta2= (EditText) view.findViewById(R.id.editTextRespuesta2MiPerfil);
        contrasena= (EditText) view.findViewById(R.id.editTextContrasenaRegistrate);
        confirmarContrasena= (EditText) view.findViewById(R.id.editTextConfirmarContrasenaRegistrate);
        preguntas1= (Spinner) view.findViewById(R.id.spinnerPreguntaSeguridad1MiPerfil);
        preguntas2= (Spinner) view.findViewById(R.id.spinnerPreguntaSeguridad2MiPerfil);
        registrarme= (Button) view.findViewById(R.id.buttonPublicar);
        iniciarsesion= (Button) view.findViewById(R.id.buttonIniciarSesionRegistrate);
        error= (TextView) view.findViewById(R.id.textViewErrorPublicar);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getContext(),
                R.array.preguntas_seguridad_1, android.R.layout.simple_spinner_item);
        preguntas1.setAdapter(adapter1);


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getContext(),
                R.array.preguntas_seguridad_2, android.R.layout.simple_spinner_item);
        preguntas2.setAdapter(adapter2);

        correo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (correo.getText().toString().trim().isEmpty() || correo.getText().toString().trim().equals(null)){
                    corr=false;
                }
                else{
                    if (correo.getText().toString().contains("@") && correo.getText().toString().substring(correo.getText().toString().indexOf("@")).contains(".")){
                        if (!correo.getText().toString().contains(" ")){
                            corr=true;
                        }
                        else{
                            corr=false;
                        }
                    }
                    else{
                        corr=false;
                    }
                }
                VerificarCondiciones();
            }
        });

        nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(nombre.getText().toString().isEmpty() || nombre.getText().toString().equals(null)){
                    nom=false;
                }
                else{
                    nom=true;
                }
                VerificarCondiciones();
            }
        });

        whatsapp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(whatsapp.getText().toString().isEmpty() || whatsapp.getText().toString().equals(null)){
                    what=false;
                }
                else{
                    what=true;
                }
                VerificarCondiciones();
            }
        });

        preguntas1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    preg1=true;
                }
                else{
                    preg1=false;
                }
                VerificarCondiciones();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        preguntas2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    preg2=true;
                }
                else{
                    preg2=false;
                }
                VerificarCondiciones();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        respuesta1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(respuesta1.getText().toString().isEmpty() || respuesta1.getText().toString().equals(null) || respuesta1.getText().toString().trim().length()==0){
                    resp1=false;
                }
                else{
                    resp1=true;
                }
                VerificarCondiciones();
            }
        });

        respuesta2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(respuesta2.getText().toString().isEmpty() || respuesta2.getText().toString().equals(null) || respuesta1.getText().toString().trim().length()==0){
                    resp2=false;
                }
                else{
                    resp2=true;
                }
                VerificarCondiciones();
            }
        });

        contrasena.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(contrasena.getText().toString().isEmpty() || contrasena.getText().toString().equals(null)){
                    contrase=false;
                }
                else{
                    contrase=true;
                }
                VerificarCondiciones();
            }
        });

        confirmarContrasena.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(confirmarContrasena.getText().toString().isEmpty() || confirmarContrasena.getText().toString().equals(null)){
                    confirmContrase=false;
                }
                else{
                    confirmContrase=true;
                }
                VerificarCondiciones();
            }
        });

        registrarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                String revisionContrasena;
                revisionContrasena=condicionesContrasena(contrasena.getText().toString(), confirmarContrasena.getText().toString());
                if(revisionContrasena.equals("Error_length")){
                    //Toast.makeText(getContext(),R.string.error_longitud_contrasena, Toast.LENGTH_SHORT).show();
                    error.setText(R.string.error_longitud_contrasena);
                }
                else{
                    if (revisionContrasena.equals("Error_num")){
                        //Toast.makeText(getContext(),R.string.error_numeros_contrasena, Toast.LENGTH_SHORT).show();
                        error.setText(R.string.error_numeros_contrasena);
                    }
                    else{
                        if (revisionContrasena.equals("Error_equals")){
                            //Toast.makeText(getContext(),R.string.error_coincidencia_contrasena, Toast.LENGTH_SHORT).show();
                            error.setText(R.string.error_coincidencia_contrasena);
                        }
                        else{
                            registrarme.setEnabled(false);
                            registrarme.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));

                            String textoCorreo;

                            textoCorreo=convertirCadena(correo.getText().toString().trim().toLowerCase());

                            String URL_VERIFICAR_CORREO= "https://udvivienda360.000webhostapp.com/verificar_correo_registrado.php?correo="+textoCorreo;
                            StringRequest stringRequestCorreo= new StringRequest(
                                    Request.Method.GET,
                                    URL_VERIFICAR_CORREO,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            if(response.equals("registrado")){
                                                //Toast.makeText(getContext(), R.string.correo_repetido, Toast.LENGTH_LONG).show();
                                                error.setText(R.string.correo_repetido);
                                                registrarme.setEnabled(true);
                                                registrarme.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
                                                System.out.println("lucho 3: email registrado");
                                            }
                                            else{
                                                if (response.equals("no_registrado")){
                                                    System.out.println("lucho 3: email no registrado");
                                                    String textoContrasena, textoNombre, textoWhatsapp, textoTelefono, textoPregunta1, textoRespuesta1, textoPregunta2, textoRespuesta2;

                                                    textoContrasena=convertirCadena(contrasena.getText().toString());

                                                    textoNombre=convertirCadena(nombre.getText().toString());
                                                    String pasowha;
                                                    if (whatsapp.getText().toString().length()==10 && !whatsapp.getText().toString().contains("+")){
                                                        pasowha="+57"+whatsapp.getText().toString();
                                                    }
                                                    else{
                                                        pasowha=whatsapp.getText().toString();
                                                    }
                                                    textoWhatsapp=convertirCadena(pasowha);

                                                    textoTelefono=convertirCadena(telefono.getText().toString().trim());

                                                    textoPregunta1=convertirCadena(preguntas1.getSelectedItem().toString());

                                                    textoRespuesta1=convertirCadena(respuesta1.getText().toString().trim());

                                                    textoPregunta2=convertirCadena(preguntas2.getSelectedItem().toString());

                                                    textoRespuesta2=convertirCadena(respuesta2.getText().toString().trim());
                                                    String URL_REGISTRO= "https://udvivienda360.000webhostapp.com/registrar_usuario.php";

                                                    System.out.println("lucho 3: URL="+URL_REGISTRO);

                                                    StringRequest stringRequest= new StringRequest(
                                                            Request.Method.POST,
                                                            URL_REGISTRO,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    if(response.equals("OK")){
                                                                        System.out.println("lucho 3: nuevo usuario registrado");
                                                                        Toast.makeText(getContext(), R.string.registro_exitoso, Toast.LENGTH_SHORT).show();
                                                                        FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                                                        LoginFragment loginFragment= new LoginFragment();
                                                                        fragmentTransaction.replace(R.id.contenedor_ppal_perfil, loginFragment);
                                                                        registrarme.setEnabled(true);
                                                                        registrarme.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
                                                                        fragmentTransaction.commit();
                                                                    }
                                                                    else{
                                                                        System.out.println("lucho 3: error inesperado");
                                                                        //Toast.makeText(getContext(), R.string.error_inesperado, Toast.LENGTH_SHORT).show();
                                                                        error.setText(R.string.error_inesperado);
                                                                        registrarme.setEnabled(true);
                                                                        registrarme.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
                                                                    }
                                                                    System.out.println("lucho 3: "+response);
                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error2) {
                                                                    System.out.println("lucho 3: "+error2.getMessage());
                                                                    error.setText(error2.getMessage());
                                                                    registrarme.setEnabled(true);
                                                                    registrarme.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
                                                                }
                                                            }
                                                    ){
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                            Map<String, String> parametros= new HashMap<>();
                                                            parametros.put("correo",textoCorreo);
                                                            parametros.put("contrasena",textoContrasena);
                                                            parametros.put("nombre",textoNombre);
                                                            parametros.put("whatsapp",textoWhatsapp);
                                                            parametros.put("numero_contacto",textoTelefono);
                                                            parametros.put("pregunta_1",textoPregunta1);
                                                            parametros.put("respuesta_1",textoRespuesta1);
                                                            parametros.put("pregunta_2",textoPregunta2);
                                                            parametros.put("respuesta_2",textoRespuesta2);
                                                            return parametros;
                                                        }
                                                    };
                                                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                                    requestQueue.add(stringRequest);


                                                }
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    }
                            );
                            stringRequestCorreo.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                            requestQueue.add(stringRequestCorreo);
                        }
                    }
                }
            }
        });

        iniciarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                LoginFragment loginFragment= new LoginFragment();
                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, loginFragment);
                fragmentTransaction.commit();
            }
        });
    }

    private void VerificarCondiciones() {
        if(corr && nom && what && preg1 && resp1 && preg2 && resp2 && contrase && confirmContrase){
            registrarme.setEnabled(true);
            registrarme.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
        }
        else{
            registrarme.setEnabled(false);
            registrarme.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
        }
        error.setText("");
    }

    @NotNull
    private String condicionesContrasena(@NotNull String password, @NotNull String confirmpassword){
        String result="";
        boolean min8char=false, min1num=false;
        if (password.length()>=8){
            min8char=true;
        }
        for(int i=0; i<password.length(); i++){
            int ascii= (int) password.charAt(i);
            if(ascii>=48 && ascii<=57){
                min1num=true;
                break;
            }
        }
        if (!min8char){
            return "Error_length";
        }
        else{
            if (!min1num){
                return "Error_num";
            }
            else{
                if(!password.equals(confirmpassword)){
                    return "Error_equals";
                }
                else{
                    return "Ok";
                }
            }
        }
    }

    @NotNull
    private String ArreglarURLs(@NotNull String cadena){
        String CadenaFinal=cadena.replace(" ","").trim().replace("á","a").replace("é","e").replace("í","i").replace("ó","o").replace("ú","u").replace("ñ","n").replace("&","").replace("=","").replace("/","").toLowerCase();
        return CadenaFinal;
    }

    private String convertirCadena(String text){
        String resultado="";
        for (int i=0; i<text.length(); i++){
            int ascii=(int) text.charAt(i);
            resultado=resultado+ascii+"-*-";
        }
        return resultado;
    }
}