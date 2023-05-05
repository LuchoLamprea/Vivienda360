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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class LoginFragment extends Fragment {

    TextView correo, contrasena, olvidasteContrasena, error;
    Button botonRegistrate;
    Button botonIniciarSesion;

    boolean mail, password;

    String nombreUsuario, correoUsuario, telefonoUsuario, whatsappUsuario, pregunta_1, respuesta_1,
            pregunta_2, respuesta_2, publicadosUsuario, favoritosUsuario;

    RequestQueue requestQueue;

    public LoginFragment() {
        mail=false;
        password=false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue= Volley.newRequestQueue(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        correo= (TextView) view.findViewById(R.id.editTextCorreoMiPerfil);
        contrasena= (TextView) view.findViewById(R.id.editTextContrasenaRegistrate);
        olvidasteContrasena= (TextView) view.findViewById(R.id.textViewOlvidasteContrasenaLogin);
        botonRegistrate= (Button) view.findViewById(R.id.buttonIniciarSesionRegistrate);
        botonIniciarSesion= (Button) view.findViewById(R.id.buttonPublicar);
        error= (TextView) view.findViewById(R.id.textViewErrorLogin);

        botonIniciarSesion.setEnabled(false);
        botonIniciarSesion.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));

        correo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(correo.getText().toString().isEmpty() || correo.getText().toString().equals(null)){
                    mail=false;
                }
                else{
                    if(correo.getText().toString().contains("@") && correo.getText().toString().substring(correo.getText().toString().indexOf("@")).contains(".")){
                        if (!correo.getText().toString().contains(" ")){
                            mail=true;
                        }
                        else{
                            mail=false;
                        }
                    }
                    else{
                        mail=false;
                    }
                }
                VerificarActivarInciarSesion();
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
                    password=false;
                }
                else{
                    password=true;
                }
                VerificarActivarInciarSesion();
            }
        });

        olvidasteContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                OlvidasteContrasenaFragment olvidasteContrasenaFragment= new OlvidasteContrasenaFragment();
                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, olvidasteContrasenaFragment);
                fragmentTransaction.commit();
            }
        });

        botonRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                RegistrateFragment registrateFragment= new RegistrateFragment();
                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, registrateFragment);
                fragmentTransaction.commit();
            }
        });

        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonIniciarSesion.setEnabled(false);
                botonIniciarSesion.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
                String correoElect= convertirCadena(correo.getText().toString().trim());
                String contrase= convertirCadena(contrasena.getText().toString());
                String URL_LOGIN= "https://udvivienda360.000webhostapp.com/iniciar_sesion.php?correo="+correoElect+"&contrasena="+contrase;
                JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(
                        Request.Method.GET,
                        URL_LOGIN,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    nombreUsuario= response.getString("nombre");
                                    correoUsuario= response.getString("correo");
                                    telefonoUsuario= response.getString("numero_contacto");
                                    whatsappUsuario= response.getString("whatsapp");
                                    pregunta_1= response.getString("pregunta_1");
                                    respuesta_1= response.getString("respuesta_1");
                                    pregunta_2= response.getString("pregunta_2");
                                    respuesta_2= response.getString("respuesta_2");
                                    favoritosUsuario= response.getString("favoritos");
                                    publicadosUsuario= response.getString("publicados");

                                    OutputStreamWriter fos=null;
                                    try {
                                        fos = new OutputStreamWriter(getActivity().openFileOutput("user_data.txt", Context.MODE_PRIVATE));
                                        fos.write(correoUsuario+"\n");
                                        fos.write(nombreUsuario+"\n");
                                        fos.write(telefonoUsuario+"\n");
                                        fos.write(whatsappUsuario+"\n");
                                        fos.write(pregunta_1+"\n");
                                        fos.write(respuesta_1+"\n");
                                        fos.write(pregunta_2+"\n");
                                        fos.write(respuesta_2+"\n");
                                        fos.write(favoritosUsuario+"\n");
                                        fos.write(publicadosUsuario+"\n");
                                        fos.flush();
                                        fos.close();
                                    } catch (FileNotFoundException fileNotFoundException) {
                                        fileNotFoundException.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    finally {
                                        try {
                                            if(fos!=null){
                                                fos.close();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                    MiPerfilFragment miPerfilFragment = new MiPerfilFragment(correoUsuario, nombreUsuario, whatsappUsuario, telefonoUsuario, pregunta_1, respuesta_1, pregunta_2, respuesta_2,favoritosUsuario,publicadosUsuario);
                                    fragmentTransaction.replace(R.id.contenedor_ppal_perfil, miPerfilFragment);
                                    fragmentTransaction.commit();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error2) {
                                Toast.makeText(view.getContext(),"Usuario o contraseña incorrectos",Toast.LENGTH_SHORT).show();
                                error.setText(R.string.error_usuario_contrasena_incorrectos);
                                botonIniciarSesion.setEnabled(true);
                                botonIniciarSesion.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                            }
                        }
                );
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                requestQueue.add(jsonObjectRequest);
            }
        });
    }

    private void VerificarActivarInciarSesion() {
        if(mail && password){
            botonIniciarSesion.setEnabled(true);
            botonIniciarSesion.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
        }
        else{
            botonIniciarSesion.setEnabled(false);
            botonIniciarSesion.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
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