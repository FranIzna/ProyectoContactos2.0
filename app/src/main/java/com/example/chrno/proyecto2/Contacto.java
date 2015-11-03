package com.example.chrno.proyecto2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2dam on 07/10/2015.
 */
public class Contacto implements Serializable,Comparable<Contacto>/*,Parcelable*/{
    private long id;
    private String nombre;
    private List<String> num;


    protected Contacto(Parcel in) {
        id = in.readLong();
        nombre = in.readString();
        num = in.createStringArrayList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contacto contacto = (Contacto) o;

        return id == contacto.id;

    }

    public Contacto(){}
    public Contacto(long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    public Contacto(long id, String nombre,List<String> num) {
        this.id = id;
        this.num = num;
        this.nombre = nombre;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNum() {
        return num.get(0);
    }
    public String getNum(int pos) {
        return num.get(pos);
    }
    public List<String> getArrayNum(){
        return this.num;
    }
    public int getSize(){
        return num.size();
    }
    public String getNumeros() {
        String s="";
            for(String a:num)
                s+=a+"\n";
        return s;
    }
    public void setNum(String num){
        this.num.add(num);
    }
    public void setNumeros(List<String> num) {
        this.num=num;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "id=" + id +
                ", num=" + num +
                ", nombre='" + nombre + '\'' +
                '}';
    }
    @Override
    public int compareTo(Contacto contacto) {
        int r=this.nombre.compareToIgnoreCase(contacto.nombre);
        if(r==0)
            r=(int) (this.id-contacto.id);
        return r;
    }


//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeLong(id);
//        dest.writeString(nombre);
//        dest.writeList(num);
//    }
//
//    public static final Creator<Contacto> CREATOR = new Creator<Contacto>() {
//        @Override
//        public Contacto createFromParcel(Parcel in) {
//            return new Contacto(in);
//        }
//
//        @Override
//        public Contacto[] newArray(int size) {
//            return new Contacto[size];
//        }
//    };
}

