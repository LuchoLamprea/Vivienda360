package com.lulapps.vivienda360;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.ar.sceneform.rendering.ModelRenderable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class PublicarInmuebleFragment extends Fragment implements OnMapReadyCallback {

    String pasoId, pasoInmueble, pasoOferta, pasoPrecio, pasoAreaconst, pasoHabitaciones, pasoBanos,
            pasoGaraje, pasoEstrato, pasoAntiguedad, pasoDescripcion, pasoDireccion, pasofotos, pasoModelo;

    String textoCorreo, textoPublicados, nombre_zona_direccion, localidad_zona_direccion;
    Button tipoVenta, tipoArriendo, tipoCasa, tipoApartamento, seleccionarFotos, limpiarFotos, seleccionarModelo, limpiarModelo, publicarInmueble, guardarEdicionesPublicacion;
    EditText precio, areaConstruida, habitaciones, banos, garajes, estrato, antiguedad, descripcion, direccion;
    TextView error;
    ConstraintLayout contenedorFotos, contenedorModelo;
    boolean price, builded, rooms, bath, garage, level, old, information, address, neighborhood, location, photos, model;
    boolean modoEdicion;
    boolean agregoFoto, agregoModelo;
    boolean ofertModified, typeModified;
    boolean actualizoInfo, actualizoMultimedia;
    int SELECCIONAR_IMAGENES_MULTIPLES=1, SELECCIONAR_MODELO_3D=2, numeroFotoActual, contadorFotosEnviadas;
    int numeroPublicacion;
    int contadorRespuestasFotos=0;
    double pesoModelo;
    ArrayList <Uri> direccionesImagenes;
    Uri direccionModelo;
    ImageView buscarDireccion, sinFoto, sinModelo, siguienteFoto, anteriorFoto, botonAtras;
    ImageSwitcher fotos,modelo;
    GoogleMap map;
    private ModelRenderable modelRenderable;
    RequestQueue requestQueue;

    public PublicarInmuebleFragment() {
        modoEdicion=false;
    }

    public PublicarInmuebleFragment(String mail, String published) {
        textoCorreo=mail;
        textoPublicados=published;
        modoEdicion=false;
    }

    public PublicarInmuebleFragment(String pasoId, String pasoInmueble, String pasoOferta, String pasoPrecio, String pasoAreaconst, String pasoHabitaciones, String pasoBanos, String pasoGaraje, String pasoEstrato, String pasoAntiguedad, String pasoDescripcion, String pasoDireccion, String pasofotos, String pasoModelo) {
        this.pasoId = pasoId;
        this.pasoInmueble = pasoInmueble;
        this.pasoOferta = pasoOferta;
        this.pasoPrecio = pasoPrecio;
        this.pasoAreaconst = pasoAreaconst;
        this.pasoHabitaciones = pasoHabitaciones;
        this.pasoBanos = pasoBanos;
        this.pasoGaraje = pasoGaraje;
        this.pasoEstrato = pasoEstrato;
        this.pasoAntiguedad = pasoAntiguedad;
        this.pasoDescripcion = pasoDescripcion;
        this.pasoDireccion = pasoDireccion;
        this.pasofotos = pasofotos;
        this.pasoModelo = pasoModelo;
        modoEdicion=true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        direccionesImagenes= new ArrayList<>();
        requestQueue= Volley.newRequestQueue(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publicar_inmueble, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        price= builded= rooms= bath= garage= level= old= information= address=model=photos=agregoFoto
                =agregoModelo=actualizoInfo=actualizoMultimedia=ofertModified=typeModified=false;

        contadorFotosEnviadas=0;

        tipoVenta= view.findViewById(R.id.buttonVenta);
        tipoArriendo= view.findViewById(R.id.buttonArriendo);
        tipoCasa= view.findViewById(R.id.buttonCasa);
        tipoApartamento= view.findViewById(R.id.buttonApartamento);
        precio= view.findViewById(R.id.editTextPrecio);
        areaConstruida= view.findViewById(R.id.editTextAreaConstruida);
        habitaciones= view.findViewById(R.id.editTextCantidadHabitaciones);
        banos= view.findViewById(R.id.editTextCantidadBanos);
        garajes= view.findViewById(R.id.editTextCantidadGarajes);
        estrato= view.findViewById(R.id.editTextEstrato);
        antiguedad= view.findViewById(R.id.editTextAntiguedad);
        descripcion= view.findViewById(R.id.editTextDescripcion);
        direccion= view.findViewById(R.id.editTextDireccion);
        buscarDireccion= view.findViewById(R.id.imageViewIconoBuscarDireccion);
        seleccionarFotos= view.findViewById(R.id.buttonSeleccionarFotos);
        limpiarFotos= view.findViewById(R.id.buttonLimpiarFotos);
        contenedorFotos= view.findViewById(R.id.constraintLayoutContenedorFotos);
        sinFoto= view.findViewById(R.id.imageViewSinFotos);
        fotos= view.findViewById(R.id.imageSwitcherFotos);
        anteriorFoto= view.findViewById(R.id.imageViewFotoAnterior);
        siguienteFoto= view.findViewById(R.id.imageViewFotoSiguiente);
        seleccionarModelo= view.findViewById(R.id.buttonSeleccionarModelo);
        limpiarModelo= view.findViewById(R.id.buttonLimpiarModelo);
        contenedorModelo= view.findViewById(R.id.constraintLayoutContenedorModelo);
        modelo= view.findViewById(R.id.imageSwitcherModelo);
        sinModelo= view.findViewById(R.id.imageViewSinModelo);
        publicarInmueble = view.findViewById(R.id.buttonPublicar);
        guardarEdicionesPublicacion= view.findViewById(R.id.buttonGuardarCambiosPublicacion);
        error= view.findViewById(R.id.textViewErrorPublicar);
        botonAtras= view.findViewById(R.id.imageViewButtonAtrasPublicarInmueble);

        anteriorFoto.setVisibility(View.GONE);
        siguienteFoto.setVisibility(View.GONE);

        fotos.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView= new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });

        modelo.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView= new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });

        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                MisPublicacionesFragment misPublicacionesFragment= new MisPublicacionesFragment();
                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, misPublicacionesFragment);
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

        publicarInmueble.setEnabled(false);
        publicarInmueble.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));

        if(modoEdicion){
            if(pasoOferta.equals("VENTA")){
                tipoVenta.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                tipoArriendo.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
            }
            else{
                tipoArriendo.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                tipoVenta.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
            }

            if(pasoInmueble.equals("CASA")){
                tipoCasa.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                tipoApartamento.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
            }
            else{
                tipoApartamento.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                tipoCasa.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
            }

            NumberFormat numberFormat= NumberFormat.getCurrencyInstance();
            numberFormat.setMaximumFractionDigits(0);
            String precioFormateado=numberFormat.format(Double.parseDouble(pasoPrecio.toString().replaceAll("[$.,]","")));
            precio.setText(precioFormateado);


            areaConstruida.setText(pasoAreaconst);
            habitaciones.setText(pasoHabitaciones);
            banos.setText(pasoBanos);
            garajes.setText(pasoGaraje);
            estrato.setText(pasoEstrato);
            antiguedad.setText(pasoAntiguedad);
            descripcion.setText(pasoDescripcion);
            direccion.setText(pasoDireccion);
            System.out.println("Luchoooooo linkFotos: "+pasofotos);
            new DownloadImageTask(fotos).execute(pasofotos);
            fotos.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));
            System.out.println("Luchooooo\tValor de modelo: "+pasoModelo);
            if(!pasoModelo.equals("NULL") && !pasoModelo.equals("null") && !pasoModelo.equals("")){
                modelo.setImageDrawable(getResources().getDrawable(R.drawable.modelo_3d_imagen));
                modelo.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));
                contenedorModelo.setBackground(null);
                modelo.setBackgroundColor(getResources().getColor(R.color.fondo_edittext));
                model=true;
            }
            publicarInmueble.setVisibility(View.GONE);

            guardarEdicionesPublicacion.setVisibility(View.VISIBLE);
            guardarEdicionesPublicacion.setEnabled(false);
            guardarEdicionesPublicacion.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));


            price=true;
            builded=true;
            rooms=true;
            bath=true;
            garage=true;
            level=true;
            old=true;
            information=true;
            photos=true;
        }
        else{
            publicarInmueble.setVisibility(View.VISIBLE);
            guardarEdicionesPublicacion.setVisibility(View.GONE);
        }

        tipoVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoEdicion){
                    ofertModified= !pasoOferta.equals("VENTA");
                    verificarActivarPublicar();
                }
                tipoVenta.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                tipoArriendo.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
            }
        });

        tipoArriendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoEdicion){
                    ofertModified= !pasoOferta.equals("ARRIENDO");
                    verificarActivarPublicar();
                }
                tipoArriendo.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                tipoVenta.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
            }
        });

        tipoCasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoEdicion){
                    typeModified= !pasoInmueble.equals("CASA");
                    verificarActivarPublicar();
                }
                tipoCasa.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                tipoApartamento.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
            }
        });

        tipoApartamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoEdicion){
                    typeModified= !pasoInmueble.equals("APARTAMENTO");
                    verificarActivarPublicar();
                }
                tipoApartamento.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                tipoCasa.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
            }
        });

        precio.addTextChangedListener(new TextWatcher() {
            String textoPrevio="";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textoPrevio=textoPrevio+s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //System.out.println("Luchooo\tDatos del cambio\ts: "+s.toString()+"\tStart: "+start+"\tbefore: "+before+"\tcount: "+count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    try{
                        NumberFormat numberFormat= NumberFormat.getCurrencyInstance();
                        numberFormat.setMaximumFractionDigits(0);
                        String precioFormateado;
                        //System.out.println("Luchooo\tValor Inicial: "+s.toString());
                        if(s.toString().equals("$") || s.toString().isEmpty()){
                            precioFormateado=numberFormat.format(0);
                        }
                        else{
                            precioFormateado=numberFormat.format(Double.parseDouble(s.toString().replaceAll("[$.,]","")));
                        }
                        //System.out.println("Luchooo\tValor formateado: "+precioFormateado+"\t Diferencias: "+(precioFormateado.length()-s.toString().length()));
                        int valorCursor= precio.getSelectionStart();
                        if((precioFormateado.length()-s.toString().length())==1){
                            valorCursor++;
                        }
                        /*
                        if(textoPrevio.length()-1>=5){
                            if((((valorCursor-1)-((textoPrevio.length()-8)%4))%4)==0){
                                System.out.println("Luchooo\tOmite borrado y corre cursor a la izquierda");
                                valorCursor--;
                            }
                        }

                        */

                        textoPrevio="";
                        //System.out.println("Luchooo\tPosicion cursor: "+valorCursor);
                        precio.removeTextChangedListener(this);
                        precio.setText(precioFormateado);
                        precio.addTextChangedListener(this);
                        try {
                            if(valorCursor!=1){
                                precio.setSelection(valorCursor);
                            }
                            else{
                                precio.setSelection(precio.length());
                            }
                        }
                        catch (IndexOutOfBoundsException e){
                            precio.setSelection(precio.length());
                        }
                        price=true;
                    }
                    catch (NumberFormatException e){
                        System.out.println("Luchooo error+\t"+e.getMessage().toString());
                        e.printStackTrace();
                        price=false;
                        error.setText(getResources().getString(R.string.error_precio_no_valido));
                    }
                }
                else{
                    price=false;
                }
                verificarActivarPublicar();
            }
        });

        areaConstruida.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>0){
                    try{
                        Double.parseDouble(s.toString());
                        builded=true;
                    }
                    catch (NumberFormatException e){
                        builded=false;
                        error.setText(getResources().getString(R.string.error_area_no_valida));
                    }
                }
                else{
                    builded=false;
                }
                verificarActivarPublicar();
            }
        });

        habitaciones.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>0){
                    try{
                        Integer.parseInt(s.toString());
                        rooms=true;
                    }
                    catch (NumberFormatException e){
                        rooms=false;
                        error.setText(getResources().getString(R.string.error_habitaciones_no_valida));
                    }
                }
                else{
                    rooms=false;
                }
                verificarActivarPublicar();
            }
        });

        banos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>0){
                    try{
                        Integer.parseInt(s.toString());
                        bath=true;
                    }
                    catch (NumberFormatException e){
                        bath=false;
                        error.setText(getResources().getString(R.string.error_banos_no_valido));
                    }
                }
                else{
                    bath=false;
                }
                verificarActivarPublicar();
            }
        });

        garajes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>0){
                    try{
                        Integer.parseInt(s.toString());
                        garage=true;
                    }
                    catch (NumberFormatException e){
                        garage=false;
                        error.setText(getResources().getString(R.string.error_garaje_no_valido));
                    }
                }
                else{
                    garage=false;
                }
                verificarActivarPublicar();
            }
        });

        estrato.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>0){
                    try{
                        Integer.parseInt(s.toString());
                        level=true;
                    }
                    catch (NumberFormatException e){
                        level=false;
                    }
                }
                else{
                    level=false;
                }
                verificarActivarPublicar();
            }
        });

        antiguedad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>0){
                    try{
                        int antiguedad_paso;
                        antiguedad_paso=Integer.parseInt(s.toString());
                        if(antiguedad_paso>=0){
                            old=true;
                        }
                        else{
                            old=false;
                            error.setText(getResources().getString(R.string.error_antiguedad_no_valida));
                        }
                        old=true;
                    }
                    catch (NumberFormatException e){
                        old=false;
                        error.setText(getResources().getString(R.string.error_antiguedad_no_valida));
                    }
                }
                else{
                    old=false;
                }
                verificarActivarPublicar();
            }
        });

        descripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    information=true;
                }
                else{
                    information=false;
                }
                verificarActivarPublicar();
            }
        });

        direccion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    buscarDireccion.setEnabled(true);
                }
                else{
                    buscarDireccion.setEnabled(false);
                }
            }
        });

        buscarDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                System.out.println("Luchoooooo\nDireccion: "+direccion.getText().toString());
                if (!direccion.getText().toString().isEmpty() && direccion.getText().toString()!=null){
                    Geocoder geocoder= new Geocoder(getContext());
                    List<Address> addresses;
                    try {
                        addresses= geocoder.getFromLocationName(direccion.getText().toString(), 1, 3.7306725330000701, -74.4507241630000038, 4.8368293580000499, -73.9890180820000012);
                        if (addresses==null || addresses.size()==0){
                            address=false;
                            System.out.println("Luchooooooo\nNo se encontró la direccion con geocoder, direccion: "+direccion.getText().toString());
                            Toast.makeText(getContext(),R.string.error_direccion_no_encontrada,Toast.LENGTH_SHORT).show();
                        }
                        else{
                            System.out.println("Luchoooooo pasó a buscar barrio y localidad\nUbicacion: "+addresses.get(0).getLatitude()+", "+addresses.get(0).getLongitude());
                            LatLng ubicacionEncontrada= new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                            String ubicacionConvertida=convertirCadena(""+ubicacionEncontrada.longitude+" "+ubicacionEncontrada.latitude);
                            String URL_UBICACION= "https://udvivienda360.000webhostapp.com/obtener_barrio_localidad.php?ubicacion="+ubicacionConvertida;
                            JsonObjectRequest jsonObjectRequest_ubicacion= new JsonObjectRequest(
                                    Request.Method.GET,
                                    URL_UBICACION,
                                    null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            System.out.println("Luchooooo \tRespondio: "+response.toString());
                                            String nombre_zona, localidad_zona;
                                            try {
                                                nombre_zona= response.getString("nombre");
                                                localidad_zona= response.getString("localidad");
                                                nombre_zona_direccion=nombre_zona;
                                                localidad_zona_direccion=localidad_zona;
                                                System.out.println("Luchoooooo: \tNombre: "+nombre_zona+"\tLocalidad: "+localidad_zona);
                                                neighborhood=true;
                                                location=true;
                                                verificarActivarPublicar();
                                            } catch (JSONException e) {
                                                neighborhood=false;
                                                location=false;
                                                Toast.makeText(getContext(),R.string.error_barrio_localidad,Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            neighborhood=false;
                                            location=false;
                                            Toast.makeText(getContext(),R.string.error_barrio_localidad,Toast.LENGTH_SHORT).show();
                                            System.out.println("Luchoooooo, Error:\n"+error.toString()+"\n"+error.getMessage());
                                        }
                                    }
                            );
                            jsonObjectRequest_ubicacion.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                            requestQueue.add(jsonObjectRequest_ubicacion);
                            map.clear();
                            map.addMarker(new MarkerOptions()
                                    .position(ubicacionEncontrada)
                                    .title(addresses.get(0).getAddressLine(0)));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    ubicacionEncontrada,15));
                            address=true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        address=false;
                    }
                }
                verificarActivarPublicar();
            }
        });

        if(modoEdicion){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    buscarDireccion.callOnClick();
                }
            },2000);
        }

        seleccionarFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, (getResources().getString(R.string.selecciona_las_fotos))),SELECCIONAR_IMAGENES_MULTIPLES);
            }
        });

        anteriorFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotos.setBackgroundColor(getResources().getColor(R.color.fondo_edittext));
                try{
                    numeroFotoActual=numeroFotoActual-1;
                    fotos.setImageURI(direccionesImagenes.get(numeroFotoActual));

                }
                catch (IndexOutOfBoundsException e){
                    numeroFotoActual=direccionesImagenes.size()-1;
                    fotos.setImageURI(direccionesImagenes.get(numeroFotoActual));
                }
            }
        });

        siguienteFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotos.setBackgroundColor(getResources().getColor(R.color.fondo_edittext));
                try{
                    numeroFotoActual=numeroFotoActual+1;
                    fotos.setImageURI(direccionesImagenes.get(numeroFotoActual));
                }
                catch (IndexOutOfBoundsException e){
                    numeroFotoActual=0;
                    fotos.setImageURI(direccionesImagenes.get(numeroFotoActual));
                }
            }
        });

        limpiarFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numeroFotoActual=-1;
                direccionesImagenes=new ArrayList<>();
                fotos.setLayoutParams(new ConstraintLayout.LayoutParams(0,0));
                contenedorFotos.setBackground(getResources().getDrawable(R.drawable.borde_sin_relleno));
                anteriorFoto.setVisibility(View.GONE);
                siguienteFoto.setVisibility(View.GONE);
                photos=false;
            }
        });

        seleccionarModelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setType("application/*");
                //intent.setType("model/gltf-binary");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, (getResources().getString(R.string.selecciona_el_modelo))),SELECCIONAR_MODELO_3D);
            }
        });

        limpiarModelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                direccionModelo=null;
                modelo.setLayoutParams(new ConstraintLayout.LayoutParams(0,0));
                contenedorModelo.setBackground(getResources().getDrawable(R.drawable.borde_sin_relleno));
                model=false;
                verificarActivarGuardar();
            }
        });

        publicarInmueble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(estrato.getText().toString())>0 && Integer.parseInt(estrato.getText().toString())<=6){
                    ArrayList<Bitmap> decodeds= new ArrayList<>();

                    String tipoOferta="";
                    String tipoInmueble="";

                    if(tipoVenta.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState())){
                        tipoOferta="VENTA";
                    }
                    else{
                        tipoOferta="ARRIENDO";
                    }

                    if(tipoCasa.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState())){
                        tipoInmueble="CASA";
                    }
                    else{
                        tipoInmueble="APARTAMENTO";
                    }

                    String tipoOfertaFinal=tipoOferta;
                    String tipoInmuebleFinal=tipoInmueble;
                    String precioFinal, areaconstFinal, habitacionesFinal, banosFinal, garajeFinal, estratoFinal,
                            antiguedadFinal, descripcionFinal, direccionFinal, barrioFinal, localidadFinal;
                    precioFinal=precio.getText().toString().replaceAll("[$.,]","");
                    areaconstFinal=areaConstruida.getText().toString();
                    habitacionesFinal=habitaciones.getText().toString();
                    banosFinal=banos.getText().toString();
                    garajeFinal=garajes.getText().toString();
                    estratoFinal=estrato.getText().toString();
                    antiguedadFinal=antiguedad.getText().toString();
                    descripcionFinal=descripcion.getText().toString();
                    direccionFinal=direccion.getText().toString();
                    barrioFinal=nombre_zona_direccion;
                    localidadFinal=localidad_zona_direccion;

                    String URL_PUBLICAR="https://udvivienda360.000webhostapp.com/registrar_publicacion.php";
                    StringRequest stringRequest= new StringRequest(
                            Request.Method.POST,
                            URL_PUBLICAR,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try{
                                        numeroPublicacion=Integer.parseInt(response);
                                        System.out.println("Luchooooooo exito realizando la publicacion numero "+numeroPublicacion);

                                        if(textoPublicados.equals("null")){
                                            textoPublicados=""+numeroPublicacion;
                                        }
                                        else{
                                            textoPublicados=textoPublicados+" "+numeroPublicacion;
                                        }

                                        System.out.println("Luchooooo \tcorreo: "+textoCorreo+"\tPublicados: "+textoPublicados);
                                        String URL_ACTUALIZAR_PUBLICADOS="https://udvivienda360.000webhostapp.com/actualizar_publicados.php";
                                        StringRequest stringRequestActualizarPublicados= new StringRequest(
                                                Request.Method.POST,
                                                URL_ACTUALIZAR_PUBLICADOS,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        System.out.println("Luchooooooo resultado= "+response);
                                                        if(response.equals("OK")){
                                                            System.out.println("Luchooooooo exito actualizando el usuario con sus publicados ");

                                                            for (int i=0; i<direccionesImagenes.size(); i++){
                                                                InputStream is= null;
                                                                try {
                                                                    ByteArrayOutputStream bytes= new ByteArrayOutputStream();
                                                                    is = getContext().getContentResolver().openInputStream(direccionesImagenes.get(i));
                                                                    Bitmap bitmap_paso=BitmapFactory.decodeStream(is);
                                                                    Bitmap bitmap_paso2= escalarBitmap(bitmap_paso);
                                                                    bitmap_paso2.compress(Bitmap.CompressFormat.JPEG,80,bytes);
                                                                    decodeds.add(BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray())));
                                                                } catch (FileNotFoundException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                            contadorRespuestasFotos=0;
                                                            for (int k=0; k<decodeds.size(); k++){
                                                                publicarFoto(numeroPublicacion, decodeds.get(k));
                                                            }

                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        System.out.println("Luchoooooo error actualizando el usuario y sus publicados\n"+error.toString()+"\n"+error.getMessage());
                                                    }
                                                }
                                        ){
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> parametros= new HashMap<>();
                                                parametros.put("correo",convertirCadena(textoCorreo));
                                                parametros.put("publicados", convertirCadena(textoPublicados));
                                                return parametros;
                                            }
                                        };
                                        stringRequestActualizarPublicados.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                        requestQueue.add(stringRequestActualizarPublicados);
                                    }
                                    catch (NumberFormatException e){
                                        Toast.makeText(getContext(),"Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.println("Luchoooooo error realizando la publicacion, no se logró registrar\n"+error.toString()+"\n"+error.getMessage());
                                }
                            }
                    ){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parametros= new HashMap<>();
                            parametros.put("propietario",convertirCadena(textoCorreo));
                            parametros.put("oferta", convertirCadena(tipoOfertaFinal));
                            parametros.put("inmueble", convertirCadena(tipoInmuebleFinal));
                            parametros.put("precio", convertirCadena(precioFinal));
                            parametros.put("areaconst", convertirCadena(areaconstFinal));
                            parametros.put("habitaciones", convertirCadena(habitacionesFinal));
                            parametros.put("banos", convertirCadena(banosFinal));
                            parametros.put("garajes", convertirCadena(garajeFinal));
                            parametros.put("estrato", convertirCadena(estratoFinal));
                            parametros.put("antiguedad", convertirCadena(antiguedadFinal));
                            parametros.put("descripcion", convertirCadena(descripcionFinal));
                            parametros.put("direccion", convertirCadena(direccionFinal));
                            parametros.put("barrio", convertirCadena(barrioFinal));
                            parametros.put("localidad", convertirCadena(localidadFinal));
                            return parametros;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                    requestQueue.add(stringRequest);
                }
                else{
                    Toast.makeText(getContext(),R.string.estrato_fuera_de_rango, Toast.LENGTH_SHORT).show();
                }
            }
        });

        guardarEdicionesPublicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(estrato.getText().toString())>0 && Integer.parseInt(estrato.getText().toString())<=6){
                    if(actualizoInfo){
                        String tipoOferta="";
                        String tipoInmueble="";

                        if(tipoVenta.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState())){
                            tipoOferta="VENTA";
                        }
                        else{
                            tipoOferta="ARRIENDO";
                        }

                        if(tipoCasa.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState())){
                            tipoInmueble="CASA";
                        }
                        else{
                            tipoInmueble="APARTAMENTO";
                        }

                        String tipoOfertaFinal=tipoOferta;
                        String tipoInmuebleFinal=tipoInmueble;
                        String precioFinal, areaconstFinal, habitacionesFinal, banosFinal, garajeFinal, estratoFinal,
                                antiguedadFinal, descripcionFinal, direccionFinal, barrioFinal, localidadFinal;
                        precioFinal=precio.getText().toString();
                        areaconstFinal=areaConstruida.getText().toString();
                        habitacionesFinal=habitaciones.getText().toString();
                        banosFinal=banos.getText().toString();
                        garajeFinal=garajes.getText().toString();
                        estratoFinal=estrato.getText().toString();
                        antiguedadFinal=antiguedad.getText().toString();
                        descripcionFinal=descripcion.getText().toString();
                        direccionFinal=direccion.getText().toString();
                        barrioFinal=nombre_zona_direccion;
                        localidadFinal=nombre_zona_direccion;

                        String URL_ACTUALIZAR_INFO="https://udvivienda360.000webhostapp.com/actualizar_publicacion.php";
                        StringRequest stringRequestActualizarInfo= new StringRequest(
                                Request.Method.POST,
                                URL_ACTUALIZAR_INFO,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if(response.equals("OK")){
                                            if(!actualizoMultimedia){
                                                Toast.makeText(getContext(), R.string.publicacion_actualizada, Toast.LENGTH_SHORT).show();
                                                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                                MisPublicacionesFragment misPublicacionesFragment= new MisPublicacionesFragment();
                                                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, misPublicacionesFragment);
                                                fragmentTransaction.commit();
                                            }
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
                                parametros.put("id",convertirCadena(pasoId));
                                parametros.put("oferta", convertirCadena(tipoOfertaFinal));
                                parametros.put("inmueble", convertirCadena(tipoInmuebleFinal));
                                parametros.put("precio", convertirCadena(precioFinal));
                                parametros.put("areaconst", convertirCadena(areaconstFinal));
                                parametros.put("habitaciones", convertirCadena(habitacionesFinal));
                                parametros.put("banos", convertirCadena(banosFinal));
                                parametros.put("garajes", convertirCadena(garajeFinal));
                                parametros.put("estrato", convertirCadena(estratoFinal));
                                parametros.put("antiguedad", convertirCadena(antiguedadFinal));
                                parametros.put("descripcion", convertirCadena(descripcionFinal));
                                parametros.put("direccion", convertirCadena(direccionFinal));
                                parametros.put("barrio", convertirCadena(barrioFinal));
                                parametros.put("localidad", convertirCadena(localidadFinal));
                                return parametros;
                            }
                        };
                        stringRequestActualizarInfo.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                        requestQueue.add(stringRequestActualizarInfo);
                    }
                    if(actualizoMultimedia){
                        System.out.println("Luchooooo inicia a actualizar multimedia");
                        if(agregoFoto){
                            ArrayList<Bitmap> decodeds= new ArrayList<>();
                            for (int i=0; i<direccionesImagenes.size(); i++){
                                InputStream is= null;
                                try {
                                    ByteArrayOutputStream bytes= new ByteArrayOutputStream();
                                    is = getContext().getContentResolver().openInputStream(direccionesImagenes.get(i));
                                    Bitmap bitmap_paso=BitmapFactory.decodeStream(is);
                                    Bitmap bitmap_paso2= escalarBitmap(bitmap_paso);
                                    bitmap_paso2.compress(Bitmap.CompressFormat.JPEG,80,bytes);
                                    decodeds.add(BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray())));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

                            String URL_ELIMINAR_FOTOS="https://udvivienda360.000webhostapp.com/eliminar_fotos_publicacion.php";
                            StringRequest stringRequestEliminarFotos = new StringRequest(
                                    Request.Method.POST,
                                    URL_ELIMINAR_FOTOS,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            System.out.println("Luchooooo hubo response de eliminar fotos, valor: "+response);
                                            if(response.equals("OK")){
                                                System.out.println("Luchooooo fotos seleccionadas: "+decodeds.size());
                                                contadorRespuestasFotos=0;
                                                for (int k=0; k<decodeds.size(); k++){
                                                    publicarFoto(Integer.parseInt(pasoId), decodeds.get(k));
                                                }
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            System.out.println("Luchoooooo... error volley\n"+error.toString()+"\n"
                                                    +error.getMessage()+"\n"+error.getLocalizedMessage()+"\n");
                                        }
                                    }
                            ){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> parametros= new HashMap<>();
                                    parametros.put("publicacion",convertirCadena(pasoId));
                                    return parametros;
                                }
                            };
                            stringRequestEliminarFotos.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                            requestQueue.add(stringRequestEliminarFotos);
                        }
                        else{
                            if(agregoModelo){
                                if(!pasoModelo.equals("NULL") && !pasoModelo.equals("null") && !pasoModelo.equals("")){
                                    String URL_ELIMINAR_MODELO="https://udvivienda360.000webhostapp.com/eliminar_modelo_publicacion.php";
                                    StringRequest stringRequestEliminarModelo = new StringRequest(
                                            Request.Method.POST,
                                            URL_ELIMINAR_MODELO,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    System.out.println("Luchooooo hubo response de eliminar modelo, valor: "+response);
                                                    if(response.equals("OK")){
                                                        String modeloCodificado="";
                                                        InputStream is2= null;
                                                        try {
                                                            is2 = getContext().getContentResolver().openInputStream(direccionModelo);
                                                            byte [] modelBytes= new byte[(int) (pesoModelo)];
                                                            is2.read(modelBytes);
                                                            is2.close();
                                                            modeloCodificado= Base64.encodeToString(modelBytes, Base64.DEFAULT);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }

                                                        String modeloCodificadoFinal=modeloCodificado;
                                                        String URL_SUBIR_MODELO="https://udvivienda360.000webhostapp.com/registrar_modelo.php";
                                                        StringRequest stringRequestModelo= new StringRequest(
                                                                Request.Method.POST,
                                                                URL_SUBIR_MODELO,
                                                                new Response.Listener<String>() {
                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                        System.out.println("Luchooooo\tResponse modelo: "+response);
                                                                        if(response.equals("OK")){
                                                                            Toast.makeText(getContext(), R.string.publicacion_actualizada, Toast.LENGTH_SHORT).show();
                                                                            FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                                                            MisPublicacionesFragment misPublicacionesFragment= new MisPublicacionesFragment();
                                                                            fragmentTransaction.replace(R.id.contenedor_ppal_perfil, misPublicacionesFragment);
                                                                            fragmentTransaction.commit();
                                                                        }
                                                                    }
                                                                },
                                                                new Response.ErrorListener() {
                                                                    @Override
                                                                    public void onErrorResponse(VolleyError error) {
                                                                        System.out.println("Luchoooo\tError linea 1420 de Publicar:\t"+error.getMessage());
                                                                    }
                                                                }
                                                        ){
                                                            @Override
                                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                                Map<String, String> parametros= new HashMap<>();
                                                                parametros.put("publicacion",String.valueOf(pasoId));
                                                                parametros.put("modelo", modeloCodificadoFinal);
                                                                return parametros;
                                                            }
                                                        };
                                                        stringRequestModelo.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                                        requestQueue.add(stringRequestModelo);
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    System.out.println("Luchoooooo... error volley\n"+error.toString()+"\n"
                                                            +error.getMessage()+"\n"+error.getLocalizedMessage()+"\n");
                                                }
                                            }
                                    ){
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> parametros= new HashMap<>();
                                            parametros.put("publicacion",convertirCadena(pasoId));
                                            return parametros;
                                        }
                                    };
                                    stringRequestEliminarModelo.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                    requestQueue.add(stringRequestEliminarModelo);
                                }
                                else{
                                    String modeloCodificado="";
                                    InputStream is2= null;
                                    try {
                                        is2 = getContext().getContentResolver().openInputStream(direccionModelo);
                                        byte [] modelBytes= new byte[(int) (pesoModelo)];
                                        is2.read(modelBytes);
                                        is2.close();
                                        modeloCodificado= Base64.encodeToString(modelBytes, Base64.DEFAULT);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    String modeloCodificadoFinal=modeloCodificado;
                                    String URL_SUBIR_MODELO="https://udvivienda360.000webhostapp.com/registrar_modelo.php";
                                    StringRequest stringRequestModelo= new StringRequest(
                                            Request.Method.POST,
                                            URL_SUBIR_MODELO,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    System.out.println("Luchooooo\tResponse modelo: "+response);
                                                    if(response.equals("OK")){
                                                        Toast.makeText(getContext(), R.string.publicacion_actualizada, Toast.LENGTH_SHORT).show();
                                                        FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                                        MisPublicacionesFragment misPublicacionesFragment= new MisPublicacionesFragment();
                                                        fragmentTransaction.replace(R.id.contenedor_ppal_perfil, misPublicacionesFragment);
                                                        fragmentTransaction.commit();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    System.out.println("Luchoooo\tError linea 1420 de Publicar:\t"+error.getMessage());
                                                }
                                            }
                                    ){
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> parametros= new HashMap<>();
                                            parametros.put("publicacion",String.valueOf(pasoId));
                                            parametros.put("modelo", modeloCodificadoFinal);
                                            return parametros;
                                        }
                                    };
                                    stringRequestModelo.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                    requestQueue.add(stringRequestModelo);
                                }
                            }
                        }
                    }
                    else{
                        if(!pasoModelo.equals("NULL") && !pasoModelo.equals("null") && !pasoModelo.equals("") && !model){
                            String URL_ELIMINAR_MODELO="https://udvivienda360.000webhostapp.com/eliminar_modelo_publicacion.php";
                            StringRequest stringRequestEliminarModelo = new StringRequest(
                                    Request.Method.POST,
                                    URL_ELIMINAR_MODELO,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            System.out.println("Luchooooo hubo response de eliminar modelo sin agregar otro, valor: "+response);
                                            if(response.equals("OK")){
                                                Toast.makeText(getContext(), R.string.publicacion_actualizada, Toast.LENGTH_SHORT).show();
                                                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                                MisPublicacionesFragment misPublicacionesFragment= new MisPublicacionesFragment();
                                                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, misPublicacionesFragment);
                                                fragmentTransaction.commit();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            System.out.println("Luchoooooo... error volley\n"+error.toString()+"\n"
                                                    +error.getMessage()+"\n"+error.getLocalizedMessage()+"\n");
                                        }
                                    }
                            ){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> parametros= new HashMap<>();
                                    parametros.put("publicacion",convertirCadena(pasoId));
                                    return parametros;
                                }
                            };
                            stringRequestEliminarModelo.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                            requestQueue.add(stringRequestEliminarModelo);
                        }
                    }
                }
                else{
                    Toast.makeText(getContext(),R.string.estrato_fuera_de_rango, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECCIONAR_IMAGENES_MULTIPLES){
            if(resultCode== Activity.RESULT_OK && data!=null){
                direccionesImagenes= new ArrayList<>();
                if(data.getClipData()!=null){
                    if(data.getClipData().getItemCount()<=15){
                        for(int i=0; i<data.getClipData().getItemCount(); i++){
                            direccionesImagenes.add(data.getClipData().getItemAt(i).getUri());
                        }
                        fotos.setImageURI(direccionesImagenes.get(0));
                        photos=true;
                        agregoFoto=true;
                        anteriorFoto.setVisibility(View.VISIBLE);
                        siguienteFoto.setVisibility(View.VISIBLE);
                    }
                    else{
                        photos=false;
                        anteriorFoto.setVisibility(View.GONE);
                        siguienteFoto.setVisibility(View.GONE);
                        Toast.makeText(getContext(),R.string.error_mas_15_fotos, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    direccionesImagenes.add(data.getData());
                    fotos.setImageURI(direccionesImagenes.get(0));
                    photos=true;
                    agregoFoto=true;
                    anteriorFoto.setVisibility(View.GONE);
                    siguienteFoto.setVisibility(View.GONE);
                }
                numeroFotoActual=0;
                fotos.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));
                contenedorFotos.setBackground(null);
                fotos.setBackgroundColor(getResources().getColor(R.color.fondo_edittext));
            }
            else{
                Toast.makeText(getContext(),R.string.no_has_seleccionado_fotos, Toast.LENGTH_SHORT).show();
                if(direccionesImagenes.size()>0){
                    photos=true;
                }
                else{
                    photos=false;
                    anteriorFoto.setVisibility(View.GONE);
                    siguienteFoto.setVisibility(View.GONE);
                }
            }
            verificarActivarPublicar();
        }

        if(requestCode==SELECCIONAR_MODELO_3D){
            if(resultCode== Activity.RESULT_OK && data!=null){
                direccionModelo= data.getData();
                String displayName="";
                pesoModelo=0;
                Cursor cursorModelo3D = getActivity().getContentResolver().query(direccionModelo, null, null, null, null, null);
                if (cursorModelo3D != null && cursorModelo3D.moveToFirst()) {
                    int IndiceCursor = cursorModelo3D.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if(IndiceCursor>=0){
                        displayName = cursorModelo3D.getString(IndiceCursor);
                    }

                    int sizeIndex = cursorModelo3D.getColumnIndex(OpenableColumns.SIZE);
                    String size = null;
                    if (!cursorModelo3D.isNull(sizeIndex)) {
                        size = cursorModelo3D.getString(sizeIndex);
                    } else {
                        size = "Unknown";
                    }
                    pesoModelo=Double.parseDouble(size);
                    System.out.println("Luchoooo\tEl modelo seleccionado pesa "+size);
                }
                cursorModelo3D.close();

                if(displayName.endsWith(".glb")){
                    modelo.setImageDrawable(getResources().getDrawable(R.drawable.modelo_3d_imagen));
                    modelo.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));
                    contenedorModelo.setBackground(null);
                    modelo.setBackgroundColor(getResources().getColor(R.color.fondo_edittext));
                    model=true;
                    agregoModelo=true;
                    Toast.makeText(getContext(),R.string.modelo_agregado, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(),R.string.selecciona_glb, Toast.LENGTH_SHORT).show();
                    model=false;
                }
            }
            else{
                Toast.makeText(getContext(),R.string.no_has_seleccionado_modelo, Toast.LENGTH_SHORT).show();
                if(direccionModelo!=null){
                    model=true;
                }
                else{
                    model=false;
                }
            }
            verificarActivarPublicar();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map= googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    private void verificarActivarPublicar() {
        System.out.println("Luchooooo: Resultados:\nPrecio: "+price+"\nArea: "+builded+"\nHabitaciones: "+rooms+
                "\nBaños: "+bath+"\nGarajes: "+garage+"\nEstrato: "+level+"\nDescripcion: "+information
                +"\nDireccion: "+address+"\nFotos: "+photos+"\nBarrio: "+neighborhood+"\nLocalidad: "+location);
        if(price && builded && rooms && bath && garage && level && old && information && address && photos && neighborhood && location){
            publicarInmueble.setEnabled(true);
            publicarInmueble.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
            if(modoEdicion){
                verificarActivarGuardar();
            }
        }
        else{
            publicarInmueble.setEnabled(false);
            publicarInmueble.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
        }
        error.setText("");
    }

    private void verificarActivarGuardar(){
        String tipoOferta="";
        String tipoInmueble="";

        if(tipoVenta.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState())){
            tipoOferta="VENTA";
        }
        else{
            tipoOferta="ARRIENDO";
        }

        if(tipoCasa.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState())){
            tipoInmueble="CASA";
        }
        else{
            tipoInmueble="APARTAMENTO";
        }

        if(!tipoInmueble.equals(pasoInmueble) || !tipoOferta.equals(pasoOferta) || !precio.getText().toString().replaceAll("[$,.]","").equals(pasoPrecio) || !areaConstruida.getText().toString().equals(pasoAreaconst) || !habitaciones.getText().toString().equals(pasoHabitaciones) || !banos.getText().toString().equals(pasoBanos) || !garajes.getText().toString().equals(pasoGaraje) || !estrato.getText().toString().equals(pasoEstrato) || !antiguedad.getText().toString().equals(pasoAntiguedad) || !descripcion.getText().toString().equals(pasoDescripcion)  || !direccion.getText().toString().equals(pasoDireccion) || ofertModified || typeModified){
            actualizoInfo=true;
        }
        else{
            actualizoInfo=false;
        }
        if(agregoFoto || agregoModelo){
            actualizoMultimedia=true;
        }
        else{
            actualizoMultimedia=false;
        }
        boolean eliminoModelo;
        eliminoModelo= !pasoModelo.equals("NULL") && !pasoModelo.equals("null") && !pasoModelo.equals("") && !model;
        if(actualizoInfo || actualizoMultimedia || eliminoModelo){
            guardarEdicionesPublicacion.setEnabled(true);
            guardarEdicionesPublicacion.setBackground(getResources().getDrawable(R.drawable.boton_aceptar_redondo));
        }
        else{
            guardarEdicionesPublicacion.setEnabled(false);
            guardarEdicionesPublicacion.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
        }
    }

    private Bitmap escalarBitmap(Bitmap bitmap_provisional){
        int ancho, alto;
        ancho=bitmap_provisional.getWidth();
        alto= bitmap_provisional.getHeight();
        if(ancho<=1000 && alto<=1000){
            return bitmap_provisional;
        }
        float ratio= ((float) (ancho))/((float) (alto));
        if(ratio>1){
            ancho=1000;
            alto=(int) (ancho/ratio);
        }
        else{
            alto=1000;
            ancho=(int) (alto*ratio);
        }
        Bitmap bitmap_retorno=Bitmap.createScaledBitmap(bitmap_provisional,ancho,alto,true);
        return bitmap_retorno;
    }

    private String obtenerStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte [] imagenBytes= baos.toByteArray();
        return android.util.Base64.encodeToString(imagenBytes, Base64.DEFAULT);
    }

    public void publicarFoto(int pub, Bitmap image_coded){
        contadorFotosEnviadas++;

        System.out.println("Luchooooooooo Contador Fotos: "+contadorFotosEnviadas+", Fotos Totales: "+direccionesImagenes.size());
        String URL_SUBIR_FOTO="https://udvivienda360.000webhostapp.com/registrar_foto.php";

        StringRequest stringRequest1= new StringRequest(
                Request.Method.POST,
                URL_SUBIR_FOTO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response2) {
                        contadorRespuestasFotos++;
                        System.out.println("Luchooooo\tResponse # "+contadorFotosEnviadas+": "+response2);
                        if(response2.equals("OK")){
                            if(contadorFotosEnviadas==direccionesImagenes.size() && contadorRespuestasFotos==direccionesImagenes.size()){
                                System.out.println("Luchoooo\tHa subido la totalidad de las fotos");
                                if(modoEdicion){
                                    System.out.println("Luchoooo\tEstás en modo edicion");
                                    if(agregoModelo){
                                        System.out.println("Luchoooo\tAgregando modelo dentro de edicion");
                                        if(!pasoModelo.equals("NULL") && !pasoModelo.equals("null") && !pasoModelo.equals("")){
                                            String URL_ELIMINAR_MODELO="https://udvivienda360.000webhostapp.com/eliminar_modelo_publicacion.php";
                                            StringRequest stringRequestEliminarModelo = new StringRequest(
                                                    Request.Method.POST,
                                                    URL_ELIMINAR_MODELO,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            System.out.println("Luchooooo hubo response de la eliminacion del modelo, valor: "+response);
                                                            if(response.equals("OK")){
                                                                String modeloCodificado="";
                                                                InputStream is2= null;
                                                                try {
                                                                    is2 = getContext().getContentResolver().openInputStream(direccionModelo);
                                                                    byte [] modelBytes= new byte[(int) (pesoModelo)];
                                                                    is2.read(modelBytes);
                                                                    is2.close();
                                                                    modeloCodificado= Base64.encodeToString(modelBytes, Base64.DEFAULT);
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }

                                                                String modeloCodificadoFinal=modeloCodificado;
                                                                String URL_SUBIR_MODELO="https://udvivienda360.000webhostapp.com/registrar_modelo.php";
                                                                StringRequest stringRequestModelo= new StringRequest(
                                                                        Request.Method.POST,
                                                                        URL_SUBIR_MODELO,
                                                                        new Response.Listener<String>() {
                                                                            @Override
                                                                            public void onResponse(String response) {
                                                                                System.out.println("Luchooooo\tResponse modelo: "+response);
                                                                                if(response.equals("OK")){
                                                                                    Toast.makeText(getContext(), R.string.publicacion_actualizada, Toast.LENGTH_SHORT).show();
                                                                                    FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                                                                    MisPublicacionesFragment misPublicacionesFragment= new MisPublicacionesFragment();
                                                                                    fragmentTransaction.replace(R.id.contenedor_ppal_perfil, misPublicacionesFragment);
                                                                                    fragmentTransaction.commit();
                                                                                }
                                                                            }
                                                                        },
                                                                        new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {
                                                                                System.out.println("Luchoooo\tError linea 1420 de Publicar:\t"+error.getMessage());
                                                                            }
                                                                        }
                                                                ){
                                                                    @Override
                                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                                        Map<String, String> parametros= new HashMap<>();
                                                                        parametros.put("publicacion",String.valueOf(pub));
                                                                        parametros.put("modelo", modeloCodificadoFinal);
                                                                        return parametros;
                                                                    }
                                                                };
                                                                stringRequestModelo.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                                                requestQueue.add(stringRequestModelo);
                                                            }
                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            System.out.println("Luchoooooo... error volley\n"+error.toString()+"\n"
                                                                    +error.getMessage()+"\n"+error.getLocalizedMessage()+"\n");
                                                        }
                                                    }
                                            ){
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String, String> parametros= new HashMap<>();
                                                    parametros.put("publicacion",convertirCadena(pasoId));
                                                    return parametros;
                                                }
                                            };
                                            stringRequestEliminarModelo.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                            requestQueue.add(stringRequestEliminarModelo);
                                        }
                                        else{
                                            String modeloCodificado="";
                                            InputStream is2= null;
                                            try {
                                                is2 = getContext().getContentResolver().openInputStream(direccionModelo);
                                                byte [] modelBytes= new byte[(int) (pesoModelo)];
                                                is2.read(modelBytes);
                                                is2.close();
                                                modeloCodificado= Base64.encodeToString(modelBytes, Base64.DEFAULT);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            String modeloCodificadoFinal=modeloCodificado;
                                            String URL_SUBIR_MODELO="https://udvivienda360.000webhostapp.com/registrar_modelo.php";
                                            StringRequest stringRequestModelo= new StringRequest(
                                                    Request.Method.POST,
                                                    URL_SUBIR_MODELO,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            System.out.println("Luchooooo\tResponse modelo: "+response);
                                                            if(response.equals("OK")){
                                                                Toast.makeText(getContext(), R.string.publicacion_actualizada, Toast.LENGTH_SHORT).show();
                                                                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                                                MisPublicacionesFragment misPublicacionesFragment= new MisPublicacionesFragment();
                                                                fragmentTransaction.replace(R.id.contenedor_ppal_perfil, misPublicacionesFragment);
                                                                fragmentTransaction.commit();
                                                            }
                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            System.out.println("Luchoooo\tError linea 1420 de Publicar:\t"+error.getMessage());
                                                        }
                                                    }
                                            ){
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String, String> parametros= new HashMap<>();
                                                    parametros.put("publicacion",String.valueOf(pub));
                                                    parametros.put("modelo", modeloCodificadoFinal);
                                                    return parametros;
                                                }
                                            };
                                            stringRequestModelo.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                            requestQueue.add(stringRequestModelo);
                                        }
                                    }
                                    else{
                                        Toast.makeText(getContext(), R.string.publicacion_actualizada, Toast.LENGTH_SHORT).show();
                                        FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                                        MisPublicacionesFragment misPublicacionesFragment= new MisPublicacionesFragment();
                                        fragmentTransaction.replace(R.id.contenedor_ppal_perfil, misPublicacionesFragment);
                                        fragmentTransaction.commit();
                                    }
                                }
                                else{
                                    System.out.println("Luchoooo\tEntrando en modo publicacion");
                                    if(model){
                                        System.out.println("Luchoooo\tPublicando modelo");
                                        System.out.println("Luchoooo\tdireccionModelo: "+direccionModelo);
                                        System.out.println("Luchoooo\tdireccionModelo.getPath: "+direccionModelo.getPath());
                                        System.out.println("Luchoooo\tdireccionModelo.getEncodedPath: "+direccionModelo.getEncodedPath());


                                        /*
                                        //↓↓↓↓↓↓↓↓↓↓↓↓   Codificacion del modelo   ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
                                        String modeloCodificado="";
                                        InputStream is2= null;
                                        try {


                                            //is2 = getContext().getContentResolver().openInputStream(direccionModelo);
                                            is2 = getActivity().getContentResolver().openInputStream(direccionModelo);

                                            byte [] modelBytes= new byte[(int) (pesoModelo)];
                                            is2.read(modelBytes);
                                            is2.close();
                                            System.out.println("cantidad caracteres: "+Base64.encodeToString(modelBytes, Base64.DEFAULT).length());
                                            modeloCodificado= Base64.encodeToString(modelBytes, Base64.DEFAULT);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        String modeloCodificadoFinal=modeloCodificado;
                                        //↑↑↑↑↑↑↑↑↑↑↑↑   Codificacion del modelo   ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
                                        */

                                        /*

                                        //---------------↓↓↓ VOLLEY ↓↓↓-----------------------------

                                        String URL_SUBIR_MODELO="https://udvivienda360.000webhostapp.com/registrar_modelo.php";
                                        StringRequest stringRequestModeloPublicando= new StringRequest(
                                                Request.Method.POST,
                                                URL_SUBIR_MODELO,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        System.out.println("Luchooooo\tResponse modelo: "+response);
                                                        if(response.equals("OK")){
                                                            finalizarProcesoPublicar();
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        System.out.println("Luchoooo\tError linea 1420 de Publicar:\t"+error.getMessage());
                                                    }
                                                }
                                        ){
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> parametrosModelo= new HashMap<>();
                                                System.out.println("Rev:1 En el modelo la pub vale: "+String.valueOf(pub));
                                                System.out.println("Rev:1 En el modelo el mod_Codificado es : "+modeloCodificadoFinal.length());
                                                String r1,r2;
                                                r1=parametrosModelo.put("publicacion",String.valueOf(pub));
                                                r2=parametrosModelo.put("modelo", "asasdasda");
                                                System.out.println("Rev1:\tR1: "+r1);
                                                System.out.println("Rev1:\tR2: "+r2);
                                                return parametrosModelo;
                                            }
                                        };
                                        stringRequestModeloPublicando.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
                                        try {
                                            System.out.println("Valores map: "+stringRequestModeloPublicando.getHeaders().values());
                                        } catch (AuthFailureError e) {
                                            e.printStackTrace();
                                        }
                                        requestQueue.add(stringRequestModeloPublicando);

                                        //---------------↑↑↑ VOLLEY ↑↑↑-----------------------------
                                        */


                                        String URL_SUBIR_MODELO="https://udvivienda360.000webhostapp.com/registrar_modelo_OKHTTP.php";
                                        Thread t = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                File f= new File(direccionModelo.getPath().substring(direccionModelo.getPath().indexOf(":")+1));
                                                String content_type= getMimeType(f.getPath());
                                                String file_path= f.getAbsolutePath();
                                                System.out.println("Content Type: "+content_type);
                                                System.out.println("file_path: "+file_path);
                                                OkHttpClient client = new OkHttpClient();
                                                RequestBody file_body = RequestBody.create(MediaType.parse("application"),f);
                                                RequestBody request_body = new MultipartBody.Builder()
                                                        .setType(MultipartBody.FORM)
                                                        //.addFormDataPart("type", content_type)
                                                        .addFormDataPart("type", "model/gltf-binary")
                                                        .addFormDataPart("uploaded_file", file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                                                        .addFormDataPart("publicacion", String.valueOf(pub))
                                                        .build();
                                                okhttp3.Request request= new okhttp3.Request.Builder()
                                                        .url(URL_SUBIR_MODELO)
                                                        .post(request_body)
                                                        .build();
                                                try {
                                                    okhttp3.Response response= client.newCall(request).execute();
                                                    System.out.println("Respuesta del modelo: "+response.body().string());
                                                    if(!response.isSuccessful()){
                                                        System.out.println("Respuesta del modelo: "+response.body().string());
                                                    }
                                                    else{
                                                        finalizarProcesoPublicar();
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        t.start();



                                        /*InputStream is2= null;
                                        String modeloCodificado="";
                                        try {
                                            is2 = getActivity().getContentResolver().openInputStream(direccionModelo);
                                            byte [] modelBytes= new byte[(int) (pesoModelo)];
                                            is2.read(modelBytes);
                                            is2.close();
                                            System.out.println("cantidad caracteres: "+Base64.encodeToString(modelBytes, Base64.DEFAULT).length());
                                            modeloCodificado= Base64.encodeToString(modelBytes, Base64.DEFAULT);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        RequestBody requestBody = new FormBody.Builder()
                                                .add("publicacion", String.valueOf(pub))
                                                .add("modelo", modeloCodificado)
                                                .build();
                                        okhttp3.Request request= new okhttp3.Request.Builder()
                                                .url(URL_SUBIR_MODELO)
                                                .post(requestBody)
                                                .build();
                                        OkHttpClient client= new OkHttpClient();
                                        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                System.out.println("Luchooooo: error");
                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                                                System.out.println("Luchooooo bien modelo");
                                                System.out.println("Respuesta del modelo: "+response.body().string());
                                                if(response.body().string().equals("OK")){
                                                    finalizarProcesoPublicar();
                                                }
                                            }
                                        });

                                        */

                                    }
                                    else{
                                        finalizarProcesoPublicar();
                                    }
                                }
                            }
                        }
                        else{
                            System.out.println("respuesta no fue ok, no error volley");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error2) {
                        System.out.println("error en Voley para enviar la foto");
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametrosFotos= new HashMap<>();
                parametrosFotos.put("publicacion",String.valueOf(pub));
                parametrosFotos.put("foto", obtenerStringImage(image_coded));
                return parametrosFotos;
            }
        };
        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
        requestQueue.add(stringRequest1);
    }

    private String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        System.out.println("Extension del archivo: "+extension);
        System.out.println("Mimetype detectado: "+MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
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

    public void finalizarProcesoPublicar(){
        String textoCorreo2, textoNombre, textoTelefono, textoWhatsapp,
                textoPregunta1, textoRespuesta1, textoPregunta2,
                textoRespuesta2, textoFavoritos, textoPublicados;

        textoCorreo2= textoNombre= textoTelefono= textoWhatsapp=
                textoPregunta1= textoRespuesta1= textoPregunta2=
                        textoRespuesta2= textoFavoritos= textoPublicados="";

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
                textoPublicados=reader.readLine();
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
        if(textoPublicados.equals("null")){
            textoPublicados=""+numeroPublicacion;
        }
        else{
            textoPublicados=textoPublicados+" "+numeroPublicacion;
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
            fos.write(textoPublicados+"\n");
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
        getActivity().runOnUiThread(()->{
            Toast.makeText(getContext(),R.string.publicacion_exitosa, Toast.LENGTH_SHORT).show();
            FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
            MisPublicacionesFragment misPublicacionesFragment= new MisPublicacionesFragment();
            fragmentTransaction.replace(R.id.contenedor_ppal_perfil, misPublicacionesFragment);
            fragmentTransaction.commit();
        });

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageSwitcher bmImage;

        public DownloadImageTask(ImageSwitcher bmImage) {
            this.bmImage = bmImage;
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
            bmImage.setImageDrawable(new BitmapDrawable(getResources(), result));
            System.out.println("Luchooooo asignacion de imagen realizada");
        }
    }
}