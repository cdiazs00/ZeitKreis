package Requests_Responses;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AllDiariesResponse {
    private boolean success;
    private List<Agenda> agendas;

    public boolean isSuccess() {
        return success;
    }

    public List<Agenda> getAgendas() {
        return agendas;
    }

    public static class Agenda {
        @SerializedName("idAgenda")
        private Long id;

        @SerializedName("nombreAgenda")
        private String nombre;

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }
}