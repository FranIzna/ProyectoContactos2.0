package com.example.chrno.proyecto2;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.CallableStatement;
import java.util.List;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.widget.Toast;

/**
 * Created by 2dam on 07/10/2015.
 */
public class ClaseAdaptador extends ArrayAdapter<Contacto> {

    private Context c;
    private int res;
    private List<Contacto> agenda;
    private LayoutInflater i;

    public ClaseAdaptador(Context context, int resource, List<Contacto> agenda) {
        super(context, resource, agenda);
        this.c=context;
        this.res=resource;
        this.agenda=agenda;
        i= (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class GuardaLista{
        public TextView tv1,tv2;
        public ImageView plus;
    }

    public void plus(View v){//metodo que crea un dialogo que muestra los numeros del contacto
        Contacto aux=agenda.get(v.getId());
        String s=c.getString(R.string.numDe)+""+aux.getNombre()+":\n";
        s+=aux.getNumeros();
                Builder dialogo = new AlertDialog.Builder(c);
                dialogo.setMessage(s);
                dialogo.setCancelable(true);
                dialogo.setPositiveButton(R.string.salir,null);
                AlertDialog muestra = dialogo.create();
                muestra.show();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //1
        GuardaLista gv=new GuardaLista();

        if(convertView==null){
            convertView =i.inflate(res,null);
            TextView tv = (TextView)convertView.findViewById(R.id.tv1);
            gv.tv1=tv;
            tv=(TextView) convertView.findViewById(R.id.tv2);
            gv.tv2=tv;
            gv.plus=(ImageView) convertView.findViewById(R.id.imgPlus);
            convertView.setTag(gv);

            gv.plus.setVisibility(View.INVISIBLE);

        }else gv=(GuardaLista) convertView.getTag();

        gv.tv1.setText(agenda.get(position).getNombre());
//        String tlf=""+R.string.telefono;

        gv.tv2.setText(c.getString(R.string.telefono)+": "+ agenda.get(position).getNum());
        gv.plus.setId(position);
        muestraPlus(gv,position);

        return convertView;
    }
    public void muestraPlus(GuardaLista gv,int pos){//metodo en el que pongo visible la imagen si tiene mas de un numero
        int tam=agenda.get(pos).getSize();
        if(tam>1){
            gv.plus.setVisibility(View.VISIBLE);
        }else gv.plus.setVisibility(View.INVISIBLE);


    }
}
