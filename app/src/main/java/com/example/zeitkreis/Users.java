package com.example.zeitkreis;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Users {
    @POST("usuarios")
    Call<LoginResponse> loginUsuario(@Body LoginRequest request);
}