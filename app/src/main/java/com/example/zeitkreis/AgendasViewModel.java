package com.example.zeitkreis;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import APIs.Diaries;
import Requests_Responses.AllDiariesResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgendasViewModel extends ViewModel {

    private final MutableLiveData<List<AllDiariesResponse.Agenda>> _agendasList = new MutableLiveData<>();
    public LiveData<List<AllDiariesResponse.Agenda>> agendasList = _agendasList;

    final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private final Diaries agendaApi;

    public AgendasViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        agendaApi = retrofit.create(Diaries.class);
    }

    public void fetchAgendas() {
        if (_agendasList.getValue() != null && !_agendasList.getValue().isEmpty()) {
            return;
        }

        _isLoading.setValue(true);
        agendaApi.obtenerAgendas().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<AllDiariesResponse.Agenda>> call, @NonNull Response<List<AllDiariesResponse.Agenda>> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _agendasList.setValue(response.body());
                } else {
                    _errorMessage.setValue("Error al obtener agendas: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AllDiariesResponse.Agenda>> call, @NonNull Throwable t) {
                _isLoading.setValue(false);
                _errorMessage.setValue("Fallo de conexión: " + t.getMessage());
            }
        });
    }
}