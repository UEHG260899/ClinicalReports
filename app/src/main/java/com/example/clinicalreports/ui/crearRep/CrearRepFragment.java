package com.example.clinicalreports.ui.crearRep;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.clinicalreports.DrawerAlumno;
import com.example.clinicalreports.R;
import com.example.clinicalreports.mdbf.Reporte;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CrearRepFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private Button btnCrear, btnLimpiar;
    private ImageButton btnCalendario;
    private ImageView ivFoto;
    private EditText etNomDu, etTelefono, etNomAn, etFecha, etDiag, etTrat;
    private Spinner spRaza;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user;
    private Uri photoUri;
    public static int REQUEST_PERMISSION = 1;
    private static int dia, mes, anio;
    private DatePickerDialog dpd;
    private Calendar c;
    private String img = "", longitud = "", latitud = "", r = "";
    private LocationManager locationManager;

    private CrearRepViewModel mViewModel;

    public static CrearRepFragment newInstance() {
        return new CrearRepFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_crear_rep, container, false);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        iniciarComp(root);
        iniciarFirebase();

        return root;
    }


    private void iniciarComp(View root) {
        botonesComp(root);
        editComp(root);
        spinnComp(root);
    }


    private void botonesComp(View root) {
        btnCrear = root.findViewById(R.id.btnCrearRep);
        btnLimpiar = root.findViewById(R.id.btnLimparRep);
        btnCalendario = root.findViewById(R.id.btnCalendarioRep);
        ivFoto = root.findViewById(R.id.ivFotoRep);

        btnCrear.setOnClickListener(this);
        btnCalendario.setOnClickListener(this);
        ivFoto.setOnClickListener(this);
    }

    private void editComp(View root) {
        etNomDu = root.findViewById(R.id.etNombreDuenioRep);
        etTelefono = root.findViewById(R.id.etTelefonoRep);
        etNomAn = root.findViewById(R.id.etNomAnRep);
        etFecha = root.findViewById(R.id.etFechaRep);
        etDiag = root.findViewById(R.id.etDiagnosticoRep);
        etTrat = root.findViewById(R.id.etTratRep);
    }

    private void spinnComp(View root) {

        ArrayAdapter<CharSequence> razaAdapter = ArrayAdapter.createFromResource(getContext(), R.array.especie, android.R.layout.simple_spinner_item);

        spRaza = root.findViewById(R.id.spRazaRep);
        spRaza.setAdapter(razaAdapter);

        spRaza.setOnItemSelectedListener(this);
    }


    private void iniciarFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Alumnos");
        storageReference = FirebaseStorage.getInstance().getReference("imagenes");
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CrearRepViewModel.class);
        // TODO: Use the ViewModel
    }

    private void limpiar() {
        etNomDu.setText("");
        etTelefono.setText("");
        etNomAn.setText("");
        etFecha.setText("");
        etDiag.setText("");
        etTrat.setText("");

        img = r = latitud = longitud = "";

        spRaza.setSelection(0);

        ivFoto.setImageResource(R.drawable.ic_menu_camera);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivFotoRep:
                Intent tomaFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (tomaFoto.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(tomaFoto, REQUEST_PERMISSION);
                }
                break;
            case R.id.btnLimparRep:
                limpiar();
                break;
            case R.id.btnCalendarioRep:
                c = Calendar.getInstance();

                anio = c.get(Calendar.YEAR);
                mes = c.get(Calendar.MONTH);
                dia = c.get(Calendar.DAY_OF_MONTH);

                dpd = new DatePickerDialog(getContext(), this, anio, mes, dia);
                dpd.show();
                break;
            case R.id.btnCrearRep:
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    activaGPS();
                } else {
                    if (validaciones()) {
                        getUbicacion();
                        if (longitud.isEmpty() || latitud.isEmpty()) {
                            Toast.makeText(getContext(), "No es posible determinar una ubicación en estos momentos, le sugerimos actualizar y volver a intentarlo", Toast.LENGTH_LONG).show();
                        } else {
                            Reporte rep = new Reporte();
                            rep.setUuid(UUID.randomUUID().toString());
                            rep.setNomDu(etNomDu.getText().toString());
                            rep.setTelefono(etTelefono.getText().toString());
                            rep.setNomAn(etNomAn.getText().toString());
                            rep.setEspecie(r);
                            rep.setFecha(etFecha.getText().toString());
                            rep.setDiagnostico(etDiag.getText().toString());
                            rep.setTratamiento(etTrat.getText().toString());
                            rep.setLatitud(latitud);
                            rep.setLongitud(longitud);
                            rep.setImagen(img);

                            ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {
                                databaseReference.child(user.getUid()).child("reportes").child(rep.getUuid()).setValue(rep).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Reporte guardado de manera exitosa", Toast.LENGTH_SHORT).show();
                                        limpiar();
                                    }
                                });
                            } else {
                                databaseReference.child(user.getUid()).child("reportes").child(rep.getUuid()).setValue(rep);
                                Toast.makeText(getContext(), "Reporte guardado de manera exitosa" + "\nEstado: Sin conexión", Toast.LENGTH_SHORT).show();
                                limpiar();

                            }
                        }
                    }
                }

                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.spRazaRep) {
            if (position != 0) {
                r = parent.getItemAtPosition(position).toString();
            } else {
                r = "";
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PERMISSION && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), imageBitmap, "IMG_" + System.currentTimeMillis(), null);
            photoUri = Uri.parse(path);

            cargaArchivo();
            ivFoto.setImageURI(photoUri);
        }
    }

    private void cargaArchivo() {
        img = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "." + extension(photoUri);
        ///////
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            StorageReference ref = storageReference.child(img);
            ref.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Archivo cargado con exito", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Algo salio mal al cargar el archivo", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            StorageReference ref = storageReference.child(img);
            ref.putFile(photoUri);
            Toast.makeText(getContext(), "Archivo cargado con exito", Toast.LENGTH_SHORT).show();
        }
//////

    }

    private String extension(Uri photoUri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(photoUri));
    }

    private void activaGPS() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
        dialogo.setMessage("Es necesario activar el GPS, ¿Desea hacerlo ahora?");
        dialogo.setCancelable(false);
        dialogo.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        dialogo.setNegativeButton("No", null);
        dialogo.show();
    }

    private void getUbicacion() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitud = String.valueOf(location.getLatitude());
                longitud = String.valueOf(location.getLongitude());
            }
        }
    }

    private boolean validaciones() {
        String nombreDu = etNomDu.getText().toString();
        String telefono = etTelefono.getText().toString();
        String nombreAn = etNomAn.getText().toString();
        String fecha = etFecha.getText().toString();
        String diag = etDiag.getText().toString();
        String trat = etTrat.getText().toString();

        if (nombreDu.isEmpty()) {
            etNomDu.requestFocus();
            etNomDu.setError("No debe de haber campos vacios");
            return false;
        } else if (telefono.isEmpty()) {
            etTelefono.requestFocus();
            etTelefono.setError("No debe de haber campos vacios");
            return false;
        } else if (nombreAn.isEmpty()) {
            etNomAn.requestFocus();
            etNomAn.setError("No debe de haber campos vacios");
            return false;
        } else if (r.isEmpty()) {
            Toast.makeText(getContext(), "Debe seleccionar una especie", Toast.LENGTH_SHORT).show();
            return false;
        } else if (fecha.isEmpty()) {
            etFecha.requestFocus();
            etFecha.setError("No debe de haber campos vacios");
            return false;
        } else if (diag.isEmpty()) {
            etDiag.requestFocus();
            etDiag.setError("No debe de haber campos vacios");
            return false;
        } else if (trat.isEmpty()) {
            etTrat.requestFocus();
            etTrat.setError("No debe de haber campos vacios");
            return false;
        } else if (img.isEmpty()) {
            Toast.makeText(getContext(), "Debe de cargar una foto", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        etFecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
    }
}