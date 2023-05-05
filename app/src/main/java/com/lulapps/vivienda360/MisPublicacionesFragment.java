package com.lulapps.vivienda360;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class MisPublicacionesFragment extends Fragment{

    ScrollView contenedorPublicaciones;
    TextView cantidadResultados, sinPublicaciones;
    ImageView flechaCurva, filtro, botonAtras;
    RecyclerView recyclerView;
    Button cargarMasResultados;
    int publicaciones;
    String textoCorreo, textoPublicados;
    String filtroSQL;
    FloatingActionButton botonAgregar;
    List<ListElement> listadoPublicaciones;
    RequestQueue requestQueue;
    ListAdapter listAdapter=null;
    int limit;
    List<Bitmap> listaImagenes;

    public MisPublicacionesFragment() {

    }

    public MisPublicacionesFragment(String mail, String pub) {
        textoCorreo=mail;
        System.out.println("Luchoooo\tpub tiene "+pub.length()+" caracteres");
        if(!pub.equals("null") && pub.length()>0 && !pub.isEmpty()){
            textoPublicados=pub;
            StringTokenizer st = new StringTokenizer(pub, " ");
            publicaciones=st.countTokens();
        }
        else{
            textoPublicados="";
            publicaciones=0;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue= Volley.newRequestQueue(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_publicaciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        cantidadResultados= view.findViewById(R.id.textViewCantidadResultados);
        sinPublicaciones= view.findViewById(R.id.textViewSinPublicaciones);
        flechaCurva=view.findViewById(R.id.imageViewFlechaCurvaFavoritos);
        botonAgregar= (FloatingActionButton) view.findViewById(R.id.floatingActionButtonAgregarFavoritos);
        contenedorPublicaciones= view.findViewById(R.id.scrollViewContenedorPublicaciones);
        recyclerView= view.findViewById(R.id.recyclerViewMisFavoritos);
        cargarMasResultados= view.findViewById(R.id.bUttonCargarMasResultados);
        filtro= view.findViewById(R.id.imageViewFiltroMisFavoritos);
        botonAtras= view.findViewById(R.id.imageViewButtonAtras);
        listaImagenes= new ArrayList<>();
        filtroSQL="(1)";

        cantidadResultados.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("0 resultados")){
                    sinPublicaciones.setVisibility(View.VISIBLE);
                    flechaCurva.setVisibility(View.VISIBLE);
                }
                else{
                    sinPublicaciones.setVisibility(View.GONE);
                    flechaCurva.setVisibility(View.GONE);
                }
            }
        });

        if(textoCorreo==null){
            InputStream is= null;
            BufferedReader reader=null;
            try {
                is = this.getActivity().openFileInput("user_data.txt");
                reader= new BufferedReader(new InputStreamReader(is));
                if(is!=null){
                    textoCorreo=reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    textoPublicados=reader.readLine();
                    StringTokenizer st = new StringTokenizer(textoPublicados, " ");
                    publicaciones=st.countTokens();
                }
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } finally {
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

        if(textoPublicados!=null && !textoPublicados.equals("")){
            sinPublicaciones.setVisibility(View.GONE);
            flechaCurva.setVisibility(View.GONE);
            listadoPublicaciones= new ArrayList<>();

            limit=0;
            String URL_OBTENER_PUBLICACIONES="https://udvivienda360.000webhostapp.com/obtener_publicaciones_por_id.php?id="+convertirCadena(textoPublicados)+"&limit="+limit+"&filtro="+convertirCadena(filtroSQL);


            JsonArrayRequest jsonArrayRequest= new JsonArrayRequest(
                    Request.Method.GET,
                    URL_OBTENER_PUBLICACIONES,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                System.out.println("Luchoooo\tListado obtenido:");
                                for(int i=0; i<response.length(); i++){
                                    if(response.getJSONObject(i).getString("barrio").equals("final")){
                                        publicaciones=Integer.parseInt(response.getJSONObject(i).getString("conteo"));
                                    }
                                    else{
                                        System.out.println("Luchooo\tPublicacion # "+response.getJSONObject(i).getString("id")+", Barrio: "+response.getJSONObject(i).getString("barrio")+" Precio: "+response.getJSONObject(i).getString("precio"));
                                        listadoPublicaciones.add(new ListElement(response.getJSONObject(i).getString("barrio"), response.getJSONObject(i).getString("precio"), response.getJSONObject(i).getString("areaconst"), response.getJSONObject(i).getString("habitaciones"), response.getJSONObject(i).getString("banos"), getResources().getDrawable(R.drawable.logo_app_transparente), response.getJSONObject(i).getString("id"), response.getJSONObject(i).getString("oferta"), response.getJSONObject(i).getString("inmueble"), "propia"));
                                    }

                                }

                                listAdapter= new ListAdapter(listadoPublicaciones, getContext(), new OnAdapterItemClickListener() {
                                    @Override
                                    public void onAdapterItemClickListener(View v, int position) {
                                        System.out.println("Luchooo\tHas clicado el adapter numero "+position+", Barrio: "+listadoPublicaciones.get(position).getBarrio());
                                        FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                        VisualizacionFragment visualizacionFragment= new VisualizacionFragment(listadoPublicaciones.get(position).getId(),"propia");
                                        fragmentTransaction.replace(R.id.contenedor_ppal_perfil, visualizacionFragment);
                                        fragmentTransaction.commit();
                                    }

                                    @Override
                                    public void onEditarButtonClickListener(View v, int idPublicacion) {
                                        String URL_OBTENER_PUBLICACIONES_EDITAR="https://udvivienda360.000webhostapp.com/obtener_publicacion_completa_por_id.php?id="+convertirCadena(String.valueOf(idPublicacion))+"&correo="+convertirCadena(" ");
                                        JsonArrayRequest jsonArrayRequestEdicion= new JsonArrayRequest(
                                                Request.Method.GET,
                                                URL_OBTENER_PUBLICACIONES_EDITAR,
                                                null,
                                                new Response.Listener<JSONArray>() {
                                                    @Override
                                                    public void onResponse(JSONArray response) {
                                                        String idPaso, inmueblePaso, ofertaPaso, precioPaso, areaconstPaso,
                                                                habitacionesPaso, banosPaso, garajePaso, estratoPaso,
                                                                antiguedadPaso, descripcionPaso, direccionPaso, fotosPaso, modeloPaso="";
                                                        try {
                                                            idPaso=response.getJSONObject(0).getString("id");
                                                            inmueblePaso=response.getJSONObject(0).getString("inmueble");
                                                            ofertaPaso=response.getJSONObject(0).getString("oferta");
                                                            precioPaso=response.getJSONObject(0).getString("precio");
                                                            areaconstPaso=response.getJSONObject(0).getString("areaconst");
                                                            habitacionesPaso=response.getJSONObject(0).getString("habitaciones");
                                                            banosPaso=response.getJSONObject(0).getString("banos");
                                                            garajePaso=response.getJSONObject(0).getString("garajes");
                                                            estratoPaso=response.getJSONObject(0).getString("estrato");
                                                            antiguedadPaso=response.getJSONObject(0).getString("antiguedad");
                                                            descripcionPaso=response.getJSONObject(0).getString("descripcion");
                                                            direccionPaso=response.getJSONObject(0).getString("direccion");
                                                            fotosPaso=response.getJSONObject(0).getString("foto");
                                                            modeloPaso=response.getJSONObject(0).getString("modelo");

                                                            FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                                            PublicarInmuebleFragment publicarInmuebleFragment= new PublicarInmuebleFragment(idPaso, inmueblePaso,ofertaPaso,precioPaso,areaconstPaso,habitacionesPaso,banosPaso,garajePaso,estratoPaso,antiguedadPaso,descripcionPaso,direccionPaso,fotosPaso, modeloPaso);
                                                            fragmentTransaction.replace(R.id.contenedor_ppal_perfil, publicarInmuebleFragment);
                                                            fragmentTransaction.commit();
                                                            try {
                                                                onDestroyView();
                                                                onDestroy();
                                                                onDetach();
                                                            } catch (Throwable e) {
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

                                                    }
                                                }
                                        );
                                        jsonArrayRequestEdicion.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                        requestQueue.add(jsonArrayRequestEdicion);
                                    }

                                    @Override
                                    public void onEliminarButtonClickListener(View v, int idPublicacion) {
                                        for(int i=0; i<listadoPublicaciones.size(); i++){
                                            if(listadoPublicaciones.get(i).getId().equals(String.valueOf(idPublicacion))){
                                                int indiceListado=i;
                                                String URL_ELIMINAR_PUBLICACION="https://udvivienda360.000webhostapp.com/eliminar_publicacion.php";
                                                StringRequest stringRequestEliminarPublicacion = new StringRequest(
                                                        Request.Method.POST,
                                                        URL_ELIMINAR_PUBLICACION,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                System.out.println("Luchoooo la respuesta de eliminar publicacion fue: "+response);
                                                                if(response.equals("OK")){
                                                                    StringTokenizer stringTokenizer= new StringTokenizer(textoPublicados, " ");
                                                                    String resultadoPublicados="";
                                                                    String token="";
                                                                    while (stringTokenizer.hasMoreTokens()){
                                                                        token=stringTokenizer.nextToken();
                                                                        if(Integer.parseInt(token)!=idPublicacion){
                                                                            if(resultadoPublicados.equals("")){
                                                                                resultadoPublicados=token;
                                                                            }
                                                                            else{
                                                                                resultadoPublicados=resultadoPublicados+" "+token;
                                                                            }
                                                                        }
                                                                    }
                                                                    String publicadosModificados= resultadoPublicados;
                                                                    String URL_ACTUALIZAR_PUBLICADOS="https://udvivienda360.000webhostapp.com/actualizar_publicados.php";
                                                                    StringRequest stringRequestActualizarPublicados= new StringRequest(
                                                                            Request.Method.POST,
                                                                            URL_ACTUALIZAR_PUBLICADOS,
                                                                            new Response.Listener<String>() {
                                                                                @Override
                                                                                public void onResponse(String response) {
                                                                                    System.out.println("Luchoooo la respuesta de actualizar publicados fue: "+response);
                                                                                    if(response.equals("OK")){

                                                                                        //--------------------------------------------------------
                                                                                        String textoCorreo2, textoNombre, textoTelefono, textoWhatsapp,
                                                                                                textoPregunta1, textoRespuesta1, textoPregunta2,
                                                                                                textoRespuesta2, textoFavoritos, textoPublicados2;

                                                                                        textoCorreo2= textoNombre= textoTelefono= textoWhatsapp=
                                                                                                textoPregunta1= textoRespuesta1= textoPregunta2=
                                                                                                        textoRespuesta2= textoFavoritos= textoPublicados2="";

                                                                                        InputStream is= null;
                                                                                        BufferedReader reader=null;
                                                                                        try {
                                                                                            is = getActivity().openFileInput("user_data.txt");
                                                                                            reader= new BufferedReader(new InputStreamReader(is));
                                                                                            if(is!=null){
                                                                                                textoCorreo2=reader.readLine();
                                                                                                textoNombre=reader.readLine();
                                                                                                textoTelefono=reader.readLine();
                                                                                                textoWhatsapp=reader.readLine();
                                                                                                textoPregunta1=reader.readLine();
                                                                                                textoRespuesta1=reader.readLine();
                                                                                                textoPregunta2=reader.readLine();
                                                                                                textoRespuesta2=reader.readLine();
                                                                                                textoFavoritos=reader.readLine();
                                                                                            }
                                                                                        } catch (FileNotFoundException fileNotFoundException) {
                                                                                            fileNotFoundException.printStackTrace();
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


                                                                                        OutputStreamWriter fos=null;
                                                                                        try {
                                                                                            fos = new OutputStreamWriter(getActivity().openFileOutput("user_data.txt", Context.MODE_PRIVATE));
                                                                                            fos.write(textoCorreo2+"\n");
                                                                                            fos.write(textoNombre+"\n");
                                                                                            fos.write(textoTelefono+"\n");
                                                                                            fos.write(textoWhatsapp+"\n");
                                                                                            fos.write(textoPregunta1+"\n");
                                                                                            fos.write(textoRespuesta1+"\n");
                                                                                            fos.write(textoPregunta2+"\n");
                                                                                            fos.write(textoRespuesta2+"\n");
                                                                                            fos.write(textoFavoritos+"\n");
                                                                                            fos.write(publicadosModificados+"\n");
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
                                                                                        textoPublicados=publicadosModificados;
                                                                                        publicaciones--;
                                                                                        cantidadResultados.setText(publicaciones+" "+getResources().getString(R.string.resultados));
                                                                                        listadoPublicaciones.remove(indiceListado);
                                                                                        listAdapter.notifyItemRemoved(indiceListado);
                                                                                    }
                                                                                }
                                                                            },
                                                                            new Response.ErrorListener() {
                                                                                @Override
                                                                                public void onErrorResponse(VolleyError error) {

                                                                                }
                                                                            }
                                                                    ){
                                                                        @Override
                                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                                            Map<String, String> parametros= new HashMap<>();
                                                                            parametros.put("correo", convertirCadena(textoCorreo));
                                                                            parametros.put("publicados", convertirCadena(publicadosModificados));
                                                                            return parametros;
                                                                        }
                                                                    };
                                                                    stringRequestActualizarPublicados.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                                                    requestQueue.add(stringRequestActualizarPublicados);
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {

                                                            }
                                                        }
                                                ){
                                                    @Override
                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                        Map<String, String> parametros= new HashMap<>();
                                                        parametros.put("publicacion",convertirCadena(String.valueOf(idPublicacion)));
                                                        return parametros;
                                                    }
                                                };
                                                stringRequestEliminarPublicacion.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                                requestQueue.add(stringRequestEliminarPublicacion);
                                                break;
                                            }
                                        }
                                        //
                                    }

                                    @Override
                                    public void onContactarButtonClickListener(View v, int idPublicacion) {
                                        //Acá este metodo no debe hacer nada
                                    }


                                });

                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(listAdapter);
                                verificarActivarBotonCargarMas();
                                for(int i=0; i<response.length(); i++){
                                    new DownloadImageTask(i).execute(response.getJSONObject(i).getString("foto"));
                                }

                                //insertarBotonCargarMas();
                                //listAdapter.notifyItemInserted(listadoPublicaciones.size()-1);
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
        }
        else{

            //publicaciones=0;
        }
        cantidadResultados.setText(publicaciones+" "+getResources().getString(R.string.resultados));

        System.out.println("luchooo: \tCorreo: "+textoCorreo+"\tPublicados: "+textoPublicados);


        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                PublicarInmuebleFragment publicarInmuebleFragment= new PublicarInmuebleFragment(textoCorreo, textoPublicados);
                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, publicarInmuebleFragment);
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

        filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FiltroFragment filtroFragment= new FiltroFragment(new OnFilterAppliedListener() {
                    @Override
                    public void OnFilterAppliedListener(String filtro) {
                        filtroSQL=filtro;
                        System.out.println("Luchooooo\tFiltro SQL"+filtroSQL);

                        limit=0;
                        int cantidadBorrados= listadoPublicaciones.size();
                        listadoPublicaciones.clear();
                        listadoPublicaciones=new ArrayList<>();
                        listAdapter.notifyItemRangeRemoved(0, cantidadBorrados);
                        recyclerView.setAdapter(null);

                        String URL_OBTENER_PUBLICACIONES_FILTRADAS="https://udvivienda360.000webhostapp.com/obtener_publicaciones_por_id.php?id="+convertirCadena(textoPublicados)+"&limit="+limit+"&filtro="+convertirCadena(filtroSQL);
                        System.out.println("Luchooooo\t URL USADA: "+URL_OBTENER_PUBLICACIONES_FILTRADAS);
                        JsonArrayRequest jsonArrayRequestFiltrado= new JsonArrayRequest(
                                Request.Method.GET,
                                URL_OBTENER_PUBLICACIONES_FILTRADAS,
                                null,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        if(response.length()>0){
                                            try {
                                                System.out.println("Luchoooo\tListado obtenido:");
                                                for(int i=0; i<response.length(); i++){
                                                    if(response.getJSONObject(i).getString("barrio").equals("final")){
                                                        publicaciones=Integer.parseInt(response.getJSONObject(i).getString("conteo"));
                                                        System.out.println("Luchoooo\tnuevo valor para cantidadPublicaciones: "+publicaciones);
                                                    }
                                                    else{
                                                        System.out.println("Luchooo\tPublicacion # "+response.getJSONObject(i).getString("id")+", Barrio: "+response.getJSONObject(i).getString("barrio")+" Precio: "+response.getJSONObject(i).getString("precio"));
                                                        listadoPublicaciones.add(new ListElement(response.getJSONObject(i).getString("barrio"), response.getJSONObject(i).getString("precio"), response.getJSONObject(i).getString("areaconst"), response.getJSONObject(i).getString("habitaciones"), response.getJSONObject(i).getString("banos"), getResources().getDrawable(R.drawable.logo_app_transparente), response.getJSONObject(i).getString("id"), response.getJSONObject(i).getString("oferta"), response.getJSONObject(i).getString("inmueble"), "propia"));
                                                    }

                                                }
                                                listAdapter= new ListAdapter(listadoPublicaciones, getContext(), new OnAdapterItemClickListener() {
                                                    @Override
                                                    public void onAdapterItemClickListener(View v, int position) {
                                                        System.out.println("Luchooo\tHas clicado el adapter numero "+position+", Barrio: "+listadoPublicaciones.get(position).getBarrio());
                                                        FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                                        VisualizacionFragment visualizacionFragment= new VisualizacionFragment(listadoPublicaciones.get(position).getId(),"propia");
                                                        fragmentTransaction.replace(R.id.contenedor_ppal_perfil, visualizacionFragment);
                                                        fragmentTransaction.commit();
                                                    }

                                                    @Override
                                                    public void onEditarButtonClickListener(View v, int idPublicacion) {
                                                        String URL_OBTENER_PUBLICACIONES_EDITAR="https://udvivienda360.000webhostapp.com/obtener_publicacion_completa_por_id.php?id="+convertirCadena(String.valueOf(idPublicacion))+"&correo="+convertirCadena(" ");
                                                        JsonArrayRequest jsonArrayRequestEdicion= new JsonArrayRequest(
                                                                Request.Method.GET,
                                                                URL_OBTENER_PUBLICACIONES_EDITAR,
                                                                null,
                                                                new Response.Listener<JSONArray>() {
                                                                    @Override
                                                                    public void onResponse(JSONArray response) {
                                                                        String idPaso, inmueblePaso, ofertaPaso, precioPaso, areaconstPaso,
                                                                                habitacionesPaso, banosPaso, garajePaso, estratoPaso,
                                                                                antiguedadPaso, descripcionPaso, direccionPaso, fotosPaso, modeloPaso="";
                                                                        try {
                                                                            idPaso=response.getJSONObject(0).getString("id");
                                                                            inmueblePaso=response.getJSONObject(0).getString("inmueble");
                                                                            ofertaPaso=response.getJSONObject(0).getString("oferta");
                                                                            precioPaso=response.getJSONObject(0).getString("precio");
                                                                            areaconstPaso=response.getJSONObject(0).getString("areaconst");
                                                                            habitacionesPaso=response.getJSONObject(0).getString("habitaciones");
                                                                            banosPaso=response.getJSONObject(0).getString("banos");
                                                                            garajePaso=response.getJSONObject(0).getString("garajes");
                                                                            estratoPaso=response.getJSONObject(0).getString("estrato");
                                                                            antiguedadPaso=response.getJSONObject(0).getString("antiguedad");
                                                                            descripcionPaso=response.getJSONObject(0).getString("descripcion");
                                                                            direccionPaso=response.getJSONObject(0).getString("direccion");
                                                                            fotosPaso=response.getJSONObject(0).getString("foto");
                                                                            modeloPaso=response.getJSONObject(0).getString("modelo");

                                                                            FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                                                            PublicarInmuebleFragment publicarInmuebleFragment= new PublicarInmuebleFragment(idPaso, inmueblePaso,ofertaPaso,precioPaso,areaconstPaso,habitacionesPaso,banosPaso,garajePaso,estratoPaso,antiguedadPaso,descripcionPaso,direccionPaso,fotosPaso, modeloPaso);
                                                                            fragmentTransaction.replace(R.id.contenedor_ppal_perfil, publicarInmuebleFragment);
                                                                            fragmentTransaction.commit();
                                                                            try {
                                                                                onDestroyView();
                                                                                onDestroy();
                                                                                onDetach();
                                                                            } catch (Throwable e) {
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

                                                                    }
                                                                }
                                                        );
                                                        jsonArrayRequestEdicion.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                                        requestQueue.add(jsonArrayRequestEdicion);
                                                    }

                                                    @Override
                                                    public void onEliminarButtonClickListener(View v, int idPublicacion) {
                                                        for(int i=0; i<listadoPublicaciones.size(); i++){
                                                            if(listadoPublicaciones.get(i).getId().equals(String.valueOf(idPublicacion))){
                                                                int indiceListado=i;
                                                                String URL_ELIMINAR_PUBLICACION="https://udvivienda360.000webhostapp.com/eliminar_publicacion.php";
                                                                StringRequest stringRequestEliminarPublicacion = new StringRequest(
                                                                        Request.Method.POST,
                                                                        URL_ELIMINAR_PUBLICACION,
                                                                        new Response.Listener<String>() {
                                                                            @Override
                                                                            public void onResponse(String response) {
                                                                                System.out.println("Luchoooo la respuesta de eliminar publicacion fue: "+response);
                                                                                if(response.equals("OK")){
                                                                                    StringTokenizer stringTokenizer= new StringTokenizer(textoPublicados, " ");
                                                                                    String resultadoPublicados="";
                                                                                    String token="";
                                                                                    while (stringTokenizer.hasMoreTokens()){
                                                                                        token=stringTokenizer.nextToken();
                                                                                        if(Integer.parseInt(token)!=idPublicacion){
                                                                                            if(resultadoPublicados.equals("")){
                                                                                                resultadoPublicados=token;
                                                                                            }
                                                                                            else{
                                                                                                resultadoPublicados=resultadoPublicados+" "+token;
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    String publicadosModificados= resultadoPublicados;
                                                                                    String URL_ACTUALIZAR_PUBLICADOS="https://udvivienda360.000webhostapp.com/actualizar_publicados.php";
                                                                                    StringRequest stringRequestActualizarPublicados= new StringRequest(
                                                                                            Request.Method.POST,
                                                                                            URL_ACTUALIZAR_PUBLICADOS,
                                                                                            new Response.Listener<String>() {
                                                                                                @Override
                                                                                                public void onResponse(String response) {
                                                                                                    System.out.println("Luchoooo la respuesta de actualizar publicados fue: "+response);
                                                                                                    if(response.equals("OK")){

                                                                                                        //--------------------------------------------------------
                                                                                                        String textoCorreo2, textoNombre, textoTelefono, textoWhatsapp,
                                                                                                                textoPregunta1, textoRespuesta1, textoPregunta2,
                                                                                                                textoRespuesta2, textoFavoritos, textoPublicados2;

                                                                                                        textoCorreo2= textoNombre= textoTelefono= textoWhatsapp=
                                                                                                                textoPregunta1= textoRespuesta1= textoPregunta2=
                                                                                                                        textoRespuesta2= textoFavoritos= textoPublicados2="";

                                                                                                        InputStream is= null;
                                                                                                        BufferedReader reader=null;
                                                                                                        try {
                                                                                                            is = getActivity().openFileInput("user_data.txt");
                                                                                                            reader= new BufferedReader(new InputStreamReader(is));
                                                                                                            if(is!=null){
                                                                                                                textoCorreo2=reader.readLine();
                                                                                                                textoNombre=reader.readLine();
                                                                                                                textoTelefono=reader.readLine();
                                                                                                                textoWhatsapp=reader.readLine();
                                                                                                                textoPregunta1=reader.readLine();
                                                                                                                textoRespuesta1=reader.readLine();
                                                                                                                textoPregunta2=reader.readLine();
                                                                                                                textoRespuesta2=reader.readLine();
                                                                                                                textoFavoritos=reader.readLine();
                                                                                                            }
                                                                                                        } catch (FileNotFoundException fileNotFoundException) {
                                                                                                            fileNotFoundException.printStackTrace();
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


                                                                                                        OutputStreamWriter fos=null;
                                                                                                        try {
                                                                                                            fos = new OutputStreamWriter(getActivity().openFileOutput("user_data.txt", Context.MODE_PRIVATE));
                                                                                                            fos.write(textoCorreo2+"\n");
                                                                                                            fos.write(textoNombre+"\n");
                                                                                                            fos.write(textoTelefono+"\n");
                                                                                                            fos.write(textoWhatsapp+"\n");
                                                                                                            fos.write(textoPregunta1+"\n");
                                                                                                            fos.write(textoRespuesta1+"\n");
                                                                                                            fos.write(textoPregunta2+"\n");
                                                                                                            fos.write(textoRespuesta2+"\n");
                                                                                                            fos.write(textoFavoritos+"\n");
                                                                                                            fos.write(publicadosModificados+"\n");
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
                                                                                                        textoPublicados=publicadosModificados;
                                                                                                        publicaciones--;
                                                                                                        cantidadResultados.setText(publicaciones+" "+getResources().getString(R.string.resultados));
                                                                                                        listadoPublicaciones.remove(indiceListado);
                                                                                                        listAdapter.notifyItemRemoved(indiceListado);
                                                                                                    }
                                                                                                }
                                                                                            },
                                                                                            new Response.ErrorListener() {
                                                                                                @Override
                                                                                                public void onErrorResponse(VolleyError error) {

                                                                                                }
                                                                                            }
                                                                                    ){
                                                                                        @Override
                                                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                                                            Map<String, String> parametros= new HashMap<>();
                                                                                            parametros.put("correo", convertirCadena(textoCorreo));
                                                                                            parametros.put("publicados", convertirCadena(publicadosModificados));
                                                                                            return parametros;
                                                                                        }
                                                                                    };
                                                                                    stringRequestActualizarPublicados.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                                                                    requestQueue.add(stringRequestActualizarPublicados);
                                                                                }
                                                                            }
                                                                        },
                                                                        new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {

                                                                            }
                                                                        }
                                                                ){
                                                                    @Override
                                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                                        Map<String, String> parametros= new HashMap<>();
                                                                        parametros.put("publicacion",convertirCadena(String.valueOf(idPublicacion)));
                                                                        return parametros;
                                                                    }
                                                                };
                                                                stringRequestEliminarPublicacion.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                                                requestQueue.add(stringRequestEliminarPublicacion);
                                                                break;
                                                            }
                                                        }
                                                        //
                                                    }

                                                    @Override
                                                    public void onContactarButtonClickListener(View v, int idPublicacion) {
                                                        //Acá este metodo no debe hacer nada
                                                    }


                                                });

                                                recyclerView.setHasFixedSize(true);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerView.setAdapter(listAdapter);
                                                verificarActivarBotonCargarMas();
                                                for(int i=0; i<response.length(); i++){
                                                    new DownloadImageTask(i).execute(response.getJSONObject(i).getString("foto"));
                                                }

                                                //insertarBotonCargarMas();
                                                //listAdapter.notifyItemInserted(listadoPublicaciones.size()-1);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            publicaciones=0;
                                            recyclerView.setHasFixedSize(true);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                            recyclerView.setAdapter(listAdapter);
                                            verificarActivarBotonCargarMas();
                                        }
                                        cantidadResultados.setText(publicaciones+" "+getResources().getString(R.string.resultados));
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
                MiPerfilFragment miPerfilFragment= new MiPerfilFragment();
                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, miPerfilFragment);
                fragmentTransaction.commit();
            }
        });

        cargarMasResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Luchooo\tCargando más resultados");
                limit=listAdapter.getItemCount();
                String URL_OBTENER_PUBLICACIONES_SECUNDARIAS="https://udvivienda360.000webhostapp.com/obtener_publicaciones_por_id.php?id="+convertirCadena(textoPublicados)+"&limit="+limit+"&filtro="+convertirCadena(filtroSQL);
                JsonArrayRequest jsonArrayRequestSecundarias= new JsonArrayRequest(
                        Request.Method.GET,
                        URL_OBTENER_PUBLICACIONES_SECUNDARIAS,
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    System.out.println("Luchoooo\tPublicaciones adicionales:");
                                    for(int i=0; i<response.length(); i++){
                                        if(response.getJSONObject(i).getString("barrio").equals("final")){
                                            publicaciones=Integer.parseInt(response.getJSONObject(i).getString("conteo"));
                                        }
                                        else{
                                            System.out.println("Luchooo\tPublicacion # "+response.getJSONObject(i).getString("id")+", Barrio: "+response.getJSONObject(i).getString("barrio")+" Precio: "+response.getJSONObject(i).getString("precio"));
                                            listadoPublicaciones.add(new ListElement(response.getJSONObject(i).getString("barrio"),
                                                    response.getJSONObject(i).getString("precio"), response.getJSONObject(i).getString("areaconst"),
                                                    response.getJSONObject(i).getString("habitaciones"), response.getJSONObject(i).getString("banos"),
                                                    getResources().getDrawable(R.drawable.logo_app_transparente), response.getJSONObject(i).getString("id"),
                                                    response.getJSONObject(i).getString("oferta"), response.getJSONObject(i).getString("inmueble"), "propia"));
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

    private String convertirCadena(String text){
        String resultado="";
        for (int i=0; i<text.length(); i++){
            int ascii=(int) text.charAt(i);
            resultado=resultado+ascii+"-*-";
        }
        return resultado;
    }

    public void verificarActivarBotonCargarMas(){
        System.out.println("Luchooo\tAnalisis para activar boton cargas más");
        limit=listadoPublicaciones.size();
        if(limit<publicaciones){
            cargarMasResultados.setVisibility(View.VISIBLE);
            System.out.println("Luchooo\tActivó boton cargar más");
        }
        else{
            cargarMasResultados.setVisibility(View.GONE);
            System.out.println("Luchooo\tDesactivó boton cargar más");
        }
        System.out.println("Luchooo\tFinalizó revision. Total adapters: "+listadoPublicaciones.size());
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

}