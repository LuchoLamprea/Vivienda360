package com.lulapps.vivienda360;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FiltroFragment extends DialogFragment{

    Button habitaciones1, habitaciones2, habitaciones3, habitaciones4, habitaciones5, banos1, banos2, banos3, banos4, banos5, garajes0, garajes1, garajes2, garajes3, estrato1, estrato2, estrato3, estrato4, estrato5, estrato6;
    Button casa, apartamento, venta, arriendo, aplicar;
    EditText minimo, maximo;
    ImageView cerrar;
    String SQLprevio;
    OnFilterAppliedListener filterAppliedListener= null;

    public FiltroFragment() {
        SQLprevio="";
    }

    public FiltroFragment(OnFilterAppliedListener filterAppliedListener, String SQLprevio) {
        this.filterAppliedListener= filterAppliedListener;
        this.SQLprevio= SQLprevio;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filtro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayMetrics metrics= new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        view.setLayoutParams(new FrameLayout.LayoutParams((int) (metrics.widthPixels*0.9),(int) (metrics.heightPixels*0.9)));

        habitaciones1= view.findViewById(R.id.buttonHabitaciones1);
        habitaciones2= view.findViewById(R.id.buttonHabitaciones2);
        habitaciones3= view.findViewById(R.id.buttonHabitaciones3);
        habitaciones4= view.findViewById(R.id.buttonHabitaciones4);
        habitaciones5= view.findViewById(R.id.buttonHabitaciones5);

        banos1= view.findViewById(R.id.buttonBanos1);
        banos2= view.findViewById(R.id.buttonBanos2);
        banos3= view.findViewById(R.id.buttonBanos3);
        banos4= view.findViewById(R.id.buttonBanos4);
        banos5= view.findViewById(R.id.buttonBanos5);

        garajes0= view.findViewById(R.id.buttonGarajes0);
        garajes1= view.findViewById(R.id.buttonGarajes1);
        garajes2= view.findViewById(R.id.buttonGarajes2);
        garajes3= view.findViewById(R.id.buttonGarajes3);

        estrato1= view.findViewById(R.id.buttonEstrato1);
        estrato2= view.findViewById(R.id.buttonEstrato2);
        estrato3= view.findViewById(R.id.buttonEstrato3);
        estrato4= view.findViewById(R.id.buttonEstrato4);
        estrato5= view.findViewById(R.id.buttonEstrato5);
        estrato6= view.findViewById(R.id.buttonEstrato6);

        casa= view.findViewById(R.id.buttonCasa);
        apartamento= view.findViewById(R.id.buttonApartamento);

        venta= view.findViewById(R.id.buttonVenta);
        arriendo= view.findViewById(R.id.buttonArriendo);

        minimo = view.findViewById(R.id.editTextPrecioMinimo);
        maximo = view.findViewById(R.id.editTextPrecioMaximo);

        aplicar= view.findViewById(R.id.buttonAplicar);

        cerrar= view.findViewById(R.id.imageViewBotonCerrar);

        habitaciones1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        habitaciones2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        habitaciones3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        habitaciones4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        habitaciones5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        banos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        banos2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        banos3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        banos4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        banos5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        garajes0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        garajes1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        garajes2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        garajes3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        estrato1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        estrato2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        estrato3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        estrato4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        estrato5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });
        estrato6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });

        casa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });

        apartamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoBoton(v, view);
            }
        });

        venta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if(arriendo.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState())){
                    arriendo.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
                    venta.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                }
                else{
                    boolean buttonActivo=venta.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState());
                    if(buttonActivo){
                        venta.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
                    }
                    else{
                        venta.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                    }
                }
            }
        });

        arriendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if(venta.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState())){
                    venta.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
                    arriendo.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                }
                else{
                    boolean buttonActivo=arriendo.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState());
                    if(buttonActivo){
                        arriendo.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
                    }
                    else{
                        arriendo.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                    }
                }
            }
        });

        minimo.addTextChangedListener(new TextWatcher() {
            String textoPrevio="";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textoPrevio=textoPrevio+s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    try{
                        NumberFormat numberFormat= NumberFormat.getCurrencyInstance();
                        numberFormat.setMaximumFractionDigits(0);
                        String precioFormateado;
                        if(s.toString().equals("$") || s.toString().isEmpty()){
                            precioFormateado=numberFormat.format(0);
                        }
                        else{
                            precioFormateado=numberFormat.format(Double.parseDouble(s.toString().replaceAll("[$.,]","")));
                        }
                        int valorCursor= minimo.getSelectionStart();
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
                        minimo.removeTextChangedListener(this);
                        minimo.setText(precioFormateado);
                        minimo.addTextChangedListener(this);
                        try {
                            if(valorCursor!=1){
                                minimo.setSelection(valorCursor);
                            }
                            else{
                                minimo.setSelection(minimo.length());
                            }
                        }
                        catch (IndexOutOfBoundsException e){
                            minimo.setSelection(minimo.length());
                        }
                    }
                    catch (NumberFormatException e){
                        System.out.println("Luchooo error+\t"+e.getMessage().toString());
                        e.printStackTrace();
                        Toast.makeText(getContext(), getResources().getString(R.string.error_precio_no_valido), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        maximo.addTextChangedListener(new TextWatcher() {
            String textoPrevio="";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textoPrevio=textoPrevio+s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    try{
                        NumberFormat numberFormat= NumberFormat.getCurrencyInstance();
                        numberFormat.setMaximumFractionDigits(0);
                        String precioFormateado;
                        if(s.toString().equals("$") || s.toString().isEmpty()){
                            precioFormateado=numberFormat.format(0);
                        }
                        else{
                            precioFormateado=numberFormat.format(Double.parseDouble(s.toString().replaceAll("[$.,]","")));
                        }
                        int valorCursor= maximo.getSelectionStart();
                        if((precioFormateado.length()-s.toString().length())==1){
                            valorCursor++;
                        }

                        textoPrevio="";
                        maximo.removeTextChangedListener(this);
                        maximo.setText(precioFormateado);
                        maximo.addTextChangedListener(this);
                        try {
                            if(valorCursor!=1){
                                maximo.setSelection(valorCursor);
                            }
                            else{
                                maximo.setSelection(maximo.length());
                            }
                        }
                        catch (IndexOutOfBoundsException e){
                            maximo.setSelection(maximo.length());
                        }
                    }
                    catch (NumberFormatException e){
                        System.out.println("Luchooo error+\t"+e.getMessage().toString());
                        e.printStackTrace();
                        Toast.makeText(getContext(), getResources().getString(R.string.error_precio_no_valido), Toast.LENGTH_SHORT).show();
                    }
                }
                double minimoPrevio=0, maximoPrevio=0;
                try{
                    minimoPrevio=Double.parseDouble(minimo.getText().toString().replaceAll("[$.,]",""));
                }
                catch (NullPointerException | NumberFormatException e){
                    minimoPrevio=0;
                }
                try{
                    maximoPrevio=Double.parseDouble(maximo.getText().toString().replaceAll("[$.,]",""));
                }
                catch (NullPointerException | NumberFormatException e){
                    maximoPrevio=0;
                }
                if(minimoPrevio!= 0 && maximoPrevio!=0 && minimoPrevio>maximoPrevio){
                    maximo.setBackground(getResources().getDrawable(R.drawable.edittext_redondo_error));
                }
                else{
                    maximo.setBackground(getResources().getDrawable(R.drawable.edittext_redondo));
                }
            }
        });

        if(!SQLprevio.equals("") && !SQLprevio.equals("(1)")){
            StringTokenizer tokenizer= new StringTokenizer(SQLprevio.replace(") AND (", "*"), "*");
            for(int i=0; i<7; i++){
                String token=tokenizer.nextToken();
                System.out.println("Luchoooo\tToken "+(i)+": "+token);
                switch (i){
                    case 0:
                        if(!token.equals("(1")){
                            if(token.contains(">= ")){
                                System.out.println("Luchoooo\tFiltro con minimo, posible valor: "+token.substring(token.indexOf(">= ")+3));
                                StringTokenizer tokenizer2= new StringTokenizer(token.substring(token.indexOf(">= ")+3), " ");
                                try {
                                    String borrar=tokenizer2.nextToken();
                                    System.out.println("Luchoooo\tPrecio minimo: "+borrar);
                                    minimo.setText(borrar);
                                }catch (NumberFormatException | NullPointerException exception){
                                    System.out.println("Luchoooo\tFiltro sin minimo");
                                }
                            }
                            if(token.contains("<= ")){
                                System.out.println("Luchoooo\tFiltro con maximo");
                                StringTokenizer tokenizer2= new StringTokenizer(token.substring(token.indexOf("<= ")+3), " ");
                                try {
                                    String borrar=tokenizer2.nextToken();
                                    System.out.println("Luchoooo\tPrecio maximo: "+borrar);
                                    maximo.setText(borrar);
                                }catch (NumberFormatException | NullPointerException exception){
                                    System.out.println("Luchoooo\tFiltro sin maximo");
                                }
                            }
                        }
                        break;
                    case 1:
                        if(!token.equals("1")){
                            if(token.contains("CASA")){
                                cambiarEstadoBoton(casa, view);
                            }
                            else{
                                cambiarEstadoBoton(apartamento,view);
                            }
                        }
                        break;
                    case 2:
                        if(!token.equals("1")){
                            if(token.contains("VENTA")){
                                venta.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                            }
                            else{
                                arriendo.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                            }
                        }
                        break;
                    case 3:
                        if(!token.equals("1")){
                            if(token.contains("1")){
                                cambiarEstadoBoton(habitaciones1,view);
                            }
                            if(token.contains("2")){
                                cambiarEstadoBoton(habitaciones2, view);
                            }
                            if(token.contains("3")){
                                cambiarEstadoBoton(habitaciones3, view);
                            }
                            if(token.contains("4")){
                                cambiarEstadoBoton(habitaciones4, view);
                            }
                            if(token.contains("5")){
                                cambiarEstadoBoton(habitaciones5, view);
                            }
                        }
                        break;
                    case 4:
                        if(!token.equals("1")){
                            if(token.contains("1")){
                                cambiarEstadoBoton(banos1, view);
                            }
                            if(token.contains("2")){
                                cambiarEstadoBoton(banos2, view);
                            }
                            if(token.contains("3")){
                                cambiarEstadoBoton(banos3, view);
                            }
                            if(token.contains("4")){
                                cambiarEstadoBoton(banos4, view);
                            }
                            if(token.contains("5")){
                                cambiarEstadoBoton(banos5, view);
                            }
                        }
                        break;
                    case 5:
                        if(!token.equals("(1)")){
                            if(token.contains("0")){
                                cambiarEstadoBoton(garajes0, view);
                            }
                            if(token.contains("1")){
                                cambiarEstadoBoton(garajes1, view);
                            }
                            if(token.contains("2")){
                                cambiarEstadoBoton(garajes2, view);
                            }
                            if(token.contains("3")){
                                cambiarEstadoBoton(garajes3, view);
                            }
                        }
                        break;
                    case 6:
                        if(!token.equals("(1)")){
                            if(token.contains("1")){
                                cambiarEstadoBoton(estrato1, view);
                            }
                            if(token.contains("2")){
                                cambiarEstadoBoton(estrato2, view);
                            }
                            if(token.contains("3")){
                                cambiarEstadoBoton(estrato3, view);
                            }
                            if(token.contains("4")){
                                cambiarEstadoBoton(estrato4, view);
                            }
                            if(token.contains("5")){
                                cambiarEstadoBoton(estrato5, view);
                            }
                            if(token.contains("6")){
                                cambiarEstadoBoton(estrato6, view);
                            }
                        }
                        break;
                }
            }
        }

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        aplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maximo.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.edittext_redondo_error).getConstantState())){
                    Toast.makeText(getContext(), getResources().getString(R.string.error_precios), Toast.LENGTH_SHORT).show();
                }
                else{
                    String fPrecio,fInmueble, fOferta, fHabitaciones, fBanos, fGarajes, fEstrato, fCompleto;
                    fPrecio=fInmueble=fOferta=fHabitaciones=fBanos=fGarajes=fEstrato=fCompleto="";
                    String pasoPrecioMinimo, pasoPrecioMaximo;


                    pasoPrecioMinimo=minimo.getText().toString().replaceAll("[$,.]","");
                    pasoPrecioMaximo=maximo.getText().toString().replaceAll("[$,.]","");
                    if(!pasoPrecioMinimo.equals("0") && !pasoPrecioMinimo.equals("")){
                        fPrecio="precio >= "+pasoPrecioMinimo;
                        if(!pasoPrecioMaximo.equals("0") && !pasoPrecioMaximo.equals("")){
                            fPrecio= fPrecio+" AND ";
                        }
                    }
                    if(!pasoPrecioMaximo.equals("0") && !pasoPrecioMaximo.equals("")){
                        fPrecio= fPrecio+"precio <= "+pasoPrecioMaximo;
                    }
                    if(fPrecio.equals("")){
                        fPrecio="1";
                    }


                    if(!casa.getBackground().getConstantState().equals(apartamento.getBackground().getConstantState())){
                        if(casa.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState())){
                            fInmueble="inmueble = 'CASA'";
                        }
                        else{
                            fInmueble="inmueble = 'APARTAMENTO'";
                        }
                    }
                    else {
                        fInmueble="1";
                    }


                    if(venta.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState())){
                        fOferta="oferta = 'VENTA'";
                    }
                    else{
                        if(arriendo.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_activo_redondo).getConstantState())){
                            fOferta="oferta = 'ARRIENDO'";
                        }
                        else{
                            fOferta="1";
                        }
                    }


                    ArrayList<Button> listadoPaso = new ArrayList<>();
                    int contadorActivados=0;
                    listadoPaso.add(habitaciones1);
                    listadoPaso.add(habitaciones2);
                    listadoPaso.add(habitaciones3);
                    listadoPaso.add(habitaciones4);
                    listadoPaso.add(habitaciones5);
                    for(int i=0; i<listadoPaso.size(); i++){
                        if(listadoPaso.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_relleno_izquierdo).getConstantState()) || listadoPaso.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_relleno_centro).getConstantState()) || listadoPaso.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_relleno_derecho).getConstantState())){
                            contadorActivados++;
                            if(contadorActivados!=1){
                                fHabitaciones=fHabitaciones+" OR ";

                            }
                            if(!listadoPaso.get(i).getText().toString().equals("5+")){
                                fHabitaciones=fHabitaciones+"habitaciones = "+listadoPaso.get(i).getText().toString();
                            }
                            else{
                                fHabitaciones=fHabitaciones+"habitaciones >= 5";
                            }
                        }
                    }
                    if(contadorActivados==0 || contadorActivados==5){
                        fHabitaciones="1";
                    }


                    listadoPaso = new ArrayList<>();
                    contadorActivados=0;
                    listadoPaso.add(banos1);
                    listadoPaso.add(banos2);
                    listadoPaso.add(banos3);
                    listadoPaso.add(banos4);
                    listadoPaso.add(banos5);
                    for(int i=0; i<listadoPaso.size(); i++){
                        if(listadoPaso.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_relleno_izquierdo).getConstantState()) || listadoPaso.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_relleno_centro).getConstantState()) || listadoPaso.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_relleno_derecho).getConstantState())){
                            contadorActivados++;
                            if(contadorActivados!=1){
                                fBanos=fBanos+" OR ";

                            }
                            if(!listadoPaso.get(i).getText().toString().equals("5+")){
                                fBanos=fBanos+"banos = "+listadoPaso.get(i).getText().toString();
                            }
                            else{
                                fBanos=fBanos+"banos >= 5";
                            }
                        }
                    }
                    if(contadorActivados==0 || contadorActivados==5){
                        fBanos="1";
                    }


                    listadoPaso = new ArrayList<>();
                    contadorActivados=0;
                    listadoPaso.add(garajes0);
                    listadoPaso.add(garajes1);
                    listadoPaso.add(garajes2);
                    listadoPaso.add(garajes3);
                    for(int i=0; i<listadoPaso.size(); i++){
                        if(listadoPaso.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_relleno_izquierdo).getConstantState()) || listadoPaso.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_relleno_centro).getConstantState()) || listadoPaso.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_relleno_derecho).getConstantState())){
                            contadorActivados++;
                            if(contadorActivados!=1){
                                fGarajes=fGarajes+" OR ";

                            }
                            if(!listadoPaso.get(i).getText().toString().equals("3+")){
                                fGarajes=fGarajes+"garajes = "+listadoPaso.get(i).getText().toString();
                            }
                            else{
                                fGarajes=fGarajes+"garajes >= 3";
                            }
                        }
                    }
                    if(contadorActivados==0 || contadorActivados==4){
                        fGarajes="1";
                    }


                    listadoPaso = new ArrayList<>();
                    contadorActivados=0;
                    listadoPaso.add(estrato1);
                    listadoPaso.add(estrato2);
                    listadoPaso.add(estrato3);
                    listadoPaso.add(estrato4);
                    listadoPaso.add(estrato5);
                    listadoPaso.add(estrato6);
                    for(int i=0; i<listadoPaso.size(); i++){
                        if(listadoPaso.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_relleno_izquierdo).getConstantState()) || listadoPaso.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_relleno_centro).getConstantState()) || listadoPaso.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_relleno_derecho).getConstantState())){
                            contadorActivados++;
                            if(contadorActivados!=1){
                                fEstrato=fEstrato+" OR ";

                            }
                            fEstrato=fEstrato+"estrato = "+listadoPaso.get(i).getText().toString();
                        }
                    }
                    if(contadorActivados==0 || contadorActivados==6){
                        fEstrato="1";
                    }

                    fCompleto="("+fPrecio+") AND ("+fInmueble+") AND ("+fOferta+") AND ("+fHabitaciones+") AND ("+fBanos+") AND ("+fGarajes+") AND ("+fEstrato+")";
                    if(fCompleto.equals("(1) AND (1) AND (1) AND (1) AND (1) AND (1) AND (1)")){
                        fCompleto="(1)";
                    }
                    filterAppliedListener.OnFilterAppliedListener(fCompleto);
                    dismiss();
                    //System.out.println("Luchoooo\tFiltro completo:\tSELECT * FROM publicaciones WHERE "+fCompleto);
                }
            }
        });
    }

    public boolean cambiarEstadoBoton(View view, View parent){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        String TAG;
        try{
            TAG= view.getTag().toString();
        }
        catch (NullPointerException e){
            TAG="";
        }
        if(TAG.equals("izquierda")){
            if(view.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_borde_izquierdo).getConstantState())){
                view.setBackground(getResources().getDrawable(R.drawable.boton_relleno_izquierdo));
                Button paso1= (Button) (parent.findViewById(view.getId()));
                paso1.setTextColor(getResources().getColor(R.color.white));
                return true;
            }
            else{
                view.setBackground(getResources().getDrawable(R.drawable.boton_borde_izquierdo));
                Button paso1= (Button) (parent.findViewById(view.getId()));
                paso1.setTextColor(getResources().getColor(R.color.azul_barra_inf));
            }
        }
        else{
            if(TAG.equals("derecha")){
                if(view.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_borde_derecho).getConstantState())){
                    view.setBackground(getResources().getDrawable(R.drawable.boton_relleno_derecho));
                    Button paso1= (Button) (parent.findViewById(view.getId()));
                    paso1.setTextColor(getResources().getColor(R.color.white));
                    return true;
                }
                else{
                    view.setBackground(getResources().getDrawable(R.drawable.boton_borde_derecho));
                    Button paso1= (Button) (parent.findViewById(view.getId()));
                    paso1.setTextColor(getResources().getColor(R.color.azul_barra_inf));
                }
            }
            else{
                if (TAG.equals("normal")){
                    if(view.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_inactivo_redondo).getConstantState())){
                        view.setBackground(getResources().getDrawable(R.drawable.boton_activo_redondo));
                        return true;
                    }
                    else{
                        view.setBackground(getResources().getDrawable(R.drawable.boton_inactivo_redondo));
                    }
                }
                else{
                    if(view.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.boton_borde_centro).getConstantState())){
                        view.setBackground(getResources().getDrawable(R.drawable.boton_relleno_centro));
                        Button paso1= (Button) (parent.findViewById(view.getId()));
                        paso1.setTextColor(getResources().getColor(R.color.white));
                        return true;
                    }
                    else{
                        view.setBackground(getResources().getDrawable(R.drawable.boton_borde_centro));
                        Button paso1= (Button) (parent.findViewById(view.getId()));
                        paso1.setTextColor(getResources().getColor(R.color.azul_barra_inf));
                    }
                }
            }
        }
        return true;
    }
}