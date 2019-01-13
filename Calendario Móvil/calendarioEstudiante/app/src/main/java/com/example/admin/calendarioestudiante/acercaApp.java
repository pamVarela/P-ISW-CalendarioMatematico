package com.example.admin.calendarioestudiante;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class acercaApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);

        final TextView textoTitulo = findViewById(R.id.textoTitulo);
        final TextView textoAcerca = findViewById(R.id.textoAcerca);


    }
}
