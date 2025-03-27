package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Registro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registro);

        EditText nombreEditText = findViewById(R.id.BarraNombre);
        EditText correoEditText = findViewById(R.id.BarraCorreo);
        EditText contrasenaEditText = findViewById(R.id.BarraContraseña);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button crearCuentaButton = findViewById(R.id.BotonRegistrar);

        crearCuentaButton.setOnClickListener(v -> {
            String nombre = nombreEditText.getText().toString().trim();
            String correo = correoEditText.getText().toString().trim();
            String contrasena = contrasenaEditText.getText().toString().trim();

            if (!nombre.isEmpty() && !correo.isEmpty() && !contrasena.isEmpty()) {
                new RegistrarUsuarioTask().execute(nombre, correo, contrasena);
            } else {
                Toast.makeText(Registro.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class RegistrarUsuarioTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String nombre = params[0];
            String correo = params[1];
            String contrasena = params[2];
            boolean exito = false;

            try {
                Class.forName("org.postgresql.Driver");

                String dbUrl = ConfigUtil.getMetaData(getApplicationContext(), "DB_URL");
                String dbUser = ConfigUtil.getMetaData(getApplicationContext(), "DB_USER");
                String dbPassword = ConfigUtil.getMetaData(getApplicationContext(), "DB_PASSWORD");

                try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                     PreparedStatement statement = connection.prepareStatement(
                             "INSERT INTO usuarios (nombre_usuario, email, contraseña) VALUES (?, ?, ?)")
                ) {
                    statement.setString(1, nombre);
                    statement.setString(2, correo);
                    statement.setString(3, contrasena);

                    int filasInsertadas = statement.executeUpdate();
                    exito = filasInsertadas > 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return exito;
        }

        @Override
        protected void onPostExecute(Boolean exito) {
            if (exito) {
                Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Registro.this, MenuPrincipal.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Registro.this, "Error en el registro", Toast.LENGTH_SHORT).show();
            }
        }
    }
}