package com.example.clinicalreports.ui.eliminarUsu;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinicalreports.DrawerAlumno;
import com.example.clinicalreports.MainActivity;
import com.example.clinicalreports.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EliminarUsuFragment extends Fragment implements View.OnClickListener {

    private EliminarUsuViewModel mViewModel;
    private Button btnEliminar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public static EliminarUsuFragment newInstance() {
        return new EliminarUsuFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_eliminar_usu, container, false);
        iniciarFirebase();
        iniciarComponentes(root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EliminarUsuViewModel.class);
        // TODO: Use the ViewModel
    }

    private void iniciarFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Alumnos");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void iniciarComponentes(View root) {
        btnEliminar = root.findViewById(R.id.btnEliminaUsu);

        btnEliminar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnEliminaUsu) {
            View dialogoView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_alumno, null);
            ((TextView) dialogoView.findViewById(R.id.tvInfoAlumno)).setText("¿Estas seguro que deseas eliminar de forma permanente los datos?\n" +
                    "Esta acción no se podra deshacer");
            AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
            dialogo.setTitle("Aviso");
            dialogo.setView(dialogoView);
            dialogo.setCancelable(false);
            dialogo.setPositiveButton("Si, estoy de acuerdo", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    aceptar();
                }
            });
            dialogo.setNegativeButton("No, prefiero no hacerlo", null);
            dialogo.show();
        }
    }

    private void aceptar() {
        
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            databaseReference.child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Usuario eliminado con exito, esperamos verte de nuevo pronto!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getContext(), MainActivity.class));
                            getActivity().finish();
                        }
                    });
                }
            });
        } else {
            databaseReference.child(user.getUid()).removeValue();
            Toast.makeText(getContext(), "Usuario eliminado con exito, esperamos verte de nuevo pronto!" + "\nEstado: Sin conexión", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();

        }
    }
}