package com.example.zeitkreis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import APIs.LoginRequest;
import APIs.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IniciarSesion extends AppCompatActivity {

    private EditText barraCorreo, barraContrasena;
    private Users usuarioApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciar_sesion);

        barraCorreo = findViewById(R.id.BarraCorreo);
        barraContrasena = findViewById(R.id.BarraContraseña);
        Button iniciarSesion = findViewById(R.id.BotonIniciarSesion);
        Button registrarse = findViewById(R.id.BotonRegistrarse);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/usuarios/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        usuarioApi = retrofit.create(Users.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barraCorreo.setText(extras.getString("email", ""));
            barraContrasena.setText(extras.getString("password", ""));
        }

        iniciarSesion.setOnClickListener(v -> {
            String correo = barraCorreo.getText().toString().trim();
            String password = barraContrasena.getText().toString().trim();

            if (!correo.isEmpty() && !password.isEmpty()) {
                verificarUsuario(correo, password);
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

    private void verificarUsuario(String correo, String password) {
        LoginRequest request = new LoginRequest(correo, password);

        Call<LoginResponse> call = usuarioApi.loginUsuario(request);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean resultado = response.body().isSuccess();
                    if (resultado) {
                        guardarUsuarioEnSesion(correo);
                        Intent intent = new Intent(IniciarSesion.this, MenuPrincipal.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(IniciarSesion.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        Toast.makeText(IniciarSesion.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
                        System.out.println("Error body: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Toast.makeText(IniciarSesion.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void guardarUsuarioEnSesion(String correo) {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", correo);
        editor.apply();
    }
}