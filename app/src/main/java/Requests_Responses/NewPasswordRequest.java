package Requests_Responses;

import com.google.gson.annotations.SerializedName;

public class NewPasswordRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String contrasena;

    @SerializedName("newPassword")
    private String nuevaContrasena;

    public NewPasswordRequest(String email, String contrasena, String nuevaContrasena) {
        this.email = email;
        this.contrasena = contrasena;
        this.nuevaContrasena = nuevaContrasena;
    }

    public String getEmail() {
        return email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }
}