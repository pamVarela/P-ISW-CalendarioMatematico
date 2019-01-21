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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private static final int SIGN_IN_CODE = 777;
    private Button boton;
    private static final int RC_SIGN_IN = 1;
    private static final String PATH_TOS = "";
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private Long numeroKeys;
    private static final String TAG = login.class.getSimpleName();
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        contadorKeysUsuarios();

        contadorKeysUsuarios();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        boton  = findViewById(R.id.login);

        boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });

        auth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    loginUser();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

       auth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void handleSignInResult(GoogleSignInResult result){
        if (result.isSuccess()) {
            firebaseAuthWithGoogle(result.getSignInAccount());
        } else {
            Toast.makeText(this, "No se puede iniciar sesión", Toast.LENGTH_SHORT).show();
        }
    }


    //metodo para loguear un usuario desde Firebase
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
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

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {

        boton.setVisibility(View.GONE);

        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                boton.setVisibility(View.VISIBLE);
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "No se pudo autenticar con Firebase", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuthListener != null) {
            auth.removeAuthStateListener(firebaseAuthListener);
        }
    }




}
