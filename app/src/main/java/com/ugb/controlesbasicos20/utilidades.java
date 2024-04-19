package com.ugb.controlesbasicos20;

import java.util.Base64;

public class utilidades {
    static String url_consulta = "http://192.168.83.106:5984/ezequiel/_design/diego/_view/diego";
    static String url_mto = "http://192.168.83.106:5984/ezequiel";
    static String user="admin";
    static String passwd="hack200";
    static String credencialesCodificadas = Base64.getEncoder().encodeToString((user+":"+passwd).getBytes());
    public String generarIdUnico(){
        return java.util.UUID.randomUUID().toString();
    }
}
