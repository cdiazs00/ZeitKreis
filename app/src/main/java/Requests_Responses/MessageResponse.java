package Requests_Responses;

import com.google.gson.annotations.SerializedName;

public class MessageResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("texto")
    private String texto;

    @SerializedName("autor")
    private String autor;

    @SerializedName("agenda_id")
    private long agendaId;

    @SerializedName("timestamp")
    private String timestamp;

    public int getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public String getAutor() {
        return autor;
    }

    public long getAgendaId() {
        return agendaId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setAgendaId(long agendaId) {
        this.agendaId = agendaId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}