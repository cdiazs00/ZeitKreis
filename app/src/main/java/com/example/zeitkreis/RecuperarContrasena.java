package com.example.zeitkreis;

import android.annotation.SuppressLint;
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
import java.sql.ResultSet;

public class RecuperarContrasena extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registro);

        EditText correoEditText = findViewById(R.id.BarraNombre);
        EditText contrasenaEditText = findViewById(R.id.BarraContraseña);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText repetirContrasenaEditText = findViewById(R.id.BarraNuevaContraseña);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button cambiarContrasenaButton = findViewById(R.id.BotonRegistrar);

        cambiarContrasenaButton.setOnClickListener(v -> {
            String correo = correoEditText.getText().toString().trim();
            String contrasena = contrasenaEditText.getText().toString().trim();
            String repetirContrasena = repetirContrasenaEditText.getText().toString().trim();

            if (!correo.isEmpty() && !contrasena.isEmpty() && !repetirContrasena.isEmpty()) {
                if (contrasena.equals(repetirContrasena)) {
                    new ActualizarContrasenaTask().execute(correo, contrasena);
                } else {
                    Toast.makeText(RecuperarContrasena.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RecuperarContrasena.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class ActualizarContrasenaTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String correo = params[0];
            String nuevaContrasena = params[1];
            boolean exito = false;

            try {
                Class.forName("org.postgresql.Driver");

                String dbUrl = ConfigUtil.getMetaData(getApplicationContext(), "DB_URL");
                String dbUser = ConfigUtil.getMetaData(getApplicationContext(), "DB_USER");
                String dbPassword = ConfigUtil.getMetaData(getApplicationContext(), "DB_PASSWORD");

                try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                     PreparedStatement selectStatement = connection.prepareStatement(
                             "SELECT * FROM usuarios WHERE Correo_Electrónico = ?");
                     PreparedStatement updateStatement = connection.prepareStatement(
                             "UPDATE usuarios SET Contraseña = ? WHERE Correo_Electrónico = ?")) {

                    selectStatement.setString(1, correo);
                    ResultSet resultSet = selectStatement.executeQuery();

                    if (resultSet.next()) {
                        updateStatement.setString(1, nuevaContrasena);
                        updateStatement.setString(2, correo);
                        int filasActualizadas = updateStatement.executeUpdate();
                        exito = filasActualizadas > 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return exito;
        }

        @Override
        protected void onPostExecute(Boolean exito) {
            if (exito) {
                Toast.makeText(RecuperarContrasena.this, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RecuperarContrasena.this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
            }
        }
    }
}