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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MiPerfilFragment extends Fragment {

    private EditText correo, nombre, whatsapp, telefono, respuesta1, respuesta2;
    private Spinner pregunta1, pregunta2;
    private TextView bienvenida, error;
    private Button guardarCambios,cambiarContrasena, misPublicaciones;
    int posicionSpin1, posicionSpin2;
    boolean cambioNombre, cambioWhatsapp, cambioTelefono, cambioPregunta1, cambioRespuesta1, cambioPregunta2, cambioRespuesta2;
    RequestQueue requestQueue;

    private String textoCorreo, textoNombre, textoWhatsapp, textoTelefono, textoRespuesta1, textoRespuesta2, textoPregunta1, textoPregunta2, textoFavoritos, textoPublicados;

    public MiPerfilFragment() {

    }

    public MiPerfilFragment(String mail, String name, String WA, String phone, String question1,
                            String ask1, String question2, String ask2, String favorites, String published) {
        textoCorreo=mail;
        textoNombre=name;
        textoWhatsapp= WA;
        textoTelefono= phone;
        textoRespuesta1= ask1;
        textoRespuesta2= ask2;
        textoPregunta1= question1;
        textoPregunta2= question2;
        textoFavoritos= favorites;
        textoPublicados= published;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue= Volley.newRequestQueue(this.getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mi_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(textoCorreo==null){
            InputStream is= null;
            BufferedReader reader=null;
            try {
                is = this.getActivity().openFileInput("user_data.txt");
                reader= new BufferedReader(new InputStreamReader(is));
                String linea;
                int contador_lineas=0;
                if(is!=null){
                    while ((linea = reader.readLine()) != null) {
                        contador_lineas++;
                        switch (contador_lineas){
                            case 1:
                                textoCorreo=linea;
                                break;
                            case 2:
                                textoNombre=linea;
                                break;
                            case 3:
                                textoTelefono=linea;
                                break;
                            case 4:
                                textoWhatsapp=linea;
                                break;
                            case 5:
                                textoPregunta1=linea;
                                break;
                            case 6:
                                textoRespuesta1=linea;
                                break;
                            case 7:
                                textoPregunta2=linea;
                                break;
                            case 8:
                                textoRespuesta2=linea;
                                break;
                            case 9:
                                textoFavoritos=linea;
                                break;
                            case 10:
                                textoPublicados=linea;
                                break;
                        }
                    }
                }
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        ArrayList<String> listado= new ArrayList<String>(Arrays.asList(view.getResources().getStringArray(R.array.preguntas_seguridad_1)));
        for (int i=0; i<listado.size(); i++){
            if (listado.get(i).equals(textoPregunta1)){
                posicionSpin1=i;
                break;
            }
        }
        listado= new ArrayList<String>(Arrays.asList(view.getResources().getStringArray(R.array.preguntas_seguridad_2)));
        for (int i=0; i<listado.size(); i++){
            if (listado.get(i).equals(textoPregunta2)){
                posicionSpin2=i;
                break;
            }
        }

        correo= (EditText) view.findViewById(R.id.editTextCorreoMiPerfil);
        nombre= (EditText) view.findViewById(R.id.editTextNombreMiPerfil);
        whatsapp= (EditText) view.findViewById(R.id.editTextWhatsappMiPerfil);
        telefono= (EditText) view.findViewById(R.id.editTextTelefonoMiPerfil);
        respuesta1= (EditText) view.findViewById(R.id.editTextRespuesta1MiPerfil);
        respuesta2= (EditText) view.findViewById(R.id.editTextRespuesta2MiPerfil);
        pregunta1= (Spinner)  view.findViewById(R.id.spinnerPreguntaSeguridad1MiPerfil);
        pregunta2= (Spinner)  view.findViewById(R.id.spinnerPreguntaSeguridad2MiPerfil);
        error= (TextView) view.findViewById(R.id.textViewErrorPublicar);

        bienvenida= (TextView) view.findViewById(R.id.textViewPublicaInmueble);
        guardarCambios= (Button) view.findViewById(R.id.buttonGuardarCambios);
        cambiarContrasena= (Button) view.findViewById(R.id.buttonCambiarContrasena);
        misPublicaciones= (Button) view.findViewById(R.id.buttonPublicar);

        bienvenida.setText(getResources().getString(R.string.bienvenida)+textoNombre);
        correo.setEnabled(false);
        correo.setText(textoCorreo);
        nombre.setText(textoNombre);
        whatsapp.setText(textoWhatsapp);
        telefono.setText(textoTelefono);
        respuesta1.setText(textoRespuesta1);
        respuesta2.setText(textoRespuesta2);

        guardarCambios.setEnabled(false);
        guardarCambios.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getContext(),
                R.array.preguntas_seguridad_1, android.R.layout.simple_spinner_item);
        pregunta1.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getContext(),
                R.array.preguntas_seguridad_2, android.R.layout.simple_spinner_item);
        pregunta2.setAdapter(adapter2);

        pregunta1.setSelection(posicionSpin1);
        pregunta2.setSelection(posicionSpin2);

        nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println(s);
                if(!s.toString().equals(textoNombre)){
                    cambioNombre=true;
                }
                else{
                    cambioNombre=false;
                }
                VerificarCambios();
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
                if(!s.toString().equals(textoWhatsapp)){
                    cambioWhatsapp=true;
                }
                else{
                    cambioWhatsapp=false;
                }
                VerificarCambios();
            }
        });

        telefono.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(textoTelefono)){
                    cambioTelefono=true;
                }
                else{
                    cambioTelefono=false;
                }
                VerificarCambios();
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
                if(!s.toString().equals(textoRespuesta1)){
                    cambioRespuesta1=true;
                }
                else{
                    cambioRespuesta1=false;
                }
                VerificarCambios();
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
                if(!s.toString().equals(textoRespuesta2)){
                    cambioRespuesta2=true;
                }
                else{
                    cambioRespuesta2=false;
                }
                VerificarCambios();
            }
        });

        pregunta1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=posicionSpin1 && position!=0){
                    cambioPregunta1=true;
                }
                else{
                    cambioPregunta1=false;
                }
                VerificarCambios();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pregunta2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=posicionSpin2 && position!=0){
                    cambioPregunta2=true;
                }
                else{
                    cambioPregunta2=false;
                }
                VerificarCambios();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        guardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                guardarCambios.setEnabled(false);
                guardarCambios.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));

                String textoNombre2, textoWhatsapp2, textoTelefono2, textoPregunta12, textoRespuesta12, textoPregunta22, textoRespuesta22;

                textoNombre2=convertirCadena(nombre.getText().toString());
                String pasowha;
                if (whatsapp.getText().toString().length()==10 && !whatsapp.getText().toString().contains("+")){
                    pasowha="+57"+whatsapp.getText().toString();
                }
                else{
                    pasowha=whatsapp.getText().toString();
                }
                textoWhatsapp2=convertirCadena(pasowha);
                textoTelefono2=convertirCadena(telefono.getText().toString().trim());
                textoPregunta12=convertirCadena(pregunta1.getSelectedItem().toString());
                textoRespuesta12=convertirCadena(respuesta1.getText().toString().trim());
                textoPregunta22=convertirCadena(pregunta2.getSelectedItem().toString());
                textoRespuesta22=convertirCadena(respuesta2.getText().toString().trim());
                String URL_REGISTRO= "https://udvivienda360.000webhostapp.com/actualizar_datos.php";

                System.out.println("lucho 3: URL="+URL_REGISTRO);

                StringRequest stringRequest= new StringRequest(
                        Request.Method.POST,
                        URL_REGISTRO,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.equals("OK")){
                                    OutputStreamWriter fos=null;
                                    try {
                                        fos = new OutputStreamWriter(getActivity().openFileOutput("user_data.txt", Context.MODE_PRIVATE));
                                        fos.write(textoCorreo+"\n");
                                        fos.write(desconvertirCadena(textoNombre2)+"\n");
                                        fos.write(desconvertirCadena(textoTelefono2)+"\n");
                                        fos.write(desconvertirCadena(textoWhatsapp2)+"\n");
                                        fos.write(desconvertirCadena(textoPregunta12)+"\n");
                                        fos.write(desconvertirCadena(textoRespuesta12)+"\n");
                                        fos.write(desconvertirCadena(textoPregunta22)+"\n");
                                        fos.write(desconvertirCadena(textoRespuesta22)+"\n");
                                        fos.write(textoFavoritos+"\n");
                                        fos.write(textoPublicados+"\n");
                                        fos.flush();
                                        fos.close();

                                        textoNombre=desconvertirCadena(textoNombre2);
                                        textoTelefono=desconvertirCadena(textoTelefono2);
                                        textoWhatsapp=desconvertirCadena(textoWhatsapp2);
                                        textoPregunta1=desconvertirCadena(textoPregunta12);
                                        textoRespuesta1=desconvertirCadena(textoRespuesta12);
                                        textoPregunta2=desconvertirCadena(textoPregunta22);
                                        textoRespuesta2=desconvertirCadena(textoRespuesta22);

                                        bienvenida.setText(getResources().getString(R.string.bienvenida)+textoNombre);

                                        Toast.makeText(getContext(), R.string.datos_actualizados, Toast.LENGTH_SHORT).show();
                                        error.setText("");
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
                                }
                                else{
                                    error.setText(R.string.error_inesperado);
                                    guardarCambios.setEnabled(true);
                                    guardarCambios.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error2) {
                                System.out.println("lucho 3: "+error2.getMessage());
                                error.setText(error2.getMessage());
                                guardarCambios.setEnabled(true);
                                guardarCambios.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                            }
                        }
                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parametros= new HashMap<>();
                        parametros.put("correo",convertirCadena(textoCorreo));
                        parametros.put("nombre",textoNombre2);
                        parametros.put("whatsapp",textoWhatsapp2);
                        parametros.put("numero_contacto",textoTelefono2);
                        parametros.put("pregunta_1",textoPregunta12);
                        parametros.put("respuesta_1",textoRespuesta12);
                        parametros.put("pregunta_2",textoPregunta22);
                        parametros.put("respuesta_2",textoRespuesta22);
                        return parametros;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                requestQueue.add(stringRequest);
            }
        });


        cambiarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                ActualizarContrasenaFragment actualizarContrasenaFragment= new ActualizarContrasenaFragment(textoCorreo);
                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, actualizarContrasenaFragment);
                fragmentTransaction.commit();
            }
        });


        misPublicaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                MisPublicacionesFragment misPublicacionesFragment= new MisPublicacionesFragment(textoCorreo, textoPublicados);
                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, misPublicacionesFragment);
                fragmentTransaction.commit();
            }
        });

    }

    private void VerificarCambios() {
        if(cambioNombre || cambioWhatsapp || cambioTelefono || cambioPregunta1 || cambioRespuesta1 || cambioPregunta2 || cambioRespuesta2){
            guardarCambios.setEnabled(true);
            guardarCambios.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
        }
        else{
            guardarCambios.setEnabled(false);
            guardarCambios.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
        }
        error.setText("");
    }

    private String convertirCadena(String text){
        String resultado="";
        for (int i=0; i<text.length(); i++){
            int ascii=(int) text.charAt(i);
            if(ascii!=0){
                resultado=resultado+ascii+"-*-";
            }
        }
        return resultado;
    }

    private String desconvertirCadena(String text){
        System.out.println("Luchoooo \tCadena Inicial: "+text);
        String resultado="";
        StringTokenizer st= new StringTokenizer(text, "-*-");
        while(st.hasMoreTokens()){
            resultado=resultado+((char)(Integer.parseInt(st.nextToken())));
        }
        return resultado;
    }

}