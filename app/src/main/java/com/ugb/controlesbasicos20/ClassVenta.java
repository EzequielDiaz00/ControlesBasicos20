package com.ugb.controlesbasicos20;

import java.io.Serializable;

public class ClassVenta {
    private String user;
    private String codigo;
    private String nombre;
    private String marca;
    private Double precio;
    private String foto;
    private String fecha;
    private Double cantidad;
    private String cliente;
    private Double totalVent;

    public ClassVenta(String user, String codigo, String nombre, String marca, Double precio, String foto, String fecha, Double cantidad, String cliente, Double totalVent) {
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

    public Double getPrecio() {
        return precio;
    }

    public String getFoto() {
        return foto;
    }

    public String getFecha() {
        return fecha;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public String getCliente() {
        return cliente;
    }

    public Double getTotalVent() {
        return totalVent;
    }

}
