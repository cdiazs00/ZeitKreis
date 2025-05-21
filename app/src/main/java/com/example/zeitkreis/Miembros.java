package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;

public class Miembros extends Fragment {

    private static final String TAG = "MiembrosFragment";
    private MiembrosViewModel viewModel;
    private MiembroAdaptador adapter;
    private RecyclerView recyclerViewMiembros;
    private ProgressBar progressBarMiembros;

    private Long currentAgendaId = -1L;

    public Miembros() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentAgendaId = getArguments().getLong("agendaId", -1L);
            String currentAgendaNombre = getArguments().getString("agendaNombre", "(Sin nombre)");
            Log.d(TAG, "onCreate: agendaId=" + currentAgendaId + ", agendaNombre=" + currentAgendaNombre);
        } else {
            Log.e(TAG, "onCreate: Argumentos no recibidos.");
        }
        viewModel = new ViewModelProvider(this).get(MiembrosViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.miembros, container, false);

        recyclerViewMiembros = view.findViewById(R.id.recyclerViewMiembros);
        progressBarMiembros = view.findViewById(R.id.progressBarMiembros);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupObservers();

        if (currentAgendaId != null && currentAgendaId > 0) {
            viewModel.fetchMiembros(currentAgendaId);
        } else {
            Toast.makeText(getContext(), "No se pudo determinar la agenda para cargar miembros.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "agendaId inválido: " + currentAgendaId);
        }
    }

    private void setupRecyclerView() {
        adapter = new MiembroAdaptador(new ArrayList<>());
        recyclerViewMiembros.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMiembros.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.miembrosList.observe(getViewLifecycleOwner(), miembros -> {
            if (miembros != null) {
                Log.d(TAG, "Lista de miembros actualizada. Número de miembros: " + miembros.size());
                adapter.updateMiembros(miembros);
            }
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBarMiembros.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                Log.d(TAG, "isLoading: " + isLoading);
            }
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error recibido: " + errorMsg);
                viewModel.clearErrorMessage();
            }
        });
    }
}