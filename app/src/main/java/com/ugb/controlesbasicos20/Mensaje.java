package com.ugb.controlesbasicos20;

public class Mensaje {
    private String contenido;
    private String emisor;

    public Mensaje(String contenido, String emisor) {
        this.contenido = contenido;
        this.emisor = emisor;
    }

    public String getContenido() {
        return contenido;
    }

    public String getEmisor() {
        return emisor;
    }
}
