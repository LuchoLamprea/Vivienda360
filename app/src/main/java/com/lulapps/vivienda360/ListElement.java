package com.lulapps.vivienda360;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ListElement {

    public String barrio;
    public String precio;
    public String area;
    public String habitaciones;
    public String banos;
    public Drawable foto;
    public String id;
    public String tipoOferta;
    public String tipoInmueble;
    public String propiedadPublicacion;

    public ListElement(String barrio, String precio, String area, String habitaciones, String banos, Drawable foto, String id, String tipoOferta, String tipoInmueble, String propiedadPublicacion) {
        this.barrio = barrio;
        this.precio = precio;
        this.area = area;
        this.habitaciones = habitaciones;
        this.banos = banos;
        this.foto = foto;
        this.id = id;
        this.tipoOferta = tipoOferta;
        this.tipoInmueble = tipoInmueble;
        this.propiedadPublicacion = propiedadPublicacion;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(String habitaciones) {
        this.habitaciones = habitaciones;
    }

    public String getBanos() {
        return banos;
    }

    public void setBanos(String banos) {
        this.banos = banos;
    }

    public Drawable getFoto() {
        return foto;
    }

    public void setFoto(Drawable foto) {
        this.foto = foto;
    }

    public void setFoto(Drawable foto, ImageView imageView) {
        imageView.setImageDrawable(foto);
        this.foto = foto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipoOferta() {
        return tipoOferta;
    }

    public void setTipoOferta(String tipoOferta) {
        this.tipoOferta = tipoOferta;
    }

    public String getTipoInmueble() {
        return tipoInmueble;
    }

    public void setTipoInmueble(String tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public String getPropiedadPublicacion() {
        return propiedadPublicacion;
    }

    public void setPropiedadPublicacion(String propiedadPublicacion) {
        this.propiedadPublicacion = propiedadPublicacion;
    }
}
