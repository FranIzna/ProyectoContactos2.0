package com.example.chrno.proyecto2;

import android.annotation.TargetApi;
import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.xmlpull.v1.XmlPullParserException;
import java.io.*;
import java.util.*;

public class Principal extends AppCompatActivity {
    private ListView lv;
    private ClaseAdaptador cl;
    private List<Contacto> agenda;//creo un arraylist que llenare con los contactos y mas tarde le pasare a la clase adaptador
    private static final int EDITAR=0,ANADIR=1;
    private TextView telf2,telf3,telf4,fechaSincro;
    private File copiaIncremental,copiaTotal;
    //el archivo copiaPrograma es con el que trabajamos en la app(cuando la cierro guardo los datos, y cuando la abro, si existe, leo los datos)
    //y la copiaTotal es la copia de seguridad que el usuario puede crear con una opcion, o puede volcar a la copiaPrograma
    private final String nombreIncremental ="incremental.xml",nombreTotal="total.xml";
    private Agenda a;
    private Ficheros f=new Ficheros();
    private ImageView guarda,lee;
    private Intent i;
    private SharedPreferences preferencia;
    private CheckBox sincro;
    private SharedPreferences.Editor ed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        try {
            init();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        telf2=(TextView) findViewById(R.id.edTelefono2);
        telf3=(TextView) findViewById(R.id.edTelefono3);
        telf4=(TextView) findViewById(R.id.edTelefono4);
        guarda=(ImageView) findViewById(R.id.ivGuarda);
        lee=(ImageView) findViewById(R.id.ivLee);
        sincro=(CheckBox)findViewById(R.id.checkBox);
        fechaSincro=(TextView) findViewById(R.id.tvFecha);

        if (preferencia.getBoolean("sincro",false)){
            sincro.setChecked(true);
            fechaSincro.setVisibility(View.VISIBLE);
            fechaSincro.setText(/*this.getText(R.string.ultFechaSincro)
                    +": "+*/preferencia.getString("fecha",""));
        } else {
            fechaSincro.setVisibility(View.INVISIBLE);
            sincro.setChecked(false);
        }

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {//creamos nuestro menu contextual
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contextual, menu);
    }
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo vistainfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int posicion = vistainfo.position;//cogemos la posicion del elemento pulsado en la vista
        switch(item.getItemId()){//acciones que hara nuestro menu contextual
            case R.id.action_edit:
                editar(posicion);
                return true;
            case R.id.action_remove:
                borrar(posicion);
                return true;
            default: return super.onContextItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add){
            añadir();
            return true;
        }
        if(id==R.id.action_ordena_asc) {
            Collections.sort(agenda);
            cl.notifyDataSetChanged();
            return true;
        }
        if(id==R.id.action_ordena_desc) {
            Collections.sort(agenda, new ComparatorNombre());
            cl.notifyDataSetChanged();
            return true;
        }
        if(id==R.id.leeCopiaTotal){
            try {
                agenda=f.leer(this,nombreTotal);
                guarda.setVisibility(View.INVISIBLE);
                construyeAdaptador(agenda);
                actualizarYordena();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        if(id==R.id.creaCopiaTotal) {
            try {
                f.escribir(this, agenda, nombreTotal);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
//        if(id==R.id.leeCopiaIncremental){
//            try {
//                agenda=f.leer(this,nombreIncremental);
//                construyeAdaptador(agenda);
//                guarda.setVisibility(View.VISIBLE);
//
//                actualizarYordena();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (XmlPullParserException e) {
//                e.printStackTrace();
//            }
//        }
//        if(id==R.id.guardaIncremental) {
//            try {
//                f.escribir(this, agenda, nombreIncremental);
//                actualizarYordena();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        if(id==R.id.sincronizaDatos) {
            System.out.println("EN CONSTRUCCION");
            try {
                sincroniza();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() throws XmlPullParserException, IOException {
        preferencia=getSharedPreferences("MiPref",this.MODE_PRIVATE);//crea grupo pref compartida

        lv= (ListView) findViewById(R.id.lvContactos);
        a=new Agenda(this);
        agenda=new ArrayList<>();
        creaFichero();
        construyeAdaptador(agenda);
    }
    public void construyeAdaptador(List<Contacto> aux){
        cl = new ClaseAdaptador(this, R.layout.elemento_lista,aux);//declaro el adaptador
        lv.setAdapter(cl);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                llamar(posicion);
            }
        });
        registerForContextMenu(lv);//registramos nuestro menu contextual
    }

    public void creaFichero() throws IOException, XmlPullParserException {

        copiaIncremental=new File(getExternalFilesDir(null),nombreIncremental);
        copiaTotal=new File(getExternalFilesDir(null),nombreTotal);
        SharedPreferences.Editor ed=preferencia.edit();
        if (copiaIncremental.exists()) {//si existe una copia incremental

            if(preferencia.getBoolean("sincro",false))//si tenemos la sincronizacion automatica activada
                agenda=mezcla();
            else //si no tenemos la sincro activada
                agenda = f.leer(this, nombreIncremental);//meto en mi agenda principal los datos del xml incremental

        }else{//si no existe una copia incremental
            agenda=a.getAgenda();//lleno mi agenda con los datos del telefono
            copiaIncremental= new File(getExternalFilesDir(null), nombreIncremental);//creo la incremental
            copiaTotal=new File(getExternalFilesDir(null),nombreTotal);//creo la copia total
            f.escribir(this,agenda,nombreTotal);//como no hay copia de seguridad inicial la creo
        }
    }

    public void añadir(){
        i = new Intent(this, FormAdd.class);
        startActivityForResult(i, ANADIR);
    }
    public void editar(int pos){
        i = new Intent(this, FormEdit.class);
        Bundle b=new Bundle();
//        b.putInt("id", pos);
        Contacto aux= agenda.get(pos);
        System.out.println("******* " + aux.getNumeros());
        b.putSerializable("aux", aux);
        i.putExtras(b);

        startActivityForResult(i, EDITAR);

//       actualiza();
    }
    public void borrar(final int pos){
        String s=this.getString(R.string.confBorrar)+agenda.get(pos).getNombre()+this.getString(R.string.interrogacion);
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(this.getString(R.string.importante));
        dialogo1.setMessage(s);
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton(this.getString(R.string.cancelar), null);
        dialogo1.setPositiveButton(this.getString(R.string.confirmar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                aceptar(pos);
            }
        });
        dialogo1.show();
    }
    public void actualiza(){
        cl.notifyDataSetChanged();
    }
    public void actualizarYordena(){
        Collections.sort(agenda);
//        Agenda.ordenar();
        cl.notifyDataSetChanged();
    }
    public void Tostada(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    public void aceptar(int pos) {
        Tostada(this.getString(R.string.borrasteA) + agenda.get(pos).getNombre());
        agenda.remove(pos);
        actualiza();
    }

    public int getPos(long id){
        System.out.println("ID GET POS: " + id);
        for (int i=0;i<agenda.size();i++){
            System.out.println("ARRAY GET POS "+agenda.get(i).toString());
            if(agenda.get(i).getId()==(id))
                return i;
        }
        return -1;
    }
    public void edita(Contacto aux){
        Contacto edi=agenda.get(getPos(aux.getId()));
        edi.setNombre(aux.getNombre());
        edi.setNumeros(aux.getArrayNum());
    }
    /******** BOTONES ********/
    public void add(View v){
        añadir();
    }
    public void asc(View v) {
        Collections.sort(agenda);
        cl.notifyDataSetChanged();
    }
    public void desc(View v) {
        Collections.sort(agenda, new ComparatorNombre());
        cl.notifyDataSetChanged();
    }
    public void plus(View v){
        cl.plus(v);
    }
    public void guardaIncremental(View v) throws IOException {
        f.escribir(this, agenda, nombreIncremental);
        lee.setVisibility(View.VISIBLE);
    }
    public void leeIncremental(View v) throws IOException, XmlPullParserException {
        agenda=f.leer(this,nombreIncremental);
        construyeAdaptador(agenda);
        guarda.setVisibility(View.VISIBLE);
    }

    public void checkSincro(View v){
    ed=preferencia.edit();
        if(sincro.isChecked()){
            System.out.println("Sincro on");
            ed.putBoolean("sincro", true);
            ed.commit();
        }else{
            System.out.println("Sincro off");
            ed.putBoolean("sincro", false);
            ed.commit();
        }
    }
    /*****************/
    public void llamar(int pos){
        String s=agenda.get(pos).getNum(0);
        Uri numero = Uri.parse( "tel:" + s.toString() );
        Intent i = new Intent(Intent.ACTION_CALL, numero);
        startActivity(i);
    }
    public List<Contacto> mezcla() throws IOException, XmlPullParserException {//metodo que mezcla mi xml incremental con los datos del telefono
        List<Contacto> telefono=a.getAgenda(),
                incremental=f.leer(this,nombreIncremental);
        List <String>x,y;
        List<Contacto> result=new ArrayList<>();
        result.addAll(telefono);

        for(int z=0;z<incremental.size();z++) {
            int cont1=0;
            for(int q=0;q<telefono.size();q++) {
                if(incremental.get(z).getNombre().equalsIgnoreCase(telefono.get(q).getNombre())){
                    x=telefono.get(q).getArrayNum();
                    y=incremental.get(z).getArrayNum();

                    for (int j = 0; j < y.size(); j++) {
                        int cont2 = 0;
                        for (int i = 0; i < x.size(); i++)
                            if (!x.get(i).equals(y.get(j)))
                                cont2++;

                        if (cont2 >=x.size())
                            x.add(y.get(j));
                    }

                }else
                    cont1++;
            }
            if(cont1>=telefono.size())
                result.add(incremental.get(z));
        }

        return result;
    }
    GregorianCalendar fecha;
    public void sincroniza() throws IOException, XmlPullParserException {
        ed= preferencia.edit();
        agenda=mezcla();//meto en mi agenda principal los datos resultantes de la mezcla del incremental+telefono
        ed.putBoolean("sincro", false);
        ed.commit();

        fecha=new GregorianCalendar();
        String s=fecha.getTime().toString();
        ed.putString("fecha",s);
        ed.commit();

        construyeAdaptador(agenda);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK && requestCode == EDITAR){
            Contacto aux= (Contacto) data.getSerializableExtra("aux");
            edita(aux);
            actualizarYordena();
        }  else if(resultCode == Activity.RESULT_OK && requestCode == ANADIR){
            Contacto aux= (Contacto) data.getSerializableExtra("aux");
            agenda.add(aux);
            actualizarYordena();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
//        List<Contacto> l=new ArrayList<>();
//        for(int i=0;0<lv.getScrollBarSize();i++)
//            l.add((Contacto) lv.getAdapter().getItem(i));
//
////        System.out.println("********** " + lv.getAdapter().getItem(0));
//        outState.putSerializable("entrada", (Serializable) l);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        List<Contacto> l=new ArrayList<>();
//        l= (List<Contacto>) savedInstanceState.getSerializable("entrada");
//        for(int i=0;0<lv.getScrollBarSize();i++)
//            l.add((Contacto) lv.set));
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            f.escribir(this, agenda,nombreIncremental);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
