package com.example.clinicalreports.ui.listarRepAlu;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.clinicalreports.R;
import com.example.clinicalreports.mdbf.Alumno;
import com.example.clinicalreports.mdbf.Reporte;
import com.example.clinicalreports.ui.agregarAlumno.AgregarAViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ListarRepAluFragment extends Fragment implements View.OnClickListener {
    private AgregarAViewModel agregarAViewModel;
    private Button btnLimpiar;
    private TextView tvNombreDu, tvTelefonoDU, tvNombreAni, tvFechaNaAni, tvEspecieAni, tvDiagnosAni, tvTratamienAni;
    private ImageView ivfoto;
    Reporte reporteSelected;

    private ListarRepAluViewModel mViewModel;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;

    ArrayAdapter<Reporte> arrayAdapter;
    private ListView lvReporte;

    private List<Reporte> ListaReportes = new ArrayList<>();

    public static ListarRepAluFragment newInstance() {
        return new ListarRepAluFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listar_rep_alu, container, false);

        iniciarComp(root);
        iniciarFirebase();
        listarDatos();
        lvReporte.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reporteSelected = (Reporte) parent.getItemAtPosition(position);
                tvNombreDu.setText("Nombre:  "+reporteSelected.getNomDu());
                tvTelefonoDU.setText("Teléfono: "+reporteSelected.getTelefono());
                tvNombreAni.setText("Nombre: "+reporteSelected.getNomAn());
                tvFechaNaAni.setText("Fecha Nacimiento: "+reporteSelected.getFecha());
                tvEspecieAni.setText("Especie: "+reporteSelected.getEspecie());
                tvDiagnosAni.setText("Diagnóstico: "+reporteSelected.getDiagnostico());
                tvTratamienAni.setText("Tratamiento: "+reporteSelected.getTratamiento());
                btnLimpiar.setEnabled(true);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("imagenes");
                storageRef.child(reporteSelected.getImagen()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getContext()).load(uri).into(ivfoto);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


            }
        });
        return root;


    }
    public void limpiar(){
        tvNombreDu.setText("Nombre");
        tvTelefonoDU.setText("Teléfono");
        tvNombreAni.setText("Nombre");
        tvFechaNaAni.setText("Fecha de nacimiento");
        tvEspecieAni.setText("Especie");
        tvDiagnosAni.setText("Diagnóstico");
        tvTratamienAni.setText("Tratamiento");
        btnLimpiar.setEnabled(false);
        ivfoto.setImageResource(R.drawable.ic_menu_camera);

    }

    private void iniciarComp(View root) {
        botonesComp(root);
        textComp(root);
    }

    private void botonesComp(View root) {
        btnLimpiar = root.findViewById(R.id.btnLimpListA);
        btnLimpiar.setEnabled(false);

       btnLimpiar.setOnClickListener(this);

    }

    private void textComp(View root) {
        tvNombreDu = root.findViewById(R.id.tvNomDuLA);
        tvTelefonoDU = root.findViewById(R.id.tvTelLA);
        tvNombreAni = root.findViewById(R.id.tvNomALA);
        tvFechaNaAni = root.findViewById(R.id.tvFechaLA);
        tvEspecieAni = root.findViewById(R.id.tvEspLA);
        tvDiagnosAni = root.findViewById(R.id.tvDiagLA);
        tvTratamienAni = root.findViewById(R.id.tvTratLA);
        lvReporte = root.findViewById(R.id.lvListAl);
        ivfoto=root.findViewById(R.id.ivFotoRep);
    }

    private void iniciarFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = firebaseDatabase.getReference("Alumnos/" + user.getUid() + "/");
    }

    private void listarDatos() {
        databaseReference.child("reportes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListaReportes.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    Reporte rep = objSnapshot.getValue(Reporte.class);
                    ListaReportes.add(rep);
                    if (getContext() != null) {
                        arrayAdapter = new ArrayAdapter<Reporte>(getContext(), android.R.layout.simple_list_item_1, ListaReportes);
                        lvReporte.setAdapter(arrayAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListarRepAluViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLimpListA:
                limpiar();
                break;
        }
    }
}