package com.softtim.mobilityusuario;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Anahi on 23/05/2017.
 */
class AdaptadorEmergencias extends ArrayAdapter<Panico>{
    private Context context;

    public AdaptadorEmergencias(Context contextx, List<Panico> panico) {
        super(contextx, R.layout.row_emergencias, panico);
        context=contextx;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.row_emergencias, null);

        TextView nombreC = (TextView) item.findViewById(R.id.egNombre);
        TextView fechaC = (TextView) item.findViewById(R.id.egFecha);

        final Panico panicoItem = getItem(position);

        if (panicoItem != null) {
            nombreC.setText(panicoItem.getNombre());

            fechaC.setText(panicoItem.getFecha());
        }

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapaEmergencia.class);
                intent.putExtra("panic", panicoItem);

                context.startActivity(intent);
            }
        });
        return item;
    }

}
