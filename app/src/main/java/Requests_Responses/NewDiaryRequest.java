package Requests_Responses;

import java.util.List;

public class NewDiaryRequest {
    private String nombreAgenda;
    private List<Long> idUsuarios;

    public NewDiaryRequest(String nombreAgenda, List<Long> idUsuarios) {
        this.nombreAgenda = nombreAgenda;
        this.idUsuarios = idUsuarios;
    }

    public String getNombreAgenda() {
        return nombreAgenda;
    }

    public void setNombreAgenda(String nombreAgenda) {
        this.nombreAgenda = nombreAgenda;
    }

    public List<Long> getIdUsuarios() {
        return idUsuarios;
    }

    public void setIdUsuarios(List<Long> idUsuarios) {
        this.idUsuarios = idUsuarios;
    }
}