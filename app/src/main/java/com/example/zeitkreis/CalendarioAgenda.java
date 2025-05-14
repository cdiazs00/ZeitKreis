package com.example.zeitkreis;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.activity.EdgeToEdge;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

public class CalendarioAgenda extends Fragment {

    @SuppressLint("UseRequireInsteadOfGet")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EdgeToEdge.enable(Objects.requireNonNull(getActivity()));

        return inflater.inflate(R.layout.calendario_agenda, container, false);
    }
}