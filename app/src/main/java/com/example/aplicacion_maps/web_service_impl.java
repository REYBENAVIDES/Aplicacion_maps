package com.example.aplicacion_maps;

import com.example.aplicacion_maps.WebService.Asynchtask;


import org.json.JSONException;

public class web_service_impl implements Asynchtask {

    public static String estatus="0";
    public static String api_key="AIzaSyAZmpF3k0bcm-3c-f_0feLZQZRwYu-gdr0";


    private String respuesta;

    public String getRespuesta() {
        return respuesta;
    }

    public web_service_impl(){
        this.respuesta=estatus;
    }

    @Override
    public void processFinish(String result) throws JSONException {
        this.respuesta=result;
    }

}
