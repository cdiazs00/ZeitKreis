package com.example.zeitkreis;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

import APIs.NewDiaryRequest;
import APIs.NewDiaryResponse;
import APIs.UserSearchRequest;
import APIs.UserSearchResponse;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrearAgenda extends AppCompatActivity {

    private AutoCompleteTextView barraMiembros;
    private EditText campoNombreAgenda;
    private TextView miembrosSeleccionados;
    private Diaries agendaApi;

    private String usuarioActivo = "nombreUsuario";
    private final List<Long> idMiembros = new ArrayList<>();
    private final Map<String, Long> nombreAIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_agenda);

        barraMiembros = findViewById(R.id.BarraMiembros);
        campoNombreAgenda = findViewById(R.id.BarraNombre);
        miembrosSeleccionados = findViewById(R.id.MiembrosSeleccionados);
        Button botonCrearAgenda = findViewById(R.id.BotonCrearAgenda);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        agendaApi = retrofit.create(Diaries.class);

        barraMiembros.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{}));
        barraMiembros.setThreshold(1);

        barraMiembros.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    buscarYActualizarSugerencias(s.toString());
                }
            }
        });

        barraMiembros.setOnItemClickListener((parent, view, position, id) -> {
            String miembroSeleccionado = parent.getItemAtPosition(position).toString();
            if (!miembroSeleccionado.isEmpty()) {
                agregarMiembro(miembroSeleccionado);
                barraMiembros.setText("");
                actualizarMiembrosSeleccionados();
            }
        });

        botonCrearAgenda.setOnClickListener(v -> {
            String nombreAgenda = campoNombreAgenda.getText().toString().trim();

            if (nombreAgenda.isEmpty() || idMiembros.isEmpty()) {
                Toast.makeText(this, "Ingresa un nombre y al menos un miembro", Toast.LENGTH_SHORT).show();
                return;
            }

            crearNuevaAgenda(nombreAgenda);
        });
    }

    private void buscarYActualizarSugerencias(String query) {
        UserSearchRequest request = new UserSearchRequest(query);

        agendaApi.buscarUsuarios(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserSearchResponse> call, @NonNull Response<UserSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<UserSearchResponse.Usuario> usuarios = response.body().getUsuarios();
                    List<String> nombres = new ArrayList<>();

                    for (UserSearchResponse.Usuario usuario : usuarios) {
                        nombreAIdMap.put(usuario.getNombreUsuario(), usuario.getId());
                        nombres.add(usuario.getNombreUsuario());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CrearAgenda.this,
                            android.R.layout.simple_dropdown_item_1line, nombres);
                    barraMiembros.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserSearchResponse> call, @NonNull Throwable t) {
                Toast.makeText(CrearAgenda.this, "Error al buscar usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarMiembro(String nombreUsuario) {
        if (!nombreAIdMap.containsKey(nombreUsuario)) {
            Toast.makeText(this, "No se encontró el ID de " + nombreUsuario, Toast.LENGTH_SHORT).show();
            return;
        }

        Long id = nombreAIdMap.get(nombreUsuario);
        if (!idMiembros.contains(id)) {
            idMiembros.add(id);
            Toast.makeText(this, nombreUsuario + " agregado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, nombreUsuario + " ya está en la lista", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarMiembrosSeleccionados() {
        StringBuilder miembrosText = new StringBuilder("Miembros: ");
        for (Long id : idMiembros) {
            String nombre = getNombreMiembro(id);
            miembrosText.append(nombre).append(", ");
        }
        miembrosSeleccionados.setText(miembrosText.toString());
    }

    private String getNombreMiembro(Long id) {
        return nombreAIdMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(id))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Desconocido");
    }

    private void crearNuevaAgenda(String nombreAgenda) {
        NewDiaryRequest request = new NewDiaryRequest(nombreAgenda, idMiembros);

        agendaApi.crearAgenda(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewDiaryResponse> call, @NonNull Response<NewDiaryResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CrearAgenda.this, "Agenda creada exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CrearAgenda.this, "Error al crear la agenda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewDiaryResponse> call, @NonNull Throwable t) {
                Toast.makeText(CrearAgenda.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getUsuarioActivo() {
        return usuarioActivo;
    }

    public void setUsuarioActivo(String usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
    }
}