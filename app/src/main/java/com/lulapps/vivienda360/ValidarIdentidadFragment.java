package com.lulapps.vivienda360;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class ValidarIdentidadFragment extends Fragment {
    String correo;
    boolean resp1ok, resp2ok;
    private String pregunta_1, pregunta_2;
    TextView pregunta1, pregunta2, error;
    EditText respuesta1, respuesta2;
    Button validar;
    FloatingActionButton atras;
    RequestQueue requestQueue;

    public ValidarIdentidadFragment() {

    }

    public ValidarIdentidadFragment(String correo) {
        this.correo=correo;
        resp1ok=false;
        resp2ok=false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue= Volley.newRequestQueue(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_validar_identidad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("lucho 4: correo para pedir preguntas: "+correo);
        String URL_VALIDAR= "https://udvivienda360.000webhostapp.com/obtener_preguntas_seguridad.php?correo="+convertirCadena(this.correo);
        System.out.println("lucho 4: URL_PEDIR_PREGUNTAS: "+URL_VALIDAR);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_VALIDAR,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            pregunta_1=response.getString("pregunta_1");
                            pregunta_2=response.getString("pregunta_2");
                            pregunta1 = (TextView) view.findViewById(R.id.textViewPregunta1);
                            pregunta1.setText(pregunta1.getText()+"\n"+pregunta_1);
                            pregunta2 = (TextView) view.findViewById(R.id.textViewPregunta2);
                            pregunta2.setText(pregunta2.getText()+"\n"+pregunta_2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
        requestQueue.add(jsonObjectRequest);


        respuesta1= (EditText) view.findViewById(R.id.editTextCorreoMiPerfil);
        respuesta2= (EditText) view.findViewById(R.id.editTextConfirmarContrasenaNueva);
        validar= (Button) view.findViewById(R.id.buttonPublicar);
        atras= (FloatingActionButton) view.findViewById(R.id.floatingButtonAtrasActualizar);
        error= (TextView) view.findViewById(R.id.textViewErrorActualizar);

        validar.setEnabled(false);
        validar.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));

        respuesta1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (respuesta1.getText().toString().isEmpty() || respuesta1.getText().toString().equals(null)){
                    resp1ok=false;
                }
                else{
                    resp1ok=true;
                }
                ValidarActivaciondeBoton();
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
                if (respuesta2.getText().toString().isEmpty() || respuesta2.getText().toString().equals(null)){
                    resp2ok=false;
                }
                else{
                    resp2ok=true;
                }
                ValidarActivaciondeBoton();
            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                OlvidasteContrasenaFragment olvidasteContrasenaFragment= new OlvidasteContrasenaFragment();
                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, olvidasteContrasenaFragment);
                fragmentTransaction.commit();
            }
        });

        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validar.setEnabled(false);
                validar.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
                String textoResp1, textoResp2;
                textoResp1=convertirCadena(respuesta1.getText().toString());
                textoResp2=convertirCadena(respuesta2.getText().toString());
                String URL_VALIDAR_RESPUESTAS= "https://udvivienda360.000webhostapp.com/verificar_preguntas_seguridad.php?correo="+convertirCadena(correo)+"&respuesta_1="+textoResp1+"&respuesta_2="+textoResp2;
                System.out.println("lucho 4: URL= "+URL_VALIDAR_RESPUESTAS);
                StringRequest stringRequest= new StringRequest(
                        Request.Method.GET,
                        URL_VALIDAR_RESPUESTAS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("lucho 4: entroooooo");
                                if (response.equals("aprobado")){
                                    FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                    ActualizarContrasenaFragment actualizarContrasenaFragment=new ActualizarContrasenaFragment(correo);
                                    fragmentTransaction.replace(R.id.contenedor_ppal_perfil, actualizarContrasenaFragment);
                                    fragmentTransaction.commit();
                                }
                                else{
                                    error.setText(R.string.error_validar_preguntas);
                                    System.out.println("lucho 4: errrorrr");
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error2) {
                                error.setText(error2.getMessage());
                                System.out.println("lucho 4: errrorrr");
                            }
                        }
                );
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                requestQueue.add(stringRequest);
            }
        });
    }

    private void ValidarActivaciondeBoton() {
        if(resp1ok && resp2ok){
            validar.setEnabled(true);
            validar.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
        }
        else{
            validar.setEnabled(false);
            validar.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
        }
        error.setText("");
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