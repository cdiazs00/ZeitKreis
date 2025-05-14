package Requests_Responses;

import com.google.gson.annotations.SerializedName;

public class MessageRequest {

    @SerializedName("texto")
    private String texto;

    @SerializedName("autor")
    private String autor;

    @SerializedName("agendaId")
    private long agendaId;

    public MessageRequest(String texto, String autor, long agendaId) {
        this.texto = texto;
        this.autor = autor;
        this.agendaId = agendaId;
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

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setAgendaId(long agendaId) {
        this.agendaId = agendaId;
    }
}