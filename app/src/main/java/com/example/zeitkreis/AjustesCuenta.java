package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AjustesCuenta extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageViewPerfil;
    private byte[] imagenSeleccionada;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageViewPerfil = view.findViewById(R.id.imagen_perfil);
        Button botonCambiarFoto = view.findViewById(R.id.BotonFondoPantalla);

        botonCambiarFoto.setOnClickListener(v -> abrirGaleria());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imagenUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imagenUri);
                imageViewPerfil.setImageBitmap(bitmap);
                imagenSeleccionada = convertirBitmapABytes(bitmap);
                guardarImagenEnBaseDeDatos();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error al seleccionar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] convertirBitmapABytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void guardarImagenEnBaseDeDatos() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String correo = preferences.getString("correo", "");

        if (correo.isEmpty() || imagenSeleccionada == null) {
            Toast.makeText(getActivity(), "Error: Datos no disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String dbUrl = ConfigUtil.getMetaData(requireContext(), "DB_URL");
                String dbUser = ConfigUtil.getMetaData(requireContext(), "DB_USER");
                String dbPassword = ConfigUtil.getMetaData(requireContext(), "DB_PASSWORD");

                try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                     PreparedStatement statement = connection.prepareStatement(
                             "UPDATE usuarios SET foto_perfil = ? WHERE Correo_Electrónico = ?")) {

                    statement.setBytes(1, imagenSeleccionada);
                    statement.setString(2, correo);
                    int filasAfectadas = statement.executeUpdate();

                    requireActivity().runOnUiThread(() -> {
                        if (filasAfectadas > 0) {
                            Toast.makeText(getActivity(), "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show());
            }
        });
    }
}