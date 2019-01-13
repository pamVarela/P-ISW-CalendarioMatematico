package com.example.admin.calendarioestudiante;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.calendarioestudiante.model.Usuario;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String PATH_TOS = "";
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private Long numeroKeys;
    private static final String TAG = login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        contadorKeysUsuarios();

        if(isUserLogin()){
            loginUser();
        }

        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Button boton = findViewById(R.id.login);
        boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setTosUrl(PATH_TOS)
                .build(),RC_SIGN_IN);


            }
        });
    }

    private boolean isUserLogin(){

        if(auth.getCurrentUser() != null){
            return true;
        }
        return false;

    }

    private void loginUser(){

        contadorKeysUsuarios();

        final FirebaseUser user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("usuarios");
        Query consulta = reference.orderByChild("correo").equalTo(user.getEmail());

            consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        String tipo = data.child("tipo").getValue().toString();
                        String email = data.child("correo").getValue().toString();
                        if (tipo.equals("2")){
                            Intent intent = new Intent(login.this,menu_profesor.class);
                            intent.putExtra("correo",email);
                            startActivity(intent);

                        }else{
                            Intent intent = new Intent(login.this,menu.class);
                            intent.putExtra("correo",email);
                            startActivity(intent);

                        }
                    }
                }else{

                    Usuario usuarioNuevo = new Usuario();
                    usuarioNuevo.setCorreo(user.getEmail());
                    usuarioNuevo.setNombre(user.getDisplayName());
                    usuarioNuevo.setNotifica(0);
                    usuarioNuevo.setTipo(3);

                    reference = FirebaseDatabase.getInstance().getReference("usuarios");
                    reference.child(String.valueOf(numeroKeys)).setValue(usuarioNuevo);

                    Intent intent = new Intent(login.this,menu.class);
                    intent.putExtra("correo",usuarioNuevo.getCorreo());
                    startActivity(intent);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void mensajeAviso(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            if (resultCode == RESULT_OK) {
                // Se loguea de manera exitosa
              loginUser();
            }
            if (resultCode == RESULT_CANCELED){
                mensajeAviso(getString(R.string.fallaLogin));
            }

            return;
        }

        mensajeAviso(getString(R.string.unknown_response));
    }

    public void contadorKeysUsuarios() {

        reference= FirebaseDatabase.getInstance().getReference("usuarios");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                numeroKeys = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Ha fallado la lectura de los datos", databaseError.toException());
            }

        });

    }



}
