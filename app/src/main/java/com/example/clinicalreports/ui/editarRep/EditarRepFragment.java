package com.example.clinicalreports.ui.editarRep;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
import com.example.clinicalreports.R;
import com.example.clinicalreports.mdbf.Reporte;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditarRepFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private EditarRepViewModel mViewModel;
    private EditText etNomAn, etNomDu, etTel, etFecha, etDiag, etTrat;
    private Spinner spRaza;
    private ImageView ivFoto;
    private Button btnEditar, btnBuscar, btnLimpiar;
    private ImageButton btnCalendario;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user;
    private Uri photoUri;
    public static int REQUEST_PERMISSION = 1;
    private static int dia, mes, anio;
    private DatePickerDialog dpd;
    private Calendar c;
    private String img = "", r = "", imgF = "";
    private Reporte reporteSelected;
    private ArrayAdapter<CharSequence> arrayAdapter;
    private boolean bandera;

    public static EditarRepFragment newInstance() {
        return new EditarRepFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editar_rep, container, false);
        iniciarFirebase();
        iniciarComponentes(root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditarRepViewModel.class);
        // TODO: Use the ViewModel
    }

    private void iniciarComponentes(View root) {
        iniciarBotones(root);
        iniciarEdit(root);
        iniciarSpinner(root);
    }

    private void iniciarFirebase() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Alumnos/" + user.getUid() + "/");
        storageReference = FirebaseStorage.getInstance().getReference("imagenes");
    }

    private void iniciarBotones(View root) {
        btnBuscar = root.findViewById(R.id.btnBuscaEdit);
        btnLimpiar = root.findViewById(R.id.btnLimpiarEdit);
        btnLimpiar.setEnabled(false);
        btnEditar = root.findViewById(R.id.btnEditar);
        btnEditar.setEnabled(false);
        btnCalendario = root.findViewById(R.id.btnCalendarioEdit);
        ivFoto = root.findViewById(R.id.ivFotoEdit);

        btnBuscar.setOnClickListener(this);
        btnLimpiar.setOnClickListener(this);
        btnEditar.setOnClickListener(this);
        ivFoto.setOnClickListener(this);
    }

    private void iniciarEdit(View root) {
        etDiag = root.findViewById(R.id.etDiagEdit);
        etTrat = root.findViewById(R.id.etTratEdit);
        etNomAn = root.findViewById(R.id.etNombreAnEdit);
        etNomDu = root.findViewById(R.id.etNombreDuenioEdit);
        etFecha = root.findViewById(R.id.etFechaEdit);
        etTel = root.findViewById(R.id.etTelEdit);
    }

    private void iniciarSpinner(View root) {
        arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.especie, android.R.layout.simple_spinner_item);

        spRaza = root.findViewById(R.id.spRazaEdit);
        spRaza.setAdapter(arrayAdapter);

        spRaza.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLimpiarEdit:
                limpiar();
                break;
            case R.id.btnBuscaEdit:
                if (etNomAn.getText().toString().isEmpty()) {
                    etNomAn.requestFocus();
                    etNomAn.setError("Por favor ingrese un criterio de busqueda");
                } else {


                    bandera = true;
                    Query queryReporte = databaseReference.child("reportes").orderByChild("nomAn").equalTo(etNomAn.getText().toString());
                    queryReporte.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && bandera) {
                                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                                    reporteSelected = objSnapshot.getValue(Reporte.class);
                                    etNomDu.setText(reporteSelected.getNomDu());
                                    etTel.setText(reporteSelected.getTelefono());
                                    r = reporteSelected.getEspecie();
                                    etDiag.setText(reporteSelected.getDiagnostico());
                                    etTrat.setText(reporteSelected.getTratamiento());
                                    etFecha.setText(reporteSelected.getFecha());
                                    imgF = reporteSelected.getImagen();
                                }
                                ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                if (networkInfo != null && networkInfo.isConnected()) {
                                    cargaImagen(ivFoto);
                                } else {
                                    Toast.makeText(getContext(), "En estos momento no se puede observar la foto", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getContext(), "ya que usted se encuentra sin conexión, esto no afecta la modificación", Toast.LENGTH_SHORT).show();
                                }
                                btnLimpiar.setEnabled(true);
                                btnEditar.setEnabled(true);
                                btnBuscar.setEnabled(false);
                                etNomAn.setEnabled(false);
                                spRaza.setSelection(arrayAdapter.getPosition(r));


                            } else {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "No se han encontrado resultados", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                break;
            case R.id.btnCalendarioEdit:
                c = Calendar.getInstance();
                anio = c.get(Calendar.YEAR);
                mes = c.get(Calendar.MONTH);
                dia = c.get(Calendar.DAY_OF_MONTH);
                dpd = new DatePickerDialog(getContext(), this, anio, mes, dia);
                dpd.show();
                break;
            case R.id.ivFotoEdit:
                Intent tomaFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (tomaFoto.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(tomaFoto, REQUEST_PERMISSION);
                }
                break;
            case R.id.btnEditar:
                if (validaciones()) {
                    if (img.isEmpty()) {
                        Reporte rep = new Reporte();
                        rep.setUuid(reporteSelected.getUuid());
                        String id = reporteSelected.getUuid();
                        rep.setNomDu(etNomDu.getText().toString());
                        rep.setNomAn(etNomAn.getText().toString());
                        rep.setTelefono(etTel.getText().toString());
                        rep.setEspecie(r);
                        rep.setFecha(etFecha.getText().toString());
                        rep.setDiagnostico(etDiag.getText().toString());
                        rep.setTratamiento(etTrat.getText().toString());
                        rep.setImagen(imgF);
                        rep.setLongitud(reporteSelected.getLongitud());
                        rep.setLatitud(reporteSelected.getLatitud());
                        limpiar();

                        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isConnected()) {
                            databaseReference.child("reportes").child(id).setValue(rep).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Actualización exitosa", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Algo falló", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            databaseReference.child("reportes").child(id).setValue(rep);
                            Toast.makeText(getContext(), "Actualización exitosa" + "\nEstado: sin conexión", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Reporte rep = new Reporte();
                        rep.setUuid(reporteSelected.getUuid());
                        String id = reporteSelected.getUuid();
                        rep.setNomDu(etNomDu.getText().toString());
                        rep.setNomAn(etNomAn.getText().toString());
                        rep.setTelefono(etTel.getText().toString());
                        rep.setEspecie(r);
                        rep.setFecha(etFecha.getText().toString());
                        rep.setDiagnostico(etDiag.getText().toString());
                        rep.setTratamiento(etTrat.getText().toString());
                        rep.setImagen(img);
                        rep.setLongitud(reporteSelected.getLongitud());
                        rep.setLatitud(reporteSelected.getLatitud());

                        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isConnected()) {
                            storageReference.child(imgF).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    limpiar();
                                    databaseReference.child("reportes").child(id).setValue(rep).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(getContext(), "Actualización exitosa", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            storageReference.child(imgF).delete();
                            limpiar();
                            databaseReference.child("reportes").child(id).setValue(rep);
                            Toast.makeText(getContext(), "Actualización exitosa" + "\nEstado: sin conexión", Toast.LENGTH_SHORT).show();

                        }

                    }

                }

                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spRazaEdit) {
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        etFecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
    }

    private void limpiar() {
        etDiag.setText("");
        etTrat.setText("");
        etNomAn.setText("");
        etNomDu.setText("");
        etFecha.setText("");
        etTel.setText("");

        ivFoto.setImageResource(R.drawable.ic_menu_camera);
        img = r = imgF = "";
        spRaza.setSelection(0);

        btnEditar.setEnabled(false);
        btnLimpiar.setEnabled(false);
        btnBuscar.setEnabled(true);
        etNomAn.setEnabled(true);


        reporteSelected = null;
        bandera = false;
    }

    private void cargaImagen(ImageView ivFoto) {

        if (getActivity() != null) {
            storageReference.child(reporteSelected.getImagen()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getContext()).load(uri).into(ivFoto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getCause() + "", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        img = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date()) + "." + extension(photoUri);

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            StorageReference ref = storageReference.child(img);

            ref.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Archivo cargado de manera exitosa", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getCause() + "", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            StorageReference ref = storageReference.child(img);

            ref.putFile(photoUri);
            Toast.makeText(getContext(), "Archivo cargado de manera exitosa" + "\nEstado: sin conexión", Toast.LENGTH_SHORT).show();

        }

    }

    private String extension(Uri photoUri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(photoUri));
    }

    private boolean validaciones() {
        String nombreDu = etNomDu.getText().toString();
        String telefono = etTel.getText().toString();
        String nombreAn = etNomAn.getText().toString();
        String fecha = etFecha.getText().toString();
        String diag = etDiag.getText().toString();
        String trat = etTrat.getText().toString();

        if (nombreDu.isEmpty()) {
            etNomDu.requestFocus();
            etNomDu.setError("No debe de haber campos vacios");
            return false;
        } else if (telefono.isEmpty()) {
            etTel.requestFocus();
            etTel.setError("No debe de haber campos vacios");
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
        } else {
            return true;
        }
    }
}