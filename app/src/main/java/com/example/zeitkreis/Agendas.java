package com.example.zeitkreis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.*;

import APIs.Diaries;
import Requests_Responses.AllDiariesResponse;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class Agendas extends AppCompatActivity {

    private Diaries agendaApi;
    private Button crearAgenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agendas);

        ListView listaAgendas = findViewById(R.id.chat_list_view);
        crearAgenda = findViewById(R.id.crear_agenda);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        agendaApi = retrofit.create(Diaries.class);

        crearAgenda.setOnClickListener(v -> {
            Intent intent = new Intent(Agendas.this, CrearAgenda.class);
            startActivity(intent);
        });

        obtenerYMostrarAgendas(listaAgendas);
    }

    private void obtenerYMostrarAgendas(ListView listaAgendas) {
        agendaApi.obtenerAgendas().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<AllDiariesResponse.Agenda>> call, @NonNull Response<List<AllDiariesResponse.Agenda>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AllDiariesResponse.Agenda> agendas = response.body();
                    List<String> nombresAgendas = new ArrayList<>();
                    for (AllDiariesResponse.Agenda agenda : agendas) {
                        String nombre = agenda.getNombre() != null ? agenda.getNombre() : "(Sin nombre)";
                        nombresAgendas.add(nombre);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            Agendas.this,
                            android.R.layout.simple_list_item_1,
                            nombresAgendas
                    );
                    listaAgendas.setAdapter(adapter);

                    listaAgendas.setOnItemClickListener((parent, view, position, id) -> {
                        View container = findViewById(R.id.chat);
                        if (container == null) {
                            Toast.makeText(Agendas.this, "No se encontró el contenedor del fragmento.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        AllDiariesResponse.Agenda agendaSeleccionada = agendas.get(position);
                        Long idAgenda = agendaSeleccionada.getId();
                        String nombreAgenda = agendaSeleccionada.getNombre();

                        AgendaFragments agendaFragments = new AgendaFragments();
                        Bundle args = new Bundle();
                        args.putLong("agendaId", idAgenda != null ? idAgenda : -1L);
                        args.putString("agendaNombre", nombreAgenda != null ? nombreAgenda : "(Sin nombre)");
                        agendaFragments.setArguments(args);

                        toggleCrearAgendaButton(false);

                        container.setVisibility(View.VISIBLE);
                        listaAgendas.setVisibility(View.GONE);

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.chat, agendaFragments);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    });

                } else {
                    Toast.makeText(Agendas.this, "Error al obtener agendas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AllDiariesResponse.Agenda>> call, @NonNull Throwable t) {
                Toast.makeText(Agendas.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleCrearAgendaButton(boolean isVisible) {
        if (crearAgenda != null) {
            if (isVisible) {
                crearAgenda.setVisibility(View.VISIBLE);
            } else {
                crearAgenda.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toggleCrearAgendaButton(true);
    }
}