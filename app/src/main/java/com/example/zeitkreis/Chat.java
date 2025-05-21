package com.example.zeitkreis;

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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class Chat extends Fragment {

    private RecyclerView recyclerViewMensajes;
    private EditText editTextMensaje;
    private MensajeAdaptador adapter;
    private String nombreUsuarioActual;
    private Long currentAgendaId;
    private ChatViewModel viewModel;

    private static final String TAG = "ChatFragment";

    public Chat() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        logAllSharedPreferences(preferences);
        nombreUsuarioActual = preferences.getString("nombre_usuario", "UsuarioDesconocido_Chat");
        Log.d(TAG, "onCreate: Nombre de usuario leído de SharedPreferences: " + nombreUsuarioActual);
        Log.d(TAG, "onCreate: Intentando obtener 'agendaId' de los argumentos.");
        Bundle arguments = getArguments();

        if (arguments != null) {
            Log.d(TAG, "onCreate: getArguments() no es NULL. Contenido del Bundle: " + arguments);
            if (arguments.containsKey("agendaId")) {
                Log.d(TAG, "onCreate: El Bundle CONTIENE la clave 'agendaId'.");
                currentAgendaId = arguments.getLong("agendaId", -1L);
                Log.d(TAG, "onCreate: 'agendaId' obtenido del Bundle: " + currentAgendaId);
            } else {
                Log.e(TAG, "onCreate: El Bundle NO CONTIENE la clave 'agendaId'. Se usará el valor por defecto.");
                currentAgendaId = -1L;
            }
        } else {
            Log.e(TAG, "onCreate: getArguments() es NULL. No se pueden obtener argumentos. Se usará el valor por defecto para agendaId.");
            currentAgendaId = -1L;
        }
        Log.d(TAG, "onCreate: Valor final de currentAgendaId después de procesar argumentos: " + currentAgendaId);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        Log.d(TAG, "onCreate: ChatViewModel inicializado.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat, container, false);

        recyclerViewMensajes = view.findViewById(R.id.recyclerViewMensajes);
        editTextMensaje = view.findViewById(R.id.editTextMensaje);
        Button buttonEnviarMensaje = view.findViewById(R.id.buttonEnviarMensaje);

        setupRecyclerView();
        setupObservers();
        setupListeners(buttonEnviarMensaje);

        if (currentAgendaId != null && currentAgendaId > 0) {
            viewModel.fetchMessages(currentAgendaId);
        } else {
            Log.e(TAG, "agendaId es nulo o inválido, no se pueden cargar mensajes.");
            Toast.makeText(getContext(), "Error: No se pudo determinar la agenda.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewMensajes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MensajeAdaptador(new ArrayList<>());
        recyclerViewMensajes.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.messagesList.observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                Log.d(TAG, "Observador: Lista de mensajes actualizada. Número de mensajes: " + messages.size());
                adapter.updateMessages(messages);
                if (!messages.isEmpty()) {
                    recyclerViewMensajes.scrollToPosition(messages.size() - 1);
                }
            }
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Observador: Error recibido: " + errorMsg);
                viewModel.clearErrorMessage();
            }
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                Log.d(TAG, "Observador: isLoading: " + isLoading);
            }
        });

        viewModel.messageSentSuccessfully.observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(getContext(), "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    editTextMensaje.setText("");
                } else {
                    Log.d(TAG, "Observador: Mensaje no enviado (posiblemente error ya mostrado).");
                }
                viewModel.clearMessageSentStatus();
            }
        });
    }

    private void setupListeners(Button buttonEnviarMensaje) {
        buttonEnviarMensaje.setOnClickListener(v -> {
            String texto = editTextMensaje.getText().toString().trim();

            Log.d(TAG, "Botón Enviar pulsado. Usuario actual: " + nombreUsuarioActual);

            if (nombreUsuarioActual.equals("UsuarioDesconocido_Chat") || nombreUsuarioActual.equals("ZZZ") || nombreUsuarioActual.isEmpty()) {
                Log.e(TAG, "Intento de enviar mensaje con nombre de usuario por defecto o inválido: " + nombreUsuarioActual);
                Toast.makeText(getContext(), "Error: Nombre de usuario no configurado. Por favor, reinicie sesión.", Toast.LENGTH_LONG).show();
                return;
            }

            if (!texto.isEmpty() && currentAgendaId != null && currentAgendaId > 0) {
                viewModel.sendMessage(texto, nombreUsuarioActual, currentAgendaId);
            } else if (texto.isEmpty()) {
                Toast.makeText(getContext(), "El mensaje no puede estar vacío.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error: No se pudo determinar la agenda para enviar el mensaje.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error al enviar: texto vacío o currentAgendaId inválido (" + currentAgendaId + ")");
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
    if (currentAgendaId != null && currentAgendaId > 0) {
    viewModel.fetchMessages(currentAgendaId);
        }
    }
}