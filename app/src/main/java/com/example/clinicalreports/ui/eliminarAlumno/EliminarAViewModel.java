package com.example.clinicalreports.ui.eliminarAlumno;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EliminarAViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EliminarAViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is eliminarAlumno fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}