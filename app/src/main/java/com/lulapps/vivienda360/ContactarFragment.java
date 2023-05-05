package com.lulapps.vivienda360;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class ContactarFragment extends DialogFragment {

    String publicacion, tituloPublicacion;
    String correo, numeroWhatsapp, numeroLlamada;
    Button email, llamada, whatsapp;
    ImageView logoEmail, logoLlamada, logoWhatsapp, cerrar;
    ProgressBar progressBar;
    RequestQueue requestQueue;

    public ContactarFragment() {

    }

    public ContactarFragment(String publicacion, String tituloPublicacion) {
        this.publicacion = publicacion;
        this.tituloPublicacion = tituloPublicacion;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue= Volley.newRequestQueue(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contactar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayMetrics metrics= new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        view.setLayoutParams(new FrameLayout.LayoutParams((int) (metrics.widthPixels*0.9),(int) (metrics.heightPixels*0.6)));
        email= view.findViewById(R.id.buttonEmail);
        logoEmail= view.findViewById(R.id.imageViewLogoEmail);
        llamada= view.findViewById(R.id.buttonLlamada);
        logoLlamada= view.findViewById(R.id.imageViewLogoLlamada);
        whatsapp= view.findViewById(R.id.buttonWhatsapp);
        logoWhatsapp= view.findViewById(R.id.imageViewLogoWhatsapp);
        cerrar= view.findViewById(R.id.imageViewBotonCerrar);
        progressBar= view.findViewById(R.id.progressBarContactar);

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        String URL_OBTENER_DATOS_PROPIETARIO="https://udvivienda360.000webhostapp.com/obtener_datos_propietario.php?id="+convertirCadena(publicacion);
        System.out.println("Luchoooo url usada para contactar: "+URL_OBTENER_DATOS_PROPIETARIO);
        JsonObjectRequest jsonObjectRequestDatosPropietario= new JsonObjectRequest(
                Request.Method.GET,
                URL_OBTENER_DATOS_PROPIETARIO,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            correo= response.getString("correo");
                            numeroWhatsapp= response.getString("whatsapp");
                            numeroLlamada= response.getString("numero_contacto");
                            System.out.println("Luchooo\tCorreo:\t"+correo+"\tWhatsapp:\t"+numeroWhatsapp+"\ttelefono:\t"+numeroLlamada);
                            email.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    escribirEmail();
                                    logoEmail.setElevation(2);
                                }
                            });
                            logoEmail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    escribirEmail();
                                    logoEmail.setElevation(2);
                                }
                            });
                            whatsapp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    escribirWhatsapp();
                                    logoWhatsapp.setElevation(2);
                                }
                            });
                            logoWhatsapp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    escribirWhatsapp();
                                    logoWhatsapp.setElevation(2);
                                }
                            });
                            llamada.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    llamar();
                                    logoLlamada.setElevation(2);
                                }
                            });
                            logoLlamada.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    llamar();
                                    logoLlamada.setElevation(2);
                                }
                            });
                            progressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Luchooo error al traer datos:\n"+error);
                    }
                }
        );
        jsonObjectRequestDatosPropietario.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
        requestQueue.add(jsonObjectRequestDatosPropietario);
    }

    private String convertirCadena(String text){
        String resultado="";
        for (int i=0; i<text.length(); i++){
            int ascii=(int) text.charAt(i);
            resultado=resultado+ascii+"-*-";
        }
        return resultado;
    }

    public void llamar(){
        if(numeroLlamada!=null){
            if (ContextCompat.checkSelfPermission(
                    getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent intentLlamada= new Intent(Intent.ACTION_CALL);
                intentLlamada.setData(Uri.parse("tel:"+numeroLlamada));
                startActivity(intentLlamada);
            } else {
                requestPermissions(new String[]{ Manifest.permission.CALL_PHONE}, PackageManager.PERMISSION_GRANTED);
                //getActivity().requestPermissionLauncher.launch(Manifest.permission.REQUESTED_PERMISSION);
            }

        }
        else{
            Toast.makeText(getContext(), getResources().getString(R.string.sin_numero_contacto), Toast.LENGTH_SHORT).show();
        }
        logoLlamada.setElevation(2);
        llamada.setElevation(0);
    }

    public void escribirWhatsapp(){
        try {
            PackageManager pm=getContext().getPackageManager();
            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            String textoMensaje=getResources().getString(R.string.saludo_whatsapp_p1)+" "+tituloPublicacion+getResources().getString(R.string.saludo_whatsapp_p2);
            String numeroWhatsappLimpio= numeroWhatsapp.replace("+","");
            Intent intentWhatsapp= new Intent(Intent.ACTION_VIEW);
            intentWhatsapp.setPackage("com.whatsapp");
            intentWhatsapp.setData(Uri.parse("http://api.whatsapp.com/send?phone="+numeroWhatsappLimpio+"&text="+textoMensaje));
            startActivity(intentWhatsapp);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), getResources().getString(R.string.sin_whatsapp_en_telefono), Toast.LENGTH_SHORT).show();
        }
        logoWhatsapp.setElevation(2);
        whatsapp.setElevation(0);
    }

    public void escribirEmail(){
        String asuntoCorreo= getResources().getString(R.string.asunto_correo)+" "+tituloPublicacion;
        String cuerpoCorreo= getResources().getString(R.string.saludo_whatsapp_p1)+" "+tituloPublicacion+getResources().getString(R.string.saludo_whatsapp_p2);
        Intent intentEmail = new Intent(Intent.ACTION_SEND);
        intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{ correo});
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, asuntoCorreo);
        intentEmail.putExtra(Intent.EXTRA_TEXT, cuerpoCorreo);
        intentEmail.setType("message/rfc822");
        startActivity(Intent.createChooser(intentEmail, getResources().getString(R.string.selecciona_correo)));
        logoEmail.setElevation(2);
        email.setElevation(0);
    }
}