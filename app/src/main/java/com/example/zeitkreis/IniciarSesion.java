package com.example.zeitkreis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IniciarSesion extends AppCompatActivity {

    private EditText barraCorreo, barraContrasena;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.iniciarsesion);

        barraCorreo = findViewById(R.id.BarraCorreo);
        barraContrasena = findViewById(R.id.BarraContraseña);
        Button iniciarSesion = findViewById(R.id.BotonIniciarSesion);
        Button registrarse = findViewById(R.id.BotonRegistrarse);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barraCorreo.setText(extras.getString("correo", ""));
            barraContrasena.setText(extras.getString("contraseña", ""));
        }

        iniciarSesion.setOnClickListener(v -> {
            String correo = barraCorreo.getText().toString().trim();
            String contrasena = barraContrasena.getText().toString().trim();

            if (!correo.isEmpty() && !contrasena.isEmpty()) {
                verificarUsuario(correo, contrasena);
            } else {
                Toast.makeText(IniciarSesion.this, "Faltan credenciales", Toast.LENGTH_SHORT).show();
            }
        });

        registrarse.setOnClickListener(v -> {
            Intent intent = new Intent(IniciarSesion.this, Registro.class);
            startActivity(intent);
            finish();
        });
    }

    private void verificarUsuario(String correo, String contrasena) {
        executorService.execute(() -> {
            boolean resultado = false;
            try {
                Class.forName("org.postgresql.Driver");

                String dbUrl = ConfigUtil.getMetaData(getApplicationContext(), "DB_URL");
                String dbUser = ConfigUtil.getMetaData(getApplicationContext(), "DB_USER");
                String dbPassword = ConfigUtil.getMetaData(getApplicationContext(), "DB_PASSWORD");

                try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                     PreparedStatement statement = connection.prepareStatement(
                             "SELECT 1 FROM nombre_usuario WHERE email = ? AND contraseña = ? LIMIT 1")) {

                    statement.setString(1, correo);
                    statement.setString(2, hashPassword(contrasena));

                    try (ResultSet resultSet = statement.executeQuery()) {
                        resultado = resultSet.next();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean finalResultado = resultado;
            runOnUiThread(() -> {
                if (finalResultado) {
                    guardarUsuarioEnSesion(correo);
                    Intent intent = new Intent(IniciarSesion.this, MenuPrincipal.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(IniciarSesion.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void guardarUsuarioEnSesion(String correo) {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("correo", correo);
        editor.apply();
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}