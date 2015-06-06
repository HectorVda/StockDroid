package com.curso.hvalentin.stockdroid.Clases;

/**
 * Created by Sene on 28/05/2015.
 */
public class Estanteria {
    private String Codigo="";
    private String CodigoAlmacen="";
    private String Nombre="";
    private String Descripcion="";

    public Estanteria(String codigo, String codigoAlmacen, String nombre, String descripcion) {
        Codigo = codigo;
        CodigoAlmacen = codigoAlmacen;
        Nombre = nombre;
        Descripcion = descripcion;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public void setCodigoAlmacen(String codigoAlmacen) {
        CodigoAlmacen = codigoAlmacen;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getCodigoAlmacen() {
        return CodigoAlmacen;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public Estanteria() {
    }
}
