package Requests_Responses;

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
        private Long id;
        private String nombreAgenda;

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombreAgenda;
        }
    }
}