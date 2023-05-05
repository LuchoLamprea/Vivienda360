package com.lulapps.vivienda360;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class VisualizacionFragment extends Fragment implements OnMapReadyCallback {

    ImageView imagenModelo3D, ivFoto, iconoFotosMultiples, atras, favorito;
    TextView verModelo, tvTitulo, tvPrecio, tvArea, tvHabitaciones, tvBanos, tvGarajes, tvEstrato, tvAntiguedad, tvDescripcion;
    GoogleMap map;
    Button contactar;
    ConstraintLayout contenedorModelo3D;
    String publicacion, tipoPublicacion, oferta, inmueble, precio, area, habitaciones, banos, garajes, estrato, antiguedad, descripcion, direccion, barrio, foto, modelo;
    String filtroZona, filtroOferta, inicialmenteFavorito;
    String textoCorreoUsuario;
    boolean fav;
    RequestQueue requestQueue;
    ArrayList<String> numerosRomanos;
    Fragment este;

    public VisualizacionFragment() {

    }

    public VisualizacionFragment(String publicacion, String tipoPublicacion) {
        this.publicacion = publicacion;
        this.tipoPublicacion = tipoPublicacion;
    }

    public VisualizacionFragment(String publicacion, String tipoPublicacion, String filtroZona, String filtroOferta) {
        this.publicacion = publicacion;
        this.tipoPublicacion = tipoPublicacion;
        this.filtroZona = filtroZona;
        this.filtroOferta= filtroOferta;
        System.out.println("Luchoooo\tDatos recibidos para visualizacion. ID: "+publicacion+", Tipo: "+tipoPublicacion+", zona: "+filtroZona);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue= Volley.newRequestQueue(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visualizacion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        este=this;
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        inicializarNumerosRomanos();
        imagenModelo3D= view.findViewById(R.id.imageViewModelo3d);
        contenedorModelo3D= view.findViewById(R.id.constraintLayoutContenedorVerModelo);
        ivFoto = view.findViewById(R.id.imageViewFoto);
        iconoFotosMultiples= view.findViewById(R.id.imageViewFotosMultiples);
        atras= view.findViewById(R.id.imageViewButtonAtrasVisualizacion);
        verModelo= view.findViewById(R.id.textViewVerModelo);
        tvTitulo = view.findViewById(R.id.textViewTituloPublicacion);
        tvPrecio = view.findViewById(R.id.textViewPrecioPublicacion);
        tvArea = view.findViewById(R.id.textViewArea);
        tvHabitaciones = view.findViewById(R.id.textViewHabitaciones);
        tvBanos = view.findViewById(R.id.textViewBanos);
        tvGarajes = view.findViewById(R.id.textViewGarajes);
        tvEstrato = view.findViewById(R.id.textViewEstrato);
        tvAntiguedad = view.findViewById(R.id.textViewAntiguedad);
        tvDescripcion = view.findViewById(R.id.textViewDescripcion);
        contactar= view.findViewById(R.id.buttonContactar);
        favorito= view.findViewById(R.id.imageViewFavorito);

        textoCorreoUsuario="";
        InputStream is= null;
        BufferedReader reader=null;
        try {
            is = getActivity().openFileInput("user_data.txt");
            reader= new BufferedReader(new InputStreamReader(is));
            if(is!=null){
                textoCorreoUsuario=reader.readLine();
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
            textoCorreoUsuario="";
        }
        catch (IOException e) {
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


        String URL_OBTENER_PUBLICACION_COMPLETA="https://udvivienda360.000webhostapp.com/obtener_publicacion_completa_por_id.php?id="+convertirCadena(publicacion)+"&correo="+convertirCadena(textoCorreoUsuario);
        System.out.println("Luchooo URL USADA: "+URL_OBTENER_PUBLICACION_COMPLETA);
        JsonArrayRequest jsonArrayRequestPublicacionCompleta= new JsonArrayRequest(
                Request.Method.GET,
                URL_OBTENER_PUBLICACION_COMPLETA,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println("Luchooo\tInmueble recibido: "+barrio+", precio: "+precio);
                            inmueble=response.getJSONObject(0).getString("inmueble");
                            oferta=response.getJSONObject(0).getString("oferta");
                            precio=response.getJSONObject(0).getString("precio");
                            area=response.getJSONObject(0).getString("areaconst");
                            habitaciones=response.getJSONObject(0).getString("habitaciones");
                            banos=response.getJSONObject(0).getString("banos");
                            garajes=response.getJSONObject(0).getString("garajes");
                            estrato=response.getJSONObject(0).getString("estrato");
                            antiguedad=response.getJSONObject(0).getString("antiguedad");
                            descripcion=response.getJSONObject(0).getString("descripcion");
                            direccion=response.getJSONObject(0).getString("direccion");
                            barrio=response.getJSONObject(0).getString("barrio");
                            foto=response.getJSONObject(0).getString("foto");
                            modelo=response.getJSONObject(0).getString("modelo");
                            inicialmenteFavorito=response.getJSONObject(0).getString("favorito");
                            if(inicialmenteFavorito.equals("NO")){
                                fav=false;
                            }
                            else{
                                fav=true;
                            }
                            if(filtroZona!=null){
                                favorito.setVisibility(View.VISIBLE);
                                if(fav){
                                    favorito.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_activated));
                                    favorito.setTag(R.drawable.ic_star_activated);
                                }
                                else{
                                    favorito.setImageDrawable(getResources().getDrawable(R.drawable.ic_star));
                                    favorito.setTag(R.drawable.ic_star);
                                }

                                favorito.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(!textoCorreoUsuario.equals("")){
                                            if(favorito.getTag().equals(R.drawable.ic_star_activated)){
                                                favorito.setImageDrawable(getResources().getDrawable(R.drawable.ic_star));
                                                favorito.setTag(R.drawable.ic_star);
                                                fav=false;
                                                return;
                                            }
                                            else{
                                                favorito.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_activated));
                                                favorito.setTag(R.drawable.ic_star_activated);
                                                fav=true;
                                            }
                                        }
                                        else{
                                            Toast.makeText(getContext(), getResources().getString(R.string.inicia_o_registrate_favoritos), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }


                            if(!modelo.equals("NULL") && !modelo.equals("null") && !modelo.equals("")){
                                contenedorModelo3D.setVisibility(View.VISIBLE);
                            }
                            else{
                                contenedorModelo3D.setVisibility(View.GONE);
                            }
                            new DownloadImageTask().execute(foto);
                            String barrioPaso=dividirBarrioyLocalidad(barrio).get(0);
                            String tituloPaso=convertirMayuscPrimera(inmueble+" "+getResources().getString(R.string.articulo_en)+" "+oferta+" "+getResources().getString(R.string.articulo_en)+"\n"+barrioPaso);
                            tvTitulo.setText(tituloPaso);
                            DecimalFormatSymbols symbols= new DecimalFormatSymbols();
                            symbols.setGroupingSeparator('.');
                            DecimalFormat decimalFormat= new DecimalFormat("###,###,###,###,###",symbols);
                            String precioFormateado="$"+decimalFormat.format(Double.parseDouble(precio));
                            tvPrecio.setText(precioFormateado);
                            tvArea.setText(area);
                            tvHabitaciones.setText(habitaciones);
                            tvBanos.setText(banos);
                            tvGarajes.setText(garajes);
                            tvEstrato.setText(estrato);
                            String antiguedadPaso= antiguedad+" "+getResources().getString(R.string.anos);
                            tvAntiguedad.setText(antiguedadPaso);
                            tvDescripcion.setText(descripcion);

                            Geocoder geocoder= new Geocoder(getContext());
                            List<Address> addresses;
                            try {
                                addresses= geocoder.getFromLocationName(direccion, 1, 3.7306725330000701, -74.4507241630000038, 4.8368293580000499, -73.9890180820000012);
                                if (addresses==null || addresses.size()==0){
                                    System.out.println("Luchooooooo\nNo se encontró la direccion con geocoder, direccion: "+direccion);
                                }
                                else{
                                    LatLng ubicacionEncontrada= new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                                    map.clear();
                                    map.addMarker(new MarkerOptions()
                                            .position(ubicacionEncontrada)
                                            .title(tituloPaso));
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionEncontrada,15));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Luchooo\tError:\nLuchoo:"+error.getMessage()+"\nLuchooo: "+error.getLocalizedMessage());
                    }
                }
        );
        jsonArrayRequestPublicacionCompleta.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
        requestQueue.add(jsonArrayRequestPublicacionCompleta);


        ivFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FotosFragment fotosFragment= new FotosFragment(publicacion);
                fotosFragment.show(getParentFragment().getChildFragmentManager(),"VisualizarFotos");
            }
        });

        Bitmap originalBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.modelo_3d_imagen)).getBitmap();
        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);
        roundedDrawable.setCornerRadius(50);
        imagenModelo3D.setImageDrawable(roundedDrawable);

        imagenModelo3D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visualizarModelo3D();
            }
        });

        verModelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visualizarModelo3D();
            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                switch (tipoPublicacion){
                    case "propia":
                        MisPublicacionesFragment misPublicacionesFragment= new MisPublicacionesFragment();
                        fragmentTransaction.replace(R.id.contenedor_ppal_perfil, misPublicacionesFragment);
                        fragmentTransaction.commit();
                        break;
                    case "favoritas":


                        System.out.println("Luchooo\tReconocimiento de variables:");
                        System.out.println("Luchooo\tCorreo: "+textoCorreoUsuario);
                        System.out.println("Luchooo\tValor if1: "+textoCorreoUsuario.equals(""));
                        System.out.println("Luchooo\tInicialmente Favorito: "+inicialmenteFavorito);
                        System.out.println("Luchooo\tfav: "+textoCorreoUsuario);
                        System.out.println("Luchooo\tZona: "+filtroZona);
                        if(!textoCorreoUsuario.equals("")){
                            if(inicialmenteFavorito.equals("NO") && fav){
                                System.out.println("Luchoooo\tVa a agregar un favorito");
                                String URL_AGREGAR_FAVORITO="https://udvivienda360.000webhostapp.com/agregar_favorito.php";
                                StringRequest stringRequestAgregarFavorito= new StringRequest(
                                        Request.Method.POST,
                                        URL_AGREGAR_FAVORITO,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                System.out.println("Luchoooo la respuesta de agregar favoritos fue: "+response);
                                                MisFavoritosFragment misFavoritosFragment= new MisFavoritosFragment();
                                                fragmentTransaction.remove(este);
                                                fragmentTransaction.replace(R.id.contenedor_ppal_favoritos, misFavoritosFragment);
                                                fragmentTransaction.commit();
                                                try {
                                                    onDestroyView();
                                                    onDestroy();
                                                    onDetach();
                                                } catch (Throwable e) {
                                                    e.printStackTrace();
                                                }



                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                System.out.println("Luchoooooo\tError al agregar el favorito");
                                            }
                                        }
                                ){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> parametros= new HashMap<>();
                                        parametros.put("correo", convertirCadena(textoCorreoUsuario));
                                        parametros.put("favorito", convertirCadena(publicacion));
                                        return parametros;
                                    }
                                };
                                stringRequestAgregarFavorito.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                requestQueue.add(stringRequestAgregarFavorito);
                            }
                            if (inicialmenteFavorito.equals("SI") && !fav){
                                System.out.println("Luchoooo\tVa a eliminar un favorito");
                                String URL_ELIMINAR_FAVORITO="https://udvivienda360.000webhostapp.com/eliminar_favorito.php";
                                StringRequest stringRequestEliminarFavorito= new StringRequest(
                                        Request.Method.POST,
                                        URL_ELIMINAR_FAVORITO,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                System.out.println("Luchoooo la respuesta de eliminar favoritos fue: "+response);
                                                MisFavoritosFragment misFavoritosFragment= new MisFavoritosFragment();
                                                fragmentTransaction.remove(este);
                                                fragmentTransaction.replace(R.id.contenedor_ppal_favoritos, misFavoritosFragment);
                                                fragmentTransaction.commit();
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                System.out.println("Luchoooooo\tError al eliminar el favorito");
                                            }
                                        }
                                ){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> parametros= new HashMap<>();
                                        parametros.put("correo", convertirCadena(textoCorreoUsuario));
                                        parametros.put("favorito", convertirCadena(publicacion));
                                        return parametros;
                                    }
                                };
                                stringRequestEliminarFavorito.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                requestQueue.add(stringRequestEliminarFavorito);
                            }
                        }
                        break;
                    case "otras":
                        System.out.println("Luchooo\tReconocimiento de variables:");
                        System.out.println("Luchooo\tCorreo: "+textoCorreoUsuario);
                        System.out.println("Luchooo\tValor if1: "+textoCorreoUsuario.equals(""));
                        System.out.println("Luchooo\tInicialmente Favorito: "+inicialmenteFavorito);
                        System.out.println("Luchooo\tfav: "+textoCorreoUsuario);
                        System.out.println("Luchooo\tZona: "+filtroZona);
                        if(!textoCorreoUsuario.equals("")){
                            if(inicialmenteFavorito.equals("NO") && fav){
                                System.out.println("Luchoooo\tVa a agregar un favorito");
                                String URL_AGREGAR_FAVORITO="https://udvivienda360.000webhostapp.com/agregar_favorito.php";
                                StringRequest stringRequestAgregarFavorito= new StringRequest(
                                        Request.Method.POST,
                                        URL_AGREGAR_FAVORITO,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                System.out.println("Luchoooo la respuesta de agregar favoritos fue: "+response);
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                System.out.println("Luchoooooo\tError al agregar el favorito");
                                            }
                                        }
                                ){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> parametros= new HashMap<>();
                                        parametros.put("correo", convertirCadena(textoCorreoUsuario));
                                        parametros.put("favorito", convertirCadena(publicacion));
                                        return parametros;
                                    }
                                };
                                stringRequestAgregarFavorito.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                requestQueue.add(stringRequestAgregarFavorito);
                            }
                            if (inicialmenteFavorito.equals("SI") && !fav){
                                System.out.println("Luchoooo\tVa a eliminar un favorito");
                                String URL_ELIMINAR_FAVORITO="https://udvivienda360.000webhostapp.com/eliminar_favorito.php";
                                StringRequest stringRequestEliminarFavorito= new StringRequest(
                                        Request.Method.POST,
                                        URL_ELIMINAR_FAVORITO,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                System.out.println("Luchoooo la respuesta de eliminar favoritos fue: "+response);
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                System.out.println("Luchoooooo\tError al eliminar el favorito");
                                            }
                                        }
                                ){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> parametros= new HashMap<>();
                                        parametros.put("correo", convertirCadena(textoCorreoUsuario));
                                        parametros.put("favorito", convertirCadena(publicacion));
                                        return parametros;
                                    }
                                };
                                stringRequestEliminarFavorito.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                requestQueue.add(stringRequestEliminarFavorito);
                            }
                        }
                        MiBusquedaFragment miBusquedaFragment= new MiBusquedaFragment(filtroZona, filtroOferta);
                        fragmentTransaction.remove(este);
                        fragmentTransaction.replace(R.id.contenedor_ppal_buscar, miBusquedaFragment);
                        fragmentTransaction.commit();
                        try {
                            onDestroyView();
                            onDestroy();
                            onDetach();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        break;
                }
                try {
                    onDestroyView();
                    onDestroy();
                    onDetach();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        contactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactarFragment contactarFragment= new ContactarFragment(String.valueOf(publicacion), tvTitulo.getText().toString());
                contactarFragment.show(getParentFragment().getChildFragmentManager(),"ContactarPropietario");
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map= googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    public void visualizarModelo3D(){
        if(tipoPublicacion.equals("propia")){
            FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
            ModeloFragment modeloFragment= new ModeloFragment(publicacion, modelo);
            fragmentTransaction.replace(R.id.contenedor_ppal_perfil, modeloFragment);
            fragmentTransaction.commit();
        }
        else{
            if(tipoPublicacion.equals("otras")){
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                ModeloFragment modeloFragment= new ModeloFragment(publicacion, modelo);
                fragmentTransaction.replace(R.id.contenedor_ppal_buscar, modeloFragment);
                fragmentTransaction.commit();
            }
            else{
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                ModeloFragment modeloFragment= new ModeloFragment(publicacion, modelo);
                fragmentTransaction.replace(R.id.contenedor_ppal_favoritos, modeloFragment);
                fragmentTransaction.commit();
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

    public String convertirMayuscPrimera(String texto){
        StringTokenizer st= new StringTokenizer(texto, " ");
        StringBuilder textoConvertido= new StringBuilder();
        while (st.hasMoreTokens()){
            String pedazo=st.nextToken();
            if(numerosRomanos.contains(pedazo)){
                textoConvertido.append(" ").append(pedazo);
            }
            else{
                int indiceMayusc=-1;
                for(int i=0; i<pedazo.length(); i++){
                    if(((int) pedazo.charAt(i))>=65 &&((int) pedazo.charAt(i))<=90 || ((int) pedazo.charAt(i))>=97 &&((int) pedazo.charAt(i))<=122 || ((int) pedazo.charAt(i))>=160 &&((int) pedazo.charAt(i))<=165 || ((int) pedazo.charAt(i))==181 || ((int) pedazo.charAt(i))==144 || ((int) pedazo.charAt(i))==214 || ((int) pedazo.charAt(i))==224 || ((int) pedazo.charAt(i))==233){
                        indiceMayusc=i;
                        break;
                    }
                    else{
                        indiceMayusc=i+1;
                    }
                }
                String pedazoConvertido="";
                for(int i=0; i<pedazo.length(); i++){
                    if(indiceMayusc==i){
                        pedazoConvertido=pedazoConvertido+Character.toUpperCase(pedazo.charAt(i));
                    }
                    else{
                        pedazoConvertido=pedazoConvertido+Character.toLowerCase(pedazo.charAt(i));
                    }
                }
                textoConvertido.append(" ").append(pedazoConvertido);
            }
        }
        return textoConvertido.toString();
    }

    public ArrayList<String> dividirBarrioyLocalidad(String texto){
        ArrayList<String> partes= new ArrayList<>();
        partes.add(texto.substring(0,texto.indexOf("(")-1).trim());
        partes.add(texto.substring(texto.indexOf("(")+1,texto.indexOf(")")));
        return partes;
    }

    private void inicializarNumerosRomanos(){
        numerosRomanos= new ArrayList<>();
        numerosRomanos.add("I");
        numerosRomanos.add("II");
        numerosRomanos.add("III");
        numerosRomanos.add("IV");
        numerosRomanos.add("V");
        numerosRomanos.add("VI");
        numerosRomanos.add("VII");
        numerosRomanos.add("VIII");
        numerosRomanos.add("IX");
        numerosRomanos.add("X");
        numerosRomanos.add("XI");
        numerosRomanos.add("XII");
        numerosRomanos.add("XIII");
        numerosRomanos.add("XIV");
        numerosRomanos.add("XV");
        numerosRomanos.add("XVI");
        numerosRomanos.add("XVII");
        numerosRomanos.add("XVIII");
        numerosRomanos.add("XIX");
        numerosRomanos.add("XX");
        numerosRomanos.add("XXI");
        numerosRomanos.add("XXII");
        numerosRomanos.add("XXIII");
        numerosRomanos.add("XXIV");
        numerosRomanos.add("XXV");
        numerosRomanos.add("XXVI");
        numerosRomanos.add("XXVII");
        numerosRomanos.add("XXVIII");
        numerosRomanos.add("XXIX");
        numerosRomanos.add("XXX");
        numerosRomanos.add("XXXI");
        numerosRomanos.add("XXXII");
        numerosRomanos.add("XXXIII");
        numerosRomanos.add("XXXIV");
        numerosRomanos.add("XXXV");
        numerosRomanos.add("XXXVI");
        numerosRomanos.add("XXXVII");
        numerosRomanos.add("XXXVIII");
        numerosRomanos.add("XXXIX");
        numerosRomanos.add("XL");
        numerosRomanos.add("XLI");
        numerosRomanos.add("XLII");
        numerosRomanos.add("XLIII");
        numerosRomanos.add("XLIV");
        numerosRomanos.add("XLV");
        numerosRomanos.add("XLVI");
        numerosRomanos.add("XLVII");
        numerosRomanos.add("XLVIII");
        numerosRomanos.add("XLIX");
        numerosRomanos.add("L");
        numerosRomanos.add("LI");
        numerosRomanos.add("LII");
        numerosRomanos.add("LIII");
        numerosRomanos.add("LIV");
        numerosRomanos.add("LV");
        numerosRomanos.add("LVI");
        numerosRomanos.add("LVII");
        numerosRomanos.add("LVIII");
        numerosRomanos.add("LIX");
        numerosRomanos.add("LX");
        numerosRomanos.add("LXI");
        numerosRomanos.add("LXII");
        numerosRomanos.add("LXIII");
        numerosRomanos.add("LXIV");
        numerosRomanos.add("LXV");
        numerosRomanos.add("LXVI");
        numerosRomanos.add("LXVII");
        numerosRomanos.add("LXVIII");
        numerosRomanos.add("LXIX");
        numerosRomanos.add("LXX");
        numerosRomanos.add("LXXI");
        numerosRomanos.add("LXXII");
        numerosRomanos.add("LXXIII");
        numerosRomanos.add("LXXIV");
        numerosRomanos.add("LXXV");
        numerosRomanos.add("LXXVI");
        numerosRomanos.add("LXXVII");
        numerosRomanos.add("LXXVIII");
        numerosRomanos.add("LXXIX");
        numerosRomanos.add("LXXX");
        numerosRomanos.add("LXXXI");
        numerosRomanos.add("LXXXII");
        numerosRomanos.add("LXXXIII");
        numerosRomanos.add("LXXXIV");
        numerosRomanos.add("LXXXV");
        numerosRomanos.add("LXXXVI");
        numerosRomanos.add("LXXXVII");
        numerosRomanos.add("LXXXVIII");
        numerosRomanos.add("LXXXIX");
        numerosRomanos.add("XC");
        numerosRomanos.add("XCI");
        numerosRomanos.add("XCII");
        numerosRomanos.add("XCIII");
        numerosRomanos.add("XCIV");
        numerosRomanos.add("XCV");
        numerosRomanos.add("XCVI");
        numerosRomanos.add("XCVII");
        numerosRomanos.add("XCVIII");
        numerosRomanos.add("XCIX");
        numerosRomanos.add("C");
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        public DownloadImageTask() {

        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                System.out.println("Error: "+e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            ivFoto.setImageBitmap(result);
        }
    }


}