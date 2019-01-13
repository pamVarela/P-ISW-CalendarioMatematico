package com.example.admin.calendarioestudiante;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.calendarioestudiante.model.Tema;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import static com.firebase.ui.auth.AuthUI.TAG;

public class adaptadorTemas extends ArrayAdapter<Tema> {

    private Context context;
    int layout;
    private ArrayList<Tema> datos;
    private DatabaseReference referencia;
    private String numKeyTema, getKeyCurso;


    public adaptadorTemas(@NonNull Context context, int layout, ArrayList<Tema> datos) {
        super(context, layout, datos);
        this.context = context;
        this.layout = layout;
        this.datos = datos;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        temaHolder holder = null;

        if (view == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layout, parent, false);

            holder = new temaHolder();

            holder.text = view.findViewById(R.id.contenidoTemas);
            holder.editarTema = view.findViewById(R.id.editarTemas);
            holder.borrarTema = view.findViewById(R.id.borrarTemas);

            view.setTag(holder);

        } else {
            holder = (adaptadorTemas.temaHolder)view.getTag();
        }


        holder.text.setText(datos.get(position).getNombre());

        getIdCurso();
        holder.editarTema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater2 = ((Activity) context).getLayoutInflater();
                final View view1 = inflater2.inflate(R.layout.dialog_modificar_tema, null);

                final EditText nomE = view1.findViewById(R.id.nombreTemaE);
                nomE.setText(datos.get(position).getNombre());

                getIdTema(datos.get(position).getNombre());

                builder.setView(view1)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (nomE.getText().toString().equals("")) {
                                    Toast.makeText(getContext(), "Datos incompletos!", Toast.LENGTH_SHORT).show();
                                } else {
                                    referencia = FirebaseDatabase.getInstance().getReference("cursos");
                                    referencia.child(getKeyCurso).child("temas").child(numKeyTema).child("nombre").setValue(nomE.getText().toString());
                                    datos.get(position).setNombre(nomE.getText().toString());
                                    Toast.makeText(getContext(), "Se ha modificado el tema de forma correcta!", Toast.LENGTH_SHORT).show();
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

        holder.borrarTema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater2 = ((Activity) context).getLayoutInflater();
                View view1 = inflater2.inflate(R.layout.borrar_contenido, null);

                builder.setView(view1)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                getIdTema(datos.get(position).getNombre());
                                eliminarTemas(position);

                                Toast.makeText(getContext(), "Se ha eliminado el tema del curso!", Toast.LENGTH_SHORT).show();
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

    static class temaHolder {
        TextView text;
        ImageButton editarTema;
        ImageButton borrarTema;

    }

    public void getIdTema(final String temaActual) {

        final String codigo= ((temas) context).getIntent().getStringExtra("codigo");
        final String periodo = ((temas) context).getIntent().getStringExtra("periodo");
        final String anno = ((temas) context).getIntent().getStringExtra("anno");

        referencia = FirebaseDatabase.getInstance().getReference().child("cursos");
        Query buscar = referencia.orderByChild("codigo").equalTo(codigo);

        buscar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    if (data.child("periodo").getValue().toString().equals(periodo)
                            && data.child("anno").getValue().toString().equals(anno)) {

                        DataSnapshot lista = data.child("temas");
                        for (DataSnapshot tem : lista.getChildren()) {
                            if (tem.child("nombre").getValue().toString().equals(temaActual)) {
                                numKeyTema = tem.getKey();
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



    public void getIdCurso() {

        final String codigo = ((temas) context).getIntent().getStringExtra("codigo");
        final String periodo = ((temas) context).getIntent().getStringExtra("periodo");
        final String anno = ((temas) context).getIntent().getStringExtra("anno");

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

    public void eliminarTemas(final int posicion){

        referencia = FirebaseDatabase.getInstance().getReference("cursos");

        referencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot data = dataSnapshot.child(getKeyCurso).child("temas").child(numKeyTema);
                for (DataSnapshot index : data.getChildren()) {
                    index.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Ha fallado la lectura de los datos", databaseError.toException());
            }
        });

        datos.remove(posicion);
        notifyDataSetChanged();
    }


}
