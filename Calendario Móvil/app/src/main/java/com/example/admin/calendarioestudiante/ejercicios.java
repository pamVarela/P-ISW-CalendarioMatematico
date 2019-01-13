package com.example.admin.calendarioestudiante;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.calendarioestudiante.model.Ejercicio;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ejercicios extends AppCompatActivity {

    private RecyclerView recyclerView;
    private adaptadorEjercicios adaptadorEjercicios;
    private List<Ejercicio> listaEjercicios;
    private DatabaseReference reference;
    private DatePickerDialog date;

    private Uri imagen1, imagen2, imagen3;
    private FirebaseStorage storage;
    private StorageReference storageReference, imageRef1, imageRef2, imageRef3;
    private ProgressDialog progressDialog;

    private static final int SELECTED1 = 1;
    private static final int SELECTED2 = 2;
    private static final int SELECTED3 = 3;
    private static final String TAG = ejercicios.class.getSimpleName();
    private Long numKeyEjercicios;
    private String getKeyCurso, getKeyTema, codigo, periodo, anno, nombre1, nombre2, nombre3, tema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_ejercicios);

        recyclerView = findViewById(R.id.recycleEjercicios);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        codigo = getIntent().getStringExtra("codigo");
        periodo = getIntent().getStringExtra("periodo");
        anno = getIntent().getStringExtra("anno");
        tema = getIntent().getStringExtra("tema");

        listaEjercicios = new ArrayList<>();

        cargarDatosEjercicios();

        adaptadorEjercicios = new adaptadorEjercicios(listaEjercicios, ejercicios.this);
        recyclerView.setAdapter(adaptadorEjercicios);
    }

    //Metodo para llamar el layout del menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main3, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void cargarDatosEjercicios() {

        codigo = getIntent().getStringExtra("codigo");
        periodo = getIntent().getStringExtra("periodo");
        anno = getIntent().getStringExtra("anno");

        reference = FirebaseDatabase.getInstance().getReference("cursos");
        Query consulta = reference.orderByChild("codigo").equalTo(codigo);

        consulta.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.child("periodo").getValue().toString().equals(periodo)
                        && dataSnapshot.child("anno").getValue().toString().equals(anno)) {

                    DataSnapshot lista = dataSnapshot.child("temas");

                    for (DataSnapshot temas : lista.getChildren()) {
                        if (temas.child("nombre").getValue().toString().equals(tema)) {
                            if (temas.child("ejercicios").exists()) {
                                DataSnapshot listaEj = temas.child("ejercicios");
                                for (DataSnapshot ejercicio : listaEj.getChildren()) {
                                    Ejercicio ej = ejercicio.getValue(Ejercicio.class);
                                    listaEjercicios.add(ej);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "No hay ejercicios ! ", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                }

                adaptadorEjercicios.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                adaptadorEjercicios.notifyDataSetChanged();

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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.add: {

                getIdCurso();
                getIdTema();
                getKeysEjercicios();

                final AlertDialog.Builder builder = new AlertDialog.Builder(ejercicios.this);
                final View view = LayoutInflater.from(ejercicios.this).inflate(R.layout.dialog_agregar_ejercicio, null);

                final EditText nom = view.findViewById(R.id.nombreEj);
                final EditText img1 = view.findViewById(R.id.img1);
                final EditText img2 = view.findViewById(R.id.img2);
                final EditText img3 = view.findViewById(R.id.img3);
                final TextView fecha = view.findViewById(R.id.fecha);
                final ImageButton btnImagen1 = view.findViewById(R.id.btnProblema);
                final ImageButton btnImagen2 = view.findViewById(R.id.btnPlanteo);
                final ImageButton btnImagen3 = view.findViewById(R.id.btnSolucion);
                final ImageButton btnFecha = view.findViewById(R.id.btnFecha);

                btnImagen1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        abrirGaleria(SELECTED1);
                        img1.setText("ejercicio_" + nombre1);
                    }
                });
                btnImagen2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        abrirGaleria(SELECTED2);
                        img2.setText("ejercicio_" + nombre2);
                    }
                });
                btnImagen3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        abrirGaleria(SELECTED3);
                        img3.setText("ejercicio_" + nombre3);
                    }
                });
                btnFecha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Calendar calendar = Calendar.getInstance();
                        int anno = calendar.get(Calendar.YEAR);
                        int mes = calendar.get(Calendar.MONTH);
                        int dia = calendar.get(Calendar.DAY_OF_MONTH);

                        date = new DatePickerDialog(ejercicios.this,

                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int anno, int mes, int dia) {

                                        @SuppressLint("SimpleDateFormat")
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        calendar.set(anno, mes, dia);

                                        String fechaString = sdf.format(calendar.getTime());
                                        fecha.setText(fechaString);

                                    }

                                }, anno, mes, dia);

                        date.show();
                    }
                });


                builder.setView(view)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                final Ejercicio ej = new Ejercicio();

                                if (numKeyEjercicios == null) {
                                    String numero = "0";
                                    numKeyEjercicios = Long.parseLong(numero);
                                }

                                progressDialog = new ProgressDialog(view.getContext());
                                progressDialog.setMax(100);
                                progressDialog.setMessage("Cargando imagen...");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                progressDialog.show();
                                progressDialog.setCancelable(false);

                                if (nombre1.isEmpty() || nombre2.isEmpty() || nom.getText().toString().isEmpty() || fecha.getText().toString().isEmpty()) {

                                    Toast.makeText(getApplicationContext(), "Los datos estan incompletos ! ", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                } else {


                                    if (!nombre1.equals("") && !nombre3.equals("") && nombre2.equals("")) {

                                        reference = FirebaseDatabase.getInstance().getReference("cursos").child(getKeyCurso).child("temas").child(getKeyTema);
                                        reference.child("ejercicios").child(String.valueOf(numKeyEjercicios)).child("nombre").setValue(nom.getText().toString());
                                        reference.child("ejercicios").child(String.valueOf(numKeyEjercicios)).child("fecha").setValue(fecha.getText().toString());

                                        imageRef1 = storageReference.child("ejercicios/" + nombre1);
                                        imageRef3 = storageReference.child("ejercicios/" + nombre3);

                                        imageRef1.putFile(imagen1).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                                double progreso = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                progressDialog.incrementProgressBy((int) progreso);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(getApplicationContext(), "Fallo en la descarga Imagen 1", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();

                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                Task<Uri> urlTsk = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!urlTsk.isSuccessful()) ;
                                                Uri descarga = urlTsk.getResult();

                                                reference.child("ejercicios").child(String.valueOf(numKeyEjercicios)).child("problema").setValue(descarga.toString());
                                                ej.setProblema(descarga.toString());
                                                progressDialog.dismiss();
                                            }
                                        });

                                        imageRef3.putFile(imagen3).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                double progreso = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                progressDialog.incrementProgressBy((int) progreso);
                                            }

                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                Toast.makeText(getApplicationContext(), "Fallo en la descarga Imagen 3", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();

                                            }

                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                Task<Uri> urlTsk = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!urlTsk.isSuccessful()) ;
                                                Uri descarga = urlTsk.getResult();

                                                reference.child("ejercicios").child(String.valueOf(numKeyEjercicios)).child("solucion").setValue(descarga.toString());
                                                ej.setSolucion(descarga.toString());
                                                progressDialog.dismiss();

                                            }
                                        });

                                        reference.child("ejercicios").child(String.valueOf(numKeyEjercicios)).child("planteamiento").setValue("");
                                        ej.setPlanteamiento("");
                                        Toast.makeText(getApplicationContext(), "Se ha agregado el ejercicio ! ", Toast.LENGTH_SHORT).show();
                                        nombre1="";nombre3="";imageRef1=null; imageRef3=null;

                                    } else if (!nombre1.equals("") && !nombre2.equals("") && !nombre3.equals("")) {

                                        reference = FirebaseDatabase.getInstance().getReference("cursos").child(getKeyCurso).child("temas").child(getKeyTema);
                                        reference.child("ejercicios").child(String.valueOf(numKeyEjercicios)).child("nombre").setValue(nom.getText().toString());
                                        reference.child("ejercicios").child(String.valueOf(numKeyEjercicios)).child("fecha").setValue(fecha.getText().toString());

                                        imageRef1 = storageReference.child("ejercicios/" + nombre1);
                                        imageRef2 = storageReference.child("ejercicios/" + nombre2);
                                        imageRef3 = storageReference.child("ejercicios/" + nombre3);

                                        imageRef1.putFile(imagen1).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                                double progreso = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                progressDialog.incrementProgressBy((int) progreso);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(getApplicationContext(), "Fallo en la descarga Imagen 1", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();

                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                Task<Uri> urlTsk = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!urlTsk.isSuccessful()) ;
                                                Uri descarga = urlTsk.getResult();

                                                reference.child("ejercicios").child(String.valueOf(numKeyEjercicios)).child("problema").setValue(descarga.toString());
                                                ej.setProblema(descarga.toString());
                                                progressDialog.dismiss();
                                            }
                                        });

                                        imageRef2.putFile(imagen2).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                double progreso = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                progressDialog.incrementProgressBy((int) progreso);

                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Task<Uri> urlTsk = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!urlTsk.isSuccessful()) ;
                                                Uri descarga = urlTsk.getResult();

                                                reference.child("ejercicios").child(String.valueOf(numKeyEjercicios)).child("planteamiento").setValue(descarga.toString());
                                                ej.setPlanteamiento(descarga.toString());
                                                progressDialog.dismiss();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Fallo en la descarga Imagen 2", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();

                                            }
                                        });

                                        imageRef3.putFile(imagen3).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                double progreso = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                progressDialog.incrementProgressBy((int) progreso);
                                            }

                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                Toast.makeText(getApplicationContext(), "Fallo en la descarga Imagen 3", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();

                                            }

                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                Task<Uri> urlTsk = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!urlTsk.isSuccessful()) ;
                                                Uri descarga = urlTsk.getResult();

                                                reference.child("ejercicios").child(String.valueOf(numKeyEjercicios)).child("solucion").setValue(descarga.toString());
                                                ej.setSolucion(descarga.toString());
                                                progressDialog.dismiss();
                                            }
                                        });

                                        ej.setNombre(nom.getText().toString());
                                        ej.setFecha(fecha.getText().toString());
                                        listaEjercicios.add(ej);

                                        Toast.makeText(getApplicationContext(), "Se ha agregado el ejercicio ! ", Toast.LENGTH_SHORT).show();
                                        adaptadorEjercicios.notifyDataSetChanged();
                                        nombre1 = ""; nombre2 = ""; nombre3= ""; imageRef1 = null; imageRef2= null; imageRef3= null;
                                    }
                                }

                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                });

                AlertDialog d = builder.create();
                d.show();

            }
            break;
        }
        return true;
    }

    public void abrirGaleria(int seleccionado) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, seleccionado);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case SELECTED1:
                if (resultCode == RESULT_OK) {
                    imagen1 = data.getData();
                    nombre1 = System.currentTimeMillis() + "." + obtenerExtension(imagen1);

                }
            case SELECTED2:
                if (resultCode == RESULT_OK) {
                    imagen2 = data.getData();
                    nombre2 = System.currentTimeMillis() + "." + obtenerExtension(imagen2);

                }
            case SELECTED3:
                if (resultCode == RESULT_OK) {
                    imagen3 = data.getData();
                    nombre3 = System.currentTimeMillis() + "." + obtenerExtension(imagen3);

                }
        }

    }

    public String obtenerExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    public void getIdCurso() {

        reference = FirebaseDatabase.getInstance().getReference("cursos");
        Query consulta = reference.orderByChild("codigo").equalTo(codigo);

        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("periodo").getValue().toString().equals(periodo)
                            && data.child("anno").getValue().toString().equals(anno)) {
                        getKeyCurso = data.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Ha fallado la lectura de los datos", databaseError.toException());
            }
        });


    }

    public void getIdTema() {

        reference = FirebaseDatabase.getInstance().getReference("cursos");
        Query consulta = reference.orderByChild("codigo").equalTo(codigo);

        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("periodo").getValue().toString().equals(periodo)
                            && data.child("anno").getValue().toString().equals(anno)) {
                        DataSnapshot temas = data.child("temas");
                        for (DataSnapshot tems : temas.getChildren()) {
                            if (tems.child("nombre").getValue().toString().equals(tema)) {
                                getKeyTema = tems.getKey();
                            }
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

    public void getKeysEjercicios() {

        reference = FirebaseDatabase.getInstance().getReference("cursos");
        Query consulta = reference.orderByChild("codigo").equalTo(codigo);

        consulta.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.child("periodo").getValue().toString().equals(periodo)
                        && dataSnapshot.child("anno").getValue().toString().equals(anno)) {

                    DataSnapshot lista = dataSnapshot.child("temas");

                    for (DataSnapshot temas : lista.getChildren()) {

                        if (temas.child("nombre").getValue().toString().equals(tema)) {
                            DataSnapshot listaEj = temas.child("ejercicios");
                            numKeyEjercicios = listaEj.getChildrenCount() + 1;
                        }

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

