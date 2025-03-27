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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Perfil extends AppCompatActivity {

    private TextView nombreUsuario, correoUsuario;
    private ImageView imagenPerfil;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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

        if (!correo.isEmpty()) {
            executorService.execute(() -> {
                try {
                    Class.forName("org.postgresql.Driver");

                    String dbUrl = ConfigUtil.getMetaData(getApplicationContext(), "DB_URL");
                    String dbUser = ConfigUtil.getMetaData(getApplicationContext(), "DB_USER");
                    String dbPassword = ConfigUtil.getMetaData(getApplicationContext(), "DB_PASSWORD");

                    try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                         PreparedStatement statement = connection.prepareStatement(
                                 "SELECT Nombre FROM usuarios WHERE Correo_Electrónico = ?")) {

                        statement.setString(1, correo);
                        try (ResultSet resultSet = statement.executeQuery()) {
                            if (resultSet.next()) {
                                String nombre = resultSet.getString("Nombre");
                                runOnUiThread(() -> {
                                    nombreUsuario.setText(nombre);
                                    correoUsuario.setText(correo);
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(Perfil.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show());
                }
            });
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