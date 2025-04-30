package com.example.zeitkreis;

import APIs.NewDiaryRequest;
import APIs.NewDiaryResponse;
import APIs.UserSearchRequest;
import APIs.UserSearchResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Diaries {
    @POST("agendas")
    Call<NewDiaryResponse> crearAgenda(@Body NewDiaryRequest request);
    @POST("usuarios/searchUser")
    Call<UserSearchResponse> buscarUsuarios(@Body UserSearchRequest request);
}