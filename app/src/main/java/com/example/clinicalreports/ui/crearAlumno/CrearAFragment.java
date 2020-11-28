package com.example.clinicalreports.ui.crearAlumno;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.clinicalreports.R;

public class CrearAFragment extends Fragment {

    private CrearAViewModel crearAViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        crearAViewModel =
                new ViewModelProvider(this).get(CrearAViewModel.class);
        View root = inflater.inflate(R.layout.fragment_crear_alumno, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        crearAViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}