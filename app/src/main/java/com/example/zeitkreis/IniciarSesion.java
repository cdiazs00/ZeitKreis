package com.example.zeitkreis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import APIs.Users;
import Requests_Responses.LoginRequest;
import Requests_Responses.LoginResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IniciarSesion extends AppCompatActivity {

    private EditText barraCorreo, barraContrasena;
    private Users usuariosApi;
    private static final String TAG = "IniciarSesion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String email = preferences.getString("email", null);

        if (email != null) {
            Intent intent = new Intent(this, MenuPrincipal.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.iniciar_sesion);

        barraCorreo = findViewById(R.id.BarraCorreo);
        barraContrasena = findViewById(R.id.BarraContraseña);
        Button iniciarSesion = findViewById(R.id.BotonIniciarSesion);
        Button registrarse = findViewById(R.id.BotonRegistrarse);
        TextView cambiarContrasena = findViewById(R.id.ContraseñaOlvidadaTexto);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.d(TAG + "_OkHttp", message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.239.125:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        usuariosApi = retrofit.create(Users.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barraCorreo.setText(extras.getString("email", ""));
            barraContrasena.setText(extras.getString("password", ""));
        }

        iniciarSesion.setOnClickListener(v -> {
            String correoInput = barraCorreo.getText().toString().trim();
            String passwordInput = barraContrasena.getText().toString().trim();

            if (!correoInput.isEmpty() && !passwordInput.isEmpty()) {
                verificarUsuario(correoInput, passwordInput);
            } else {
                Toast.makeText(IniciarSesion.this, "Faltan credenciales", Toast.LENGTH_SHORT).show();
            }
        });

        registrarse.setOnClickListener(v -> {
            Intent intent = new Intent(IniciarSesion.this, Registro.class);
            startActivity(intent);
        });

        cambiarContrasena.setOnClickListener(v -> {
            Intent intent = new Intent(IniciarSesion.this, CambiarContraseña.class);
            startActivity(intent);
        });
    }

    private void verificarUsuario(String correo, String password) {
        LoginRequest request = new LoginRequest(correo, password);
        Log.d(TAG, "Enviando petición de login para: " + correo);

        Call<LoginResponse> call = usuariosApi.loginUsuario(request);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Respuesta de login - Success: " + loginResponse.isSuccess() + ", Message: " + loginResponse.getMessage() + ", NombreUsuario: " + loginResponse.getNombreUsuario());
                    if (loginResponse.isSuccess()) {
                        String nombreUsuarioApi = loginResponse.getNombreUsuario();
                        if (nombreUsuarioApi != null && !nombreUsuarioApi.trim().isEmpty()) {
                            guardarUsuarioEnSesion(correo, nombreUsuarioApi);
                            Intent intent = new Intent(IniciarSesion.this, MenuPrincipal.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Nombre de usuario recibido de la API es nulo o vacío.");
                            Toast.makeText(IniciarSesion.this, "Error: No se recibió el nombre de usuario del servidor.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(IniciarSesion.this, "Credenciales incorrectas: " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorBodyString = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBodyString = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al parsear errorBody", e);
                    }
                    Log.e(TAG, "Error en login - Código: " + response.code() + ", Cuerpo del error: " + errorBodyString);
                    Toast.makeText(IniciarSesion.this, "Error en el servidor: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Fallo en la llamada de login", t);
                Toast.makeText(IniciarSesion.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarUsuarioEnSesion(String correo, String nombreUsuario) {
        Log.d(TAG, "Guardando en SharedPreferences - email: " + correo + ", nombre_usuario: " + nombreUsuario);
        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", correo);
        editor.putString("nombre_usuario", nombreUsuario);
        boolean commitExitoso = editor.commit();
        Log.d(TAG, "SharedPreferences commit exitoso para nombre_usuario: " + commitExitoso + ". Nombre guardado: " + nombreUsuario);
        if (!commitExitoso) {
            Log.e(TAG, "¡FALLO AL GUARDAR EN SHARED PREFERENCES!");
        }
    }
}