package com.example.zeitkreis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import APIs.Chats;
import Requests_Responses.MessageRequest;
import Requests_Responses.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class Chat extends Fragment {

    private RecyclerView recyclerViewMensajes;
    private EditText editTextMensaje;
    private Chats chatsApi;
    private List<MessageResponse> mensajes;

    public Chat() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat, container, false);

        recyclerViewMensajes = view.findViewById(R.id.recyclerViewMensajes);
        editTextMensaje = view.findViewById(R.id.editTextMensaje);
        Button buttonEnviarMensaje = view.findViewById(R.id.buttonEnviarMensaje);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        chatsApi = retrofit.create(Chats.class);

        recyclerViewMensajes.setLayoutManager(new LinearLayoutManager(getContext()));

        Long agendaId = getArguments() != null ? getArguments().getLong("agendaId") : 1;

        cargarMensajes(agendaId);

        buttonEnviarMensaje.setOnClickListener(v -> {
            String texto = editTextMensaje.getText().toString();

            SharedPreferences preferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String nombreUsuario = preferences.getString("nombre_usuario", "ZZZ");

            if (!texto.isEmpty()) {
                enviarMensaje(texto, nombreUsuario, agendaId);
            }
        });

        return view;
    }

    private void cargarMensajes(Long agendaId) {
        chatsApi.obtenerMensajes(agendaId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<MessageResponse>> call, @NonNull Response<List<MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mensajes = response.body();
                    MensajeAdaptador adapter = new MensajeAdaptador(mensajes);
                    recyclerViewMensajes.setAdapter(adapter);
                } else {
                    String errorMessage = "Error al cargar los mensajes. Código de respuesta: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            errorMessage += " Detalles: " + errorBody;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MessageResponse>> call, @NonNull Throwable t) {
                String errorDetails = "Error de conexión al cargar mensajes: " + t.getMessage();
                Toast.makeText(getContext(), errorDetails, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void enviarMensaje(String texto, String autor, Long agendaId) {
        MessageRequest request = new MessageRequest(texto, autor, agendaId);

        chatsApi.enviarMensaje(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    editTextMensaje.setText("");
                    cargarMensajes(agendaId);
                } else {
                    String errorMessage = "Error al enviar el mensaje. Código de respuesta: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            errorMessage += " Detalles: " + errorBody;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                String errorDetails = "Error de conexión al enviar mensaje: " + t.getMessage();
                Toast.makeText(getContext(), errorDetails, Toast.LENGTH_LONG).show();
            }
        });
    }
}