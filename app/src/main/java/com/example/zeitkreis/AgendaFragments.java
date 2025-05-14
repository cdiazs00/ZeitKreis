package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AgendaFragments extends Fragment {

    private long agendaId = -1;
    private String agendaNombre = "(Sin nombre)";

    public AgendaFragments() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_agenda, container, false);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            agendaId = args.getLong("agendaId", -1);
            agendaNombre = args.getString("agendaNombre", "(Sin nombre)");
        }

        BottomNavigationView navView = view.findViewById(R.id.bottom_nav_view);

        cargarFragment(new Chat());

        navView.setOnItemSelectedListener(item -> {
            Fragment fragmentSeleccionado;

            int itemId = item.getItemId();

            if (itemId == R.id.calendarioAgenda) {
                fragmentSeleccionado = new CalendarioAgenda();
            } else if (itemId == R.id.miembros) {
                fragmentSeleccionado = new Miembros();
            } else {
                fragmentSeleccionado = new Chat();
            }

            Bundle bundle = new Bundle();
            bundle.putLong("agendaId", agendaId);
            bundle.putString("agendaNombre", agendaNombre);
            fragmentSeleccionado.setArguments(bundle);

            cargarFragment(fragmentSeleccionado);
            return true;
        });
    }

    private void cargarFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}