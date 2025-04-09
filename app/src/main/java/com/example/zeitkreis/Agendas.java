package com.example.zeitkreis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Agendas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agendas);

        Button CrearAgenda = findViewById(R.id.crear_agenda);

        CrearAgenda.setOnClickListener(v -> {
            Intent intent = new Intent(Agendas.this, CrearAgenda.class);
            startActivity(intent);
        });
    }
}