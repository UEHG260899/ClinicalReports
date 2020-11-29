package com.example.clinicalreports.ui.listarRepAlu;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clinicalreports.R;

public class ListarRepAluFragment extends Fragment {

    private ListarRepAluViewModel mViewModel;

    public static ListarRepAluFragment newInstance() {
        return new ListarRepAluFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listar_rep_alu, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListarRepAluViewModel.class);
        // TODO: Use the ViewModel
    }

}