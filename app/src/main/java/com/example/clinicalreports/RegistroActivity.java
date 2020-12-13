package com.example.clinicalreports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.clinicalreports.mdbf.Alumno;
import com.example.clinicalreports.mdbf.Profesor;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistroActivity extends AppCompatActivity {


    Button btnRegistrar;
    EditText etNombre, etEmail, etPass, etPassConf, etClave;
    TextInputLayout textInputLayout;
    RadioButton radioAlu, radioProf;
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
                if (radioAlu.isChecked()) {
                    String nombre = etNombre.getText().toString();
                    String correo = etEmail.getText().toString();
                    String pass = etPass.getText().toString();
                    String passConf = etPassConf.getText().toString();
                    String clave = etClave.getText().toString();
                    if (validacion(nombre, pass, passConf, correo, clave)) {
                        if (!pass.equals(passConf)) {
                            Toast.makeText(RegistroActivity.this, "Error, las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        } else {
                            firebaseAuth.createUserWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        Alumno alumno = new Alumno();
                                        alumno.setNombre(nombre);
                                        alumno.setUuid(user.getUid());
                                        alumno.setCorreo(correo);
                                        alumno.setPassword(pass);
                                        alumno.setReportes(null);
                                        alumno.setNoCtrl(clave);
                                        databaseReference.child("Alumnos").child(alumno.getUuid()).setValue(alumno).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                startActivity(new Intent(RegistroActivity.this, DrawerAlumno.class));
                                            }
                                        });
                                    } else {
                                        //Manejo de excepciones con la autenticación
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException ex) {
                                            //La contraseña tiene menos de 6 caracteres
                                            etPassConf.setError("La contraseña es demasiado corta, debe contener al menos 6 caracteres");
                                            etPass.requestFocus();
                                        } catch (FirebaseAuthUserCollisionException ex) {
                                            //El usuario ya existe
                                            etEmail.setError("Este usuario ya esta registrado");
                                            etEmail.requestFocus();
                                        } catch (Exception ex) {
                                            //Excepciones varias
                                            Toast.makeText(RegistroActivity.this, "Ha ocurrido un error desconocido", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                        }
                    }
                } else if (radioProf.isChecked()) {
                    String nombre = etNombre.getText().toString();
                    String correo = etEmail.getText().toString();
                    String pass = etPass.getText().toString();
                    String passConf = etPassConf.getText().toString();
                    String clave = etClave.getText().toString();
                    if (validacion(nombre, pass, passConf, correo, clave)) {
                        if (!pass.equals(passConf)) {
                            Toast.makeText(RegistroActivity.this, "Error, las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        } else {
                            firebaseAuth.createUserWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        Profesor profesor = new Profesor();
                                        profesor.setNombre(nombre);
                                        profesor.setUuid(user.getUid());
                                        profesor.setClave(clave);
                                        profesor.setCorreo(correo);
                                        profesor.setPassword(pass);
                                        databaseReference.child("Profesores").child(profesor.getUuid()).setValue(profesor).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                startActivity(new Intent(RegistroActivity.this, DrawerMaestro.class));
                                            }
                                        });
                                    }else{
                                        try{
                                            throw task.getException();
                                        }catch(FirebaseAuthWeakPasswordException ex){
                                            etPass.setError("La contraseña es demasiado corta, debe contener al menos 6 caracteres");
                                            etPass.requestFocus();
                                        }catch(FirebaseAuthUserCollisionException ex){
                                            etEmail.setError("Error, el usuario ya existe");
                                            etEmail.requestFocus();
                                        }catch(Exception ex){
                                            Toast.makeText(RegistroActivity.this, "Ha ocurrido un error desconocido", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private boolean validacion(String nombre, String pass, String confPass, String correo, String clave) {

        if (nombre.isEmpty()) {
            etNombre.requestFocus();
            etNombre.setError("No puede haber campor vacios");
            return false;
        } else if (pass.isEmpty()) {
            etPass.requestFocus();
            etPass.setError("No puede haber campor vacios");
            return false;
        } else if (confPass.isEmpty()) {
            etPassConf.requestFocus();
            etPassConf.setError("No puede haber campor vacios");
            return false;
        } else if (correo.isEmpty()) {
            etEmail.requestFocus();
            etEmail.setError("No puede haber campor vacios");
            return false;
        } else if (clave.isEmpty()) {
            etClave.requestFocus();
            etClave.setError("No puede haber campor vacios");
            return false;
        } else {
            return true;
        }

    }

    private void inicializarComponentes() {

        btnRegistrar = findViewById(R.id.btnRegistrarse);
        btnRegistrar.setVisibility(View.GONE);
        etNombre = findViewById(R.id.etNomR);
        etNombre.setVisibility(View.GONE);
        etEmail = findViewById(R.id.etEmailR);
        etEmail.setVisibility(View.GONE);
        etPass = findViewById(R.id.etContR);
        etPass.setVisibility(View.GONE);
        etPassConf = findViewById(R.id.etContConfR);
        etPassConf.setVisibility(View.GONE);
        etClave = findViewById(R.id.etClaveR);
        etClave.setVisibility(View.GONE);
        textInputLayout = findViewById(R.id.inputLayoutClave);
        radioAlu = findViewById(R.id.radioAlu);
        radioProf = findViewById(R.id.radioProf);
    }

    public void onRadioButtonClicked(View view) {

        switch (view.getId()) {

            case R.id.radioAlu:
                if (radioAlu.isChecked()) {
                    btnRegistrar.setVisibility(View.VISIBLE);
                    etNombre.setVisibility(View.VISIBLE);
                    etEmail.setVisibility(View.VISIBLE);
                    etPass.setVisibility(View.VISIBLE);
                    etPassConf.setVisibility(View.VISIBLE);
                    etClave.setVisibility(View.VISIBLE);
                    textInputLayout.setHint("No. de Control");
                }
                break;
            case R.id.radioProf:
                if (radioProf.isChecked()) {
                    btnRegistrar.setVisibility(View.VISIBLE);
                    etNombre.setVisibility(View.VISIBLE);
                    etEmail.setVisibility(View.VISIBLE);
                    etPass.setVisibility(View.VISIBLE);
                    etPassConf.setVisibility(View.VISIBLE);
                    etClave.setVisibility(View.VISIBLE);
                    textInputLayout.setHint("Clave de Docente");
                }
                break;
        }
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }
}