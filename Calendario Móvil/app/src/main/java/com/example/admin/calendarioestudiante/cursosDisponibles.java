package com.example.admin.calendarioestudiante;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.admin.calendarioestudiante.model.Curso;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class cursosDisponibles extends AppCompatActivity {

    ListView listView;
    ArrayList<Curso> listaCursosDisponibles = new ArrayList<>();
    ArrayList<Curso> listaEstudiante = new ArrayList<>();
    adaptadorCursosDisponibles adaptadorCursos;
    DatabaseReference referencia;
    String correo;

    private static final String TAG = cursosDisponibles.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos_disponibles);

        listView = findViewById(R.id.lista2);

        correo = getIntent().getStringExtra("correo");

        cursosEstudiante(correo);

        cursosDisponibles();

        adaptadorCursos = new adaptadorCursosDisponibles(cursosDisponibles.this, R.layout.single_row2, listaCursosDisponibles);
        listView.setAdapter(adaptadorCursos);

    }

    //Metodo para extraer los cursos disponibles
    public void cursosDisponibles() {

        referencia = FirebaseDatabase.getInstance().getReference("cursos");

        referencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot cur : dataSnapshot.getChildren()) {
                    Curso c = new Curso();
                    c = cur.getValue(Curso.class);
                    listaCursosDisponibles.add(c);
                }


                for (int index = 0; index < listaEstudiante.size(); index++) {

                    if (listaCursosDisponibles.toString().contains(listaEstudiante.get(index).toString())) {
                        Iterator<Curso> index2 = listaCursosDisponibles.iterator();
                        while (index2.hasNext()) {
                            Curso c = index2.next();
                            if (c.toString().equals(listaEstudiante.get(index).toString())) {
                                index2.remove();
                            }
                        }

                    }
                }
                adaptadorCursos.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Ha fallado la lectura de los datos", databaseError.toException());
            }

        });

    }

    //Metodo para extraer los cursos por estudiante
    public void cursosEstudiante(String correo) {

        referencia = FirebaseDatabase.getInstance().getReference("usuarios");
        Query buscar = referencia.orderByChild("correo").equalTo(correo);

        buscar.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                DataSnapshot lista = dataSnapshot.child("cursos");
                for (DataSnapshot cursos : lista.getChildren()) {
                    Curso c = cursos.getValue(Curso.class);
                    listaEstudiante.add(c);
                }

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

