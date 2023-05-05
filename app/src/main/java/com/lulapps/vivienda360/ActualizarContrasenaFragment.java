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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ActualizarContrasenaFragment extends Fragment {

    String correo;
    EditText contrasenanueva, confirmarcontrasenanueva;
    Button actualizarContrasena;
    FloatingActionButton atras;
    TextView error;
    boolean pass1, pass2;
    RequestQueue requestQueue;

    public ActualizarContrasenaFragment() {

    }

    public ActualizarContrasenaFragment(String correo){
        this.correo=correo;
        pass1=false;
        pass2=false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue= Volley.newRequestQueue(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actualizar_contrasena, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contrasenanueva= (EditText) view.findViewById(R.id.editTextCorreoMiPerfil);
        confirmarcontrasenanueva= (EditText) view.findViewById(R.id.editTextConfirmarContrasenaNueva);
        actualizarContrasena= (Button) view.findViewById(R.id.buttonPublicar);
        atras= (FloatingActionButton) view.findViewById(R.id.floatingButtonAtrasActualizar);
        error= (TextView) view.findViewById(R.id.textViewErrorActualizar);

        actualizarContrasena.setEnabled(false);
        actualizarContrasena.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));

        contrasenanueva.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (contrasenanueva.getText().toString().isEmpty() || contrasenanueva.getText().toString().equals(null)){
                    pass1=false;
                }
                else{
                    pass1=true;
                }
                VerificarActivarBotonActualizar();
            }
        });

        confirmarcontrasenanueva.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (confirmarcontrasenanueva.getText().toString().isEmpty() || confirmarcontrasenanueva.getText().toString().equals(null)){
                    pass2=false;
                }
                else{
                    pass2=true;
                }
                VerificarActivarBotonActualizar();
            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                LoginFragment loginFragment= new LoginFragment();
                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, loginFragment);
                fragmentTransaction.commit();
            }
        });

        actualizarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String revisionContrasena;
                revisionContrasena=condicionesContrasena(contrasenanueva.getText().toString(), confirmarcontrasenanueva.getText().toString());
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
                            actualizarContrasena.setEnabled(false);
                            actualizarContrasena.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
                            String textoContrasena=convertirCadena(contrasenanueva.getText().toString());
                            String URL_VERIFICAR_CONTRASENA= "https://udvivienda360.000webhostapp.com/verificar_contrasena_existente.php?correo="+convertirCadena(correo)+"&contrasena="+textoContrasena;
                            StringRequest stringRequestVerificar= new StringRequest(
                                    Request.Method.GET,
                                    URL_VERIFICAR_CONTRASENA,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            if(response.equals("registrada")){
                                                error.setText(R.string.error_contrasena_igual);
                                                actualizarContrasena.setEnabled(true);
                                                actualizarContrasena.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
                                            }
                                            else{
                                                if(response.equals("no_registrada")){
                                                    String URL_ACTUALIZAR_CONTRASENA= "https://udvivienda360.000webhostapp.com/actualizar_contrasena.php";
                                                    StringRequest stringRequest= new StringRequest(
                                                            Request.Method.POST,
                                                            URL_ACTUALIZAR_CONTRASENA,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    if (response.equals("OK")){
                                                                        Toast.makeText(getContext(),R.string.contrasena_actualizada,Toast.LENGTH_LONG).show();
                                                                        FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                                                        LoginFragment loginFragment= new LoginFragment();
                                                                        fragmentTransaction.replace(R.id.contenedor_ppal_perfil, loginFragment);
                                                                        fragmentTransaction.commit();
                                                                    }
                                                                    else{
                                                                        if(response.equals("registro_no_encontrado")){
                                                                            error.setText(R.string.error_correo_no_registrado);
                                                                            actualizarContrasena.setEnabled(true);
                                                                            actualizarContrasena.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
                                                                        }
                                                                        else{
                                                                            error.setText(R.string.error_inesperado);
                                                                            actualizarContrasena.setEnabled(true);
                                                                            actualizarContrasena.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
                                                                        }
                                                                    }
                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error2) {
                                                                    error.setText(error2.getMessage());
                                                                    actualizarContrasena.setEnabled(true);
                                                                    actualizarContrasena.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
                                                                }
                                                            }
                                                    ){
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                            Map<String, String> parametros= new HashMap<>();
                                                            parametros.put("correo",convertirCadena(correo));
                                                            parametros.put("contrasena",textoContrasena);
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
                            stringRequestVerificar.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                            requestQueue.add(stringRequestVerificar);
                        }
                    }
                }
            }
        });
    }

    private void VerificarActivarBotonActualizar() {
        if (pass1 && pass2){
            actualizarContrasena.setEnabled(true);
            actualizarContrasena.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
        }
        else{
            actualizarContrasena.setEnabled(false);
            actualizarContrasena.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
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

    private String convertirCadena(String text){
        String resultado="";
        for (int i=0; i<text.length(); i++){
            int ascii=(int) text.charAt(i);
            resultado=resultado+ascii+"-*-";
        }
        return resultado;
    }

}