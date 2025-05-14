package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import Requests_Responses.MessageResponse;

public class MensajeAdaptador extends RecyclerView.Adapter<MensajeAdaptador.MensajeViewHolder> {

    private final List<MessageResponse> mensajes;

    public MensajeAdaptador(List<MessageResponse> mensajes) {
        this.mensajes = mensajes;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mensaje, parent, false);
        return new MensajeViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MensajeViewHolder holder, int position) {
        MessageResponse mensaje = mensajes.get(position);
        holder.texto.setText(mensaje.getTexto());
        holder.autor.setText(mensaje.getAutor());

        String timestamp = mensaje.getTimestamp();
        Date date = null;

        String[] possibleFormats = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd HH:mm:ss.SSS"
        };

        for (String format : possibleFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                date = sdf.parse(timestamp);
                if (date != null) break;
            } catch (ParseException ignored) {
            }
        }

        if (date != null) {
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.timestamp.setText(outputFormat.format(date));
        } else {
            holder.timestamp.setText("Fecha no válida");
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView texto, autor, timestamp;

        public MensajeViewHolder(View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.textoMensaje);
            autor = itemView.findViewById(R.id.autorMensaje);
            timestamp = itemView.findViewById(R.id.timestampMensaje);
        }
    }

    public void agregarMensaje(MessageResponse mensaje) {
        mensajes.add(mensaje);
        notifyItemInserted(mensajes.size() - 1);
    }
}