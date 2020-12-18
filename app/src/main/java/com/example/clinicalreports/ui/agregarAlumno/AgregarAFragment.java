package com.example.clinicalreports.ui.agregarAlumno;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class AgregarAFragment extends Fragment implements View.OnClickListener {

    private AgregarAViewModel agregarAViewModel;
    private Button btnBuscar, btnLimpiar, btnAgregar;
    private TextView tvNombre, tvNoctrl, tvCorreo;
    private EditText etNoctrl;
    private ListView lvAlumnos;
    private Alumno alumnoSelected;
    private List<Alumno> listaAlumnos = new ArrayList<>();
    ArrayAdapter<Alumno> arrayAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        agregarAViewModel =
                new ViewModelProvider(this).get(AgregarAViewModel.class);
        View root = inflater.inflate(R.layout.fragment_agregar_alumno, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        agregarAViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        iniciarComp(root);
        iniciarFirebase();
        listarDatos();

        return root;
    }


    private void iniciarComp(View root){
        botonesComp(root);
        textComp(root);
        editComp(root);
    }

    private void botonesComp(View root){
        btnLimpiar = root.findViewById(R.id.btnLimpiarAg);
        btnLimpiar.setEnabled(false);
        btnAgregar = root.findViewById(R.id.btnAgregar);
        btnAgregar.setEnabled(false);
        btnBuscar = root.findViewById(R.id.btnBuscaAg);

        btnLimpiar.setOnClickListener(this);
        btnBuscar.setOnClickListener(this);
        btnAgregar.setOnClickListener(this);
    }

    private void textComp(View root){
        tvNoctrl = root.findViewById(R.id.tvNoctrlAg);
        tvNombre = root.findViewById(R.id.tvnomAluAg);
        tvCorreo = root.findViewById(R.id.tvCorreoAg);
    }

    private void editComp(View root){
        etNoctrl = root.findViewById(R.id.etNoctrlAg);
        lvAlumnos = root.findViewById(R.id.lvAlumnos);
    }

    private void listarDatos(){
        databaseReference.orderByChild("profesor").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaAlumnos.clear();
                for(DataSnapshot objSnapshot : snapshot.getChildren()){
                    Alumno alu = objSnapshot.getValue(Alumno.class);
                    listaAlumnos.add(alu);
                    if(getContext() != null){
                        arrayAdapter = new ArrayAdapter<Alumno>(getContext(), android.R.layout.simple_list_item_1, listaAlumnos);
                        lvAlumnos.setAdapter(arrayAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private  void limpiar(){
        tvNombre.setText("Nombre del alumno: ");
        tvNoctrl.setText("No. de Control: ");
        tvCorreo.setText("Correo electrónico: ");
        btnAgregar.setEnabled(false);
        btnLimpiar.setEnabled(false);
        etNoctrl.setText("");
    }

    private void iniciarFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Alumnos");
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnLimpiarAg:
                limpiar();
                break;
            case R.id.btnBuscaAg:
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
                                btnAgregar.setEnabled(true);
                                btnLimpiar.setEnabled(true);
                                for(DataSnapshot objSnapshot : snapshot.getChildren()){
                                    alumnoSelected = objSnapshot.getValue(Alumno.class);
                                    tvNombre.setText("Nombre del alumno: " + alumnoSelected.getNombre());
                                    tvNoctrl.setText("No. de Control: " + alumnoSelected.getNoCtrl());
                                    tvCorreo.setText("Correo electrónico: " + alumnoSelected.getCorreo());
                                }
                            }else{
                                limpiar();
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
            case R.id.btnAgregar:
                alumnoSelected.setProfesor(user.getUid());
                databaseReference.child(alumnoSelected.getUuid()).setValue(alumnoSelected).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Alumno agregado al grupo", Toast.LENGTH_SHORT).show();
                        limpiar();
                    }
                });
                break;
        }
    }
}