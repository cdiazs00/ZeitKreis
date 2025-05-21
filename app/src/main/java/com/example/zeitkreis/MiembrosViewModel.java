package com.example.zeitkreis;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import APIs.Diaries;
import Requests_Responses.UserSearchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MiembrosViewModel extends ViewModel {

    private final MutableLiveData<List<UserSearchResponse.Usuario>> _miembrosList = new MutableLiveData<>();
    public LiveData<List<UserSearchResponse.Usuario>> miembrosList = _miembrosList;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private final Diaries diariesApi;
    private static final String TAG = "MiembrosViewModel";

    public MiembrosViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        diariesApi = retrofit.create(Diaries.class);
    }

    public void fetchMiembros(Long agendaId) {
        if (agendaId == null || agendaId <= 0) {
            _errorMessage.setValue("ID de agenda inválido para cargar miembros.");
            Log.e(TAG, "fetchMiembros: ID de agenda inválido: " + agendaId);
            return;
        }
        _isLoading.setValue(true);
        Log.d(TAG, "Cargando miembros para agendaId: " + agendaId);

        diariesApi.obtenerMiembrosDeAgenda(agendaId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<UserSearchResponse.Usuario>> call, @NonNull Response<List<UserSearchResponse.Usuario>> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _miembrosList.setValue(response.body());
                    Log.d(TAG, "Miembros recibidos: " + response.body().size());
                } else {
                    String errorMsg = "Error al cargar los miembros. Código: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " Detalles: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al parsear errorBody en fetchMiembros", e);
                    }
                    _errorMessage.setValue(errorMsg);
                    Log.e(TAG, errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserSearchResponse.Usuario>> call, @NonNull Throwable t) {
                _isLoading.setValue(false);
                _errorMessage.setValue("Fallo de conexión al cargar miembros: " + t.getMessage());
                Log.e(TAG, "Fallo de conexión al cargar miembros", t);
            }
        });
    }

    public void clearErrorMessage() {
        _errorMessage.setValue(null);
    }
}