package com.ugb.controlesbasicos20;

import java.io.Serializable;

public class ClassBalance {
    private String user;
    private String compra;
    private String venta;

    public ClassBalance(String user, String compra, String venta) {
        this.user = user;
        this.compra = compra;
        this.venta = venta;
    }

    public String getUser() {
        return user;
    }

    public String getCompra() {
        return compra;
    }

    public String getVenta() {
        return venta;
    }
}
