package com.example.admin.calendarioestudiante;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.admin.calendarioestudiante.model.Curso;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.util.ArrayList;


public class cursosEstudiante extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Curso> listaCursosE;
    private adaptadorCursosE adaptadorCursosE;
    private DatabaseReference referencia;
    String correo;


    private static final String TAG = cursosEstudiante.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos_estudiante);

        listView = findViewById(R.id.lista);

        correo = getIntent().getStringExtra("correo");

        listaCursosE = new ArrayList<>();

        cursosEstudiantee(correo);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(cursosEstudiante.this, visualizarEjercicios.class);
                intent.putExtra("codigo",listaCursosE.get(position).getCodigo());
                intent.putExtra("periodo", String.valueOf(listaCursosE.get(position).getPeriodo()));
                intent.putExtra("anno", String.valueOf(listaCursosE.get(position).getAnno()));
                startActivity(intent);
            }
        });

        adaptadorCursosE = new adaptadorCursosE(cursosEstudiante.this, R.layout.single_row, listaCursosE);
        listView.setAdapter(adaptadorCursosE);

    }

    //Metodo para extraer los cursos por estudiante
    public void cursosEstudiantee(String correo){

        referencia = FirebaseDatabase.getInstance().getReference("usuarios");
        Query consulta = referencia.orderByChild("correo").equalTo(correo);

        consulta.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DataSnapshot lista = dataSnapshot.child("cursos");
                for (DataSnapshot cursos: lista.getChildren()){
                    Curso c = cursos.getValue(Curso.class);
                    listaCursosE.add(c);
                }
                adaptadorCursosE.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                adaptadorCursosE.notifyDataSetChanged();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:

                Intent intent = new Intent(cursosEstudiante.this, cursosDisponibles.class);
                intent.putExtra("correo", correo);
                startActivity(intent);

                break;
        }

        return true;
    }


}
