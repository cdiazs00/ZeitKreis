package com.example.zeitkreis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

import APIs.Diaries;
import Requests_Responses.NewDiaryRequest;
import Requests_Responses.NewDiaryResponse;
import Requests_Responses.UserSearchRequest;
import Requests_Responses.UserSearchResponse;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrearAgenda extends AppCompatActivity {

    private AutoCompleteTextView barraMiembros;
    private EditText campoNombreAgenda;
    private TextView miembrosSeleccionados;
    private Diaries agendaApi;

    private String nombreUsuarioActualGlobal;
    private final List<Long> idMiembros = new ArrayList<>();
    private final Map<String, Long> nombreAIdMap = new HashMap<>();
    private static final String TAG = "CrearAgendaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_agenda);

        Log.d(TAG, "onCreate: Iniciando CrearAgendaActivity");

        barraMiembros = findViewById(R.id.BarraMiembros);
        campoNombreAgenda = findViewById(R.id.BarraNombre);
        miembrosSeleccionados = findViewById(R.id.MiembrosSeleccionados);
        Button botonCrearAgenda = findViewById(R.id.BotonCrearAgenda);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        agendaApi = retrofit.create(Diaries.class);

        cargarYAnadirUsuarioActual();

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
            Log.d(TAG, "Miembro seleccionado del AutoComplete: " + miembroSeleccionado);
            if (!miembroSeleccionado.isEmpty()) {
                if (nombreUsuarioActualGlobal != null && nombreUsuarioActualGlobal.equals(miembroSeleccionado) && idMiembros.contains(nombreAIdMap.get(nombreUsuarioActualGlobal))) {
                    Toast.makeText(this, miembroSeleccionado + " (tú) ya está en la lista.", Toast.LENGTH_SHORT).show();
                } else {
                    agregarMiembro(miembroSeleccionado);
                }
                barraMiembros.setText("");
            }
        });

        botonCrearAgenda.setOnClickListener(v -> {
            String nombreAgenda = campoNombreAgenda.getText().toString().trim();
            Log.d(TAG, "Botón Crear Agenda pulsado. Nombre: " + nombreAgenda + ", Miembros IDs: " + idMiembros.toString());

            if (nombreAgenda.isEmpty()) {
                Toast.makeText(this, "Ingresa un nombre para la agenda", Toast.LENGTH_SHORT).show();
                return;
            }
            if (idMiembros.isEmpty()) {
                Toast.makeText(this, "Debes añadir al menos un miembro (tú ya deberías estar)", Toast.LENGTH_SHORT).show();
                return;
            }

            crearNuevaAgenda(nombreAgenda);
        });
    }

    private void cargarYAnadirUsuarioActual() {
        Log.d(TAG, "cargarYAnadirUsuarioActual: Cargando datos del usuario actual desde SharedPreferences");
        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String nombreUsuario = preferences.getString("nombre_usuario", null);

        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            nombreUsuarioActualGlobal = nombreUsuario;
            Log.d(TAG, "Usuario actual recuperado de SharedPreferences: " + nombreUsuario);

            UserSearchRequest request = new UserSearchRequest(nombreUsuario);
            agendaApi.buscarUsuarios(request).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<UserSearchResponse> call, @NonNull Response<UserSearchResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<UserSearchResponse.Usuario> usuarios = response.body().getUsuarios();
                        boolean foundAndAdded = false;
                        for (UserSearchResponse.Usuario u : usuarios) {
                            if (u.getNombreUsuario().equals(nombreUsuario)) {
                                Log.d(TAG, "Usuario actual encontrado en la búsqueda: " + u.getNombreUsuario() + " con ID: " + u.getId());
                                nombreAIdMap.put(u.getNombreUsuario(), u.getId());
                                agregarMiembro(u.getNombreUsuario());
                                foundAndAdded = true;
                                break;
                            }
                        }
                        if (!foundAndAdded) {
                            Log.w(TAG, "Usuario actual '" + nombreUsuario + "' no encontrado vía API search, o no es una coincidencia exacta.");
                            Toast.makeText(CrearAgenda.this, "No se pudo verificar al usuario '" + nombreUsuario + "' en el sistema.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e(TAG, "Error al buscar datos del usuario actual. Código: " + response.code());
                        Toast.makeText(CrearAgenda.this, "Error al obtener datos del usuario actual.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserSearchResponse> call, @NonNull Throwable t) {
                    Log.e(TAG, "Fallo de conexión al buscar datos del usuario actual.", t);
                    Toast.makeText(CrearAgenda.this, "Fallo de conexión al verificar usuario.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.w(TAG, "Nombre de usuario no encontrado en SharedPreferences.");
            Toast.makeText(this, "No se pudo cargar el usuario actual. Intenta iniciar sesión de nuevo.", Toast.LENGTH_LONG).show();
        }
    }


    private void buscarYActualizarSugerencias(String query) {
        Log.d(TAG, "buscarYActualizarSugerencias: Query: " + query);
        UserSearchRequest request = new UserSearchRequest(query);

        agendaApi.buscarUsuarios(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserSearchResponse> call, @NonNull Response<UserSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<UserSearchResponse.Usuario> usuarios = response.body().getUsuarios();
                    List<String> nombresSugeridos = new ArrayList<>();
                    Log.d(TAG, "Usuarios encontrados para sugerencias: " + usuarios.size());

                    for (UserSearchResponse.Usuario usuario : usuarios) {
                        boolean yaEsMiembro = false;
                        if (nombreUsuarioActualGlobal != null && nombreUsuarioActualGlobal.equals(usuario.getNombreUsuario())) {
                            Long idActual = nombreAIdMap.get(nombreUsuarioActualGlobal);
                            if (idActual != null && idMiembros.contains(idActual)) {
                                yaEsMiembro = true;
                            }
                        }

                        if (!yaEsMiembro) {
                            if (!nombreAIdMap.containsKey(usuario.getNombreUsuario()) || !Objects.equals(nombreAIdMap.get(usuario.getNombreUsuario()), usuario.getId())) {
                                nombreAIdMap.put(usuario.getNombreUsuario(), usuario.getId());
                            }
                            nombresSugeridos.add(usuario.getNombreUsuario());
                        }
                    }
                    Log.d(TAG, "Nombres para el ArrayAdapter: " + nombresSugeridos);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CrearAgenda.this,
                            android.R.layout.simple_dropdown_item_1line, nombresSugeridos);
                    barraMiembros.setAdapter(adapter);
                    if (barraMiembros.isPopupShowing() && nombresSugeridos.isEmpty()) {} else if (!nombresSugeridos.isEmpty()){
                        barraMiembros.showDropDown();
                    }

                } else {
                    Log.w(TAG, "Respuesta no exitosa o cuerpo vacío al buscar usuarios. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserSearchResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error en API al buscar usuarios para sugerencias.", t);
                Toast.makeText(CrearAgenda.this, "Error al buscar usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarMiembro(String nombreUsuario) {
        Log.d(TAG, "agregarMiembro: Intentando agregar a '" + nombreUsuario + "'");
        if (!nombreAIdMap.containsKey(nombreUsuario)) {
            Log.w(TAG, "No se encontró el ID para '" + nombreUsuario + "' en nombreAIdMap.");
            Toast.makeText(this, "No se encontró el ID de " + nombreUsuario, Toast.LENGTH_SHORT).show();
            return;
        }

        Long id = nombreAIdMap.get(nombreUsuario);
        if (id == null) {
            Log.e(TAG, "ID es null para '" + nombreUsuario + "' a pesar de estar en el mapa. Esto no debería ocurrir.");
            Toast.makeText(this, "Error interno al obtener ID de " + nombreUsuario, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!idMiembros.contains(id)) {
            idMiembros.add(id);
            Log.d(TAG, nombreUsuario + " (ID: " + id + ") agregado a idMiembros. Lista actual: " + idMiembros);
            Toast.makeText(this, nombreUsuario + " agregado", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, nombreUsuario + " (ID: " + id + ") ya está en idMiembros.");
            Toast.makeText(this, nombreUsuario + " ya está en la lista", Toast.LENGTH_SHORT).show();
        }
        actualizarMiembrosSeleccionados();
    }

    private void actualizarMiembrosSeleccionados() {
        StringBuilder miembrosText = new StringBuilder("Miembros: ");
        if (idMiembros.isEmpty()) {
            miembrosText.append("Ninguno aún");
        } else {
            for (int i = 0; i < idMiembros.size(); i++) {
                Long id = idMiembros.get(i);
                String nombre = getNombreMiembro(id);
                miembrosText.append(nombre);
                if (i < idMiembros.size() - 1) {
                    miembrosText.append(", ");
                }
            }
        }
        Log.d(TAG, "actualizarMiembrosSeleccionados: Texto final: " + miembrosText);
        miembrosSeleccionados.setText(miembrosText.toString());
    }

    private String getNombreMiembro(Long id) {
        for (Map.Entry<String, Long> entry : nombreAIdMap.entrySet()) {
            if (entry.getValue().equals(id)) {
                return entry.getKey();
            }
        }
        Log.w(TAG, "getNombreMiembro: No se encontró nombre para ID: " + id);
        return "ID:" + id + " (Desconocido)";
    }

    private void crearNuevaAgenda(String nombreAgenda) {
        Log.d(TAG, "crearNuevaAgenda: Nombre: " + nombreAgenda + ", IDs Miembros: " + idMiembros.toString());
        NewDiaryRequest request = new NewDiaryRequest(nombreAgenda, idMiembros);

        agendaApi.crearAgenda(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewDiaryResponse> call, @NonNull Response<NewDiaryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "Agenda creada exitosamente. Respuesta: " + response.body().toString());
                    Toast.makeText(CrearAgenda.this, "Agenda creada exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Error al crear la agenda.";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += " Server: " + response.errorBody().string();
                        } catch (Exception e) { Log.e(TAG, "Error parsing error body", e); }
                    } else if(response.body() != null && response.body().getMessage() != null) {
                        errorMsg += " Mensaje: " + response.body().getMessage();
                    } else {
                        errorMsg += " Código: " + response.code();
                    }
                    Log.e(TAG, errorMsg);
                    Toast.makeText(CrearAgenda.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewDiaryResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Fallo de conexión al crear agenda.", t);
                Toast.makeText(CrearAgenda.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}