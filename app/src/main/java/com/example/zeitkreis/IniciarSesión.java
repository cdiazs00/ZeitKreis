package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IniciarSesión extends AppCompatActivity {

    private String correo, contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        Button iniciarSesion = findViewById(R.id.BotonIniciarSesion);
        Button registrarse = findViewById(R.id.BotonRegistrarse);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            correo = extras.getString("correo");
            contrasena = extras.getString("contraseña");
        }

        iniciarSesion.setOnClickListener(v -> {
            if (correo != null && contrasena != null) {
                new VerificarUsuarioTask().execute(correo, contrasena);
            } else {
                Toast.makeText(IniciarSesión.this, "Faltan credenciales", Toast.LENGTH_SHORT).show();
            }
        });

        registrarse.setOnClickListener(v -> {
            Intent intent = new Intent(IniciarSesión.this, Registro.class);
            startActivity(intent);
            finish();
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class VerificarUsuarioTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String usuario = params[0];
            String contrasena = params[1];
            boolean resultado = false;

            try {
                Class.forName("org.postgresql.Driver");

                String dbUrl = ConfigUtil.getMetaData(getApplicationContext(), "DB_URL");
                String dbUser = ConfigUtil.getMetaData(getApplicationContext(), "DB_USER");
                String dbPassword = ConfigUtil.getMetaData(getApplicationContext(), "DB_PASSWORD");

                try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                     PreparedStatement statement = connection.prepareStatement(
                             "SELECT 1 FROM usuarios WHERE Correo_Electrónico = ? AND Contraseña = ? LIMIT 1")) {

                    statement.setString(1, usuario);
                    statement.setString(2, contrasena);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        resultado = resultSet.next();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if (resultado) {
                Intent intent = new Intent(IniciarSesión.this, MenuPrincipal.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(IniciarSesión.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}