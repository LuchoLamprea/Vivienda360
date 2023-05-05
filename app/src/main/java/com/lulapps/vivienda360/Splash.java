package com.lulapps.vivienda360;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Splash extends AppCompatActivity {

    RequestQueue requestQueue;
    ArrayList<Zona> zonas_bogota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        while(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            solicitarPermisos();
        }
        /*
        requestQueue= Volley.newRequestQueue(this);
        zonas_bogota= new ArrayList<Zona>();

        String URL_PRUEBA="https://udvivienda360.000webhostapp.com/obtener_zonas.php";
        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest(
                Request.Method.GET,
                URL_PRUEBA,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for(int i=0; i<response.length(); i++){
                                    try {
                                        String nombreZona, boundaries;
                                        nombreZona=response.getJSONObject(i).getString("nombre");
                                        boundaries=response.getJSONObject(i).getString("boundary");
                                        StringTokenizer stCoordenadas= new StringTokenizer(boundaries, ", ");
                                        ArrayList<LatLng> puntos_zona_paso= new ArrayList<>();
                                        while (stCoordenadas.hasMoreTokens()){
                                            String longitud = stCoordenadas.nextToken();
                                            String latitud = stCoordenadas.nextToken();
                                            LatLng punto_boundary= new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
                                            puntos_zona_paso.add(punto_boundary);
                                        }
                                        zonas_bogota.add(new Zona(nombreZona, puntos_zona_paso));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println("LUCHO: TAMAÑO: "+zonas_bogota.size());
                                Intent lanzarActividadPantallaInicial= new Intent(Splash.this, PantallaInicial.class);
                                ArrayList<Zona> zonas_paso=new ArrayList<>();
                                for (int j=0; j<zonas_bogota.size(); j++){
                                    zonas_paso.add(zonas_bogota.get(j));
                                    if(j%100==0 || j==zonas_bogota.size()){
                                        String nombre_key= "LISTA "+j;
                                        lanzarActividadPantallaInicial.putParcelableArrayListExtra(nombre_key, zonas_paso);
                                        zonas_paso=new ArrayList<>();
                                    }
                                }
                                //lanzarActividadPantallaInicial.putParcelableArrayListExtra("LISTA", zonas_bogota);
                                //Bundle bundle= new Bundle();
                                //bundle.putParcelableArrayList("LISTA", zonas_bogota);
                                //new Intent();
                                //lanzarActividadPantallaInicial.putExtra("LISTA", )
                                startActivity(lanzarActividadPantallaInicial);
                                finish();
                            }
                        }).start();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
        requestQueue.add(jsonArrayRequest);

         */
        boolean existe_el_archivo=false;
        for(int i=0; i<fileList().length; i++){
            System.out.println("Rev1:\t Archivo "+(i+1)+": "+fileList()[i]);
            if(fileList()[i].equals("user_data.txt")){
                existe_el_archivo=true;
            }
        }
        if(existe_el_archivo){
            System.out.println("Rev1:\tEn el splash el archivo ya existe");
        }
        else{
            System.out.println("Rev1:\tEn el splash el archivo NO existe");
        }

        handler.postDelayed(new Runnable() {
            public void run() {
                Intent lanzarActividadPantallaInicial= new Intent(Splash.this, PantallaInicial.class);
                startActivity(lanzarActividadPantallaInicial);
                System.out.println("Rev1:\tPasó Splash");
                finish();
            }
        },2000);

    }

    public void solicitarPermisos(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
        }
    }

}