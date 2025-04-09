package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.IOException;
import java.util.Locale;

public class AjustesGenerales extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ConstraintLayout layout;
    private boolean notificacionesActivadas = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(getSavedLanguage());
        setContentView(R.layout.ajustes_generales);

        layout = findViewById(R.id.login);
        findViewById(R.id.BotonFondoPantalla).setOnClickListener(v -> seleccionarImagen());
        findViewById(R.id.BotonNotificaciones).setOnClickListener(v -> toggleNotificaciones());
        findViewById(R.id.BotonIdioma).setOnClickListener(v -> mostrarDialogoIdioma());
    }

    @SuppressLint("IntentReset")
    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                layout.setBackground(new BitmapDrawable(getResources(), bitmap));
                establecerFondoDePantalla(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void establecerFondoDePantalla(Bitmap bitmap) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(this, "Fondo de pantalla cambiado", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "No se pudo cambiar el fondo", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleNotificaciones() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificacionesActivadas) {
            notificationManager.cancelAll();
            Toast.makeText(this, "Notificaciones desactivadas", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Notificaciones activadas", Toast.LENGTH_SHORT).show();
        }
        notificacionesActivadas = !notificacionesActivadas;
    }

    private void mostrarDialogoIdioma() {
        String[] idiomas = {"Español", "English"};
        new AlertDialog.Builder(this)
                .setTitle("Seleccionar idioma")
                .setItems(idiomas, (dialog, which) -> {
                    if (which == 0) {
                        setLocale("es");
                    } else {
                        setLocale("en");
                    }
                })
                .show();
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        preferences.edit().putString("language", language).apply();

        recreate();
    }

    private String getSavedLanguage() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        return preferences.getString("language", "es");
    }
}