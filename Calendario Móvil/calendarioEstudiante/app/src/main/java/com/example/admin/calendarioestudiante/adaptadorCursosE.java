package com.example.admin.calendarioestudiante;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.calendarioestudiante.model.Curso;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


public class adaptadorCursosE extends ArrayAdapter<Curso> {

    Context context;
    int layout;
    ArrayList<Curso> listaCursosE;
    DatabaseReference referencia;
    String getId, getIdCurso;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser usuario = auth.getCurrentUser();
    private String correo = usuario.getEmail();

    private static final String TAG = adaptadorCursosE.class.getSimpleName();
    private static final String CHANNEL_ID = "1";

    public adaptadorCursosE(Context context, int layout, ArrayList<Curso> datos) {
        super(context, layout, datos);
        this.layout = layout;
        this.context = context;
        this.listaCursosE = datos;
    }

    @NonNull
    @Override
    public View getView(final int position,View convertView,ViewGroup parent) {

        View view = convertView;
        final cursoHolder holder;
        obtenerID();

        if (view == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layout, parent, false);

            holder = new cursoHolder();

            holder.text = view.findViewById(R.id.contenido);
            holder.notificar = view.findViewById(R.id.notificacion);
            holder.borrar = view.findViewById(R.id.borrar);

            view.setTag(holder);

        } else {
            holder = (cursoHolder) view.getTag();
        }

        final String codigo = listaCursosE.get(position).getCodigo();
        final String nombre = listaCursosE.get(position).getNombre();
        final int periodo = listaCursosE.get(position).getPeriodo();
        final int anno = listaCursosE.get(position).getAnno();


        holder.text.setText(codigo + "\n" + nombre + "\nPeríodo: " + periodo + " - " + anno);

        //boton de notificaciones de los cursos holder.notificacion
        if(listaCursosE.get(position).getNotifica() == 0){
            holder.notificar.setImageResource(R.drawable.notificacion);
        }else{
            holder.notificar.setImageResource(R.drawable.active_bell);
            enviarNotificacion(listaCursosE.get(position).getNombre());
        }

        holder.notificar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                obtenerIDcurso(listaCursosE.get(position).getCodigo(),
                        listaCursosE.get(position).getPeriodo(), listaCursosE.get(position).getAnno());

                if(listaCursosE.get(position).getNotifica() == 0){

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater3 = ((Activity) context).getLayoutInflater();
                    View view1 = inflater3.inflate(R.layout.notifica_act, null);

                    builder.setView(view1)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    holder.notificar.setImageResource(R.drawable.active_bell);
                                    listaCursosE.get(position).setNotifica(1);
                                    cambiarNotificacion(1);
                                    enviarNotificacion(listaCursosE.get(position).getNombre());
                                    Toast.makeText(context, "Notificaciones activadas", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                    AlertDialog d = builder.create();
                    d.show();

                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater4 = ((Activity) context).getLayoutInflater();
                    View view1 = inflater4.inflate(R.layout.notifica_desactivar, null);

                    builder.setView(view1)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    holder.notificar.setImageResource(R.drawable.notificacion);
                                    listaCursosE.get(position).setNotifica(0);
                                    cambiarNotificacion(0);
                                    desactivarNotificacion();
                                    Toast.makeText(context, "Notificaciones desactivadas", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                    AlertDialog d = builder.create();
                    d.show();

                }

            }
        });

        //boton de borrar el curso en la listview
        holder.borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                obtenerIDcurso(listaCursosE.get(position).getCodigo(),
                        listaCursosE.get(position).getPeriodo(), listaCursosE.get(position).getAnno());

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater2 = ((Activity) context).getLayoutInflater();
                View view1 = inflater2.inflate(R.layout.borrar_contenido, null);

                builder.setView(view1)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                eliminarCurso(position);
                                Toast.makeText(getContext(), "Se ha eliminado el curso!", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                AlertDialog d = builder.create();
                d.show();
            }
        });

        return view;
    }


    static class cursoHolder {
        TextView text;
        ImageButton notificar;
        ImageButton borrar;

    }


    public void eliminarCurso(final int posicion){

        obtenerID();
        referencia = FirebaseDatabase.getInstance().getReference("usuarios");

        Query buscar = referencia.orderByChild("correo").equalTo(correo);
        buscar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot data = dataSnapshot.child(getId).child("cursos").child(getIdCurso);

                for (DataSnapshot index : data.getChildren()) {
                    index.getRef().removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Ha fallado la lectura de los datos", databaseError.toException());
            }
        });

        listaCursosE.remove(posicion);
        notifyDataSetChanged();


    }

    public void obtenerIDcurso(final String codigo, final int periodo, final int anno ){

        referencia = FirebaseDatabase.getInstance().getReference("usuarios");

        Query buscar = referencia.orderByChild("correo").equalTo(correo);
        buscar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot data = dataSnapshot.child(getId).child("cursos");

                for (DataSnapshot lista: data.getChildren()){

                    if( lista.child("codigo").getValue().toString().equals(codigo)
                            && lista.child("periodo").getValue().toString().equals(String.valueOf(periodo))
                            &&  lista.child("anno").getValue().toString().equals(String.valueOf(anno))){

                        getIdCurso = lista.getKey();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Ha fallado la lectura de los datos", databaseError.toException());
            }
        });


    }

    public void obtenerID() {

        referencia = FirebaseDatabase.getInstance().getReference("usuarios");
        Query consulta = referencia.orderByChild("correo").equalTo(correo);

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    getId = data.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void cambiarNotificacion(int num){

        referencia = FirebaseDatabase.getInstance().getReference("usuarios");
        referencia.child(getId).child("cursos").child(getIdCurso).child("notifica").setValue(num);

    }


    public void enviarNotificacion(String nombre){

        Intent intent = new Intent(context, broadcastReceiver.class);
        intent.putExtra("curso", nombre);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR)+1);
        calendar.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE)+1);
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND)+1);

        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pending);

    }

    public void desactivarNotificacion(){

        Intent intent = new Intent(context, broadcastReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        pending.cancel();
        alarm.cancel(pending);

    }


}
