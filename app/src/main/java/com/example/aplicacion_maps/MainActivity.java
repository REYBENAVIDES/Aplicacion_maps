package com.example.aplicacion_maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.example.aplicacion_maps.WebService.WebService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//EDWIN EDUARDO REY BENAVIDES
public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener{

    GoogleMap mapa;
    DATA_API data_api;
    List<DATA_API> lst_data_api;

    String direccion;
    List<String> horario;
    List<String> lista_de_lugares;

    static Double latitud_uteq=-1.012487;
    static Double longitud_uteq=-79.46953209871774;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        data_api=new DATA_API();
        lst_data_api=new ArrayList<>();

        lista_de_lugares=new ArrayList<>();
        lista_de_lugares.add("store");
        lista_de_lugares.add("restaurant");
        lista_de_lugares.add("lodging");
    }



    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        mapa.clear();
        mapa.addMarker(new MarkerOptions().position(latLng).title("REY"));
        get_resul_apis_google_maps(latLng);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.getUiSettings().setZoomControlsEnabled(true);
        mapa.setOnMapClickListener(this);

        mapa.setInfoWindowAdapter(new Tag_IMP(MainActivity.this, LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_window, null)));

        LatLng posicion_inicial = new LatLng(latitud_uteq, longitud_uteq);
        CameraPosition camPos = new CameraPosition.Builder()
                .target(posicion_inicial)
                .zoom(16)
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        mapa.animateCamera(camUpd3);
    }


    public void get_resul_apis_google_maps(LatLng ln)
    {
        Thread task_req=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    for(int x=0;x<lista_de_lugares.size();x++) {

                        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + ln.latitude + "," + ln.longitude + "&radius=300&type="+lista_de_lugares.get(x)+"&key=AIzaSyAZmpF3k0bcm-3c-f_0feLZQZRwYu-gdr0";

                        web_service_impl web_service_class = new web_service_impl();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                webservice(web_service_class,url);
                            }
                        });
                        while (web_service_class.getRespuesta().equals("0"))
                            Thread.sleep(1000);
                        
                        List<DATA_API> principal=new ArrayList<>();

                        /************************************************/

                        JSONObject jsonprincipal=new JSONObject(web_service_class.getRespuesta());
                        JSONArray JSONA=jsonprincipal.getJSONArray("results");
                        for(Integer cc=0;cc<JSONA.length();cc++)
                        {
                            DATA_API datos=new DATA_API();
                            JSONObject jsonobject_geometry=JSONA.getJSONObject(cc).getJSONObject("geometry").getJSONObject("location");
                            datos.setLocalizacion(new LatLng(jsonobject_geometry.getDouble("lat"),jsonobject_geometry.getDouble("lng")));
                            datos.setNombre(JSONA.getJSONObject(cc).getString("name"));
                            datos.setPlace_id(JSONA.getJSONObject(cc).getString("place_id"));
                            JSONObject jsonobject_obtain=JSONA.getJSONObject(cc);
                            if(jsonobject_obtain.has("photos"))
                            {
                                JSONArray fotos=jsonobject_obtain.getJSONArray("photos");
                                String ref="";
                                if(fotos.length()>0)
                                    ref=fotos.getJSONObject(0).getString("photo_reference");
                                String urlimg="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference="+ref+"&key=AIzaSyAZmpF3k0bcm-3c-f_0feLZQZRwYu-gdr0";
                                datos.setIcono(urlimg);
                            }
                            principal.add(datos);
                        }

                        /************************************************/

                        for(int w=0;w<principal.size();w++) {
                            String url1 = "https://maps.googleapis.com/maps/api/place/details/json?place_id="+principal.get(w).getPlace_id()+"&key=AIzaSyAZmpF3k0bcm-3c-f_0feLZQZRwYu-gdr0";
                            web_service_impl serv1 = new web_service_impl();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    webservice(serv1,url1);
                                }
                            });
                            
                            while (serv1.getRespuesta().equals("0"))
                                Thread.sleep(1000);
                            JSONObject response_req = new JSONObject(serv1.getRespuesta());
                            JSONObject det = response_req.getJSONObject("result");
                            direccion = det.getString("formatted_address");
                            horario = new ArrayList<>();
                            if (det.has("current_opening_hours")) {

                                JSONArray ARRAY = det.getJSONObject("current_opening_hours").getJSONArray("weekday_text");
                                for (int n = 0; n < ARRAY.length(); n++)
                                    horario.add(ARRAY.getString(n));
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(DATA_API DAT : principal) {
                                    DAT.setHorario(horario);
                                    DAT.setUbicacion(direccion);
                                    mapa.addMarker(new MarkerOptions().position(DAT.getLocalizacion())).setTag(DAT);
                                }
                            }
                        });
                    }
                }catch (Exception ex){
                    Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        task_req.start();
    }


    public void webservice(web_service_impl servicio,String url)
    {
        Map<String, String> datos = new HashMap<String, String>();
        WebService ws = new WebService(
                url,
                datos, MainActivity.this, servicio);
        ws.execute("GET");
    }


}