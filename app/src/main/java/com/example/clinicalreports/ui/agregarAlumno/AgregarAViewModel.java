package com.example.clinicalreports.ui.agregarAlumno;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AgregarAViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AgregarAViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is agregarAlumno fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}