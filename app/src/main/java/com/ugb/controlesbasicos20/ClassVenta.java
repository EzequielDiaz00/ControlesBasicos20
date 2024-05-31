package com.ugb.controlesbasicos20;

import java.io.Serializable;

public class ClassVenta {
    private String user;
    private String codigo;
    private String nombre;
    private String marca;
    private String precio;
    private String foto;
    private String fecha;
    private String cantidad;
    private String cliente;
    private String totalVent;

    public ClassVenta(String user, String codigo, String nombre, String marca, String precio, String foto, String fecha, String cantidad, String cliente, String totalVent) {
        this.user = user;
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.precio = precio;
        this.foto = foto;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.cliente = cliente;
        this.totalVent = totalVent;
    }

    public String getUser() {
        return user;
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

    public String getPrecio() {
        return precio;
    }

    public String getFoto() {
        return foto;
    }

    public String getFecha() {
        return fecha;
    }

    public String getCantidad() {
        return cantidad;
    }

    public String getCliente() {
        return cliente;
    }

    public String getTotalVent() {
        return totalVent;
    }

}
