package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AgendaFragments extends Fragment {

    private long agendaId = -1L;
    private String agendaNombre = "(Sin nombre)";
    private static final String TAG = "AgendaFragments";

    public AgendaFragments() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            agendaId = args.getLong("agendaId", -1L);
            agendaNombre = args.getString("agendaNombre", "(Sin nombre)");
            Log.d(TAG, "onCreate: agendaId=" + agendaId + ", agendaNombre=" + agendaNombre);
        } else {
            Log.e(TAG, "onCreate: Los argumentos son nulos.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_agenda, container, false);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView navView = view.findViewById(R.id.bottom_nav_view);

        if (savedInstanceState == null) {
            Log.d(TAG, "onViewCreated: Cargando fragmento Chat inicial con agendaId=" + agendaId);
            Chat chatFragment = new Chat();
            Bundle initialArgs = new Bundle();
            initialArgs.putLong("agendaId", agendaId);
            initialArgs.putString("agendaNombre", agendaNombre);
            chatFragment.setArguments(initialArgs);
            cargarFragment(chatFragment, "ChatInitialTag");
        }


        navView.setOnItemSelectedListener(item -> {
            Fragment fragmentSeleccionado = null;
            String tag = null;

            int itemId = item.getItemId();
            Log.d(TAG, "BottomNav item seleccionado. ID recibido: " + itemId);
            Log.d(TAG, "Comparando con R.id.chatAgenda (XML): " + R.id.chatAgenda);
            Log.d(TAG, "Comparando con R.id.calendarioAgenda (XML): " + R.id.calendarioAgenda);
            Log.d(TAG, "Comparando con R.id.miembrosAgenda (XML): " + R.id.miembrosAgenda);


            if (itemId == R.id.calendarioAgenda) {
                Log.d(TAG, "Cargando CalendarioAgenda");
                fragmentSeleccionado = new CalendarioAgenda();
                tag = "CalendarioAgendaTag";
            } else if (itemId == R.id.miembrosAgenda) {
                Log.d(TAG, "Cargando Miembros");
                fragmentSeleccionado = new Miembros();
                tag = "MiembrosTag";
            } else if (itemId == R.id.chatAgenda) {
                Log.d(TAG, "Cargando Chat");
                fragmentSeleccionado = new Chat();
                tag = "ChatTag";
            } else {
                Log.w(TAG, "ID de item no reconocido: " + itemId + ". No se cargará ningún fragmento nuevo.");
            }

            if (fragmentSeleccionado != null) {
                Bundle bundle = new Bundle();
                bundle.putLong("agendaId", agendaId);
                bundle.putString("agendaNombre", agendaNombre);
                fragmentSeleccionado.setArguments(bundle);
                Log.d(TAG, "Cargando fragmento por BottomNav con agendaId=" + agendaId + " para " + fragmentSeleccionado.getClass().getSimpleName());
                cargarFragment(fragmentSeleccionado, tag);
            } else {
                Log.e(TAG, "fragmentSeleccionado es NULL después de la lógica if/else if. No se cargará ningún fragmento.");
            }
            return true;
        });
    }

    private void cargarFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment, tag)
                .commit();
    }
}