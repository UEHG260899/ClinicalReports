package com.example.clinicalreports;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinicalreports.mdbf.Profesor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    Button btnIn;
    TextView tvSignin;
    EditText etCorreo, etPass;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference profesorRef, alumnoRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciarFirebase();
        firebaseAuth = FirebaseAuth.getInstance();
        btnIn = findViewById(R.id.btnIngresar);
        tvSignin = findViewById(R.id.tvSignIn);
        etCorreo = findViewById(R.id.etClaveR);
        etPass = findViewById(R.id.etCont);

        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etCorreo.getText().toString();
                String pass = etPass.getText().toString();

                if(email.isEmpty()){
                    etCorreo.requestFocus();
                    etCorreo.setError("Ingrese un correo");
                }else if(pass.isEmpty()){
                    etPass.requestFocus();
                    etCorreo.setError("Ingrese una contraseña");
                }else if (email.isEmpty() && pass.isEmpty()){
                    etCorreo.requestFocus();
                    etPass.requestFocus();
                    Toast.makeText(MainActivity.this, "No puede haber campos vacios", Toast.LENGTH_SHORT).show();
                }else if(!(email.isEmpty() && pass.isEmpty())){
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                //Busca en el documento Profesor un elemento que contenga el uuid del usuario
                                Query query = profesorRef.orderByChild("uuid").equalTo(user.getUid());
                                query.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        startActivity(new Intent(MainActivity.this, DrawerMaestro.class));
                                        finish();
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                //Busca en el documento Alumnos un elemento que contenga el uuid del usuario
                                Query queryAlu = alumnoRef.orderByChild("uuid").equalTo(user.getUid());
                                queryAlu.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        startActivity(new Intent(MainActivity.this, DrawerAlumno.class));
                                        finish();
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }else{
                                try {
                                    throw task.getException();

                                }catch(FirebaseAuthInvalidUserException ex){
                                    etCorreo.requestFocus();
                                    etCorreo.setError("El usuario no esta registrado");
                                }catch (FirebaseAuthInvalidCredentialsException ex){
                                    //Cuando la contraseña no coincide o el usuario no tiene contraseña
                                    etPass.requestFocus();
                                    etPass.setError("La contraseña no coincide con los registros de este usuario");
                                }catch (Exception ex){
                                    Toast.makeText(MainActivity.this, "Ocurrio un error desconocido", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
            }
        });

        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void iniciarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        profesorRef = firebaseDatabase.getReference("Profesores");
        alumnoRef = firebaseDatabase.getReference("Alumnos");
    }
}