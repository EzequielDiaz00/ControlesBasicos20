package com.ugb.controlesbasicos20;

public class Auto {
    private String ID;
    private String marca;
    private String motor;
    private String chasis;
    private String vin;
    private String combustion;
    private String foto;

    public Auto(String ID, String marca, String motor, String chasis, String vin, String combustion, String foto) {
        this.ID = ID;
        this.marca = marca;
        this.motor = motor;
        this.chasis = chasis;
        this.vin = vin;
        this.combustion = combustion;
        this.foto = foto;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getChasis() {
        return chasis;
    }

    public void setChasis(String chasis) {
        this.chasis = chasis;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getCombustion() {
        return combustion;
    }

    public void setCombustion(String combustion) {
        this.combustion = combustion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

}
