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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class menu extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient googleApiClient;
    private ImageView foto;
    private TextView nombre;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_menu);

        foto = findViewById(R.id.foto);
        nombre = findViewById(R.id.nombreUsuario);

        final ImageButton cursos = findViewById(R.id.cursos);
        final ImageButton salir = findViewById(R.id.salida);
        final ImageButton acercaApp = findViewById(R.id.about);
        final ImageButton notificaciones  = findViewById(R.id.notificacion);

        final String correo = getIntent().getStringExtra("correo");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        cursos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu.this, cursosEstudiante.class);
                intent.putExtra("correo", correo);
                startActivity(intent);
            }
        });


        auth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mostrarDatosUsuario();
                } else {
                    signOutUser();
                }
            }
        };

        salir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               auth.signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            signOutUser();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error en la salida", Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onStart() {
        super.onStart();

        auth.addAuthStateListener(firebaseAuthListener);

    }
    private void signOutUser() {
        Intent intent = new Intent(menu.this, login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void mostrarDatosUsuario() {

        FirebaseUser mUser = auth.getCurrentUser();
        Uri uri = mUser.getPhotoUrl();

        if (mUser != null) {
            nombre.setText(mUser.getDisplayName());
            Glide.with(getApplicationContext()).load(uri).into(foto);
        }
    }


    public void revoke(View view) {
        auth.signOut();

        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    signOutUser();
                } else {
                    Toast.makeText(getApplicationContext(), "No se pudo revocar el acceso", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuthListener != null) {
            auth.removeAuthStateListener(firebaseAuthListener);
        }
    }


}
