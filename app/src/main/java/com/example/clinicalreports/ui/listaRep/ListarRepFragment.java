package com.example.clinicalreports.ui.listaRep;

import androidx.lifecycle.ViewModelProvider;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinicalreports.R;
import com.example.clinicalreports.mdbf.Alumno;
import com.example.clinicalreports.mdbf.Reporte;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListarRepFragment extends Fragment implements View.OnClickListener{

    private ListarRepViewModel mViewModel;
    private ListView lvReportes;
    private Reporte reporteSelected;
    private List<Reporte> listaReprtes = new ArrayList<>();
    private EditText etNoctrl;
    private TextView txtNomAn,txtNomDu,txtTel,txtFecNac,txtEspecie,txtDiagnostico,txtTratamiento;
    private Button btnBuscar;
    private Alumno alumnoSelected;
    ArrayAdapter<Reporte> arrayAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceReportes;
    FirebaseUser user;

    public static ListarRepFragment newInstance() {
        return new ListarRepFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listar_rep, container, false);

        iniciarComp(root);
        iniciarFirebase();
        //listarDatos();

        return root;
    }

    private void iniciarFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Alumnos");
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListarRepViewModel.class);
        // TODO: Use the ViewModel


    }

    private void iniciarComp(View root){
        botonesComp(root);
        textComp(root);
        editComp(root);
    }

    private void editComp(View root){
        etNoctrl = root.findViewById(R.id.etNoctrllr);
        lvReportes = root.findViewById(R.id.lvRepProf);

        lvReportes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txtNomDu.setText(listaReprtes.get(position).getNomDu());
                txtNomAn.setText(listaReprtes.get(position).getNomAn());
                txtTel.setText(listaReprtes.get(position).getTelefono());
                txtFecNac.setText(listaReprtes.get(position).getFecha());
                txtEspecie.setText(listaReprtes.get(position).getEspecie());
                txtDiagnostico.setText(listaReprtes.get(position).getDiagnostico());
                txtTratamiento.setText(listaReprtes.get(position).getTratamiento());
            }
        });
    }

    private void textComp(View root){
        txtNomDu =  root.findViewById(R.id.tvNomDuLP);
        txtNomAn =  root.findViewById(R.id.tvNomALP);
        txtTel =  root.findViewById(R.id.tvTelLP);
        txtFecNac =  root.findViewById(R.id.tvFechaLP);
        txtEspecie =  root.findViewById(R.id.tvEspLP);
        txtDiagnostico =  root.findViewById(R.id.tvDiagLP);
        txtTratamiento =  root.findViewById(R.id.tvTratLP);
    }

    private void botonesComp(View root){
        btnBuscar = root.findViewById(R.id.btnBuscaAllr);
        btnBuscar.setOnClickListener(this);
    }
/*
    private void listarDatos(){
        databaseReference.child("reportes").orderByChild("uuid").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listaReprtes.clear();
                for(DataSnapshot objSnapshot : snapshot.getChildren()){
                    Reporte reporte = objSnapshot.getValue(Reporte.class);
                    listaReprtes.add(reporte);
                    if(getContext() != null){
                        arrayAdapter = new ArrayAdapter<Reporte>(getContext(), android.R.layout.simple_list_item_1, listaReprtes);
                        lvReportes.setAdapter(arrayAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnBuscaAllr:

                if (etNoctrl.getText().toString().isEmpty()) {
                    etNoctrl.requestFocus();
                    etNoctrl.setError("Por favor ingrese un criterio de búsqueda");
                } else {
                    String noCtrl = etNoctrl.getText().toString();
                    Query queryAlu = databaseReference.orderByChild("noCtrl").equalTo(noCtrl);
                    queryAlu.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                //btnAgregar.setEnabled(true);
                                //btnLimpiar.setEnabled(true);
                                listaReprtes.clear();
                                for(DataSnapshot objSnapshot : snapshot.getChildren()){
                                    alumnoSelected = objSnapshot.getValue(Alumno.class);
                                }
                                databaseReference = firebaseDatabase.getReference("Alumnos/" + alumnoSelected.getUuid() + "/");
                                Query queryReporte = databaseReference.child("reportes");
                                queryReporte.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        listaReprtes.clear();
                                        for(DataSnapshot objSnapshot : snapshot.getChildren()){
                                            Reporte reporte = objSnapshot.getValue(Reporte.class);
                                            listaReprtes.add(reporte);
                                            if(getContext() != null){
                                                arrayAdapter = new ArrayAdapter<Reporte>(getContext(), android.R.layout.simple_list_item_1, listaReprtes);
                                                lvReportes.setAdapter(arrayAdapter);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
                                //limpiar();
                                if (getContext() != null) {
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
         }
    }
}