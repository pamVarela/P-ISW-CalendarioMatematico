package com.example.admin.calendarioestudiante;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.admin.calendarioestudiante.model.Tema;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class temas extends AppCompatActivity {

    private String codigo, periodo, anno, keyCurso;
    private Long keysTemas;
    private DatabaseReference reference;
    private EditText nombreTema;
    private ArrayList<Tema> listaTemas;
    private ListView listViewTemas;
    private adaptadorTemas adaptadorTemas;

    private static final String TAG = temas.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temas);

        //Inicializar las variables
        nombreTema = findViewById(R.id.nombreTema);

        codigo = getIntent().getStringExtra("codigo");
        periodo = getIntent().getStringExtra("periodo");
        anno = getIntent().getStringExtra("anno");

        listViewTemas = findViewById(R.id.listaTemass);
        listaTemas = new ArrayList<>();

        cargarTemas();

        listViewTemas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(temas.this, ejercicios.class);
                intent.putExtra("codigo", getIntent().getStringExtra("codigo"));
                intent.putExtra("periodo", getIntent().getStringExtra("periodo"));
                intent.putExtra("anno", getIntent().getStringExtra("anno"));
                intent.putExtra("tema", listaTemas.get(position).getNombre());
                startActivity(intent);
            }
        });



        adaptadorTemas = new adaptadorTemas(temas.this, R.layout.single_row_temas, listaTemas);
        listViewTemas.setAdapter(adaptadorTemas);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main4, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //boton en el actionBar para agregar un tema
        switch (item.getItemId()) {

            case R.id.addTema: {

                getIdCurso();
                getKeysTemas();

                final AlertDialog.Builder builder = new AlertDialog.Builder(temas.this);
                final View view = LayoutInflater.from(temas.this).inflate(R.layout.dialog_agregar_tema, null);

                final EditText nombreT = view.findViewById(R.id.nombreTema);

                builder.setView(view)

                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (nombreT.getText().toString().equals("")) {

                                    Toast.makeText(getApplicationContext(), "Datos incompletos!", Toast.LENGTH_SHORT).show();

                                } else {

                                    reference = FirebaseDatabase.getInstance().getReference("cursos");
                                    Query consulta = reference.orderByChild("codigo").equalTo(codigo);

                                    consulta.addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for(DataSnapshot data : dataSnapshot.getChildren()){

                                                if (data.child("periodo").getValue().toString().equals(periodo)
                                                        && data.child("anno").getValue().toString().equals(anno)) {

                                                    if(data.child("temas").exists()){

                                                        DataSnapshot lista = data.child("temas");
                                                        for (DataSnapshot temas : lista.getChildren()) {

                                                            if (temas.child("nombre").getValue().toString().equals(nombreT.getText().toString())) {
                                                                Toast.makeText(getApplicationContext(), "El tema ya existe, vuelve a intentarlo!", Toast.LENGTH_SHORT).show();
                                                                break;

                                                            } else {

                                                                agregarTema(nombreT.getText().toString());
                                                                Toast.makeText(getApplicationContext(), "Se ha agregado el tema !", Toast.LENGTH_LONG).show();
                                                                break;
                                                            }
                                                        }
                                                    } else{
                                                        agregarTema(nombreT.getText().toString());
                                                        Toast.makeText(getApplicationContext(), "Se ha agregado el tema !", Toast.LENGTH_LONG).show();
                                                        break;
                                                    }
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.w(TAG, "Ha fallado la lectura de los datos", databaseError.toException());
                                        }
                                    });
                                }
                            }

                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                final AlertDialog d = builder.create();
                d.show();
            }

            break;
        }
        return true;
    }

    //metodo para cargar Temas desde la BD Firebase
    public void cargarTemas() {

        reference = FirebaseDatabase.getInstance().getReference("cursos");
        Query consulta = reference.orderByChild("codigo").equalTo(codigo);

        consulta.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.child("periodo").getValue().toString().equals(periodo)
                        && dataSnapshot.child("anno").getValue().toString().equals(anno)) {

                    if(dataSnapshot.child("temas").exists()){

                        DataSnapshot lista = dataSnapshot.child("temas");
                        for (DataSnapshot temas : lista.getChildren()) {
                            Tema tem = temas.getValue(Tema.class);
                            if (tem != null) {
                                listaTemas.add(tem);

                            }
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "No hay temas en el curso ! ", Toast.LENGTH_SHORT).show();
                    }

                }

                adaptadorTemas.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adaptadorTemas.notifyDataSetChanged();
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
    //metodo para agregar un tema
    public void agregarTema(String nom) {

        if (keysTemas == null) {
            String numero = "0";
            keysTemas = Long.parseLong(numero );
        }

        Tema nuevo = new Tema();
        nuevo.setNombre(nom);

        reference = FirebaseDatabase.getInstance().getReference("cursos");
        reference.child(keyCurso).child("temas").child(String.valueOf(keysTemas)).setValue(nuevo);

        listaTemas.add(nuevo);
        adaptadorTemas.notifyDataSetChanged();

    }

    //metodo para obtener la key del curso
    public void getIdCurso() {

        reference = FirebaseDatabase.getInstance().getReference("cursos");
        Query consulta = reference.orderByChild("codigo").equalTo(codigo);

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("periodo").getValue().toString().equals(periodo)
                            && data.child("anno").getValue().toString().equals(anno)) {
                        keyCurso = data.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //metodo para obtener el total items de temas
    public void getKeysTemas() {

        reference = FirebaseDatabase.getInstance().getReference("cursos");
        Query consulta = reference.orderByChild("codigo").equalTo(codigo);

        consulta.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.child("periodo").getValue().toString().equals(periodo)
                        && dataSnapshot.child("anno").getValue().toString().equals(anno)) {

                    DataSnapshot temas = dataSnapshot.child("temas");
                    for(DataSnapshot lista : temas.getChildren()){
                        keysTemas = lista.getChildrenCount();
                    }


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

            }
        });

    }

}
