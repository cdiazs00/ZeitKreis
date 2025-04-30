package com.example.zeitkreis;

import APIs.LoginRequest;
import APIs.LoginResponse;
import APIs.NewPasswordRequest;
import APIs.NewPasswordResponse;
import APIs.RegisterRequest;
import APIs.RegisterResponse;
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