package Requests_Responses;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("nombreUsuario")
    private String nombre;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String contraseña;

    public RegisterRequest(String nombre, String email, String contraseña) {
        this.nombre = nombre;
        this.email = email;
        this.contraseña = contraseña;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
}