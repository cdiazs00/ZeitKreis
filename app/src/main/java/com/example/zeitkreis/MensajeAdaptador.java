package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import Requests_Responses.MessageResponse;

public class MensajeAdaptador extends RecyclerView.Adapter<MensajeAdaptador.MensajeViewHolder> {

    private final List<MessageResponse> mensajes;
    private static final String TAG = "MensajeAdaptador";

    public MensajeAdaptador(List<MessageResponse> mensajes) {
        this.mensajes = (mensajes != null) ? mensajes : new ArrayList<>();
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

        String timestampString = mensaje.getTimestamp();
        Date date = null;

        try {
            long millis = Long.parseLong(timestampString);
            date = new Date(millis);
        } catch (NumberFormatException e) {
            String[] possibleFormats = {
                    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss.SSS",
                    "yyyy-MM-dd HH:mm:ss"
            };

            for (String formatPattern : possibleFormats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(formatPattern, Locale.US);
                    if (formatPattern.endsWith("'Z'") || formatPattern.contains("XXX")) {
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    }
                    date = sdf.parse(timestampString);
                    if (date != null) {
                        break;
                    }
                } catch (ParseException ignored) {}
            }
        }


        if (date != null) {
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.timestamp.setText(outputFormat.format(date));
        } else {
            Log.w(TAG, "No se pudo parsear el timestamp: " + timestampString + ". Mostrando placeholder.");
            holder.timestamp.setText(timestampString);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateMessages(List<MessageResponse> newMessages) {
        this.mensajes.clear();
        if (newMessages != null) {
            this.mensajes.addAll(newMessages);
        }
        notifyDataSetChanged();
        Log.d(TAG, "Lista de mensajes actualizada en el adaptador. Nuevo tamaño: " + this.mensajes.size());
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
}