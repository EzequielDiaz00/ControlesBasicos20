package com.ugb.controlesbasicos20;

public class ClassProductos {
    private String codigo;
    private String nombre;
    private String marca;
    private String descripcion;
    private Double precio;

    public ClassProductos(String codigo, String nombre, String marca, String descripcion, Double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMarca() {
        return marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getPrecio() {
        return precio;
    }
}