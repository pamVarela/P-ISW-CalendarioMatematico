package com.example.admin.calendarioestudiante;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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


public class cursosProfesor extends AppCompatActivity {

    private EditText codigo, nombre, periodo, anno;
    private ListView listView;
    private adaptador adaptadorCursos;
    private DatabaseReference databaseReference;
    private CheckBox chk ;
    String correo;
    private ArrayList<Curso> listaC;
    private ArrayList<Curso> listaCursosGenerales = new ArrayList<>();
    private Long numeroKey, numeroKey2;
    private Curso cursoSeleccionado;
    private String getId;

    private static final String TAG = cursosProfesor.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos_profesor);

        codigo = findViewById(R.id.codigo);
        nombre = findViewById(R.id.nombre);
        periodo = findViewById(R.id.periodo);
        anno = findViewById(R.id.anno);
        listView = findViewById(R.id.lista);
        chk = findViewById(R.id.chk);

        correo =  getIntent().getStringExtra("correo");

        listaC = new  ArrayList<>();

        //Se hace la llamada a los metodos para inicializar todos los datos necesarios
        cursosProfesor(correo);
        cursosGenerales();

        obtenerID();
        contadorKeysCursosProfesor();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(cursosProfesor.this, temas.class);
                intent.putExtra("codigo",listaC.get(position).getCodigo());
                intent.putExtra("periodo", String.valueOf(listaC.get(position).getPeriodo()));
                intent.putExtra("anno", String.valueOf(listaC.get(position).getAnno()));
                startActivity(intent);
            }
        });

        adaptadorCursos = new adaptador(cursosProfesor.this, R.layout.single_row3, listaC);
        listView.setAdapter(adaptadorCursos);


    }


    //Metodo para llamar el layout del menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Metodo para la extraer los datos de los cursos por profesor a la base de datos
    public void cursosProfesor(String correo){

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        Query buscar = databaseReference.orderByChild("correo").equalTo(correo);

        buscar.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DataSnapshot lista = dataSnapshot.child("cursos");
                for (DataSnapshot cursos : lista.getChildren()) {
                    Curso c = cursos.getValue(Curso.class);
                    listaC.add(c);
                }
                adaptadorCursos.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "DATOS HAN CAMBIADO");
                adaptadorCursos.notifyDataSetChanged();

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
    //Metodo para la extraer los datos de los cursos generales a la base de datos
    public void cursosGenerales() {

        databaseReference = FirebaseDatabase.getInstance().getReference("cursos");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot cur : dataSnapshot.getChildren()) {
                    Curso c = cur.getValue(Curso.class);
                    listaCursosGenerales.add(c);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Ha fallado la lectura de los datos", databaseError.toException());
            }

        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.agregar: {

                contadorKeysCursosGeneral();

                final AlertDialog.Builder builder = new AlertDialog.Builder(cursosProfesor.this);
                final View view = LayoutInflater.from(cursosProfesor.this).inflate(R.layout.dialog_agregar_curso, null);

                final Spinner spinner = view.findViewById(R.id.spinnerCursos);
                final Button boton = view.findViewById(R.id.agregarCursos);
                final CheckBox ch = view.findViewById(R.id.chk);

                for (int index = 0; index < listaC.size(); index++) {

                    if (listaCursosGenerales.toString().contains(listaC.get(index).toString())) {
                        Iterator<Curso> index2 = listaCursosGenerales.iterator();
                        while (index2.hasNext()) {
                            Curso c = index2.next();
                            if (c.toString().equals(listaC.get(index).toString())) {
                                index2.remove();
                            }
                        }
                    }
                }

                final ArrayAdapter<Curso> adaptador = new ArrayAdapter<Curso>(cursosProfesor.this, R.layout.support_simple_spinner_dropdown_item, listaCursosGenerales);
                adaptador.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(adaptador);

                builder.setView(view);

                final AlertDialog d = builder.create();

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                        obtenerID();
                        contadorKeysCursosProfesor();

                        cursoSeleccionado = listaCursosGenerales.get(position);
                        //Log.d("SELECCIONADO: ", String.valueOf(cursoSeleccionado.toString()));

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }

                });

                boton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                    if(ch.isChecked()){
                        agregarCursoProfesor(cursoSeleccionado);
                        listaC.add(cursoSeleccionado);
                        d.dismiss();
                        adaptadorCursos.notifyDataSetChanged();

                    }else{

                        agregarCursoNuevo(cursoSeleccionado);
                        listaC.add(cursoSeleccionado);
                        d.dismiss();
                        adaptadorCursos.notifyDataSetChanged();
                    }

                    }
                });

                d.show();

            }
            break;
        }
        return true;
    }

    public void agregarCursoProfesor(Curso curso) {

        if (numeroKey2 == null) {
            String numero = "0";
            numeroKey2 = Long.parseLong(numero);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        databaseReference.child(getId).child("cursos").child(String.valueOf(numeroKey2+1)).setValue(curso);

        Toast.makeText(getApplicationContext(), "Se ha agregado el curso!", Toast.LENGTH_SHORT).show();

        adaptadorCursos.notifyDataSetChanged();
    }

    //Metodo para agregar un curso nuevo
    public void agregarCursoNuevo(Curso curso) {

        databaseReference = FirebaseDatabase.getInstance().getReference("cursos");
        databaseReference.child(Long.toString(numeroKey)).setValue(curso);

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        databaseReference.child(getId).child("cursos").child(String.valueOf(numeroKey2+1)).setValue(curso);

        Toast.makeText(getApplicationContext(), "Se ha agregado el nuevo curso!", Toast.LENGTH_SHORT).show();
        adaptadorCursos.notifyDataSetChanged();

    }


    //Metodo para obtener el numero de ID o clave del profesor
    public void obtenerID() {

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        Query consulta = databaseReference.orderByChild("correo").equalTo(correo);

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    getId = data.getKey();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Ha fallado la lectura de los datos", databaseError.toException());
            }
        });

    }

    //Metodo para contar el numero de keys en los cursos generales
    public void contadorKeysCursosGeneral() {

        databaseReference = FirebaseDatabase.getInstance().getReference("cursos");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                numeroKey = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Ha fallado la lectura de los datos", databaseError.toException());
            }

        });

    }

    //Metodo para contar el numero de keys en los cursos por profesor
    public void contadorKeysCursosProfesor() {

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        Query consulta = databaseReference.orderByChild("correo").equalTo(correo);

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    numeroKey2 = data.child("cursos").getChildrenCount();
                }

                Log.d("DATA", String.valueOf(numeroKey2));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



}
