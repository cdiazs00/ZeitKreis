package APIs;

import java.util.List;

import Requests_Responses.AllDiariesResponse;
import Requests_Responses.NewDiaryRequest;
import Requests_Responses.NewDiaryResponse;
import Requests_Responses.UserSearchRequest;
import Requests_Responses.UserSearchResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Diaries {
    @POST("agendas")
    Call<NewDiaryResponse> crearAgenda(@Body NewDiaryRequest request);

    @POST("usuarios/searchUser")
    Call<UserSearchResponse> buscarUsuarios(@Body UserSearchRequest request);

    @GET("agendas")
    Call<List<AllDiariesResponse.Agenda>> obtenerAgendas();
}