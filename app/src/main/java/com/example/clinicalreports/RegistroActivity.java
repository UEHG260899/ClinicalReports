package com.example.clinicalreports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.clinicalreports.mdbf.Profesor;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistroActivity extends AppCompatActivity {


    Button btnRegistrar;
    EditText etNombre, etEmail, etPass, etPassConf, etClave;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        inicializarComponentes();
        iniciarFirebase();
        firebaseAuth = FirebaseAuth.getInstance();
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = etEmail.getText().toString();
                String pass = etPass.getText().toString();
                String passConf = etPassConf.getText().toString();
                String clave = etClave.getText().toString();

                if(correo.isEmpty() || pass.isEmpty() || passConf.isEmpty() || clave.isEmpty()){
                    Toast.makeText(RegistroActivity.this, "No puede haber campos vacios", Toast.LENGTH_LONG).show();
                }else if(!pass.equals(passConf)){
                    etPassConf.requestFocus();
                    etPass.requestFocus();
                    etPassConf.setError("Las contraseñas no coinciden");
                }else{
                    firebaseAuth.createUserWithEmailAndPassword(correo, pass).addOnCompleteListener(RegistroActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Profesor profesor = new Profesor();
                                profesor.setNombre(etNombre.getText().toString());
                                profesor.setCorreo(etEmail.getText().toString());
                                profesor.setPassword(etPass.getText().toString());
                                profesor.setClave(etClave.getText().toString());
                                databaseReference.child("Profesor").child(profesor.getClave()).setValue(profesor);
                                startActivity(new Intent(RegistroActivity.this, DrawerMaestro.class));
                            }else{

                            }
                        }
                    });
                }
            }
        });
    }

    private void inicializarComponentes(){

        btnRegistrar = findViewById(R.id.btnRegistrarse);
        etNombre = findViewById(R.id.etNomR);
        etEmail = findViewById(R.id.etEmailR);
        etPass = findViewById(R.id.etContR);
        etPassConf = findViewById(R.id.etContConfR);
        etClave = findViewById(R.id.etClaveR);

    }

    private void iniciarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }
}