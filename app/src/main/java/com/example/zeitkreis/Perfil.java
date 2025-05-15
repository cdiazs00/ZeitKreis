package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Perfil extends AppCompatActivity {

    private TextView textViewNombreUsuario;
    private ImageView imageViewImagenPerfil;
    private static final String TAG = "PerfilActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        Log.d(TAG, "onCreate: Iniciando PerfilActivity");

        textViewNombreUsuario = findViewById(R.id.nombre_usuario);
        imageViewImagenPerfil = findViewById(R.id.imagen_perfil);

        Button botonAjustesGenerales = findViewById(R.id.Ajustes_Generales);
        Button botonAjustesCuenta = findViewById(R.id.Ajustes_de_Cuenta);
        Button botonCerrarSesion = findViewById(R.id.Cerrar_sesion);

        cargarDatosUsuario();

        botonAjustesGenerales.setOnClickListener(v -> {
            Log.d(TAG, "Botón Ajustes Generales pulsado");
            Toast.makeText(this, "Ajustes Generales (Próximamente)", Toast.LENGTH_SHORT).show();
        });

        botonAjustesCuenta.setOnClickListener(v -> {
            Log.d(TAG, "Botón Ajustes de Cuenta pulsado");
            Toast.makeText(this, "Ajustes de Cuenta (Próximamente)", Toast.LENGTH_SHORT).show();
        });

        botonCerrarSesion.setOnClickListener(v -> {
            Log.d(TAG, "Botón Cerrar Sesión pulsado");
            cerrarSesion();
        });
    }

    @SuppressLint("SetTextI18n")
    private void cargarDatosUsuario() {
        Log.d(TAG, "cargarDatosUsuario: Cargando datos desde SharedPreferences");
        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        String nombre = preferences.getString("nombre_usuario", "");

        Log.d(TAG, "Datos leídos: '" + nombre);

        if (!nombre.isEmpty()) {
            textViewNombreUsuario.setText(nombre);
        } else {
            Log.w(TAG, "Nombre de usuario no encontrado o vacío en SharedPreferences.");
            textViewNombreUsuario.setText("Nombre no disponible");
        }

        if (nombre.isEmpty()) {
            Toast.makeText(this, "No se pudieron cargar los datos del usuario. Intente iniciar sesión de nuevo.", Toast.LENGTH_LONG).show();
        }
    }

    private void cargarImagenPerfil() {
        Log.d(TAG, "cargarImagenPerfil: Intentando cargar imagen de perfil");
        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String imageUriString = preferences.getString("imagen_perfil_uri", null);

        if (imageUriString != null) {
            Log.d(TAG, "URI de imagen encontrada: " + imageUriString);
            try {
                Uri imageUri = Uri.parse(imageUriString);
                imageViewImagenPerfil.setImageURI(imageUri);
            } catch (Exception e) {
                Log.e(TAG, "Error al parsear o cargar la URI de la imagen de perfil", e);
            }
        } else {
            Log.d(TAG, "No se encontró URI de imagen de perfil en SharedPreferences.");
        }
    }

    private void cerrarSesion() {
        Log.d(TAG, "cerrarSesion: Limpiando SharedPreferences y redirigiendo a IniciarSesion");
        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        boolean commitExitoso = editor.commit();
        Log.d(TAG, "SharedPreferences clear commit exitoso: " + commitExitoso);


        Intent intent = new Intent(Perfil.this, IniciarSesion.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Actividad Perfil reanudada, recargando datos de usuario.");
        cargarDatosUsuario();
        cargarImagenPerfil();
    }
}