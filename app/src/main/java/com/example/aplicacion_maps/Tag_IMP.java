package com.example.aplicacion_maps;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class Tag_IMP implements GoogleMap.InfoWindowAdapter{

    View view;
    Context context;
    String horarios_texto ;


    public Tag_IMP(Context context, View view)
    {
        this.context=context;
        this.view= view;
        horarios_texto="";
    }


    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) { //sd

        init_Var(marker);
        return view;
    }


    public void init_Var(Marker marker)
    {
        horarios_texto="";
        DATA_API dat_req = (DATA_API) marker.getTag();
        TextView nombre = view.findViewById(R.id.Nombre);
        TextView ubicacion = view.findViewById(R.id.Ubicacion);
        TextView horarios = view.findViewById(R.id.Horarios);

        nombre.setText(dat_req.getNombre());
        ubicacion.setText(dat_req.getUbicacion());
        img_glide_init(dat_req.getIcono());
        for (int x = 0; x < dat_req.getHorario().size(); x++)
            horarios_texto += dat_req.getHorario().get(x) + "\n";
        horarios.setText(horarios_texto);
    }



    public void img_glide_init(String url)
    {
        ImageView imageView = (ImageView) view.findViewById(R.id.imgAvatar);
        Glide.with(context).load(url).into(imageView);
    }



}
