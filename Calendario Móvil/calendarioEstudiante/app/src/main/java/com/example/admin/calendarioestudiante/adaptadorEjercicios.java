package com.example.admin.calendarioestudiante;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.admin.calendarioestudiante.model.Ejercicio;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class adaptadorEjercicios extends RecyclerView.Adapter<adaptadorEjercicios.ejerciciosHolder> {

    private List<Ejercicio> listaEjercicios;
    private Context context;
    DatabaseReference referencia;
    StorageReference storageReference1, storageReference2, storageReference3;
    String getKeyCurso, keyTema, getIdEjercicio;
    private DatePickerDialog date;

    private Uri image1, image2, image3;
    private ProgressDialog progressDialog;
    private static final int SELECTED1 = 1;
    private static final int SELECTED2 = 2;
    private static final int SELECTED3 = 3;
    String nombre1, nombre2, nombre3;

    String codigo, periodo, anno, tema;

    private static final String TAG = adaptadorEjercicios.class.getSimpleName();

    public static class ejerciciosHolder extends RecyclerView.ViewHolder {

        TextView nombreEjercicio;
        TextView fechaEjercicio;
        ImageButton modificar;
        ImageButton eliminar;
        ImageView problema;
        ImageView planteo;
        ImageView solucion;

        public ejerciciosHolder(View itemView) {
            super(itemView);

            this.nombreEjercicio = itemView.findViewById(R.id.nombreE);
            this.fechaEjercicio = itemView.findViewById(R.id.fechaEjercicio);
            this.modificar = itemView.findViewById(R.id.editarEjercicio);
            this.eliminar = itemView.findViewById(R.id.borrarEjercicio);
            this.problema = itemView.findViewById(R.id.problema);
            this.planteo = itemView.findViewById(R.id.planteo);
            this.solucion = itemView.findViewById(R.id.solucion);
        }
    }

    public adaptadorEjercicios(List<Ejercicio> listaEjercicios, Context context) {
        this.listaEjercicios = listaEjercicios;
        this.context = context;
    }

    @NonNull
    @Override
    public ejerciciosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ejercicios, parent, false);

        ejerciciosHolder holderEjercicios = new ejerciciosHolder(view);

        return holderEjercicios;
    }

    @Override
    public int getItemCount() {
        return listaEjercicios.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ejerciciosHolder holder, final int position) {

        getIdTema();getKeyCurso();
        holder.nombreEjercicio.setText(listaEjercicios.get(position).getNombre());
        holder.fechaEjercicio.setText(listaEjercicios.get(position).getFecha());

        if (listaEjercicios.get(position).getProblema() == null) {
            holder.problema.setImageResource(R.drawable.images);
        } else {
            Glide.with(context).load(listaEjercicios.get(position).getProblema()).into(holder.problema);
        }

        if (listaEjercicios.get(position).getPlanteamiento() == null) {
            holder.problema.setImageResource(R.drawable.images);
        } else {
            Glide.with(context).load(listaEjercicios.get(position).getPlanteamiento()).into(holder.planteo);
        }

        if (listaEjercicios.get(position).getSolucion() == null) {
            holder.problema.setImageResource(R.drawable.images);
        } else {
            Glide.with(context).load(listaEjercicios.get(position).getSolucion()).into(holder.solucion);
        }
        //boton para modificar un ejercicio
        holder.modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getKeyEjercicio(listaEjercicios.get(position).getNombre());

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater1 = ((Activity) context).getLayoutInflater();

                final View view1 = inflater1.inflate(R.layout.dialog_modificar_ejercicio, null);

                final EditText nom = view1.findViewById(R.id.nombreEj);
                final EditText img1 = view1.findViewById(R.id.img1E);
                final EditText img2 = view1.findViewById(R.id.img2E);
                final EditText img3 = view1.findViewById(R.id.img3E);
                final TextView fecha = view1.findViewById(R.id.fechaE);
                final ImageButton btnImagen1 = view1.findViewById(R.id.btnProblemaE);
                final ImageButton btnImagen2 = view1.findViewById(R.id.btnPlanteoE);
                final ImageButton btnImagen3 = view1.findViewById(R.id.btnSolucionE);
                final ImageButton btnFecha = view1.findViewById(R.id.btnFechaE);

                nom.setText(listaEjercicios.get(position).getNombre());

                btnImagen1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        abrirGaleria(SELECTED1);
                        String nom1 = ((ejercicios) context).getIntent().getStringExtra("nombre1");
                        img1.setText(nom1);
                    }
                });

                btnImagen2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        abrirGaleria(SELECTED2);
                        String nom2 = ((ejercicios) context).getIntent().getStringExtra("nombre2");
                        img2.setText(nom2);
                    }
                });

                btnImagen3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        abrirGaleria(SELECTED3);
                        String nom3 = ((ejercicios) context).getIntent().getStringExtra("nombre3");
                        img3.setText(nom3);
                    }
                });

                btnFecha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar calendar = Calendar.getInstance();
                        int anno = calendar.get(Calendar.YEAR);
                        int mes = calendar.get(Calendar.MONTH);
                        int dia = calendar.get(Calendar.DAY_OF_MONTH);

                        date = new DatePickerDialog(context,

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

                fecha.setText(listaEjercicios.get(position).getFecha());

                builder.setView(view1)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String nombre = nom.getText().toString();
                                String fechaE = fecha.getText().toString();
                                if(nombre.isEmpty()){
                                    Toast.makeText(context, "Los datos están incompletos ! ", Toast.LENGTH_SHORT).show();
                                }else{
                                    referencia = FirebaseDatabase.getInstance().getReference("cursos");
                                    referencia.child(getKeyCurso).child("temas").child(keyTema).child("ejercicios").child(getIdEjercicio).child("nombre").setValue(nombre);
                                    referencia.child(getKeyCurso).child("temas").child(keyTema).child("ejercicios").child(getIdEjercicio).child("fecha").setValue(fechaE);


                                    listaEjercicios.get(position).setFecha(fechaE);
                                    listaEjercicios.get(position).setNombre(nombre);

                                    Toast.makeText(context, "Se ha modificado el ejercicio ! ", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                }


                            }
//                                storageReference1= FirebaseStorage.getInstance().getReferenceFromUrl(listaEjercicios.get(position).getProblema());
//                                storageReference1.delete();
//
//                                storageReference2= FirebaseStorage.getInstance().getReferenceFromUrl(listaEjercicios.get(position).getPlanteamiento());
//                                storageReference2.delete();
//
//                                storageReference3= FirebaseStorage.getInstance().getReferenceFromUrl(listaEjercicios.get(position).getSolucion());
//                                storageReference3.delete();

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
        //boton para eliminar el ejercicio
        holder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater2 = ((Activity) context).getLayoutInflater();

                final View view2 = inflater2.inflate(R.layout.borrar_contenido, null);

                builder.setView(view2)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                getKeyEjercicio(listaEjercicios.get(position).getNombre());

                                if (listaEjercicios.get(position).getProblema() != null ||
                                        listaEjercicios.get(position).getPlanteamiento() != null
                                        || listaEjercicios.get(position).getSolucion() != null) {

                                    storageReference1 = FirebaseStorage.getInstance().getReferenceFromUrl(listaEjercicios.get(position).getProblema());
                                    storageReference1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Se ha borrado el contenido");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Ha fallado la operacion de borrar");
                                        }
                                    });

                                    storageReference2 = FirebaseStorage.getInstance().getReferenceFromUrl(listaEjercicios.get(position).getPlanteamiento());
                                    storageReference2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Se ha borrado el contenido");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Ha fallado la operacion de borrar");
                                        }
                                    });

                                    storageReference3 = FirebaseStorage.getInstance().getReferenceFromUrl(listaEjercicios.get(position).getSolucion());
                                    storageReference3.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Se ha borrado el contenido");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Ha fallado la operacion de borrar");
                                        }
                                    });

                                    eliminarEjercicio(position);
                                    Toast.makeText(context, "Se ha eliminado el ejercicio!", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();

                                } else {

                                    eliminarEjercicio(position);
                                    Toast.makeText(context, "Se ha eliminado el ejercicio!", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                }


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
    }


    public void eliminarEjercicio(int posicion) {

        referencia = FirebaseDatabase.getInstance().getReference("cursos");
        Query consulta = referencia.orderByChild("codigo").equalTo(codigo);

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot data = dataSnapshot.child(getKeyCurso).child("temas").child(keyTema).child("ejercicios").child(getIdEjercicio);

                for (DataSnapshot index : data.getChildren()) {
                    index.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        listaEjercicios.remove(posicion);
        notifyDataSetChanged();

    }

    public void getKeyEjercicio(final String nombre) {


        codigo = ((ejercicios) context).getIntent().getStringExtra("codigo");
        periodo = ((ejercicios) context).getIntent().getStringExtra("periodo");
        anno = ((ejercicios) context).getIntent().getStringExtra("anno");
        tema = ((ejercicios) context).getIntent().getStringExtra("tema");

        referencia = FirebaseDatabase.getInstance().getReference("cursos");
        Query consulta = referencia.orderByChild("codigo").equalTo(codigo);

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("codigo").getValue().toString().equals(codigo)
                            && data.child("periodo").getValue().toString().equals(String.valueOf(periodo))
                            && data.child("anno").getValue().toString().equals(String.valueOf(anno))) {

                        DataSnapshot temas = data.child("temas");
                        for (DataSnapshot dataTemas : temas.getChildren()) {
                            if (dataTemas.child("nombre").getValue().toString().equals(tema)) {
                                DataSnapshot ejs = dataTemas.child("ejercicios");
                                for (DataSnapshot dataEj : ejs.getChildren()) {
                                    if (dataEj.child("nombre").getValue().toString().equals(nombre)) {
                                        getIdEjercicio = dataEj.getKey();
                                    }

                                }
                            }
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void getKeyCurso() {

        codigo = ((ejercicios) context).getIntent().getStringExtra("codigo");
        periodo = ((ejercicios) context).getIntent().getStringExtra("periodo");
        anno = ((ejercicios) context).getIntent().getStringExtra("anno");

        referencia = FirebaseDatabase.getInstance().getReference("cursos");
        Query consulta = referencia.orderByChild("codigo").equalTo(codigo);

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

        codigo = ((ejercicios) context).getIntent().getStringExtra("codigo");
        periodo = ((ejercicios) context).getIntent().getStringExtra("periodo");
        anno = ((ejercicios) context).getIntent().getStringExtra("anno");
        tema = ((ejercicios) context).getIntent().getStringExtra("tema");

        referencia = FirebaseDatabase.getInstance().getReference("cursos");
        Query consulta = referencia.orderByChild("codigo").equalTo(codigo);

        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("periodo").getValue().toString().equals(periodo)
                            && data.child("anno").getValue().toString().equals(anno)) {
                        DataSnapshot temas = data.child("temas");
                        for (DataSnapshot tems : temas.getChildren()) {
                            if (tems.child("nombre").getValue().toString().equals(tema)) {
                                keyTema = tems.getKey();
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

    public void abrirGaleria(int seleccionado) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        ((ejercicios) context).startActivityForResult(intent, seleccionado);

    }

}


