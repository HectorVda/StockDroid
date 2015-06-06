package com.curso.hvalentin.stockdroid.Clases;

/**
 * Created by Sene on 28/05/2015.
 */
public class Item {
    private String Codigo="";
    private String CodigoEstanteria = "";
    private float Cantidad=0;
    private String Nombre="";
    private String Descripcion="";


    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public void setCodigoEstanteria(String codigoEstanteria) {
        CodigoEstanteria = codigoEstanteria;
    }

    public void setCantidad(float cantidad) {
        Cantidad = cantidad;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getCodigo() {
        return Codigo;
    }

    public String getCodigoEstanteria() {
        return CodigoEstanteria;
    }

    public float getCantidad() {
        return Cantidad;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public Item() {
    }

    public Item(String codigo, String codigoEstanteria, float cantidad, String nombre, String descripcion) {
        Codigo = codigo;
        CodigoEstanteria = codigoEstanteria;
        Cantidad = cantidad;
        Nombre = nombre;
        Descripcion = descripcion;
    }
}
