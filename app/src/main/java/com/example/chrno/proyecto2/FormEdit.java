package com.example.chrno.proyecto2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chrno on 17/10/2015.
 */
public class FormEdit extends AppCompatActivity {
    private int id=-1;
    private Contacto aux;
    private TextView nombre,telf1;
    private TextView telf2,telf3,telf4;
    private Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);
        i=this.getIntent();
        Bundle b=i.getExtras();
//        id=b.getInt("id");
        aux= (Contacto) b.getSerializable("aux");
        System.out.println("----- "+aux.getNumeros());

        nombre=(TextView) findViewById(R.id.edNombre);
        nombre.setText(aux.getNombre());
        telf1=(TextView) findViewById(R.id.edTelefono1);
//        telf1.setText(aux.getNum());
        telf1.setHint(this.getString(R.string.min));

        telf2=(TextView) findViewById(R.id.edTelefono2);
            telf2.setHint(this.getString(R.string.opcional));
        telf3=(TextView) findViewById(R.id.edTelefono3);
            telf3.setHint(this.getString(R.string.opcional));
        telf4=(TextView) findViewById(R.id.edTelefono4);
            telf4.setHint(this.getString(R.string.opcional));

        int tam=aux.getSize();
        telf1.setText(aux.getNum(0));
//        if(tam>0)
//            telf1.setText(aux.getNum(0));
        if(tam>1)
            telf2.setText(aux.getNum(1));
        if(tam>2)
            telf3.setText(aux.getNum(2));
        if(tam>3)
            telf4.setText(aux.getNum(3));
//        if(tam>4)
//            telf2.setText(aux.getNum(4));

        }
    public void add(View v){
        String nom,tlf1,tlf2,tlf3,tlf4;
        List<String> nums=new ArrayList<>();

        nom=nombre.getText().toString();
        tlf1=telf1.getText().toString();
        if(!nom.isEmpty() && !tlf1.isEmpty()){
            nums.add(tlf1);
            if(telf2.length()>0){
                tlf2=telf2.getText().toString();
                nums.add(tlf2);
            } if(telf3.length()>0){
                tlf3=telf3.getText().toString();
                nums.add(tlf3);
            } if(telf4.length()>0){
                tlf4=telf4.getText().toString();
                nums.add(tlf4);
            }

            aux.setNombre(nom);
            aux.setNumeros(nums);
            Bundle b=new Bundle();
            b.putSerializable("aux",aux);
            i.putExtras(b);
            setResult(Activity.RESULT_OK, i);

//            Bundle b=new Bundle();
//            b.putParcelable("aux", aux);

            //
//            Principal.actualizar();
            finish();
        }else Toast.makeText(this, R.string.campoVacio,
                Toast.LENGTH_SHORT).show();
    }
    public void cancelar(View v){
        finish();
    }

}