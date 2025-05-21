package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import Requests_Responses.UserSearchResponse;

public class MiembroAdaptador extends RecyclerView.Adapter<MiembroAdaptador.MiembroViewHolder> {

    private final List<UserSearchResponse.Usuario> miembrosList;

    public MiembroAdaptador(List<UserSearchResponse.Usuario> miembrosList) {
        this.miembrosList = (miembrosList != null) ? miembrosList : new ArrayList<>();
    }

    @NonNull
    @Override
    public MiembroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_miembros, parent, false);
        return new MiembroViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MiembroViewHolder holder, int position) {
        UserSearchResponse.Usuario miembro = miembrosList.get(position);
        if (miembro != null && miembro.getNombreUsuario() != null) {
            holder.nombreMiembroTextView.setText(miembro.getNombreUsuario());
        } else {
            holder.nombreMiembroTextView.setText("Nombre no disponible");
        }
    }

    @Override
    public int getItemCount() {
        return miembrosList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateMiembros(List<UserSearchResponse.Usuario> newMiembrosList) {
        this.miembrosList.clear();
        if (newMiembrosList != null) {
            this.miembrosList.addAll(newMiembrosList);
        }
        notifyDataSetChanged();
    }

    static class MiembroViewHolder extends RecyclerView.ViewHolder {
        TextView nombreMiembroTextView;

        MiembroViewHolder(View itemView) {
            super(itemView);
            nombreMiembroTextView = itemView.findViewById(R.id.textViewNombreMiembro);
        }
    }
}