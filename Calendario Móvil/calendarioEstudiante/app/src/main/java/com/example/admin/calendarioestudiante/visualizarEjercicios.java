package com.example.admin.calendarioestudiante;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class visualizarEjercicios extends AppCompatActivity {

    ViewPager viewPager;
    ImageButton btnMostrarSolucion, btnArte;
    ArrayList<String> imagenes = new ArrayList<>();
    String codigo, periodo, anno, fechaHoy;
    Long keysTemas ,numeroEj;
    DatabaseReference reference;
    DateFormat df;

    private static final String TAG = visualizarEjercicios.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_ejercicios);

        viewPager = findViewById(R.id.viewpagerE);
        btnMostrarSolucion = findViewById(R.id.btnS);
        btnArte = findViewById(R.id.btnArte);

        df = new SimpleDateFormat("yyyy-MM-dd");
        fechaHoy = df.format(new Date());

        //Toast.makeText(getApplicationContext(), fechaHoy, Toast.LENGTH_SHORT).show();

        codigo = getIntent().getStringExtra("codigo");
        periodo = getIntent().getStringExtra("periodo");
        anno = getIntent().getStringExtra("anno");

        cargarImagenesEjercicios();

        btnMostrarSolucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(visualizarEjercicios.this);
                final View view2 = LayoutInflater.from(visualizarEjercicios.this).inflate(R.layout.ejercicio_slider, null);

                final ImageView img = view2.findViewById(R.id.imageView2);

                if(imagenes.size() == 0){
                    Toast.makeText(getApplicationContext(), "No esta disponible ", Toast.LENGTH_SHORT).show();
                }else{
                    Glide.with(getApplicationContext()).load(imagenes.get(2)).into(img);
                }


                builder.setView(view2);

                AlertDialog d = builder.create();
                d.show();

            }
        });

        btnArte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(visualizarEjercicios.this);
                final View view2 = LayoutInflater.from(visualizarEjercicios.this).inflate(R.layout.dialog_arte, null);

                final ImageView img = view2.findViewById(R.id.imageView3);
                final TextView texto = view2.findViewById(R.id.txtArte);

                reference = FirebaseDatabase.getInstance().getReference("imagenes");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data: dataSnapshot.getChildren()){

                             String[] fechas = fechaHoy.split("-");
                             String diaS =fechas[2]; int dia = Integer.parseInt(diaS);
                             String mesS = fechas[1]; int mes = Integer.parseInt(mesS);

                             String fechaInicioFirebase = data.child("fechaInicio").getValue().toString();
                             String[] fechaI = fechaInicioFirebase.split("-");
                             String diaI = fechaI[2]; int diaII = Integer.parseInt(diaI);
                             String mesI = fechaI[1]; int mesII = Integer.parseInt(mesI);

                             String fechaFinalFirebase =  data.child("fechaFinal").getValue().toString();
                             String[] fechaF = fechaFinalFirebase.split("-");
                             String diaF =fechaF[2]; int diaIF = Integer.parseInt(diaF);
                             String mesF = fechaF[1]; int mesIF= Integer.parseInt(mesF);

                            if((dia >= diaII && dia <= diaIF) && (mes >= mesII && mes <= mesIF)){
                                texto.setText(data.child("nombre").getValue().toString() + "\n" + data.child("descripcion").getValue().toString() +
                                        "\nAutor: " + data.child("autor").getValue().toString());
                                Glide.with(getApplicationContext()).load(data.child("imagen").getValue().toString()).into(img);
                            }else{
                                if(data.child("imagen").getValue().toString().equals("") || data.child("fechaInicio").getValue().toString().equals("")){
                                    Glide.with(getApplicationContext()).load(R.drawable.arte).into(img);
                                    Toast.makeText(getApplicationContext(), "No esta disponible la imagen ", Toast.LENGTH_SHORT).show();
                                }else{
                                    Glide.with(getApplicationContext()).load(R.drawable.arte).into(img);
                                    Toast.makeText(getApplicationContext(), "No esta disponible la imagen ", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                builder.setView(view2);

                AlertDialog d = builder.create();
                d.show();

            }
        });

    }

    public void cargarImagenesEjercicios() {

        codigo = getIntent().getStringExtra("codigo");
        periodo = getIntent().getStringExtra("periodo");
        anno = getIntent().getStringExtra("anno");

        reference = FirebaseDatabase.getInstance().getReference("cursos");
        Query consulta = reference.orderByChild("codigo").equalTo(codigo);

        consulta.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int contadorEj = 0, contadorEjs = 0, contadorTemas = 0, contador = 0;

                String per = dataSnapshot.child("periodo").getValue().toString();
                String an = dataSnapshot.child("anno").getValue().toString();

                if ((per.equals(periodo)) || (an.equals(anno))) {

                    if (!dataSnapshot.child("temas").exists()) {
                        Toast.makeText(getApplicationContext(), "No hay ejercicios disponibles para hoy ", Toast.LENGTH_SHORT).show();

                    } else {
                        DataSnapshot lista = dataSnapshot.child("temas");
                        for (DataSnapshot temas : lista.getChildren()) {
                            keysTemas = lista.getChildrenCount();

                            if (!temas.child("ejercicios").exists()) {
                                contador += 1;
                            } else {
                                DataSnapshot listaEj = temas.child("ejercicios");
                                for (DataSnapshot ejercicio : listaEj.getChildren()) {
                                    numeroEj = listaEj.getChildrenCount();

                                    if (ejercicio.child("fecha").getValue().toString().equals(fechaHoy)) {
                                        if (ejercicio.child("problema").exists() && ejercicio.child("planteamiento").exists()
                                                && ejercicio.child("solucion").exists()) {
                                            imagenes.add(ejercicio.child("problema").getValue().toString());
                                            imagenes.add(ejercicio.child("planteamiento").getValue().toString());
                                            imagenes.add(ejercicio.child("solucion").getValue().toString());
                                            break;

                                        } else if (ejercicio.child("problema").exists() && !ejercicio.child("planteamiento").exists()
                                                && ejercicio.child("solucion").exists()) {
                                            imagenes.add(ejercicio.child("problema").getValue().toString());
                                            imagenes.add(ejercicio.child("solucion").getValue().toString());
                                            break;
                                        }
                                    }else{
                                        contadorEj += 1;
                                    }
                                }

                                String valor = String.valueOf(numeroEj);
                                int numEj = Integer.parseInt(valor);
                                contadorEjs += numEj;
                            }
                            String num = String.valueOf(keysTemas);
                            int numInt = Integer.parseInt(num);
                            contadorTemas = numInt;
                        }

                        if(contadorEjs != 0 && contadorEj != 0){

                            if(contadorEjs == contadorEj){
                              Toast.makeText(getApplicationContext(), "No hay ejercicios disponibles para hoy", Toast.LENGTH_SHORT).show();
                          }

                        }

                        if(contadorTemas != 0 && contador != 0){

                            if (contadorTemas == contador) {
                                Toast.makeText(getApplicationContext(), "No hay ejercicios disponibles para hoy", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                }

                viewPagerAdaptador viewPagerAdaptador = new viewPagerAdaptador(visualizarEjercicios.this, imagenes);
                viewPager.setAdapter(viewPagerAdaptador);

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


