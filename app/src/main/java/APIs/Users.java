package APIs;

import Requests_Responses.LoginRequest;
import Requests_Responses.LoginResponse;
import Requests_Responses.NewPasswordRequest;
import Requests_Responses.NewPasswordResponse;
import Requests_Responses.RegisterRequest;
import Requests_Responses.RegisterResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Users {
    @POST("usuarios/login")
    Call<LoginResponse> loginUsuario(@Body LoginRequest request);

    @POST("usuarios/register")
    Call<RegisterResponse> registrarUsuario(@Body RegisterRequest request);

    @POST("usuarios/newPassword")
    Call<NewPasswordResponse> cambiarContrasena(@Body NewPasswordRequest request);
}