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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OlvidasteContrasenaFragment extends Fragment {

    EditText correo;
    Button validar;
    FloatingActionButton atras;
    TextView error;
    boolean corr;
    RequestQueue requestQueue;

    public OlvidasteContrasenaFragment() {
        corr=false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue= Volley.newRequestQueue(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_olvidaste_contrasena, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        correo= (EditText) view.findViewById(R.id.editTextCorreoMiPerfil);
        validar= (Button) view.findViewById(R.id.buttonPublicar);
        atras= (FloatingActionButton) view.findViewById(R.id.floatingButtonAtrasActualizar);
        error= (TextView) view.findViewById(R.id.textViewErrorActualizar);



        validar.setEnabled(false);
        validar.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                LoginFragment loginFragment= new LoginFragment();
                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, loginFragment);
                fragmentTransaction.commit();
            }
        });

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
                        if(!correo.getText().toString().contains(" ")){
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
                ActivarValidar();
            }
        });

        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                validar.setEnabled(false);
                validar.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
                String textoCorreo;
                textoCorreo=convertirCadena(correo.getText().toString().toLowerCase().trim());
                String URL_VERIFICAR_CORREO= "https://udvivienda360.000webhostapp.com/verificar_correo_registrado.php?correo="+textoCorreo;
                StringRequest stringRequestCorreo= new StringRequest(
                        Request.Method.GET,
                        URL_VERIFICAR_CORREO,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("registrado")){
                                    FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                    //Bundle bundle= new Bundle();
                                    //bundle.putString("correo", textoCorreo);
                                    //getParentFragment().getChildFragmentManager().setFragmentResult("key", bundle);
                                    //getParentFragmentManager().setFragmentResult("key", bundle);
                                    ValidarIdentidadFragment validarIdentidadFragment= new ValidarIdentidadFragment(correo.getText().toString());
                                    fragmentTransaction.replace(R.id.contenedor_ppal_perfil, validarIdentidadFragment);
                                    fragmentTransaction.commit();
                                }
                                else{
                                    error.setText(R.string.error_correo_no_registrado);
                                    validar.setEnabled(true);
                                    validar.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
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
        });

    }

    private void ActivarValidar() {
        if(corr){
            validar.setEnabled(true);
            validar.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
        }
        else{
            validar.setEnabled(false);
            validar.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
        }
        error.setText("");
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