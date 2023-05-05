package com.lulapps.vivienda360;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;

public class FotosFragment extends DialogFragment {

    ImageSwitcher fotos;
    ImageView anterior, siguiente;
    TextView contadorFoto;
    ProgressBar progressBar;
    String id;
    RequestQueue requestQueue;
    ArrayList<Bitmap> listadoImagenes;
    int contadorImagenesDescargadas, cantidadTotalFotos, fotoActual;

    public FotosFragment() {
        listadoImagenes = new ArrayList<>();
        contadorImagenesDescargadas=0;
        cantidadTotalFotos=0;
    }

    public FotosFragment(String id){
        listadoImagenes = new ArrayList<>();
        this.id = id;
        contadorImagenesDescargadas=0;
        cantidadTotalFotos=0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue= Volley.newRequestQueue(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fotos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DisplayMetrics metrics= new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        view.setLayoutParams(new FrameLayout.LayoutParams((int) (metrics.widthPixels*0.9), ViewGroup.LayoutParams.WRAP_CONTENT));

        fotos= view.findViewById(R.id.imageSwitcherVisualizadorFotos);
        anterior= view.findViewById(R.id.imageViewFotoAnterior);
        siguiente= view.findViewById(R.id.imageViewFotoSiguiente);
        contadorFoto= view.findViewById(R.id.textViewContadorFotos);
        progressBar= view.findViewById(R.id.progressBarVisualizacion);

        habilitarDeshabilitarControles(false, (ViewGroup) view);
        progressBar.setEnabled(true);

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

        String URL_OBTENER_PUBLICACIONES="https://udvivienda360.000webhostapp.com/obtener_fotos_de_publicacion.php?id="+convertirCadena(id);

        JsonArrayRequest jsonArrayRequestFotos= new JsonArrayRequest(
                Request.Method.GET,
                URL_OBTENER_PUBLICACIONES,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        cantidadTotalFotos=response.length();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                new DownloadImageTask(view).execute(response.getJSONObject(i).getString("foto"));
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
        jsonArrayRequestFotos.setRetryPolicy(new DefaultRetryPolicy(Variables.tiempo_de_espera, Variables.reintentos_maximos, Variables.delay));
        requestQueue.add(jsonArrayRequestFotos);

        anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    fotoActual--;
                    fotos.setImageDrawable(new BitmapDrawable(getResources(), listadoImagenes.get(fotoActual)));
                }catch (IndexOutOfBoundsException e){
                    fotoActual=listadoImagenes.size()-1;
                    fotos.setImageDrawable(new BitmapDrawable(getResources(), listadoImagenes.get(fotoActual)));
                }
                contadorFoto.setText((fotoActual+1)+"/"+cantidadTotalFotos);
            }
        });

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    fotoActual++;
                    fotos.setImageDrawable(new BitmapDrawable(getResources(), listadoImagenes.get(fotoActual)));
                }catch (IndexOutOfBoundsException e){
                    fotoActual=0;
                    fotos.setImageDrawable(new BitmapDrawable(getResources(), listadoImagenes.get(fotoActual)));
                }
                contadorFoto.setText((fotoActual+1)+"/"+cantidadTotalFotos);
            }
        });



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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        View view;

        public DownloadImageTask(View view) {
            this.view= view;
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
            listadoImagenes.add(result);
            contadorImagenesDescargadas++;
            if(contadorImagenesDescargadas==1){
                fotos.setImageDrawable(new BitmapDrawable(getResources(), result));
                fotoActual=0;
            }
            if(contadorImagenesDescargadas==2){
                anterior.setVisibility(View.VISIBLE);
                siguiente.setVisibility(View.VISIBLE);
            }
            if(contadorImagenesDescargadas==cantidadTotalFotos){
                if(contadorImagenesDescargadas>=2){
                    anterior.setVisibility(View.VISIBLE);
                    siguiente.setVisibility(View.VISIBLE);
                }
                contadorFoto.setText((fotoActual+1)+"/"+cantidadTotalFotos);
                habilitarDeshabilitarControles(true, (ViewGroup) view);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void habilitarDeshabilitarControles(boolean enable, ViewGroup vg){
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup){
                habilitarDeshabilitarControles(enable, (ViewGroup)child);
            }
        }
    }

}