package com.lulapps.vivienda360;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MiBusquedaFragment extends Fragment {

    ScrollView contenedorResultados;
    TextView cantidadResultados, sinResultados, seccion;
    ImageView flechaCurva, filtro, botonAtras;
    RecyclerView recyclerView;
    Button cargarMasResultados;
    int resultados;
    String textoZona, textoOferta;
    String filtroSQL;
    FloatingActionButton botonAgregar;
    List<ListElement> listadoPublicaciones;
    RequestQueue requestQueue;
    ListAdapter listAdapter=null;
    int limit;
    List<Bitmap> listaImagenes;
    ArrayList<String> numerosRomanos;

    public MiBusquedaFragment() {

    }

    public MiBusquedaFragment(String textoZona, String textoOferta) {
        this.textoZona= textoZona;
        this.textoOferta= textoOferta;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue= Volley.newRequestQueue(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mi_busqueda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seccion= view.findViewById(R.id.textViewSeccion);
        cantidadResultados= view.findViewById(R.id.textViewCantidadResultados);
        sinResultados= view.findViewById(R.id.textViewSinPublicaciones);
        flechaCurva=view.findViewById(R.id.imageViewFlechaCurvaFavoritos);
        botonAgregar= view.findViewById(R.id.floatingActionButtonAgregarFavoritos);
        contenedorResultados= view.findViewById(R.id.scrollViewContenedorPublicaciones);
        recyclerView= view.findViewById(R.id.recyclerViewMisFavoritos);
        cargarMasResultados= view.findViewById(R.id.bUttonCargarMasResultados);
        filtro= view.findViewById(R.id.imageViewFiltroMisFavoritos);
        botonAtras= view.findViewById(R.id.imageViewButtonAtras);
        listaImagenes= new ArrayList<>();
        filtroSQL="(1)";
        inicializarNumerosRomanos();
        listadoPublicaciones= new ArrayList<>();


        String seccionPaso=convertirMayuscPrimera(textoZona);

        seccion.setText(seccionPaso);

        cantidadResultados.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("Luchooooo\tEjecutó el cambio en cantidad resultados");
                if(s.toString().equals("0 resultados")){
                    sinResultados.setVisibility(View.VISIBLE);
                    flechaCurva.setVisibility(View.VISIBLE);
                    botonAgregar.setVisibility(View.VISIBLE);
                }
                else{
                    sinResultados.setVisibility(View.GONE);
                    flechaCurva.setVisibility(View.GONE);
                    botonAgregar.setVisibility(View.GONE);
                }
            }
        });

        limit=0;
        String URL_OBTENER_RESULTADOS="https://udvivienda360.000webhostapp.com/obtener_publicaciones_por_zona.php?zona="+convertirCadena(textoZona)+"&oferta="+convertirCadena(textoOferta)+"&limit="+limit+"&filtro="+convertirCadena(filtroSQL);
        System.out.println("Luchoooo\tURL: "+URL_OBTENER_RESULTADOS);

        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest(
                Request.Method.GET,
                URL_OBTENER_RESULTADOS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println("Luchoooo\tListado obtenido: ("+response.length()+") items");
                            for(int i=0; i<response.length(); i++){
                                if(response.getJSONObject(i).getString("barrio").equals("final")){
                                    resultados=Integer.parseInt(response.getJSONObject(i).getString("conteo"));
                                    cantidadResultados.setText(resultados+" "+getResources().getString(R.string.resultados));
                                }
                                else{
                                    System.out.println("Luchooo\tPublicacion # "+response.getJSONObject(i).getString("id")+", Barrio: "+response.getJSONObject(i).getString("barrio")+" Precio: "+response.getJSONObject(i).getString("precio"));
                                    listadoPublicaciones.add(new ListElement(response.getJSONObject(i).getString("barrio"), response.getJSONObject(i).getString("precio"), response.getJSONObject(i).getString("areaconst"), response.getJSONObject(i).getString("habitaciones"), response.getJSONObject(i).getString("banos"), getResources().getDrawable(R.drawable.logo_app_transparente), response.getJSONObject(i).getString("id"), response.getJSONObject(i).getString("oferta"), response.getJSONObject(i).getString("inmueble"), "otras"));
                                }

                            }

                            listAdapter= new ListAdapter(listadoPublicaciones, getContext(), new OnAdapterItemClickListener() {
                                @Override
                                public void onAdapterItemClickListener(View v, int position) {
                                    System.out.println("Luchooo\tHas clicado el adapter numero "+position+", Barrio: "+listadoPublicaciones.get(position).getBarrio());
                                    System.out.println("Luchooo\tDatos de envio a visualizacion. ID: "+listadoPublicaciones.get(position).getId()+", otras ");
                                    FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                    VisualizacionFragment visualizacionFragment= new VisualizacionFragment(listadoPublicaciones.get(position).getId(),"otras", textoZona, textoOferta);
                                    fragmentTransaction.replace(R.id.contenedor_ppal_buscar, visualizacionFragment);
                                    fragmentTransaction.commit();
                                }

                                @Override
                                public void onEditarButtonClickListener(View v, int idPublicacion) {
                                    //Acá este metodo no debe hacer nada
                                }

                                @Override
                                public void onEliminarButtonClickListener(View v, int idPublicacion) {
                                    //Acá este metodo no debe hacer nada
                                }

                                @Override
                                public void onContactarButtonClickListener(View v, int idPublicacion) {
                                    String zonaPublicacion="";
                                    String ofertaPublicacion="";
                                    String inmueblePublicacion="";
                                    for(int i=0; i<listadoPublicaciones.size(); i++){
                                        if(listadoPublicaciones.get(i).getId().equals(String.valueOf(idPublicacion))){
                                            zonaPublicacion=listadoPublicaciones.get(i).getBarrio();
                                            ofertaPublicacion=listadoPublicaciones.get(i).getTipoOferta();
                                            inmueblePublicacion=listadoPublicaciones.get(i).getTipoInmueble();
                                        }
                                    }
                                    String barrioPublicacion= dividirBarrioyLocalidad(zonaPublicacion).get(0);
                                    String tituloPublicacion=inmueblePublicacion+" "+getResources().getString(R.string.articulo_en)+" "+ofertaPublicacion+" "+getResources().getString(R.string.articulo_en)+" "+barrioPublicacion;
                                    tituloPublicacion= convertirMayuscPrimera(tituloPublicacion);

                                    ContactarFragment contactarFragment= new ContactarFragment(String.valueOf(idPublicacion), tituloPublicacion);
                                    contactarFragment.show(getParentFragment().getChildFragmentManager(),"ContactarPropietario");

                                }


                            });

                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(listAdapter);
                            verificarActivarBotonCargarMas();
                            for(int i=0; i<response.length(); i++){
                                new DownloadImageTask(i).execute(response.getJSONObject(i).getString("foto"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Luchoooooo error: "+error.toString()+"\n"+error.getMessage()+"\n"+error.getLocalizedMessage());
                        error.printStackTrace();
                    }
                }
        );
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
        requestQueue.add(jsonArrayRequest);

        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.navigation_perfil);
                try {
                    onDestroyView();
                    onDestroy();
                    onDetach();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FiltroFragment filtroFragment= new FiltroFragment(new OnFilterAppliedListener() {
                    @Override
                    public void OnFilterAppliedListener(String filtro) {
                        filtroSQL=filtro;

                        limit=0;
                        int cantidadBorrados= listadoPublicaciones.size();
                        listadoPublicaciones.clear();
                        listadoPublicaciones=new ArrayList<>();
                        listAdapter.notifyItemRangeRemoved(0, cantidadBorrados);
                        recyclerView.setAdapter(null);

                        String URL_OBTENER_RESULTADOS_FILTRADOS="https://udvivienda360.000webhostapp.com/obtener_publicaciones_por_zona.php?zona="+convertirCadena(textoZona)+"&oferta="+convertirCadena(textoOferta)+"&limit="+limit+"&filtro="+convertirCadena(filtroSQL);
                        System.out.println("Luchooooo\t URL USADA: "+URL_OBTENER_RESULTADOS_FILTRADOS);
                        JsonArrayRequest jsonArrayRequestFiltrado= new JsonArrayRequest(
                                Request.Method.GET,
                                URL_OBTENER_RESULTADOS_FILTRADOS,
                                null,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        if(response.length()>0){
                                            try {
                                                System.out.println("Luchoooo\tListado obtenido:");
                                                for(int i=0; i<response.length(); i++){
                                                    if(response.getJSONObject(i).getString("barrio").equals("final")){
                                                        resultados=Integer.parseInt(response.getJSONObject(i).getString("conteo"));
                                                        cantidadResultados.setText(resultados+" "+getResources().getString(R.string.resultados));
                                                        System.out.println("Luchoooo\tnuevo valor para cantidadPublicaciones: "+resultados);
                                                    }
                                                    else{
                                                        System.out.println("Luchooo\tPublicacion # "+response.getJSONObject(i).getString("id")+", Barrio: "+response.getJSONObject(i).getString("barrio")+" Precio: "+response.getJSONObject(i).getString("precio"));
                                                        listadoPublicaciones.add(new ListElement(response.getJSONObject(i).getString("barrio"), response.getJSONObject(i).getString("precio"), response.getJSONObject(i).getString("areaconst"), response.getJSONObject(i).getString("habitaciones"), response.getJSONObject(i).getString("banos"), getResources().getDrawable(R.drawable.logo_app_transparente), response.getJSONObject(i).getString("id"), response.getJSONObject(i).getString("oferta"), response.getJSONObject(i).getString("inmueble"), "otras"));
                                                    }

                                                }
                                                listAdapter= new ListAdapter(listadoPublicaciones, getContext(), new OnAdapterItemClickListener() {
                                                    @Override
                                                    public void onAdapterItemClickListener(View v, int position) {
                                                        System.out.println("Luchooo\tHas clicado el adapter numero "+position+", Barrio: "+listadoPublicaciones.get(position).getBarrio());
                                                        FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                                        VisualizacionFragment visualizacionFragment= new VisualizacionFragment(listadoPublicaciones.get(position).getId(), "otras", textoZona, textoOferta);
                                                        fragmentTransaction.replace(R.id.contenedor_ppal_buscar, visualizacionFragment);
                                                        fragmentTransaction.commit();
                                                    }

                                                    @Override
                                                    public void onEditarButtonClickListener(View v, int idPublicacion) {
                                                        //Acá este metodo no debe hacer nada
                                                    }

                                                    @Override
                                                    public void onEliminarButtonClickListener(View v, int idPublicacion) {
                                                        //Acá este metodo no debe hacer nada
                                                    }

                                                    @Override
                                                    public void onContactarButtonClickListener(View v, int idPublicacion) {

                                                        String zonaPublicacion="";
                                                        String ofertaPublicacion="";
                                                        String inmueblePublicacion="";
                                                        for(int i=0; i<listadoPublicaciones.size(); i++){
                                                            if(listadoPublicaciones.get(i).getId().equals(String.valueOf(idPublicacion))){
                                                                zonaPublicacion=listadoPublicaciones.get(i).getBarrio();
                                                                ofertaPublicacion=listadoPublicaciones.get(i).getTipoOferta();
                                                                inmueblePublicacion=listadoPublicaciones.get(i).getTipoInmueble();
                                                            }
                                                        }
                                                        String barrioPublicacion= dividirBarrioyLocalidad(zonaPublicacion).get(0);
                                                        String tituloPublicacion=inmueblePublicacion+" "+getResources().getString(R.string.articulo_en)+" "+ofertaPublicacion+" "+getResources().getString(R.string.articulo_en)+" "+barrioPublicacion;
                                                        tituloPublicacion= convertirMayuscPrimera(tituloPublicacion);

                                                        ContactarFragment contactarFragment= new ContactarFragment(String.valueOf(idPublicacion), tituloPublicacion);
                                                        contactarFragment.show(getParentFragment().getChildFragmentManager(),"ContactarPropietario");

                                                    }


                                                });

                                                recyclerView.setHasFixedSize(true);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerView.setAdapter(listAdapter);
                                                verificarActivarBotonCargarMas();
                                                for(int i=0; i<response.length(); i++){
                                                    new DownloadImageTask(i).execute(response.getJSONObject(i).getString("foto"));
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            resultados=0;
                                            recyclerView.setHasFixedSize(true);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                            recyclerView.setAdapter(listAdapter);
                                            verificarActivarBotonCargarMas();
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println("Luchoooooo error en linea 768 de MisPublicaciones: "+error.toString()+"\n"+error.getMessage()+"\n"+error.getLocalizedMessage());
                                        error.printStackTrace();
                                    }
                                }
                        );
                        jsonArrayRequestFiltrado.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                        requestQueue.add(jsonArrayRequestFiltrado);

                        //*************************************************************************
                    }
                }, filtroSQL);
                filtroFragment.show(getParentFragment().getChildFragmentManager(),"MostrarFiltros");
            }
        });

        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                SeccionBuscarFragment seccionBuscarFragment= new SeccionBuscarFragment();
                fragmentTransaction.replace(R.id.contenedor_ppal_buscar, seccionBuscarFragment);
                fragmentTransaction.commit();
                try {
                    onDestroyView();
                    onDestroy();
                    onDetach();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        cargarMasResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Luchooo\tCargando más resultados");
                limit=listAdapter.getItemCount();
                String URL_OBTENER_RESULTADOS_SECUNDARIOS="https://udvivienda360.000webhostapp.com/obtener_publicaciones_por_zona.php?zona="+convertirCadena(textoZona)+"&oferta="+convertirCadena(textoOferta)+"&limit="+limit+"&filtro="+convertirCadena(filtroSQL);
                JsonArrayRequest jsonArrayRequestSecundarias= new JsonArrayRequest(
                        Request.Method.GET,
                        URL_OBTENER_RESULTADOS_SECUNDARIOS,
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    System.out.println("Luchoooo\tPublicaciones adicionales:");
                                    for(int i=0; i<response.length(); i++){
                                        if(response.getJSONObject(i).getString("barrio").equals("final")){
                                            resultados=Integer.parseInt(response.getJSONObject(i).getString("conteo"));
                                            cantidadResultados.setText(resultados+" "+getResources().getString(R.string.resultados));
                                        }
                                        else{
                                            System.out.println("Luchooo\tPublicacion # "+response.getJSONObject(i).getString("id")+", Barrio: "+response.getJSONObject(i).getString("barrio")+" Precio: "+response.getJSONObject(i).getString("precio"));
                                            listadoPublicaciones.add(new ListElement(response.getJSONObject(i).getString("barrio"),
                                                    response.getJSONObject(i).getString("precio"), response.getJSONObject(i).getString("areaconst"),
                                                    response.getJSONObject(i).getString("habitaciones"), response.getJSONObject(i).getString("banos"),
                                                    getResources().getDrawable(R.drawable.logo_app_transparente), response.getJSONObject(i).getString("id"),
                                                    response.getJSONObject(i).getString("oferta"), response.getJSONObject(i).getString("inmueble"), "otras"));
                                            listAdapter.notifyItemInserted(listadoPublicaciones.size()-1);
                                            new DownloadImageTask(listadoPublicaciones.size()-1).execute(response.getJSONObject(i).getString("foto"));
                                        }
                                    }
                                    System.out.println("Luchooo\tLos adaptadores tras cargar las nuevas publicaciones son:");
                                    for(int i=0; i<listAdapter.getItemCount(); i++){
                                        System.out.println("Luchooo\t"+(i+1)+": ID # "+listadoPublicaciones.get(i).getId()+", Barrio: "+listadoPublicaciones.get(i).getBarrio());
                                    }
                                    verificarActivarBotonCargarMas();
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
                jsonArrayRequestSecundarias.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                requestQueue.add(jsonArrayRequestSecundarias);
            }
        });
    }

    public void verificarActivarBotonCargarMas(){
        System.out.println("Luchooo\tAnalisis para activar boton cargas más");
        limit=listadoPublicaciones.size();
        if(limit<resultados){
            cargarMasResultados.setVisibility(View.VISIBLE);
            System.out.println("Luchooo\tActivó boton cargar más");
        }
        else{
            cargarMasResultados.setVisibility(View.GONE);
            System.out.println("Luchooo\tDesactivó boton cargar más");
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        int posicion;

        public DownloadImageTask(int posicion) {
            this.posicion=posicion;
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
            listaImagenes.add(result);
            listadoPublicaciones.get(posicion).setFoto(new BitmapDrawable(result));
            listAdapter.notifyItemChanged(posicion);
        }
    }

    private String convertirCadena(String text){
        String resultado="";
        for (int i=0; i<text.length(); i++){
            int ascii=text.charAt(i);
            resultado=resultado+ascii+"-*-";
        }
        return resultado;
    }

    public ArrayList<String> dividirBarrioyLocalidad(String texto){
        ArrayList<String> partes= new ArrayList<>();
        partes.add(texto.substring(0,texto.indexOf("(")-1).trim());
        partes.add(texto.substring(texto.indexOf("(")+1,texto.indexOf(")")));
        return partes;
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
}