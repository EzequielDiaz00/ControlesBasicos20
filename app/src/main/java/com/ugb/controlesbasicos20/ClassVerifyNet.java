package com.ugb.controlesbasicos20;

import android.app.Activity;
import android.widget.Toast;

public class ClassVerifyNet {
    private Activity activity;
    public boolean verifyNet;

    public ClassVerifyNet(Activity activity) {
        this.activity = activity;
    }

    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();
            boolean reachable = (val == 0);
            verifyNet = reachable;
            Toast.makeText(activity, "Valor conexion: " + verifyNet, Toast.LENGTH_SHORT).show();
            return reachable;

        } catch (Exception e) {
            Toast.makeText(activity, "No hay conexion a internet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }
}
