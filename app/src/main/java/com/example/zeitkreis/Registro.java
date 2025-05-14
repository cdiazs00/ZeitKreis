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

import APIs.Users;
import Requests_Responses.RegisterResponse;
import Requests_Responses.RegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Registro extends AppCompatActivity {

    private EditText barraNombre, barraCorreo, barraContrasena;
    private Users usuariosApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        barraNombre = findViewById(R.id.BarraNombre);
        barraCorreo = findViewById(R.id.BarraCorreo);
        barraContrasena = findViewById(R.id.BarraContraseña);
        Button registrarse = findViewById(R.id.BotonRegistrar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        usuariosApi = retrofit.create(Users.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barraNombre.setText(extras.getString("nombreUsuario", ""));
            barraCorreo.setText(extras.getString("email", ""));
            barraContrasena.setText(extras.getString("password", ""));
        }

        registrarse.setOnClickListener(v -> {
            String nombre = barraNombre.getText().toString().trim();
            String correo = barraCorreo.getText().toString().trim();
            String password = barraContrasena.getText().toString().trim();

            if (!nombre.isEmpty() && !correo.isEmpty() && !password.isEmpty()) {
                UsuarioUnico(nombre, correo, password);
            } else {
                Toast.makeText(Registro.this, "Faltan credenciales", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UsuarioUnico(String nombre, String correo, String password) {
        RegisterRequest request = new RegisterRequest(nombre, correo, password);

        Call<RegisterResponse> call = usuariosApi.registrarUsuario(request);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean resultado = response.body().isSuccess();
                    if (resultado) {
                        guardarUsuarioEnSesion(correo);
                        Intent intent = new Intent(Registro.this, MenuPrincipal.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Registro.this, "Este usuario ya está registrado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        Toast.makeText(Registro.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
                        System.out.println("Error body: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                Toast.makeText(Registro.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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