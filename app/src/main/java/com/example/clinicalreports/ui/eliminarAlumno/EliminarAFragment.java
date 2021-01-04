package com.example.clinicalreports.ui.eliminarAlumno;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.clinicalreports.R;
import com.example.clinicalreports.mdbf.Alumno;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;

public class EliminarAFragment extends Fragment implements View.OnClickListener {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;

    private Alumno alumnoSelected;

    private Button btnBuscar, btnLimpiar,btnBaja;

    private TextView tvNombreAlu, tvCorreoAlu, tvNumControl;

    private EditText etNoctrl;

    private EliminarAViewModel eliminarAViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eliminarAViewModel =
                new ViewModelProvider(this).get(EliminarAViewModel.class);
        View root = inflater.inflate(R.layout.fragment_eliminar_alumno, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        eliminarAViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        iniciarComp(root);
        iniciarFirebase();
        return root;
    }

    private void iniciarFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = firebaseDatabase.getReference("Alumnos");
    }

    private void iniciarComp(View root) {
        botonesComp(root);
        textComp(root);
        editComp(root);
    }

    private void botonesComp(View root) {
        btnLimpiar = root.findViewById(R.id.btnLimpiaEl);
        btnLimpiar.setEnabled(false);
        btnBuscar = root.findViewById(R.id.btnBuscaAl);
        btnBuscar.setEnabled(true);
        btnBaja = root.findViewById(R.id.btnBaja);
        btnBaja.setEnabled(true);

        btnBuscar.setOnClickListener(this);
        btnLimpiar.setOnClickListener(this);
        btnBaja.setOnClickListener(this);

        btnBaja.setEnabled(false);

        btnLimpiar.setEnabled(false);



    }
    private void editComp(View root){
        etNoctrl = root.findViewById(R.id.etNoctrlEl);
    }

    private void textComp(View root) {
        tvNombreAlu = root.findViewById(R.id.tvNombreAlu);
        tvCorreoAlu = root.findViewById(R.id.tvCorreoAlu);
        tvNumControl = root.findViewById(R.id.tvNoctrlAlu);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBuscaAl:
                if(etNoctrl.getText().toString().isEmpty()){
                    etNoctrl.requestFocus();
                    etNoctrl.setError("Por favor ingrese un criterio de búsqueda");
                }else{
                    String noCtrl = etNoctrl.getText().toString();
                    Query queryAlu = databaseReference.orderByChild("noCtrl").equalTo(noCtrl);
                    queryAlu.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot objSnapshot : snapshot.getChildren()){
                                    alumnoSelected = objSnapshot.getValue(Alumno.class);
                                    if(alumnoSelected.getProfesor() != null){
                                        if (alumnoSelected.getProfesor().equals(user.getUid())){
                                            btnLimpiar.setEnabled(true);
                                            btnBaja.setEnabled(true);

                                            tvNombreAlu.setText("Nombre del alumno: " + alumnoSelected.getNombre());
                                            tvCorreoAlu.setText("Apellido:  "+ alumnoSelected.getNoCtrl());
                                            tvNumControl.setText("No. de Control: " + alumnoSelected.getCorreo());
                                        }else{
                                            Toast.makeText(getContext(), "No hay resultados", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            }else{
                                if(getContext() != null){
                                    Toast.makeText(getContext(), "No hay resultados", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                break;
            case R.id.btnBaja:
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_alumno, null);
                ((TextView) dialogView.findViewById(R.id.tvInfoAlumno)).setText("¿Desea eliminar el registro?\n" +
                        "Correo: " + alumnoSelected.getCorreo() + "\n" +
                        "Número de Control: " + alumnoSelected.getNoCtrl() + "\n" +
                        "Nombre: " + alumnoSelected.getNombre());
                AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                dialogo.setTitle("Importante");
                dialogo.setView(dialogView);
                dialogo.setCancelable(false);

                dialogo.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        aceptar(alumnoSelected.getUuid());
                    }
                });
                dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Registro aún activo", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogo.show();
                break;
            case R.id.btnLimpiaEl:
                limpiar();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }
    private void aceptar(String id) {
        if(getContext() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                alumnoSelected.setProfesor(null);
                databaseReference.child(alumnoSelected.getUuid()).setValue(alumnoSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Alumno eliminado del grupo", Toast.LENGTH_SHORT).show();
                        limpiar();
                    }
                });
            } else {
                alumnoSelected.setProfesor(null);
                databaseReference.child(alumnoSelected.getUuid()).setValue(alumnoSelected);
                Toast.makeText(getContext(), "Alumno eliminado del grupo" + "\nEstado: Sin conexión", Toast.LENGTH_SHORT).show();
                limpiar();
            }
        }
    }

    private void limpiar() {
        tvNombreAlu.setText("Nombre del Alumno: ");
        tvCorreoAlu.setText("Apellido: ");
        tvNumControl.setText("No. de Control: ");
        etNoctrl.setText("");
        btnLimpiar.setEnabled(false);
        btnBaja.setEnabled(false);
    }
}


