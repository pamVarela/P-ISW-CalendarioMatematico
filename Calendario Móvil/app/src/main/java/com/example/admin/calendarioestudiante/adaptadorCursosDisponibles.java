package com.example.admin.calendarioestudiante;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class adaptadorCursosDisponibles extends ArrayAdapter<Curso> {

    Context context;
    int layout;
    ArrayList<Curso>listaCursosDisponibles;
    DatabaseReference referencia;
    String getId;
    Long numeroKey;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser usuario = auth.getCurrentUser();
    private String correo = usuario.getEmail();

    private static final String TAG = adaptadorCursosDisponibles.class.getSimpleName();

    //Se hace el adapter para personalizar la listView
    public adaptadorCursosDisponibles(Context context, int layout, ArrayList<Curso>datos) {
        super(context, layout, datos);
        this.layout = layout;
        this.context = context;
        this.listaCursosDisponibles = datos;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        cursoHolder holder = null;

        obtenerID();


        if (view == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layout, parent, false);

            holder = new cursoHolder();

            holder.text = view.findViewById(R.id.contenido2);
            holder.agregar = view.findViewById(R.id.addCursos);

            view.setTag(holder);

        } else {
            holder = (cursoHolder) view.getTag();
        }

        //Se hace llenan los datos en la listaView
        final String codigo = listaCursosDisponibles.get(position).getCodigo();
        final String nombre = listaCursosDisponibles.get(position).getNombre();
        final int periodo = listaCursosDisponibles.get(position).getPeriodo();
        final int anno = listaCursosDisponibles.get(position).getAnno();

        contadorKeysCursosEstudiante();

        holder.text.setText(codigo + "\n" + nombre+ "\nPeríodo: " + periodo + " - " +  anno);
        holder.agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                contadorKeysCursosEstudiante();

                Curso curso = new Curso();
                curso.setCodigo(listaCursosDisponibles.get(position).getCodigo());
                curso.setNombre(listaCursosDisponibles.get(position).getNombre());
                curso.setPeriodo(listaCursosDisponibles.get(position).getPeriodo());
                curso.setAnno(listaCursosDisponibles.get(position).getAnno());

                agregarCurso(curso);
                Toast.makeText(context, "Se ha inscrito al curso !",Toast.LENGTH_SHORT).show();


            }
        });
        return view;
    }

    static class cursoHolder {
        ImageButton agregar;
        TextView text;

    }

    //Metodo para agregar un curso
    public void agregarCurso(Curso Curso){

        if(numeroKey == null){

            String numero = "0";
            numeroKey = Long.parseLong(numero);

        }

        referencia = FirebaseDatabase.getInstance().getReference("usuarios");
        referencia.child(getId).child("cursos").child(String.valueOf(numeroKey+1)).setValue(Curso);

    }
    //Metodo para obtener la ID del usuario
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

                Log.w(TAG, "Ha fallado la lectura de los datos", databaseError.toException());
            }
        });


    }
    //metodo para contar las claves de la clave JSON usuarios
    public void contadorKeysCursosEstudiante() {

        referencia = FirebaseDatabase.getInstance().getReference("usuarios");
        Query consulta = referencia.orderByChild("correo").equalTo(correo);

        consulta.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DataSnapshot data = dataSnapshot.child("cursos");
                numeroKey = data.getChildrenCount();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Ha fallado la lectura de los datos", databaseError.toException());
            }
        });

    }



}