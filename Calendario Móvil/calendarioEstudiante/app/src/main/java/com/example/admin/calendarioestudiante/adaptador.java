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


public class adaptador extends ArrayAdapter<Curso> {

    private Context context;
    int layout;
    private ArrayList<Curso> datos;
    private DatabaseReference referencia;

    private String getId, getIdCurso, getIdCursoGeneral;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser usuario = auth.getCurrentUser();
    private String correo = usuario.getEmail();

    private static final String TAG = adaptador.class.getSimpleName();

    public adaptador(Context context, int layout, ArrayList<Curso> data) {
        super(context, layout, data);
        this.layout = layout;
        this.context = context;
        this.datos = data;
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

            holder.text = view.findViewById(R.id.contenidoCursos);
            holder.editar = view.findViewById(R.id.editar);
            holder.borrar = view.findViewById(R.id.borrar);

            view.setTag(holder);

        } else {
            holder = (cursoHolder) view.getTag();
        }

        String textoCodigo = datos.get(position).getCodigo();
        String textoNombre =  datos.get(position).getNombre();
        int textperiodo = datos.get(position).getPeriodo();
        int textanno =  datos.get(position).getAnno();

        holder.text.setText(textoCodigo + "\n" + textoNombre + "\nPeríodo: " + textperiodo + " - " +  textanno);

        holder.editar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater1 = ((Activity) context).getLayoutInflater();
                final View view1 = inflater1.inflate(R.layout.dialog_editar_curso, null);

                final TextView nom = view1.findViewById(R.id.nombreE);
                final TextView cod = view1.findViewById(R.id.codigoE);
                final EditText per = view1.findViewById(R.id.periodoE);
                final EditText an = view1.findViewById(R.id.annoE);

                nom.setText(datos.get(position).getNombre());
                cod.setText(datos.get(position).getCodigo());
                per.setText(String.valueOf(datos.get(position).getPeriodo()));
                an.setText(String.valueOf(datos.get(position).getAnno()));

                getKeyCursoGeneral(datos.get(position).getCodigo(),datos.get(position).getPeriodo(), datos.get(position).getAnno());
                getKeyCursoProfesor(datos.get(position).getCodigo(),datos.get(position).getPeriodo(), datos.get(position).getAnno());

                builder.setView(view1)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if ((esNumero(per.getText().toString())) && (esNumero(an.getText().toString()))){

                                    modificarCurso(position,Integer.parseInt(per.getText().toString()), Integer.parseInt(an.getText().toString()));
                                    Toast.makeText(getContext(), "Se ha modificado el curso !", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();

                                }else{
                                    Toast.makeText(getContext(), "Los datos son inválidos !", Toast.LENGTH_SHORT).show();
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

        //Se hace la llamada del boton borrar
        holder.borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater2 = ((Activity) context).getLayoutInflater();
                View view1 = inflater2.inflate(R.layout.borrar_contenido, null);

                builder.setView(view1)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                getKeyCursoProfesor(datos.get(position).getCodigo(),datos.get(position).getPeriodo(),datos.get(position).getAnno());
                                eliminarCurso(position);
                                Toast.makeText(getContext(), "Se ha eliminado el curso", Toast.LENGTH_SHORT).show();
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
        ImageButton editar;
        TextView text;
        ImageButton borrar;

    }

    //metodo para obtener la key o clave del profesor
    private void getKeyCursoGeneral(final String codigo, final int periodo, final int anno){

        referencia = FirebaseDatabase.getInstance().getReference().child("cursos");

        Query consulta = referencia.orderByChild("codigo").equalTo(codigo);

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot lista: dataSnapshot.getChildren()){

                    if( lista.child("codigo").getValue().toString().equals(codigo)
                            && lista.child("periodo").getValue().toString().equals(String.valueOf(periodo))
                            &&  lista.child("anno").getValue().toString().equals(String.valueOf(anno))){

                        getIdCursoGeneral = lista.getKey();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        notifyDataSetChanged();

    }

    //metodo para obtener la key o clave del profesor
    private void getKeyCursoProfesor(final String codigo, final int periodo, final int anno){

        referencia = FirebaseDatabase.getInstance().getReference().child("usuarios");

        Query consulta = referencia.orderByChild("correo").equalTo(correo);

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
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

            }
        });

        notifyDataSetChanged();

    }

    //Metodo para modificar un curso de la lista del profesor
    private void modificarCurso(int posicion, final int periodo, final int anno){

        referencia = FirebaseDatabase.getInstance().getReference().child("usuarios");

        datos.get(posicion).setPeriodo(periodo);
        datos.get(posicion).setAnno(anno);

        referencia.child(getId).child("cursos").child(getIdCurso).setValue(datos.get(posicion));

        referencia = FirebaseDatabase.getInstance().getReference().child("cursos");
        referencia.child(getIdCursoGeneral).child("periodo").setValue(periodo);
        referencia.child(getIdCursoGeneral).child("anno").setValue(anno);

    }

    //Metodo para eliminar un Curso
    public void eliminarCurso(int posicion){

        referencia = FirebaseDatabase.getInstance().getReference("usuarios");
        Query buscar = referencia.orderByChild("correo").equalTo(correo);

        buscar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot data2 = dataSnapshot.child(getId).child("cursos").child(getIdCurso);
                for (DataSnapshot index : data2.getChildren()) {
                    index.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        datos.remove(posicion);
        notifyDataSetChanged();
    }

    private void obtenerID() {

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

        //Log.d("OBTENER ID::", String.valueOf(getId));

    }

    public boolean esNumero (String objeto){
        try{
            Integer.parseInt(objeto);
            return true;
        }catch (NumberFormatException exception){
            return false;
        }

    }




}
