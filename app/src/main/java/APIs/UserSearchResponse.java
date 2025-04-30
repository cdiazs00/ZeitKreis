package APIs;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserSearchResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("usuarios")
    private List<Usuario> usuarios;

    public boolean isSuccess() {
        return success;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public static class Usuario {
        @SerializedName("id")
        private long id;

        @SerializedName("nombreUsuario")
        private String nombreUsuario;

        public long getId() {
            return id;
        }

        public String getNombreUsuario() {
            return nombreUsuario;
        }
    }
}