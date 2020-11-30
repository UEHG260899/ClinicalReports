package com.example.clinicalreports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button btnIn;
    TextView tvSignin;
    EditText etCorreo, etPass;
    FirebaseAuth firebaseAuth;

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        btnIn = findViewById(R.id.btnIngresar);
        tvSignin = findViewById(R.id.tvSignIn);
        etCorreo = findViewById(R.id.etClaveR);
        etPass = findViewById(R.id.etCont);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    //esto se debe de cambiar para que verifique si es un alumno o maestro
                    startActivity(new Intent(MainActivity.this, DrawerMaestro.class));
                }
            }
        };

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
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Ocurrio un error", Toast.LENGTH_LONG);
                            }else{
                                Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                                //esto se debe de cambiar para que verifique si es un alumno o maestro
                                startActivity(new Intent(MainActivity.this, DrawerMaestro.class));
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
}