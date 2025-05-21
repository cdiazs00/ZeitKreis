package com.example.zeitkreis;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

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

public class ChatViewModel extends ViewModel {

    private final MutableLiveData<List<MessageResponse>> _messagesList = new MutableLiveData<>();
    public LiveData<List<MessageResponse>> messagesList = _messagesList;

    final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<Boolean> _messageSentSuccessfully = new MutableLiveData<>();
    public LiveData<Boolean> messageSentSuccessfully = _messageSentSuccessfully;


    private final Chats chatsApi;
    private static final String TAG = "ChatViewModel";

    public ChatViewModel() {
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

    public void fetchMessages(Long agendaId) {
        if (agendaId == null || agendaId <= 0) {
            _errorMessage.setValue("ID de agenda inválido para cargar mensajes.");
            Log.e(TAG, "fetchMessages: ID de agenda inválido: " + agendaId);
            return;
        }
        _isLoading.setValue(true);
        Log.d(TAG, "Cargando mensajes para agendaId: " + agendaId);

        chatsApi.obtenerMensajes(agendaId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<MessageResponse>> call, @NonNull Response<List<MessageResponse>> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _messagesList.setValue(response.body());
                    Log.d(TAG, "Mensajes recibidos: " + response.body().size());
                } else {
                    String errorMsg = "Error al cargar los mensajes. Código: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " Detalles: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al parsear errorBody en fetchMessages", e);
                    }
                    _errorMessage.setValue(errorMsg);
                    Log.e(TAG, errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MessageResponse>> call, @NonNull Throwable t) {
                _isLoading.setValue(false);
                _errorMessage.setValue("Fallo de conexión al cargar mensajes: " + t.getMessage());
                Log.e(TAG, "Fallo de conexión al cargar mensajes", t);
            }
        });
    }

    public void sendMessage(String texto, String autor, Long agendaId) {
        if (agendaId == null || agendaId <= 0) {
            _errorMessage.setValue("ID de agenda inválido para enviar mensaje.");
            Log.e(TAG, "sendMessage: ID de agenda inválido: " + agendaId);
            return;
        }
        _isLoading.setValue(true);
        Log.d(TAG, "Preparando para enviar mensaje - Texto: " + texto + ", Autor: " + autor + ", AgendaId: " + agendaId);
        MessageRequest request = new MessageRequest(texto, autor, agendaId);

        chatsApi.enviarMensaje(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Mensaje enviado exitosamente. Respuesta: " + response.body());
                    _messageSentSuccessfully.setValue(true);
                    fetchMessages(agendaId);
                } else {
                    String errorMsg = "Error al enviar el mensaje. Código: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " Detalles: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al parsear errorBody en sendMessage", e);
                    }
                    _errorMessage.setValue(errorMsg);
                    _messageSentSuccessfully.setValue(false);
                    Log.e(TAG, errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                _isLoading.setValue(false);
                _messageSentSuccessfully.setValue(false);
                _errorMessage.setValue("Fallo de conexión al enviar mensaje: " + t.getMessage());
                Log.e(TAG, "Fallo de conexión al enviar mensaje", t);
            }
        });
    }

    public void clearErrorMessage() {
        _errorMessage.setValue(null);
    }

    public void clearMessageSentStatus() {
        _messageSentSuccessfully.setValue(null); // Reset status
    }
}