package com.lulapps.vivienda360;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Zona implements Parcelable {
    public String Nombre;
    public ArrayList<LatLng> puntos;

    public Zona() {
        puntos= new ArrayList<>();
    }

    public Zona(String nombre, ArrayList<LatLng> puntos) {
        this.puntos= new ArrayList<>(puntos);
        Nombre = nombre;
    }

    protected Zona (Parcel in){
        puntos= new ArrayList<>();
        Nombre= in.readString();
        puntos= in.readArrayList(LatLng.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Nombre);
        dest.writeList(puntos);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Zona> CREATOR = new Creator<Zona>() {
        @Override
        public Zona createFromParcel(Parcel source) {
            return new Zona(source);
        }

        @Override
        public Zona[] newArray(int size) {
            return new Zona[size];
        }
    };
}
