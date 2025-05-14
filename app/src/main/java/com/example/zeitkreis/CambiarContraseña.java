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
import Requests_Responses.NewPasswordRequest;
import Requests_Responses.NewPasswordResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CambiarContraseña extends AppCompatActivity {

    private EditText barraCorreo, barraContrasena, barraCambiarContrasena;
    private Users usuariosApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambiar_contrasena);

        barraCorreo = findViewById(R.id.BarraCorreo);
        barraContrasena = findViewById(R.id.BarraContraseña);
        barraCambiarContrasena = findViewById(R.id.BarraNuevaContraseña);
        Button cambiarContrasena = findViewById(R.id.BotonCambiarContrasena);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        usuariosApi = retrofit.create(Users.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barraCorreo.setText(extras.getString("email", ""));
            barraContrasena.setText(extras.getString("password", ""));
        }

        cambiarContrasena.setOnClickListener(v -> {
            String correo = barraCorreo.getText().toString().trim();
            String password = barraContrasena.getText().toString().trim();
            String newPassword = barraCambiarContrasena.getText().toString().trim();

            if (!correo.isEmpty() && !password.isEmpty() && !newPassword.isEmpty()) {
                nuevaContraseña(correo, password, newPassword);
            } else {
                Toast.makeText(CambiarContraseña.this, "Faltan credenciales", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void nuevaContraseña(String correo, String password, String newPassword) {
        NewPasswordRequest request = new NewPasswordRequest(correo, password, newPassword);

        Call<NewPasswordResponse> call = usuariosApi.cambiarContrasena(request);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewPasswordResponse> call, @NonNull Response<NewPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean resultado = response.body().isSuccess();
                    if (resultado) {
                        guardarUsuarioEnSesion(correo);
                        Intent intent = new Intent(CambiarContraseña.this, MenuPrincipal.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CambiarContraseña.this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        Toast.makeText(CambiarContraseña.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
                        System.out.println("Error body: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewPasswordResponse> call, @NonNull Throwable t) {
                Toast.makeText(CambiarContraseña.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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