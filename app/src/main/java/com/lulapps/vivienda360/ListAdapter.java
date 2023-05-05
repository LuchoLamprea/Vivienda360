package com.lulapps.vivienda360;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    public List<ListElement> mData;
    private LayoutInflater mInflater;
    private Context context;
    private OnAdapterItemClickListener adapterItemClickListener = null;
    private ViewHolder mViewHolder;

    public ListAdapter(List<ListElement> mData, Context context, OnAdapterItemClickListener listener) {
        this.mInflater= LayoutInflater.from(context);
        this.context = context;
        this.mData = mData;
        this.adapterItemClickListener= listener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<ListElement> getmData() {
        return mData;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= mInflater.inflate(R.layout.publicacion, parent, false);
        ViewHolder mViewHolder=new ListAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterItemClickListener.onAdapterItemClickListener(v, mViewHolder.getPosition());
            }
        });
        this.mViewHolder=mViewHolder;
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<ListElement> items){
        mData=items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView fotoCasa;
        TextView area, habitaciones, banos, categoriaOferta, barrio, localidad, precio, separador1, separador2, contactar, idPublicacion;
        public Button editar, eliminar;
        ArrayList<String> numerosRomanos;

        ViewHolder(View itemView){
            super(itemView);
            numerosRomanos=new ArrayList<>();
            fotoCasa= itemView.findViewById(R.id.imageViewFotoCasa);
            area= itemView.findViewById(R.id.textViewArea);
            habitaciones= itemView.findViewById(R.id.textViewHabitaciones);
            banos= itemView.findViewById(R.id.textViewBanos);
            categoriaOferta= itemView.findViewById(R.id.textViewCategoriaOferta);
            barrio= itemView.findViewById(R.id.textViewBarrio);
            localidad= itemView.findViewById(R.id.textViewLocalidad);
            precio= itemView.findViewById(R.id.textViewPrecio);
            editar= itemView.findViewById(R.id.buttonEditar);
            eliminar= itemView.findViewById(R.id.buttonEliminar);
            separador1= itemView.findViewById(R.id.textViewSeparadorHabitaciones);
            separador2= itemView.findViewById(R.id.textViewSeparadorArea);
            contactar = itemView.findViewById(R.id.textViewContactar);
            idPublicacion= itemView.findViewById(R.id.textViewIdPublicacion);
        }

        void bindData(final ListElement item){
            if(!item.propiedadPublicacion.equals("propia")){
                System.out.println("Luchooo\tDetectó sin edicion");
                editar.setVisibility(View.INVISIBLE);
                editar.setEnabled(false);
                editar.setElevation(0);
                eliminar.setVisibility(View.INVISIBLE);
                eliminar.setEnabled(false);
                eliminar.setElevation(0);
                contactar.setVisibility(View.VISIBLE);
                contactar.setEnabled(true);
                contactar.setElevation(2);
            }
            else{
                editar.setVisibility(View.VISIBLE);
                editar.setEnabled(true);
                editar.setElevation(2);
                eliminar.setVisibility(View.VISIBLE);
                eliminar.setEnabled(true);
                eliminar.setElevation(2);
                contactar.setVisibility(View.INVISIBLE);
                contactar.setEnabled(false);
                contactar.setElevation(0);
            }
            fotoCasa.setImageDrawable(item.getFoto());
            area.setText(context.getResources().getString(R.string.area)+" "+item.getArea());
            habitaciones.setText(item.getHabitaciones()+" "+context.getResources().getString(R.string.habitaciones));
            banos.setText(item.getBanos()+" "+context.getResources().getString(R.string.banos));
            categoriaOferta.setText(convertirMayuscPrimera(item.getTipoInmueble()+" "+context.getResources().getString(R.string.articulo_en)+" "+item.getTipoOferta()));
            ArrayList<String> partes= new ArrayList<>();
            partes= dividirTextos(convertirMayuscPrimera(item.getBarrio()));
            barrio.setText(partes.get(0));
            localidad.setText(partes.get(1));
            DecimalFormatSymbols symbols= new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            DecimalFormat decimalFormat= new DecimalFormat("###,###,###,###,###",symbols);
            String precioFormateado="$"+decimalFormat.format(Double.parseDouble(item.getPrecio()));
            precio.setText(precioFormateado);

            idPublicacion.setText(context.getResources().getString(R.string.id_numero)+" "+item.getId());
            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterItemClickListener.onEditarButtonClickListener(v, Integer.parseInt(item.getId()));
                }
            });
            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterItemClickListener.onEliminarButtonClickListener(v, Integer.parseInt(item.getId()));
                }
            });
            contactar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterItemClickListener.onContactarButtonClickListener(v, Integer.parseInt(item.getId()));
                }
            });
        }

        public String convertirMayuscPrimera(String texto){
            StringTokenizer st= new StringTokenizer(texto, " ");
            StringBuilder textoConvertido= new StringBuilder();
            inicializarNumerosRomanos();
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

        public ArrayList<String> dividirTextos(String texto){
            ArrayList<String> partes= new ArrayList<>();
            partes.add(texto.substring(0,texto.indexOf("(")-1).trim());
            partes.add(texto.substring(texto.indexOf("(")+1,texto.indexOf(")")));
            return partes;
        }

        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView bmImage;

            public DownloadImageTask(ImageView bmImage) {
                System.out.println("Luchooo\tIniciando descarga foto");
                this.bmImage = bmImage;
            }

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                System.out.println("Luchooo\tDescargando la foto: "+urldisplay);
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
                System.out.println("Luchooo\tFoto descargada");
                bmImage.setImageBitmap(result);
            }
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
}
