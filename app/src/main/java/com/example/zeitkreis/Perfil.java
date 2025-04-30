package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Perfil extends AppCompatActivity {

    private TextView nombreUsuario, correoUsuario;
    private ImageView imagenPerfil;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.perfil);

        nombreUsuario = findViewById(R.id.nombre_usuario);
        correoUsuario = findViewById(R.id.Correo_usuario);
        imagenPerfil = findViewById(R.id.imagen_perfil);

        Button ajustesGenerales = findViewById(R.id.Ajustes_Generales);
        Button ajustesCuenta = findViewById(R.id.Ajustes_de_Cuenta);
        Button cerrarSesion = findViewById(R.id.Cerrar_sesion);

        cargarDatosUsuario();
        cargarImagenPerfil();

        ajustesGenerales.setOnClickListener(v -> Toast.makeText(this, "Ajustes Generales", Toast.LENGTH_SHORT).show());
        ajustesCuenta.setOnClickListener(v -> Toast.makeText(this, "Ajustes de Cuenta", Toast.LENGTH_SHORT).show());
        cerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cargarDatosUsuario() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String correo = preferences.getString("correo", "");
        String nombre = preferences.getString("nombre", "");

        if (!correo.isEmpty() && !nombre.isEmpty()) {
            nombreUsuario.setText(nombre);
            correoUsuario.setText(correo);
        } else {
            Toast.makeText(this, "Datos de usuario no disponibles", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarImagenPerfil() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String imageUriString = preferences.getString("imagen_perfil", null);

        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            imagenPerfil.setImageURI(imageUri);
        }
    }

    private void cerrarSesion() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(Perfil.this, IniciarSesion.class);
        startActivity(intent);
        finish();
    }
}