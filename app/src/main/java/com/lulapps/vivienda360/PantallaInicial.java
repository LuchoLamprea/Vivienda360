package com.lulapps.vivienda360;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lulapps.vivienda360.ui.buscar.BuscarFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class PantallaInicial extends AppCompatActivity {

    //RequestQueue requestQueue;
    //public ArrayList<Zona> zonas_bogota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //zonas_bogota= new ArrayList<>();
        //requestQueue= Volley.newRequestQueue(this);




        /*
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
                            }
                        }).start();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"No se pudo conectar con la base de datos. Se cerrará la app", Toast.LENGTH_LONG).show();
                    }
                }
        );
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
        requestQueue.add(jsonArrayRequest);
        */





        /*
        ArrayList<Zona> zonas_paso;
        for(int i=0; i<30; i++){
            String nombre_key= "LISTA "+i;
            zonas_paso= new ArrayList<>();
            try {
                zonas_paso= new ArrayList<>(getIntent().getParcelableArrayListExtra(nombre_key));
                System.out.println("LUCHO: zonas paso tiene "+zonas_paso.size()+" registros");
                for(int j=0; j<zonas_paso.size(); j++){
                    zonas_bogota.add(zonas_paso.get(j));
                }
            }
            catch (NullPointerException e){
                System.out.println("LUCHO: ERROR... No existe el extra llamado "+nombre_key);
                System.out.println("LUCHO: "+e.getMessage());
                break;
            }
        }
        //zonas_bogota= new ArrayList<>(getIntent().getParcelableArrayListExtra("LISTA"));
        System.out.println("LUCHO: zonas bogota tiene: "+zonas_bogota.size());

         */


        setContentView(R.layout.activity_pantalla_inicial);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        /*
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId= item.getItemId();
                switch (itemId){
                    case R.id.navigation_buscar:
                        BuscarFragment buscarFragment= new BuscarFragment();
                        addFragment(buscarFragment);
                        break;
                    case R.id.navigation_perfil:

                        break;
                    case R.id.navigation_notifications:

                        break;
                }
                return true;
            }
        });
        */


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_buscar, R.id.navigation_perfil, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    /*
    private void addFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
    */

}