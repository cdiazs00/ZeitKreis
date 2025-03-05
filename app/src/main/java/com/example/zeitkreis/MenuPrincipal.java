package com.example.zeitkreis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);

        Button Agendas = findViewById(R.id.BotonAgenda);
        Button CalendarioGlobal = findViewById(R.id.BotonCalendario);
        Button Amigos = findViewById(R.id.BotonAmigos);

        Agendas.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, Agendas.class);
            startActivity(intent);
            finish();
        });

        CalendarioGlobal.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, CalendarioGlobal.class);
            startActivity(intent);
            finish();
        });

        Amigos.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPrincipal.this, Amigos.class);
            startActivity(intent);
            finish();
        });
    }
}