package APIs;

import Requests_Responses.MessageResponse;
import Requests_Responses.MessageRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface Chats {

    @GET("mensajes/agenda/{agendaId}")
    Call<List<MessageResponse>> obtenerMensajes(@Path("agendaId") Long agendaId);

    @POST("mensajes/enviar")
    Call<MessageResponse> enviarMensaje(@Body MessageRequest request);
}
