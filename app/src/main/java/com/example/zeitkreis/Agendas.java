package com.example.zeitkreis;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.*;

import Requests_Responses.AllDiariesResponse;

public class Agendas extends AppCompatActivity {

    private AgendasViewModel viewModel;
    private ListView listaAgendasView;
    private ArrayAdapter<String> agendasAdapter;
    private List<AllDiariesResponse.Agenda> currentAgendasList;
    private Button crearAgendaButton;
    private View fragmentContainer;

    private static final String TAG = "AgendasActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agendas);

        listaAgendasView = findViewById(R.id.chat_list_view);
        crearAgendaButton = findViewById(R.id.crear_agenda);
        fragmentContainer = findViewById(R.id.chat);

        currentAgendasList = new ArrayList<>();
        agendasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listaAgendasView.setAdapter(agendasAdapter);

        viewModel = new ViewModelProvider(this).get(AgendasViewModel.class);

        setupObservers();
        setupListeners();

        if (savedInstanceState == null) {
            viewModel.fetchAgendas();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                Log.d(TAG, "Back stack está vacío, mostrando lista de agendas.");
                listaAgendasView.setVisibility(View.VISIBLE);
                if (fragmentContainer != null) {
                    fragmentContainer.setVisibility(View.GONE);
                }
                toggleCrearAgendaButton(true);
            }
        });
    }

    private void setupObservers() {
        viewModel.agendasList.observe(this, agendas -> {
            Log.d(TAG, "Lista de agendas actualizada. Número de agendas: " + (agendas != null ? agendas.size() : 0));
            if (agendas != null) {
                currentAgendasList.clear();
                currentAgendasList.addAll(agendas);

                List<String> nombresAgendas = new ArrayList<>();
                for (AllDiariesResponse.Agenda agenda : agendas) {
                    String nombre = agenda.getNombre() != null ? agenda.getNombre() : "(Sin nombre)";
                    nombresAgendas.add(nombre);
                }
                agendasAdapter.clear();
                agendasAdapter.addAll(nombresAgendas);
                agendasAdapter.notifyDataSetChanged();
            }
        });

        viewModel.errorMessage.observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(Agendas.this, errorMsg, Toast.LENGTH_LONG).show();
                viewModel._errorMessage.setValue(null);
            }
        });
    }

    private void setupListeners() {
        crearAgendaButton.setOnClickListener(v -> {
            Intent intent = new Intent(Agendas.this, CrearAgenda.class);
            startActivity(intent);
        });

        listaAgendasView.setOnItemClickListener((parent, view, position, id) -> {
            if (fragmentContainer == null) {
                Toast.makeText(Agendas.this, "No se encontró el contenedor del fragmento.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Contenedor R.id.chat es nulo.");
                return;
            }

            if (currentAgendasList != null && position < currentAgendasList.size()) {
                AllDiariesResponse.Agenda agendaSeleccionada = currentAgendasList.get(position);
                Long idAgenda = agendaSeleccionada.getId();
                String nombreAgenda = agendaSeleccionada.getNombre();

                Log.d(TAG, "Agenda seleccionada: " + nombreAgenda + " (ID: " + idAgenda + ")");

                AgendaFragments agendaFragments = new AgendaFragments();
                Bundle args = new Bundle();
                args.putLong("agendaId", idAgenda != null ? idAgenda : -1L);
                args.putString("agendaNombre", nombreAgenda != null ? nombreAgenda : "(Sin nombre)");
                agendaFragments.setArguments(args);

                toggleCrearAgendaButton(false);

                fragmentContainer.setVisibility(View.VISIBLE);
                listaAgendasView.setVisibility(View.GONE);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.chat, agendaFragments);
                transaction.addToBackStack("AgendaFragmentsTransaction");
                transaction.commit();
            } else {
                Log.e(TAG, "Error: currentAgendasList es nulo o la posición está fuera de los límites.");
            }
        });
    }


    private void toggleCrearAgendaButton(boolean isVisible) {
        if (crearAgendaButton != null) {
            crearAgendaButton.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
            Log.d(TAG, "Botón Crear Agenda visibilidad: " + isVisible);
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentContainer != null && fragmentContainer.getVisibility() == View.VISIBLE &&
                getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Log.d(TAG, "onBackPressed: AgendaFragments está visible, dejando que el sistema lo maneje.");
        } else {
            Log.d(TAG, "onBackPressed: No hay fragmentos en el backstack o el contenedor no está visible. Comportamiento por defecto.");
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            listaAgendasView.setVisibility(View.VISIBLE);
            if (fragmentContainer != null) {
                fragmentContainer.setVisibility(View.GONE);
            }
            toggleCrearAgendaButton(true);
        } else {
            listaAgendasView.setVisibility(View.GONE);
            if (fragmentContainer != null) {
                fragmentContainer.setVisibility(View.VISIBLE);
            }
            toggleCrearAgendaButton(false);
        }
    }
}