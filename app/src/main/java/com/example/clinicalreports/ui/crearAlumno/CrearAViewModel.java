package com.example.clinicalreports.ui.crearAlumno;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CrearAViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CrearAViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is crearAlumno fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}