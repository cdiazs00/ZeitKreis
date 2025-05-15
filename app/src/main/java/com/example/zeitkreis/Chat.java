package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import APIs.Chats;
import Requests_Responses.MessageRequest;
import Requests_Responses.MessageResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Chat extends Fragment {

    private RecyclerView recyclerViewMensajes;
    private EditText editTextMensaje;
    private Chats chatsApi;
    private List<MessageResponse> mensajes;
    private MensajeAdaptador adapter;
    private String nombreUsuarioActual;
    private Long currentAgendaId;

    private static final String TAG = "ChatFragment";

    public Chat() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        logAllSharedPreferences(preferences);
        nombreUsuarioActual = preferences.getString("nombre_usuario", "UsuarioDesconocido_Chat");
        Log.d(TAG, "onCreate: Nombre de usuario leído de SharedPreferences: " + nombreUsuarioActual);

        if (getArguments() != null) {
            currentAgendaId = getArguments().getLong("agendaId", 1L);
        } else {
            currentAgendaId = 1L;
        }
        Log.d(TAG, "onCreate: agendaId: " + currentAgendaId);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.d(TAG + "_OkHttp", message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        chatsApi = retrofit.create(Chats.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat, container, false);

        recyclerViewMensajes = view.findViewById(R.id.recyclerViewMensajes);
        editTextMensaje = view.findViewById(R.id.editTextMensaje);
        Button buttonEnviarMensaje = view.findViewById(R.id.buttonEnviarMensaje);

        recyclerViewMensajes.setLayoutManager(new LinearLayoutManager(getContext()));
        if (mensajes == null) {
            mensajes = new ArrayList<>();
        }
        if (adapter == null) {
            adapter = new MensajeAdaptador(mensajes);
            recyclerViewMensajes.setAdapter(adapter);
        }

        if (currentAgendaId != null) {
            cargarMensajes(currentAgendaId);
        } else {
            Log.e(TAG, "agendaId es nulo, no se pueden cargar mensajes.");
            Toast.makeText(getContext(), "Error: No se pudo determinar la agenda.", Toast.LENGTH_SHORT).show();
        }


        buttonEnviarMensaje.setOnClickListener(v -> {
            String texto = editTextMensaje.getText().toString().trim();

            Log.d(TAG, "Botón Enviar pulsado. Nombre de usuario actual para enviar: " + nombreUsuarioActual);

            if (nombreUsuarioActual.equals("UsuarioDesconocido_Chat") || nombreUsuarioActual.equals("ZZZ")) {
                Log.e(TAG, "Intento de enviar mensaje con nombre de usuario por defecto o inválido: " + nombreUsuarioActual);
                Toast.makeText(getContext(), "Error: Nombre de usuario no configurado. Por favor, reinicie sesión.", Toast.LENGTH_LONG).show();
                return;
            }

            if (!texto.isEmpty() && currentAgendaId != null) {
                enviarMensaje(texto, nombreUsuarioActual, currentAgendaId);
            } else if (texto.isEmpty()){
                Toast.makeText(getContext(), "El mensaje no puede estar vacío.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error: No se pudo determinar la agenda para enviar el mensaje.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void cargarMensajes(Long agendaId) {
        Log.d(TAG, "Cargando mensajes para agendaId: " + agendaId);
        chatsApi.obtenerMensajes(agendaId).enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<MessageResponse>> call, @NonNull Response<List<MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Mensajes recibidos: " + response.body().size());
                    mensajes.clear();
                    mensajes.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    if (!mensajes.isEmpty()) {
                        recyclerViewMensajes.scrollToPosition(mensajes.size() - 1);
                    }
                } else {
                    String errorMessage = "Error al cargar los mensajes. Código: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage += " Detalles: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al parsear errorBody en cargarMensajes", e);
                    }
                    Log.e(TAG, errorMessage);
                    Toast.makeText(getContext(), "Error al cargar mensajes. " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MessageResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error de conexión al cargar mensajes", t);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void enviarMensaje(String texto, String autor, Long agendaId) {
        Log.d(TAG, "Preparando para enviar mensaje - Texto: " + texto + ", Autor: " + autor + ", AgendaId: " + agendaId);
        MessageRequest request = new MessageRequest(texto, autor, agendaId);

        chatsApi.enviarMensaje(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Mensaje enviado exitosamente. Respuesta del servidor: " + response.body());
                    Toast.makeText(getContext(), "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    editTextMensaje.setText("");
                    cargarMensajes(agendaId);
                } else {
                    String errorMessage = "Error al enviar el mensaje. Código: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage += " Detalles: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al parsear errorBody en enviarMensaje", e);
                    }
                    Log.e(TAG, errorMessage);
                    Toast.makeText(getContext(), "Error al enviar mensaje. " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error de conexión al enviar mensaje", t);
                Toast.makeText(getContext(), "Error de conexión al enviar: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logAllSharedPreferences(SharedPreferences prefs) {
        Map<String, ?> allEntries = prefs.getAll();
        if (allEntries.isEmpty()) {
            Log.d(TAG, "SharedPreferences 'UserPrefs' está vacío.");
            return;
        }
        Log.d(TAG, "Contenido completo de SharedPreferences 'UserPrefs':");
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d(TAG, entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}