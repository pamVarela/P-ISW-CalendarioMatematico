package com.example.admin.calendarioestudiante;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class menu extends AppCompatActivity {

    private ImageView foto;
    private TextView nombre;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (!isUserLogin()) {
            signOutUser();
        }
        setContentView(R.layout.activity_menu);

        foto = findViewById(R.id.foto);
        nombre = findViewById(R.id.nombreUsuario);

        mostrarDatosUsuario();

        final ImageButton cursos = findViewById(R.id.cursos);
        final ImageButton salir = findViewById(R.id.salida);
        final ImageButton acercaApp = findViewById(R.id.about);
        final ImageButton notificaciones  = findViewById(R.id.notificacion);

        final String correo = getIntent().getStringExtra("correo");

        cursos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu.this, cursosEstudiante.class);
                intent.putExtra("correo", correo);
                startActivity(intent);
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(menu.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    signOutUser();
                                } else {
                                    mensajeAviso("Error al salir");
                                }
                            }
                        });
            }
        });

        acercaApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(menu.this,acercaApp.class);
                startActivity(intent);
            }
        });


        notificaciones.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(menu.this, notificaciones.class);
                startActivity(intent);
            }
        });
    }

    private boolean isUserLogin() {

        if (auth.getCurrentUser() != null) {
            return true;
        }
        return false;

    }

    private void signOutUser() {
        Intent intent = new Intent(menu.this, login.class);
        startActivity(intent);
        finish();
    }


    private void mensajeAviso(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }


    private void mostrarDatosUsuario() {

        FirebaseUser mUser = auth.getCurrentUser();
        Uri uri = mUser.getPhotoUrl();

        if (mUser != null) {

            nombre.setText(mUser.getDisplayName());
            Glide.with(getApplicationContext()).load(uri).into(foto);
        }
    }


}
