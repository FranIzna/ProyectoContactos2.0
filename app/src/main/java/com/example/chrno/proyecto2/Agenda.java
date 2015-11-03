package com.example.chrno.proyecto2;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Chrno on 11/10/2015.
 */
public class Agenda {
    private List<Contacto> agenda;

    public Agenda(Context c){//Constructor de mi clase agenda en el que meto los contactos de mi movil en una lista
        agenda=getListaContactos(c);

        for(Contacto aux:agenda)//recorro todos los contactos de mi agenda
            aux.setNumeros(Agenda.getListaTelefonos(c, aux.getId()));//con un set le meto al contacto el numero de telefono

    }
//    public List getListaNum(Context c, long id){//devuelve una lista de numeros de un contacto que buscamos por su id
//        return getListaTelefonos(c,id);
//    }

    public Contacto getContacto(int pos){//devuelve un contacto segun su posicion en el array
        return (Contacto)agenda.get(pos);
    }
    public void setContacto(Contacto aux){
        agenda.add(aux);
    }
    public int getUltId(){
        return agenda.size();
    }
    public void setAgenda(List aux){
        agenda=aux;
    }
    public List getAgenda(){
        return agenda;
    }
    @Override
    public String toString(){//devuelvo una cadena con todos los datos de la agenda
        String s="";
        for(Object aux:agenda)
            s+=aux.toString();
        return s;
    }
    public void ordenar(){
        Collections.sort(agenda);
    }

    public void edita(Contacto aux,int pos){
        System.out.println("ID REMOVE " + aux.getId());
        agenda.remove(pos);
        agenda.add(aux);
    }
    public void ordenaInverso(){
        Collections.sort(agenda, new ComparatorNombre());
    }
    public static List<Contacto> getListaContactos(Context contexto){

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Contacto contacto;
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indiceNombre = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        List<Contacto> lista = new ArrayList<>();


        while(cursor.moveToNext()){
            contacto = new Contacto();
            contacto.setId((int) cursor.getLong(indiceId));
            contacto.setNombre(cursor.getString(indiceNombre));
            lista.add(contacto);
        }
        return lista;
    }
    public static List<String> getListaTelefonos(Context contexto, long id){
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos[] = new String[]{id+""};
        String orden = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        List<String> lista = new ArrayList<>();
        String numero;
        while(cursor.moveToNext()){
            numero = cursor.getString(indiceNumero);
//            System.out.println("NUM "+numero);
            lista.add(numero);
        }
        return lista;
    }

}
