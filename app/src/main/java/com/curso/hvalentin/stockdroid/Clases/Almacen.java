package com.curso.hvalentin.stockdroid.Clases;

/**
 * Created by Héctor Valentín Úbeda on 25/05/2015.
 */
public class Almacen {
    private String Codigo="";
    private String Nombre="";
    private String Descripcion="";
    private String Creador="";

    public Almacen() {

    }

    public Almacen(String codigo, String nombre, String descripcion, String creador) {
        Codigo = codigo;
        Nombre = nombre;
        Descripcion = descripcion;
        Creador = creador;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public void setCreador(String creador) {
        Creador = creador;
    }

    public String getCodigo() {

        return Codigo;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public String getCreador() {
        return Creador;
    }
}
