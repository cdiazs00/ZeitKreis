package Requests_Responses;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("nombreUsuario")
    private String nombreUsuario;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
}
